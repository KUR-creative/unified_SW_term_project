package com.example.kouram.activitystudy;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.skp.Tmap.TMapPoint;

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

    private static final String TOURS       = "tours";
    private static final String _ID         = "_id";
    private static final String NAME        = "name";

    private static final String PATHS       = "paths";
    private static final String LATITUDE    = "latitude";
    private static final String LONGITUDE   = "longitude";
    private static final String TOUR_ID     = "tour_id";

    private static final String DESCRIPTIONS= "descriptions";
    private static final String PATH_INDEX  = "path_index";

    private static final String TEXTS       = "texts";
    private static final String STRING      = "string";


    public DBManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
    }

    /*
     * Database가 존재하지 않을 때, 딱 한번 실행됨.
     */
    @Override
    public void onCreate(SQLiteDatabase tmpDB) {
        // sql의 syntax가 틀리면 앱이 죽어버린다.
        String toursSql =
                "CREATE TABLE IF NOT EXISTS " +
                "tours(" +
                "  _id          INTEGER PRIMARY KEY," +
                "  name         TEXT);";
        String pathsSql =
                "CREATE TABLE IF NOT EXISTS " +
                "paths(" +
                "  latitude     REAL," +
                "  longitude    REAL," +
                "  tour_id      INTEGER," +
                "  FOREIGN KEY(tour_id) REFERENCES tours(_id) );";
        String descriptionsSql =
                "CREATE TABLE IF NOT EXISTS " +
                "descriptions(" +
                "  description  TEXT," +
                "  path_index   INTEGER," +
                "  tour_id      INTEGER," +
                "  FOREIGN KEY(tour_id) REFERENCES tours(_id) );";
        String textsSql =
                "CREATE TABLE IF NOT EXISTS " +
                "texts(" +
                "  string       TEXT," +
                "  latitude     REAL," +
                "  longitude    REAL," +
                "  path_id      INTEGER," +
                "  FOREIGN KEY(path_id) REFERENCES paths(_id) );";

        tmpDB.execSQL(toursSql);
        tmpDB.execSQL(pathsSql);
        tmpDB.execSQL(descriptionsSql);
        tmpDB.execSQL(textsSql);
        db = tmpDB;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }

    public void insert(ArrayList<TMapPoint> points){
    }

    public void insert(String str, TMapPoint point){
        ContentValues values = new ContentValues();
        values.put(STRING, str);
        //values.put(LATITUDE, point.getLatitude());
        //values.put(LONGITUDE, point.getLongitude());
        db.insert(TEXTS, null, values);
    }

    public void select(){
        // to test insert(text,point)
        Cursor c = db.query(TEXTS, null, null, null, null, null, null, null);
        while(c.moveToNext()){
            String text = c.getString(c.getColumnIndex(STRING));
            double latitude = c.getDouble(c.getColumnIndex(LATITUDE));
            double longitude = c.getDouble(c.getColumnIndex(LONGITUDE));

            System.out.println(
                      TEXTS + " = " + text
                    + ", " + LATITUDE + " = " + latitude
                    + ", " + LONGITUDE + " = " + longitude);
        }
        c.close();
    }

    public void update(){
    }

    public void delete(){
    }

}

