package com.example.kouram.activitystudy;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapMarkerItem;
import com.skp.Tmap.TMapPOIItem;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapPolyLine;
import com.skp.Tmap.TMapView;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private TMapView mapView;
    // Create only one manager! it's not singleton!!!
    private DBManager       dbManager   = new DBManager(this, "test09.db", null, 1); // version은 내 맘대로 함.
    private TourManager     tourManager = new TourManager();

    private TextToSpeech tts;

    // map을 위한 정보들 - 없으면 아직 path가 없는 것.
    // path를 그리기 위한 데이터 / navigation을 위한 데이터
    private ArrayList<TMapPoint>                pathOnMap       = null;
    private ArrayList<Tuple<Integer,String>>    navigationInfos = null;

    private final int ttsCallFrequency = 10;
    private int outOfPathCount = 0;
    final MainActivity context = this;
    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            double lon = location.getLongitude();
            double lat = location.getLatitude();
            mapView.setLocationPoint(lon, lat);

            //TODO: 그래서 path가 discard되면 pathOnMap = null로 해야 함.
            if(pathOnMap != null){
                if( isUserInTTSPoint(lat, lon, pathOnMap, navigationInfos) ){
                    // 경로 안내
                    String navigationStr = navigationInfos.get(routeNum).right;
                    if(navigationStr.contains("이동")) {
                        tts.speak(navigationStr, TextToSpeech.QUEUE_FLUSH, null);
                    }
                    navigationText.setText(navigationStr); // TODO: need field test!!!
                    routeNum++;
                }
                // 유저가 경로에서 벗어났음.
                if (isUserOutOfThePath(lat, lon, pathOnMap)) {
                    if( outOfPathCount % ttsCallFrequency == 0 ) {
                        tts.speak("길에서 벗어났습니다. 앱을 켜고 지도를 보십시오.", TextToSpeech.QUEUE_FLUSH, null);
                    }
                    outOfPathCount++;
                }
                // 경로가 끝났음.
                if(routeNum == navigationInfos.size()){
                    tts.speak("길이 끝났습니다.", TextToSpeech.QUEUE_FLUSH, null);
                    // 저장할래? 물어볼수도.
                    pathOnMap = null;
                    navigationInfos = null;
                }
            }else{
                //Toast.makeText(thisContext, "GPS ELSE!!!.", Toast.LENGTH_SHORT).show();
            }
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    private LocationManager locationManager;
    private TextView navigationText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //setContentView(R.layout.activity2);
        setContentView(R.layout.activity_main);

        initDB();
        initTTS();
        initTMap();
        initButtons();

        navigationText = (TextView)findViewById(R.id.navigationText);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        shutDownTTS();
    }

    private void shutDownTTS(){
        if(tts !=null){
            tts.stop();
            tts.shutdown();
        }
    }

    private void initTTS() {
        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.KOREAN);
                }
            }
        });
    }

    private void initDB() {
        SQLiteDatabase db = dbManager.getWritableDatabase();
        dbManager.onCreate(db);
    }


    private void initTMap() {
        mapView = (TMapView)findViewById(R.id.map_view);
        mapView.setSKPMapApiKey("9553cc22-8104-3088-a882-b90ef2a051d7");
        mapView.setLanguage(TMapView.LANGUAGE_KOREAN);

        mapView.setIconVisibility(true);
        mapView.setZoomLevel(17);
        mapView.setMapType(TMapView.MAPTYPE_STANDARD);
        mapView.setCompassMode(false);
        mapView.setTrackingMode(true);
        mapView.setSightVisible(false);

        mapView.setLocationPoint(10,10);
        setLocationManager(mapView);
    }

    double getDistance(ArrayList<TMapPoint> path){
        double distance = 0.0;
        int len = path.size();
        for(int prev = 0,now = 1; now < len; prev++,now++){
            TMapPoint prevPoint = path.get(prev);
            TMapPoint nowPoint  = path.get(now);
            distance += getDistance(prevPoint, nowPoint);
        }
        return distance;
    }

    double getDistance(TMapPoint a, TMapPoint b) {
        float[] result = new float[1];
        Location.distanceBetween(a.getLatitude(), a.getLongitude(),
                b.getLatitude(), b.getLongitude(),
                result);
        return result[0];
    }

    private void initButtons() {
        Button getRouteBtn = (Button)findViewById(R.id.routeMenu);
        getRouteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(MainActivity.this, GetPathActivity.class);
                //startActivityForResult(intent, ACT_TEST);
                Intent intent = new Intent(MainActivity.this, GetPathActivity.class);
                intent.putExtra("lat-lon",
                                new Tuple<Double,Double>(mapView.getLatitude(),
                                                         mapView.getLongitude()));
                startActivityForResult(intent, GET_PATH_NAVI_DATA);
            }
        });

        Button saveTourBtn = (Button)findViewById(R.id.save_tour_btn);
        saveTourBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // tour = current path + nav info + pictures + some text
                if(pathOnMap != null && navigationInfos != null){
                    if(tourManager.hasCurrentWorkingTour()){
                                dbManager.select();
                        tourManager.saveAndDiscardCurrentTour(dbManager);
                                dbManager.select();
                        clearMap();
                        return;
                    }
                }
                Toast.makeText(context, "현재 저장할 수 있는 '여행'이 없습니다.",
                               Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearMap(){
        TMapPolyLine eraser = new TMapPolyLine();
        displayPathOnMap(eraser);
        pathOnMap = null;
        navigationInfos = null;
    }

    private void setLocationManager(TMapView map) {
        // check permission.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            throw new SecurityException("no proper permissions"); // fail
        }
        // set location listener
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                250, 0.3f, locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                250, 0.3f, locationListener);

        // set initial location.
        String locationProvider = LocationManager.GPS_PROVIDER;
        Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
        if (lastKnownLocation != null) {
            double lng = lastKnownLocation.getLongitude();
            double lat = lastKnownLocation.getLatitude();
            System.out.println("-------------------> longtitude=" + lng + ", latitude=" + lat);
            map.setLocationPoint(lng, lat);
        }
    }


    // route와 path를 만들고 discard는 하지 않는다.
    // id는 나중에 체계적으로 관리해야 함. 그래야 지우지.
    private int id = 0;
    public void addMarker(double lat, double lng, String title) {
        TMapMarkerItem item = new TMapMarkerItem();
        TMapPoint point = new TMapPoint(lat, lng);
        item.setTMapPoint(point);

        // just '+' marker!
        Bitmap icon = ((BitmapDrawable) ContextCompat.getDrawable(this, android.R.drawable.ic_input_add)).getBitmap();
        item.setIcon(icon);
        item.setPosition(0.5f, 1);
        item.setCalloutTitle(title);
        item.setCalloutSubTitle("sub " + title);

        mapView.addMarkerItem("m" + id, item);
        id++;
        // 이걸로 삭제 가능. if(id == 2) mapView.removeMarkerItem("m1");

    }

    // radiusLevel = 1  : 300m 내에서 검색,
    // radiusLevel = 33 : 9900m 내에서 검색,
    // radiusLevel = 0  : 서버가 알아서 함.
    // searchCount는 최대 200개까지.
    private void searchPOI(String keyword, final int radiusLevel, final int searchCount) {
        TMapData data = new TMapData();
        if (!TextUtils.isEmpty(keyword)) {
            TMapPoint nowLocation = mapView.getLocationPoint();
            data.findAroundKeywordPOI(nowLocation, keyword, radiusLevel, searchCount, new TMapData.FindAroundKeywordPOIListenerCallback() {
                @Override
                public void onFindAroundKeywordPOI(final ArrayList<TMapPOIItem> arrayList) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mapView.removeAllMarkerItem();

                            for (TMapPOIItem poi : arrayList) {
                                addMarker(poi);
                                System.out.println(poi.name);
                            }

                            if (arrayList.size() > 0) {
                                TMapPOIItem poi = arrayList.get(0);
                            }
                        }
                    });
                }
            });
        }
    }

    //TODO
    private int routeNum = 0;
    private boolean isUserInTTSPoint(double lat, double lon,
                                     ArrayList<TMapPoint> path,
                                     ArrayList<Tuple<Integer,String>> pathTuple){ //routeNum 전역선언
        //Toast.makeText(thisContext, "" + routeNum, Toast.LENGTH_SHORT).show();
        float[] userDistance = new float[1];

        ArrayList<TMapPoint> pathData = path;
        ArrayList<Tuple<Integer,String>> pathNav = pathTuple;

        double savedLat = pathData.get(pathNav.get(routeNum).left).getLatitude();
        double savedLon = pathData.get(pathNav.get(routeNum).left).getLongitude();
        Location.distanceBetween(lat,lon, savedLat, savedLon, userDistance);

        if(userDistance[0] < 15) {
            //System.out.println("user in: "+pathNav.get(routeNum).right);
            addMarker(pathData.get(pathNav.get(routeNum).left).getLatitude(),pathData.get(pathNav.get(routeNum).left).getLongitude(),"complete");
            return true;
        } else {
            //Toast.makeText(thisContext, "ELSE !!!.", Toast.LENGTH_SHORT).show();
            //System.out.println(pathData.get(pathNav.get(routeNum).left));
            //System.out.println("user out ");
            return false;
        }
    }

    int passIndex = 0;
    private boolean isUserOutOfThePath(double lat, double lon, ArrayList<TMapPoint> pathData){
        TMapPoint point = new TMapPoint(lat,lon);

        double savedLat = pathData.get(passIndex+1).getLatitude();
        double savedLon = pathData.get(passIndex+1).getLongitude();
        float[] userDistance = new float[5];

        Location.distanceBetween(lat,lon, savedLat, savedLon, userDistance);

        if(userDistance[0] < 15) {
            if(passIndex < pathData.size()-1)
                //System.out.println("nextLocate");
                ++passIndex;
        }

        if(distancePointToLine(point,pathData.get(passIndex),pathData.get(passIndex+1)) > 15){
            //System.out.println("OUT" + distancePointToLine(point,pathData.get(passIndex),pathData.get(passIndex+1)));
            //addMarker(pathData.get(passIndex).getLatitude(),pathData.get(passIndex).getLongitude(),"out");
            return true;
        } else {
            //tts.speak("user IN", TextToSpeech.QUEUE_FLUSH, null);
            //System.out.println("IN" + distancePointToLine(point,pathData.get(passIndex),pathData.get(passIndex+1)));
            //addMarker(pathData.get(passIndex).getLatitude(),pathData.get(passIndex).getLongitude(),"IN");
            return false;
        }
    }

    private double distancePointToLine(TMapPoint point, TMapPoint lineStart, TMapPoint lineEnd) {
        TMapPoint a; // p 와 p1, p2 를 지나는 직선과의 교점
        double ax;
        double ay;

        double m1;  // l 의 기울기
        double k1;  // l 의 방정식 y = mx + k1 의 상수 k1

        double m2;  // l 과 수직인 직선의 기울기
        double k2;  // l 과 수직인 직선의 방정식 y = mx + k2 의 상수 k2

        // y - yp1 = (yp1-yp2)/(xp1-xp2) * (x-xp1)

        // 선분이 수직일 경우
        if (lineStart.getLatitude() == lineEnd.getLatitude())
        {
            ax=lineStart.getLatitude();
            ay=point.getLongitude();
        }
        // 선분이 수평일 경우
        else if (lineStart.getLongitude() == lineEnd.getLongitude())
        {
            ax = point.getLatitude();
            ay = lineStart.getLongitude();
        }
        // 그 외의 경우
        else
        {
            // 기울기 m1
            m1 = (lineStart.getLongitude() - lineEnd.getLongitude()) / (lineStart.getLatitude() - lineEnd.getLatitude());
            // 상수 k1
            k1 = -m1 * lineStart.getLatitude() + lineStart.getLongitude();

            // 기울기 m2
            m2 = -1.0 / m1;
            // p 를 지나기 때문에 yp = m2 * xp + k2 => k2 = yp - m2 * xp
            k2 = point.getLongitude() - m2 * point.getLatitude();

            // 두 직선 y = m1x + k1, y = m2x + k2 의 교점을 구한다
            ax = (k2 - k1) / (m1 - m2);
            ay = m1 * ax + k1;
        }
        a = new TMapPoint(ax,ay);

        // 구한 점이 선분 위에 있는 지 확인
        if ( ax >= getMin(lineStart.getLatitude(),lineEnd.getLatitude()) && ax <= getMax(lineStart.getLatitude(),lineEnd.getLatitude()) &&
                ay >= getMin(lineStart.getLongitude(),lineEnd.getLongitude()) && ay <= getMax(lineStart.getLongitude(), lineEnd.getLongitude()) )
        // 구한 교점이 선분위에 있으면 p 와 a 와의 거리가 최소 거리임
        {
            return getDistance(point,a);
        }
        // 구한 교점이 선분 위에 없으면 p~p1 또는 p~p2 중 작은 값이 최소 거리임
        else
        {
            return getMin(getDistance(point,lineStart), getDistance(point,lineEnd));
        }
    }

    double getMin(double d1, double d2) {
        if (d1 < d2) {
            return d1;
        } else{
            return d2;
        }
    }

    double getMax(double d1, double d2) {
        if (d1 > d2) {
            return d1;
        } else {
            return d2;
        }
    }

    public void addMarker(TMapPOIItem poi) {
        TMapMarkerItem item = new TMapMarkerItem();
        item.setTMapPoint(poi.getPOIPoint());
        Bitmap icon = ((BitmapDrawable) ContextCompat.getDrawable(this, android.R.drawable.ic_input_add)).getBitmap();
        item.setIcon(icon);
        item.setPosition(0.5f, 1);
        item.setCalloutTitle(poi.getPOIName());
        item.setCalloutSubTitle(poi.getPOIContent());

        Bitmap left = ((BitmapDrawable) ContextCompat.getDrawable(this, android.R.drawable.ic_dialog_alert)).getBitmap();
        item.setCalloutLeftImage(left);

        Bitmap right = ((BitmapDrawable) ContextCompat.getDrawable(this, android.R.drawable.ic_input_get)).getBitmap();
        item.setCalloutRightButtonImage(right);
        item.setCanShowCallout(true);
        mapView.addMarkerItem(poi.getPOIID(), item);
    }

    public void displayPathOnMap(final TMapPolyLine path){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                path.setLineWidth(5);
                path.setLineColor(Color.RED);
                mapView.addTMapPath(path);
            }
        });
    }


    //private static final int ACT_TEST = 123;
    private static final int GET_PATH_NAVI_DATA = 0;
    private static final int PICK_FROM_CAMERA   = 1;
    private static final int CROP_FROM_CAMERA   = 2;

    public static final String PATH_DATA = "path-data";
    public static final String NAVI_DATA = "navi-data";

    private Uri mImageCaptureUri;
    private ImageView photoImageView;
    private Button mButton;

    private void initCamera(){
        mButton = (Button) findViewById(R.id.get_pic_btn);
        photoImageView = (ImageView) findViewById(R.id.image);

        mButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                doTakePhotoAction();
            }
        });
    }
    private void doTakePhotoAction() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // 임시로 사용할 파일의 경로를 생성
        String url = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
        mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), url));

        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
        // 특정기기에서 사진을 저장못하는 문제가 있어 다음을 주석처리 합니다.
        //intent.putExtra("return-data", true);
        startActivityForResult(intent, PICK_FROM_CAMERA);
    }

    Bitmap cropedPhoto = null;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_OK) {
            return;
        }

        if(requestCode == PICK_FROM_CAMERA){
            // 이미지를 가져온 이후의 리사이즈할 이미지 크기를 결정합니다.
            // 이후에 이미지 크롭 어플리케이션을 호출하게 됩니다.

            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setDataAndType(mImageCaptureUri, "image/*");

            intent.putExtra("outputX", 242);
            intent.putExtra("outputY", 242);
            intent.putExtra("aspectX", photoImageView.getX());
            intent.putExtra("aspectY", photoImageView.getY());
            intent.putExtra("scale", true);
            intent.putExtra("return-data", true);
            startActivityForResult(intent, CROP_FROM_CAMERA);
        }
        else if(requestCode == CROP_FROM_CAMERA) {
            final Bundle extras = data.getExtras();

            if (extras != null) {
                cropedPhoto = extras.getParcelable("data");
                photoImageView.setImageBitmap(cropedPhoto);
            }

            // 임시 파일 삭제
            File f = new File(mImageCaptureUri.getPath());
            if (f.exists()) {
                f.delete();
            }
        }
        else if(requestCode == GET_PATH_NAVI_DATA){
            ArrayList<Tuple<Double,Double>> pathData;
            pathData = (ArrayList<Tuple<Double, Double>>)
                       data.getSerializableExtra(PATH_DATA);

            // TODO: 언제 discard할까? 다시 경로를 생성할 때. 혹은 tour를 불러올 때.
            pathOnMap = Tools.convertFrom(pathData);
            displayPathOnMap(Tools.getPathFrom(pathOnMap));

            navigationInfos = (ArrayList<Tuple<Integer, String>>)
                              data.getSerializableExtra(NAVI_DATA);
            tourManager.createNewTour("temp-name", pathOnMap, navigationInfos);
        }
        /*
        else if(requestCode == ACT_TEST){
            String str = data.getStringExtra("test");
            Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
            //"get path intent TEST!!!"
        }
        */
    }
}
