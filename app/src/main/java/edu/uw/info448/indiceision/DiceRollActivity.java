package edu.uw.info448.indiceision;

import android.animation.AnimatorSet;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

import java.util.Random;

public class DiceRollActivity extends AppCompatActivity implements SensorEventListener {

    private static final String TAG = "Main";
    private Long lastUpdate = null;
    private float scaleFactor = .03f;

    private DrawingSurfaceView view;

    private AnimatorSet radiusAnim;

    private GestureDetectorCompat mDetector;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dice_roll);

        view = (DrawingSurfaceView) findViewById(R.id.drawingView);

        mDetector = new GestureDetectorCompat(this, new MyGestureListener());

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        if (mAccelerometer == null) {
            Log.e(TAG, "No accelerometer");
        }
        startSensor();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //Log.v(TAG, event.toString());

        boolean gesture = mDetector.onTouchEvent(event);

        float x = event.getX();
        float y = event.getY() - getSupportActionBar().getHeight(); //closer to center...

        int action = event.getActionMasked();
        return super.onTouchEvent(event);
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

                    float min = 2000;
                    float max = 10000;

                    Random rand = new Random();

                    float xMagnitude = rand.nextFloat() * (max - min) + min;
                    float yMagnitude = rand.nextFloat() * (max - min) + min;

                    view.ball.dx = -1 * xMagnitude * scaleFactor;
                    view.ball.dy = -1 * yMagnitude * scaleFactor;

                    view.ball.moved = true;

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
            view.ball.dx = -1 * velocityX * scaleFactor;
            view.ball.dy = -1 * velocityY * scaleFactor;

            return true; //we got this
        }
    }
}

