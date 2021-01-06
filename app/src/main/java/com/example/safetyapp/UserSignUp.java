
package com.example.safetyapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class UserSignUp extends AppCompatActivity {
    Button bt1,bt2,bt3;
    EditText et1,et2,et3,et4;
    ImageView imv1;
    RadioButton rbmale, rbfemale;
    DatabaseReference mainref;
    Uri GalleryUri = null;
    Bitmap CameraBitmap = null;
    String type = "";
    String filenametobeuploaded;
    String tempfilepath;
    StorageReference mainref2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_sign_up);
        bt1=findViewById(R.id.bt1);
        bt2=findViewById(R.id.bt2);
        bt3=findViewById(R.id.bt3);
        et1=findViewById(R.id.et1);
        et2=findViewById(R.id.et2);
        et3=findViewById(R.id.et3);
        et4=findViewById(R.id.et4);
        imv1=findViewById(R.id.imv1);
        rbmale=findViewById(R.id.rbmale);
        rbfemale=findViewById(R.id.rbfemale);
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        mainref2 = firebaseStorage.getReference("userimages");

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        mainref = firebaseDatabase.getReference("users");
        Intent in = getIntent();
        //This phone number is being received from Authentication screen
        String phoneNumber = in.getStringExtra("phoneNumber");
        et2.setText(phoneNumber);
        et2.setEnabled(false);

    }
    public void SignUp(View v)
    {
        String name = et1.getText().toString();
        String phonenumber = et2.getText().toString();
        String password = et3.getText().toString();
        String confirmpassword = et4.getText().toString();
        String gender = rbmale.isChecked() ? rbmale.getText().toString() : rbfemale.getText().toString();

        if(name.isEmpty()|| phonenumber.isEmpty()|| password.isEmpty()||confirmpassword.isEmpty())
        {
            Toast.makeText(this, "Fields are mandatory", Toast.LENGTH_SHORT).show();
        }
         else if(!password.equals(confirmpassword))
        {
            Toast.makeText(this, "Password and confirm password should match", Toast.LENGTH_SHORT).show();
        }
         else {

            if (type.equals("gallery")) {

                // gallery upload
                File localfile = new File(getRealPathFromURI(GalleryUri));

                String local2 = "temp" + (int) (Math.random() * 1000000000) + localfile.getName();
                Uri uri2 = Uri.fromFile(localfile);
                final StorageReference newfile = mainref2.child(local2);
                final UploadTask uploadTask = newfile.putFile(uri2);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = newfile.getDownloadUrl();
                        uriTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
//                                save_record();
                                String downloadpath = uri.toString();
                                user_details obj = new user_details(name,phonenumber,password,gender,downloadpath);


                                mainref.child(phonenumber).setValue(obj);
                                //Saving phone number of user who logged in inside local memory.
                                SharedPreferences sharedPreferences = getSharedPreferences("myapp", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("mobileno", phonenumber);
                                editor.apply();

                                Toast.makeText(getApplicationContext(), "Signup Success", Toast.LENGTH_SHORT).show();

                                Intent in = new Intent(getApplicationContext(),User_Home.class);
                                in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                                startActivity(in);
                                finish();

                            }
                        });
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });

                    }
                });
            }
            else {
                File localfile = new File(getRealPathFromURI(getImageUri(getApplicationContext(), CameraBitmap)));

                String local2 = "temp" + (int) (Math.random() * 1000000000) + localfile.getName();
                Uri uri2 = Uri.fromFile(localfile);
                final StorageReference newfile = mainref2.child(local2);
                final UploadTask uploadTask = newfile.putFile(uri2);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = newfile.getDownloadUrl();
                        uriTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String downloadpath = uri.toString();
                                user_details obj = new user_details(name,phonenumber,password,gender,downloadpath);


                                mainref.child(phonenumber).setValue(obj);
                                //Saving phone number of user who logged in inside local memory.
                                SharedPreferences sharedPreferences = getSharedPreferences("myapp", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("mobileno", phonenumber);
                                editor.apply();

                                Toast.makeText(getApplicationContext(), "Signup Success", Toast.LENGTH_SHORT).show();

                                Intent in = new Intent(getApplicationContext(),User_Home.class);
                                in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                                startActivity(in);
                                finish();
                            }
                        });
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });

                    }
                });


            }

        }

    }

    public void LaunchCamera(View view) {
        //Fetch photo from SD card
        Intent cameraintent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraintent,100);


    }

    public void LaunchGallery(View view) {
         Intent galleryintent = new Intent(Intent.ACTION_PICK);
        galleryintent.setType("image/*");
        startActivityForResult(galleryintent,101);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent backintent) {
        super.onActivityResult(requestCode, resultCode, backintent);
        if (requestCode == 100)   // from camera
        {
            if (resultCode == RESULT_OK) {
                Bitmap bmp = (Bitmap) (backintent.getExtras().get("data"));

                CameraBitmap = bmp;
                type = "camera";
                try {
                    tempfilepath = Environment.getExternalStorageDirectory() + File.separator + "temp.jpg";
                    FileOutputStream fos = new FileOutputStream(tempfilepath);
                    CameraBitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);

                    filenametobeuploaded = "temp" + (int) (Math.random() * 1000000000) + ".jpg";
                    Toast.makeText(this, "" + filenametobeuploaded, Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                imv1.setImageBitmap(CameraBitmap);

            }

        }
        else if (requestCode == 101)   // from gallery
        {
            GalleryUri = backintent.getData();
            type = "gallery";
            Picasso.get().load(GalleryUri).resize(150, 150).into(imv1);
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Audio.Media.DATA};
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

}