package com.prg.xformbuilder.xformbuilder;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
 import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.hintdesk.core.utils.JSONHttpClient;
import com.loopj.android.http.Base64;
import com.onesignal.OneSignal;
import com.prg.xformbuilder.xformbuilder.common.UploadErrorCode;
import com.prg.xformbuilder.xformbuilder.events.UploadFilesCompleteListener;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;


public class FormActivity extends Activity {
    String jsonFormTitle = "", jsonUserName = "", jsonMobileHtml = "", jsonImage = "", PutJsonCode="",versionName="",sessionUserName = "", sessionPassword = "",currentDateTimeString ="";
    int parentId = 0, userId = 0, jsonParentId = 0, jsonFormId = 0, PutUserId, PutFormId,  draftId,draftCount=0,totalCount_=0,totalDraftCount=0,counter_ = 1;
    FormAdaptor adaptor;
    ImageButton ButtonLogout, ButtonSync, ButtonSettings;
    ImageView imgNoForms;
    ListView lv;
    DatabaseHandler dbHandler;
    final Bundle bundleForm = new Bundle();
    ProgressDialog progressDialogFormList;
    User GetUserSync;
    final static String AppId = "20a9d85f-3a67-4c91-be5b-0aff74fa00df";
    final static String AppKey ="61993513-c1c5-4ce1-aacd-3d37e36627b7";

    PtrClassicFrameLayout ptrFrameLayout;
     private List<HDFile> uploadedFiles;
    private UploadFilesCompleteListener uploadFilesCompleteListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_form);

        // Pass in your app's Context, Google Project number, your OneSignal App ID, and NotificationOpenedHandler
        OneSignal.init(this, "71156653394", "52ee36a0-e8c3-11e4-b391-0370dbb1438c", new ExampleNotificationOpenedHandler());



        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.formlist_titlebar);
        try {

            versionName = getApplicationContext().getPackageManager()
                    .getPackageInfo(getApplicationContext().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        dbHandler = new DatabaseHandler(getApplicationContext());
        Bundle bundle = getIntent().getExtras();
        parentId = bundle.getInt("ParentId");
        userId = bundle.getInt("UserId");
        progressDialogFormList = new ProgressDialog(FormActivity.this, AlertDialog.THEME_HOLO_LIGHT);
        lv = (ListView) findViewById(R.id.liste);
        ButtonLogout = (ImageButton) findViewById(R.id.imageButton_logout);
        ButtonSync = (ImageButton) findViewById(R.id.imageButton_sync);
        ButtonSettings = (ImageButton) findViewById(R.id.imageButton_settings);
        imgNoForms = (ImageView)findViewById(R.id.imageView_noForms);
        //------------------------------------Session Kontrol
        SharedPreferences preferences;     //preferences için bir nesne tanımlıyorum.
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
         sessionUserName = preferences.getString("UserName", "NULL");
         sessionPassword = preferences.getString("Password", "NULL");
        if (sessionUserName.contains("NULL") && sessionPassword.contains("NULL")) {
            Intent i = new Intent(FormActivity.this, MainActivity.class);
            startActivity(i);
        }
        //------------------------------------Session Kontrol


        if (savedInstanceState == null)
        {
    //-------------------------Progress Dialog Baslangıc
    progressDialogFormList.setTitle(R.string.SyncProcess);
    progressDialogFormList.setMessage(getString(R.string.LoadForms));
    progressDialogFormList.setCanceledOnTouchOutside(false);
    //--------------------------Progress Dialog Bitis
    try {
        GetFormList();


        ptrFrameLayout = (PtrClassicFrameLayout) findViewById(R.id.rotate_header_list_view_frame);

        ptrFrameLayout.setLastUpdateTimeRelateObject(this);
        ptrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {

                    frame.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(NetWorkControl()){
                                new GetDataAsync().execute();
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(), R.string.CheckYourNetwork, Toast.LENGTH_SHORT).show();
                                ptrFrameLayout.refreshComplete();
                            }

                        }
                    }, 1800);


                  /*  MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.sound1);
                    mp.start();*/

            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }
        });

    }
    catch (Exception e) {
        dbHandler.CreateLog(new LogError(0, "GetFormList  FormActivity", "Formlistesi çekilirken oluşan bir hata", e.getMessage().toString(), currentDateTimeString,sessionUserName,versionName,userId,parentId));

        Toast.makeText(getApplicationContext(), R.string.GetFormDataError, Toast.LENGTH_SHORT).show();
         Intent i = new Intent(FormActivity.this,MainActivity.class);
        startActivity(i);

     }
}


        ButtonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              Logout();
            }
        });

        ButtonSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetWorkControl()) {
                    new HttpAsyncTask().execute("http://developer.xformbuilder.com/api/AppForm?userId=" + userId+"&mobileType=1&appId="+AppId+"&appKey="+AppKey);
                    SendServerDraftData();
                    SendServerLogData();
                    progressDialogFormList.show();
                } else {
                    Toast.makeText(getApplicationContext(),R.string.CheckYourNetwork, Toast.LENGTH_SHORT).show();
                }
            }
        });
        ButtonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundleForm.putInt("UserId", userId);
                bundleForm.putInt("ParentId", parentId);
                Intent i = new Intent(FormActivity.this, SettingsActivity.class);
                i.putExtras(bundleForm);
                startActivity(i);
                overridePendingTransition(R.anim.left_animation, R.anim.out_right_animation);

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
                    overridePendingTransition(R.anim.left_animation, R.anim.out_right_animation);

                } else {
                    Intent i = new Intent(FormActivity.this, FormResponseActivity.class);
                    i.putExtras(bundleForm);
                     startActivity(i);
                }
            }
        });
    }

    private void Logout() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(FormActivity.this,AlertDialog.THEME_HOLO_LIGHT);
        alertDialog.setMessage( getString(R.string.LogOut));
        alertDialog
                .setTitle("XFORMBUILDER")
                .setCancelable(false)
                .setPositiveButton(R.string.Yes,
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
                                dbHandler.DeleteSplashValue();
                                //----------------------------------------Session Kontrol
                                Intent i = new Intent(FormActivity.this, MainActivity.class);
                                startActivity(i);
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
    public void onBackPressed() {
        Logout();
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




    private class GetDataAsync extends AsyncTask<String, String, String> {
        protected String doInBackground(String... strings) {
            new HttpAsyncTask().execute("http://developer.xformbuilder.com/api/AppForm?userId=" + userId+"&mobileType=1&appId="+AppId+"&appKey="+AppKey);
            return null;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(String s) {
            ptrFrameLayout.refreshComplete();
            //mPullToRefreshView.onRefreshComplete();
            MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.sound2);
            mp.start();
        }
    }

    //----------------------------------Data get in local database-------------------------------------//
    private void SetFormListInListView() {
        List<Form> formList = null;
        try {
            if(parentId != 0 || userId != 0){
                try{
                   formList = dbHandler.getAllFormListVw(String.valueOf(parentId));

                }
                catch (Exception e){
                    dbHandler.CreateLog(new LogError(0, "SetFormListInListView  FormActivity", "Kullanıcıya ait formlar çekilirken ouşan bir hata", e.getMessage().toString(), currentDateTimeString, sessionUserName,versionName,userId,parentId));

                }

                if(formList.size() > 0)
                {
                    lv.setVisibility(View.VISIBLE);
                    imgNoForms.setVisibility(View.GONE);
                    FormList formArray[] = new FormList[formList.size()];
                    for (int i = 0; i < formList.size(); i++) {
                        Bitmap bmp = null;
                        if(!formList.get(i).getFormImage().equals("")){
                            try{
                                byte [] FormImageByte = formList.get(i).getFormImage().getBytes("UTF-8");
                                InputStream stream = new ByteArrayInputStream(Base64.decode(FormImageByte, Base64.DEFAULT));
                                bmp =  BitmapFactory.decodeStream(stream);
                            }
                            catch (Exception e){
                                dbHandler.CreateLog(new LogError(0, "SetFormListInListView  FormActivity", "Base64 stringin resime çevrilmesi sırasında oluşan bir hata", e.getMessage().toString(), currentDateTimeString,sessionUserName,versionName,userId,parentId));
                                }

                        }
                        try{
                            int count = dbHandler.getFormCount(String.valueOf(formList.get(i).getFormId()));
                            if (count >= 1) {
                                formArray[i] = new FormList(formList.get(i).getFormId(), formList.get(i).getFormTitle(),"Created By " + formList.get(i).getUserName(), bmp,String.valueOf(count),R.mipmap.appbar_draw_pencil);
                            }
                            else{
                                formArray[i] = new FormList(formList.get(i).getFormId(), formList.get(i).getFormTitle(),"Created By "+ formList.get(i).getUserName(), bmp,"",R.mipmap.appbar_draw_pencil_white);
                            }
                        }
                        catch (Exception e){
                            dbHandler.CreateLog(new LogError(0, "SetFormListInListView  FormActivity", "Formlistesi çekilirken ve listviewe basılırken oluşan bir hata", e.getMessage().toString(), currentDateTimeString,sessionUserName,versionName,userId,parentId));
                        }
                    }
                    adaptor = new FormAdaptor(getApplicationContext(), R.layout.line_layout, formArray);
                    lv.setAdapter(adaptor);
                }
                else{
                    lv.setVisibility(View.GONE);
                    imgNoForms.setVisibility(View.VISIBLE);
                    imgNoForms.setImageResource(R.drawable.noformfound);
                }
            }
            else{
                Toast.makeText(getApplicationContext(), R.string.SessionTimeOut, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(FormActivity.this,MainActivity.class);
                startActivity(intent);
            }

        }
        catch (Exception e) {
            dbHandler.CreateLog(new LogError(0, "SetFormListInListView  FormActivity", "", e.getMessage().toString(), currentDateTimeString,sessionUserName,versionName,userId,parentId));

            Intent intent;
            Toast.makeText(getApplicationContext(), getString(R.string.UploadFormsError) , Toast.LENGTH_SHORT).show();
            if(userId != 0 && parentId != 0){
                 intent = new Intent(FormActivity.this,FormActivity.class);
                bundleForm.putInt("UserId", userId);
                bundleForm.putInt("ParentId", parentId);
                intent.putExtras(bundleForm);
            }
            else
            {
                Toast.makeText(getApplicationContext(), R.string.GetFormDataError, Toast.LENGTH_SHORT).show();
                intent = new Intent(FormActivity.this,MainActivity.class);

            }
             startActivity(intent);

        }

    }

    private  void SendServerLogData(){
        List<LogError> logList = null;
        try{
             logList = dbHandler.getAllLogList();
            if(logList.size()>0){
                new LogAsyncTask().execute(logList);
            }
      }
        catch (Exception e){

         }


    }


    private void SendServerDraftData(){
        List<Form> formListPut = null;
        List<DraftForm> draftFormsPut = null;
        try {
            if(parentId != 0 || userId != 0){
                try{
                    formListPut = dbHandler.getAllFormListVw(String.valueOf(parentId));

                }
                catch (Exception e){
                    dbHandler.CreateLog(new LogError(0, "SendServerDraftData  FormActivity", "formları servere yollarken karşılaşılan hata", e.getMessage().toString(), currentDateTimeString,sessionUserName,versionName,userId,parentId));

                }

                int pFormId = 0;
                try{
                    totalDraftCount = dbHandler.getAllFormDraftCount();
                    totalCount_ = totalDraftCount;
                }
                catch (Exception e){
                    dbHandler.CreateLog(new LogError(0, "SendServerDraftData  FormActivity", "toplam upload edilebilir draft kayıt sayısı çekilirken karşılaşılan bir hata", e.getMessage().toString(), currentDateTimeString,sessionUserName,versionName,userId,parentId));
               }

                if(formListPut.size() > 0){
                    for (int i = 0; i < formListPut.size(); i++) {
                        pFormId = formListPut.get(i).getFormId();
                        try {
                            draftCount = dbHandler.getFormDraftCount(String.valueOf(pFormId));

                        }
                        catch (Exception e){
                            dbHandler.CreateLog(new LogError(0, "SendServerDraftData  FormActivity", "forma ait draft sayısı çekilirken karşılaşılan bir hata", e.getMessage().toString(), currentDateTimeString,sessionUserName,versionName,userId,parentId));

                        }

                        if (draftCount >= 1) {
                            try{
                                draftFormsPut = dbHandler.getAllIsUploadDraftFormListByFormId(String.valueOf(pFormId));

                            }
                            catch (Exception e){
                                dbHandler.CreateLog(new LogError(0, "SendServerDraftData  FormActivity", "forma ait toplam upload edilebilir draft sayısı çekilirken oluşan bir hata", e.getMessage().toString(), currentDateTimeString,sessionUserName,versionName,userId,parentId));

                            }
                            if(draftFormsPut.size()>0){
                                     for (int k = 0; k < draftFormsPut.size(); k++) {
                                    draftId = draftFormsPut.get(k).getId();
                                    String HostUrl = "http://developer.xformbuilder.com/api/AppForm?userId="+userId+"&formId="+pFormId+"&appId="+AppId+"&appKey="+AppKey;
                                    new PutHttpAsyncTask().execute(HostUrl, String.valueOf(draftId));

                                }
                            }

                        }

                    }
                }

            }
            else{
                Toast.makeText(getApplicationContext(), R.string.SessionTimeOut, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(FormActivity.this,MainActivity.class);
                startActivity(intent);
            }
        } catch (Exception ex) {
            dbHandler.CreateLog(new LogError(0, "SendServerDraftData  FormActivity", "", ex.getMessage().toString(), currentDateTimeString,sessionUserName,versionName,userId,parentId));

            Toast.makeText(getApplicationContext(), getString(R.string.UploadFormsError), Toast.LENGTH_SHORT).show();
            ex.printStackTrace();
            Intent intent;
            if(userId != 0 && parentId != 0){
               intent = new Intent(FormActivity.this,FormActivity.class);
                bundleForm.putInt("UserId", userId);
                bundleForm.putInt("ParentId", parentId);
                intent.putExtras(bundleForm);
            }
            else
            {
                Toast.makeText(getApplicationContext(), R.string.GetFormDataError, Toast.LENGTH_SHORT).show();
                intent = new Intent(FormActivity.this,MainActivity.class);

            }

             startActivity(intent);
        }
    }

    private void GetFormList() {
        try{
            if(userId != 0){
                try {
                    GetUserSync = dbHandler.GetUserByUserIdForSettings(userId);
                }
                catch (Exception e){
                    dbHandler.CreateLog(new LogError(0, "GetFormList  FormActivity", "kullanıcının oto senkron bilgisi çekilirken karşılaşılan bir hata ", e.getMessage().toString(), currentDateTimeString,sessionUserName,versionName,userId,parentId));
                 }
                //Internet baglantısı var ise web apiden formları cekiyoruz.
                if (NetWorkControl() &&  GetUserSync != null && GetUserSync.getSync().equals("true")) {
                    progressDialogFormList.show();//api/AppForm?userId={userId}&mobileType={mobileType}&appId={appId}&appKey={appKey}
                    new HttpAsyncTask().execute("http://developer.xformbuilder.com/api/AppForm?userId="+userId+"&mobileType=1&appId=20a9d85f-3a67-4c91-be5b-0aff74fa00df&appKey=61993513-c1c5-4ce1-aacd-3d37e36627b7");
                    SendServerDraftData();
                }
                else {
                    SetFormListInListView();
                }
            }
            else{
                Toast.makeText(getApplicationContext(), R.string.SessionTimeOut, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(FormActivity.this,MainActivity.class);
                startActivity(intent);
            }

        }
        catch (Exception e){
            dbHandler.CreateLog(new LogError(0, "GetFormList  FormActivity", "", e.getMessage().toString(), currentDateTimeString,sessionUserName,versionName,userId,parentId));

            SetFormListInListView();

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
            dbHandler.CreateLog(new LogError(0, "GET  FormActivity", "urlin yanlış gönderilmesinden kaynaklanan bir hata", e.getMessage().toString(), currentDateTimeString,sessionUserName,versionName,userId,parentId));

         }
        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        String result = "";
        String line = "";
        try{
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            while ((line = bufferedReader.readLine()) != null)
                result += line;
            inputStream.close();
            return result;
        }
        catch (Exception e){


        }
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

                if(!result.equals("")){
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
                        try {
                            if (deleteForm) {
                                Form form = new Form(0, jsonFormTitle, jsonFormId, jsonParentId, jsonUserName, jsonMobileHtml, userId, jsonImage);
                                dbHandler.CreateForm(form);
                            } else {
                                Intent intent = new Intent(FormActivity.this,MainActivity.class);
                                startActivity(intent);
                            }
                        }
                        catch (Exception e){
                            dbHandler.CreateLog(new LogError(0, "HttpAsyncTask onPostExecute  FormActivity", "form kaydedilirken oluşan bir hata", e.getMessage().toString(), currentDateTimeString,sessionUserName,versionName,userId,parentId));

                            Intent intent = new Intent(FormActivity.this,MainActivity.class);
                            startActivity(intent);
                        }
                    }
                    try {
                        SetFormListInListView();

                        if(totalDraftCount == 0) {
                            progressDialogFormList.dismiss();
                        }
                    }
                    catch (Exception e) {
                        dbHandler.CreateLog(new LogError(0, "HttpAsyncTask onPostExecute  FormActivity", "formlar listviewe yerleştirilirken oluşan bir hata", e.getMessage().toString(), currentDateTimeString,sessionUserName,versionName,userId,parentId));

                     }
                }
            }
            catch (Exception e) {
                dbHandler.CreateLog(new LogError(0, "HttpAsyncTask onPostExecute  FormActivity", "", e.getMessage().toString(), currentDateTimeString,sessionUserName,versionName,userId,parentId));
                Toast.makeText(getApplicationContext(), R.string.GetFormDataError, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(FormActivity.this,MainActivity.class);
                startActivity(intent);
             }
        }

    }


    // NotificationOpenedHandler is implemented in its own class instead of adding implements to MainActivity so we don't hold on to a reference of our first activity if it gets recreated.
    public class ExampleNotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {
        /**
         * Callback to implement in your app to handle when a notification is opened from the Android status bar or
         * a new one comes in while the app is running.
         * This method is located in this activity as an example, you may have any class you wish implement NotificationOpenedHandler and define this method.
         *
         * @param message        The message string the user seen/should see in the Android status bar.
         * @param additionalData The additionalData key value pair section you entered in on onesignal.com.
         * @param isActive       Was the app in the foreground when the notification was received.
         */
        @Override
        public void notificationOpened(String message, JSONObject additionalData, boolean isActive) {
            String messageTitle = "", messageBody = " " + message;

            try {
                if (additionalData != null) {

                    String addCompany = additionalData.getString("COMPANY");



                    if (additionalData.has("title"))
                        messageTitle = additionalData.getString("title");
                    if (additionalData.has("actionSelected"))
                        messageBody += "\nPressed ButtonID: " + additionalData.getString("actionSelected");

                }
            } catch (JSONException e) {
                dbHandler.CreateLog(new LogError(0, "notificationOpened  FormActivity", "push notification açılırken oluşan bir hata", e.getMessage().toString(), currentDateTimeString,sessionUserName,versionName,userId,parentId));

            }

            new AlertDialog.Builder(FormActivity.this,AlertDialog.THEME_HOLO_LIGHT)
                    .setTitle(messageTitle)
                    .setMessage(messageBody)
                    .setCancelable(true)
                    .setPositiveButton(R.string.OK, null)
                    .create().show();
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

    public String [] ConvertFile(String path) {

        String [] value =  null;
        byte[] buffer = null;
        FileInputStream stream = null;
        File file = null;

        try{
            if (!path.equals("")){
                value =  new String[3];
                try{
                      file = new File(path);
                }
                catch (Exception e){
                    dbHandler.CreateLog(new LogError(0, "ConvertFile  FormActivity", "file convert işlemi", e.getMessage().toString(), currentDateTimeString,sessionUserName,versionName,userId,parentId));
               }

                value[0] = String.valueOf(file.length());
                value[1] = file.getName();
                buffer = new byte[(int) file.length()];

                try {
                    stream = new FileInputStream(file);
                } catch (FileNotFoundException e) {
                    dbHandler.CreateLog(new LogError(0, "ConvertFile  FormActivity", "file convert işlemi", e.getMessage().toString(), currentDateTimeString,sessionUserName,versionName,userId,parentId));

                    e.printStackTrace();
                }
                assert stream != null;
                try {

                    stream.read(buffer);
                } catch (IOException e) {
                    dbHandler.CreateLog(new LogError(0, "ConvertFile  FormActivity", "file convert işlemi", e.getMessage().toString(), currentDateTimeString,sessionUserName,versionName,userId,parentId));
                    e.printStackTrace();
                }
                try {
                    stream.close();
                } catch (IOException e) {
                    dbHandler.CreateLog(new LogError(0, "ConvertFile  FormActivity", "stream close işlemi", e.getMessage().toString(), currentDateTimeString,sessionUserName,versionName,userId,parentId));
                    e.printStackTrace();
                }
                value[2] = Base64.encodeToString(buffer, Base64.DEFAULT);
                return value;
            }
        }
        catch (Exception e){
            dbHandler.CreateLog(new LogError(0, "ConvertFile  FormResponse", "byte stringin bozuk olmasından kaynaklanan bir hata", e.getMessage().toString(), currentDateTimeString,sessionUserName,versionName,userId,parentId));
            return value;
        }
        return value;
    }


    public String PUT(String url, String JsonCode, String formId, String uId, String DraftId) {
        InputStream inputStream = null;
        String result = "";
        String rValue = "False";
        List<Files> files = null;
        String [] value = new String[3];
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPut httpPost = new HttpPut(url);
            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
            nameValuePair.add(new BasicNameValuePair("JsonCode",JsonCode));
            nameValuePair.add(new BasicNameValuePair("FormId", formId));
            nameValuePair.add(new BasicNameValuePair("UserId", uId));
            nameValuePair.add(new BasicNameValuePair("DraftId", DraftId));
            //Encoding POST data
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));

            } catch (UnsupportedEncodingException e) {
                dbHandler.CreateLog(new LogError(0, "PUT  FormActivity", "draft verileri servere yollanırken oluşan bir hata url den kaynaklı ya da parametrelerden kaynaklanıyo olabilir.", e.getMessage().toString(), currentDateTimeString,sessionUserName,versionName,userId,parentId));

                e.printStackTrace();
            }
            try {
                HttpResponse response = httpclient.execute(httpPost);
                inputStream = response.getEntity().getContent();
                // write response to log
                Log.d("Http Post Response:", response.toString());
            } catch (ClientProtocolException e) {
                dbHandler.CreateLog(new LogError(0, "PUT  FormActivity", "draft verileri servere yollandıktan sonra alınan cevapta bir hata oldugundan kaynaklanan hata", e.getMessage().toString(), currentDateTimeString,sessionUserName,versionName,userId,parentId));

                // Log exception
                e.printStackTrace();
            } catch (IOException e) {
                dbHandler.CreateLog(new LogError(0, "PUT  FormActivity", "draft verileri servere yollandıktan sonra alınan cevapta bir hata oldugundan kaynaklanan hata", e.getMessage().toString(), currentDateTimeString,sessionUserName,versionName,userId,parentId));

                // Log exception
                e.printStackTrace();
            }
            // convert inputstream to string
            if (inputStream != null) {
                result = PutConvertInputStreamToString(inputStream);

                int jFormId = 0, jDraftId = 0,jUserId=0;
                boolean jSave = false;
                String guid = "";
                try {
                    if(!result.equals("")){
                        JSONObject jsonObj = new JSONObject(result);
                        jDraftId = jsonObj.getInt("DraftId");
                        jSave = jsonObj.getBoolean("Save");
                        guid = jsonObj.getString("GroupId");
                        jFormId = jsonObj.getInt("FormId");
                        jUserId = jsonObj.getInt("UserId");
                        if (jSave) {
                             dbHandler.DeleteDraftFormByDraftId(jDraftId);
                            try{
                                files = dbHandler.GetFilesListByDraftId(String.valueOf(jDraftId));
                            }
                            catch (Exception e){
                                dbHandler.CreateLog(new LogError(0, "PUT  FormActivity", "file listesi çekilirken hata oluştu.", e.getMessage().toString(), currentDateTimeString,sessionUserName,versionName,userId,parentId));
                            }
                            if(files != null && files.size() > 0){
                                JSONObject jobject = new JSONObject();
                                JSONArray jArray = new JSONArray();
                                List<HDFile> selectedFiles = new ArrayList<HDFile>();
                                for(int i=0;i<files.size();i++){
                                    try {
                                        File file = new File(files.get(i).getPath());
                                        HDFile f = new HDFile();
                                        f.setFilePath(files.get(i).getPath());
                                        f.setName(file.getName());
                                        f.setId(String.valueOf(files.get(i).getId()));
                                        f.setFileId(String.valueOf(files.get(i).getId()));
                                        f.setElementId(files.get(i).getElementId());
                                        f.setGuId(guid);
                                        f.setSelected(true);
                                        f.setSize(String.valueOf(file.length()));
                                        f.setUserId(String.valueOf(jUserId));
                                        f.setFormId(files.get(i).getFormId());
                                        f.setUrl(null);
                                        selectedFiles.add(i,f);
                                        uploadFiles(selectedFiles);
                                     }
                                    catch (Exception e)
                                    {
                                        dbHandler.CreateLog(new LogError(0, "PUT  FormActivity", "Dosya bilgileri hdfile classına set edilirken hata oluştu.", e.getMessage().toString(), currentDateTimeString,sessionUserName,versionName,userId,parentId));

                                     }
                                }
                            }
                             rValue = "True";

                        }
                    }

                } catch (Exception e) {
                    dbHandler.CreateLog(new LogError(0, "PUT  FormActivity","dönen sonucun json objeye dönüşmemesinden kaynaklanan hata", e.getMessage().toString(), currentDateTimeString,sessionUserName,versionName,userId,parentId));
                    Toast.makeText(getApplicationContext(), R.string.CheckYourInfo, Toast.LENGTH_SHORT).show();
                 }
            } else
                rValue = "False";

        } catch (Exception e) {
            dbHandler.CreateLog(new LogError(0, "PUT  FormActivity", "", e.getMessage().toString(), currentDateTimeString,sessionUserName,versionName,userId,parentId));

         }
        return rValue;
    }




    public void uploadFiles(List<HDFile> files) {
        try{
            new UploadFilesTask().execute(files.toArray(new HDFile[files.size()]));
        }
        catch (Exception e){
            dbHandler.CreateLog(new LogError(0, "uploadFiles void  FormActivity", "upload file task execute edilirken hata ile karşılaşşıldı.", e.getMessage().toString(), currentDateTimeString,sessionUserName,versionName,userId,parentId));

        }

    }



  public class UploadFilesTask extends AsyncTask<HDFile, String, Integer> {
        Integer totalCount = 0;

        @Override
        protected Integer doInBackground(HDFile... params) {
            Integer uploadCount = 0;
            totalCount = params.length;
            uploadedFiles = new ArrayList<HDFile>();
            if(params.length>0){

                for (int index = 0; index < params.length ; index++) {
                    File file = new File(params[index].getFilePath());
                    JSONHttpClient jsonHttpClient = new JSONHttpClient();

                    HDFile[] hdFiles = null;
                    if(file != null)
                    {
                        try
                        {
                            String eId =params[index].getElementId();
                            String RguId = params[index].getGuId();
                            String fileSize = params[index].getSize();
                            String formId = params[index].getFormId();
                            String uId = params[index].getUserId();
                            String fileId = params[index].getFileId();
                            hdFiles = jsonHttpClient.PostFile("http://developer.xformbuilder.com/api/HDFiles?guid="+RguId+"&elementId="+eId+"&formId="+formId+"&fileSize="+fileSize+"&userId="+uId+"&fileId="+fileId,
                                    params[index].getId(), file, params[index].getName(),HDFile[].class);

                        }
                        catch (Exception e){
                            dbHandler.CreateLog(new LogError(0, "UploadFilesTask doInBackground  FormActivity", "File upload işleminde hata oluştu.", e.getMessage().toString(), currentDateTimeString,sessionUserName,versionName,userId,parentId));
                        }
                    }
                    if (hdFiles != null && hdFiles.length == 1) {
                        uploadedFiles.add(hdFiles[0]);
                        boolean deleteFile =   dbHandler.DeleteFilesById(Integer.parseInt(hdFiles[0].getId()));
                        if(deleteFile){
                            if(file.exists()){
                                file.delete();
                            }
                        }
                    }
                }
            }

            return uploadCount;
        }

        @Override
        protected void onPostExecute(Integer uploadCount) {
            UploadErrorCode errorCode = UploadErrorCode.OK;
            if (uploadCount == 0)
                errorCode = UploadErrorCode.Failed;
            else if (uploadCount < totalCount)
                errorCode = UploadErrorCode.PartlySuccessful;
            if (uploadFilesCompleteListener != null)
                uploadFilesCompleteListener.onCompleted(errorCode);


        }
    }








    private static String PutConvertInputStreamToString(InputStream inputStream) throws IOException {
        String line = "";
        String result = "";

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            while ((line = bufferedReader.readLine()) != null)
                result += line;
            inputStream.close();
            return result;


    }



    private class PutHttpAsyncTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... urls) {
           try {
                DraftForm draftForm = dbHandler.GetDraftByDraftId(urls[1]);
                PutJsonCode = draftForm.getDraftJson();
                PutFormId = draftForm.getFormId();
                PutUserId = draftForm.getUserId();
               // putDraftForm = new PutDraftForm(draftId, PutFormId, PutJsonCode, PutUserId);
            } catch (Exception e) {
               dbHandler.CreateLog(new LogError(0, "PutHttpAsyncTask doInBackground  FormActivity", "drafta ait bilgiler çekilirken alınan bir hata.", e.getMessage().toString(), currentDateTimeString,sessionUserName,versionName,userId,parentId));
                  }

            String rValue =  PUT(urls[0], PutJsonCode,String.valueOf(PutFormId),String.valueOf(PutUserId),String.valueOf(urls[1]));

            publishProgress(totalDraftCount,counter_++);

            return rValue;
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            progressDialogFormList.setMessage(getString(R.string.UploadForms)+values[0]+"/"+values[1]);
           if(values[0] == values[1] ){
               SetFormListInListView();
               progressDialogFormList.dismiss();
           }
            super.onProgressUpdate(values);
        }
        @Override
        protected void onPreExecute(){

        }
    }

    private class LogAsyncTask extends AsyncTask<List<LogError>, Void, String> {

        @Override
        protected String doInBackground(List<LogError>... urls) {
            String HostUrl = "http://developer.xformbuilder.com/api/ElmahError?appId="+AppId+"&appKey="+AppKey;
            return LOGPUT(HostUrl,urls[0]);

        }
        @Override
        protected void onPostExecute(String result) {
            if(result != null){

            }
        }
        @Override
        protected void onPreExecute(){

        }
    }

    public String LOGPUT(String url,List<LogError> list) {
        InputStream inputStream = null;
        String result = "";
        String rValue = "False";
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPut httpPost = new HttpPut(url);
            List<NameValuePair> nameValuePair = null;
            for(int i=0;i< list.size();i++){
                nameValuePair = new ArrayList<NameValuePair>();
                nameValuePair.add(new BasicNameValuePair("Id",String.valueOf(list.get(i).getId())));
                nameValuePair.add(new BasicNameValuePair("MetodName", list.get(i).getMethodName()));
                nameValuePair.add(new BasicNameValuePair("Exception", list.get(i).getErrorMessage()));
                nameValuePair.add(new BasicNameValuePair("ErrorDesc", list.get(i).getDescription() + "UserId:"+  String.valueOf(list.get(i).getUserId()) + "ParentId:"+ String.valueOf(list.get(i).getParentId())+ "UserName:"+ String.valueOf(list.get(i).getUserName()) ));
                nameValuePair.add(new BasicNameValuePair("Version", list.get(i).getVersion()));
                nameValuePair.add(new BasicNameValuePair("ErrorTime", list.get(i).getDate()));
                nameValuePair.add(new BasicNameValuePair("UserId",String.valueOf(list.get(i).getUserId())));
                nameValuePair.add(new BasicNameValuePair("MobileTypeId","1"));
                try {
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));

                } catch (UnsupportedEncodingException e) {
                    dbHandler.CreateLog(new LogError(0, "LOGPUT  FormActivity", "log verileri servere yollanırken oluşan bir hata url den kaynaklı ya da parametrelerden kaynaklanıyo olabilir.", e.getMessage().toString(), currentDateTimeString,sessionUserName,versionName,userId,parentId));
                    e.printStackTrace();
                }

                try {
                    HttpResponse response = httpclient.execute(httpPost);
                    inputStream = response.getEntity().getContent();
                    // write response to log
                    Log.d("Http Post Response:", response.toString());
                } catch (ClientProtocolException e) {
                    dbHandler.CreateLog(new LogError(0, "LOGPUT  FormActivity", "log verileri servere yollandıktan sonra alınan cevapta bir hata oldugundan kaynaklanan hata", e.getMessage().toString(), currentDateTimeString,sessionUserName,versionName,userId,parentId));

                    // Log exception
                    e.printStackTrace();
                } catch (IOException e) {
                    dbHandler.CreateLog(new LogError(0, "LOGPUT  FormActivity", "log verileri servere yollandıktan sonra alınan cevapta bir hata oldugundan kaynaklanan hata", e.getMessage().toString(), currentDateTimeString,sessionUserName,versionName,userId,parentId));

                    // Log exception
                    e.printStackTrace();
                }
                if (inputStream != null) {
                    result = PutConvertInputStreamToString(inputStream);

                    int Id = 0;

                    try {
                        if(!result.equals("")){
                            JSONObject jsonObj = new JSONObject(result);
                            Id = jsonObj.getInt("Id");
                            boolean success = dbHandler.DeleteLogById(Id);
                            if(success){
                                rValue = "True";
                            }
                            else {
                                rValue = "False";

                            }

                        }

                    } catch (Exception e) {
                        dbHandler.CreateLog(new LogError(0, "LOGPUT  FormActivity", "dönen sonucun json objeye dönüşmemesinden kaynaklanan hata", e.getMessage().toString(), currentDateTimeString,sessionUserName,versionName,userId,parentId));

                        Toast.makeText(getApplicationContext(), R.string.CheckYourInfo, Toast.LENGTH_SHORT).show();
                        Log.d("ReadWeatherJSONFeedTask", e.getLocalizedMessage());
                    }
                } else
                    rValue = "False";
            }

        } catch (Exception e) {
            dbHandler.CreateLog(new LogError(0, "LOGPUT  FormActivity", "", e.getMessage().toString(), currentDateTimeString,sessionUserName,versionName,userId,parentId));
        }
        return rValue;
    }


    private class FileAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String HostUrl = "http://developer.xformbuilder.com/api/FileUpload?userId="+urls[1]+"&formId="+urls[2]+"&appId="+AppId+"&appKey="+AppKey;
            return FILEPUT(HostUrl, urls[0]);

        }
        @Override
        protected void onPostExecute(String result) {
            if(result != null){

            }
        }
        @Override
        protected void onPreExecute(){

        }
    }


    public String FILEPUT(String url,String json) {
        InputStream inputStream = null;
        String result = "";
        String rValue = "False";
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPut httpPost = new HttpPut(url);
            List<NameValuePair> nameValuePair = null;
               nameValuePair = new ArrayList<NameValuePair>();
               nameValuePair.add(new BasicNameValuePair("JsonFile",json));
                 try {
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));

                } catch (UnsupportedEncodingException e) {
                    dbHandler.CreateLog(new LogError(0, "FILEPUT  FormActivity", "log verileri servere yollanırken oluşan bir hata url den kaynaklı ya da parametrelerden kaynaklanıyo olabilir.", e.getMessage().toString(), currentDateTimeString,sessionUserName,versionName,userId,parentId));
                    e.printStackTrace();
                }

                try {
                   HttpResponse response = httpclient.execute(httpPost);
                    inputStream = response.getEntity().getContent();
                    // write response to log
                    Log.d("Http Post Response:", response.toString());
                } catch (ClientProtocolException e) {
                    dbHandler.CreateLog(new LogError(0, "FILEPUT  FormActivity", "file verileri servere yollandıktan sonra alınan cevapta bir hata oldugundan kaynaklanan hata", e.getMessage().toString(), currentDateTimeString,sessionUserName,versionName,userId,parentId));

                    // Log exception
                    e.printStackTrace();
                } catch (IOException e) {
                    dbHandler.CreateLog(new LogError(0, "FILEPUT  FormActivity", "file verileri servere yollandıktan sonra alınan cevapta bir hata oldugundan kaynaklanan hata", e.getMessage().toString(), currentDateTimeString,sessionUserName,versionName,userId,parentId));

                    // Log exception
                    e.printStackTrace();
                }
                if (inputStream != null) {
                    result = PutConvertInputStreamToString(inputStream);

                    boolean save = false;
                    String id = "";

                    try {
                        if(!result.equals("")){
                            JSONObject jsonObj = new JSONObject(result);
                            save = jsonObj.getBoolean("Save");
                            id= jsonObj.getString("FileId");
                             if(save){
                                 try{
                                     rValue = "True";
                                 }
                                 catch (Exception e){
                                     dbHandler.CreateLog(new LogError(0, "FILEPUT  FormActivity", "file kaydının silinemediğinden hataya düşmüştür.", e.getMessage().toString(), currentDateTimeString,sessionUserName,versionName,userId,parentId));

                                 }
                            }
                            else {
                                rValue = "False";
                            }

                        }

                    } catch (Exception e) {
                        dbHandler.CreateLog(new LogError(0, "FILEPUT  FormActivity", "dönen sonucun json objeye dönüşmemesinden kaynaklanan hata", e.getMessage().toString(), currentDateTimeString,sessionUserName,versionName,userId,parentId));

                        Toast.makeText(getApplicationContext(), R.string.CheckYourInfo, Toast.LENGTH_SHORT).show();
                        Log.d("ReadWeatherJSONFeedTask", e.getLocalizedMessage());
                    }
                } else
                    rValue = "False";


        } catch (Exception e) {
            dbHandler.CreateLog(new LogError(0, "FILEPUT  FormActivity", "", e.getMessage().toString(), currentDateTimeString,sessionUserName,versionName,userId,parentId));
        }
        return rValue;
    }








}


