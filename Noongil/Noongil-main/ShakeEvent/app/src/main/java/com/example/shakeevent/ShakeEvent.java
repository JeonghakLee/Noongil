package com.example.shakeevent;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.Toast;

public class ShakeEvent implements SensorEventListener {
    private SensorManager   mSensorManager;
    private Context         mContext;
    private Sensor          mAccelerometer;

    private long                startTime = 0;
    private long                ShakeTime = 0;
    private static int          mShakeCount = 0;
    private static final int    SHAKE_SKIP_TIME = 300;
    private static final int    SHAKE_RESET_TIME = 2000;
    private static final float  SHAKE_THRESHOLD_GRAVITY = 2.0F;

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float curX = event.values[0];
            float curY = event.values[1];
            float curZ = event.values[2];

            float lastX = curX / SensorManager.GRAVITY_EARTH;
            float lastY = curY / SensorManager.GRAVITY_EARTH;
            float lastZ = curZ / SensorManager.GRAVITY_EARTH;

            // Calc Force
            float force = (float) Math.sqrt((double) lastX * lastX + lastY * lastY + lastZ * lastZ);
            if (force > SHAKE_THRESHOLD_GRAVITY) {
                long curTime = System.currentTimeMillis();
                if (mShakeCount == 0) startTime = curTime;

                // ignore shake too close to each time 0.3 sec
                if (ShakeTime + SHAKE_SKIP_TIME > curTime) {
                    return;
                }

                // Reset time over 2 sec
                if (startTime + SHAKE_RESET_TIME < curTime) {
                    switch (mShakeCount) {
                        case 2:
                            // Camera
                            break;
                        case 3:
                            // OCR
                            break;
                        default:
                            break;
                    }
                    mShakeCount = 0;
                }

                ShakeTime = curTime;
                mShakeCount++;
                print();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    // init variable
    public void create(Context context) {
        mContext = context;
        mSensorManager = (SensorManager) mContext.getSystemService(mContext.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    // Shake start
    public void start() {
        this.mSensorManager.registerListener(this,
                mAccelerometer,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    // Shake end
    public void stop() {
        this.mSensorManager.unregisterListener(this);
    }

    // shakeCount print to use debug
    public void print() {
        Toast.makeText(mContext.getApplicationContext(),
                "Count = " + mShakeCount,
                Toast.LENGTH_SHORT).show();
    }
}