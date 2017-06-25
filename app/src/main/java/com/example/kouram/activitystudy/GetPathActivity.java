package com.example.kouram.activitystudy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapPOIItem;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapPolyLine;

import java.util.ArrayList;

import static com.example.kouram.activitystudy.MainActivity.LAT_LON;
import static com.example.kouram.activitystudy.MainActivity.ROUTE_ID;

public class GetPathActivity extends AppCompatActivity {
    private final int ACT_GET_RADIUS = 0;
    private RouteManager routeManager= new RouteManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_path);

        Button setRadiusBtn = (Button)findViewById(R.id.set_radius_btn);
        setRadiusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GetPathActivity.this, PopUpActivity.class);
                intent.putExtra("data", "Test Popup");
                startActivityForResult(intent, ACT_GET_RADIUS);
            }
        });

        Button getPathBtn = (Button)findViewById(R.id.get_path_btn);
        getPathBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            // get start point = gps point.
                Intent latLonIntent = getIntent();
                Tuple<Double,Double> latLon
                    = (Tuple<Double,Double>) latLonIntent.getSerializableExtra(MainActivity.LAT_LON);
                        //System.out.println("lat = " + latLon.left + ", lon = " + latLon.right);
                TMapPoint start = new TMapPoint(latLon.left, latLon.right);
            // get pass list (optional)
                ArrayList<TMapPoint> passList = new ArrayList<TMapPoint>();
                for(int index : indexList){
                    passList.add(POIPoints.get(index));
                }
            // get end point
                // TODO: 만일 currentRadius == 0이라면: finish하지 않음..!
                if(currentRadius == 0) {
                    Toast.makeText(GetPathActivity.this,
                                   "먼저 반경을 선택하십시오.",
                                   Toast.LENGTH_SHORT).show();
                    return;
                }
                TMapPoint end = Tools.getRandPointAtCircle(start, currentRadius);

            // add to routeManager.
                routeManager.createNewRoute();
                /*
                if(passList.isEmpty()){
                    routeManager.add(start);
                    routeManager.add(end);
                }else{
                    routeManager.add(start);
                    for(TMapPoint point : passList){
                        routeManager.add(point);
                    }
                    routeManager.add(end);
                }
                */
                routeManager.add(start);
                for(TMapPoint point : passList){
                    routeManager.add(point);
                }
                routeManager.add(end);

            // get path / navi from routeManager.
                Tuple< TMapPolyLine, ArrayList<Tuple<Integer,String>> > pathNaviData;
                pathNaviData = routeManager.getPathData();

                ArrayList<TMapPoint> points = pathNaviData.left.getLinePoint();
                ArrayList<Tuple<Integer,String>> naviInfos = pathNaviData.right;

            // finish!
                ArrayList<Tuple<Double,Double>> pathDataList = Tools.convertFromPointList(points);

                Intent outIntent = getIntent();
                outIntent.putExtra(MainActivity.PATH_DATA, pathDataList);
                outIntent.putExtra(MainActivity.NAVI_DATA, naviInfos);
                outIntent.putExtra(MainActivity.LOADED_ROUTE_ID, -1);

                setResult(RESULT_OK, outIntent);
                finish();
            }
        });

        Button loadBtn = (Button)findViewById(R.id.load_btn);
        loadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GetPathActivity.this, HistoryActivity.class);
                int numRow = getIntent().getIntExtra(ROUTE_ID, -1);
                intent.putExtra(ROUTE_ID, numRow);
                startActivityForResult(intent, GET_ROUTE_ID);

            }
        });

        final EditText searchEditText = (EditText)findViewById(R.id.searchEditText);

        Button searchBtn = (Button) findViewById(R.id.search_btn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchKeyword = searchEditText.getText().toString();
                //System.out.println("??: [" + searchKeyword + "]");
                System.out.println(searchKeyword.isEmpty());
                if(searchKeyword.isEmpty()){
                    Toast.makeText(GetPathActivity.this,
                            "검색어를 입력하시오.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if(currentRadius == 0){
                    Toast.makeText(GetPathActivity.this,
                            "먼저 반경을 선택하십시오.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                searchPOI(searchKeyword, currentRadius, 15);
                //Intent intent = new Intent(GetPathActivity.this, SearchPopupActivity.class);
                //intent.putExtra("OBJECT", POINames);
                //startActivity(intent);
            }
        });
    }




    ArrayList<String>       POINames    = new ArrayList<>();
    ArrayList<TMapPoint>    POIPoints   = new ArrayList<>();
    ArrayList<Integer>      indexList   = new ArrayList<>();
    // radiusLevel = 1  : 300m 내에서 검색,
    // radiusLevel = 33 : 9900m 내에서 검색,
    // radiusLevel = 0  : 서버가 알아서 함.
    // searchCount는 최대 200개까지.
    private void searchPOI(String keyword, final int radiusLevel, final int searchCount) {
        TMapData data = new TMapData();
        if (!TextUtils.isEmpty(keyword)) {
            Tuple<Double,Double> latLon = (Tuple<Double,Double>)
                                          getIntent().getSerializableExtra(LAT_LON);
            double lat = latLon.left;
            double lon = latLon.right;
            TMapPoint nowLocation = new TMapPoint(lat,lon);

            data.findAroundKeywordPOI(nowLocation, keyword, radiusLevel, searchCount, new TMapData.FindAroundKeywordPOIListenerCallback() {
                @Override
                public void onFindAroundKeywordPOI(final ArrayList<TMapPOIItem> arrayList) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //mapView.removeAllMarkerItem();

                            for (TMapPOIItem poi : arrayList) {
                                POINames.add(poi.getPOIName());
                                POIPoints.add(poi.getPOIPoint());
                            }

                            //if (arrayList.size() > 0) {
                                //TMapPOIItem poi = arrayList.get(0);
                            //}

                            Intent intent = new Intent(GetPathActivity.this, SearchPopupActivity.class);
                            intent.putExtra(SEARCH_POPUP, POINames);
                            startActivityForResult(intent, SEARCH_RESULT);
                        }
                    });
                }
            });
        }
    }

    public static final String SEARCH_POPUP = "search-popup";
    private final int SEARCH_RESULT = 10;
    private static final int GET_ROUTE_ID = 3;
    private int currentRadius = 0;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_OK) {
            return;
        }

        if(requestCode == ACT_GET_RADIUS){
            currentRadius = data.getIntExtra("result", 0);
        }
        else if(requestCode == GET_ROUTE_ID){
            Intent outIntent = new Intent();
            int routeID = data.getIntExtra(MainActivity.ROUTE_ID, -1);
            outIntent.putExtra(MainActivity.LOADED_ROUTE_ID, routeID);
            setResult(RESULT_OK, outIntent);
            finish();
        }
        else if(requestCode == SEARCH_RESULT){
            indexList = data.getIntegerArrayListExtra(SEARCH_POPUP);
        }
    }
}
