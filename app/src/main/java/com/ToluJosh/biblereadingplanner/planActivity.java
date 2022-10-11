package com.ToluJosh.biblereadingplanner;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.Date;

public class planActivity extends AppCompatActivity {

    RadioGroup radioGroup;
    RadioButton radioButton;
    TextView dateTextView;
    ImageButton goBotton;
    CalendarView calendarView;

    public void go (View view) {

        int id = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(id);

        Intent returnIntent = new Intent();

        returnIntent.putExtra("planType", radioButton.getTag().toString());
        returnIntent.putExtra("startDate", dateTextView.getText().toString());
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);

        radioGroup = findViewById(R.id.radioGroup2);
        dateTextView = findViewById(R.id.textView);
        goBotton = findViewById(R.id.imageButton);
        calendarView = findViewById(R.id.calendarView2);
        calendarView.setMaxDate(new Date().getTime());

        setCalendar();
    }


    public void setCalendar() {

        calendarView.setOnDateChangeListener((calendarView, i, i1, i2) -> {
            String day = "";
            String month;
            if (String.valueOf(i) != null) {
                if (i2 < 10) {
                    day = "0" + i2;
                } else {
                    day = String.valueOf(i2);
                }
                if (i1 + 1 < 10) {
                    month = "0" + (i1 + 1);
                } else {
                    month = String.valueOf(i1 + 1);
                }
                String calendarDate = day + " " + month + " " + i;
                dateTextView.setText(calendarDate);
            }
        });
    }
}