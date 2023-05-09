package model;

public class User {
    private String email;
    private float totalCal, carbs, prot, fat, height;
    private int day, week, steps, currentWeight;

    public User(String email) {
        this.email = email;
    }

    public void addToTotalCal(float cal) {
        totalCal += cal;
    }

    public void addToCarbs(float carbs) {
        this.carbs += carbs;
    }

    public void addToProt(float pro) {
        this.prot += pro;
    }

    public void addSteps(int steps) {
        this.steps += steps;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public float getTotalCal() {
        return totalCal;
    }

    public void setTotalCal(float totalCal) {
        this.totalCal = totalCal;
    }

    public float getCarbs() {
        return carbs;
    }

    public void setCarbs(float carbs) {
        this.carbs = carbs;
    }

    public float getProt() {
        return prot;
    }

    public void setProt(float prot) {
        this.prot = prot;
    }

    public float getFat() {
        return fat;
    }

    public int getCurrentWeight() {
        return currentWeight;
    }

    public void setCurrentWeight(int currentWeight) {
        this.currentWeight = currentWeight;
    }

    public void setFat(float fat) {
        this.fat = fat;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }
}
