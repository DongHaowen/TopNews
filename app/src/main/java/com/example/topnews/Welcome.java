package com.example.topnews;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

public class Welcome extends Activity {
    private AlphaAnimation startAnimation;
    View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = View.inflate(this, R.layout.welcome, null);
        setContentView(view);
        initView();
        initData();
    }

    private void initData() {
        startAnimation = new AlphaAnimation(0.3f, 1.0f);
        startAnimation.setDuration(500);
        view.startAnimation(startAnimation);
        startAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                redirectTo();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void initView() {

    }

    private void redirectTo() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }
}
