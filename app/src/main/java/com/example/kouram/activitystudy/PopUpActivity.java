package com.example.kouram.activitystudy;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import java.util.ArrayList;

public class PopUpActivity extends Activity {
    private ArrayList<Button> radioBtns = new ArrayList<>();
    private int outRadius = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams layoutParams= new WindowManager.LayoutParams();
        layoutParams.flags= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount= 0.7f;
        getWindow().setAttributes(layoutParams);

        setContentView(R.layout.activity_pop_up);
        initButtons();
    }

    private void initButtons() {
        for(int i = 1; i <= 10; i++){
            final int radius = i * 500;
            int id = getResources().getIdentifier("radioButton" + radius,
                                                  "id", getPackageName());
            Button tmpBtn = (Button)findViewById(id);
            tmpBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    outRadius = radius;
                }
            });
            radioBtns.add(tmpBtn);
        }
    }

    //확인 버튼 클릭
    public void onClose(View v){
        //데이터 전달하기
        Intent outIntent = new Intent();
        outIntent.putExtra("result", outRadius);
        setResult(RESULT_OK, outIntent);

        //액티비티(팝업) 닫기
        finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction() == MotionEvent.ACTION_OUTSIDE){
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
