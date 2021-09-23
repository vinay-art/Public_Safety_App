package com.example.safetyapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;
import com.google.firestore.v1.WriteResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class listActivity extends AppCompatActivity {

    FirebaseFirestore fStore;
    static ArrayAdapter<String> arrayAdapter;
    ArrayList<String> cities = new ArrayList<String>();;
    ListView listView;
    static String a,b,c;
    int x;
    SharedPreferences sharedPreferences;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        listView = (ListView) findViewById(R.id.listView);
        fStore = FirebaseFirestore.getInstance();
        sharedPreferences = getApplicationContext().getSharedPreferences("com.example.safetyapp", Context.MODE_PRIVATE);
        HashSet<String> set = (HashSet<String>) sharedPreferences.getStringSet("a",null);

        if(set == null)
            cities.add("Locations...");
        else {
            cities = new ArrayList(set);
        }

                fStore
                .collection("Users")
                .whereNotEqualTo("Latitude", false)
                .addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                    @SuppressLint("CommitPrefEdits")
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            cities.add("Error occured!");
                            System.err.println("Listen failed:" + error);
                            return;
                        }

                        for (DocumentSnapshot doc : value) {
                            if (doc.get("Latitude") != null || doc.get("Longitude") != null) {
                                a = doc.getString("Latitude");
                                b = doc.getString("Longitude");
                                if(cities.equals("Locations..."))
                                    cities.remove("Locations...");
                                cities.add(a + "," + b);
                                HashSet<String> set = new HashSet<>(cities);
                                sharedPreferences.edit().putStringSet("a", set).apply();
                                        fStore
                                        .collection("Users")
                                        .document(doc.getId())
                                        .update("Latitude",null,"Longitude", null);
                            }
                        }
                        arrayAdapter.notifyDataSetChanged();
                    }
                });

        arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, cities);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                c = cities.get(i).toString();
                if(c.equals("Locations...")){
                    return ;
                }else {
                    String[] d = c.split(",", 2);
                    Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                    intent.putExtra("Lat", d[0]);
                    intent.putExtra("Lon", d[1]);
                    startActivity(intent);
                }
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                x=i;
                new AlertDialog.Builder(listActivity.this)
                        .setIcon(android.R.drawable.ic_delete)
                        .setTitle("Are you sure?")
                        .setMessage("Selected location will be deleted")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                cities.remove(x);
                                HashSet<String> set = new HashSet<>(cities);
                                sharedPreferences.edit().putStringSet("a", set).apply();
                                arrayAdapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("Cancel",null)
                        .show();
                return true;
            }
        });

    }
}