package com.example.safetyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

public class User_Login extends AppCompatActivity {
    EditText et1,et2;
    Button bt1;
    DatabaseReference mainref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user__login);
        et1=findViewById(R.id.et1);
        et2=findViewById(R.id.et2);
        bt1=findViewById(R.id.bt1);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        mainref = firebaseDatabase.getReference("users");
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usermob  = "+1"+et1.getText().toString();
                String password  = et2.getText().toString();
                if(usermob.isEmpty()||password.isEmpty())
                {
                    Toast.makeText(User_Login.this, "All fields are mandatory", Toast.LENGTH_SHORT).show();
                }
                else{

                    mainref.child(usermob).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                user_details obj = snapshot.getValue(user_details.class);
                                if(obj.getPassword().equals(password)){
//                                    Toast.makeText(User_Login.this,"User Found",Toast.LENGTH_SHORT).show();
                                    SharedPreferences sharedPreferences = getSharedPreferences("myapp", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("mobileno", usermob);
                                    editor.apply();

                                    Toast.makeText(getApplicationContext(), " Success", Toast.LENGTH_SHORT).show();

                                    Intent in = new Intent(getApplicationContext(),User_Home.class);
                                    startActivity(in);
                                    finish();

                                }else {
                                    Toast.makeText(User_Login.this,"Invalid Details",Toast.LENGTH_SHORT).show();

                                }


                            }else {
                                Toast.makeText(User_Login.this, "User does not exist", Toast.LENGTH_SHORT).show();
                            }
                        }


                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });
    }
}