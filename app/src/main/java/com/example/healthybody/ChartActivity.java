package com.example.healthybody;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChartActivity extends AppCompatActivity {
    private float carbs = 0f, fat = 0f, protein = 0f;
    private PieChart chart;
    private String email = "";
    private ImageView calendarView;
    private TextView welcomeBackView;
    private CircleImageView userPhotoImage;
    private CardView mealview, activityView, chartInfo;
    private PieData pieData;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private CollectionReference usersRef = firestore.collection("Foods");
    private int lightPink = Color.argb(255, 255, 182, 193);

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chartlayout);

        initialize();
        setUserPhoto();

        final Calendar c = Calendar.getInstance();
        final int year = c.get(Calendar.YEAR);
        final int month = c.get(Calendar.MONTH);
        final int day = c.get(Calendar.DAY_OF_MONTH);

        calendarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(ChartActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("date", 0);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.clear();
                        editor.putString("year", String.valueOf(year));
                        editor.putString("month", String.valueOf(month));
                        editor.putString("day", String.valueOf(dayOfMonth));
                        editor.apply();

                        makeValuesEmpty();
                        getInfoByDate(year, month, dayOfMonth);
                    }
                }, year, month, day);
                dialog.show();
            }
        });

        chartInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChartActivity.this, ListActivity.class));
            }
        });

        mealview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChartActivity.this, FoodSearchActivity.class));
            }
        });

        activityView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChartActivity.this, StepCounterActivity.class));
            }
        });

        showPieChart();
    }

    private void initialize() {
        chart = findViewById(R.id.chart);
        mealview = findViewById(R.id.addMealCardView);
        activityView = findViewById(R.id.showActivityCardView);
        userPhotoImage = findViewById(R.id.userImageView);
        welcomeBackView = findViewById(R.id.welcomeBckView);
        firestore = FirebaseFirestore.getInstance();
        calendarView = findViewById(R.id.calendarView);
        chartInfo= findViewById(R.id.chartInfo);
    }

    private void showPieChart() {
        // Set the data for the chart.
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(fat, "Fat"));
        entries.add(new PieEntry(protein, "Protein"));
        entries.add(new PieEntry(carbs, "Carbs"));
        PieDataSet dataSet = new PieDataSet(entries, "Macronutrients");
        dataSet.setColors(Color.LTGRAY, lightPink, Color.BLACK);
        dataSet.setValueTextColor(Color.WHITE);
        pieData = new PieData(dataSet);
        chart.setData(pieData);

// Customize the appearance of the chart.
        chart.setHoleRadius(0);
        chart.setTransparentCircleRadius(0);
        chart.setDrawEntryLabels(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkDatabase();
    }


    private void updateChart() {
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(fat, "Fat"));
        entries.add(new PieEntry(protein, "Protein"));
        entries.add(new PieEntry(carbs, "Carbs"));
        PieDataSet dataSet = new PieDataSet(entries, "Macronutrients");
        dataSet.setColors(Color.LTGRAY, lightPink, Color.BLACK);
        dataSet.setValueTextColor(Color.WHITE);
        PieData pieData = new PieData(dataSet);

        dataSet.setValues(entries);
        chart.setData(pieData);
        chart.invalidate();
    }


    @SuppressLint("SetTextI18n")
    private void setUserPhoto() {
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("user", 0);
        String url = sharedPref.getString("photoUrl", "");
        String name = sharedPref.getString("userName", "");
        email = sharedPref.getString("email", "");
        Glide.with(this).load(url).into(userPhotoImage);
        welcomeBackView.setText("Welcome Back " + name + " !");
    }

    private void checkDatabase() {
        Date date = java.util.Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
        Query query = usersRef.whereEqualTo("email", email).whereEqualTo("date", date);
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                List<DocumentSnapshot> documents = querySnapshot.getDocuments();
                if (!documents.isEmpty()) {
                    for (int i = 0; i < documents.size(); i++) {
                        fat += Float.parseFloat(Objects.requireNonNull(documents.get(i).getString("fat")));
                        carbs += Float.parseFloat(Objects.requireNonNull(documents.get(i).getString("carbs")));
                        carbs += Float.parseFloat(Objects.requireNonNull(documents.get(i).getString("carbs")));
                        protein += Float.parseFloat(Objects.requireNonNull(documents.get(i).getString("protein")));
                    }
                    updateChart();
                    makeValuesEmpty();
                }
            }
        });
    }

    private void getInfoByDate(int year, int month, int dayOfMonth) {
        Query query = usersRef.whereEqualTo("email", email);
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                List<DocumentSnapshot> documents = querySnapshot.getDocuments();
                if (!documents.isEmpty()) {
                    for (int i = 0; i < documents.size(); i++) {
                        Timestamp timestamp = documents.get(i).getTimestamp("date");
                        Date date = Objects.requireNonNull(timestamp).toDate();
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(date);

                        if (calendar.get(Calendar.YEAR) == year && calendar.get(Calendar.MONTH) == month && calendar.get(Calendar.DAY_OF_MONTH) == dayOfMonth) {
                            fat += Float.parseFloat(Objects.requireNonNull(documents.get(i).getString("fat")));
                            carbs += Float.parseFloat(Objects.requireNonNull(documents.get(i).getString("carbs")));
                            carbs += Float.parseFloat(Objects.requireNonNull(documents.get(i).getString("carbs")));
                            protein += Float.parseFloat(Objects.requireNonNull(documents.get(i).getString("protein")));
                        }
                    }
                    updateChart();
                    makeValuesEmpty();
                }
            }
        });
    }

    private void makeValuesEmpty() {
        fat = 0;
        protein = 0;
        carbs = 0;
    }
}

