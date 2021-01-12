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
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

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
                //record audio
//                new Thread(new AudioRecord()).start();
                //Toast.makeText(this, "Recording Audio!!", Toast.LENGTH_SHORT).show();
                flag = true;
                //camera pictures
                new Thread(new ClickPictures()).start();
                //send locations
//                startNetwork();
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

}