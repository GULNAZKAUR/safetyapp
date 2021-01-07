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
import android.hardware.Camera;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

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
        if (intent.getAction().trim().equals("EMERGENCY SITUATION") && runningflag == false) {
            try {
                SharedPreferences sp1 = getSharedPreferences("myapp", MODE_PRIVATE);
                String no = sp1.getString("mobileno", "");
                runningflag = true;
                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("users").child(no).child("emergency");
                dbRef.setValue("ON");
//            gettoken("+917009741717","Hello This is app notification");
//            gettoken("+917009074928","Hello This is app notification");
                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                //record audio
//                new Thread(new AudioRecord()).start();
                //Toast.makeText(this, "Recording Audio!!", Toast.LENGTH_SHORT).show();
                flag = true;
                //camera pictures
//                new Thread(new ClickPictures()).start();
                //send locations
//                startNetwork();
                SharedPreferences sharedpreferences = getSharedPreferences("myapp", Context.MODE_PRIVATE);
                String user_mobile = sharedpreferences.getString("mobileno", "");

                SharedPreferences sp = getSharedPreferences("myapp", MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("Emergency", "ON");

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
    public Notification simpleNotification(String title, String message, boolean sound) {
        String CHANNEL_ID = "CHANNEL222";

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
}