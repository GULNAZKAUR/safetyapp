package com.example.safetyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Recording_data extends AppCompatActivity {
    ArrayList<String> al;
    DatabaseReference mainref;
    String user_in_emergency_mobileno,current_date;

 myadapter ad;
    ListView lv1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording_data);

        lv1 = findViewById(R.id.lv1);
        al = new ArrayList<>();
        Intent in = getIntent();
        user_in_emergency_mobileno = in.getStringExtra("user_in_emergency_mobileno");
        current_date = in.getStringExtra("current_date");
        Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        mainref = firebaseDatabase.getReference("photodata");
        ad = new myadapter();
        lv1.setAdapter(ad);

        mainref.child(user_in_emergency_mobileno).child(current_date).child("audios").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("MyMSG123",snapshot.toString());
               if(snapshot.exists()){
                    for (DataSnapshot sin : snapshot.getChildren()) {
                        al.add(sin.getValue(String.class));
                    }
                   Toast.makeText(Recording_data.this, ""+al.size(), Toast.LENGTH_SHORT).show();
                    ad.notifyDataSetChanged();
                }
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
            return al.size();
        }

        @Override
        public Object getItem(int position)
        {
            return al.get(position);
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
            convertView = inflater.inflate(R.layout.recordings_design, parent, false);

            // get course object from al
            String obj = al.get(position);

            TextView tv_view_dates = convertView.findViewById(R.id.tv_view_dates);

            tv_view_dates.setText("Recording "+position);


            return convertView;
        }
    }
}