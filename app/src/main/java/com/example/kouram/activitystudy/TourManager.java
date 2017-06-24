package com.example.kouram.activitystudy;

import android.graphics.Bitmap;

import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapPolyLine;

import java.util.ArrayList;

/**
 * Created by Ko U Ram on 2017-06-24.
 */

class TourManager {
    class Tour {
        // 필수 field: 없으면 저장 불가능.
        ArrayList< TMapPoint >                  path        = null;
        ArrayList< Tuple<Integer,String> >      naviInfos   = null;
        // optionals.
        ArrayList< Tuple<TMapPoint,Bitmap> >    linkedPics  = null;
        ArrayList< Tuple<TMapPoint,String> >    linkedTexts = null;
    }

    private Tour currentTour = null;
    public void createNewTour() {
        if(currentTour != null){
            throw new RuntimeException("createNewTour cannot be duplicated in same time!" +
                    "you need to end job for current Tour.");
        }
        currentTour = new Tour();
    }

    public boolean hasCurrentWorkingTour() {
        if(currentTour != null)
            return true;
        else
            return false;
    }

    public void discardCurrentTour() {
        currentTour = null;
    }

    public void setCurrentEssentialValues(ArrayList<TMapPoint> path,
                                          ArrayList<Tuple<Integer,String>> navigationInfos)
    {
        setCurrentPath(path);
        setCurrent(navigationInfos);
    }

    public void setCurrentPath(ArrayList<TMapPoint> path) {
        if(currentTour == null){
            throw new RuntimeException("need to create tour first. call createNewTour");
        }
        currentTour.path = path;
    }
    public void setCurrent(TMapPolyLine path) {
        if(currentTour == null){
            throw new RuntimeException("need to create tour first. call createNewTour");
        }
        currentTour.path = path.getLinePoint();
    }
    public void setCurrent(ArrayList<Tuple<Integer,String>> navigationInfos) {
        if(currentTour == null){
            throw new RuntimeException("need to create tour first. call createNewTour");
        }
        currentTour.naviInfos = navigationInfos;
    }

    // TODO: save tour to DB! and load too!
    public void saveAndDiscardCurrentTour(DBManager db) {
        if(currentTour == null){
            throw new RuntimeException("need to create tour first. call createNewTour");
        }
        if(currentTour.path == null){
            throw new RuntimeException("you need to add path to current tour!");
        }
        if(currentTour.naviInfos == null){
            throw new RuntimeException("you need to add navigation information list to current tour!");
        }
        db.insert(currentTour); // 아직 path만 저장 함.
        discardCurrentTour();
    }

}
