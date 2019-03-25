package me.rosuh.countdownview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import java.text.DecimalFormat;

import me.rosuh.library.CountDownView;

public class MainActivity extends AppCompatActivity {

    private TextView mMiniTv;
    private TextView mDaysTv;
    private TextView mMinesTv;
    private TextView mHourTv;
    private TextView mSecTv;
    private DecimalFormat decimalFormat = new DecimalFormat("00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final CountDownView countDownView = findViewById(R.id.count_down);
        countDownView.buildTimer(10000);
        countDownView.start();
        findViewById(R.id.button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                countDownView.cancel();
                countDownView.buildTimer(5462134);
                countDownView.start();
            }
        });
    }
}
