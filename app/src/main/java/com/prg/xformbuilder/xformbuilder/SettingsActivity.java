package com.prg.xformbuilder.xformbuilder;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.media.audiofx.BassBoost;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.provider.Contacts;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class SettingsActivity extends Activity {

    DatabaseHandler dbHandler;
    ListView lv;
    int parentId = 0;
    int userId = 0,getParentId=0;
    LinearLayout AboutButton,FaqButton,ContactButton,ClearDatabase;
    CheckBox checkBoxSync;
    User GetUserSync;
    LinearLayout layoutBack;
    ImageButton imgBtnBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_settings);
        overridePendingTransition(R.anim.right_animation, R.anim.out_left_animation);

        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.response_title);
        final Bundle bundle=getIntent().getExtras();
        userId=bundle.getInt("UserId");
        parentId=bundle.getInt("ParentId");
        TextView txtSettings = (TextView)findViewById(R.id.textView_FormName);
        txtSettings.setText("Settings");
        ContactButton=(LinearLayout)findViewById(R.id.LinearLayout_Contact);
        FaqButton=(LinearLayout)findViewById(R.id.LinearLayout_Faq);
        AboutButton=(LinearLayout)findViewById(R.id.LinearLayout_about);
        ClearDatabase=(LinearLayout)findViewById(R.id.LinearLayout_CleanDatabase);
        checkBoxSync=(CheckBox)findViewById(R.id.checkBox_sync);

        layoutBack=(LinearLayout)findViewById(R.id.LinearLayoutBack);

        layoutBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                backProcess();

            }
        });


        imgBtnBack=(ImageButton)findViewById(R.id.imageButton_Back);
        imgBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backProcess();
            }
        });


        dbHandler = new DatabaseHandler(getApplicationContext());
        if(userId != 0 && parentId != 0){
            GetUserSync = dbHandler.GetUserByUserIdForSettings(userId);
            if (GetUserSync != null && GetUserSync.getSync().equals("true")){
                checkBoxSync.setChecked(true);
            }else{
                checkBoxSync.setChecked(false);
            }
        }
        else{
            Toast.makeText(getApplicationContext(), "Oturumunuz dolmuştur tekrar giriş yapın.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(SettingsActivity.this,MainActivity.class);
            startActivity(intent);

        }






        AboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(NetWorkControl()){
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.xformbuilder.com/Company/AboutUs"));
                    startActivity(browserIntent);
                }
                else{
                    Toast.makeText(getApplicationContext(), "Lütfen Internet bağlantınızı kontrol ediniz.", Toast.LENGTH_SHORT).show();
                }

            }
        });
        FaqButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(NetWorkControl()) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.xformbuilder.com/ContactUs/Faq"));
                    startActivity(browserIntent);
                }
            else{
                Toast.makeText(getApplicationContext(), "Lütfen Internet bağlantınızı kontrol ediniz.", Toast.LENGTH_SHORT).show();
            }
            }
        });
        ContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(NetWorkControl()){
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.xformbuilder.com/ContactUs/ContactForm"));
                startActivity(browserIntent);
            }
            else{
                Toast.makeText(getApplicationContext(), "Lütfen Internet bağlantınızı kontrol ediniz.", Toast.LENGTH_SHORT).show();
            }
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
                if(userId != 0){
                    if (checkBoxSync.isChecked()) {
                        dbHandler.SettingSyncUpdate("true",userId);
                    }
                    else
                    {
                        dbHandler.SettingSyncUpdate("false",userId);
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(), "Oturumunuz dolmuştur tekrar giriş yapın.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SettingsActivity.this,MainActivity.class);
                    startActivity(intent);

                }


            }
        });

    }

    @Override
    public void onBackPressed() {
      backProcess();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    //----------------------------------------------Internet Connection Control-------------------------------------------------------------//
    private boolean NetWorkControl(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            return  true;
        } else
            return  false;
    }


    public  void backProcess() {
        if(userId != 0 || parentId != 0){
            final Bundle bundle=getIntent().getExtras();
            Intent goBackFormList = new Intent(SettingsActivity.this,FormActivity.class);
            bundle.putInt("UserId", userId);
            bundle.putInt("ParentId",parentId);
            goBackFormList.putExtras(bundle);
            startActivity(goBackFormList);
            overridePendingTransition(R.anim.right_start_animation, R.anim.left_start_animation);
        }
        else{
            Toast.makeText(getApplicationContext(), "Oturumunuz dolmuştur tekrar giriş yapın.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(SettingsActivity.this,MainActivity.class);
            startActivity(intent);
        }

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
