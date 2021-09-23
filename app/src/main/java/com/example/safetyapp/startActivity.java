package com.example.safetyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

public class startActivity<k> extends AppCompatActivity {

    int s;
    SharedPreferences sharedPreferences;

    public void userButton(View view){
        sharedPreferences.edit().putInt("ap",1).apply();
        startActivity(new Intent(getApplicationContext(),otp.class));
    }

    public void adminButton(View view){
        sharedPreferences.edit().putInt("ap",2).apply();
        startActivity(new Intent(getApplicationContext(),adminVerify.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        sharedPreferences = this.getSharedPreferences("com.example.safetyapp", Context.MODE_PRIVATE);
        s = sharedPreferences.getInt("ap",0);

        if(s == 1){
            startActivity(new Intent(getApplicationContext(),otp.class));
            finish();
        }

        if(s == 2){
            startActivity(new Intent(getApplicationContext(),adminVerify.class));
            finish();
        }
    }
}