package com.example.safetyapp;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

//    private GoogleMap mMap;
    private GoogleMap mMap;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference mainref;
    String no;
    String phone,pic,name;
    LatLng mymarker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
//        if (android.os.Build.VERSION.SDK_INT > 9) {
//            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//            StrictMode.setThreadPolicy(policy);
//        }
        Intent intent = getIntent();
        phone = intent.getStringExtra("mobile");
        pic = intent.getStringExtra("pic");
        name = intent.getStringExtra("name");
        Log.d("mobile: ", phone+"....."+pic);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.clear();

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(56.1304, 106.3468);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("users").child(phone).child("Location");
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

              if(dataSnapshot.exists()){
                  MyLocation myLocation = dataSnapshot.getValue(MyLocation.class);
                  mymarker = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                  Log.d("Latitude: ", myLocation.getLatitude()+"");

                  //My Marker is used to show user's latitude and longitude location marker on google maps

                  MarkerOptions markerOptions=new MarkerOptions().position(mymarker).title(name).draggable(true);
                  mMap.addMarker(markerOptions);
                  //  mMap.animateCamera(CameraUpdateFactory.newLatLng(mymarker));
                  mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mymarker,19));
//fetch location fundtion does real time location update of user's current location onto map
                  new Thread(new fetchLocation()).start();
              }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    public class fetchLocation implements Runnable{

        @Override
        public void run() {

            firebaseDatabase = FirebaseDatabase.getInstance();
            mainref = firebaseDatabase.getReference("users");

            final DatabaseReference LocRef = mainref.child(phone).child("Location");

            Log.d("MYMSG: ","phone"+phone);

            LocRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    final MyLocation location = dataSnapshot.getValue(MyLocation.class);


//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Log.d("MYMSG: ","Latitude: "+location.getLatitude()+" Longitude: "+location.getLongitude());
//                        }
//                    });
                    final double lat =  location.getLatitude();
                    final double lon = location.getLongitude();



//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Log.d("MYMSG: ","Latitude: "+lat+" Longitude: "+lon);
//                        }
//                    });

                    if(mMap!=null){
                        mMap.clear();
                    }

                    LatLng mymarker=new LatLng(lat,lon);
                    //This is used to update user  emergency's current location on map
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MarkerOptions markerOptions=new MarkerOptions().position(mymarker).title(name+" is in Emergency").draggable(true)
                                    .flat(true)

                                    ;
                            mMap.addMarker(markerOptions);
                            //  mMap.animateCamera(CameraUpdateFactory.newLatLng(mymarker));
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mymarker,19));
                        }
                    });



                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }
//    public Bitmap getBitmapFromLink(String link) {
//        try {
//            URL url = new URL(link);
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            try {
//                connection.connect();
//            } catch (Exception e) {
//                Log.v("asfwqeds", e.getMessage());
//            }
//            InputStream input = connection.getInputStream();
//            Bitmap myBitmap = BitmapFactory.decodeStream(input);
//            Bitmap resizedBitmap = Bitmap.createScaledBitmap(myBitmap, 100, 100, false);
//            return resizedBitmap;
//        } catch (IOException e) {
//            Log.v("asfwqeds", e.getMessage());
//            e.printStackTrace();
//            return null;
//        }
//    }

}