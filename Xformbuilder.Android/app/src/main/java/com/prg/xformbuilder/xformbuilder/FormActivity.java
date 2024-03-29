package com.prg.xformbuilder.xformbuilder;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class FormActivity extends Activity {

    int parentId=0;

    String jsonFormTitle="",jsonUserName="", jsonMobileHtml="",jsonModifiedDate="";
    int jsonParentId=0,jsonFormId=0 ;
    DatabaseHandler dbHandler;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
        dbHandler = new DatabaseHandler(getApplicationContext());

        ListView lv;
        lv = (ListView) findViewById(R.id.liste);


        FormList bankaDizi[] = new FormList[]{
                new FormList(1, "Contact", "0 212 212 12 12", R.mipmap.ic_launcher),
                new FormList(2, "User", " 0212 111 11 11", R.mipmap.ic_launcher),
                new FormList(3, "Form List", "0 212 222 22 22",R.mipmap.ic_launcher),
                new FormList(4, "Demos", "0 212 333 33 33", R.mipmap.ic_launcher),
                new FormList(5, "Demoss", "0212 444 44 44", R.mipmap.ic_launcher),
        };


        FormAdaptor adap = new FormAdaptor(this, R.layout.line_layout, bankaDizi);
        lv.setAdapter(adap);




        new HttpAsyncTask().execute("http://developer.xformbuilder.com/api/AppForm?parentId=3358");
        Bundle bundle=getIntent().getExtras();
        parentId=bundle.getInt("ParentId");

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
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            try {
                List<Form> formss=dbHandler.getFormList();
                JSONArray jsonArray = new JSONArray(result);
                for(int i=0; i<jsonArray.length(); i++){
                    JSONObject obj = jsonArray.getJSONObject(i);

                    jsonFormTitle = obj.getString("FormTitle");
                    jsonFormId = obj.getInt("FormId");
                    jsonParentId = obj.getInt("ParentId");
                    jsonUserName = obj.getString("UserName");
                    jsonMobileHtml = obj.getString("MobileHtml");
                    jsonModifiedDate =obj.getString("ModifiedDate");

                //    Form form = new Form(0,jsonFormTitle,jsonFormId,jsonParentId,jsonUserName,jsonMobileHtml,jsonModifiedDate);
                 //   dbHandler.CreateForm(form);
                }


            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Lutfen bilgileri kontrol ediniz.",Toast.LENGTH_SHORT).show();
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
}
