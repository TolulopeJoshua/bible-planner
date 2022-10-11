package com.ToluJosh.biblereadingplanner;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    TextView dateTextView;
    TextView textView0;
    TextView textView1;
    TextView textView2;
    TextView textView3;
    CheckBox checkBox0;
    CheckBox checkBox1;
    CheckBox checkBox2;
    CheckBox checkBox3;
    CalendarView calendarView;
    WebView webView;
    TextView[] textViews = new TextView[4];
    CheckBox[] checkBoxes = new CheckBox[4];
    ScrollView scrollView;

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    ArrayList<Book> books = new ArrayList<>();
    ArrayList<String> allChapters = new ArrayList<>();
    ArrayList<String[]> dailyChapters = new ArrayList<>();
    String[] todayChapters = new String[0];
    Calendar c = Calendar.getInstance();
    DateFormat formatter = new SimpleDateFormat("dd MM yyyy");
    String planText = "";
    String startDate;
    String today = formatter.format(new Date());
    String dayOfWeek = null;
    int LAUNCH_SECOND_ACTIVITY = 1;
    int planType;

    public void backButton (View view) {
        scrollView.setVisibility(View.INVISIBLE);
    }

    public void loadChapter (View view) {
        TextView textView = (TextView) view;
        String chapter = textView.getText().toString();
        webView.clearView();

        String chapterName = "Genesis";
        int chapterNum = 1;
        for (int i = 0; i < allChapters.size(); i++) {
            if (chapter.equals(allChapters.get(i))) {
                String[] chapterString = chapter.split(" ");
                chapterName = chapterString[0];
                chapterNum = Integer.parseInt(chapterString[1]);
            }
        }

        for (Book book : books) {
            if (book.getName().equals(chapterName)) {
                String url = "https://my.bible.com/bible/1/" + book.getNameCode() + "." + chapterNum + ".KJV";
                webView.loadUrl(url);
            }
        }
    }

    public void previousDay(View view) throws ParseException {

//        c.setTime(formatter.parse(today));
//        c.add(Calendar.DATE, -1);
//        today = formatter.format(c.getTime());
//        dayOfWeek = new SimpleDateFormat("EE").format(c.getTime());
        numOfDays -= 1;
        getToday();
        setScreen();
    }

    public void nextDay(View view) throws ParseException {

//        c.setTime(formatter.parse(today));
//        c.add(Calendar.DATE, 1);
//        today = formatter.format(c.getTime());
//        dayOfWeek = new SimpleDateFormat("EE").format(c.getTime());
        numOfDays += 1;
        getToday();
        setScreen();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LAUNCH_SECOND_ACTIVITY && resultCode == Activity.RESULT_OK) {
            startDate = data.getStringExtra("startDate");
            planType = Integer.parseInt(data.getStringExtra("planType"));
            editor.putString("start_date", startDate);
            editor.putInt("plan_type", planType);
            editor.apply();
            plan();
            setScreen();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        pref  = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        editor = pref.edit();
        startDate = pref.getString("start_date", "01 01 " + c.get(Calendar.YEAR));
        planType = pref.getInt("plan_type", 1);

        dateTextView = findViewById(R.id.dateTextView);
        textView0 = findViewById(R.id.textView1);
        textView1 = findViewById(R.id.textView2);
        textView2 = findViewById(R.id.textView3);
        textView3 = findViewById(R.id.textView4);
        checkBox0 = findViewById(R.id.checkBox1);
        checkBox1 = findViewById(R.id.checkBox2);
        checkBox2 = findViewById(R.id.checkBox3);
        checkBox3 = findViewById(R.id.checkBox4);
        calendarView = findViewById(R.id.calendarView);
        webView = findViewById(R.id.webView);
        scrollView = findViewById(R.id.scrollView);
        scrollView.setVisibility(View.INVISIBLE);
        textViews[0] = textView0; textViews[1] = textView1; textViews[2] = textView2; textViews[3] = textView3;
        checkBoxes[0] = checkBox0; checkBoxes[1] = checkBox1; checkBoxes[2] = checkBox2; checkBoxes[3] = checkBox3;
        CompoundButton.OnCheckedChangeListener onCheckedChangeListener;
        onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                Toast.makeText(MainActivity.this, compoundButton.getTag().toString(), Toast.LENGTH_SHORT).show();
                editor.putBoolean(textViews[Integer.parseInt(compoundButton.getTag().toString())].getText().toString(), b);
                editor.apply();
//                 Toast.makeText(MainActivity.this, String.valueOf(pref.getBoolean(textViews[i].getText().toString(), false)), Toast.LENGTH_SHORT).show();
            }
        };
        checkBox0.setOnCheckedChangeListener(onCheckedChangeListener);
        checkBox1.setOnCheckedChangeListener(onCheckedChangeListener);
        checkBox2.setOnCheckedChangeListener(onCheckedChangeListener);
        checkBox3.setOnCheckedChangeListener(onCheckedChangeListener);

        addBooks();
        plan();
        setScreen();
        setDialogue();
        setCalendar();

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
    }

    public void setCalendar() {

        calendarView.setOnDateChangeListener((calendarView, i, i1, i2) -> {
            String day;
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
                today = calendarDate;
//                try {
//                    c.setTime(formatter.parse(today));
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//                dayOfWeek = new SimpleDateFormat("EE").format(c.getTime());
                calendarView.setDate((new Date().getTime()));
            }
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd MM yyyy");
            LocalDate date1 = LocalDate.parse(startDate, dtf);
            LocalDate date2 = LocalDate.parse(today, dtf);
            numOfDays = ChronoUnit.DAYS.between(date1, date2);
            getToday();
            setScreen();
        });
    }

    public void setDialogue () {

        Spinner dialogueSpinner = findViewById(R.id.spinner);
        String[] dialogueItems = new String[6];
        dialogueItems[0] = "Go to today";
        dialogueItems[1] = "Go to date";
        dialogueItems[2] = "View dates";
        dialogueItems[3] = "New plan";
        dialogueItems[4] = "Clear all read";
        dialogueItems[5] = "";

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dialogueItems);
        dialogueSpinner.setAdapter(adapter);

        dialogueSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                switch (i) {
                    case 0:
                        today = formatter.format(new Date());
                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd MM yyyy");
                        LocalDate date1 = LocalDate.parse(startDate, dtf);
                        LocalDate date2 = LocalDate.parse(today, dtf);
                        numOfDays = ChronoUnit.DAYS.between(date1, date2);
                        getToday();
                        setScreen();
                        adapterView.setSelection(5);
                        break;
                    case 1:
                        calendarView.setVisibility(View.VISIBLE);
                        adapterView.setSelection(5);
                        break;
                    case 2:
                        plan();
                        Intent allDatesText = new Intent(MainActivity.this, allDates.class);
                        allDatesText.putExtra("planTexts", planText);
                        startActivity(allDatesText);
                        adapterView.setSelection(5);
                        break;
                    case 3:
                        Intent intent = new Intent(MainActivity.this, planActivity.class);
                        startActivityForResult(intent, LAUNCH_SECOND_ACTIVITY);
                        adapterView.setSelection(5);
                        break;
                    case 4:
                        for (Book book : books) {
                            ArrayList<String> chapters = book.getChapters();
                            for (String chapter : chapters) {
                                editor.remove(chapter);
                                editor.apply();
                            }
                        }
                        setScreen();
                        adapterView.setSelection(5);

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                setScreen();
            }
        });

    }

    public void setScreen() {

            if (todayChapters.length != 0) {
                dateTextView.setText(dayOfWeek + " " + new SimpleDateFormat("dd MMM, yyyy").format(c.getTime()));
                textView0.setText(todayChapters[0]);
                textView1.setText(todayChapters[1]);
                textView2.setText(todayChapters[2]);

                for (int i = 0; i < todayChapters.length; i++) {
                    checkBoxes[i].setChecked(pref.getBoolean(todayChapters[i], false));

                if (todayChapters.length == 4) {
                    textView3.setText(todayChapters[3]);
                    checkBox3.setAlpha(1);
                    textView3.setAlpha(1);
                } else {
                    checkBox3.setChecked(false);
                    checkBox3.setAlpha(0);
                    textView3.setAlpha(0);
                }
            }

        }
        TableLayout tableLayout = findViewById(R.id.tableLayout);
        tableLayout.setAlpha(0);
        tableLayout.animate().alphaBy(1).setDuration(100);

        calendarView.setVisibility(View.INVISIBLE);
        loadChapter(textView0);
    }

    public void planChapters (int type) {
        switch (type) {
            case 1:
                allChapters.clear();
                for (Book book : books) {
                    allChapters.addAll(book.getChapters());
                }
                allChapters.addAll(books.get(18).getChapters());
                break;
            case 2:
                allChapters.clear();
                for (int i = 39; i < 66; i++) {
                    allChapters.addAll(books.get(i).getChapters());
                }
                for (int i = 0; i < 4; i++) {
                    allChapters.addAll(books.get(i).getChapters());
                }
                for (int i = 39; i < 66; i++) {
                    allChapters.addAll(books.get(i).getChapters());
                }
                for (int i = 17; i < 22; i++) {
                    allChapters.addAll(books.get(i).getChapters());
                }
                for (int i = 39; i < 66; i++) {
                    allChapters.addAll(books.get(i).getChapters());
                }
                allChapters.addAll(books.get(41).getChapters());
                break;
            case 3:
                allChapters.clear();
                for (int j = 0; j < 5; j++) {
                    for (int i = 39; i < 66; i++) {
                        allChapters.addAll(books.get(i).getChapters());
                    }
                }
                break;
        }
    }

    long numOfDays;
    public void plan() {

        dailyChapters.clear();
        planChapters(planType);
        planText = "";

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd MM yyyy");
        LocalDate date1 = LocalDate.parse(startDate, dtf);
        LocalDate date2 = LocalDate.parse(today, dtf);
        numOfDays = ChronoUnit.DAYS.between(date1, date2);
        try {
            c.setTime(formatter.parse(startDate));
        } catch (Exception e) {
            e.printStackTrace();
        }
        c.add(Calendar.DATE, (int) numOfDays);
        today = formatter.format(c.getTime());

        int previousChapters = 0;
        for (int i = 0; i < 365; i++) {
            String[] temp = new String[0];
//            todayChapters.clear();
            try {
                c.setTime(formatter.parse(startDate));
                c.add(Calendar.DATE, i);
                planText += "Day " + (i + 1) + ": " + new SimpleDateFormat("dd MMM, yyyy").format(c.getTime());
                dayOfWeek = new SimpleDateFormat("EE").format(c.getTime());
                if (dayOfWeek.equals("Sun") || dayOfWeek.equals("Sat")) {
                    temp = new String[4];
                    for (int j = 0; j < 4; j++) {
                        temp[j] = allChapters.get(previousChapters + j);
                        planText += "\n>>" + temp[j];
                        if (pref.getBoolean(temp[j], false)) {
                            planText += "  *";
                        }
//                        todayChapters.add(allChapters.get(previousChapters + j));
                    }
                    previousChapters += 4;
                } else {
                    temp = new String[3];
                    for (int j = 0; j < 3; j++) {
                        temp[j] = allChapters.get(previousChapters + j);
                        planText += "\n>>" + temp[j];
                        if (pref.getBoolean(temp[j], false)) {
                            planText += "  *";
                        }
//                        todayChapters.add(allChapters.get(previousChapters + j));
                    }
                    previousChapters += 3;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            dailyChapters.add(temp);
            planText += "\n\n";
        }
        getToday();
//        Toast.makeText(this, Integer.toString(todayChapters.size()), Toast.LENGTH_SHORT).show();
    }

    public void getToday () {
        if (numOfDays < 0) {
            Toast.makeText(this, "One year plan exceeded.", Toast.LENGTH_SHORT).show();
            numOfDays = 0;
        } else if (numOfDays > 364) {
            Toast.makeText(this, "One year plan exceeded.", Toast.LENGTH_SHORT).show();
            numOfDays = 364;
        }
        todayChapters = dailyChapters.get((int) numOfDays);
        try {
            c.setTime(formatter.parse(startDate));
        } catch (Exception e) {
            // to be added...
        }
        c.add(Calendar.DATE, (int) numOfDays);
        dayOfWeek = new SimpleDateFormat("EE").format(c.getTime());
    }
//
//    public void plan() {
//
//        planChapters(planType);
//
//        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd MM yyyy");
//        LocalDate date1 = LocalDate.parse(startDate, dtf);
//        LocalDate date2 = LocalDate.parse(today, dtf);
//        long numOfDays = ChronoUnit.DAYS.between(date1, date2);
//        if (numOfDays > 364) {
//            Toast.makeText(this, "One year plan exceeded.", Toast.LENGTH_SHORT).show();
//            numOfDays = 364;
//            try {
//                c.setTime(formatter.parse(startDate));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            c.add(Calendar.DATE, (int) numOfDays);
//            today = formatter.format(c.getTime());
//        }
//
//        int previousChapters = 0;
//        for (int i = 0; i <= numOfDays; i++) {
//            todayChapters.clear();
//            try {
//                c.setTime(formatter.parse(startDate));
//                c.add(Calendar.DATE, i);
//                dayOfWeek = new SimpleDateFormat("EE").format(c.getTime());
//                if (dayOfWeek.equals("Sun") || dayOfWeek.equals("Sat")) {
//                    for (int j = 0; j < 4; j++) {
//                        todayChapters.add(allChapters.get(previousChapters + j));
//                    }
//                    previousChapters += 4;
//                } else {
//                    for (int j = 0; j < 3; j++) {
//                        todayChapters.add(allChapters.get(previousChapters + j));
//                    }
//                    previousChapters += 3;
//                }
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//        }
////        Toast.makeText(this, Integer.toString(todayChapters.size()), Toast.LENGTH_SHORT).show();
//    }

    public void addBooks() {
        books.add(new Book("Genesis", "GEN", 50));
        books.add(new Book("Exodus", "EXO", 40));
        books.add(new Book("Leviticus","LEV", 27));
        books.add(new Book("Numbers","NUM", 36));
        books.add(new Book("Deuteronomy","DEU", 34));
        books.add(new Book("Joshua","JOS", 24));
        books.add(new Book("Judges", "JDG",21));
        books.add(new Book("Ruth","RUT", 4));
        books.add(new Book("1Samuel","1SA", 31));
        books.add(new Book("2Samuel","2SA", 24));
        books.add(new Book("1Kings","1KI", 22));
        books.add(new Book("2Kings","2KI", 25));
        books.add(new Book("1Chronicles","1CH", 29));
        books.add(new Book("2Chronicles","2CH", 36));
        books.add(new Book("Ezra","EZR", 10));
        books.add(new Book("Nehemiah","NEH", 13));
        books.add(new Book("Esther","EST", 10));
        books.add(new Book("Job","JOB", 42));
        books.add(new Book("Psalms", "PSA",150));
        books.add(new Book("Proverbs","PRO", 31));
        books.add(new Book("Ecclesiastes","ECC", 12));
        books.add(new Book("SongOfSolomon","SNG", 8));
        books.add(new Book("Isaiah","ISA", 66));
        books.add(new Book("Jeremiah","JER", 52));
        books.add(new Book("Lamentations","LAM", 5));
        books.add(new Book("Ezekiel","EZK", 48));
        books.add(new Book("Daniel","DAN", 12));
        books.add(new Book("Hosea","HOS", 14));
        books.add(new Book("Joel","JOL", 3));
        books.add(new Book("Amos","AMO", 9));
        books.add(new Book("Obadiah","OBA", 1));
        books.add(new Book("Jonah","JON", 4));
        books.add(new Book("Micah","MIC", 7));
        books.add(new Book("Nahum","NAM", 3));
        books.add(new Book("Habakkuk", "HAB",3));
        books.add(new Book("Zephaniah","ZEP", 3));
        books.add(new Book("Haggai","HAG", 2));
        books.add(new Book("Zechariah","ZEC", 14));
        books.add(new Book("Malachi","MAL", 4));
        books.add(new Book("Matthew","MAT", 28));
        books.add(new Book("Mark","MRK", 16));
        books.add(new Book("Luke","LUK", 24));
        books.add(new Book("John","JHN", 21));
        books.add(new Book("Acts","ACT", 28));
        books.add(new Book("Romans","ROM", 16));
        books.add(new Book("1Corinthians","1CO", 16));
        books.add(new Book("2Corinthians","2CO", 13));
        books.add(new Book("Galatians","GAL", 6));
        books.add(new Book("Ephesians", "EPH",6));
        books.add(new Book("Philippians","PHP", 4));
        books.add(new Book("Colossians","COL", 4));
        books.add(new Book("1Thessalonians","1TH", 5));
        books.add(new Book("2Thessalonians","2TH", 3));
        books.add(new Book("1Timothy","1TI", 6));
        books.add(new Book("2Timothy","1TI", 4));
        books.add(new Book("Titus","TIT", 3));
        books.add(new Book("Philemon","PHM", 1));
        books.add(new Book("Hebrews","HEB", 13));
        books.add(new Book("James","JAS", 5));
        books.add(new Book("1Peter","1PE", 5));
        books.add(new Book("2Peter","2PE", 3));
        books.add(new Book("1John","1JN", 5));
        books.add(new Book("2John","2JN", 1));
        books.add(new Book("3John","3JN", 1));
        books.add(new Book("Jude","JUD", 1));
        books.add(new Book("Revelation","REV", 22));
    }
    class Book {

        private String name;
        private String nameCode;
        private ArrayList<String> chapters = new ArrayList<>();

        public Book(String name, String nameCode, int chaptersNum) {
            this.name = name;
            this.nameCode = nameCode;
            for (int i = 1; i <= chaptersNum; i++) {
                chapters.add(this.name + " " + i);
            }
        }

        public String getNameCode() {
            return nameCode;
        }

        public String getName() {
            return name;
        }

        public ArrayList<String> getChapters() {
            return this.chapters;
        }

        public ArrayList<String> getChapters(int start, int end) {
            ArrayList<String> selectedChapters = new ArrayList<>();
            for (int i = start - 1; i < end; i++) {
                selectedChapters.add(chapters.get(i));
            }
            return selectedChapters;
        }

    }
}

