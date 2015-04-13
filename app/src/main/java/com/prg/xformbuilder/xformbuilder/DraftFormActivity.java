package com.prg.xformbuilder.xformbuilder;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;


public class DraftFormActivity extends ActionBarActivity {
    DatabaseHandler dbHandler;
    String formId="";
    FormAdaptor adaptor;
    ListView lv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draft_form);
        dbHandler = new DatabaseHandler(getApplicationContext());
        final Bundle bundleForm = new Bundle();//Formlar arasý veri transferi için kullanýyoruz
        Bundle bundle=getIntent().getExtras();
        formId=bundle.getString("FormId");
        lv = (ListView) findViewById(R.id.listView_draftForm);
        try{
            List<DraftForm> draftForms=  dbHandler.getAllDraftFormListVw(String.valueOf(formId));
            FormList   formArray[] = new FormList[draftForms.size()];
            for (int i=0;i<draftForms.size();i++){
                formArray[i] = new FormList(draftForms.get(i).getFormId(), draftForms.get(i).getDateDraft(), draftForms.get(i).getDateDraft(), R.mipmap.ic_launcher);
            }
            adaptor = new FormAdaptor(getApplicationContext(), R.layout.draf_line_layout, formArray);
            lv.setAdapter(adaptor);
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "Verileri çekerken hata oluþtu lütfen daha sonra tekrar deneyiniz.", Toast.LENGTH_SHORT).show();
            Log.d("ReadWeatherJSONFeedTask", e.getLocalizedMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_draft_form, menu);
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
