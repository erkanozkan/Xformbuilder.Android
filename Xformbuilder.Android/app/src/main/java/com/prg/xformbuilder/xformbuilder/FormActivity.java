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
import java.util.List;


public class FormActivity extends Activity {

int parentId=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
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

                JSONArray jsonArray = new JSONArray(result);
                String[] Forms;


                final String[] array_spinner = new String[jsonArray.length()];
                final ArrayList<String> list = new ArrayList<String>();
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject json_data = jsonArray.getJSONObject(i);
                    String jj=json_data.getString("FormTitle");
                    array_spinner[i] = jj;
                    list.add(jj);
                }
                //(A) adımı
                ListView listemiz=(ListView) findViewById(R.id.listView1);


            //    ListView listView =(listView)findViewById(R.id.list);
                for(int i=0; i<jsonArray.length(); i++){
                    JSONObject obj = jsonArray.getJSONObject(i);

                    String name = obj.getString("FormTitle");
                    String url = obj.getString("FormId");

                    System.out.println(name);
                    System.out.println(url);
                }
                //   JSONObject jsonObject = new JSONObject(result);

              /*  jsonUserName=jsonObject.getString("UserName").toString();
                if (jsonUserName.equals(username.getText().toString()))
                {
                    Toast.makeText(getApplicationContext(), "Giriş Başarılı",Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(MainActivity.this,FormActivity.class);
                    startActivity(i);
                }else
                {
                    Toast.makeText(getApplicationContext(), "Giriş Başarısız",Toast.LENGTH_SHORT).show();
                }
*/
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
