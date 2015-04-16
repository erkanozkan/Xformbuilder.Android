package com.prg.xformbuilder.xformbuilder;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


public class Settings extends Activity {

    DatabaseHandler dbHandler;
    ListView lv;
    int parentId = 0;
    int userId = 0;
    LinearLayout AboutButton,FaqButton,ContactButton,ClearDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_settings);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.response_title);
        dbHandler = new DatabaseHandler(getApplicationContext());
        AboutButton=(LinearLayout)findViewById(R.id.LinearLayout_about);
        AboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.xformbuilder.com/Company/AboutUs"));
                startActivity(browserIntent);
            }
        });

        FaqButton=(LinearLayout)findViewById(R.id.LinearLayout_Faq);
        FaqButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.xformbuilder.com/ContactUs/Faq"));
                startActivity(browserIntent);
            }
        });
        ContactButton=(LinearLayout)findViewById(R.id.LinearLayout_Contact);
        ContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.xformbuilder.com/ContactUs/ContactForm"));
                startActivity(browserIntent);
            }
        });
        ClearDatabase=(LinearLayout)findViewById(R.id.LinearLayout_CleanDatabase);
        ClearDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(Settings.this);
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
                                        Intent i = new Intent(Settings.this,MainActivity.class);
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
