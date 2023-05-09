package com.example.healthybody;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import model.Food;
import model.JsonParser;
import viewModel.ClickListener;
import viewModel.ParseJsonAdapter;

public class FoodSearchActivity extends AppCompatActivity implements ClickListener {
    private TextInputEditText editFood;
    private ImageView search;
    private RecyclerView foodRecycleView;
    private RequestQueue req;
    private ParseJsonAdapter adapter;
    private List<Food> foods = new ArrayList<>();
    private FirebaseFirestore firebaseFirestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchfoodlayout);
        initialize();

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String foodname = Objects.requireNonNull(editFood.getText()).toString();
                if (foodname.isEmpty()) {
                    Toast.makeText(FoodSearchActivity.this, "Please enter food name", Toast.LENGTH_SHORT).show();
                } else getFoods(foodname);
            }
        });
    }

    @Override
    protected void onPause() {
        if (req != null) req.cancelAll(this);
        super.onPause();
    }

    private void getFoods(String foodname) {
        String url = "https://api.api-ninjas.com/v1/nutrition?query=" + foodname;
        RequestQueue req = Volley.newRequestQueue(FoodSearchActivity.this);
        JsonArrayRequest obj = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(JSONArray response) {
                foods.clear();
                foods = JsonParser.onResponse(response);
                adapter = new ParseJsonAdapter(FoodSearchActivity.this, foods, FoodSearchActivity.this);
                foodRecycleView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(FoodSearchActivity.this, "Not Found", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap header = new HashMap();
                header.put("X-Api-Key", "oE4tp3iHrADOO6L5bhzxrA==OAqLFZCMhfEyzBbx");
                return header;
            }
        };
        obj.setTag(this);
        req.add(obj);
    }

    private void initialize() {
        editFood = findViewById(R.id.editFood);
        search = findViewById(R.id.search);
        foodRecycleView = findViewById(R.id.foodRecycleView);
        foodRecycleView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    @Override
    public void onAddButtonClicked(String name, String calories, String carbs, String fat, String protein) {
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("user", 0);
        String email = sharedPref.getString("email", "");

        Date date = java.util.Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
        Map<String, Object> food = new HashMap<>();
        food.put("foodName", name);
        food.put("calories", calories);
        food.put("carbs", carbs);
        food.put("fat", fat);
        food.put("protein", protein);
        food.put("date", date);
        food.put("email", email);

        firebaseFirestore.collection("Foods").add(food);

        Toast.makeText(this, "Foods has been added", Toast.LENGTH_SHORT).show();
        finish();
    }
}
