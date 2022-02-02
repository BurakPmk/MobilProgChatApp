package com.example.mobilprogchatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth myAuth;
    DatabaseReference myRef;
    FirebaseUser myUser;
    Timer timer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);//ÜSTTEKİ BAR
        myAuth = FirebaseAuth.getInstance();
        myUser = myAuth.getCurrentUser();
        myRef = FirebaseDatabase.getInstance().getReference().child("Users");
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(myUser!=null)
                {
                    myRef.child(myUser.getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists())
                            {
                                Intent i = new Intent(MainActivity.this,HomeActivity.class);
                                startActivity(i);
                                finish();
                            }
                            else
                            {
                                Intent i = new Intent(MainActivity.this,ProfileSettingsActivity.class);
                                startActivity(i);
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                else
                {
                    Intent i = new Intent(MainActivity.this,LoginActivity.class);
                    startActivity(i);
                }

            }
        },2000);
    }
}