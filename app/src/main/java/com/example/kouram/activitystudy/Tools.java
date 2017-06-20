package com.example.kouram.activitystudy;

import com.skp.Tmap.TMapPoint;

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
}
