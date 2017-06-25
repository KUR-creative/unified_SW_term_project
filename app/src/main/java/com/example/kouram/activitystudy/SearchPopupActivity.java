package com.example.kouram.activitystudy;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import com.skp.Tmap.TMapPoint;

import java.io.Serializable;
import java.util.ArrayList;

public class SearchPopupActivity extends Activity {

    private ArrayList<TMapPoint> pointArray;
    private int index;
    private Button btn;
    private ArrayList<Integer> result = new ArrayList<Integer>();
    ArrayList<String> data;
    ToggleButton compass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams layoutParams= new WindowManager.LayoutParams();
        layoutParams.flags= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount= 0.7f;
        getWindow().setAttributes(layoutParams);

        setContentView(R.layout.activity_search_popup);
        LinearLayout topLL2 = (LinearLayout) findViewById(R.id.Linear_Search);

        Intent intent = getIntent();
        data = getIntent().getStringArrayListExtra(GetPathActivity.SEARCH_POPUP);
        System.out.println(data);

        final ArrayList<CheckBox> checkboxArray = new ArrayList<CheckBox>();
        for(int i=0; i<data.size(); i++) {
            CheckBox topTV12 = new CheckBox(SearchPopupActivity.this);
            topTV12.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            topTV12.setText(data.get(i));
            topTV12.setWidth(500);
            topLL2.addView(topTV12);
            checkboxArray.add(topTV12);
        }
        btn=(Button)findViewById(R.id.confirm_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            int count=0;
            @Override
            public void onClick(View view) {
                System.out.println("0");
                for(int i=0; i < checkboxArray.size(); i++) {
                    System.out.println("1");
                    if (checkboxArray.get(i).isChecked()) {
                        result.add(i);
                        ++count;
                    }
                }
                if(count > 5) {
                    System.out.println("-1");
                    System.out.println("너무 많이 선택함");
                    result.clear();
                    count = 0;
                }
                else {
                    //System.out.println(result);
                    //finish();
                    Intent outIntent = new Intent();
                    setResult(RESULT_OK, outIntent);
                    outIntent.putExtra(GetPathActivity.SEARCH_POPUP, result);
                    System.out.println("END OF PASS POINT PICK");
                    System.out.println("is result is empty? : " + result.isEmpty());
                    finish();
                }
            }
        });

        /*
        Button confirmBtn = (Button)findViewById(R.id.confirm_btn);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent outIntent = new Intent();
                setResult(RESULT_OK, outIntent);
                outIntent.putExtra(GetPathActivity.SEARCH_POPUP, result);
                System.out.println("END OF PASS POINT PICK");
                System.out.println("is result is empty? : " + result.isEmpty());
                finish();
            }
        });
        */
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
        return;
    }
}