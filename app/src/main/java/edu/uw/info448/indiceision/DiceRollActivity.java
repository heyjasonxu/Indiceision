package edu.uw.info448.indiceision;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;

import java.util.Random;

public class DiceRollActivity extends AppCompatActivity implements SensorEventListener {

    private static final String TAG = "Main";
    private Long lastUpdate = null;
    private float scaleFactor = .03f;

    private DrawingSurfaceView view;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dice_roll);

        view = (DrawingSurfaceView) findViewById(R.id.drawingView);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        if (mAccelerometer == null) {
            Log.e(TAG, "No accelerometer");
        }
        startSensor();



    }

    @Override
    protected void onResume() {
        Log.v(TAG, "on resume");
        view.started = false;
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.profile:
                startActivity(new Intent(this, Profile.class));
                return true;
            case R.id.sign_out:
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                                Toast.makeText(DiceRollActivity.this, "Signed out", Toast.LENGTH_SHORT).show();
                            }
                        });
                startActivity(new Intent(this, Introduction.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case DrawingSurfaceView.LOCATION_REQUEST_CODE: { //if asked for location
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    view.onConnected(null); //do whatever we'd do when first connecting (try again)
                }
            }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    private void startSensor() {
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void stopSensor() {
        mSensorManager.unregisterListener(this, mAccelerometer);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        //Log.v(TAG, "Raw: " + sensorEvent.sensor + Arrays.toString(sensorEvent.values));

        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float[] gravity = sensorEvent.values;

            long currTime = System.currentTimeMillis();
            if (lastUpdate == null || (currTime - lastUpdate) > 150) {
                lastUpdate = currTime;

                float x = sensorEvent.values[0];
                float y = sensorEvent.values[1];
                float z = sensorEvent.values[2];

                double acceleration = Math.sqrt(Math.pow(x, 2) +
                        Math.pow(y, 2) +
                        Math.pow(z, 2)) - SensorManager.GRAVITY_EARTH;

                //Log.v(TAG, "accel: " + acceleration);

                if (acceleration > 5) {
                    Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    v.vibrate(500);

                    float min = 2000;
                    float max = 10000;

                    Random rand = new Random();

                    float xMagnitude = rand.nextFloat() * (max - min) + min;
                    float yMagnitude = rand.nextFloat() * (max - min) + min;

                    view.dice.dx = -1 * xMagnitude * scaleFactor;
                    view.dice.dy = -1 * yMagnitude * scaleFactor;

                    view.dice.moved = true;

                }

            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {


    }


    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return true; //recommended practice
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {


            //fling!
            Log.v(TAG, "Fling! " + velocityX + ", " + velocityY);
            view.dice.dx = -1 * velocityX * scaleFactor;
            view.dice.dy = -1 * velocityY * scaleFactor;

            return true; //we got this
        }
    }


}

