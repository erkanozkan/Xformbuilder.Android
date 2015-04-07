package com.prg.xformbuilder.xformbuilder;

import android.app.Activity;
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


public class MainActivity extends ActionBarActivity {

    private EditText  username=null;
    private EditText  password=null;
    private Button login;
    String jsonUserName="",jsonCompany="", jsonLastName="", jsonFirstName="", jsonPassword="";
    int jsonParentId=0,jsonUserId=0 ;
    DatabaseHandler dbHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        username = (EditText)findViewById(R.id.editText_userName);
        password = (EditText)findViewById(R.id.editText_password);
        dbHandler = new DatabaseHandler(getApplicationContext());


        
        boolean InternetConnection = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            InternetConnection = true;
        }
        else
            InternetConnection = false;

        login = (Button)findViewById(R.id.button_login);

        login.setOnClickListener(new View.OnClickListener() {

         /*   EditText userEditText = (EditText) findViewById(R.id.editText_password);
            String userName = userEditText.getText().toString();

            EditText passEditText = (EditText) findViewById(R.id.editText_password);
            String password = passEditText.getText().toString();*/

            @Override
            public void onClick(View v) {
                if( username != null && !username.getText().toString().isEmpty() &&  password != null && !password.getText().toString().isEmpty() ) {
                    // call AsynTask to perform network operation on separate thread
                    new HttpAsyncTask().execute("http://developer.xformbuilder.com/api/AppLogin?userName="+ username.getText().toString()+"&password="+password.getText().toString());
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

    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;

    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return GET(urls[0]);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                jsonUserName=jsonObject.getString("UserName").toString();
                jsonFirstName=jsonObject.getString("FirstName").toString();
                jsonLastName=jsonObject.getString("LastName").toString();
                jsonPassword=jsonObject.getString("Password").toString();
                jsonParentId=jsonObject.getInt("ParentId");
                jsonUserId=jsonObject.getInt("UserId");
                jsonCompany=jsonObject.getString("Company").toString();

                final Bundle bundle = new Bundle();

                if (jsonUserName.equals(username.getText().toString()))
                {
                    bundle.putInt("ParentId", jsonParentId);
                    List<User> userss = dbHandler.getAllUserList();
                   // if (dbHandler.AccountLogin("admin","admin")){
                        Toast.makeText(getApplicationContext(), "Giriş Başarılı",Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(MainActivity.this,FormActivity.class);
                        i.putExtras(bundle);
                        startActivity(i);
                  /*}else {
                        User user = new User(0,String.valueOf(jsonUserName),String.valueOf(jsonFirstName),String.valueOf(jsonLastName),String.valueOf(jsonCompany),String.valueOf(jsonPassword),Integer.valueOf(jsonUserId),Integer.valueOf(jsonParentId));
                        dbHandler.CreateUser(user);
                        Toast.makeText(getApplicationContext(), "Giriş Başarılı",Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(MainActivity.this,FormActivity.class);
                        i.putExtras(bundle);
                        startActivity(i);
                  //  }*/


                }else
                {
                    Toast.makeText(getApplicationContext(), "Giriş Başarısız Lütfen tekrar deneyiniz.",Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Lutfen bilgileri kontrol ediniz.",Toast.LENGTH_SHORT).show();
                Log.d("ReadWeatherJSONFeedTask", e.getLocalizedMessage());
            }
        }
    }
//deneme
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
