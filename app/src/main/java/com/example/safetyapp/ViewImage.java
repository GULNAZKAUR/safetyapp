package com.example.safetyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class ViewImage extends AppCompatActivity {
Button bt1;
ImageView imv1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);
imv1 = findViewById(R.id.imv1);
        Intent intent1 = getIntent();
        String pic = intent1.getStringExtra("picId");
        Picasso.get().load(pic).into(imv1);
    }
    public void go(View v){
        finish();
    }
}