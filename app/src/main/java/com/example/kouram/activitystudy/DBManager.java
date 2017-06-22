package com.example.kouram.activitystudy;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

/**
 * Created by Ko U Ram on 2017-06-02.
 *
 * DB를 생성하고 관리한다. DB는 반드시 다음처럼 생성하여야 한다.
 *  helper = new DBManager(this, "persons3.db", null, 1);
 *  SQLiteDatabase db = helper.getWritableDatabase();
 *  helper.onCreate(db);
 */

class DBManager extends SQLiteOpenHelper {
    private SQLiteDatabase db;

    public DBManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
    }

    /*
     * Database가 존재하지 않을 때, 딱 한번 실행됨.
     */
    @Override
    public void onCreate(SQLiteDatabase tmpDB) {
        // sql의 syntax가 틀리면 앱이 죽어버린다.
        String sql = "create table if not exists data( _id INTEGER primary key autoincrement, " +
                "name TEXT, age INTEGER, address TEXT)";
        String blobSql = "create table if not exists blobs( _id INTEGER primary key autoincrement," +
                "data BLOB)";
        tmpDB.execSQL(sql);
        tmpDB.execSQL(blobSql);

        db = tmpDB;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "drop table if exists data";
        db.execSQL(sql);

        String blobSql = "drop table if exists blobs";
        db.execSQL(blobSql);
        onCreate(db);
    }

    public void insert(byte[] blobData){
        ContentValues blobValue = new ContentValues();
        blobValue.put("data", blobData);
        db.insert("blobs", null, blobValue);

        ContentValues values = new ContentValues();
        values.put("name", "asdf");
        values.put("age", 26);
        values.put("address", "ButSSan");
        db.insert("data", null, values);

        ContentValues values2 = new ContentValues();
        values2.put("name", "지워");
        values2.put("age", 26);
        values2.put("address", "스울");
        db.insert("data", null, values2);
    }

    public void select(){
        Cursor c = db.query("data", null, null, null, null, null, null, null);
        while(c.moveToNext()){
            int id = c.getInt(c.getColumnIndex("_id"));
            String name = c.getString(c.getColumnIndex("name"));
            int age = c.getInt(c.getColumnIndex("age"));
            String address = c.getString(c.getColumnIndex("address"));

            System.out.println("result: " + "_id = " + id
                    + ", name = " + name
                    + ", age = " + age
                    + ", address = " + address);
        }
        c.close();

        Cursor blobCursor = db.query("blobs", null, null, null, null, null, null, null);
        while(blobCursor.moveToNext()){
            int id = blobCursor.getInt(blobCursor.getColumnIndex("_id"));
            byte[] data = blobCursor.getBlob(blobCursor.getColumnIndex("data"));

            try {
                ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
                ArrayList<Integer> outList = (ArrayList<Integer>)ois.readObject();
                System.out.print("list values: ");
                for(int num : outList){
                    System.out.println(num);
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        blobCursor.close();
    }

    public void update(){
        ContentValues new_values = new ContentValues();
        new_values.put("address", "UPDATED DATA");
        db.update("data", new_values, "name=?", new String[] {"지워"});
    }

    public void delete(){
        System.out.println("call delete click listener");
        db.delete("data", "_id=?", new String[]{"1"}); // _id = 1 인 거 지워짐.
        db.delete("data", "name=?", new String[]{"지워"}); // name = 지워 인 거 전부 지워짐.
    }
}

