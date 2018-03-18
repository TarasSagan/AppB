package com.bigdig.appbbigdig;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.bigdig.appbbigdig.imageActivity.ImageActivity;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity {
    @BindView(R.id.textClose) TextView textClose;
    @BindView(R.id.textCloseTimer) TextView textCloseTimer;
    private CountDownTimer countDownTimer;
    private final String timeToCloseKEY = "timeToCloseKEY";
    private long timeToClose = 10 * 1000;
    private long timeTickInterval = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        init();
    }

    private void init(){
        countDownTimer = new CountDownTimer(timeToClose, timeTickInterval) {
            @Override
            public void onTick(long millisUntilFinished) {
                textCloseTimer.setText(Long.toString(millisUntilFinished / timeTickInterval));
            }

            @Override
            public void onFinish() {
                finish();
            }
        };
        textClose.setText(getResources().getString(R.string.text_app_close));
        countDownTimer.start();
    }
}
