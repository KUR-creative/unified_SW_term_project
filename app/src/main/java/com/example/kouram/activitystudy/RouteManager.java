package com.example.kouram.activitystudy;

import android.widget.Toast;

import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapPolyLine;
import java.util.ArrayList;

/**
 * Created by Ko U Ram on 2017-06-16.
 *
 * Route는 시작점, 경유지, 끝점으로 이루어지는 TMapPoint의 리스트이다.
 * Path는 Route를 이용해서 생성하여 화면에 출력하는 길이다.
 *
 * RouteManager는 항상 단 하나의 Route를 유지하고 관리한다.
 * 예외적인 상황에 대해서는 RuntimeException을 throw한다.
 */

public class RouteManager {
    private boolean hasCurrentRoute = false;
    private ArrayList<TMapPoint> route = new ArrayList<>();
    private ArrayList<TMapPoint> path = new ArrayList<>();

    public void createNewRoute() {
        if(hasCurrentRoute) {
            throw new RuntimeException("createNewRoute cannot be duplicated in same time!" +
                                    "you need to end job for current path.");
        }
        hasCurrentRoute = true;
    }

    public void discardCurrentRoute() {
        hasCurrentRoute = false;
        route.clear();
    }

    public void addPoint(TMapPoint point) {
        if(hasCurrentRoute == false){
            throw new RuntimeException("need to create route first. call createNewRoute");
        }
        route.add(point);
    }

    // 경유지가 있다면?  (3개 이상)     checked.
    // 경유지가 없다면?  (2개: 시작-끝) checked.
    // 시작,끝이 없다면? (0개 / 1개)    checked.
    //
    // path는 화면에 표시하는 길을 의미한다.
    // route는 TMapPoint의 리스트이다.
    public void getAndDisplayPath(final TMapActivity context){
        int numOfPointsInRoute = getNumOfPointInRoute();
        int indexOfLastPoint = numOfPointsInRoute - 1;

        if(numOfPointsInRoute <= 1){
            // it will be removed in product.
            Toast.makeText(context, "add more point.", Toast.LENGTH_SHORT).show();
            return;
        }

        TMapPoint start = route.get(0);
        TMapPoint end = route.get(indexOfLastPoint);

        TMapData data = new TMapData();
        if(numOfPointsInRoute == 2)
        {   // 경유지 없음
            data.findPathData(start, end, new TMapData.FindPathDataListenerCallback()
            {
                @Override
                public void onFindPathData(TMapPolyLine tmpPath) {
                    context.displayPathOnMap(tmpPath);
                    path = tmpPath.getLinePoint();
                    for(TMapPoint p : path){
                        //System.out.println("lat " + p.getLatitude());
                        // for graph algorithm
                        //context.addMarker(p.getLatitude(), p.getLongitude(), "a");
                    }
                }
            });
        }
        else
        {   // 경유지 있음
            ArrayList<TMapPoint> passList = new ArrayList<>(route.subList(1, indexOfLastPoint));
            data.findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH,
                start, end, passList, 0, new TMapData.FindPathDataListenerCallback()
            {
                @Override
                public void onFindPathData(TMapPolyLine tmpPath) {
                    context.displayPathOnMap(tmpPath);
                    path = tmpPath.getLinePoint();
                    for(TMapPoint p : path){
                        //System.out.println("lat " + p.getLatitude());
                        // for graph algorithm
                        //context.addMarker(p.getLatitude(), p.getLongitude(), "a");
                    }
                }
            });
        }
    }

    public int getNumOfPointInRoute(){
        return route.size();
    }

    public boolean hasCurrentWorkingRoute(){
        return hasCurrentRoute;
    }

    public ArrayList<TMapPoint> getCurrentPath() { return path; }
}
