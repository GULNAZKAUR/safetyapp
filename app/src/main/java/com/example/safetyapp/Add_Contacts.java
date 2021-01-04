package com.example.safetyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Add_Contacts extends AppCompatActivity {
    EditText et1;
    Button bt1;
    ArrayList<String> al;
    DatabaseReference mainref;
    String usermobileno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__contacts);
        et1=findViewById(R.id.et1);
        bt1=findViewById(R.id.bt1);
        al=new ArrayList<>();
        SharedPreferences sharedPreferences = getSharedPreferences("myapp", MODE_PRIVATE);
        usermobileno = sharedPreferences.getString("mobileno", null);


        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        mainref = firebaseDatabase.getReference("users");
        mainref.child(usermobileno).child("favourite_contacts").addValueEventListener(new ValueEventListener() {
            @Override
            //To retain previous entries of favourite contacts in Firebase.
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                al.clear();
                for (DataSnapshot  sin : snapshot.getChildren()){
                    al.add(sin.getValue(String.class));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    String phoneno;
    public void addnumber(View v)
    {
       phoneno  = et1.getText().toString();
        phoneno = "+1"+phoneno;
        if(phoneno.isEmpty())
        {
            Toast.makeText(this, "Phone number is mandatory", Toast.LENGTH_SHORT).show();
        }
        else
        {
            mainref.child(phoneno).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                   if(snapshot.exists()){
                       al.add(phoneno);
                       mainref.child(usermobileno).child("favourite_contacts").setValue(al);
                       finish();
                   }else{
                       Toast.makeText(Add_Contacts.this, "User Not Signup yet", Toast.LENGTH_SHORT).show();
                   }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });




        }
    }
}