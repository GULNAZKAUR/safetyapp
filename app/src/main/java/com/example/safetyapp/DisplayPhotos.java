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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DisplayPhotos extends AppCompatActivity {
    ListView lv1;
    ArrayList<String> al;
    DatabaseReference mainref;
    String user_in_emergency_mobileno;
myadapter ad;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_photos);
        lv1 = findViewById(R.id.lv1);
al = new ArrayList<>();
        Intent in = getIntent();
        user_in_emergency_mobileno = in.getStringExtra("user_in_emergency");

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        mainref = firebaseDatabase.getReference("photodata");
        ad = new myadapter();
        lv1.setAdapter(ad);
        mainref.child(user_in_emergency_mobileno).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("MyMSG",snapshot.toString());
                for (DataSnapshot sin : snapshot.getChildren()) {
                    al.add(sin.getKey());
                }
                ad.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(getApplicationContext(), ViewPhotos.class);
            intent.putExtra("current_date",al.get(position));
            intent.putExtra("user_in_emergency_mobileno",user_in_emergency_mobileno);
            startActivity(intent);
            }
        });
    }
    // Inner Class //
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
            convertView = inflater.inflate(R.layout.single_row_dates_design, parent, false);

            // get course object from al
            String obj = al.get(position);

            TextView tv_view_dates = convertView.findViewById(R.id.tv_view_dates);

            tv_view_dates.setText(obj);


            return convertView;
        }
    }
    ////////////////

}