package com.example.safetyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class otp extends AppCompatActivity {

    EditText otpField;
    Button button6;
    FirebaseAuth aAuth;
    String verificationId,pNumber;
    EditText phoneNumber;
    PhoneAuthProvider.ForceResendingToken token;
    SharedPreferences sharedPreferences;
    int k;

    public void button5(View view)
    {
        verifyPhoneNumber(pNumber);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        aAuth = FirebaseAuth.getInstance();
        otpField = findViewById(R.id.textNumber);
        button6 = findViewById(R.id.button10);
        //button5 = findViewById(R.id.button5);
        phoneNumber = findViewById(R.id.editTextPhone);
        pNumber = "+918959487939";
        sharedPreferences = this.getSharedPreferences("com.example.safetyapp", Context.MODE_PRIVATE);
        k = sharedPreferences.getInt("ab",0);

        if(k == 3)
        {
            startActivity(new Intent(getApplicationContext(),registerActivity.class));
        }

        if(aAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(),registerActivity.class));
            finish();
        }

        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(otpField.getText().toString().isEmpty()){
                    otpField.setError("Enter OTP first!");
                    return;
                }

                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId,otpField.getText().toString());
                authenticateUser(credential);
            }
        });

    }

    public void verifyPhoneNumber(String phoneNum){

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(aAuth)
                .setActivity(otp.this)
                .setPhoneNumber(phoneNum)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        authenticateUser(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(otp.this,e.getMessage(),Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        verificationId = s;
                        token = forceResendingToken;
                    }

                    @Override
                    public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                        super.onCodeAutoRetrievalTimeOut(s);
                    }
                })
                .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    public void authenticateUser(PhoneAuthCredential credential){

        aAuth.signInWithCredential(credential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                sharedPreferences.edit().putInt("ab",3).apply();
               startActivity(new Intent(getApplicationContext(),registerActivity.class));
               finish();
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(otp.this, e.getMessage().toString(),Toast.LENGTH_LONG).show();

            }
        });
    }
}