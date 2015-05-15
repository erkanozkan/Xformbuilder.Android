package com.prg.xformbuilder.xformbuilder;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.security.PublicKey;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Profesor-PC on 6.4.2015.
 */


public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION=1;

    private static final String DATABASE_NAME="xformbuilder",
            TABLE_USER ="user",
            TABLE_SPLASH ="splash",
            TABLE_FILES ="files",
            TABLE_LOG ="log",
            KEY_ID="id",
            KEY_USERNAME="username",
            KEY_FIRSTNAME="firstname",
            KEY_LASTNAME="lastname",
            KEY_USERID="userid",
            KEY_PARENTID="parentid",
            KEY_COMPANY="company",
            KEY_PASSWORD="password",
            TABLE_FORM ="form",
            KEY_FORMTITLE="formtitle",
            KEY_FORMID="formid",
            KEY_MOBILEHTML="mobilehtml",
            TABLE_DRAFTFORM="draftform",
            KEY_DRAFHTML="drafthtml",
            KEY_DRAFTJSON="draftjson",
            KEY_FIELD1_TITLE ="field1title",
            KEY_FIELD2_TITLE ="field2title",
            KEY_FIELD3_TITLE ="field3title",
            KEY_FIELD1_VALUE ="field1value",
            KEY_FIELD2_VALUE ="field2value",
            KEY_FIELD3_VALUE ="field3value",
            KEY_ISUPLOADABLE="isuploadable",
            KEY_ISPUSHNOTIFICATION="pushnotification",
            KEY_SYNC="sync",
            KEY_FORMIMAGE="formimage",
            KEY_DATEDRAFT="datedraft",
            KEY_ISSPLASH="issplash",
            KEY_DESCRIPTION="description",
            KEY_ERRORDATE="errordate",
            KEY_METHODNAME="methodname",
            KEY_VERSION="version",
            KEY_DRAFTID="draftid",
            KEY_ELEMENTID="elementid",
            KEY_PATH="path",
            KEY_ERRORMESSAGE="errormessage";


    public DatabaseHandler(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }
    @Override
    public void onCreate(SQLiteDatabase db){
        String sqlUser= ("CREATE TABLE IF NOT EXISTS  "+TABLE_USER + "(" +KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_USERNAME + " TEXT,"
                +KEY_FIRSTNAME + " TEXT,"
                +KEY_LASTNAME + " TEXT,"
                +KEY_PASSWORD + " TEXT,"
                +KEY_PARENTID + " TEXT,"
                +KEY_USERID + " TEXT,"
                +KEY_ISPUSHNOTIFICATION + " TEXT,"
                +KEY_SYNC + " TEXT,"
                +KEY_COMPANY + " TEXT)");
        Log.d("DBHelper", "SQL : " + sqlUser);
        db.execSQL(sqlUser);


        String sqlForm= ("CREATE TABLE IF NOT EXISTS  "+TABLE_FORM + "(" +KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_FORMTITLE + " TEXT,"
                +KEY_FORMID + " TEXT,"
                +KEY_PARENTID + " TEXT,"
                +KEY_USERNAME + " TEXT,"
                +KEY_MOBILEHTML + " TEXT,"
                +KEY_USERID + " TEXT,"
                +KEY_FORMIMAGE + " TEXT)");
        Log.d("DBHelper", "SQL : " + sqlForm);
        db.execSQL(sqlForm);


        String sqlFıles= ("CREATE TABLE IF NOT EXISTS  "+TABLE_FILES + "(" +KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_DRAFTID + " TEXT,"
                +KEY_FORMID + " TEXT,"
                +KEY_ELEMENTID + " TEXT,"
                +KEY_PATH + " TEXT)");
        Log.d("DBHelper", "SQL : " + sqlFıles);
        db.execSQL(sqlFıles);

        String sqlDraftForm= ("CREATE TABLE IF NOT EXISTS  "+TABLE_DRAFTFORM + "(" +KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                +KEY_FORMID+ " TEXT,"
                +KEY_DRAFHTML+ " TEXT,"
                +KEY_DRAFTJSON +" TEXT,"
                +KEY_DATEDRAFT+  " TEXT,"
                +KEY_FIELD1_TITLE+  " TEXT,"
                +KEY_FIELD2_TITLE+  " TEXT,"
                +KEY_FIELD3_TITLE+  " TEXT,"
                +KEY_FIELD1_VALUE+  " TEXT,"
                +KEY_FIELD2_VALUE+  " TEXT,"
                +KEY_FIELD3_VALUE+  " TEXT,"
                +KEY_ISUPLOADABLE+  " TEXT,"
                +KEY_USERID + " TEXT)" );
        Log.d("DBHelper", "SQL : " + sqlDraftForm);
        db.execSQL(sqlDraftForm);

        String sqlSplash= ("CREATE TABLE IF NOT EXISTS  "+TABLE_SPLASH + "(" +KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_ISSPLASH + " TEXT)");
        Log.d("DBHelper", "SQL : " + sqlSplash);
        db.execSQL(sqlSplash);

       String sqlLog= ("CREATE TABLE IF NOT EXISTS  "+TABLE_LOG + "(" +KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
               +KEY_ERRORDATE + " TEXT,"
               +KEY_DESCRIPTION + " TEXT,"
               +KEY_ERRORMESSAGE + " TEXT,"
               +KEY_USERNAME + " TEXT,"
               +KEY_PARENTID + " TEXT,"
               +KEY_USERID + " TEXT,"
               +KEY_VERSION + " TEXT,"
               +KEY_METHODNAME + " TEXT )");
        Log.d("DBHelper", "SQL : " + sqlLog);
        db.execSQL(sqlLog);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

        onCreate(db);
    }




    public void CreateUser (User user){

        try{
            SQLiteDatabase db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_USERNAME,user.getUserName());
            values.put(KEY_FIRSTNAME,user.getFirstName());
            values.put(KEY_LASTNAME,user.getLastName());
            values.put(KEY_USERID,user.getUserId());
            values.put(KEY_PARENTID,user.getParentId());
            values.put(KEY_PASSWORD,user.getPassword());
            values.put(KEY_COMPANY, user.getCompany());
            values.put(KEY_SYNC,user.getSync());
            values.put(KEY_ISPUSHNOTIFICATION,user.getPush());
            db.insert(TABLE_USER, null, values);
        }
        catch (Exception e){
         }

    }

    public void CreateLog (LogError logError){
        try{
            SQLiteDatabase db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_ERRORMESSAGE, logError.getErrorMessage());
            values.put(KEY_ERRORDATE,logError.getDate());
            values.put(KEY_METHODNAME,logError.getMethodName());
            values.put(KEY_DESCRIPTION, logError.getDescription());
            values.put(KEY_USERNAME, logError.getUserName());
            values.put(KEY_USERID, logError.getUserId());
            values.put(KEY_PARENTID, logError.getParentId());
            values.put(KEY_VERSION, logError.getVersion());
            db.insert(TABLE_LOG, null, values);
        }
        catch (Exception e){

        }

    }


    public void CreateDraftForm (DraftForm draftForm){
        try{
            SQLiteDatabase db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_FORMID,draftForm.getFormId());
            values.put(KEY_DRAFHTML,draftForm.getDraftHtml());
            values.put(KEY_DRAFTJSON,draftForm.getDraftJson());
            values.put(KEY_DATEDRAFT,draftForm.getDateDraft());
            values.put(KEY_FIELD1_TITLE,draftForm.getField1Title());
            values.put(KEY_FIELD2_TITLE,draftForm.getField2Title());
            values.put(KEY_FIELD3_TITLE,draftForm.getField3Title());
            values.put(KEY_FIELD1_VALUE,draftForm.getField1Value());
            values.put(KEY_FIELD2_VALUE,draftForm.getField2Value());
            values.put(KEY_FIELD3_VALUE,draftForm.getField3Value());
            values.put(KEY_ISUPLOADABLE,draftForm.getIsUploadable());

            values.put(KEY_USERID,draftForm.getUserId());

            db.insert(TABLE_DRAFTFORM, null, values);

         }catch (Exception e){
            
        }

    }

    public void CreateForm (Form form){
        try{
            SQLiteDatabase db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_FORMTITLE, form.getFormTitle());
            values.put(KEY_FORMID,form.getFormId());
            values.put(KEY_PARENTID,form.getParentId());
            values.put(KEY_USERNAME,form.getUserName());
            values.put(KEY_MOBILEHTML,form.getMobileHtml());
            values.put(KEY_USERID, form.getUserId());
            values.put(KEY_FORMIMAGE,form.getFormImage());
            db.insert(TABLE_FORM, null, values);
        }
        catch(Exception e){

        }

    }
    public void CreateFiles (Files file){
        try{
            SQLiteDatabase db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_DRAFTID, file.getDraftId());
            values.put(KEY_FORMID,file.getFormId());
            values.put(KEY_PATH,file.getPath());
            values.put(KEY_ELEMENTID,file.getElementId());
            db.insert(TABLE_FILES, null, values);
        }
        catch(Exception e){

        }

    }


    public void UpdateFiles(Files file){
        try
        {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_DRAFTID, file.getDraftId());
            values.put(KEY_FORMID,file.getFormId());
            values.put(KEY_PATH,file.getPath());
            values.put(KEY_ELEMENTID,file.getElementId());
            db.update(TABLE_FILES, values, KEY_ELEMENTID + "=?", new String[]{String.valueOf(file.getElementId())});
        }
        catch (Exception e){

        }

    }

    public boolean CreateSplash (){
        boolean state = false;
        try{

            SQLiteDatabase db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_ISSPLASH, "true");
            db.insert(TABLE_SPLASH, null, values);
            state = true;

        }
        catch (Exception e){

            return  state;
        }
        return state;
    }


    public String getSplashValue(){
        String value = "";
        try{
            SQLiteDatabase db = getReadableDatabase();
             Cursor cursor=db.query(TABLE_SPLASH,new String[] {KEY_ID,KEY_ISSPLASH},null, null,null,null,null,null);


            if (cursor !=null &&  cursor.moveToFirst())
                value =cursor.getString(1);

            cursor.close();

            return  value;

        }
        catch (Exception e){
            return  value;
        }
             }

    public void DeleteSplashValue(){
        try{
            SQLiteDatabase db = getWritableDatabase();
            String sql="DELETE FROM " + TABLE_SPLASH  ;
            db.execSQL(sql);
        }
        catch(Exception e){

        }
    }





    public User getUser(int userId){
        User user = null;
        try{

            SQLiteDatabase db =this.getReadableDatabase();
            Cursor cursor=db.query(TABLE_USER,new String[] {KEY_ID,KEY_USERNAME,KEY_FIRSTNAME,KEY_LASTNAME,KEY_USERID,KEY_PARENTID,KEY_PASSWORD,KEY_COMPANY,KEY_SYNC}, KEY_USERID + "=?", new String[] {String.valueOf(userId)},null,null,null,null);
            if (cursor !=null) {
                cursor.moveToFirst();
            }
              user = new User(Integer.parseInt(cursor.getString(0)),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    Integer.parseInt(cursor.getString(6)),
                    Integer.parseInt(cursor.getString(7)),
                    cursor.getString(8),cursor.getString(9));

            cursor.close();
            return user;

        }
        catch (Exception e){
            return user;
        }

    }

    public  void DeleteUser(User user) {
        try{
            SQLiteDatabase db = getWritableDatabase();
            db.delete(TABLE_USER, KEY_ID + "=?", new String[]{String.valueOf(user.getId())});
        }
        catch (Exception e){

        }

    }


    public String GetLastDraftId(String formId){
        String id=null;

        try{
            SQLiteDatabase db = getReadableDatabase();
            String sql = "SELECT "+KEY_ID+" FROM "+TABLE_DRAFTFORM+ " WHERE "+KEY_FORMID+"='"+formId+"' ORDER BY "+KEY_ID+" DESC LIMIT 1";
            Cursor cursor = db.rawQuery(sql,null);
            if (cursor != null){
                cursor.moveToFirst();
                id = String.valueOf(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
             }
            return id;
 }
        catch (Exception e){
            return id;
        }
    }

    public String GetLastFileId(String draftId){
        String id=null;

        try{
            SQLiteDatabase db = getReadableDatabase();
            String sql = "SELECT "+KEY_ID+" FROM "+TABLE_FILES+ " WHERE "+KEY_DRAFTID+"='"+draftId+"' ORDER BY "+KEY_ID+" DESC LIMIT 1";
            Cursor cursor = db.rawQuery(sql,null);
            if (cursor != null){
                cursor.moveToFirst();
                id = String.valueOf(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
            }
            return id;
        }
        catch (Exception e){
            return id;
        }
    }



    public boolean DeleteFormTable(){
try{
    SQLiteDatabase db = getWritableDatabase();
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_FORM);
    onCreate(db);
    return true;
}
catch (Exception e){
    return  false;
}

    }
    public boolean DeleteFormTableAndFormDraft(){
        try{
            SQLiteDatabase db = getWritableDatabase();
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_FORM);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_DRAFTFORM);
            onCreate(db);
            return true;
        }
        catch (Exception e){
            return false;
        }

    }
    public int getUserCount(){
        int count=0;
        try{
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM" + TABLE_USER,null);
             count = cursor.getCount();
            cursor.close();
            return count;

        }
        catch (Exception e){
            return count;
        }

    }


    public List<LogError> getAllLogList(){
        List<LogError> logErrorList = new ArrayList<LogError>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor  cursor = db.query(TABLE_LOG, new String[]{KEY_ID, KEY_METHODNAME, KEY_DESCRIPTION,KEY_ERRORMESSAGE,KEY_ERRORDATE,KEY_USERNAME,KEY_VERSION,KEY_USERID,KEY_PARENTID},null,null, null, null, null, null);
        while (cursor.moveToNext()) {
            LogError logError = new LogError(
                    Integer.parseInt(cursor.getString(0)),
                     cursor.getString(1),
                     cursor.getString(2),
                     cursor.getString(3) ,
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getString(6),
                    Integer.parseInt(cursor.getString(7)),
                    Integer.parseInt(cursor.getString(8)));
            logErrorList.add(logError);
        }
        cursor.close();
        return  logErrorList;


    }

    public int getFormCount(String formID){
        int count =0;
        try
        {
            SQLiteDatabase db = getReadableDatabase();
            String sql="SELECT * FROM " + TABLE_DRAFTFORM + " WHERE " + KEY_FORMID + "='" + formID+"'";
            Cursor cursor = db.rawQuery(sql, null);
              count = cursor.getCount();
            cursor.close();
            return count;
        }
        catch (Exception e){
            return count;
        }

    }

    public int getFormDraftCount(String formID){
        int count =0;
        try {
            SQLiteDatabase db = getReadableDatabase();
            String sql="SELECT * FROM " + TABLE_DRAFTFORM + " WHERE " + KEY_FORMID + "='" + formID +"' AND isuploadable='1'";
            Cursor cursor = db.rawQuery(sql, null);
           count = cursor.getCount();
            cursor.close();
            return count;
        }
        catch (Exception e){
            return count;

        }

    }


    public int FileControl(String elementId,String draftId){
        int count =0;
        try {
            SQLiteDatabase db = getReadableDatabase();
            String sql="SELECT * FROM " + TABLE_FILES + " WHERE " + KEY_ELEMENTID + "='" + elementId +"' AND "  + KEY_DRAFTID + "='"+draftId+"'" ;
            Cursor cursor = db.rawQuery(sql, null);
            count = cursor.getCount();
            cursor.close();
            return count;
        }
        catch (Exception e){
            return count;

        }

    }

    public int getAllFormDraftCount(){
        int count=0;
        try{
            SQLiteDatabase db = getReadableDatabase();
            String sql="SELECT * FROM " + TABLE_DRAFTFORM + " WHERE  isuploadable='1'";
            Cursor cursor = db.rawQuery(sql, null);
           count = cursor.getCount();
            cursor.close();
            return count;
        }
        catch (Exception e){
            return count;
        }

    }

    public User AccountLogin(String userName,String password)
    {
        User user = null;
        try{
            SQLiteDatabase db = getReadableDatabase();
            String sql="SELECT * FROM " + TABLE_USER + " WHERE " + KEY_USERNAME + "='" + userName + "' AND " + KEY_PASSWORD + "='" + password + "'";
            try{
                Cursor cursor = db.rawQuery(sql, null);
                if (cursor !=null &&  cursor.moveToFirst()) {
                    user  = new User(
                            cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                            cursor.getString(cursor.getColumnIndex(KEY_USERNAME)),
                            cursor.getString(cursor.getColumnIndex(KEY_FIRSTNAME)),
                            cursor.getString(cursor.getColumnIndex(KEY_LASTNAME)),
                            cursor.getString(cursor.getColumnIndex(KEY_COMPANY)),
                            cursor.getString(cursor.getColumnIndex(KEY_PASSWORD)),
                            cursor.getInt(cursor.getColumnIndex(KEY_USERID)),
                            cursor.getInt(cursor.getColumnIndex(KEY_PARENTID)),
                            cursor.getString(cursor.getColumnIndex(KEY_SYNC)),cursor.getString(cursor.getColumnIndex(KEY_ISPUSHNOTIFICATION)));
                    return user;
                }
                cursor.close();
            }
            catch (Exception e){
                 return user;
            }
        }
        catch (Exception e){
             return user;
        }
        return user;

    }

    public void UpdateUser(User user){
        try
        {
            SQLiteDatabase db= getReadableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_USERNAME,user.getUserName());
            values.put(KEY_FIRSTNAME,user.getFirstName());
            values.put(KEY_LASTNAME,user.getLastName());
            values.put(KEY_USERID,user.getUserId());
            values.put(KEY_PARENTID,user.getParentId());
            values.put(KEY_PASSWORD, user.getPassword());
            values.put(KEY_ISPUSHNOTIFICATION, user.getPush());
            values.put(KEY_COMPANY, user.getCompany());
            db.update(TABLE_USER, values, KEY_USERID + "=?", new String[]{String.valueOf(user.getUserId())});
        }
        catch (Exception e){

        }

    }

    public void SettingSyncUpdate(String sync,int userId){
        try
        {
            SQLiteDatabase db= getReadableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_SYNC,sync);
            db.update(TABLE_USER, values, KEY_USERID + "=?", new String[]{String.valueOf(userId)});

        }
        catch (Exception e){

        }
            }
    public void SettingPushNotificationUpdate(String push,int userId){
        try {
            SQLiteDatabase db= getReadableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_ISPUSHNOTIFICATION,push);
            db.update(TABLE_USER, values, KEY_USERID + "=?", new String[]{String.valueOf(userId)});
        }
        catch (Exception e){

        }

    }




    public List<Form> getAllFormListVw(String parentId ) {
        try{
            List<Form> formList = new ArrayList<Form>();
            SQLiteDatabase db = this.getReadableDatabase();
try{
    Cursor cursor = db.query(TABLE_FORM, new String[]{KEY_ID, KEY_FORMTITLE, KEY_FORMID,KEY_PARENTID,KEY_USERNAME,KEY_MOBILEHTML,KEY_USERID,KEY_FORMIMAGE}, KEY_PARENTID + "=?", new String[] {String.valueOf(parentId)}, null, null, null, null);

    while (cursor.moveToNext()) {
        Form form = new Form(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1),
                Integer.parseInt(cursor.getString(2)),
                Integer.parseInt(cursor.getString(3)),
                cursor.getString(4),
                cursor.getString(5),
                Integer.parseInt(cursor.getString(6)),
                cursor.getString(7));
        formList.add(form);
       }
         cursor.close();

       }
       catch (Exception e){

           }
            return formList;
        }
        catch (Exception e){

            List<Form> formList = new ArrayList<Form>();
            return formList;
        }

    }

    public boolean GetUserByUserId(int userId){
        try{
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USER + " WHERE " + KEY_USERID + "='" + userId + "' ", null);
            int count = cursor.getCount();
            cursor.close();
            if (count>=1){
                return true;
            }else{
                return false;
            }
        }
        catch (Exception e){

            return false;
        }

    }
    public User GetUserByUserIdForSettings(int userId){
        User user = null;

        try{
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USER + " WHERE " + KEY_USERID + "='" + userId + "' ", null);

            if (cursor != null && cursor.moveToFirst()){
           /* cursor.moveToFirst();*/
                user  = new User(
                        cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                        cursor.getString(cursor.getColumnIndex(KEY_USERNAME)),
                        cursor.getString(cursor.getColumnIndex(KEY_FIRSTNAME)),
                        cursor.getString(cursor.getColumnIndex(KEY_LASTNAME)),
                        cursor.getString(cursor.getColumnIndex(KEY_COMPANY)),
                        cursor.getString(cursor.getColumnIndex(KEY_PASSWORD)),
                        cursor.getInt(cursor.getColumnIndex(KEY_USERID)),
                        cursor.getInt(cursor.getColumnIndex(KEY_PARENTID)),
                        cursor.getString(cursor.getColumnIndex(KEY_SYNC)),cursor.getString(cursor.getColumnIndex(KEY_ISPUSHNOTIFICATION)));

            }
            cursor.close();
            return  user;
        }
        catch (Exception e){

            return  user;

        }

    }
    public Form GetFormByFormId(String formId){
        Form form=null;
        try{
            SQLiteDatabase db = getReadableDatabase();

            String sql="SELECT * FROM " + TABLE_FORM + " WHERE " + KEY_FORMID + "='" + formId+"'";
            Cursor cursor = db.rawQuery(sql, null);
            if (cursor !=null && cursor.moveToFirst()) {
            /*cursor.moveToFirst();*/
                form  = new Form(
                        cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                        cursor.getString(cursor.getColumnIndex(KEY_FORMTITLE)),
                        cursor.getInt(cursor.getColumnIndex(KEY_FORMID)),
                        cursor.getInt(cursor.getColumnIndex(KEY_PARENTID)),
                        cursor.getString(cursor.getColumnIndex(KEY_USERNAME)),
                        cursor.getString(cursor.getColumnIndex(KEY_MOBILEHTML)),
                        cursor.getInt(cursor.getColumnIndex(KEY_USERID)),
                        cursor.getString(cursor.getColumnIndex(KEY_FORMIMAGE)));
                return form;
            }
            cursor.close();
            return form;
        }
        catch (Exception e){

            return form;

        }
    }


    public DraftForm GetDraftByDraftId(String draftId){
        DraftForm  draft=null;

        try {
            SQLiteDatabase db = getReadableDatabase();
            String sql="SELECT * FROM " + TABLE_DRAFTFORM + " WHERE " + KEY_ID + "='" + draftId+"'";
            Cursor cursor = db.rawQuery(sql, null);
            if (cursor !=null && cursor.moveToFirst()) {
            /*cursor.moveToFirst();*/
                draft  = new DraftForm(
                        cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                        cursor.getInt(cursor.getColumnIndex(KEY_FORMID)),
                        cursor.getString(cursor.getColumnIndex(KEY_DRAFHTML)),
                        cursor.getString(cursor.getColumnIndex(KEY_DRAFTJSON)),
                        cursor.getString(cursor.getColumnIndex(KEY_DATEDRAFT)),
                        cursor.getInt(cursor.getColumnIndex(KEY_USERID)),
                        cursor.getString(cursor.getColumnIndex(KEY_FIELD1_TITLE)),
                        cursor.getString(cursor.getColumnIndex(KEY_FIELD2_TITLE)),
                        cursor.getString(cursor.getColumnIndex(KEY_FIELD3_TITLE)),
                        cursor.getString(cursor.getColumnIndex(KEY_FIELD1_VALUE)),
                        cursor.getString(cursor.getColumnIndex(KEY_FIELD2_VALUE)),
                        cursor.getString(cursor.getColumnIndex(KEY_FIELD3_VALUE)),
                        cursor.getString(cursor.getColumnIndex(KEY_ISUPLOADABLE))
                );
                return draft;
            }
            cursor.close();
            return draft;
        }
        catch (Exception e){

            return draft;
        }

    }



    public Files GetFilesByDraftId(String draftId){
        Files  files=null;

        try {
            SQLiteDatabase db = getReadableDatabase();
            String sql="SELECT * FROM " + TABLE_FILES + " WHERE " + KEY_ID + "='" + draftId+"'";
            Cursor cursor = db.rawQuery(sql, null);
            if (cursor !=null && cursor.moveToFirst()) {
            /*cursor.moveToFirst();*/
                files  = new Files(
                        cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                        cursor.getString(cursor.getColumnIndex(KEY_FORMID)),
                        cursor.getString(cursor.getColumnIndex(KEY_ELEMENTID)),
                        cursor.getString(cursor.getColumnIndex(KEY_PATH)),
                        cursor.getString(cursor.getColumnIndex(KEY_DRAFTID)));
                return files;
            }
            cursor.close();
            return files;
        }
        catch (Exception e){
            return files;
        }
    }


    public List<Files> GetFilesListByDraftId(String draftId) {
           try{
            List<Files> files = new ArrayList<Files>();
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.query(TABLE_FILES, new String[]{KEY_ID
                    , KEY_FORMID
                    , KEY_ELEMENTID
                    ,KEY_PATH ,KEY_DRAFTID}, KEY_DRAFTID + "=?", new String[] {draftId}, null, null, null, null);

            while (cursor.moveToNext()) {
                Files file = new Files(
                        Integer.parseInt(cursor.getString(0)),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4));
                files.add(file);
            }
            cursor.close();
            return files;
        }
        catch (Exception e){
            List<Files> files = new ArrayList<Files>();
            return files;
        }



    }

    public void UpdateDraft(DraftForm draftForm){
        try {
            SQLiteDatabase db= getReadableDatabase();
            ContentValues values = new ContentValues();

            values.put(KEY_DRAFHTML,draftForm.getDraftHtml());
            values.put(KEY_DRAFTJSON,draftForm.getDraftJson());
            values.put(KEY_DATEDRAFT,draftForm.getDateDraft());
            values.put(KEY_FIELD1_TITLE,draftForm.getField1Title());
            values.put(KEY_FIELD2_TITLE,draftForm.getField2Title());
            values.put(KEY_FIELD3_TITLE,draftForm.getField3Title());
            values.put(KEY_FIELD1_VALUE,draftForm.getField1Value());
            values.put(KEY_FIELD2_VALUE,draftForm.getField2Value());
            values.put(KEY_FIELD3_VALUE,draftForm.getField3Value());
            values.put(KEY_ISUPLOADABLE,draftForm.getIsUploadable());

            db.update(TABLE_DRAFTFORM, values, KEY_ID + "=?", new String[]{String.valueOf(draftForm.getId())});
        }
        catch (Exception e){
        }
    }


    public void UpdateFiles(DraftForm draftForm){
        try {
            SQLiteDatabase db= getReadableDatabase();
            ContentValues values = new ContentValues();

            values.put(KEY_DRAFHTML,draftForm.getDraftHtml());
            values.put(KEY_DRAFTJSON,draftForm.getDraftJson());
            values.put(KEY_DATEDRAFT,draftForm.getDateDraft());
            values.put(KEY_FIELD1_TITLE,draftForm.getField1Title());
            values.put(KEY_FIELD2_TITLE,draftForm.getField2Title());
            values.put(KEY_FIELD3_TITLE,draftForm.getField3Title());
            values.put(KEY_FIELD1_VALUE,draftForm.getField1Value());
            values.put(KEY_FIELD2_VALUE,draftForm.getField2Value());
            values.put(KEY_FIELD3_VALUE,draftForm.getField3Value());
            values.put(KEY_ISUPLOADABLE,draftForm.getIsUploadable());

            db.update(TABLE_DRAFTFORM, values, KEY_ID + "=?", new String[]{String.valueOf(draftForm.getId())});
        }
        catch (Exception e){
        }
    }




    public List<DraftForm> getAllDraftFormListVw(String formId ) {


        try{
            List<DraftForm> draftForms = new ArrayList<DraftForm>();
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.query(TABLE_DRAFTFORM, new String[]{KEY_ID
                    , KEY_FORMID
                    , KEY_DRAFHTML
                    ,KEY_DRAFTJSON
                    ,KEY_DATEDRAFT
                    ,KEY_USERID
                    ,KEY_FIELD1_TITLE
                    ,KEY_FIELD2_TITLE
                    ,KEY_FIELD3_TITLE
                    ,KEY_FIELD1_VALUE
                    ,KEY_FIELD2_VALUE
                    ,KEY_FIELD3_VALUE
                    ,KEY_ISUPLOADABLE}, KEY_FORMID + "=?", new String[] {String.valueOf(formId)}, null, null, null, null);

            while (cursor.moveToNext()) {
                DraftForm form = new DraftForm(
                        Integer.parseInt(cursor.getString(0)),
                        Integer.parseInt(cursor.getString(1)),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        Integer.parseInt(cursor.getString(5)),
                        cursor.getString(6),
                        cursor.getString(7),
                        cursor.getString(8),
                        cursor.getString(9),
                        cursor.getString(10),
                        cursor.getString(11),
                        cursor.getString(12)

                );
                draftForms.add(form);
            }
            cursor.close();
            return draftForms;
        }
        catch (Exception e){
             List<DraftForm> draftForms = new ArrayList<DraftForm>();
            return draftForms;
        }



    }

    public List<DraftForm> getAllIsUploadDraftFormListByFormId(String formId ) {

      try{
          List<DraftForm> draftForms = new ArrayList<DraftForm>();
          SQLiteDatabase db = this.getReadableDatabase();
          Cursor cursor = db.query(TABLE_DRAFTFORM, new String[]{KEY_ID
                  , KEY_FORMID
                  , KEY_DRAFHTML
                  ,KEY_DRAFTJSON
                  ,KEY_DATEDRAFT
                  ,KEY_USERID
                  ,KEY_FIELD1_TITLE
                  ,KEY_FIELD2_TITLE
                  ,KEY_FIELD3_TITLE
                  ,KEY_FIELD1_VALUE
                  ,KEY_FIELD2_VALUE
                  ,KEY_FIELD3_VALUE
                  ,KEY_ISUPLOADABLE}, KEY_FORMID + "=? AND "+KEY_ISUPLOADABLE+"=?" , new String[] {String.valueOf(formId),"1"},null, null, null, null);

          while (cursor.moveToNext()) {
              DraftForm form = new DraftForm(
                      Integer.parseInt(cursor.getString(0)),
                      Integer.parseInt(cursor.getString(1)),
                      cursor.getString(2),
                      cursor.getString(3),
                      cursor.getString(4),
                      Integer.parseInt(cursor.getString(5)),
                      cursor.getString(6),
                      cursor.getString(7),
                      cursor.getString(8),
                      cursor.getString(9),
                      cursor.getString(10),
                      cursor.getString(11),
                      cursor.getString(12)

              );
              draftForms.add(form);
          }
          cursor.close();
          return draftForms;
      }
      catch (Exception e){
           List<DraftForm> draftForms = new ArrayList<DraftForm>();
          return draftForms;

      }


    }


    //Tum Database Siler
    public boolean ClearLocalDatabase(){
try{
    SQLiteDatabase db = getWritableDatabase();
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_DRAFTFORM);
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_FORM);
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_SPLASH);
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_FILES);
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOG);
    onCreate(db);
    return true;
}
catch (Exception e){
     return false;

}


    }

    //Draft Form Database Siler
    public boolean DeleteDraftFormByDraftId(int draftId){
     try    {

        SQLiteDatabase db = getWritableDatabase();
        String sql="DELETE FROM " + TABLE_DRAFTFORM + " WHERE " + KEY_ID + "=" + draftId;
        db.execSQL(sql);
        return true;
       }
         catch ( Exception e) {
             return false;
         }
    }

    //Draft Form Database Siler
    public boolean DeleteFilesByDraftId(int id){
        try    {

            SQLiteDatabase db = getWritableDatabase();
            String sql="DELETE FROM " + TABLE_FILES + " WHERE " + KEY_ID + "=" + id;
            db.execSQL(sql);
            return true;
        }
        catch ( Exception e) {
            return false;
        }
    }


    public boolean DeleteLogById(int Id){
        try    {

            SQLiteDatabase db = getWritableDatabase();
            String sql="DELETE FROM " + TABLE_LOG + " WHERE " + KEY_ID + "=" + Id;
            db.execSQL(sql);
            return true;
        }
        catch ( Exception e) {
            return false;
        }
    }
}
