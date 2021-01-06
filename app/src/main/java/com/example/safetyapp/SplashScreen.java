package com.example.safetyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashScreen extends AppCompatActivity {
    ImageView imv1;
    Animation top, buttom;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        imv1 = findViewById(R.id.imv1);
        top = AnimationUtils.loadAnimation(this, R.anim.top);
        buttom = AnimationUtils.loadAnimation(this, R.anim.buttom);
        imv1.setAnimation(buttom);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent in = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(in);
                finish();
            }
        }, 3000);
    }
}