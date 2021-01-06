package com.example.safetyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
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

        SharedPreferences sharedPreferences = getSharedPreferences("myapp", MODE_PRIVATE);
        String usermobileno = sharedPreferences.getString("mobileno", null);
        //Auto-login code:when user opens app after loggin in i.e second time and so on.
        if(usermobileno
                != null)
        {
            Intent in = new Intent(this, User_Home.class);
            startActivity(in);
            finish();
        }
    }
    public void go1(View v)
    {
        Intent intent = new Intent(this, User_Login.class);
        startActivity(intent);
    }
    public void go2(View v)
    {
        Intent intent = new Intent(this, authenticationScreen.class);
        startActivity(intent);
    }
}