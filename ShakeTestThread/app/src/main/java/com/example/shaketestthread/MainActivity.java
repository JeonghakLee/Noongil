package com.example.shaketestthread;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
    private ShakeE      mShake;
    private myHandler   mHandler;
    private boolean     TimeSave = true;
    private TextView    txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txt = (TextView) findViewById(R.id.txt);
        txt.setText("0");
        mShake = new ShakeE();
        mShake.create();
        mHandler = new myHandler();
    }

    private class myHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                default:
                    mShake.print(msg.what);
                    mShake.displayCount(msg.what);
            }
        }
    }

    public class TestThread implements Runnable {
        @Override
        public void run() {
            try {
                Thread.sleep(2000);

                mHandler.sendEmptyMessage(mShake.getmShakeCount());
                mShake.setmShakeCount();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class ShakeE implements SensorEventListener {
        private SensorManager   mSensorManager;
        private Sensor          mAccelerometer;

        private long                ShakeTime = 0;
        private int                 mShakeCount = 0;
        private static final int    SHAKE_SKIP_TIME = 500;
        private static final float  SHAKE_THRESHOLD_GRAVITY = 2.5F;

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
                    // ignore shake too close to each time 0.3 sec
                    long curTime = System.currentTimeMillis();
                    if (ShakeTime + SHAKE_SKIP_TIME > curTime) {
                        return;
                    }

                    if (TimeSave) {
                        TimeSave = false;
                        Thread t = new Thread(new TestThread());
                        t.start();
                    }

                    mShakeCount++;
                    displayCount(mShakeCount);
                }
            }
        }

        public void displayCount(int c) {
            Integer txtCount = c;
            txt.setText(txtCount.toString());
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {}

        public void create() {
            mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        public void start() {
            this.mSensorManager.registerListener(this,
                    mAccelerometer,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }

        public void stop() {
            this.mSensorManager.unregisterListener(this);
        }

        public void print(int c) {
            Toast.makeText(getApplicationContext(),
                    "Count = " + c,
                    Toast.LENGTH_SHORT).show();
        }

        public int getmShakeCount() {
            return mShakeCount;
        }

        public void setmShakeCount() {
            mShakeCount = 0;
            TimeSave = true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mShake.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mShake.stop();
    }
}