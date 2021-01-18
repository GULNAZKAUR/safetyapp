package com.example.safetyapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
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

import org.w3c.dom.Text;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;


public class ManageFriends extends Fragment {

    TextView tv22;
    Button bt1;
    ListView lv1;
    String usermobileno;
    DatabaseReference mainref;
    ArrayList<String> al;
    ArrayList<user_details> arraylist;
myadapter ad;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_manage_friends, container, false);
ad = new myadapter();
        // Add contacs ==> Intent
        al = new ArrayList<>();
        arraylist = new ArrayList<>();
        lv1 = v.findViewById(R.id.lv1);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("myapp", MODE_PRIVATE);
        usermobileno = sharedPreferences.getString("mobileno", null);


        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        mainref = firebaseDatabase.getReference("users");

         lv1.setAdapter(ad);
//        lv1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
//        lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//            }
//        });
        bt1 = v.findViewById(R.id.bt1);
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), Add_Contacts.class);
                startActivity(intent);


            }
        });
        mainref.child(usermobileno).child("favourite_contacts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               if(snapshot.exists()){
                   al.clear();
                   arraylist.clear();
                   for (DataSnapshot  sin : snapshot.getChildren()){
                       al.add(sin.getValue(String.class));
                   }
//                ad.notifyDataSetChanged();
//                   Toast.makeText(getContext(), "SIZE"+al.size(), Toast.LENGTH_SHORT).show();
                   fetch_userdata();

               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return v;
    }

    // Inner Class //
    class myadapter extends BaseAdapter
    {
        @Override
        public int getCount()
        {
            return arraylist.size();
        }

        @Override
        public Object getItem(int position)
        {
            return arraylist.get(position);
        }

        @Override
        public long getItemId(int position)
        {
            return (position);
        }

        @Override
        public View getView( int position, View convertView, ViewGroup parent)
        {
            // Inflate XML (Single Row) refer it as convertView in Java
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.single_row2, parent, false);

            // get course object from al
            user_details obj = arraylist.get(position);

            TextView tv111 = convertView.findViewById(R.id.tv111);
            ImageView imv111 = convertView.findViewById(R.id.imv111);
            ImageView imv222 = convertView.findViewById(R.id.imv222);
            LinearLayout lvmanage_frds = convertView.findViewById(R.id.lvmanage_frds);
            tv111.setText(obj.getName());
            Picasso.get().load(obj.getUserpic()).into(imv111);
            mainref.child(al.get(position)).child("emergency").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        String abc  = snapshot.getValue(String.class);
                        if(abc.equals("ON")){
                            lvmanage_frds.setBackgroundColor(Color.RED);
                            arraylist.get(position).setEmergency("ON");
                        }
                        else {
                            lvmanage_frds.setBackgroundColor(Color.WHITE);
                            arraylist.get(position).setEmergency("OFF");
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            imv222.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Toast.makeText(getContext(), "Delete", Toast.LENGTH_SHORT).show();
                    al.remove(position);
                    mainref.child(usermobileno).child("favourite_contacts").setValue(al);
                    Toast.makeText(getContext(), "Removed", Toast.LENGTH_SHORT).show();
                }
            });
            lvmanage_frds.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(), ""+obj.getName(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getContext(), ViewUserDetails.class);
                    intent.putExtra("name",obj.getName()+"");
                    intent.putExtra("phone",obj.getPhoneno()+"");
                    intent.putExtra("status",obj.getEmergency()+"");
                    intent.putExtra("photo",obj.getUserpic()+"");

                startActivity(intent);
                }
            });

            return convertView;
        }
    }
    ////////////////
    public void fetch_userdata(){
        // al loop
        for(int i=0;i<al.size();i++){
            //Fetching detailsf each favourite contact
            mainref.child(al.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Log.d("MYMSG",snapshot.toString());
                    //Details of each contact are being fetched from mapping class,i.e, 'user_details'.
                user_details obj = snapshot.getValue(user_details.class);
                arraylist.add(obj);
                ad.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }
}