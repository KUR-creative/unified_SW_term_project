package com.example.kouram.activitystudy;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;

import com.skp.Tmap.TMapPoint;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Created by Ko U Ram on 2017-06-02.
 *
 * DB를 생성하고 관리한다. DB는 반드시 다음처럼 생성하여야 한다.
 *  helper = new DBManager(this, "persons3.db", null, 1);
 *  SQLiteDatabase db = helper.getWritableDatabase();
 *  helper.onCreate(db);
 */

// column = -1은 sql 에러를 의미한다.
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
    private static final String DESCRIPTION = "description";
    private static final String PATH_INDEX  = "path_index";

    private static final String TEXTS       = "texts";
    private static final String STRING      = "string";

    private static final String PICTURES    = "pictures";
    private static final String PICTURE     = "picture";

    public DBManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
        //initTourID();
    }

    /*
    private int tourID; // 현재 insert 가능한 id.
    private int initTourID() {
        System.out.println(getNumOfRowInTours());
        tourID = 0;
        // select해서 최대 _id + 1 한 값이 되어야 함.
        return tourID;
    }
    */

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
        String descriptionsSql =  // for navigation.
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
                "  tour_id      INTEGER," +
                "  FOREIGN KEY(tour_id) REFERENCES tours(_id) );";

        String picturesSql =
                "CREATE TABLE IF NOT EXISTS " +
                "pictures(" +
                "  picture      BLOB," +
                "  latitude     REAL," +
                "  longitude    REAL," +
                "  tour_id      INTEGER," +
                "  FOREIGN KEY(tour_id) REFERENCES tours(_id) );";

        tmpDB.execSQL(toursSql);
        tmpDB.execSQL(pathsSql);
        tmpDB.execSQL(descriptionsSql);
        tmpDB.execSQL(textsSql);
        tmpDB.execSQL(picturesSql);
        db = tmpDB;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }

    public void insert(TourManager.Tour tour){
        int tourID = getNumOfRowInTours();

        // insert tour
        ContentValues values = new ContentValues();
        values.put(_ID, tourID);
        values.put(NAME, tour.name);
        db.insert(TOURS, null, values);

        //System.out.println("-----> tour id = " + tourID);
        insert(tour.path, tourID);

        // insert navigation informations.
        ArrayList< Tuple<Integer,String> >
                naviInfoTuples = tour.naviInfos;
        for(Tuple<Integer,String> naviInfo : naviInfoTuples){
            insert(naviInfo.left, naviInfo.right, tourID);
        }

        // insert pictures.
        ArrayList< Tuple<TMapPoint,Bitmap> >
                linkedPics = tour.linkedPics;
        for(Tuple<TMapPoint,Bitmap> linkedPic : linkedPics){
            insert(linkedPic.left, linkedPic.right, tourID);
        }
    }

    // 나중에 tour를 저장하는 insert에서 호출된다.
    // insert Path
    public void insert(ArrayList<TMapPoint> path, int tour_id){
        for(TMapPoint point : path){
            ContentValues values = new ContentValues();
            values.put(LATITUDE, point.getLatitude());
            values.put(LONGITUDE, point.getLongitude());
            values.put(TOUR_ID, tour_id);
            db.insert(PATHS, null, values);
        }
    }

    public void insert(TMapPoint point, Bitmap bitmap, int tour_id){
        byte[] blob = getBlobDataFrom(bitmap);
        ContentValues values = new ContentValues();
        values.put(PICTURE, blob);
        values.put(LATITUDE, point.getLatitude());
        values.put(LONGITUDE, point.getLongitude());
        values.put(TOUR_ID, tour_id);
        db.insert(PICTURES, null, values);
    }

    private byte[] getBlobDataFrom(Bitmap bitmap) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos;
        try {
            oos = new ObjectOutputStream(bos);
            oos.writeObject(bitmap);
            oos.flush();
            oos.close();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("error in getBlobDataFrom(Bitmap) !");
        }

        return bos.toByteArray();
    }

    public void insert(String string, TMapPoint point){
        ContentValues values = new ContentValues();
        values.put(STRING, string);
        //values.put(LATITUDE, point.getLatitude());
        //values.put(LONGITUDE, point.getLongitude());
        db.insert(TEXTS, null, values);
    }

    public void insert(int path_index, String naviInfoStr, int tour_id){
        ContentValues values = new ContentValues();
        values.put(DESCRIPTION, naviInfoStr);
        values.put(PATH_INDEX, path_index);
        values.put(TOUR_ID, tour_id);
        db.insert(DESCRIPTIONS, null, values);
    }

    // TODO: just for test
    public void select() {
        {
        // to test insert(text,point)
        Cursor c = db.query(TEXTS, null, null, null, null, null, null, null);
        while (c.moveToNext()) {
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

        {
        Cursor c = db.query(PATHS, null, null, null, null, null, null, null);
        while (c.moveToNext()) {
            int tour_id = c.getInt(c.getColumnIndex(TOUR_ID));
            double latitude = c.getDouble(c.getColumnIndex(LATITUDE));
            double longitude = c.getDouble(c.getColumnIndex(LONGITUDE));

            System.out.println(
                    TOUR_ID + " = " + tour_id
                            + ", " + LATITUDE + " = " + latitude
                            + ", " + LONGITUDE + " = " + longitude);
        }
        c.close();
        }

        {
        Cursor c = db.query(DESCRIPTIONS, null, null, null, null, null, null, null);
        while (c.moveToNext()) {
            String description = c.getString(c.getColumnIndex(DESCRIPTION));
            int path_index = c.getInt(c.getColumnIndex(PATH_INDEX));
            int tour_id = c.getInt(c.getColumnIndex(TOUR_ID));

            System.out.println(
                     TOUR_ID + " = " + tour_id
            + ", " + PATH_INDEX + " = " + path_index
            + ", " + DESCRIPTION + " = " + description);
        }
        c.close();
        }

        System.out.println("------------------PICTURES----------------");
        {
        Cursor c = db.query(PICTURES, null, null, null, null, null, null, null);
        while (c.moveToNext()) {
            int tour_id = c.getInt(c.getColumnIndex(TOUR_ID));
            double latitude = c.getDouble(c.getColumnIndex(LATITUDE));
            double longitude = c.getDouble(c.getColumnIndex(LONGITUDE));
            byte[] blob = c.getBlob(c.getColumnIndex(PICTURE));

            System.out.println(
                    TOUR_ID + " = " + tour_id
                            + ", " + LATITUDE + " = " + latitude
                            + ", " + LONGITUDE + " = " + longitude
                            + ", " + PICTURE + " = " + blob);
        }
        c.close();
        }
    }

    public int getNumOfRowInTours(){
        String countQuery = "SELECT  * FROM " + TOURS;
        Cursor cursor = db.rawQuery(countQuery, null);
        int numOfRow = cursor.getCount();
        cursor.close();
        return numOfRow;
    }

    // TODO: 만일 table에 맞는 id가 없다면?
    public ArrayList<TMapPoint> loadPath(int _id) {
        ArrayList<TMapPoint> retPath = new ArrayList<>();

        String[] whereArgs = new String[]{ String.valueOf(_id) };
        String whereClause = TOUR_ID + "=?";
        Cursor c = db.query(PATHS, null, whereClause, whereArgs, null, null, null);
        while(c.moveToNext()){
            double lat = c.getDouble(c.getColumnIndex("latitude"));
            double lon = c.getDouble(c.getColumnIndex("longitude"));
            retPath.add(new TMapPoint(lat,lon));
        }
        c.close();

        return retPath;
    }
}

