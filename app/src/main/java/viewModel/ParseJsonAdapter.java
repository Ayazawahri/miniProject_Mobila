package viewModel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthybody.R;
import com.google.android.material.button.MaterialButton;

import java.util.List;

import model.Food;

public class ParseJsonAdapter extends RecyclerView.Adapter<ParseJsonAdapter.DataHolder> {
    private final LayoutInflater mInflater;
    private List<Food> foods;
    private ClickListener listener;

    public ParseJsonAdapter(Context context, List<Food> foods, ClickListener listener) {
        this.mInflater = LayoutInflater.from(context);
        this.foods = foods;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DataHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.foodcardviewlayout, parent, false);
        return new DataHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull DataHolder holder, int position) {
        Food food = foods.get(position);
        String foodName = food.getName();
        String fat = food.getFat_total_g();
        String protein = food.getProtein_g();
        String calories = food.getCalories();
        String carbs = food.getCarbs();

        holder.caloriesView.setText("Calories: " + calories + " Kcal");
        holder.foodNameView.setText(foodName);
        holder.fatView.setText("Fat: " + fat + "g");
        holder.proteinView.setText("Protein: " + protein + "g");
        holder.carbsView.setText("Carbs: " + carbs + "g");

        holder.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onAddButtonClicked(foodName, calories, carbs, fat, protein);
            }
        });
    }

    @Override
    public int getItemCount() {
        return foods.size();
    }

    public static class DataHolder extends RecyclerView.ViewHolder {
        private TextView foodNameView, fatView, proteinView, caloriesView, carbsView;
        private MaterialButton addButton;

        public DataHolder(@NonNull View itemView) {
            super(itemView);
            foodNameView = itemView.findViewById(R.id.food_name_text_view);
            caloriesView = itemView.findViewById(R.id.calories_text_view);
            carbsView = itemView.findViewById(R.id.carbs_text_view);
            proteinView = itemView.findViewById(R.id.protein_text_view);
            fatView = itemView.findViewById(R.id.fat_text_view);
            addButton = itemView.findViewById(R.id.add_button);
        }
    }
}
