package com.example.safetyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class ViewUserDetails extends AppCompatActivity {
    TextView tv1,tv2,tv3;
    ImageView imv1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user_details);
        tv1=findViewById(R.id.tv1);
        tv2=findViewById(R.id.tv2);
        tv3=findViewById(R.id.tv3);
        imv1=findViewById(R.id.imv1);
        Intent in  = getIntent();
        String name = in.getStringExtra("name");
        String phone = in.getStringExtra("phone");
        String status = in.getStringExtra("status");
        String photo = in.getStringExtra("photo");
        tv1.setText(name);
        tv2.setText(phone);
        tv3.setText(status);
        Picasso.get().load(photo).into(imv1);

    }
}