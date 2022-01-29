package com.zemmyang.gyroscopetrackerdemo;



import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.lang.Math;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class MainActivity extends AppCompatActivity {
    private Context context;
    private DBManager dbManager;
    private SensorManager sm = null;
    private TextView textViewDisplay = null;
    final int MAX_DISPLAY = 20;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    SensorEventListener sel = new SensorEventListener() {
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        public void onSensorChanged(SensorEvent event) {
            float[] values = event.values;

            Instant instantNow = Instant.now();
            long msecSinceEpoch = instantNow.toEpochMilli();

            // use nanosecond precision
            // long nanosecondsSinceEpoch = ( instant.getEpochSecond() * 1_000_000_000L ) + instant.getNano() ;

            dbManager.insert(msecSinceEpoch, values);

            PrettifyRecord pr = new PrettifyRecord();
            textViewDisplay.setText(pr.getDisplay(msecSinceEpoch, values));
        }
    };

    public void startRecording(View view) {
        dbManager = new DBManager(this);
        dbManager.open();

        /* Get a SensorManager instance */
        sm = (SensorManager) getSystemService(SENSOR_SERVICE);

        textViewDisplay = (TextView) findViewById(R.id.gyroscope_value);

        if (sm.getSensorList(Sensor.TYPE_GYROSCOPE).size() > 0) {
            Toast.makeText(getBaseContext(), "Starting recording.", Toast.LENGTH_SHORT).show();
            sm.registerListener(sel, (Sensor) sm.getSensorList(Sensor.TYPE_GYROSCOPE).get(0),
                    SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            Toast.makeText(getBaseContext(), "Error: No Gyroscope.", Toast.LENGTH_SHORT).show();
        }

    }

    public void stopRecording(View view) {
        Toast.makeText(getBaseContext(), "Stopping recording.", Toast.LENGTH_SHORT).show();
        if (sm.getSensorList(Sensor.TYPE_GYROSCOPE).size() > 0) {
            sm.unregisterListener(sel);
        }
        String fetched = dbManager.display(MAX_DISPLAY);
        textViewDisplay.setText(fetched);
    }

    public void saveAsCSV(View view) {
        // use current datetime as of saving as the filename
        LocalDateTime myDateObj = LocalDateTime.now();
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm-ss");
        String filename = myDateObj.format(myFormatObj);

        try {
            dbManager.saveAsCSVToCache(filename);
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), "Save failed", Toast.LENGTH_SHORT).show();
            Log.e("MainActivity", e.getMessage(), e);
        } finally {
            Toast.makeText(getBaseContext(), "Saved to CSV.", Toast.LENGTH_SHORT).show();
        }

    }

    public void clearRecording(View view) {
        Toast.makeText(getBaseContext(), "Clearing cache.", Toast.LENGTH_SHORT).show();
        dbManager.deleteAll();
        dbManager.close();
        textViewDisplay.setText("CLEARED");
    }
}

class PrettifyRecord {
    protected String getDisplay(long timeSinceEpoch, float[] rot_values){
        double[] dispRot = {0.0, 0.0, 0.0};
        for (int i = 0; i < rot_values.length; i++) {
            dispRot[i] = Math.round(rot_values[i] * 100.0) / 100.0;
        }

        LocalDateTime humanReadableTime = Instant.ofEpochMilli(timeSinceEpoch)
                .atZone(ZoneId.systemDefault()).toLocalDateTime();

        return humanReadableTime + " |" +
                " x: " + dispRot[0] +
                " y: " + dispRot[1] +
                " z: " + dispRot[2];

    }
}

