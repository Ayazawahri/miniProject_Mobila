package model;

public class Food {
    private String name, calories, fat_total_g, protein_g, carbs;

    public Food(String name, String calories, String fat_total_g, String protein_g, String carbs) {
        this.name = name;
        this.calories = calories;
        this.fat_total_g = fat_total_g;
        this.protein_g = protein_g;
        this.carbs = carbs;
    }

    public String getCarbs() {
        return carbs;
    }

    public void setCarbs(String carbs) {
        this.carbs = carbs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCalories() {
        return calories;
    }

    public void setCalories(String calories) {
        this.calories = calories;
    }

    public String getFat_total_g() {
        return fat_total_g;
    }

    public void setFat_total_g(String fat_total_g) {
        this.fat_total_g = fat_total_g;
    }

    public String getProtein_g() {
        return protein_g;
    }

    public void setProtein_g(String protein_g) {
        this.protein_g = protein_g;
    }
}
