package com.example.healthybody;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class AppActivity extends AppCompatActivity {
    private Button nextButton;
    private TextView pageView;
    private ImageView calendarView;
    private int counter = 1;

    private FirebaseFirestore firebaseFirestore;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.applayout);

        firebaseFirestore = FirebaseFirestore.getInstance();

        nextButton = findViewById(R.id.nextButton);
        pageView = findViewById(R.id.pageView);

        setUpAlarm();

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.framelayout, new WelcomeFragment(), "");
        fragmentTransaction.commit();

        nextButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                if (counter == 1) {
                    counter++;
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.framelayout, new SetUpFragment(), "");
                    fragmentTransaction.commit();
                    pageView.setText("Page 2 Of 2");
                } else {
                    getAllInfo();
                    finish();
                    startActivity(new Intent(AppActivity.this, ChartActivity.class));
                }
            }
        });
    }

    private void getAllInfo() {
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("user", 0);
        SharedPreferences sharedPref2 = getApplicationContext().getSharedPreferences("setUp", 0);
        SharedPreferences sharedPref3 = getApplicationContext().getSharedPreferences("weight", 0);
        int height = Integer.parseInt(sharedPref2.getString("height", ""));
        int weight = Integer.parseInt(sharedPref3.getString("weight", ""));

        String email = sharedPref.getString("email", "");
        String userName = sharedPref.getString("userName", "");
        Map<String, Object> users = new HashMap<>();
        users.put("email", email);
        users.put("weight", weight);
        users.put("height", height);

        firebaseFirestore.collection("Users").add(users);
    }

    private void setUpAlarm() {
        // Set up the notification channel
        String channelId = "default";
        String channelName = "Default";
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH));

        // Set up the pending intent
        Intent intent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        // Set up the alarm manager
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        long interval = AlarmManager.INTERVAL_HALF_DAY;
        long triggerTime = SystemClock.elapsedRealtime() + interval;
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, interval, pendingIntent);
    }

}
