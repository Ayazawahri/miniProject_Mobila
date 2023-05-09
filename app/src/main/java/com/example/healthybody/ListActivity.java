package com.example.healthybody;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import model.Food;
import viewModel.ListAdapter;

public class ListActivity extends AppCompatActivity {
    private RecyclerView listRecycleView;
    private final FirebaseFirestore firebaseFirestore= FirebaseFirestore.getInstance();
    private final CollectionReference usersRef = firebaseFirestore.collection("Foods");
    private int year, month, day;
    private String email = "";
    private List<Food> foods = new ArrayList<>();
    private ListAdapter adapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listfoodslayout);

        listRecycleView = findViewById(R.id.listRecycleView);
        listRecycleView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

        getDate();
        getFromDatabase();
    }

    private void getFromDatabase() {
        foods.clear();
        Query query = usersRef.whereEqualTo("email", email);
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                List<DocumentSnapshot> documents = querySnapshot.getDocuments();
                if (!documents.isEmpty()) {
                    for (int i = 0; i < documents.size(); i++) {
                        Timestamp timestamp = documents.get(i).getTimestamp("date");
                        Date date = Objects.requireNonNull(timestamp).toDate();
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(date);
                        if (calendar.get(Calendar.YEAR) == year && calendar.get(Calendar.MONTH) == month && calendar.get(Calendar.DAY_OF_MONTH) == day) {
                            float fat = Float.parseFloat(Objects.requireNonNull(documents.get(i).getString("fat")));
                            float carbs = Float.parseFloat(Objects.requireNonNull(documents.get(i).getString("carbs")));
                            String foodsName = documents.get(i).getString("foodName");
                            float protein = Float.parseFloat(Objects.requireNonNull(documents.get(i).getString("protein")));
                            float calories = Float.parseFloat(Objects.requireNonNull(documents.get(i).getString("calories")));

                            foods.add(new Food(foodsName, String.valueOf(calories), String.valueOf(fat), String.valueOf(protein), String.valueOf(carbs)));
                        }
                    }
                    adapter = new viewModel.ListAdapter(ListActivity.this, foods);
                    listRecycleView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }


    private void getDate() {
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("date", 0);
        SharedPreferences sharedPref2 = getApplicationContext().getSharedPreferences("user", 0);
        year = Integer.parseInt(sharedPref.getString("year", ""));
        month = Integer.parseInt(sharedPref.getString("month", ""));
        day = Integer.parseInt(sharedPref.getString("day", ""));
        email = sharedPref2.getString("email", "");
    }
}


