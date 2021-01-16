package com.example.safetyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class ViewUserDetails extends AppCompatActivity {
    TextView tv1,tv2,tv3;
    ImageView imv1;
    String phone,photo,name;
    Button btn_view_user_picss;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user_details);
        tv1=findViewById(R.id.tv1);
        tv2=findViewById(R.id.tv2);
        tv3=findViewById(R.id.tv3);
        imv1=findViewById(R.id.imv1);
        btn_view_user_picss=findViewById(R.id.btn_view_user_picss);
        Intent in  = getIntent();
         name = in.getStringExtra("name");
       phone  = in.getStringExtra("phone");
        String status = in.getStringExtra("status");
         photo = in.getStringExtra("photo");
        tv1.setText(name);
        tv2.setText(phone);
        tv3.setText(status);
        Picasso.get().load(photo).into(imv1);
        btn_view_user_picss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),DisplayPhotos.class);
                intent.putExtra("user_in_emergency",phone);
                startActivity(intent);
            }
        });

    }
//    public void go(View v)
//    {
//        Intent intent = new Intent(this,DisplayPhotos.class);
//        intent.putExtra("user_in_emergency",phone);
//        startActivity(intent);
//
//    }
    public void go1(View v)
    {
//        Intent intent  = new Intent(this,View_Recording.class);
//        intent.putExtra("user_in_emergency", phone);
//        startActivity(intent);

        Intent intent  = new Intent(this,MapsActivity.class);
        intent.putExtra("mobile",phone+"");
        intent.putExtra("pic",photo+"");
        intent.putExtra("name",name+"");
        startActivity(intent);


    }
    public void OpenDialer(View v)
    {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        Uri uri = Uri.parse("tel:"+ phone);
        intent.setData(uri);
        startActivity(intent);
    }


}