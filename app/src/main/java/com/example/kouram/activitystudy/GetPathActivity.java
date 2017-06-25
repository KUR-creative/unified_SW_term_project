package com.example.kouram.activitystudy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapPolyLine;

import java.util.ArrayList;

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
                routeManager.add(start);
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
    }

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
    }
}
