package com.example.kouram.activitystudy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class GetPathActivity extends AppCompatActivity {
    private final int ACT_GET_RADIUS = 0;

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
                // get pass list (optional)
                // get end point
                // finish!
                Intent outIntent = getIntent();
                ArrayList<Tuple<Double,Double>> pathNaviData = new ArrayList<Tuple<Double, Double>>();
                pathNaviData.add(new Tuple<Double, Double>(1.1, 2.2));
                pathNaviData.add(new Tuple<Double, Double>(11.1, 22.2));
                pathNaviData.add(new Tuple<Double, Double>(111.1, 222.2));
                outIntent.putExtra("path-navi-data", pathNaviData);
                setResult(RESULT_OK, outIntent);
                finish();
            }
        });
    }

    private int currentRadius = 0;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_OK) {
            return;
        }

        if(requestCode == ACT_GET_RADIUS){
            currentRadius = data.getIntExtra("result", 0);
            //System.out.println("r = " + currentRadius);
        }
    }
}
