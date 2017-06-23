package com.example.kouram.activitystudy;

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

    // path는 화면에 표시하는 길을 의미한다.
    // route는 TMapPoint의 리스트이다.
    // path를 구할 수 없다면 null을 반환한다.
    public Tuple< TMapPolyLine, ArrayList<Tuple<Integer,String>> > getPathData(){
        int numOfPointsInRoute = getNumOfPointInRoute();
        int indexOfLastPoint = numOfPointsInRoute - 1;

        if(numOfPointsInRoute <= 1){
            // it will be removed in product.
            return null;
        }

        TMapPoint start = route.get(0);
        TMapPoint end = route.get(indexOfLastPoint);

        DomThread dom;
        if(numOfPointsInRoute == 2) {
            // 경유지 없음
            dom = new DomThread(start, end);
        } else {
            // 경유지 있음
            ArrayList<TMapPoint> passList = new ArrayList<>(route.subList(1, indexOfLastPoint));
            dom = new DomThread(start, end, passList);
        }
        Tuple< TMapPolyLine, ArrayList<Tuple<Integer,String>> >
                retTuple = new Tuple<>(getPathFrom(dom), dom.getPathnav());
        path = retTuple.x.getLinePoint(); // for test
        return retTuple;
    }

    private TMapPolyLine getPathFrom(DomThread dom){
        dom.start();
        try {
            dom.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ArrayList<TMapPoint> pointList = dom.getPathData();
        return Tools.getPathFrom(pointList);
    }

    public int getNumOfPointInRoute(){
        return route.size();
    }

    public boolean hasCurrentWorkingRoute(){
        return hasCurrentRoute;
    }

    public ArrayList<TMapPoint> getCurrentPath() { return path; }
    public boolean hasCurrentPath(){
        return !path.isEmpty(); // not empty = has current path
    }

    public void saveCurrentPath(DBManager db){
        System.out.println("called!");
        //serialize data (bos and oos do that job!)
        db.insert(path, -1); // TODO: just test!!
    }

    /*
    public void swapCurrentPath(ArrayList<TMapPoint> newPath){
        hasCurrentRoute = false;
        path = newPath;
    }
    */
}
