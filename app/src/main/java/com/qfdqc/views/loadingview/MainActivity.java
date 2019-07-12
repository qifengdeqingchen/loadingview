package com.qfdqc.views.loadingview;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ValueAnimator;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    LoadingView loading1;
    LoadingView loading2;
    ValueAnimator valueAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loading1 = findViewById(R.id.loading1);
        loading2 = findViewById(R.id.loading2);

        valueAnimator = ValueAnimator.ofFloat(0, 100);
        valueAnimator.setDuration(2000);
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                loading1.setPercent(animation.getAnimatedFraction());
            }
        });
        loading1.post(new Runnable() {
            @Override
            public void run() {
                valueAnimator.start();
            }
        });
    }

}
