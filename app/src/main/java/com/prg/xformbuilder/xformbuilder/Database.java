package com.prg.xformbuilder.xformbuilder;

import java.util.ArrayList;
import java.util.HashMap;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
public class Database extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "xformbuilder_sqlite";//database adı

    private static final String TABLE_NAME = "User";
    private static String Id = "Id";
    private static String UserName = "UserName";
    private static String UserId = "UserId";
    private static String FirstName = "FirstName";
    private static String LastName = "LastName";
    private static String Password = "Password";
    private static String ParentId = "ParentId";
    private static String Company = "Company"; 
    private static String DecoderPassword = "DecoderPassword";

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {  // Databesi oluşturuyoruz.Bu methodu biz çağırmıyoruz. Databese de obje oluşturduğumuzda otamatik çağırılıyor.
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + Id + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + UserName + " TEXT,"
                + UserId + " TEXT,"
                + FirstName + " TEXT,"
                + LastName + " TEXT,"
                + DecoderPassword + " TEXT,"
                + Password + " TEXT,"
                + ParentId + " TEXT,"
                + Company + " TEXT" + ")";
        db.execSQL(CREATE_TABLE);
    }

    public void UserDelete(int id){ //id si belli olan row u silmek için

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, Id + " = ?",
                new String[] { String.valueOf(id) });
        db.close();
    }

    public void UserAdd(String pUserName, String pUserId,String pFirstName,String pLastName,String pDecoderPassword,String pPassword,String pParentId,String pCompany) {
        //UserAdd methodu ise adı üstünde Databese veri eklemek için
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(UserName, pUserName);
        values.put(UserId, pUserId);
        values.put(FirstName, pFirstName);
        values.put(LastName, pLastName);
        values.put(DecoderPassword, pDecoderPassword);
        values.put(Password, pPassword);
        values.put(ParentId, pParentId);
        values.put(Company,pCompany);
        db.insert(TABLE_NAME, null, values);
        db.close(); //Database Bağlantısını kapattık*/
    }

    public HashMap<String, String> UserInformation(int id){
        //Databeseden id si belli olan row u çekmek için.
        //Bu methodda sadece tek row değerleri alınır.
        //HashMap bir çift boyutlu arraydir.anahtar-değer ikililerini bir arada tutmak için tasarlanmıştır.
        //map.put("x","300"); mesala burda anahtar x değeri 300.

        HashMap<String,String> user = new HashMap<String,String>();
        String selectQuery = "SELECT * FROM " + TABLE_NAME+ " WHERE id="+id;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if(cursor.getCount() > 0){
            user.put(UserName, cursor.getString(1));
            user.put(UserId, cursor.getString(2));
            user.put(FirstName, cursor.getString(3));
            user.put(LastName, cursor.getString(4));
            user.put(DecoderPassword, cursor.getString(5));
            user.put(Password, cursor.getString(6));
            user.put(ParentId, cursor.getString(7));
            user.put(Company, cursor.getString(8));

        }
        cursor.close();
        db.close();
        // return user
        return user;
    }

    public  ArrayList<HashMap<String, String>> UserList(){

        //Bu methodda ise tablodaki tüm değerleri alıyoruz
        //ArrayList adı üstünde Array lerin listelendiği bir Array.Burda hashmapleri listeleyeceğiz
        //Herbir satırı değer ve value ile hashmap a atıyoruz. Her bir satır 1 tane hashmap arrayı demek.
        //olusturdugumuz tüm hashmapleri ArrayList e atıp geri dönüyoruz(return).

        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<HashMap<String, String>> userList = new ArrayList<HashMap<String, String>>();
        // looping through all rows and adding to list

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                for(int i=0; i<cursor.getColumnCount();i++)
                {
                    map.put(cursor.getColumnName(i), cursor.getString(i));
                }

                userList.add(map);
            } while (cursor.moveToNext());
        }
        db.close();
        // return kitap liste
        return userList;
    }

    public void UserEdit(String pUserName, String pUserId,String pFirstName,String pLastName,String pDecoderPassword,String pPassword,String pParentId,String pCompany,int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        //Bu methodda ise var olan veriyi güncelliyoruz(update)
        ContentValues values = new ContentValues();
        values.put(UserName, pUserName);
        values.put(UserId, pUserId);
        values.put(FirstName, pFirstName);
        values.put(LastName, pLastName);
        values.put(DecoderPassword, pDecoderPassword);
        values.put(Password, pPassword);
        values.put(ParentId, pParentId);
        values.put(Company,pCompany);

        // updating row
        db.update(TABLE_NAME, values, Id + " = ?",
                new String[] { String.valueOf(id) });
    }

    public int getRowCount() {
        // Bu method bu uygulamada kullanılmıyor ama her zaman lazım olabilir.Tablodaki row sayısını geri döner.
        //Login uygulamasında kullanacağız
        String countQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int rowCount = cursor.getCount();
        db.close();
        cursor.close();
        // return row count
        return rowCount;
    }

    public void resetTables(){
        //Bunuda uygulamada kullanmıyoruz. Tüm verileri siler. tabloyu resetler.
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_NAME, null, null);
        db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
        // TODO Auto-generated method stub

    }
}
