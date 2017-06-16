package com.example.kouram.activitystudy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.skp.Tmap.TMapView;

public class Activity2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity2);

        TMapView tmapview = (TMapView)findViewById(R.id.map_view);
        tmapview.setSKPMapApiKey("9553cc22-8104-3088-a882-b90ef2a051d7");
        tmapview.setLanguage(TMapView.LANGUAGE_KOREAN);

        tmapview.setIconVisibility(true);
        tmapview.setZoomLevel(17);
        tmapview.setMapType(TMapView.MAPTYPE_STANDARD);
        tmapview.setCompassMode(false);
        tmapview.setTrackingMode(true);
        tmapview.setSightVisible(true);


    }
}
