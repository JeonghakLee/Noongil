package com.example.shaketestthread;
import androidx.appcompat.app.AppCompatActivity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;


public class MainActivity extends AppCompatActivity {
    private ShakeE mShake = new ShakeE();
    private TextView txt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txt = (TextView) findViewById(R.id.txt);
        txt.setText("0");
        mShake.create();
    }

    private class ShakeE extends AsyncTask<Void, Void, Integer> implements SensorEventListener {
        private SensorManager   mSensorManager;
        private Sensor          mAccelerometer;

        private long                ShakeTime = 0;
        private long                preTime;
        private long                curTime;
        private int                 mShakeCount = 0;
        private boolean             TimeSave = true;
        private static final int    SHAKE_SKIP_TIME = 300;
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
                    // ignore shake too close to each time 0.3 sec
                    long curTime = System.currentTimeMillis();
                    if (ShakeTime + SHAKE_SKIP_TIME > curTime) {
                        return;
                    }

                    if (TimeSave) {
                        TimeSave = false;
                        mShake.execute();
                    }

                    mShakeCount++;
                    Integer txtCount = mShakeCount;
                    txt.setText(txtCount.toString());
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }

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

        public void print() {
            Toast.makeText(getApplicationContext(),
                    "Count = " + mShakeCount,
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            curTime = System.currentTimeMillis();
            preTime = System.currentTimeMillis();

            while (curTime - preTime < 2000) {
                curTime = System.currentTimeMillis();
            }

            return mShakeCount;
        }

        @Override
        protected void onPostExecute(Integer camera) {
            super.onPostExecute(camera);
            TimeSave = true;
            mShakeCount = 0;
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