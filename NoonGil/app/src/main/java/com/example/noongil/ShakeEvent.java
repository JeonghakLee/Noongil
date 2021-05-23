package com.example.noongil;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class ShakeEvent implements SensorEventListener {
    private SensorManager   mSensorManager;
    private Context         mContext;
    private Sensor          mAccelerometer;

    private long                ShakeTime = 0;
    private static int          mShakeCount = 0;
    private static final int    SHAKE_SKIP_TIME = 300;
    private static final float  SHAKE_THRESHOLD_GRAVITY = 2.F;

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

                // ignore shake too close to each time 0.3 sec
                if (ShakeTime + SHAKE_SKIP_TIME > curTime) {
                    return;
                }

                ShakeTime = curTime;
                mShakeCount++;
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

    public int getmShakeCount() {
        return mShakeCount;
    }

    public void setmShakeCount() {
        mShakeCount = 0;
    }
}