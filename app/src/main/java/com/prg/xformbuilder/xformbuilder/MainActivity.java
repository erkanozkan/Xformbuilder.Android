package com.prg.xformbuilder.xformbuilder;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.Toast;

import com.onesignal.OneSignal;
import com.onesignal.OneSignal.NotificationOpenedHandler;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class MainActivity extends Activity {

    private EditText  username=null;
    private EditText  password=null;
    private Button login;
    String jsonUserName="",jsonCompany="", jsonLastName="", jsonFirstName="", jsonPassword="";
    int jsonParentId=0,jsonUserId=0 ;
    DatabaseHandler dbHandler;
    boolean InternetConnection = false;
    final Bundle bundle = new Bundle();//Formlar arası veri transferi için kullanıyoruz
    ProgressDialog loginDialog ;

    SharedPreferences preferences; //preferences için bir nesne tanımlıyorum.
    SharedPreferences.Editor editor; //preferences içerisine bilgi girmek için tanımlama


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Pass in your app's Context, Google Project number, your OneSignal App ID, and NotificationOpenedHandler
        OneSignal.init(this, "71156653394", "52ee36a0-e8c3-11e4-b391-0370dbb1438c", new ExampleNotificationOpenedHandler());

        dbHandler = new DatabaseHandler(getApplicationContext());
         preferences= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = preferences.edit();
        username = (EditText)findViewById(R.id.editText_userName);
        password = (EditText)findViewById(R.id.editText_password);
       // dbHandler.ClearLocalDatabase();
        login = (Button)findViewById(R.id.button_login);
        //--------------------------------------Internet Connection
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            InternetConnection = true;
        }
        else
            InternetConnection = false;
        //--------------------------------------Internet Connection


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( username != null && !username.getText().toString().isEmpty() &&  password != null && !password.getText().toString().isEmpty() ) {
                    loginDialog = new ProgressDialog(MainActivity.this, AlertDialog.THEME_HOLO_LIGHT);
                    loginDialog.setTitle("Login Process");
                    loginDialog.setMessage("Please Wait...");
                    loginDialog.setCanceledOnTouchOutside(false);
                    loginDialog.show();
                    if (InternetConnection){
                        //Web Api Cagırıyoruz.
                        new HttpAsyncTask().execute("http://developer.xformbuilder.com/api/AppLogin?userName="+ username.getText().toString()+"&password="+password.getText().toString());



                    }else{
                       User login = dbHandler.AccountLogin(username.getText().toString(),password.getText().toString());
                        if (login!=null){
                            loginDialog.dismiss();
                            //Toast.makeText(getApplicationContext(), "Xformbuilder Hoş geldiniz.",Toast.LENGTH_SHORT).show();
                            bundle.putInt("ParentId", login.getParentId());
                            bundle.putInt("UserId",login.getUserId());


                            editor.putString("UserName",username.getText().toString());    //bilgileri ekle ve kaydet
                            editor.putString("Password", password.getText().toString());
                            editor.putInt("UserId",login.getUserId());
                            editor.putInt("ParentId",login.getParentId());
                            editor.commit();

                            Intent i = new Intent(MainActivity.this,FormActivity.class);
                            i.putExtras(bundle);

                            SendTag(login.getCompany());

                            startActivity(i);


                        }
                        else {
                            loginDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Böyle bir kullanıcı kaydı bulunamadı.",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(), "Lutfen bilgileri kontrol ediniz.",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        OneSignal.onPaused();
    }
    @Override
    protected void onResume() {
        super.onResume();
        OneSignal.onResumed();
    }

    // NotificationOpenedHandler is implemented in its own class instead of adding implements to MainActivity so we don't hold on to a reference of our first activity if it gets recreated.
    private class ExampleNotificationOpenedHandler implements NotificationOpenedHandler {
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
            }

            new AlertDialog.Builder(MainActivity.this,AlertDialog.THEME_HOLO_LIGHT)
                    .setTitle(messageTitle)
                    .setMessage(messageBody)
                    .setCancelable(true)
                    .setPositiveButton("OK", null)
                    .create().show();
        }
    }
    @Override
    public void onBackPressed() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setMessage("Xformbuilder kapatılsın mı?");
        alertDialog
                .setCancelable(false)
                .setPositiveButton("Evet",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {

                                moveTaskToBack(true);
                                finish();

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


    public String GET(String url){

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
            if(inputStream != null) {
                result = convertInputStreamToString(inputStream);
            }
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line ;
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
                JSONObject jsonObject = new JSONObject(result);
                jsonUserName= jsonObject.getString("UserName").toString();
                jsonFirstName=jsonObject.getString("FirstName").toString();
                jsonLastName=jsonObject.getString("LastName").toString();
                jsonPassword=jsonObject.getString("Password").toString();
                jsonParentId=jsonObject.getInt("ParentId");
                jsonUserId=jsonObject.getInt("UserId");
                jsonCompany=jsonObject.getString("Company").toString();

                if (jsonUserName.equals(username.getText().toString()))
                {
                    editor.putString("UserName", jsonUserName);
                    editor.putString("Password", jsonPassword);
                    editor.putInt("UserId",jsonUserId);
                    editor.putInt("ParentId",jsonParentId);
                    editor.commit();

                    boolean  getUser=dbHandler.GetUserByUserId(jsonUserId);
                    if (getUser){
                        User user = new User(0,String.valueOf(jsonUserName),String.valueOf(jsonFirstName),String.valueOf(jsonLastName),String.valueOf(jsonCompany),String.valueOf(jsonPassword),Integer.valueOf(jsonUserId),Integer.valueOf(jsonParentId),"true");
                        dbHandler.UpdateUser(user);
                    }else{
                        User user = new User(0,String.valueOf(jsonUserName),String.valueOf(jsonFirstName),String.valueOf(jsonLastName),String.valueOf(jsonCompany),String.valueOf(jsonPassword),Integer.valueOf(jsonUserId),Integer.valueOf(jsonParentId),"true");
                        dbHandler.CreateUser(user);
                    }
                    editor.putString("UserName",username.getText().toString());    //bilgileri ekle ve kaydet
                    editor.putString("Password", password.getText().toString());
                    editor.putInt("UserId",jsonUserId);
                    editor.putInt("ParentId",jsonParentId);
                    editor.commit();

                    bundle.putInt("ParentId", jsonParentId);
                    bundle.putInt("UserId",jsonUserId);
                    loginDialog.dismiss();
                    Intent i = new Intent(MainActivity.this,FormActivity.class);
                    i.putExtras(bundle);

                    SendTag(jsonCompany);

                    startActivity(i);

                }else
                {
                    loginDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Giriş Başarısız Lütfen tekrar deneyiniz.",Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                loginDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Lutfen bilgileri kontrol ediniz.",Toast.LENGTH_SHORT).show();
                Log.d("ReadWeatherJSONFeedTask", e.getLocalizedMessage());
            }
        }
    }

    private void SendTag(String companyName) {
        OneSignal.sendTag("COMPANY",companyName);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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



}
