package com.prg.xformbuilder.xformbuilder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.security.PublicKey;
import java.util.ArrayList;
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
            KEY_PASSWORD="password";

    public DatabaseHandler(Context context){
        super(context, DATABASE_NAME,null,DATABASE_VERSION);

    }
    @Override
    public void onCreate(SQLiteDatabase db){
        String sql= ("CREATE TABLE if not exists "+TABLE_USER + "(" +KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_USERNAME + " TEXT,"
                +KEY_FIRSTNAME + " TEXT," +KEY_LASTNAME + " TEXT," +KEY_PASSWORD + " TEXT," +KEY_PARENTID + " TEXT," +KEY_USERID + " TEXT,"+KEY_COMPANY + " TEXT)");
                Log.d("DBHelper", "SQL : " + sql);
                db.execSQL(sql);
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
        values.put(KEY_COMPANY,user.getCompany());

        db.insert(TABLE_USER, null, values);
        db.close();

    }
    public User getUser( int id){
        SQLiteDatabase db =getReadableDatabase();
        Cursor cursor=db.query(TABLE_USER,new String[] {KEY_ID,KEY_USERNAME,KEY_FIRSTNAME,KEY_LASTNAME,KEY_USERID,KEY_PARENTID,KEY_PASSWORD,KEY_COMPANY}, KEY_ID + "=?", new String[] {String.valueOf(id)},null,null,null,null);
        if (cursor !=null) {
            cursor.moveToFirst();
        }
        User user = new User(Integer.parseInt(cursor.getString(0)), cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),Integer.parseInt(cursor.getString(6)),Integer.parseInt(cursor.getString(7)));
        db.close();
        cursor.close();
        return user;


    }

    public  void DeleteUser(User user) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_USER, KEY_ID + "=?", new String[]{String.valueOf(user.getId())});
        db.close();
    }
    public int getUserCount(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM" + TABLE_USER,null);
       int count = cursor.getCount();
        cursor.close();
        db.close();
        return cursor.getCount();

    }
    public boolean AccountLogin(String userName,String password)
    {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USER +" WHERE "+KEY_USERNAME+"='"+userName+"' " ,null);
        cursor.close();
        db.close();
        int count = cursor.getCount();
        if (count>1){
            return true;
        }else{
            return false;
        }
    }

    public int UpdateUser(User user){
        SQLiteDatabase db= getReadableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_USERNAME,user.getUserName());
        values.put(KEY_FIRSTNAME,user.getFirstName());
        values.put(KEY_LASTNAME,user.getLastName());
        values.put(KEY_USERID,user.getUserId());
        values.put(KEY_PARENTID,user.getParentId());
        values.put(KEY_PASSWORD, user.getPassword());
        values.put(KEY_COMPANY, user.getCompany());

        return db.update(TABLE_USER, values, KEY_ID + "=?", new String[]{String.valueOf(user.getId())});
    }
    /*
    public List<User> getAllUsers(){
        List<User> users = new ArrayList<User>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USER, null);
        if (cursor.moveToFirst()){
            do {
                User user = new User(Integer.parseInt(cursor.getString(0)), cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),Integer.parseInt(cursor.getString(6)),Integer.parseInt(cursor.getString(7)));
                users.add(user);

            }
            while (cursor.moveToNext());
        }
        return users;
    }*/
    public List<User> getAllUserList() {
        List<User> userList = new ArrayList<User>();
        SQLiteDatabase db = this.getWritableDatabase();

        // String sqlQuery = "SELECT  * FROM " + TABLE_COUNTRIES;
        // Cursor cursor = db.rawQuery(sqlQuery, null);

        Cursor cursor = db.query(TABLE_USER, new String[]{"id", "userName", "firstName","lastName","company","password","userId","parentId"}, null, null, null, null, null);
        while (cursor.moveToNext()) {
            User user = new User(Integer.parseInt(cursor.getString(0)), cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),Integer.parseInt(cursor.getString(6)),Integer.parseInt(cursor.getString(7)));
            userList.add(user);
        }
        cursor.close();
        db.close();
        return userList;
    }
}
