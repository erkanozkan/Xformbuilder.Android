package com.prg.xformbuilder.xformbuilder;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
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
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;


public class FormResponseActivity extends Activity {
    DatabaseHandler dbHandler;
    String formId="",draftId="",formTitle="";
    int userId=0,parentId=0;
    StringBuilder html = new StringBuilder();
    private WebView webView;
    DraftForm draft;
    Form form;
    final Activity activity = this;
    String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
    public Uri imageUri;
    ProgressDialog progressDialogResponce;
     ImageButton btnBackResponse;
    LinearLayout  BackLinearLayout;


    private static final int FILECHOOSER_RESULTCODE   = 2888;
    private ValueCallback<Uri> mUploadMessage;
    private Uri mCapturedImageURI = null;
    final Bundle bundleFormResponse = new Bundle();//Formlar aras� veri transferi i�in kullan�yoruz
    @Override
    protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);

//----------------------------------------Session Kontrol
        SharedPreferences preferences;     //preferences için bir nesne tanımlıyorum.
        //SharedPreferences.Editor editor;        //preferences içerisine bilgi girmek için tanımlama
        preferences= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        // editor = preferences.edit();

        String sessionUserName=preferences.getString("UserName", "NULL");
        String sessionPassword=preferences.getString("Password", "NULL");

        if (sessionUserName.contains("NULL") && sessionPassword.contains("NULL")){
            Intent i = new Intent(FormResponseActivity.this,MainActivity.class);
            startActivity(i);
        }
//----------------------------------------Session Kontrol
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_form_response);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.response_title);
        dbHandler = new DatabaseHandler(getApplicationContext());
        Bundle bundle=getIntent().getExtras();
        formId=bundle.getString("FormId");
        userId=bundle.getInt("UserId");
        draftId =bundle.getString("DraftId");
        formTitle =bundle.getString("FormTitle");
        parentId=bundle.getInt("ParentId");
        TextView frmname = (TextView)findViewById(R.id.textView_FormName);
        //   dbHandler.DeleteDraftFormTable();
        if(draftId != null){
            draft = dbHandler.GetDraftByDraftId(draftId);
            html.append("<html>"+ draft.getDraftHtml()+"</html>");
        }
    else{
            form=  dbHandler.GetFormByFormId(formId);
            html.append(form.getMobileHtml());
        }
        frmname.setText(formTitle);
        webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        //webView.getSettings().setUseWideViewPort(true);
        //Other webview settings
        webView.setScrollbarFadingEnabled(false);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.getSettings().setAllowFileAccess(true);
        webView.loadDataWithBaseURL("file:///android_asset/", html.toString(), "text/html", "utf-8", null);
        webView.addJavascriptInterface(new WebViewJavaScriptInterface(this), "app");
        startWebView();
        webView.setWebViewClient(new WebViewClient(){

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url){

                if (url.startsWith("https://") || url.startsWith("http://")) {
                    view.getContext().startActivity(
                            new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    return true;
                }
                else
                {
                    view.loadUrl(url);
                     webView.setVisibility(View.GONE);
                    return true;
                }
            }
        });



        BackLinearLayout = (LinearLayout)findViewById(R.id.LinearLayoutBack);

        BackLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundleFormResponse.putInt("UserId",userId);
                bundleFormResponse.putInt("ParentId",parentId);
                bundleFormResponse.putString("FormTitle",formTitle);
                bundleFormResponse.putString("DraftId",draftId);
                bundleFormResponse.putString("FormId",formId);


                int count=  dbHandler.getFormCount(formId);
                if (count>=1)
                {
                    Intent i = new Intent(FormResponseActivity.this,DraftFormActivity.class);
                    i.putExtras(bundleFormResponse);
                    startActivity(i);
                }else{
                    Intent i = new Intent(FormResponseActivity.this,FormActivity.class);
                    i.putExtras(bundleFormResponse);
                    startActivity(i);
                }

            }
        });

        btnBackResponse = (ImageButton)findViewById(R.id.imageButton_Back);

        btnBackResponse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundleFormResponse.putInt("UserId",userId);
                bundleFormResponse.putInt("ParentId",parentId);
                bundleFormResponse.putString("FormTitle",formTitle);
                bundleFormResponse.putString("DraftId",draftId);
                bundleFormResponse.putString("FormId",formId);

                int count=  dbHandler.getFormCount(formId);
                if (count>=1)
                {
                    Intent i = new Intent(FormResponseActivity.this,DraftFormActivity.class);
                    i.putExtras(bundleFormResponse);
                    startActivity(i);
                }else{
                    Intent i = new Intent(FormResponseActivity.this,FormActivity.class);
                    i.putExtras(bundleFormResponse);
                    startActivity(i);
                }

            }
        });




    }

public void AlertMessagge(String messagge){

    AlertDialog.Builder alertDialog = new AlertDialog.Builder(FormResponseActivity.this,AlertDialog.THEME_HOLO_LIGHT);


    alertDialog.setTitle("Form save as draft");
    alertDialog.setMessage(messagge);
    alertDialog
            .setCancelable(false)
            .setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int which) {

                        }
                    });
    AlertDialog alert = alertDialog.create();
    alert.show();

    }



    public class WebViewJavaScriptInterface{

        private Context context;
        /*
         * Need a reference to the context in order to sent a post message
         */
        public WebViewJavaScriptInterface(Context context){
            this.context = context;
        }

        /*
         * This method can be called from Android. @JavascriptInterface
         * required after SDK version 17.
         */
        @JavascriptInterface
        public void FormSubmit(String html, String json,String isUploadable,String field1_title ,String field1_value ,String field2_title ,String field2_value  ,String field3_title  ,String field3_value){
           /* progressDialogResponce = new ProgressDialog(FormResponseActivity.this, AlertDialog.THEME_HOLO_LIGHT);

            progressDialogResponce.setTitle("Form Cevaplama İşlemi");
            progressDialogResponce.setMessage("Form cevaplanıyor...");
            progressDialogResponce.setCanceledOnTouchOutside(false);
            progressDialogResponce.show();*/
              // dbHandler.DeleteDraftFormTable();
            bundleFormResponse.putString("FormId", formId);
            bundleFormResponse.putInt("UserId",userId);
            bundleFormResponse.putInt("ParentId",parentId);
            bundleFormResponse.putString("FormTitle",formTitle);
            if(draftId != null){
                DraftForm draftForm = new DraftForm(Integer.parseInt(draftId),Integer.parseInt(formId),html,json,currentDateTimeString,userId,field1_title,field1_value,field2_title,field2_value,field3_title,field3_value,isUploadable);
                dbHandler.UpdateDraft(draftForm);
                if (isUploadable.equals("1")){
                    AlertMessagge("");
                }
                else{
                    AlertMessagge("Doldurulması gereken alanlar var.");
                }

            }
            else{
                if (isUploadable.equals("1")){
                    AlertMessagge("");
                }
                else{
                    AlertMessagge("Doldurulması gereken alanlar var.");
                }
             //  String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                DraftForm form = new DraftForm(0,Integer.parseInt(formId),html,json, currentDateTimeString,userId,field1_title,field1_value,field2_title,field2_value,field3_title,field3_value,isUploadable);
                dbHandler.CreateDraftForm(form);
                draftId = dbHandler.GetLastDraftId(formId);

            }
        }
    }
    private void startWebView() {

        //Create new webview Client to show progress dialog
        //Called When opening a url or click on link

        webView.setWebViewClient(new WebViewClient() {
            ProgressDialog progressDialog;


            // Called when all page resources loaded
            public void onPageFinished(WebView view, String url) {

                try{
                    // Close progressDialog
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                }catch(Exception exception){
                    exception.printStackTrace();
                }
            }

        });



        // implement WebChromeClient inner class
        // we will define openFileChooser for select file from camera
        webView.setWebChromeClient(new WebChromeClient() {

            // openFileChooser for Android 3.0+

            public void openFileChooser(ValueCallback uploadMsg) {

                Log.i("For Android < 3.0", "called");

                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);


                i.addCategory(Intent.CATEGORY_OPENABLE);

                i.setType("*/*");
                FormResponseActivity.this.startActivityForResult(
                        Intent.createChooser(i, "File Browser"),
                        FILECHOOSER_RESULTCODE);
            }

            // For Android 3.0+
            public void openFileChooser(ValueCallback uploadMsg,
                                        String acceptType) {

                Log.i("For Android 3.0+", "called");

                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);

                i.addCategory(Intent.CATEGORY_OPENABLE);

                i.setType("*/*");
                FormResponseActivity.this.startActivityForResult(
                        Intent.createChooser(i, "File Browser"),
                        FILECHOOSER_RESULTCODE);
            }

            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                openFileChooser(uploadMsg);

                Log.i("For Android Jellybeans", "called");

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

        if(requestCode==FILECHOOSER_RESULTCODE)
        {

            if (null == this.mUploadMessage) {
                return;
            }

            Uri result=null;

            try{
                if (resultCode != RESULT_OK) {

                    result = null;

                } else {

                    // retrieve from the private variable if the intent is null
                    result = intent == null ? mCapturedImageURI : intent.getData();
                }
            }
            catch(Exception e)
            {
                Toast.makeText(getApplicationContext(), "activity :"+e, Toast.LENGTH_LONG).show();
            }

            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;

        }

    }

    // Open previous opened link from history on webview when back button pressed

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(FormResponseActivity.this);
        alertDialog.setMessage("Değişiklikleri kaydetmeden çıkmak istediğinize emin misiniz ?");
        alertDialog
                .setCancelable(false)
                .setPositiveButton("Evet",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                bundleFormResponse.putInt("UserId",userId);
                                bundleFormResponse.putInt("ParentId",parentId);
                                bundleFormResponse.putString("FormTitle",formTitle);
                                bundleFormResponse.putString("DraftId",draftId);
                                bundleFormResponse.putString("FormId",formId);

                                if(draftId != null){
                                    Intent i = new Intent(FormResponseActivity.this, DraftFormActivity.class);
                                    i.putExtras(bundleFormResponse);
                                    startActivity(i);
                                    finish();
                                }
                                else{
                                    Intent i = new Intent(FormResponseActivity.this, FormActivity.class);
                                    i.putExtras(bundleFormResponse);
                                    startActivity(i);
                                    finish();
                                }
                            }
                        })
                .setNegativeButton("Hayır",
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
        preferences= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        // editor = preferences.edit();

        String sessionUserName=preferences.getString("UserName", "NULL");
        String sessionPassword=preferences.getString("Password", "NULL");

        if (sessionUserName.contains("NULL") && sessionPassword.contains("NULL")){
            Intent i = new Intent(FormResponseActivity.this,MainActivity.class);
            startActivity(i);
        }
        //----------------------------------------Session Kontrol
    }


}
