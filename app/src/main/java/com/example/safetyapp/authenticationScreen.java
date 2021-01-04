package com.example.safetyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.content.Intent;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;

import java.util.concurrent.TimeUnit;

public class authenticationScreen extends AppCompatActivity {
    private FirebaseAuth mAuth;
    String phoneNumber ;
    String VerificationId;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    EditText etmobileno, etotp;
    Button btnverify;
    //    ProgressDialog progressDialog;
    boolean networkstatusflag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticationscreen);
        etmobileno = findViewById(R.id.et1signup_mobileno);
        etotp = findViewById(R.id.et2signup_otp);
        btnverify = findViewById(R.id.bt2_signup);
        mAuth = FirebaseAuth.getInstance();


        SharedPreferences sharedPreferences = getSharedPreferences("myapp", MODE_PRIVATE);
        String usermobileno = sharedPreferences.getString("mobileno", null);
        //Auto-login code:when user opens app after loggin in i.e second time and so on.
        if(usermobileno
                != null)
        {
            Intent in = new Intent(this, User_Home.class);
            startActivity(in);
            finish();
        }
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            // onVerificationCompleted is Auto Called if Auto Detection of SMS is done
            @Override
            public void onCodeAutoRetrievalTimeOut(String s) {
                super.onCodeAutoRetrievalTimeOut(s);
                Toast.makeText(getApplicationContext(), "OTP Timed out ", Toast.LENGTH_SHORT).show();
//                progressDialog.hide();
            }
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verificaiton without
                //     user action.

                etotp.setText(credential.getSmsCode());
//                progressDialog.hide();
                Toast.makeText(getApplicationContext(), "Completed " + credential.getSmsCode(), Toast.LENGTH_LONG).show();
                Log.d("MYMSG", "verification completed");
                // progressDialog.hide();
                //Toast.makeText(mainactivity.this, "Code verified", Toast.LENGTH_SHORT).show();
//                PhoneAuthCredential credential1 = PhoneAuthProvider.getCredential(VerificationId, credential.getSmsCode());
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.d("MYMSG", "onVerificationFailed");
                Toast.makeText(getApplicationContext(), "Failed " + e.getMessage(), Toast.LENGTH_LONG).show();
//                progressDialog.hide();
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                Log.d("MYMSG", "code sent " + verificationId);
                VerificationId = verificationId;
                etotp.setVisibility(View.VISIBLE);
                btnverify.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), "OTP Sent", Toast.LENGTH_LONG).show();
            }
        };
        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    networkstatusflag = true;
                } else {
                    networkstatusflag = false;
//                    Crouton.makeText(signup1.this, "No internet,Please check your network connection",Style.ALERT).show();
                    return;
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled");
            }
        });

    }
    public void bt1_signup(View v)
    {
        if(networkstatusflag==false) {
//            Crouton.makeText(signup1.this, "No internet,Please check your network connection", Style.ALERT).show();
            return;
        }
//        progressDialog  = GlobalClass.CreateProgressDialog(this,"Mobile Verification","Please Wait");
        String mobileno = etmobileno.getText().toString();
        if (mobileno.equals("")) {
            Toast.makeText(this, "Mobile No. is must", Toast.LENGTH_SHORT).show();
        } else if (mobileno.length() != 10) {
            Toast.makeText(this, "Mobile No. should be of 10 digits", Toast.LENGTH_SHORT).show();
        } else {
//            progressDialog.show();
            mobileno = "+1" + mobileno;
            phoneNumber = mobileno;
//            Toast.makeText(getApplicationContext(), phoneNumber, Toast.LENGTH_SHORT).show();
            etotp.setVisibility(View.VISIBLE);
            btnverify.setVisibility(View.VISIBLE);
            // progressDialog = GlobalClass.CreateProgessDialog(signup1.this, "Moblile Verificatoin", "Please Wait");
            // progressDialog.show();
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    phoneNumber,        // Phone number to verify
                    120,                 // Timeout duration
                    TimeUnit.SECONDS,   // Unit of timeout
                    this,               // Activity (for callback binding)
                    mCallbacks);        // OnVerificationStateChangedCallbacks
//            PhoneAuthOptions options =
//                    PhoneAuthOptions.newBuilder(mAuth)
//                            .setPhoneNumber(phoneNumber)       // Phone number to verify
//                            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
//                            .setActivity(this)                 // Activity (for callback binding)
//                            .setCallbacks(mCallBack)          // OnVerificationStateChangedCallbacks
//                            .build();
//            PhoneAuthProvider.verifyPhoneNumber(options);
            Toast.makeText(getApplicationContext(), "wait until phone verified", Toast.LENGTH_LONG).show();
            Log.d("MYMSG", "wait until phone verified");


        }

    }
    public void bt2_signup(View v) {
        String code = etotp.getText().toString();
        if(code.equals("") || code.equals(null)){
            Toast.makeText(this, "Enter OTP", Toast.LENGTH_SHORT).show();
        }
        //This code firebase runs on its own
        else {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(VerificationId, code);
            signInWithPhoneAuthCredential(credential);
            //if this code runs successfully then the following function will run next
            SaveUser();
        }



    }
    private void signInWithPhoneAuthCredential(final PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            SaveUser();

                        } else {
                            if (task.getException() instanceof
                                    FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(getApplicationContext(), "Invalid code", Toast.LENGTH_SHORT).show();
                                // progressDialog.hide();
                            }
                        }
                    }
                });

    }
    void SaveUser() {

//        GlobalClass.customermobile = phoneNumber;
        Intent in = new Intent(this, UserSignUp.class);
        in.putExtra("phoneNumber",phoneNumber);

        startActivity(in);
        finish();


    }
}