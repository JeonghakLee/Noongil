package com.example.shakeevent;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private ShakeEvent mShake = new ShakeEvent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mShake.create(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mShake.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mShake.stop();
    }
}