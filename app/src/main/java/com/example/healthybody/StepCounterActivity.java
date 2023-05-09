package com.example.healthybody;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;


import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;



public class StepCounterActivity extends AppCompatActivity {
    private static final int GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 1;
    private TextView calories, steps;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stepcounterlayout);

        calories = findViewById(R.id.nrOfCalories);
        steps = findViewById(R.id.nrOfSteps);

        requestFitnessPermissions();
    }


    private void requestFitnessPermissions() {
        // Get the currently signed in account
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        // Create the FitnessOptions object
        FitnessOptions fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_CALORIES_EXPENDED, FitnessOptions.ACCESS_READ)
                .build();

        // Check if the account has the necessary permissions
        if (!GoogleSignIn.hasPermissions(account, fitnessOptions)) {
            // Request the necessary permissions
            GoogleSignIn.requestPermissions(this, GOOGLE_FIT_PERMISSIONS_REQUEST_CODE, account, fitnessOptions);
        } else {
            // Permissions granted, access Fitness data
            // Set the start and end time for today
            Calendar cal = Calendar.getInstance();
            Date now = new Date();
            cal.setTime(now);
            long endTime = cal.getTimeInMillis();
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            long startTime = cal.getTimeInMillis();

            // Read the daily step count and calorie expenditure data
            DataReadRequest readRequest = new DataReadRequest.Builder()
                    .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                    .aggregate(DataType.TYPE_CALORIES_EXPENDED, DataType.AGGREGATE_CALORIES_EXPENDED)
                    .bucketByTime(1, TimeUnit.DAYS)
                    .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                    .build();

            Fitness.getHistoryClient(getApplicationContext(), account)
                    .readData(readRequest)
                    .addOnSuccessListener(new OnSuccessListener<DataReadResponse>() {
                        @Override
                        public void onSuccess(DataReadResponse dataReadResponse) {
                            // Check if the data is empty
                            if (dataReadResponse.getBuckets().isEmpty()) {
                                Toast.makeText(StepCounterActivity.this, "No step count or calorie expenditure data for today", Toast.LENGTH_SHORT).show();
                            } else {
                                // Get the daily step count and calorie expenditure for today
                                long totalSteps = 0;
                                float totalCalories = 0;
                                for (Bucket bucket : dataReadResponse.getBuckets()) {
                                    for (DataSet dataSet : bucket.getDataSets()) {
                                        for (DataPoint dp : dataSet.getDataPoints()) {
                                            if (dp.getDataType().equals(DataType.TYPE_STEP_COUNT_DELTA)) {
                                                totalSteps += dp.getValue(Field.FIELD_STEPS).asInt();
                                            } else if (dp.getDataType().equals(DataType.TYPE_CALORIES_EXPENDED)) {
                                                totalCalories += dp.getValue(Field.FIELD_CALORIES).asFloat();
                                            }
                                        }
                                    }
                                }
                                steps.setText(String.valueOf(totalSteps));
                                calories.setText(String.valueOf((int)totalCalories));
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Handle failure
                        }
                    });
        }
    }

}
