package com.prg.xformbuilder.xformbuilder;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        username = (EditText)findViewById(R.id.editText_userName);
        password = (EditText)findViewById(R.id.editText_password);
        dbHandler = new DatabaseHandler(getApplicationContext());
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
                    loginDialog = new ProgressDialog(MainActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
                    loginDialog.setTitle("Login Process");
                    loginDialog.setMessage("Please Wait...");
                    loginDialog.setCanceledOnTouchOutside(false);
                    loginDialog.show();
                    if (InternetConnection){
                        //Web Api Cagırıyoruz.
                        new HttpAsyncTask().execute("http://developer.xformbuilder.com/api/AppLogin?userName="+ username.getText().toString()+"&password="+password.getText().toString());

                    }else{
                       User  login=dbHandler.AccountLogin(username.getText().toString(),password.getText().toString());
                        if (login!=null){
                            loginDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Xformbuilder Hoş geldiniz.",Toast.LENGTH_SHORT).show();
                            bundle.putInt("ParentId", login.getParentId());
                            Intent i = new Intent(MainActivity.this,FormActivity.class);
                            i.putExtras(bundle);
                            startActivity(i);
                        }
                        else {
                            loginDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Sistemde kayıtlı kullanıcı bulunamadı.",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(), "Lutfen bilgileri kontrol ediniz.",Toast.LENGTH_SHORT).show();
                }
            }
        });
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

             //   final Bundle bundle = new Bundle();//Formlar arası veri transferi için kullanıyoruz

                if (jsonUserName.equals(username.getText().toString()))
                {
                    boolean  getUser=dbHandler.GetUserByUserId(jsonUserId);
                    if (getUser){
                        User user = new User(0,String.valueOf(jsonUserName),String.valueOf(jsonFirstName),String.valueOf(jsonLastName),String.valueOf(jsonCompany),String.valueOf(jsonPassword),Integer.valueOf(jsonUserId),Integer.valueOf(jsonParentId));
                        dbHandler.UpdateUser(user);
                    }else{
                        User user = new User(0,String.valueOf(jsonUserName),String.valueOf(jsonFirstName),String.valueOf(jsonLastName),String.valueOf(jsonCompany),String.valueOf(jsonPassword),Integer.valueOf(jsonUserId),Integer.valueOf(jsonParentId));
                        dbHandler.CreateUser(user);
                    }
                    //List<User> userss = dbHandler.getAllUserList();

                    bundle.putInt("ParentId", jsonParentId);
                    loginDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Xformbuilder hoş geldiniz.",Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(MainActivity.this,FormActivity.class);
                    i.putExtras(bundle);
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
