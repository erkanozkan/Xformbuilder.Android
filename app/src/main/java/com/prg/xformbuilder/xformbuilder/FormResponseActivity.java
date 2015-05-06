package com.prg.xformbuilder.xformbuilder;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.MimeTypeMap;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.Base64;
import com.loopj.android.http.FileAsyncHttpResponseHandler;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.util.Date;


public class FormResponseActivity extends Activity {
    DatabaseHandler dbHandler;
    String formId = "", draftId = "", formTitle = "", fileStringByte = "", fileName = "", fileSize = "";
    int userId = 0, parentId = 0;
    StringBuilder html = new StringBuilder();
    private WebView webView;
    DraftForm draft;
    Form form;
    String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
    ImageButton btnBackResponse;
    LinearLayout BackLinearLayout;
    private static final int FILECHOOSER_RESULTCODE = 2888;
    private ValueCallback<Uri> mUploadMessage;
    private Uri mCapturedImageURI = null;
    final Bundle bundleFormResponse = new Bundle();//Formlar aras� veri transferi i�in kullan�yoruz


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//----------------------------------------Session Kontrol
        SharedPreferences preferences;     //preferences için bir nesne tanımlıyorum.
        //SharedPreferences.Editor editor;        //preferences içerisine bilgi girmek için tanımlama
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        // editor = preferences.edit();

        String sessionUserName = preferences.getString("UserName", "NULL");
        String sessionPassword = preferences.getString("Password", "NULL");

        if (sessionUserName.contains("NULL") && sessionPassword.contains("NULL")) {
            Intent i = new Intent(FormResponseActivity.this, MainActivity.class);
            startActivity(i);
        }
//----------------------------------------Session Kontrol
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_form_response);

        overridePendingTransition(R.anim.right_animation, R.anim.out_left_animation);

        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.response_title);
        dbHandler = new DatabaseHandler(getApplicationContext());
        Bundle bundle = getIntent().getExtras();
        formId = bundle.getString("FormId");
        userId = bundle.getInt("UserId");
        draftId = bundle.getString("DraftId");
        formTitle = bundle.getString("FormTitle");
        parentId = bundle.getInt("ParentId");
        TextView frmname = (TextView) findViewById(R.id.textView_FormName);
        frmname.setText(formTitle);
        webView = (WebView) findViewById(R.id.webview);

        if (savedInstanceState != null) {
            webView.restoreState(savedInstanceState);
        } else {

            if (draftId != null) {
                draft = dbHandler.GetDraftByDraftId(draftId);
                html.append("<html>" + draft.getDraftHtml() + "</html>");
            } else {
                form = dbHandler.GetFormByFormId(formId);
                html.append(form.getMobileHtml());
            }

            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setLoadWithOverviewMode(true);
            //webView.getSettings().setUseWideViewPort(true);
            //Other webview settings
            webView.setScrollbarFadingEnabled(false);
            webView.getSettings().setPluginState(WebSettings.PluginState.ON);
            webView.getSettings().setAllowFileAccess(true);
            webView.loadDataWithBaseURL("file:///android_asset/", html.toString(), "text/html", "utf-8", null);
            webView.addJavascriptInterface(new WebViewJavaScriptInterface(this), "app");
        }


        //   dbHandler.DeleteDraftFormTable();


        startWebView();


        webView.setWebViewClient(new WebViewClient() {

             public boolean shouldOverrideUrlLoading(WebView view, String url) {


                if(NetWorkControl()){

                    if (url.startsWith("https://") || url.startsWith("http://")) {
                        view.getContext().startActivity(
                                new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                        return true;
                    } else {
                        view.getContext().startActivity(
                                new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.xformbuilder.com/")));
                        return true;
                    }
                } else{
                    Toast.makeText(getApplicationContext(), R.string.CheckYourNetwork, Toast.LENGTH_SHORT).show();
                }
                 return true;
            }
        });


        BackLinearLayout = (LinearLayout) findViewById(R.id.LinearLayoutBack);

        BackLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BackPressed();

            }
        });

        btnBackResponse = (ImageButton) findViewById(R.id.imageButton_Back);

        btnBackResponse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BackPressed();
            }
        });

    }
    public void YesClikced() {
        bundleFormResponse.putInt("UserId", userId);
        bundleFormResponse.putInt("ParentId", parentId);
        bundleFormResponse.putString("FormTitle", formTitle);
        bundleFormResponse.putString("DraftId", draftId);
        bundleFormResponse.putString("FormId", formId);
if(!formId.equals("")){
    int count = dbHandler.getFormCount(formId);
    if (count >= 1) {
        Intent i = new Intent(FormResponseActivity.this, DraftFormActivity.class);
        i.putExtras(bundleFormResponse);
        startActivity(i);
        overridePendingTransition(R.anim.right_start_animation, R.anim.left_start_animation);

    } else {
        Intent i = new Intent(FormResponseActivity.this, FormActivity.class);
        i.putExtras(bundleFormResponse);
        startActivity(i);
        overridePendingTransition(R.anim.right_start_animation, R.anim.left_start_animation);

    }

}
        else{
    if(userId != 0 || parentId != 0)
    {
        Intent i = new Intent(FormResponseActivity.this,FormActivity.class);
        i.putExtras(bundleFormResponse);
        startActivity(i);
    }
    else{
        Intent i = new Intent(FormResponseActivity.this,MainActivity.class);
        startActivity(i);
    }


}
    }
    //----------------------------------------------Internet Connection Control-------------------------------------------------------------//
    private boolean NetWorkControl(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            return  true;
        } else
            return  false;
    }
    public void AlertMessagge(String messagge) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(FormResponseActivity.this, AlertDialog.THEME_HOLO_LIGHT);


        alertDialog.setTitle(R.string.SavedProcess);
        alertDialog.setMessage(messagge);
        alertDialog
                .setCancelable(false)
                .setPositiveButton(R.string.OK,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {

                            }
                        });
        AlertDialog alert = alertDialog.create();
        alert.show();

    }

    public class WebViewJavaScriptInterface {

        private Context context;

        /*
         * Need a reference to the context in order to sent a post message
         */
        public WebViewJavaScriptInterface(Context context) {
            this.context = context;
        }

        /*
         * This method can be called from Android. @JavascriptInterface
         * required after SDK version 17.
         */
        @JavascriptInterface
        public void FormSubmit( String html,  String json,  String isUploadable, String keyCode,String forFile, String field1_title,  String field1_value,  String field2_title
                ,  String field2_value,  String field3_title,  String field3_value) {

            try {
                bundleFormResponse.putString("FormId", formId);
                bundleFormResponse.putInt("UserId", userId);
                bundleFormResponse.putInt("ParentId", parentId);
                bundleFormResponse.putString("FormTitle", formTitle);
                if(!formId.equals("")){
                    if (draftId != null) {
                        DraftForm draftForm = new DraftForm(Integer.parseInt(draftId), Integer.parseInt(formId), html, json, currentDateTimeString, userId, field1_title, field1_value, field2_title, field2_value, field3_title, field3_value, isUploadable);
                        dbHandler.UpdateDraft(draftForm);

                    } else {
                        //  String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                        DraftForm form = new DraftForm(0, Integer.parseInt(formId), html, json, currentDateTimeString, userId, field1_title, field1_value, field2_title, field2_value, field3_title, field3_value, isUploadable);
                        dbHandler.CreateDraftForm(form);
                        draftId = dbHandler.GetLastDraftId(formId);
                    }

                    if(isUploadable.equals("0") && forFile.equals("0"))
                        AlertMessagge(getString(R.string.ThereAreFillFields));
                    else if (keyCode.equals("0") && forFile.equals("0")) {
                        AlertMessagge(getString(R.string.FormSaveAsDraft));
                    }
                }
                else {
                    if(userId != 0 || parentId != 0)
                    {
                        Intent i = new Intent(FormResponseActivity.this,FormActivity.class);
                        i.putExtras(bundleFormResponse);
                        startActivity(i);
                    }
                    else{
                        Intent i = new Intent(FormResponseActivity.this,MainActivity.class);
                        startActivity(i);
                    }
                }


            } catch (Exception e) {
                if(userId != 0 || parentId != 0)
                {
                    Toast.makeText(getApplicationContext(),R.string.Error, Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(FormResponseActivity.this,FormActivity.class);
                    i.putExtras(bundleFormResponse);
                    startActivity(i);
                }
                else{
                    Toast.makeText(getApplicationContext(),R.string.Error, Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(FormResponseActivity.this,MainActivity.class);
                    startActivity(i);
                }
            }
        }


        @JavascriptInterface
        public String OpenFile() {
            String IsImage="false";
            if(fileName.toLowerCase().contains(".png") || fileName.toLowerCase().contains(".jpg") || fileName.toLowerCase().contains(".jpeg") || fileName.toLowerCase().contains(".gif")){
                IsImage="true";
            }
            String returnValue = fileStringByte + "$^^$^^$" + fileName + "$^^$^^$" + fileSize+ "$^^$^^$" +IsImage;
            return returnValue;
        }

        @JavascriptInterface
        public void ViewFile(String base64File) throws IOException {

            if (!base64File.equals("")) {

                bundleFormResponse.putInt("UserId", userId);
                bundleFormResponse.putInt("ParentId", parentId);
                bundleFormResponse.putString("FormTitle", formTitle);
                bundleFormResponse.putString("DraftId", draftId);
                bundleFormResponse.putString("FormId", formId);

                byte [] FormImageByte = null;
                try {
                    FormImageByte = base64File.getBytes("UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                bundleFormResponse.putByteArray("base64", FormImageByte);

                Intent i = new Intent(FormResponseActivity.this,ViewFileActivity.class);
                i.putExtras(bundleFormResponse);
                startActivity(i);
                overridePendingTransition(R.anim.left_animation, R.anim.out_right_animation);

            }
        }

    }


    public void openFile(Context context, File url) throws IOException {
        // Create URI

        Uri uri = Uri.fromFile(url);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        // Check what kind of file you are trying to open, by comparing the url with extensions.
        // When the if condition is matched, plugin sets the correct intent (mime) type,
        // so Android knew what application to use to open the file
        if (url.toString().contains(".doc") || url.toString().contains(".docx")) {
            // Word document
            intent.setDataAndType(uri, "application/msword");
        } else if (url.toString().contains(".pdf")) {
            // PDF file
            intent.setDataAndType(uri, "application/pdf");
        } else if (url.toString().contains(".ppt") || url.toString().contains(".pptx")) {
            // Powerpoint file
            intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        } else if (url.toString().contains(".xls") || url.toString().contains(".xlsx")) {
            // Excel file
            intent.setDataAndType(uri, "application/vnd.ms-excel");
        } else if (url.toString().contains(".zip") || url.toString().contains(".rar")) {
            // WAV audio file
            intent.setDataAndType(uri, "application/x-wav");
        } else if (url.toString().contains(".rtf")) {
            // RTF file
            intent.setDataAndType(uri, "application/rtf");
        } else if (url.toString().contains(".wav") || url.toString().contains(".mp3")) {
            // WAV audio file
            intent.setDataAndType(uri, "audio/x-wav");
        } else if (url.toString().contains(".gif")) {
            // GIF file
            intent.setDataAndType(uri, "image/gif");
        } else if (url.toString().contains(".jpg") || url.toString().contains(".jpeg") || url.toString().contains(".png")) {
            // JPG file
            intent.setDataAndType(uri, "image/jpeg");
        } else if (url.toString().contains(".txt")) {
            // Text file
            intent.setDataAndType(uri, "text/plain");
        } else if (url.toString().contains(".3gp") || url.toString().contains(".mpg") || url.toString().contains(".mpeg") || url.toString().contains(".mpe") || url.toString().contains(".mp4") || url.toString().contains(".avi")) {
            // Video files
            intent.setDataAndType(uri, "video/*");
        } else {
            //if you want you can also define the intent type for any other file

            //additionally use else clause below, to manage other unknown extensions
            //in this case, Android will show all applications installed on the device
            //so you can choose which application to use
            intent.setDataAndType(uri, "*/*");
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


    private void startWebView() {
        //Create new webview Client to show progress dialog
        //Called When opening a url or click on link

        webView.setWebViewClient(new WebViewClient() {
            ProgressDialog progressDialog;

            // Called when all page resources loaded
            public void onPageFinished(WebView view, String url) {

                try {
                    // Close progressDialog
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }

        });


        // implement WebChromeClient inner class
        // we will define openFileChooser for select file from camera
        webView.setWebChromeClient(new WebChromeClient() {

            // openFileChooser for Android 3.0+
            public void openFileChooser(ValueCallback uploadMsg) {

                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");
                FormResponseActivity.this.startActivityForResult(
                        Intent.createChooser(i, "File Browser"),
                        FILECHOOSER_RESULTCODE);
            }

            // For Android 3.0+
            public void openFileChooser(ValueCallback uploadMsg, String acceptType) {

                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);

                i.addCategory(Intent.CATEGORY_OPENABLE);

                i.setType("*/*");
                FormResponseActivity.this.startActivityForResult(
                        Intent.createChooser(i, "File Browser"),
                        FILECHOOSER_RESULTCODE);
            }

            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {

                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);

                i.addCategory(Intent.CATEGORY_OPENABLE);

                i.setType("*/*");
                FormResponseActivity.this.startActivityForResult(
                        Intent.createChooser(i, "File Browser"),
                        FILECHOOSER_RESULTCODE);

            }

        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {

        if (requestCode == FILECHOOSER_RESULTCODE) {

            if (null == this.mUploadMessage) {
                return;
            }
            Uri result = null;
            try {
                if (resultCode != RESULT_OK) {

                    result = null;

                } else {
                    result = intent == null ? mCapturedImageURI : intent.getData();
                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "activity :" + e, Toast.LENGTH_LONG).show();
            }
            if (result != null) {

                String path = getPath(getApplicationContext(), result);
                fileStringByte = ConvertFile(path);
                mUploadMessage.onReceiveValue(result);
                mUploadMessage = null;

            }
            else{
                mUploadMessage.onReceiveValue(result);
                mUploadMessage = null;
            }
        }
    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    public String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = Uri.parse(Uri.parse("content://downloads/public_downloads/") + Long.valueOf(id).toString());
                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public String getDataColumn(Context context, Uri uri, String selection,
                                String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }

        } catch (Exception e) {
            throw e;
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }


    public String ConvertFile(String path) {
        String byteString = null;
        byte[] buffer = null;
        FileInputStream stream = null;

        File file = new File(path);

        fileSize = String.valueOf(file.length());
        fileName = file.getName();


        buffer = new byte[(int) file.length()];

        try {
            stream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        assert stream != null;
        try {
            stream.read(buffer);


        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        byteString = Base64.encodeToString(buffer, Base64.DEFAULT);
        return byteString;
    }

    @Override
    public void onBackPressed() {

        BackPressed();
    }


    public void BackPressed() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(FormResponseActivity.this, AlertDialog.THEME_HOLO_LIGHT);
        alertDialog.setMessage(getString(R.string.UnsavedEdits));
        alertDialog
                .setTitle("xFormBuilder")
                .setCancelable(false)
                .setPositiveButton(R.string.Yes,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                bundleFormResponse.putInt("UserId", userId);
                                bundleFormResponse.putInt("ParentId", parentId);
                                bundleFormResponse.putString("FormTitle", formTitle);
                                bundleFormResponse.putString("DraftId", draftId);
                                bundleFormResponse.putString("FormId", formId);

                                if (draftId != null) {
                                    Intent i = new Intent(FormResponseActivity.this, DraftFormActivity.class);
                                    i.putExtras(bundleFormResponse);
                                    startActivity(i);
                                    finish();
                                } else {
                                    Intent i = new Intent(FormResponseActivity.this, FormActivity.class);
                                    i.putExtras(bundleFormResponse);
                                    startActivity(i);
                                    overridePendingTransition(R.anim.right_start_animation, R.anim.left_start_animation);
                                    finish();
                                }
                            }
                        })
                .setNegativeButton(R.string.No,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {

                                dialog.cancel();

                            }
                        });

        AlertDialog alert = alertDialog.create();
        alert.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_form_response, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        //----------------------------------------Session Kontrol
        SharedPreferences preferences;     //preferences için bir nesne tanımlıyorum.
        //SharedPreferences.Editor editor;        //preferences içerisine bilgi girmek için tanımlama
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        // editor = preferences.edit();

        String sessionUserName = preferences.getString("UserName", "NULL");
        String sessionPassword = preferences.getString("Password", "NULL");

        if (sessionUserName.contains("NULL") && sessionPassword.contains("NULL")) {
            Intent i = new Intent(FormResponseActivity.this, MainActivity.class);
            startActivity(i);
        }
        //----------------------------------------Session Kontrol
    }


}
