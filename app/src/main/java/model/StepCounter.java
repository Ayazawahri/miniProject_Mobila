package model;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class StepCounter implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor stepCounter;

    // variables to store step count and calories burned
    private int stepCount = 0;
    private float caloriesBurned = 0;

    public StepCounter(SensorManager sensorManager) {
        this.sensorManager = sensorManager;
        stepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
    }

    public void start() {
        if (stepCounter != null) {
            sensorManager.registerListener(this, stepCounter, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    public void stop() {
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            stepCount = (int) event.values[0];
            // calculate calories burned based on step count and user's weight
            // this formula is an estimate and may not be accurate
            caloriesBurned = stepCount * 0.04f * 75;  // 75 is the user's weight in kg
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // do nothing
    }

    public int getStepCount() {
        return stepCount;
    }

    public float getCaloriesBurned() {
        return caloriesBurned;
    }
}
