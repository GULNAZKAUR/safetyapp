package com.example.safetyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashScreen extends AppCompatActivity {
    ImageView imv1;
    TextView tv1;
    Animation top, buttom;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,// this will hide the notification area.
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        startPermission();

         tv1=findViewById(R.id.tv1);
        imv1 = findViewById(R.id.imv1);
        top = AnimationUtils.loadAnimation(this, R.anim.top);
        buttom = AnimationUtils.loadAnimation(this, R.anim.buttom);
        imv1.setAnimation(buttom);
    }

    void startPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)  // if Android version is >= M
        {

            //Check If Permissions are already granted, otherwise show Ask Permission Dialog
            if (checkPermission()) {
                Toast.makeText(this, "All Permissions Already Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission Not Granted", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        Intent in = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(in);
                        finish();
                    }
                }, 3000);
                // generate dialogs to ask for permissions
                requestPermission();
            }

        }
    }

    // This method returns true if all permissions are already granted
    boolean checkPermission() {

        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;

        boolean result2 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        boolean result3 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED;

        boolean result4 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;
        boolean result5 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        boolean result6 = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;


        return (result1 && result2 && result3 && result4 && result5 && result6);

    }

    public void requestPermission() {

        //Show ASK FOR PERSMISSION DIALOG (passing array of permissions that u want to ask)

        ActivityCompat.requestPermissions(this,

                new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_SMS, Manifest.permission.READ_CONTACTS,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.RECORD_AUDIO},

                1);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==1)
        {
            if(grantResults.length>0)
            {

                if(grantResults[0]==PackageManager.PERMISSION_GRANTED && grantResults[1]==PackageManager.PERMISSION_GRANTED && grantResults[2]==PackageManager.PERMISSION_GRANTED && grantResults[3]==PackageManager.PERMISSION_GRANTED  && grantResults[4]==PackageManager.PERMISSION_GRANTED  && grantResults[5]==PackageManager.PERMISSION_GRANTED    )
                {
                    Toast.makeText(this, "All PERMISSON GRANTED", Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            Intent in = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(in);
                            finish();
                        }
                    }, 3000);
                }

                if(grantResults[0]== PackageManager.PERMISSION_GRANTED)
                {

                    Toast.makeText(this,"Camera Permission Granted",Toast.LENGTH_SHORT).show();

                }


                if(grantResults[1]==PackageManager.PERMISSION_GRANTED)
                {

                    Toast.makeText(this, "Storage Permission Granted", Toast.LENGTH_SHORT).show();

                }

                if(grantResults[2]==PackageManager.PERMISSION_GRANTED)
                {

                    Toast.makeText(this, "Read SMS Permission Granted", Toast.LENGTH_SHORT).show();

                }

                if(grantResults[3]==PackageManager.PERMISSION_GRANTED)
                {

                    Toast.makeText(this, "READ CONTACTS Permission Granted", Toast.LENGTH_SHORT).show();

                }


                if(grantResults[0]==PackageManager.PERMISSION_DENIED  && grantResults[1]==PackageManager.PERMISSION_DENIED &&  grantResults[2]==PackageManager.PERMISSION_DENIED &&  grantResults[3]==PackageManager.PERMISSION_DENIED &&  grantResults[4]==PackageManager.PERMISSION_DENIED  &&  grantResults[5]==PackageManager.PERMISSION_DENIED )
                {
                    Toast.makeText(this, "All Permission Denied", Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            Intent in = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(in);
                            finish();
                        }
                    }, 3000);
                }

            }

        }



    }


}



