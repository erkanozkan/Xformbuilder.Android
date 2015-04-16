package com.prg.xformbuilder.xformbuilder;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;


public class SettingsActivity extends Activity {

    DatabaseHandler dbHandler;
    ListView lv;
    int parentId = 0;
    int userId = 0;
    LinearLayout AboutButton,FaqButton,ContactButton,ClearDatabase;
    CheckBox checkBoxSync;
    User GetUserSync;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_settings);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.response_title);
        Bundle bundle=getIntent().getExtras();
        userId=bundle.getInt("UserId");
        ContactButton=(LinearLayout)findViewById(R.id.LinearLayout_Contact);
        FaqButton=(LinearLayout)findViewById(R.id.LinearLayout_Faq);
        AboutButton=(LinearLayout)findViewById(R.id.LinearLayout_about);
        ClearDatabase=(LinearLayout)findViewById(R.id.LinearLayout_CleanDatabase);
        checkBoxSync=(CheckBox)findViewById(R.id.checkBox_sync);
        dbHandler = new DatabaseHandler(getApplicationContext());

        GetUserSync = dbHandler.GetUserByUserIdForSettings(userId);
        if (GetUserSync.getSync().equals("true")){
            checkBoxSync.setChecked(true);
        }else{
            checkBoxSync.setChecked(false);
        }

        AboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.xformbuilder.com/Company/AboutUs"));
                startActivity(browserIntent);
            }
        });
        FaqButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.xformbuilder.com/ContactUs/Faq"));
                startActivity(browserIntent);
            }
        });
        ContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.xformbuilder.com/ContactUs/ContactForm"));
                startActivity(browserIntent);
            }
        });

        ClearDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(SettingsActivity.this);
                alertDialog.setMessage("Tüm verilerinizi silmek istediğinize emin misiniz ?");
                alertDialog
                        .setCancelable(false)
                        .setPositiveButton("Evet",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        dbHandler.ClearLocalDatabase();
                                        //----------------------------------------Session Kontrol
                                        SharedPreferences preferences;     //preferences için bir nesne tanımlıyorum.
                                        SharedPreferences.Editor editor;        //preferences içerisine bilgi girmek için tanımlama
                                        preferences= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                        editor = preferences.edit();
                                        editor.remove("UserName");
                                        editor.remove("Password");
                                        editor.remove("UserId");
                                        editor.remove("ParentId");
                                        editor.commit();
                                        //----------------------------------------Session Kontrol
                                        Intent i = new Intent(SettingsActivity.this,MainActivity.class);
                                        startActivity(i);
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
        });
        checkBoxSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBoxSync.isChecked()) {
                   dbHandler.SettingSyncUpdate("true",userId);
                }else {
                    dbHandler.SettingSyncUpdate("false",userId);
                }

            }
        });

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
