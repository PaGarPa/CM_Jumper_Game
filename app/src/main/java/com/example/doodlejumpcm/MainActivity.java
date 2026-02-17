package com.example.doodlejumpcm;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private VistaJuego vista;
    private SensorManager sm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int x = getResources().getDisplayMetrics().widthPixels;
        int y = getResources().getDisplayMetrics().heightPixels;
        vista = new VistaJuego(this, x, y);
        setContentView(vista);
        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
    }

    @Override
    public void onSensorChanged(SensorEvent e) { vista.mover(e.values[0]); }
    @Override public void onAccuracyChanged(Sensor s, int a) {}

    @Override
    protected void onResume() {
        super.onResume();
        sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
        vista.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        sm.unregisterListener(this);
        vista.pause();
    }
}