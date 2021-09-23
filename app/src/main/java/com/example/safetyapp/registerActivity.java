package com.example.safetyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class registerActivity extends AppCompatActivity {

    EditText editName,editMail,editPhone,password;
    ProgressBar progressBar;
    Button button;
    FirebaseAuth fAuth;
    TextView textView;
    FirebaseFirestore fstore;
    String userInfo,phone,Lat = " ",Lon = " ";
    int y;

    public void logIn(View view){
        startActivity(new Intent(getApplicationContext(),loginActivity.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editName = findViewById(R.id.editName);
        editMail = findViewById(R.id.editMail);
        editPhone = findViewById(R.id.editPhone);
        password = findViewById(R.id.editPassword);
        textView = findViewById(R.id.textView3);
        button = findViewById(R.id.button1);

        fAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        progressBar = findViewById(R.id.progressBar);

        SharedPreferences sharedPreferences = this.getSharedPreferences("com.example.safetyapp", Context.MODE_PRIVATE);
        y = sharedPreferences.getInt("x",0);

        if(y == 1){
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String eMail = editMail.getText().toString().trim();
                String pass = password.getText().toString().trim();
                String userName = editName.getText().toString();
                phone = editPhone.getText().toString();

                if(TextUtils.isEmpty(userName)){
                    editName.setError("Username required!");
                    return;
                }

                if(TextUtils.isEmpty(eMail)){
                    editMail.setError("Email id required!");
                    return;
                }

                if(TextUtils.isEmpty(pass)){
                    password.setError("Password is required.");
                    return;
                }

                if(password.length() < 6){
                    password.setError("Password should contains atleast 6 characters.");
                    return;
                }

                if(TextUtils.isEmpty(phone)){
                    editPhone.setError("Phone is required!");
                    return;
                }

                if(editPhone.length() < 10 && editPhone.length() > 10){
                    editPhone.setError("Invalid Phone Number!");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                fAuth.createUserWithEmailAndPassword(eMail,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            sharedPreferences.edit().putInt("x",1).apply();
                            Toast.makeText(registerActivity.this,"User created",Toast.LENGTH_SHORT).show();
                            userInfo = fAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = fstore.collection("Users").document(userInfo);
                            Map<String, Object> user = new HashMap<>();
                            user.put("User",userName);
                            user.put("Email",eMail);
                            user.put("Phone",phone);
                            user.put("Latitude",Lat);
                            user.put("Longitude",Lon);

                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(registerActivity.this,"Data added in database successfully",Toast.LENGTH_LONG).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(registerActivity.this,"Data couldn't be added in firebase",Toast.LENGTH_SHORT).show();
                                }
                            });
                            progressBar.setVisibility(View.GONE);
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        }else{
                            Toast.makeText(registerActivity.this,"Error! "+ task.getException().getMessage(),Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });
    }
}