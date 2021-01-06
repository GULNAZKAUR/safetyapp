package com.example.safetyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ViewPhotos extends AppCompatActivity {
    GridView lv1;
    ArrayList<String> arrayList;
   myadapter ad;
   DatabaseReference mainref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_photos);
        lv1 = findViewById(R.id.lv1);
        arrayList = new ArrayList<>();
        Intent intent1 = getIntent();
       String current_date= intent1.getStringExtra("current_date");
       String mobile_in_emergency = intent1.getStringExtra("user_in_emergency_mobileno");

        ad = new myadapter();
        lv1.setAdapter(ad);
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        mainref = firebaseDatabase.getReference("photodata");

        mainref.child(mobile_in_emergency).child(current_date).child("pics").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("MyMSG",snapshot.toString());
                for (DataSnapshot sin : snapshot.getChildren()) {
                    arrayList.add(sin.getValue(String.class));
                }
                ad.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
     class myadapter extends BaseAdapter
    {
        @Override
        public int getCount()
        {
            return arrayList.size();
        }

        @Override
        public Object getItem(int position)
        {
            return arrayList.get(position);
        }

        @Override
        public long getItemId(int position)
        {
            return (position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            // Inflate XML (Single Row) refer it as convertView in Java
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.rowofphotos, parent, false);

            // get course object from al
            String obj = arrayList.get(position);

            ImageView imv_view_pics = convertView.findViewById(R.id.imv1);

            Picasso.get().load(obj).into(imv_view_pics);


            return convertView;
        }
    }
}