package com.prg.xformbuilder.xformbuilder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;


public class DraftFormActivity extends Activity {
    DatabaseHandler dbHandler;
    String formId="",formTitle="",currentDateTimeString="",versionName="" ,sessionUserName="" ,sessionPassword="";
    int parentId=0,userId=0;
    DraftAdapter draftAdapter;
    ListView lv;
    ImageButton buttonNewResponse,imgBtnBack;
    DraftList   draftArray[] = null;
    List<DraftForm> draftForms = null ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_draft_form);
        overridePendingTransition(R.anim.right_animation, R.anim.out_left_animation);

        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.draftlist_titlebar);
         SharedPreferences preferences;
         preferences= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
          sessionUserName=preferences.getString("UserName", "NULL");
          sessionPassword=preferences.getString("Password", "NULL");

        if (sessionUserName.contains("NULL") && sessionPassword.contains("NULL")){
            Intent i = new Intent(DraftFormActivity.this,MainActivity.class);
            startActivity(i);
        }
       currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        try {

            versionName = getApplicationContext().getPackageManager()
                    .getPackageInfo(getApplicationContext().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        dbHandler = new DatabaseHandler(getApplicationContext());
        final Bundle bundleForm = new Bundle();

        try{
            Bundle bundle=getIntent().getExtras();
            formId=bundle.getString("FormId");
            formTitle=bundle.getString("FormTitle");
            parentId=bundle.getInt("ParentId");
            userId=bundle.getInt("UserId");
        }
        catch (Exception e)
        {
            dbHandler.CreateLog(new LogError(0, "onCreate  DraftFormActivity", "bundleden veriler çekilirken karşılasılan bir hata", e.getMessage().toString(), currentDateTimeString,sessionUserName,versionName,userId,parentId));
        }

        lv = (ListView) findViewById(R.id.listView_draftForm);
        registerForContextMenu(lv);
        buttonNewResponse=(ImageButton) findViewById(R.id.buttonNewResponse);

        LinearLayout backFormList = (LinearLayout)findViewById(R.id.LinearLayout_BackFormList);

        backFormList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              BackPressed();
            }
        });

        imgBtnBack=(ImageButton)findViewById(R.id.imageButtonDraftList_Back);
        imgBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               BackPressed();
            }
        });


       buttonNewResponse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
 try{
     bundleForm.putString("FormId", formId);
     bundleForm.putInt("UserId", userId);
     bundleForm.putInt("ParentId", parentId);
     bundleForm.putString("FormTitle", formTitle);
     Intent i = new Intent(DraftFormActivity.this,FormResponseActivity.class);
     i.putExtras(bundleForm);
     startActivity(i);
     finish();
 }
 catch (Exception e){
     dbHandler.CreateLog(new LogError(0, "onCreate  DraftFormActivity", "bundleden veriler çekilirken karşılasılan bir hata", e.getMessage().toString(), currentDateTimeString,sessionUserName,versionName,userId,parentId));

 }


            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

              try{
                  String selectFormId =  ((TextView)view .findViewById(R.id.frmId)).getText().toString();
                  String selectDraftId =  ((TextView)view .findViewById(R.id.draftId)).getText().toString();
                  //Toast.makeText(getApplicationContext(), " Form  Yükleniyor...", Toast.LENGTH_SHORT).show();
                  bundleForm.putString("FormId", selectFormId);
                  bundleForm.putString("DraftId", selectDraftId);
                  bundleForm.putInt("UserId", userId);
                  bundleForm.putInt("ParentId",parentId);
                  bundleForm.putString("FormTitle", formTitle);
                  Intent i = new Intent(DraftFormActivity.this,FormResponseActivity.class);
                  i.putExtras(bundleForm);
                  startActivity(i);
                  finish();

              }
              catch (Exception e){

                  dbHandler.CreateLog(new LogError(0, "onCreate  DraftFormActivity", "bundleden veriler çekilirken karşılasılan bir hata", e.getMessage().toString(), currentDateTimeString,sessionUserName,versionName,userId,parentId));

              }
            }
        });

        GetDraftList();
    }


    private void GetDraftList(){
        try{
            if(!formId.equals("")){
                try{
                    draftForms =  dbHandler.getAllDraftFormListVw(String.valueOf(formId));
                }
                catch (Exception e){
                    dbHandler.CreateLog(new LogError(0, "onCreate  DraftFormActivity", "forma ait draftlar çekilirken oluşan bir hata", e.getMessage().toString(), currentDateTimeString,sessionUserName,versionName,userId,parentId));
                    }
                draftArray  = new DraftList[draftForms.size()];
                if(draftForms.size() > 0){
                    for (int i=0;i< draftForms.size();i++){
                        draftArray[i] = new DraftList (R.mipmap.appbar_draw_pencil,draftForms.get(i).getDateDraft(), String.valueOf(draftForms.get(i).getFormId()),String.valueOf(draftForms.get(i).getId()));
                    }
                    draftAdapter = new DraftAdapter(this.getApplicationContext(), R.layout.draf_line_layout, draftArray);
                    lv.setAdapter(draftAdapter);
                }
            }
            else{
                if(userId != 0 || parentId != 0){
                    Toast.makeText(getApplicationContext(), getString(R.string.NoDataForms) , Toast.LENGTH_SHORT).show();

                    Bundle bundleReturn = new Bundle();
                    bundleReturn.putInt("UserId", userId);
                    bundleReturn.putInt("ParentId", parentId);
                    Intent i = new Intent(DraftFormActivity.this,FormActivity.class);
                    i.putExtras(bundleReturn);
                    startActivity(i);
                }
                else{

                    Toast.makeText(getApplicationContext(), getString(R.string.SessionTimeOut) , Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(DraftFormActivity.this,MainActivity.class);
                    startActivity(i);
                }

            }
        }
        catch (Exception e){
            dbHandler.CreateLog(new LogError(0, "onCreate  DraftFormActivity", "", e.getMessage().toString(), currentDateTimeString,sessionUserName,versionName,userId,parentId));

            if(userId != 0 || parentId != 0) {
                Toast.makeText(getApplicationContext(), getString(R.string.NoDataForms), Toast.LENGTH_SHORT).show();
                Bundle bundleReturn = new Bundle();
                bundleReturn.putInt("UserId", userId);
                bundleReturn.putInt("ParentId", parentId);
                Intent i = new Intent(DraftFormActivity.this,FormActivity.class);
                i.putExtras(bundleReturn);
                startActivity(i);
            }
            else{

                Toast.makeText(getApplicationContext(), getString(R.string.SessionTimeOut) , Toast.LENGTH_SHORT).show();
                Intent i = new Intent(DraftFormActivity.this,MainActivity.class);
                startActivity(i);
            }

        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
         getMenuInflater().inflate(R.menu.ctx_menu_draftlist,menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        String selectDraftId ="";
        boolean delete =false;

        switch (item.getItemId()){
             case R.id.delete:
                 selectDraftId = ((TextView)info.targetView.findViewById(R.id.draftId)).getText().toString();
                   try{
                     delete =  dbHandler.DeleteDraftFormByDraftId(Integer.parseInt(selectDraftId));
                 }
                 catch (Exception e){
                     delete = false;
                 }
                 if(delete){

                              if(!formId.equals("")){
                                  try{
                                      draftForms =  dbHandler.getAllDraftFormListVw(String.valueOf(formId));
                                  }
                                  catch (Exception e){
                                      dbHandler.CreateLog(new LogError(0, "onCreate  DraftFormActivity", "forma ait draftlar çekilirken oluşan bir hata", e.getMessage().toString(), currentDateTimeString,sessionUserName,versionName,userId,parentId));
                                  }
                                  draftArray  = new DraftList[draftForms.size()];
                                  if(draftForms.size() > 0){
                                      for (int i=0;i< draftForms.size();i++){
                                          draftArray[i] = new DraftList (R.mipmap.appbar_draw_pencil,draftForms.get(i).getDateDraft(), String.valueOf(draftForms.get(i).getFormId()),String.valueOf(draftForms.get(i).getId()));
                                      }
                                      draftAdapter = new DraftAdapter(this.getApplicationContext(), R.layout.draf_line_layout, draftArray);
                                      lv.setAdapter(draftAdapter);
                                  }
                                  else{
                                       if(parentId != 0 || userId != 0){
                                           onBackPressed();
                                       }
                                      else{
                                           Toast.makeText(getApplicationContext(), getString(R.string.SessionTimeOut) , Toast.LENGTH_SHORT).show();
                                           Intent i = new Intent(DraftFormActivity.this,MainActivity.class);
                                           startActivity(i);
                                       }
                                  }
                              }
                  }
                 else{

                 }
                 return true;
             case R.id.cancel:

                 return  true;
             default:
                 return super.onContextItemSelected(item);
         }

    }

    @Override
    public void onBackPressed() {
        BackPressed();
    }

    public void BackPressed() {
        try{
            final Bundle bundleForm = new Bundle();
            Bundle bundle=getIntent().getExtras();
            bundleForm.putString("FormId", formId);
            bundleForm.putInt("UserId", userId);
            bundleForm.putInt("ParentId", parentId);
            bundleForm.putString("FormTitle", formTitle);
            Intent i = new Intent(DraftFormActivity.this,FormActivity.class);
            i.putExtras(bundleForm);
            startActivity(i);
            overridePendingTransition(R.anim.right_start_animation, R.anim.left_start_animation);
            finish();
        }
        catch (Exception e){
            dbHandler.CreateLog(new LogError(0, "onCreate  DraftFormActivity", "bundleden veriler çekilirken oluşan bir hata", e.getMessage().toString(), currentDateTimeString,sessionUserName,versionName,userId,parentId));

        }

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        //----------------------------------------Session Kontrol
        SharedPreferences preferences;     //preferences için bir nesne tanımlıyorum.
        //SharedPreferences.Editor editor;        //preferences içerisine bilgi girmek için tanımlama
        preferences= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        // editor = preferences.edit();

        String sessionUserName=preferences.getString("UserName", "NULL");
        String sessionPassword=preferences.getString("Password", "NULL");

        if (sessionUserName.contains("NULL") && sessionPassword.contains("NULL")){
            Intent i = new Intent(DraftFormActivity.this,MainActivity.class);
            startActivity(i);
        }
//----------------------------------------Session Kontrol
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
