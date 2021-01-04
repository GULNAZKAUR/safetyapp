package com.example.safetyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button bt1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bt1=findViewById(R.id.bt1);
    }
    public void go1(View v)
    {
        Intent intent = new Intent(this, UserSignUp.class);
        startActivity(intent);
    }
    public void go2(View v)
    {
        Intent intent = new Intent(this, authenticationScreen.class);
        startActivity(intent);
    }
}