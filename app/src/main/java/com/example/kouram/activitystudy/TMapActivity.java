package com.example.kouram.activitystudy;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.skp.Tmap.TMapMarkerItem;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapPolyLine;
import com.skp.Tmap.TMapView;

public class TMapActivity extends AppCompatActivity {

    private TMapView mapView;
    // Create only one manager! it's not singleton!!!
    private RouteManager routeManager = new RouteManager();

    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            //String msg = "New Latitude: " + location.getLatitude() + "New Longitude: " + location.getLongitude();
            //Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();
            //System.out.println("New Latitude: " + location.getLatitude() +
                    //"New Longitude: " + location.getLongitude());
            double lng = location.getLongitude();
            double lat = location.getLatitude();
            //System.out.println("longtitude=" + lng + ", latitude=" + lat);
            mapView.setLocationPoint(lng, lat);
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity2);

        inintTMap();
        initButtons();
    }

    private void inintTMap() {
        mapView = (TMapView)findViewById(R.id.map_view);
        mapView.setSKPMapApiKey("9553cc22-8104-3088-a882-b90ef2a051d7");
        mapView.setLanguage(TMapView.LANGUAGE_KOREAN);

        mapView.setIconVisibility(true);
        mapView.setZoomLevel(17);
        mapView.setMapType(TMapView.MAPTYPE_STANDARD);
        mapView.setCompassMode(true);
        mapView.setTrackingMode(true);
        mapView.setSightVisible(true);
        setLocationManager(mapView);
    }
    private void initButtons() {
        Button addMarkerBtn = (Button) findViewById(R.id.add_marker_btn);
        addMarkerBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                System.out.println("add-marker");
                TMapPoint point = mapView.getCenterPoint();
                addMarker(point.getLatitude(), point.getLongitude(), "My Marker");

                if(! routeManager.hasCurrentWorkingRoute()){
                    routeManager.createNewRoute();
                }
                routeManager.addPoint(point);
            }
        });

        final TMapActivity context = this;
        Button routeBtn = (Button) findViewById(R.id.get_route_btn);
        routeBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                System.out.println("get-route");
                routeManager.getAndDisplayPath(context);
            }
        });

        // 테스트용. 현재 중앙 좌표와 함께 일정 거리에 있는 무작위 좌표 하나를 Route에 추가한다.
        // path를 만들지는 않으니까 보고 싶으면 path 버튼으로 보셈.
        // route와 path를 만들고 discard는 하지 않는다.
        Button randBtn = (Button) findViewById(R.id.rand_btn);
        randBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "it add random Point(on circle) to RouteManager.", Toast.LENGTH_SHORT).show();
                TMapPoint centerPoint = mapView.getCenterPoint();
                TMapPoint randPoint = Tools.getRandPointAtCircle(centerPoint, 1000);

                if(! routeManager.hasCurrentWorkingRoute()){
                    routeManager.createNewRoute();
                }
                routeManager.addPoint(centerPoint);
                routeManager.addPoint(randPoint);
            }
        });
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
            //System.out.println("longtitude=" + lng + ", latitude=" + lat);
            map.setLocationPoint(lng, lat);
        }
    }


    // route와 path를 만들고 discard는 하지 않는다.
    // id는 나중에 체계적으로 관리해야 함. 그래야 지우지.
    private int id = 0;
    private void addMarker(double lat, double lng, String title) {
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
}
