package com.prg.xformbuilder.xformbuilder;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
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
import java.util.List;


public class FormActivity extends Activity {
    int parentId=0,userId=0;
    boolean InternetConnection = false;
    FormAdaptor adaptor;
    ListView lv;
    String jsonFormTitle="",jsonUserName="", jsonMobileHtml="";
    int jsonParentId=0,jsonFormId=0 ,jsonUserId=0;
    DatabaseHandler dbHandler;
    final Bundle bundleForm = new Bundle();//Formlar arası veri transferi için kullanıyoruz

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
        dbHandler = new DatabaseHandler(getApplicationContext());
        Bundle bundle=getIntent().getExtras();
        parentId=bundle.getInt("ParentId");
        userId=bundle.getInt("UserId");
        lv = (ListView) findViewById(R.id.liste);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectFormId =  ((TextView)view .findViewById(R.id.frmId)).getText().toString();
                String selectFormTitle =  ((TextView)view .findViewById(R.id.formTitle)).getText().toString();
                Toast.makeText(getApplicationContext(), selectFormTitle+" formu açılıyor...", Toast.LENGTH_SHORT).show();
                bundleForm.putString("FormId", selectFormId);
                bundleForm.putInt("FormUserId",userId);
                int count=  dbHandler.getFormCount(selectFormId);

           /* //   dbHandler.DeleteDraftFormTable();
                String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                DraftForm form = new DraftForm(0,4430,"String draft html","json", currentDateTimeString);
                dbHandler.CreateDraftForm(form);
*/
                if (count>=1)
                {
                    Intent i = new Intent(FormActivity.this,DraftFormActivity.class);
                    i.putExtras(bundleForm);
                    startActivity(i);
                }else{
                    Intent i = new Intent(FormActivity.this,FormResponseActivity.class);
                    i.putExtras(bundleForm);
                    startActivity(i);
                }
            }
        });
        try {
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

            //Internet baglantısı var ise web apiden formları cekiyoruz.
            if (InternetConnection){
                new HttpAsyncTask().execute("http://developer.xformbuilder.com/api/AppForm?parentId=" + parentId);



            }else{
              //TODO: Eger internet yoksa veriler veri tabanından çekilecek ve list view ekranına dizilecek.
                try{
                    List<Form> formList=  dbHandler.getAllFormListVw(String.valueOf(parentId));
                    FormList   formArray[] = new FormList[formList.size()];
                    for (int i=0;i<formList.size();i++){
                        formArray[i] = new FormList(formList.get(i).getFormId(), formList.get(i).getFormTitle(), formList.get(i).getUserName(), R.mipmap.ic_launcher);
                    }
                    adaptor = new FormAdaptor(getApplicationContext(), R.layout.line_layout, formArray);
                    lv.setAdapter(adaptor);
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), "Verileri çekerken hata oluştu lütfen daha sonra tekrar deneyiniz.",Toast.LENGTH_SHORT).show();
                    Log.d("ReadWeatherJSONFeedTask", e.getLocalizedMessage());
                }


            }
        }catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Verileri çekerken hata oluştu lütfen daha sonra tekrar deneyiniz.",Toast.LENGTH_SHORT).show();
            Log.d("ReadWeatherJSONFeedTask", e.getLocalizedMessage());
        }
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
                JSONArray jsonArray = new JSONArray(result);
                boolean deleteForm = dbHandler.DeleteFormTable();
                for(int i=0; i<jsonArray.length(); i++){
                    JSONObject obj = jsonArray.getJSONObject(i);
                    jsonFormTitle = obj.getString("FormTitle");
                    jsonFormId = obj.getInt("FormId");
                    jsonParentId = obj.getInt("ParentId");
                    jsonUserName = obj.getString("UserName");
                    jsonMobileHtml = obj.getString("MobileHtml");

                    if (deleteForm){
                        Form form = new Form(0,jsonFormTitle,jsonFormId,jsonParentId,jsonUserName,jsonMobileHtml,userId);
                        dbHandler.CreateForm(form);
                    }
                    else{
                        //TODO:tablolar silinemediyse kontrol edilecek. Login sayfasına geri gönder.
                    }
                }
                try{
                    List<Form> formList=  dbHandler.getAllFormListVw(String.valueOf(parentId));
                    FormList   formArray[] = new FormList[formList.size()];
                    for (int i=0;i<formList.size();i++){
                        formArray[i] = new FormList(formList.get(i).getFormId(), formList.get(i).getFormTitle(), formList.get(i).getUserName(), R.mipmap.ic_launcher);
                    }
                    adaptor = new FormAdaptor(getApplicationContext(), R.layout.line_layout, formArray);
                    lv.setAdapter(adaptor);
                }catch (Exception e){
                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Lütfen daha sonra tekar deneyiniz.",Toast.LENGTH_SHORT).show();
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
