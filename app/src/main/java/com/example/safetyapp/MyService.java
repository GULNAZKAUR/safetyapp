package com.example.safetyapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MyService extends Service {
    boolean runningflag;
    boolean flag, stoppingFlag;
    //a variable to control the camera
    private Camera mCamera;
    //the camera parameters
    private Camera.Parameters parameters;
    SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
    String date = df.format(Calendar.getInstance().getTime());
    File[] listFiles;
    StorageReference photoRef;
    int i = 0;
    String no;
    Thread t;
    File[] listAudioFiles;
    double lat = 0, lon = 0;
    DatabaseReference fav_ref, token_ref;

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("MYMSG",intent.getAction());
        if (intent.getAction().trim().equals("EMERGENCY SITUATION") && runningflag == false) {
            try {
                SharedPreferences sp1 = getSharedPreferences("myapp", MODE_PRIVATE);
                String no = sp1.getString("mobileno", "");
//                Toast.makeText(this, "...."+no, Toast.LENGTH_SHORT).show();
                runningflag = true;
                // The following line will put entry in firebase showing Emergency:ON
                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("users").child(no).child("emergency");
                dbRef.setValue("ON");
                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                fav_ref = firebaseDatabase.getReference("users").child(no).child("fav_contacts");
                token_ref = firebaseDatabase.getReference("tokenrecords");
                fav_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot sin : snapshot.getChildren()) {
                                // Toast.makeText(MyService.this, ""+sin.getValue(String.class), Toast.LENGTH_SHORT).show();
                                Log.d("MYMESSAGE", "RESPONSE " + sin.getValue(String.class));
                                token_ref.child(sin.getValue(String.class)).child("devicetoken").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                        if (snapshot1.exists()) {
                                            String device_token = snapshot1.getValue(String.class);
                                            // Toast.makeText(MyService.this, ""+device_token, Toast.LENGTH_SHORT).show();
                                            Log.d("MYMESSAGE", "RESPONSE " + device_token);

//                                        gettoken(sin.getValue(String.class),"Hello This is app notification + "+sin.getValue(String.class));
                                            SendNotification("Your Friend is in Emergency. " + no, device_token);
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                //record audio
                new Thread(new AudioRecord()).start();
                //Toast.makeText(this, "Recording Audio!!", Toast.LENGTH_SHORT).show();
                flag = true;
                //camera pictures
                new Thread(new ClickPictures()).start();
                //send locations
                startNetwork(); // This function finds user's actual location and uploads on firebase.
                SharedPreferences sharedpreferences = getSharedPreferences("myapp", Context.MODE_PRIVATE);
                String user_mobile = sharedpreferences.getString("mobileno", "");

                SharedPreferences sp = getSharedPreferences("myapp", MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("Emergency", "ON"); // This will locally store that Emergency has been On.

                editor.putString("lockStatus", "lock");
                editor.commit();

                editor.commit();

                Notification mynotif = simpleNotification("Danger", "Emergency Situation Running", false);

                startForeground(1, mynotif);
            } catch (Exception e) {
//                e.printStackTrace();
                Toast.makeText(this, "" + e, Toast.LENGTH_SHORT).show();
            }

        }
        else if (intent.getAction().trim().equals("STOP EMERGENCY SITUATION")) {

            try {
                SharedPreferences sp = getSharedPreferences("myapp", MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("Emergency", "OFF");
                editor.commit();
                String no = sp.getString("mobileno", "");
                Log.d("MYMSG: ", no);

                DatabaseReference dbRefer = FirebaseDatabase.getInstance().getReference("users").child(no).child("emergency");
                dbRefer.setValue("OFF");

                runningflag = false;

                flag = false;
                stoppingFlag = false;

                stopForegroundService(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    //This will build the notification message in notification area.
    public Notification simpleNotification(String title, String message, boolean sound) {
        String CHANNEL_ID = "CHANNEL222"; // This assigns a unique ID to notification.

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);

        builder.setContentTitle(title);
        builder.setContentText(message);
        builder.setSmallIcon(R.drawable.bell_icon);
        builder.setContentInfo("Con Info");

        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.bell_icon);
        builder.setLargeIcon(bmp);

        // We can Specify Activity to be launched here
        Intent in = new Intent(this, MainActivity.class);
        PendingIntent pin = PendingIntent.getActivity(this, 0, in, 0);
        builder.setContentIntent(pin);

        // Auto Cancel Notification after click (to launch activity)
        builder.setAutoCancel(true);

        // For Permanent Notification
        //builder.setOngoing(true);


        // EXTRA Code needed (for devcies < 8.0), since we are creating channels
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);


        NotificationManager notificationManager = (NotificationManager) (getSystemService(NOTIFICATION_SERVICE));


        Notification notification = builder.build();

        //////////// EXTRA CODE  to Handle Oreo Devices   ///////////
        ////// Since Oreo Devices uses Notification Channels    /////

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library

            CharSequence name = "My Channel Name";
            String description = "My Channel Description";
            int importance = NotificationManager.IMPORTANCE_NONE;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);


            // Register the channel with the system

            notificationManager.createNotificationChannel(channel);

            Log.d("MYMESSAGE", "NEW CODE Oreo");
        }

        // DONT Notify here
        // It will be done by ForegroundService
        //notificationManager.notify(20,notification);

        //notificationManager.cancel(20);

        return notification;
    }

    void stopForegroundService(Intent intent) {
        Log.d("MYMESSAGE", "STOP Service Called");
        SharedPreferences sp = getSharedPreferences("myapp", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("Emergency", "OFF");
        editor.commit();
        stopSelf();
        stopService(intent);

    }

    public class ClickPictures implements Runnable {

        @Override
        public void run() {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (flag) {
                Log.d("MYMSG: ", stoppingFlag + "");

                if (stoppingFlag == false) {
                    mCamera = Camera.open(0);
                    final SurfaceTexture surfaceTexture = new SurfaceTexture(10);

                    try {

                        Log.d("Camera Handler", Calendar.getInstance().getTime().toString());

                        mCamera.setPreviewTexture(surfaceTexture);

                        parameters = mCamera.getParameters();

                        //set camera parameters
                        mCamera.setParameters(parameters);
                        mCamera.startPreview();

                        mCamera.takePicture(null, null, mCall);

                        stoppingFlag = true;

                        //tells Android that this surface will have its data constantly replaced
//                    sHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {

                    mCamera = Camera.open(1);
                    final SurfaceTexture surfaceTexture = new SurfaceTexture(10);

                    try {

                        Log.d("Camera Handler", Calendar.getInstance().getTime().toString());

                        mCamera.setPreviewTexture(surfaceTexture);

                        parameters = mCamera.getParameters();

                        //set camera parameters
                        mCamera.setParameters(parameters);
                        mCamera.startPreview();

                        mCamera.takePicture(null, null, mCall);


                        stoppingFlag = false;
                        //tells Android that this surface will have its data constantly replaced
//                    sHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    Camera.PictureCallback mCall = new Camera.PictureCallback() {

        public void onPictureTaken(byte[] data, Camera camera) {
            //decode the data obtained by the camera into a Bitmap

            FileOutputStream outStream = null;
            try {
// this will store images in phone's local storage
                String dir = "/sdcard/SafetyApp/" + date + "/Pics/";
                File f = new File(dir);
                f.mkdirs();
                String path = dir + System.currentTimeMillis();
                outStream = new FileOutputStream(path + ".jpeg");
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                Bitmap compressedBitmap = Bitmap.createScaledBitmap(bitmap, 1080, 1080, true);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                compressedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                outStream.write(byteArray);
                outStream.close();
//                Toast.makeText(MyService.this, "Photo Saved", Toast.LENGTH_SHORT).show();

                File directory = new File("/sdcard/SafetyApp/" + date + "/" + "Pics");
                // check for directory
                if (directory.isDirectory()) {
                    // getting list of file paths
                    listFiles = directory.listFiles();
                    // Check for count
                    if (listFiles.length > 0) {
                        Log.d("MYMSG", "FIle length > 0 " + listFiles.length);
                        if (runningflag) {
                            savePhotos(new File(path + ".jpeg"));
                        }
                    }
                } else {
                    // image directory is empty
//                    Toast.makeText(MyService.this, "Folder is empty. Please load some images in it !", Toast.LENGTH_LONG).show();
                }

                Log.d("Camera Callback", Calendar.getInstance().getTime().toString());

                mCamera.release();
                //Thread.sleep(3000);
                new Thread(new ClickPictures()).start();

            } catch (FileNotFoundException e) {
                Log.d("CAMERA", e.getMessage());
            } catch (IOException e) {
                Log.d("CAMERA", e.getMessage());
            }
        }
    };
// This will now upload images from phone's local storage to Firebase
    void savePhotos(File f) {

        Uri uri = Uri.fromFile(f);
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        photoRef = firebaseStorage.getReference();
        final StorageReference myfile = photoRef.child(date + "/Pics/" + f.getName());
        UploadTask myut = myfile.putFile(uri);
        myut.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                i++;
                Log.d("msg----", "uplaoded");
                Log.d("msg----", "adding url");
                final Task<Uri> url = myfile.getDownloadUrl();
                Log.d("msg----", "adding url");
                url.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String url = uri.toString();
                        Log.d("MYMSGURL: ", url);
                        SharedPreferences sp = getSharedPreferences("myapp", MODE_PRIVATE);
                        no = sp.getString("mobileno", "");
                        Log.d("PhoneNo: ", no);

                        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("photodata").child(no).child(date).child("pics");
                        dbRef.push().setValue(url).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d("MYMSG: ", "File added to Database" + task.isSuccessful());
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("MYMSG: ", e.getMessage());
                            }
                        });

                        if (i < listFiles.length) {
//                            savePhotos(listFiles[i]);
                        }
                    }
                });
                url.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
//                savePhotos(listFiles[i]);
            }
        });

    }

    class AudioRecord implements Runnable {
        @Override
        public void run() {
            while (runningflag) {

                MediaRecorder mediaRecorder;
                mediaRecorder = new MediaRecorder();
                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

                String dir = "/sdcard/SafetyApp/" + date + "/" + "Audio/";
                File f = new File(dir);
                f.mkdirs();

                String path = dir + "/" + System.currentTimeMillis();
                // This will sava the recorded audio at root folder of your device with (recordtest.3gp) name
                mediaRecorder.setOutputFile(path + ".3gp");
                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                Log.d("RecordingAudio: ", "Audio recorded");

                try {
                    mediaRecorder.prepare();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                mediaRecorder.start();

                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {

                    Log.d("MYMSG: ", e.getMessage());
                }

                mediaRecorder.stop();
                mediaRecorder.reset();
                mediaRecorder.release();
                mediaRecorder = null;

                File direc = new File("/sdcard/SafetyApp/" + date + "/" + "Audio"); // Retrieving audio files from local memory
                // check for directory
                if (direc.isDirectory()) {
                    // getting list of file paths
                    listAudioFiles = direc.listFiles();
                    // Check for count
                    if (listAudioFiles.length > 0) {
                        Log.d("MYMSG", "FIle length > 0 " + listAudioFiles.length);
                        saveAudios(new File(path + ".3gp"));
                    }
                } else {
                    // image directory is empty
//                    Toast.makeText(
//                            MyService.this, "Folder is empty. Please load some images in it !",
//                            Toast.LENGTH_LONG).show();
                }

            }
        }
    }

    void saveAudios(File f) {
        Uri uri = Uri.fromFile(f);
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        photoRef = firebaseStorage.getReference();
        final StorageReference myfile = photoRef.child(date + "/Audios/" + f.getName());
        UploadTask myut = myfile.putFile(uri);
        myut.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                i++;
                Log.d("msg----", "uplaoded");
                Log.d("msg----", "adding url");
                final Task<Uri> url = myfile.getDownloadUrl();
                Log.d("msg----", "adding url");
                url.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String url = uri.toString();
                        Log.d("MYMSGURL: ", url);
//
                        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("photodata").child(no).child(date).child("audios");
                        dbRef.push().setValue(url).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d("MYMSG: ", "File added to Database" + task.isSuccessful());
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("MYMSG: ", e.getMessage());
                            }
                        });

                        if (i < listAudioFiles.length) {
//                            saveAudios(listAudioFiles[i]);
                        }
                    }
                });
                url.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
//                saveAudios(listAudioFiles[i]);
            }
        });

    }


    //locations
    public void startNetwork() {


        LocationManager lm = (LocationManager) getSystemService(getApplicationContext().LOCATION_SERVICE);

        Location lastlcgps = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Location lastlcnw = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if (lastlcgps == null) {
            Log.d("LASTGPS", "last gps not available");
        } else {
            Log.d("LASTGPS1", "last gps location available" + lastlcgps.getLatitude() + "," + lastlcgps.getLongitude());
        }
        if (lastlcnw == null) {
            Log.d("LASTNW", "last nw location not available");
        } else {
            Log.d("LASTNW1", "last nw location " + lastlcnw.getLatitude() + "," + lastlcnw.getLongitude());
        }
        //check if GPPS_PROVIDER is enabled
        boolean gpsstatus = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);

        //check if NETWORK_PROVIDER os enavbled
        boolean networkstatus = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        //int flag=0;
        //check which provider is enabled
        mylocationlistener ml = new mylocationlistener();
        if (gpsstatus == false && networkstatus == false) {
//            Toast.makeText(MyService.this, "Both GPS and Network are enabled", Toast.LENGTH_SHORT).show();
            Intent in = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(in);
        }
        if (gpsstatus == true) {
//            Toast.makeText(MyService.this, "GPS is enabled ,using it", Toast.LENGTH_SHORT).show();
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ml);
        }
        if (networkstatus == true) {
//            Toast.makeText(MyService.this, "Network location is enabled,using it", Toast.LENGTH_SHORT).show();
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, ml);
        }

    }


    public class mylocationlistener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {


            lat = location.getLatitude();
            lon = location.getLongitude();
//33 37
            t = new Thread(new myjob(lat, lon));
            t.start();
//            Toast.makeText(MyService.this, "Latitude: " + lat + " Longitude: " + lon, Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    }
    //// Inner Class ////
    // This Thread class is used to get Latitude and longitude values from Location Listener above and then put them onto firebase.
    class myjob implements Runnable {

        double latitude, longitude;//33 37

        //33 37
        myjob(double latitude, double longitude) {

            this.latitude = latitude;
            this.longitude = longitude;
        }

        @Override
        public void run() {

            SharedPreferences sp = getSharedPreferences("myapp", MODE_PRIVATE);
            no = sp.getString("mobileno", "");
            Log.d("PhoneNo: ", no);

            DatabaseReference db = FirebaseDatabase.getInstance().getReference("users").child(no).child("Location");
            MyLocation myLocation = new MyLocation(latitude, longitude);
            db.setValue(myLocation);

            Log.d("MYMSG: ", "Location added!!");
            //Thread Syntax
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//
//                }
//            }).start();
        }

    }

    private void SendNotification(final String message, final String devicetoken) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        final String cloudserverip = "server1.vmm.education";

//        final String serverkey = "AAAAtmftrxQ:APA91bEWv8Q6J8nWAhd5xXZuTPjwCtwrr3YOpJWo6QXjf9Z_mFCbVPyb0gEtwuLdMarQs_RN0_nNGkCKfNad7p4GQlUmr03931QKxv2dBzs8tFWRxjrDZJdqZQyIsPbFm02yEKPg5v5R\n";
        final String serverkey = "AAAAqv7ge7c:APA91bF1-EZGgeCMFeS47WCA46IeQKKqrA4nOcF8G7b6--Weec71ZD3rlzW8iBqDYLJ6roECazO22npCk-HsW9KgdqNshVnIUDemBOF0PB054NFaQmnvqmMfxe_0nV5WJ7fz7ml-YbCD";
        String url = "http://" + cloudserverip + "/VMMCloudMessaging/SendSimpleNotificationUsingTokens";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("MYMESSAGE", "RESPONSE " + response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("MYMESSAGE", error.toString());
                    }
                }) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put("serverkey", serverkey);
                MyData.put("tokens", devicetoken);
                MyData.put("title", "Emergency");
                MyData.put("message", message);
                return MyData;
            }
        };


        requestQueue.add(stringRequest);
    }


}