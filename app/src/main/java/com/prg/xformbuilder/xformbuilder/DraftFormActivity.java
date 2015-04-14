package com.prg.xformbuilder.xformbuilder;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;


public class DraftFormActivity extends ActionBarActivity {
    DatabaseHandler dbHandler;
    String formId="";
     DraftAdapter draftAdapter;
    ListView lv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draft_form);
        dbHandler = new DatabaseHandler(getApplicationContext());
        final Bundle bundleForm = new Bundle();//Formlar aras� veri transferi i�in kullan�yoruz
        Bundle bundle=getIntent().getExtras();
        formId=bundle.getString("FormId");
        lv = (ListView) findViewById(R.id.listView_draftForm);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectFormId =  ((TextView)view .findViewById(R.id.frmId)).getText().toString();
                String selectDraftId =  ((TextView)view .findViewById(R.id.draftId)).getText().toString();
                Toast.makeText(getApplicationContext(), selectDraftId+" formu a��l�yor...", Toast.LENGTH_SHORT).show();
                bundleForm.putString("FormId", selectFormId);
                bundleForm.putString("DraftId", selectDraftId);
                Intent i = new Intent(DraftFormActivity.this,FormResponseActivity.class);
                i.putExtras(bundleForm);
                startActivity(i);

            }
        });

        try{
            List<DraftForm> draftForms=  dbHandler.getAllDraftFormListVw(String.valueOf(formId));
            DraftList   draftArray[] = new DraftList[draftForms.size()];
            for (int i=0;i<draftForms.size();i++){
                draftArray[i] = new DraftList ( R.mipmap.ic_launcher,  draftForms.get(i).getDateDraft(), String.valueOf(draftForms.get(i).getFormId()),String.valueOf(draftForms.get(i).getId()));
            }
            draftAdapter = new DraftAdapter(this.getApplicationContext(), R.layout.draf_line_layout, draftArray);
            lv.setAdapter(draftAdapter);
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "Verileri �ekerken hata olu�tu l�tfen daha sonra tekrar deneyiniz.", Toast.LENGTH_SHORT).show();
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