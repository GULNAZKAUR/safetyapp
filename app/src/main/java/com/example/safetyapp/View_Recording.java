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
import android.widget.GridView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class View_Recording extends AppCompatActivity {
    GridView lv1;
    ArrayList<String> arrayList;
    myadapter ad;
    DatabaseReference mainref;
    String mobile_in_emergency;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view__recording);

        lv1 = findViewById(R.id.lv1);
        arrayList = new ArrayList<>();
        Intent intent1 = getIntent();
       mobile_in_emergency  = intent1.getStringExtra("user_in_emergency");

        ad = new myadapter();
        lv1.setAdapter(ad);
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        mainref = firebaseDatabase.getReference("photodata");

        mainref.child(mobile_in_emergency).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("MyMSG", snapshot.toString());
                for (DataSnapshot sin : snapshot.getChildren()) {
                    arrayList.add(sin.getKey());
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
                Intent intent = new Intent(getApplicationContext(), Recording_data.class);
                intent.putExtra("current_date",arrayList.get(position));
                intent.putExtra("user_in_emergency_mobileno",mobile_in_emergency+"");
                startActivity(intent);
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
            convertView = inflater.inflate(R.layout.single_row_dates_design, parent, false);

            // get course object from al
            String obj = arrayList.get(position);

            TextView tv_view_dates = convertView.findViewById(R.id.tv_view_dates);

            tv_view_dates.setText(obj);


            return convertView;
        }
    }
}