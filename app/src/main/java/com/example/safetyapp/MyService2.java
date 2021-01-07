package com.example.safetyapp;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MyService2 extends Service {
    public MyService2() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent.getAction().trim().equals("abc")){
            Toast.makeText(this, "Hello from abc", Toast.LENGTH_SHORT).show();
            SharedPreferences sp1 = getSharedPreferences("myapp", MODE_PRIVATE);
            String no = sp1.getString("mobileno", "");
            Toast.makeText(this, "...."+no, Toast.LENGTH_SHORT).show();
//            runningflag = true;
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("users").child(no).child("emergency");
            dbRef.setValue("ON");
        }
        return super.onStartCommand(intent, flags, startId);
    }
}