package com.example.shaketestthread;
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
    private ShakeEvent      mShake;
    private myHandler       mHandler;
    private TextView        txt;
    private Thread          mThread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txt = (TextView) findViewById(R.id.txt);
        txt.setText("0");
        mShake = new ShakeEvent();
        mShake.create(this);
        mHandler = new myHandler();
        mThread = new Thread(new TestThread());
    }

    private class myHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                default:
                    mShake.print(msg.what);
                    Integer txtCount = msg.what;
                    txt.setText(txtCount.toString());
            }
        }
    }

    public class TestThread implements Runnable {
        @Override
        public void run() {
            try {
                while(true) {
                    if (mShake.getmShakeCount() != 0) {
                        Thread.sleep(2000);

                        mHandler.sendEmptyMessage(mShake.getmShakeCount());
                        mShake.setmShakeCount();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mShake.start();
        mThread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mShake.stop();
    }
}