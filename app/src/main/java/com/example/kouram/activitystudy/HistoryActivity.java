package com.example.kouram.activitystudy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

public class HistoryActivity extends AppCompatActivity {
    private int numRow = 0;
    private int saveIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        Intent intent = getIntent();
        numRow = intent.getIntExtra(MainActivity.ROUTE_ID, 0);
        if(numRow == 0){
            finish();
        }

        LinearLayout topLL2 = (LinearLayout) findViewById(R.id.linear_root);
        for(int i = 0; i < numRow; i++){
            Button rowBtn = new Button(HistoryActivity.this);
            rowBtn.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            rowBtn.setText("path id: " + i);
            rowBtn.setWidth(1080);

            topLL2.addView(rowBtn);
            saveIndex = i;
            rowBtn.setOnClickListener(new Button.OnClickListener() {
                int i = saveIndex;
                public void onClick(View v) {
                    Intent outIntent = getIntent();
                    outIntent.putExtra(MainActivity.ROUTE_ID, i);
                    setResult(RESULT_OK, outIntent);
                    finish();
                }
            });
        }
    }
}
