package com.example.mobilprogchatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewFriendActivity extends AppCompatActivity {

    DatabaseReference myUserRef,requestRef,friendRef;
    FirebaseAuth myAuth;
    FirebaseUser myUser;
    String userID;
    String profileImageUrl, userNameV, statusV;
    String myProfileImageUrl, myUserNameV,myStatusV;

    CircleImageView profileImage;
    TextView userName,status;
    Button bttnSendRequest,bttnDeclineRequest;
    String currentState="nothingHappen";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_friend);
        userID = getIntent().getStringExtra("userKey");

        myUserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        requestRef = FirebaseDatabase.getInstance().getReference().child("Requests");
        friendRef = FirebaseDatabase.getInstance().getReference().child("Friends");
        myAuth = FirebaseAuth.getInstance();
        myUser = myAuth.getCurrentUser();
        bttnSendRequest = findViewById(R.id.bttnSendRequest);
        bttnDeclineRequest = findViewById(R.id.bttnDeclineRequest);

        profileImage = findViewById(R.id.viewProfileImage);
        userName = findViewById(R.id.viewUserName);
        status = findViewById(R.id.viewStatus);

        loadUser();
        LoadMyUser();

        bttnSendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequestAction(userID);
            }
        });
        ChechUserExistance(userID);
        bttnDeclineRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Unfriend(userID);
            }
        });
    }

    private void Unfriend(String userID)
    {
        if(currentState.equals("friend"))
        {
            friendRef.child(myUser.getUid()).child(userID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful())
                    {
                        friendRef.child(userID).child(myUser.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful())
                                {
                                    Toast.makeText(ViewFriendActivity.this,"Arkadaşlıktan çıkarıldı!",Toast.LENGTH_SHORT).show();
                                    currentState="nothingHappen";
                                    bttnSendRequest.setText("İstek gönder");
                                    bttnDeclineRequest.setVisibility(View.GONE);
                                }
                            }
                        });
                    }
                }
            });
        }
        if (currentState.equals("they_request_pending"))
        {
            HashMap hashMap = new HashMap();
            hashMap.put("requestStatus","decline");
            requestRef.child(userID).child(myUser.getUid()).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(ViewFriendActivity.this,"İstek reddedildi!",Toast.LENGTH_SHORT).show();
                        currentState = "they_request_decline";
                        bttnSendRequest.setVisibility(View.GONE);
                        bttnDeclineRequest.setVisibility(View.GONE);
                    }
                }
            });
        }
    }

    private void ChechUserExistance(String userID)
    {
        friendRef.child(myUser.getUid()).child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    currentState="friend";
                    bttnSendRequest.setText("Mesaj Gönder");
                    bttnDeclineRequest.setText("Arkadaşlıktan çıkar");
                    bttnDeclineRequest.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        friendRef.child(userID).child(myUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    currentState="friend";
                    bttnSendRequest.setText("Mesaj Gönder");
                    bttnDeclineRequest.setText("Arkadaşlıktan çıkar");
                    bttnDeclineRequest.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        requestRef.child(myUser.getUid()).child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    if(snapshot.child("requestStatus").getValue().toString().equals("pending"))
                    {
                        currentState="my_request_pending";
                        bttnSendRequest.setText("İsteği iptal et");
                        bttnDeclineRequest.setVisibility(View.GONE);
                    }
                    if(snapshot.child("requestStatus").getValue().toString().equals("decline"))
                    {
                        currentState="my_request_decline";
                        bttnSendRequest.setText("İsteği iptal et");
                        bttnDeclineRequest.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        requestRef.child(userID).child(myUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    if(snapshot.child("requestStatus").getValue().toString().equals("pending"))
                    {
                        currentState="they_request_pending";
                        bttnSendRequest.setText("İsteği Kabul Et");
                        bttnDeclineRequest.setText("İsteği Reddet");
                        bttnDeclineRequest.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        if (currentState.equals("nothingHappen"));
        {
            currentState="nothingHappen";
            bttnSendRequest.setText("İstek gönder");

            bttnDeclineRequest.setVisibility(View.GONE);
        }
    }

    private void sendRequestAction(String userID)
    {
        if (currentState.equals("nothingHappen"))
        {
            HashMap hashMap = new HashMap();
            hashMap.put("requestStatus","pending");
            requestRef.child(myUser.getUid()).child(userID).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(ViewFriendActivity.this,"Arkadaşlık isteği gönderildi",Toast.LENGTH_SHORT).show();
                        bttnDeclineRequest.setVisibility(View.GONE);
                        currentState = "my_request_pending";
                        bttnSendRequest.setText("İsteği iptal et!");
                    }
                    else
                    {
                        Toast.makeText(ViewFriendActivity.this,""+task.getException(),Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        if (currentState.equals("my_request_pending")||currentState.equals("my_request_decline"))
        {
            requestRef.child(myUser.getUid()).child(userID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(ViewFriendActivity.this,"İstek iptal edildi!",Toast.LENGTH_SHORT).show();
                        currentState = "nothingHappen";
                        bttnSendRequest.setText("İstek Gönder");
                        bttnDeclineRequest.setVisibility(View.GONE);
                    }
                    else
                    {
                        Toast.makeText(ViewFriendActivity.this,""+task.getException(),Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        if(currentState.equals("they_request_pending"))
        {
            requestRef.child(userID).child(myUser.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        final HashMap hashMap = new HashMap();
                        hashMap.put("friendStatus","friend");
                        hashMap.put("username",userNameV);
                        hashMap.put("profileImageUrl",profileImageUrl);
                        hashMap.put("status",statusV);

                        final HashMap hashMap1 = new HashMap();
                        hashMap1.put("friendStatus","friend");
                        hashMap1.put("username",myUserNameV);
                        hashMap1.put("profileImageUrl",myProfileImageUrl);
                        hashMap1.put("status",myStatusV);

                        friendRef.child(myUser.getUid()).child(userID).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if(task.isSuccessful())
                                {
                                    friendRef.child(userID).child(myUser.getUid()).updateChildren(hashMap1).addOnCompleteListener(new OnCompleteListener() {
                                        @Override
                                        public void onComplete(@NonNull Task task) {
                                        Toast.makeText(ViewFriendActivity.this,"Arkadaş eklendi!",Toast.LENGTH_SHORT).show();
                                            currentState="friend";
                                            bttnSendRequest.setText("Mesaj gönder");
                                            bttnDeclineRequest.setText("Arkadaşlıktan çıkar");
                                            bttnDeclineRequest.setVisibility(View.VISIBLE);
                                        }
                                    });
                                }
                            }
                        });
                    }
                }
            });
        }
        if(currentState.equals("friend"))
        {
            Intent intent = new Intent(ViewFriendActivity.this,ChatActivity.class);
            intent.putExtra("userKey",userID);
            startActivity(intent);

        }

    }

    private void loadUser() {

        myUserRef.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    profileImageUrl = snapshot.child("profileImage").getValue().toString();
                    userNameV = snapshot.child("user").getValue().toString();
                    statusV = snapshot.child("status").getValue().toString();

                    Picasso.get().load(profileImageUrl).into(profileImage);
                    userName.setText(userNameV);
                    status.setText(statusV);
                }
                else
                {
                    Toast.makeText(ViewFriendActivity.this,"Veri bulunamadı!",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewFriendActivity.this,""+error.getMessage().toString(),Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void LoadMyUser() {
        myUserRef.child(myUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    myProfileImageUrl = snapshot.child("profileImage").getValue().toString();
                    myUserNameV = snapshot.child("user").getValue().toString();
                    myStatusV = snapshot.child("status").getValue().toString();

                    Picasso.get().load(profileImageUrl).into(profileImage);
                    userName.setText(userNameV);
                    status.setText(statusV);
                }
                else
                {
                    Toast.makeText(ViewFriendActivity.this,"Veri bulunamadı!",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewFriendActivity.this,""+error.getMessage().toString(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}