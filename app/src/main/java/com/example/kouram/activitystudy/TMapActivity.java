package com.example.kouram.activitystudy;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.skp.Tmap.TMapMarkerItem;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapPolyLine;
import com.skp.Tmap.TMapView;

public class TMapActivity extends AppCompatActivity {

    private TMapView mapView;
    private Button addMarkerBtn;
    private Button routeBtn;

    private RouteManager routeManager = new RouteManager();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity2);

        mapView = (TMapView)findViewById(R.id.map_view);
        mapView.setSKPMapApiKey("9553cc22-8104-3088-a882-b90ef2a051d7");
        mapView.setLanguage(TMapView.LANGUAGE_KOREAN);

        mapView.setIconVisibility(true);
        mapView.setZoomLevel(17);
        mapView.setMapType(TMapView.MAPTYPE_STANDARD);
        mapView.setCompassMode(false);
        mapView.setTrackingMode(true);
        mapView.setSightVisible(true);

        addMarkerBtn = (Button)findViewById(R.id.add_marker_btn);
        addMarkerBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                System.out.println("add-marker");
                TMapPoint point = mapView.getCenterPoint();
                addMarker(point.getLatitude(), point.getLongitude(), "My Marker");

                try {    // create new route.
                    routeManager.createNewRoute();
                } catch (RuntimeException e) {
                    // if routeManager has current path,
                    // then don't call createNewRoute();
                } finally {
                    routeManager.addPoint(point);
                }
            }
        });

        final TMapActivity context = this;
        routeBtn = (Button)findViewById(R.id.get_route_btn);
        routeBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                System.out.println("get-route");
                routeManager.getAndDisplayPath(context);
            }
        });

        //--------
    }

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
