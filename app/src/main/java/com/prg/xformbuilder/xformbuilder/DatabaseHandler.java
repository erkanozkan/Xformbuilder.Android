package com.prg.xformbuilder.xformbuilder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.security.PublicKey;
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
            KEY_MODIFIEDDATE="modifieddate",
            TABLE_DRAFTFORM="draftform",
            KEY_DRAFHTML="drafthtml",
            KEY_DRAFTJSON="draftjson";

    public DatabaseHandler(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }
    @Override
    public void onCreate(SQLiteDatabase db){
        String sqlUser= ("CREATE TABLE IF NOT EXISTS  "+TABLE_USER + "(" +KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_USERNAME + " TEXT,"
                +KEY_FIRSTNAME + " TEXT," +KEY_LASTNAME + " TEXT," +KEY_PASSWORD + " TEXT," +KEY_PARENTID + " TEXT," +KEY_USERID + " TEXT,"+KEY_COMPANY + " TEXT)");
        Log.d("DBHelper", "SQL : " + sqlUser);
        db.execSQL(sqlUser);

        db.execSQL(sqlUser);
        String sqlForm= ("CREATE TABLE IF NOT EXISTS  "+TABLE_FORM + "(" +KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_FORMTITLE + " TEXT,"
                +KEY_FORMID + " TEXT," +KEY_PARENTID + " TEXT," +KEY_USERNAME + " TEXT," +KEY_MOBILEHTML + " TEXT," +KEY_MODIFIEDDATE + " TEXT NULL)");
        Log.d("DBHelper", "SQL : " + sqlForm);

        String sqlDraftForm= ("CREATE TABLE IF NOT EXISTS  "+TABLE_DRAFTFORM + "(" +KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +KEY_FORMID
                + " TEXT," +KEY_DRAFHTML + " TEXT," +KEY_DRAFTJSON + " TEXT," +KEY_MOBILEHTML + " TEXT)");
        Log.d("DBHelper", "SQL : " + sqlDraftForm);
        db.execSQL(sqlDraftForm);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

        // db.execSQL("ALTER TABLE IF EXISTS " + TABLE_USER);
        onCreate(db);
    }

    public void CreateUser (User user){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USERNAME,user.getUserName());
        values.put(KEY_FIRSTNAME,user.getFirstName());
        values.put(KEY_LASTNAME,user.getLastName());
        values.put(KEY_USERID,user.getUserId());
        values.put(KEY_PARENTID,user.getParentId());
        values.put(KEY_PASSWORD,user.getPassword());
        values.put(KEY_COMPANY, user.getCompany());
        db.insert(TABLE_USER, null, values);


    }

    public void CreateDraftForm (DraftForm draftForm){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_FORMID,draftForm.getFormId());
        values.put(KEY_DRAFHTML,draftForm.getDraftHtml());
        values.put(KEY_DRAFTJSON,draftForm.getDraftJson());
        values.put(KEY_MOBILEHTML,draftForm.getMobileHtml());
        db.insert(TABLE_DRAFTFORM, null, values);
    }

    public void CreateForm (Form form){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_FORMTITLE, form.getFormTitle());
        values.put(KEY_FORMID,form.getFormId());
        values.put(KEY_PARENTID,form.getParentId());
        values.put(KEY_USERNAME,form.getUserName());
        values.put(KEY_MOBILEHTML,form.getMobileHtml());
        values.put(KEY_MODIFIEDDATE, form.getModifiedDate());
        db.insert(TABLE_FORM, null, values);
    }


    public User getUser(int userId){
        SQLiteDatabase db =this.getReadableDatabase();
        Cursor cursor=db.query(TABLE_USER,new String[] {KEY_ID,KEY_USERNAME,KEY_FIRSTNAME,KEY_LASTNAME,KEY_USERID,KEY_PARENTID,KEY_PASSWORD,KEY_COMPANY}, KEY_USERID + "=?", new String[] {String.valueOf(userId)},null,null,null,null);
        if (cursor !=null) {
            cursor.moveToFirst();
        }
        User user = new User(Integer.parseInt(cursor.getString(0)), cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),Integer.parseInt(cursor.getString(6)),Integer.parseInt(cursor.getString(7)));

        cursor.close();
        return user;
    }

    public  void DeleteUser(User user) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_USER, KEY_ID + "=?", new String[]{String.valueOf(user.getId())});
    }

    public boolean DeleteFormTable(){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FORM);
        onCreate(db);
        return true;
    }
    public int getUserCount(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM" + TABLE_USER,null);
        int count = cursor.getCount();
        cursor.close();
        return cursor.getCount();

    }
    public User AccountLogin(String userName,String password)
    {
        SQLiteDatabase db = getReadableDatabase();
        User user=null;
        String sql="SELECT * FROM " + TABLE_USER + " WHERE " + KEY_USERNAME + "='" + userName + "' AND " + KEY_PASSWORD + "='" + password + "'";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor !=null) {
            cursor.moveToFirst();
            user  = new User(
                    cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                    cursor.getString(cursor.getColumnIndex(KEY_USERNAME)),
                    cursor.getString(cursor.getColumnIndex(KEY_FIRSTNAME)),
                    cursor.getString(cursor.getColumnIndex(KEY_LASTNAME)),
                    cursor.getString(cursor.getColumnIndex(KEY_COMPANY)),
                    cursor.getString(cursor.getColumnIndex(KEY_PASSWORD)),
                    cursor.getInt(cursor.getColumnIndex(KEY_USERID)),
                    cursor.getInt(cursor.getColumnIndex(KEY_PARENTID)));

            return user;
        }
        cursor.close();
        return user;
    }

    public void UpdateUser(User user){
        SQLiteDatabase db= getReadableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_USERNAME,user.getUserName());
        values.put(KEY_FIRSTNAME,user.getFirstName());
        values.put(KEY_LASTNAME,user.getLastName());
        values.put(KEY_USERID,user.getUserId());
        values.put(KEY_PARENTID,user.getParentId());
        values.put(KEY_PASSWORD, user.getPassword());
        values.put(KEY_COMPANY, user.getCompany());
        db.update(TABLE_USER, values, KEY_USERID + "=?", new String[]{String.valueOf(user.getUserId())});
    }

    public List<User> getAllUserList() {
        List<User> userList = new ArrayList<User>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USER, new String[]{"id", "userName", "firstName","lastName","company","password","userId","parentId"}, null, null, null, null, null);

        while (cursor.moveToNext()) {
            User user = new User(Integer.parseInt(cursor.getString(0)), cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),Integer.parseInt(cursor.getString(6)),Integer.parseInt(cursor.getString(7)));
            userList.add(user);
        }
        cursor.close();
        return userList;
    }

    public List<Form> getAllFormListVw(String parentId ) {
        List<Form> formList = new ArrayList<Form>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_FORM, new String[]{KEY_ID, KEY_FORMTITLE, KEY_FORMID,KEY_PARENTID,KEY_USERNAME,KEY_MOBILEHTML,KEY_MODIFIEDDATE}, KEY_PARENTID + "=?", new String[] {String.valueOf(parentId)}, null, null, null, null);

        while (cursor.moveToNext()) {
            Form form = new Form(Integer.parseInt(cursor.getString(0)), cursor.getString(1),Integer.parseInt(cursor.getString(2)),Integer.parseInt(cursor.getString(3)),cursor.getString(4),cursor.getString(5),cursor.getString(6));
            formList.add(form);
        }
        cursor.close();
        return formList;
    }
    public List<Form> getFormList(int parentId) {
        List<Form> formList = new ArrayList<Form>();
        SQLiteDatabase db = this.getReadableDatabase();

        String  column[] = new String[]{"id", "userName", "firstName","lastName","company","password","userId","parentId"};
        //  Cursor cursor = db.query(TABLE_FORM, new String[]{"id", "formtitle", "formid","parentid","username","mobilehtml","modifieddate"},KEY_PARENTID + "=?", new String[] {String.valueOf(parentId)}, null, null, null, null);
        String sql="SELECT * FROM " + TABLE_FORM + " WHERE " + KEY_PARENTID + "=?";

        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            Form form = new Form(
                    cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                    cursor.getString(cursor.getColumnIndex(KEY_FORMTITLE)),
                    cursor.getInt(cursor.getColumnIndex(KEY_FORMID)),
                    cursor.getInt(cursor.getColumnIndex(KEY_PARENTID)),
                    cursor.getString(cursor.getColumnIndex(KEY_USERNAME)),
                    cursor.getString(cursor.getColumnIndex(KEY_MOBILEHTML)),
                    cursor.getString(cursor.getColumnIndex(KEY_MODIFIEDDATE)));
            formList.add(form);
        }
        cursor.close();
        return formList;
    }

    public boolean GetUserByUserId(int userId){
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


    public User GetUserByUserIdForSettings(int userId){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USER + " WHERE " + KEY_USERID + "='" + userId + "' ", null);

        User user = null;
        if (cursor != null){
            cursor.moveToFirst();
            user  = new User(
                    cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                    cursor.getString(cursor.getColumnIndex(KEY_USERNAME)),
                    cursor.getString(cursor.getColumnIndex(KEY_FIRSTNAME)),
                    cursor.getString(cursor.getColumnIndex(KEY_LASTNAME)),
                    cursor.getString(cursor.getColumnIndex(KEY_COMPANY)),
                    cursor.getString(cursor.getColumnIndex(KEY_PASSWORD)),
                    cursor.getInt(cursor.getColumnIndex(KEY_USERID)),
                    cursor.getInt(cursor.getColumnIndex(KEY_PARENTID)));

        }
        cursor.close();
        return  user;
    }
    public Form GetFormByFormId(String formId){
        SQLiteDatabase db = getReadableDatabase();
        Form form=null;
        String sql="SELECT * FROM " + TABLE_FORM + " WHERE " + KEY_FORMID + "='" + formId+"'";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor !=null) {
            cursor.moveToFirst();
            form  = new Form(
                    cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                    cursor.getString(cursor.getColumnIndex(KEY_FORMTITLE)),
                    cursor.getInt(cursor.getColumnIndex(KEY_FORMID)),
                    cursor.getInt(cursor.getColumnIndex(KEY_PARENTID)),
                    cursor.getString(cursor.getColumnIndex(KEY_USERNAME)),
                    cursor.getString(cursor.getColumnIndex(KEY_MOBILEHTML)),
                    cursor.getString(cursor.getColumnIndex(KEY_MODIFIEDDATE)));
            return form;
        }
        cursor.close();
        return form;
    }
}
