package com.example.kouram.activitystudy;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Ko U Ram on 2017-06-02.
 */

class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
    }

    /*
     * Database가 존재하지 않을 때, 딱 한번 실행됨.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // sql의 syntax가 틀리면 앱이 죽어버린다.
        String sql = "create table if not exists data( _id INTEGER primary key autoincrement, " +
                "name TEXT, age INTEGER, address TEXT)";
        String blobSql = "create table if not exists blobs( _id INTEGER primary key autoincrement," +
                "data BLOB)";
        db.execSQL(sql);
        db.execSQL(blobSql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "drop table if exists data";
        db.execSQL(sql);

        String blobSql = "drop table if exists blobs";
        db.execSQL(blobSql);
        onCreate(db);
    }
}
