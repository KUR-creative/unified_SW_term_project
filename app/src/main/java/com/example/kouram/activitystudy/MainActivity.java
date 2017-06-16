package com.example.kouram.activitystudy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button act2btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        act2btn = (Button)findViewById(R.id.goto_act2_btn);
        act2btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
            // call other activity!
            Intent intent = new Intent(MainActivity.this, TMapActivity.class);
            startActivity(intent);
            finish(); // 이제 타이틀로는 돌아가지 않음.
            }
        });
    }
}
