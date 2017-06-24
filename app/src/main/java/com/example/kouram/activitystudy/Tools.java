package com.example.kouram.activitystudy;

import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapPolyLine;

import java.util.ArrayList;

import static android.R.attr.pathData;

/**
 * Created by Ko U Ram on 2017-06-21.
 */

public class Tools {
    static
    public double meterTolat(double m){
        return  (m * 0.0000089898);
    } //미터 거리를 위도 차로 변환

    static
    public double meterTolon(double m){
        return (m * 0.0000142206);
    } //미터 거리를 경도 차로 변환

    static
    public TMapPoint getRandPointAtCircle(TMapPoint center, double radius){
        double endLat = (Math.random() * 2.0 - 1.0) * radius;
        double endLon = meterTolon(Math.sqrt(radius * radius - endLat * endLat));

        endLat = meterTolat(endLat) + center.getLatitude();

        if(Math.random()-0.5 >= 0) {
            endLon = center.getLongitude() + endLon;
        } else {
            endLon = center.getLongitude() - endLon;
        }

        return new TMapPoint(endLat,endLon);
    }

    static
    public TMapPolyLine getPathFrom(ArrayList<TMapPoint> pointList){
        TMapPolyLine path = new TMapPolyLine();
        for(TMapPoint point : pointList){
            path.addLinePoint(point);
        }
        return path;
    }

    static
    public ArrayList<Tuple<Double,Double>> convertFromPointList(ArrayList<TMapPoint> pointList){
        ArrayList<Tuple<Double,Double>> retList = new ArrayList<Tuple<Double, Double>>();
        for(TMapPoint point : pointList){
            retList.add(new Tuple<Double, Double>(point.getLatitude(),
                    point.getLongitude()));
        }
        return retList;
    }

    static
    public ArrayList<TMapPoint> convertFrom(ArrayList<Tuple<Double,Double>> xylist){
        ArrayList<TMapPoint> pointList = new ArrayList<>();
        for(Tuple<Double,Double> xy : xylist){
            pointList.add( new TMapPoint(xy.left, xy.right) );
        }
        return pointList;
    }
}
