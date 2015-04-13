package com.prg.xformbuilder.xformbuilder;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;


public class Settings extends ActionBarActivity {

    DatabaseHandler dbHandler;
    ListView lv;
    int parentId = 0;
    int userId = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        dbHandler = new DatabaseHandler(getApplicationContext());
        Bundle bundle=getIntent().getExtras();
        parentId=bundle.getInt("ParentId");
        userId = bundle.getInt("UserId");
        lv = (ListView) findViewById(R.id.listView_settings);
/*
        User user = dbHandler.GetUserByUserIdForSettings(userId);
        String[] rowItem = {user.getUserName().toString(),user.getPassword().toString()};
        ListAdapter listAdapter  = new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,rowItem);
        lv.setAdapter(listAdapter);
*/
        SettingsAdaptorSection adapter = new SettingsAdaptorSection(this, generateData());
        lv.setAdapter(adapter);


    }


    private ArrayList<SettingsModel> generateData(){
        ArrayList<SettingsModel> models = new ArrayList<SettingsModel>();
        models.add(new SettingsModel("CONNECTİVİTY"));
        models.add(new SettingsModel("Username"));
        models.add(new SettingsModel("Password"));
        models.add(new SettingsModel("ABOUT"));
        models.add(new SettingsModel("About Xformbuilder"));
        models.add(new SettingsModel("Version:1.0"));
        models.add(new SettingsModel("FAQ"));
        models.add(new SettingsModel("Community Support"));
        models.add(new SettingsModel("MAİNTENANCE"));
        models.add(new SettingsModel("Clean Local Database"));
        models.add(new SettingsModel("ADVANCED"));
        models.add(new SettingsModel("Advanced Settings"));
        return models;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
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
