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
import java.util.List;

import model.Food;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.DataHolder>{
    private final LayoutInflater mInflater;
    private final List<Food> foods;

    public ListAdapter(Context context, List<Food> foods) {
        this.mInflater = LayoutInflater.from(context);
        this.foods = foods;
    }

    @NonNull
    @Override
    public ListAdapter.DataHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.tableitemlayout, parent, false);
        return new ListAdapter.DataHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ListAdapter.DataHolder holder, int position) {
        Food food= foods.get(position);
        holder.foodNameView.setText(food.getName());
        holder.fatView.setText(food.getFat_total_g()+" g");
        holder.carbsView.setText(food.getCarbs()+" g");
        holder.proteinView.setText(food.getProtein_g()+" g");
        holder.caloriesView.setText(food.getCalories()+" Kcal");
    }

    @Override
    public int getItemCount() {
        return foods.size();
    }

    public static class DataHolder extends RecyclerView.ViewHolder {
        private TextView foodNameView, fatView, proteinView, caloriesView, carbsView;

        public DataHolder(@NonNull View itemView) {
            super(itemView);
            foodNameView = itemView.findViewById(R.id.foodList);
            caloriesView = itemView.findViewById(R.id.caloriesList);
            carbsView = itemView.findViewById(R.id.carbsList);
            proteinView = itemView.findViewById(R.id.proteinList);
            fatView = itemView.findViewById(R.id.fatList);
        }
    }
}
