package com.prg.xformbuilder.xformbuilder;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.Base64;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;


public class ViewFileActivity extends Activity {

    Bundle bundleMain = new Bundle();
    int userId =0, parentId=0;
    String formTitle="",formId="",draftId="";
     LinearLayout layoutBack;
    ImageButton imgBtnBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_view_file);
        overridePendingTransition(R.anim.right_animation, R.anim.out_left_animation);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.response_title);
        TextView txtSettings = (TextView)findViewById(R.id.textView_FormName);
        txtSettings.setText(getString(R.string.ViewerPicture));

        try{
            bundleMain = getIntent().getExtras();
            byte [] imgBytes = bundleMain.getByteArray("base64");
            userId = bundleMain.getInt("UserId");
            parentId=bundleMain.getInt("ParentId");
            formTitle = bundleMain.getString("FormTitle");
            draftId = bundleMain.getString("DraftId");
            formId = bundleMain.getString("FormId");
            InputStream stream = new ByteArrayInputStream(Base64.decode(imgBytes, Base64.DEFAULT));
            Bitmap bmp =  BitmapFactory.decodeStream(stream);
            ImageView img = (ImageView)findViewById(R.id.imageView_viewFile);
            img.setImageBitmap(bmp);
        }
        catch(Exception e){

            if(userId != 0 && parentId != 0 & !formId.equals(""))
            {
                bundleMain.putInt("UserId", userId);
                bundleMain.putInt("ParentId", parentId);
                formTitle = bundleMain.getString("FormTitle");
                draftId = bundleMain.getString("DraftId");
                formId = bundleMain.getString("FormId");
                Toast.makeText(getApplicationContext(),getString(R.string.FileCorrupted), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ViewFileActivity.this,FormResponseActivity.class);
                startActivity(intent);
            }
            else{
                Toast.makeText(getApplicationContext(), R.string.SessionTimeOut, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ViewFileActivity.this,MainActivity.class);
                startActivity(intent);
            }

        }
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


    }

    private void backProcess() {

        if(userId != 0 && parentId != 0)
        {
            bundleMain = getIntent().getExtras();
            bundleMain.putInt("UserId", userId);
            bundleMain.putInt("ParentId", parentId);
            bundleMain.putString("FormTitle", formTitle);
            bundleMain.putString("DraftId", draftId);
            bundleMain.putString("FormId", formId);
            Intent i = new Intent(ViewFileActivity.this,FormResponseActivity.class);
            i.putExtras(bundleMain);
            startActivity(i);
            overridePendingTransition(R.anim.right_start_animation, R.anim.left_start_animation);

        }
        else{
            Toast.makeText(getApplicationContext(), R.string.SessionTimeOut, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ViewFileActivity.this,MainActivity.class);
            startActivity(intent);

        }



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_file, menu);
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
    @Override
    public void onBackPressed() {
        backProcess();
    }
}
