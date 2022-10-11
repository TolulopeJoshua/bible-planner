package com.ToluJosh.biblereadingplanner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class allDates extends AppCompatActivity {

    String planText;

    public void back (View view) {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_dates2);

        Intent intent = getIntent();
        planText = intent.getStringExtra("planTexts");
        TextView allDatesText = findViewById(R.id.allDatesTextView);
        allDatesText.setText(planText);
    }
}