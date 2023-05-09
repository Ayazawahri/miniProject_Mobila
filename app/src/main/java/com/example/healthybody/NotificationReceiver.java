package com.example.healthybody;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

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

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class NotificationReceiver extends BroadcastReceiver {
    private int steps = 0;
    private static final int GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        requestFitnessPermissions(context);
        String notificationText;
        if (steps < 10000) {
            notificationText = "Your number of steps is less than 10,000, you should exercise!";
        } else {
            notificationText = "Keep up the good work!";
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "default")
                .setSmallIcon(R.drawable.activity_png_isolated_file)
                .setContentTitle("Notification")
                .setContentText(notificationText)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(1, builder.build());
    }

    private void requestFitnessPermissions(Context context) {
        // Get the currently signed in account
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);

        // Create the FitnessOptions object
        FitnessOptions fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_CALORIES_EXPENDED, FitnessOptions.ACCESS_READ)
                .build();

        // Check if the account has the necessary permissions
        if (!GoogleSignIn.hasPermissions(account, fitnessOptions)) {
            // Request the necessary permissions
            GoogleSignIn.requestPermissions((Activity) context, GOOGLE_FIT_PERMISSIONS_REQUEST_CODE, account, fitnessOptions);
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
                    .bucketByTime(1, TimeUnit.DAYS)
                    .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                    .build();

            Fitness.getHistoryClient(context, account)
                    .readData(readRequest)
                    .addOnSuccessListener(new OnSuccessListener<DataReadResponse>() {
                        @Override
                        public void onSuccess(DataReadResponse dataReadResponse) {
                            // Check if the data is empty
                            if (!dataReadResponse.getBuckets().isEmpty()) {
                                // Get the daily step count and calorie expenditure for today
                                long totalSteps = 0;
                                for (Bucket bucket : dataReadResponse.getBuckets()) {
                                    for (DataSet dataSet : bucket.getDataSets()) {
                                        for (DataPoint dp : dataSet.getDataPoints()) {
                                            if (dp.getDataType().equals(DataType.TYPE_STEP_COUNT_DELTA)) {
                                                totalSteps += dp.getValue(Field.FIELD_STEPS).asInt();
                                            }
                                        }
                                    }
                                }
                                steps = (int) totalSteps;
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
