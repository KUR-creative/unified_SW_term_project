package com.example.kouram.activitystudy;

import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapPolyLine;
import com.skp.Tmap.TMapView;

import java.util.ArrayList;

/**
 * Created by Ko U Ram on 2017-06-16.
 */

public class RouteManager {
    private boolean hasCurrentRoute = false;
    private ArrayList<TMapPoint> route = new ArrayList<>();

    public void createNewRoute() {
        if(hasCurrentRoute) {
            throw new RuntimeException("createNewRoute cannot be duplicated in same time!" +
                                    "you need to end job for current path.");
        }
        hasCurrentRoute = true;
    }

    public void discardCurrentRoute() {
        hasCurrentRoute = false;
    }

    public void addPoint(TMapPoint point) {
        if(hasCurrentRoute == false){
            throw new RuntimeException("need to create route first. call createNewRoute");
        }
        route.add(point);
    }

    public void getAndDisplayPath(final Activity2Activity context){
        TMapData data = new TMapData();

        TMapPoint start = route.get(0);
        TMapPoint end = route.get(1);
        data.findPathData(start, end, new TMapData.FindPathDataListenerCallback() {
            @Override
            public void onFindPathData(TMapPolyLine path) {
                context.displayPathOnMap(path);
            }
        });
    }
}
