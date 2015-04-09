package com.prg.xformbuilder.xformbuilder;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;


public class FormResponseActivity extends ActionBarActivity {
    DatabaseHandler dbHandler;
    String formId="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_response);
        dbHandler = new DatabaseHandler(getApplicationContext());
        Bundle bundle=getIntent().getExtras();
        formId=bundle.getString("FormId");

      Form form=  dbHandler.GetFormByFormId(formId);


        WebView webview = (WebView) findViewById(R.id.webview);
        webview.getSettings().setJavaScriptEnabled(true);


        StringBuilder html = new StringBuilder();
        html.append(form.getMobileHtml());


       webview.loadDataWithBaseURL("file:///android_asset/", html.toString(), "text/html", "utf-8", null);
        // webview.loadData(html.toString(),"text/html",null);
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
}
