package model;

import android.hardware.lights.LightState;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonParser {
    private static final List<Food> foods = new ArrayList<>();

    public static List<Food> onResponse(JSONArray response) {
        foods.clear();
        try {
            for (int j = 0; j < response.length(); j++) {
                JSONObject responses = response.getJSONObject(j);
                String name = responses.getString("name");
                String calories = responses.getString("calories");
                String fat_total_g = responses.getString("fat_total_g");
                String protein_g = responses.getString("protein_g");
                String carbohydrates_total_g= responses.getString("carbohydrates_total_g");
                foods.add(new Food(name, calories, fat_total_g, protein_g, carbohydrates_total_g));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return foods;
    }
}
