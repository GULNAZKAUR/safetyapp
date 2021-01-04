package com.example.safetyapp;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.content.Context.MODE_PRIVATE;


public class Profile extends Fragment {
EditText et1,et2,et3,et4;
Button bt1;
String usermobileno;
DatabaseReference mainref;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_profile, container, false);
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        et1=v.findViewById(R.id.et1);
        et2=v.findViewById(R.id.et2);
        et3=v.findViewById(R.id.et3);
        et4=v.findViewById(R.id.et4);
        bt1=v.findViewById(R.id.bt1);


        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("myapp", MODE_PRIVATE);
        usermobileno = sharedPreferences.getString("mobileno", null);
et1.setText(usermobileno);
et1.setEnabled(false);
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        mainref = firebaseDatabase.getReference("users");
       bt1.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               String oldPassword = et2.getText().toString();
               String newPassword = et3.getText().toString();
               String confirmPassword = et3.getText().toString();
               if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                   Toast.makeText(getContext(), "All fields are mandatory", Toast.LENGTH_SHORT).show();
               } else if (!newPassword.equals(confirmPassword)) {

               } else {
                   mainref.child(usermobileno).addListenerForSingleValueEvent(new ValueEventListener() {
                       @Override
                       public void onDataChange(@NonNull DataSnapshot snapshot) {
                           user_details obj = snapshot.getValue(user_details.class);
                           if (obj.getPassword().equals(oldPassword)) {
                           mainref.child(usermobileno).child("password").setValue(newPassword);
                               Toast.makeText(getContext(), "Password changed", Toast.LENGTH_SHORT).show();
                           }
                           else {
                               Toast.makeText(getContext(), "old", Toast.LENGTH_SHORT).show();

                           }

                       }

                       @Override
                       public void onCancelled(@NonNull DatabaseError error) {

                       }
                   });
               }
           }
       });
        return v;
    }

}