package com.prg.xformbuilder.xformbuilder;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import android.util.JsonReader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Handler;

import com.markupartist.android.widget.PullToRefreshListView;
import com.markupartist.android.widget.PullToRefreshListView.OnRefreshListener;
import com.yalantis.phoenix.PullToRefreshView;

public class FormActivity extends Activity {

    int parentId = 0, userId = 0, jsonParentId = 0, jsonFormId = 0, jsonUserId = 0, PutUserId, PutFormId, PutParentId, draftId;
    boolean InternetConnection = false;
    FormAdaptor adaptor;
    ImageButton ButtonLogout, ButtonSync, ButtonSettings;
    ListView lv;
    String jsonFormTitle = "", jsonUserName = "", jsonMobileHtml = "", jsonImage = "", PutJsonCode;
    DatabaseHandler dbHandler;
    final Bundle bundleForm = new Bundle();
    ProgressDialog progressDialogFormList;
    User GetUserSync;
    public static final int REFRESH_DELAY = 2000;
    private PullToRefreshListView mPullToRefreshView;
    PutDraftForm putDraftForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_form);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.formlist_titlebar);
        dbHandler = new DatabaseHandler(getApplicationContext());
        Bundle bundle = getIntent().getExtras();
        parentId = bundle.getInt("ParentId");
        userId = bundle.getInt("UserId");

        //-------------------------Progress Dialog Baslangıc
        progressDialogFormList = new ProgressDialog(FormActivity.this, AlertDialog.THEME_HOLO_LIGHT);
        progressDialogFormList.setTitle("Senkronize işlemleri");
        progressDialogFormList.setMessage("Formlarınız Yükleniyor...");
        progressDialogFormList.setCanceledOnTouchOutside(false);
        //--------------------------Progress Dialog Bitis


        lv = (ListView) findViewById(R.id.liste);
        ButtonLogout = (ImageButton) findViewById(R.id.imageButton_logout);
        ButtonSync = (ImageButton) findViewById(R.id.imageButton_sync);
        ButtonSettings = (ImageButton) findViewById(R.id.imageButton_settings);

        //--------------------------------------Internet Connection baslangıc
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            InternetConnection = true;
        } else
            InternetConnection = false;
        //--------------------------------------Internet Connection bitis

        GetUserSync = dbHandler.GetUserByUserIdForSettings(userId);
        if (GetUserSync.getSync().equals("true") && InternetConnection) {
            progressDialogFormList.show();
        }
        //------------------------------------Session Kontrol
        SharedPreferences preferences;     //preferences için bir nesne tanımlıyorum.
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String sessionUserName = preferences.getString("UserName", "NULL");
        String sessionPassword = preferences.getString("Password", "NULL");
        if (sessionUserName.contains("NULL") && sessionPassword.contains("NULL")) {
            Intent i = new Intent(FormActivity.this, MainActivity.class);
            startActivity(i);
        }
        //------------------------------------Session Kontrol

        if (InternetConnection && GetUserSync.getSync().equals("true")) {

           /* new Thread(new Runnable(){
                @Override
                public void run() { */
                    try {
                        List<Form> formListPut = dbHandler.getAllFormListVw(String.valueOf(parentId));
                        int pFormId = 0;
                        for (int i = 0; i < formListPut.size(); i++) {
                            pFormId = formListPut.get(i).getFormId();
                            int count = dbHandler.getFormDraftCount(String.valueOf(pFormId));
                            if (count >= 1) {
                                List<DraftForm> draftFormsPut = dbHandler.getAllIsUploadDraftFormListByFormId(String.valueOf(pFormId));
                                for (int k = 0; k < draftFormsPut.size(); k++) {
                                    draftId = draftFormsPut.get(k).getId();
                                    //SystemClock.sleep(3000);
                                    String HostUrl = "http://developer.xformbuilder.com/api/AppForm?userId=" + userId + "&formId=" + pFormId;
                                    new PutHttpAsyncTask().execute(HostUrl, String.valueOf(draftId));
                                }
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                //}
           // }).start();

        }


       /* //----------------------------------Verilerin Upload işlemi baslangıc
        List<Form> formListPut=dbHandler.getAllFormListVw(String.valueOf(parentId));
        FormList   formArray[] = new FormList[formListPut.size()];
        int pFormId=0,pDraftId=0;
        for (int i=0;i<formListPut.size();i++){
            pFormId= formListPut.get(i).getFormId();
            List<DraftForm> draftFormsPut =dbHandler.getAllDraftFormListVw(String.valueOf(pFormId));
            for (int k=0;k<draftFormsPut.size();k++){
                draftId=draftFormsPut.get(k).getId();
                // String HostUrl = "http://developer.xformbuilder.com/api/AppForm?userId=3366&formId=4446";

                //  new PutHttpAsyncTask().execute(HostUrl);

            }
        }
        //----------------------------------Verilerin Upload işlemi bitis*/

        ButtonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(FormActivity.this);
                alertDialog.setMessage("Oturum kapatılsın mı ?");
                alertDialog
                        .setCancelable(false)
                        .setPositiveButton("Evet",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        //----------------------------------------Session Kontrol
                                        SharedPreferences preferences;     //preferences için bir nesne tanımlıyorum.
                                        SharedPreferences.Editor editor;        //preferences içerisine bilgi girmek için tanımlama
                                        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                        editor = preferences.edit();
                                        editor.remove("UserName");
                                        editor.remove("Password");
                                        editor.remove("UserId");
                                        editor.remove("ParentId");
                                        editor.commit();
                                        //----------------------------------------Session Kontrol
                                        Intent i = new Intent(FormActivity.this, MainActivity.class);
                                        startActivity(i);
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
        });

        ButtonSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (InternetConnection) {
                    new HttpAsyncTask().execute("http://developer.xformbuilder.com/api/AppForm?userId=" + userId);
                    progressDialogFormList.show();
                } else {
                    Toast.makeText(getApplicationContext(), "Lütfen Internet bağlantınızı kontrol ediniz.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        ButtonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundleForm.putInt("UserId", userId);
                Intent i = new Intent(FormActivity.this, SettingsActivity.class);
                i.putExtras(bundleForm);
                startActivity(i);
            }
        });
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectFormId = ((TextView) view.findViewById(R.id.frmId)).getText().toString();
                String selectFormTitle = ((TextView) view.findViewById(R.id.formTitle)).getText().toString();
                bundleForm.putString("FormId", selectFormId);
                bundleForm.putInt("UserId", userId);
                bundleForm.putInt("ParentId", parentId);
                bundleForm.putString("FormTitle", selectFormTitle);
                int count = dbHandler.getFormCount(selectFormId);
                if (count >= 1) {
                    Intent i = new Intent(FormActivity.this, DraftFormActivity.class);
                    i.putExtras(bundleForm);
                    startActivity(i);
                } else {
                    Intent i = new Intent(FormActivity.this, FormResponseActivity.class);
                    i.putExtras(bundleForm);
                    startActivity(i);
                }
            }
        });
        try {
            GetFormList();
            mPullToRefreshView = (PullToRefreshListView) findViewById(R.id.liste);
            mPullToRefreshView.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    new GetDataAsync().execute();

                    // mPullToRefreshView.onRefreshComplete();
                }
            });
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Verileri çekerken hata oluştu lütfen daha sonra tekrar deneyiniz.", Toast.LENGTH_SHORT).show();
            Log.d("ReadWeatherJSONFeedTask", e.getLocalizedMessage());
        }
    }

    private class GetDataAsync extends AsyncTask<String, String, String> {
        protected String doInBackground(String... strings) {
            new HttpAsyncTask().execute("http://developer.xformbuilder.com/api/AppForm?userId=" + userId);
            return null;
        }

        @Override
        protected void onPreExecute() {
            MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.sound1);
            mp.start();
        }

        @Override
        protected void onPostExecute(String s) {
            mPullToRefreshView.onRefreshComplete();
            MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.sound2);
            mp.start();
        }
    }

    private void GetFormList() {
        //Internet baglantısı var ise web apiden formları cekiyoruz.
        if (InternetConnection) {

            if (GetUserSync.getSync().equals("true")) {
                new HttpAsyncTask().execute("http://developer.xformbuilder.com/api/AppForm?userId=" + userId);
            } else {
                try {
                    List<Form> formList = dbHandler.getAllFormListVw(String.valueOf(parentId));
                    FormList formArray[] = new FormList[formList.size()];
                    for (int i = 0; i < formList.size(); i++) {
                        formArray[i] = new FormList(formList.get(i).getFormId(), formList.get(i).getFormTitle(), formList.get(i).getUserName(), R.mipmap.icon1);
                    }
                    adaptor = new FormAdaptor(getApplicationContext(), R.layout.line_layout, formArray);
                    lv.setAdapter(adaptor);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Verileri çekerken hata oluştu lütfen daha sonra tekrar deneyiniz.", Toast.LENGTH_SHORT).show();
                    Log.d("ReadWeatherJSONFeedTask", e.getLocalizedMessage());
                }
            }
        } else {
            try {
                List<Form> formList = dbHandler.getAllFormListVw(String.valueOf(parentId));
                FormList formArray[] = new FormList[formList.size()];
                for (int i = 0; i < formList.size(); i++) {
                    formArray[i] = new FormList(formList.get(i).getFormId(), formList.get(i).getFormTitle(), formList.get(i).getUserName(), R.mipmap.icon1);
                }
                // progressDialogFormList.dismiss();
                adaptor = new FormAdaptor(getApplicationContext(), R.layout.line_layout, formArray);
                lv.setAdapter(adaptor);
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Verileri çekerken hata oluştu lütfen daha sonra tekrar deneyiniz.", Toast.LENGTH_SHORT).show();
                Log.d("ReadWeatherJSONFeedTask", e.getLocalizedMessage());
            }
        }
    }

    public String GET(String url) {
        InputStream inputStream = null;
        String result = "";
        try {
            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();
            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));
            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();
            // convert inputstream to string
            if (inputStream != null) {
                result = convertInputStreamToString(inputStream);
            } else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }
        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;
        inputStream.close();
        return result;

    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return GET(urls[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            try {

                JSONArray jsonArray = new JSONArray(result);
                boolean deleteForm = dbHandler.DeleteFormTable();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    jsonFormTitle = obj.getString("FormTitle");
                    jsonFormId = obj.getInt("FormId");
                    jsonParentId = obj.getInt("ParentId");
                    jsonUserName = obj.getString("UserName");
                    jsonMobileHtml = obj.getString("MobileHtml");
                    jsonImage = obj.getString("FormImage");

                   /* if (jsonImage.equals("null")){
                        FormImageByte = jsonImage.getBytes();
                      //  Bitmap bmp = BitmapFactory.decodeByteArray(FormImageByte, 0, FormImageByte.length);
                       // imageview.setImageBitmap(bmp);
                    }*/
                    if (deleteForm) {
                        Form form = new Form(0, jsonFormTitle, jsonFormId, jsonParentId, jsonUserName, jsonMobileHtml, userId, jsonImage);
                        dbHandler.CreateForm(form);
                    } else {
                        //TODO:tablolar silinemediyse kontrol edilecek. Login sayfasına geri gönder.
                    }
                }
                try {
                    List<Form> formList = dbHandler.getAllFormListVw(String.valueOf(parentId));
                    FormList formArray[] = new FormList[formList.size()];
                    for (int i = 0; i < formList.size(); i++) {
                       /*
                        FormImageByte = formList.get(i).getFormImage().getBytes(Charset.forName("UTF-8"));
                        Bitmap bmp = BitmapFactory.decodeByteArray(FormImageByte, 0, FormImageByte.length);
                        imageViewForm.setImageBitmap(bmp);*/

                        formArray[i] = new FormList(formList.get(i).getFormId(), formList.get(i).getFormTitle(), formList.get(i).getUserName(), R.mipmap.icon1);
                    }
                    progressDialogFormList.dismiss();
                    adaptor = new FormAdaptor(getApplicationContext(), R.layout.line_layout, formArray);
                    lv.setAdapter(adaptor);
                } catch (Exception e) {
                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Lütfen daha sonra tekar deneyiniz.", Toast.LENGTH_SHORT).show();
                Log.d("ReadWeatherJSONFeedTask", e.getLocalizedMessage());
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_form, menu);
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
            Intent i = new Intent(FormActivity.this, MainActivity.class);
            startActivity(i);
        }
        //----------------------------------------Session Kontrol
    }

    public String PUT(String url, String JsonCode, String formId, String userId, String DraftId) {
        InputStream inputStream = null;
        String result = "";
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPut httpPost = new HttpPut(url);

            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
            nameValuePair.add(new BasicNameValuePair("JsonCode",JsonCode));
            nameValuePair.add(new BasicNameValuePair("FormId", formId));
            nameValuePair.add(new BasicNameValuePair("UserId", userId));
            nameValuePair.add(new BasicNameValuePair("DraftId", DraftId));

            //Encoding POST data
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            try {
                HttpResponse response = httpclient.execute(httpPost);
                inputStream = response.getEntity().getContent();
                // write response to log
                Log.d("Http Post Response:", response.toString());
            } catch (ClientProtocolException e) {
                // Log exception
                e.printStackTrace();
            } catch (IOException e) {
                // Log exception
                e.printStackTrace();
            }
            // convert inputstream to string
            if (inputStream != null) {
                result = PutConvertInputStreamToString(inputStream);

                int jFormId = 0, jDraftId = 0;
                boolean jSave = false;
                try {
                    JSONObject jsonObj = new JSONObject(result);
                    jDraftId = jsonObj.getInt("DraftId");
                    jSave = jsonObj.getBoolean("Save");
                    if (jSave) {
                        boolean success = dbHandler.DeleteDraftFormByDraftId(jDraftId);

                        if (success) {
                            Toast.makeText(getApplicationContext(), "Form Kaydedildi. " + jDraftId, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Silinemedii..." + jDraftId, Toast.LENGTH_SHORT).show();
                        }
                    }


                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Lutfen bilgileri kontrol ediniz.", Toast.LENGTH_SHORT).show();
                    Log.d("ReadWeatherJSONFeedTask", e.getLocalizedMessage());
                }
            } else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }
        return result;
    }

    private static String PutConvertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;
        inputStream.close();
        return result;

    }


    private class PutHttpSyncTask {

        protected void execute(String... urls) {
            try {
                DraftForm draftForm = dbHandler.GetDraftByDraftId(urls[1]);
                PutJsonCode = draftForm.getDraftJson();
                PutFormId = draftForm.getFormId();
                PutUserId = draftForm.getUserId();
                putDraftForm = new PutDraftForm(draftId, PutFormId, PutJsonCode, PutUserId);
            } catch (Exception e) {

            }


            //onPostExecute(PUT(urls[0], putDraftForm));
        }

        protected void onPostExecute(String result) {
            int jFormId = 0, jDraftId = 0;
            boolean jSave = false;
            try {
                JSONObject jsonObj = new JSONObject(result);
                jDraftId = jsonObj.getInt("DraftId");
                jSave = jsonObj.getBoolean("Save");
                if (jSave) {
                    boolean success = dbHandler.DeleteDraftFormByDraftId(jDraftId);

                    if (success) {
                        Toast.makeText(getApplicationContext(), "Form Kaydedildi. " + jDraftId, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Silinemedii..." + jDraftId, Toast.LENGTH_SHORT).show();
                    }
                }


            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Lutfen bilgileri kontrol ediniz.", Toast.LENGTH_SHORT).show();
                Log.d("ReadWeatherJSONFeedTask", e.getLocalizedMessage());
            }
        }


    }

    private class PutHttpAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
           try {
                DraftForm draftForm = dbHandler.GetDraftByDraftId(urls[1]);
                PutJsonCode = draftForm.getDraftJson();
                PutFormId = draftForm.getFormId();
                PutUserId = draftForm.getUserId();
               // putDraftForm = new PutDraftForm(draftId, PutFormId, PutJsonCode, PutUserId);
            } catch (Exception e) {

            }
            return PUT(urls[0], PutJsonCode,String.valueOf(PutFormId),String.valueOf(PutUserId),String.valueOf(urls[1]));
        }

        @Override
        protected void onPostExecute(String result) {

        }


    }

}
