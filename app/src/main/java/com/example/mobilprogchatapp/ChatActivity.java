 package com.example.mobilprogchatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobilprogchatapp.Utills.Chat;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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

 public class ChatActivity extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView recyclerView;
    EditText inputMessage;
    ImageView sendMessage;
    CircleImageView userProfileImageAppBar;
    TextView usernameAppbar,statusAppbar;
    String otherUserID;

    DatabaseReference myRef,messageRef;
    FirebaseAuth myAuth;
    FirebaseUser myUser;

    String otherUsername,otherUserProfileUrl,otherUserStatus;
    String myProfile;

    FirebaseRecyclerOptions<Chat>options;
    FirebaseRecyclerAdapter<Chat,ChatMyViewHolder>adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        toolbar = findViewById(R.id.chatAppBar);
        setSupportActionBar(toolbar);
        otherUserID = getIntent().getStringExtra("userKey");

        recyclerView = findViewById(R.id.chatRecyclerView);
        inputMessage = findViewById(R.id.inputMessage);
        sendMessage = findViewById(R.id.sendMessage);
        userProfileImageAppBar = findViewById(R.id.userProfileImageAppBar);
        usernameAppbar = findViewById(R.id.usernameAppbar);
        statusAppbar = findViewById(R.id.statusAppbar);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        myAuth = FirebaseAuth.getInstance();
        myUser = myAuth.getCurrentUser();
        myRef = FirebaseDatabase.getInstance().getReference().child("Users");
        messageRef = FirebaseDatabase.getInstance().getReference().child("Message");

        
        LoadOtherUser();
        LoadMyProfile();
        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SendMessage();
            }
        });

        loadMessage();
    }

     private void LoadMyProfile() {
        myRef.child(myUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    myProfile = snapshot.child("profileImage").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatActivity.this,""+error.toException(),Toast.LENGTH_SHORT).show();
            }
        });
     }

     private void loadMessage() {
        options = new FirebaseRecyclerOptions.Builder<Chat>().setQuery(messageRef.child(myUser.getUid()).child(otherUserID),Chat.class).build();
        adapter = new FirebaseRecyclerAdapter<Chat, ChatMyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ChatMyViewHolder holder, int position, @NonNull Chat model) {
                if(model.getUserID().equals(myUser.getUid())){
                    holder.firstUserProfile.setVisibility(View.GONE);
                    holder.firstUserMessage.setVisibility(View.GONE);
                    holder.secondUserProfile.setVisibility(View.VISIBLE);
                    holder.secondUserMassage.setVisibility(View.VISIBLE);

                    holder.secondUserMassage.setText(model.getMessage());
                    Picasso.get().load(myProfile).into(holder.secondUserProfile);
                }
                else
                {
                    holder.secondUserMassage.setVisibility(View.GONE);
                    holder.secondUserProfile.setVisibility(View.GONE);
                    holder.firstUserMessage.setVisibility(View.VISIBLE);
                    holder.firstUserProfile.setVisibility(View.VISIBLE);

                    holder.firstUserMessage.setText(model.getMessage());
                    Picasso.get().load(otherUserProfileUrl).into(holder.firstUserProfile);
                }
            }

            @NonNull
            @Override
            public ChatMyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view_message,parent,false);
                return new ChatMyViewHolder(view);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
     }

     private void SendMessage() {
        String message = inputMessage.getText().toString();
        if(message.isEmpty())
        {
            Toast.makeText(this,"Bir şeyler yazın!",Toast.LENGTH_SHORT).show();
        }
        else
        {
            HashMap hashMap = new HashMap();
            hashMap.put("message",message);
            hashMap.put("status","unseen");
            hashMap.put("userID",myUser.getUid());
            messageRef.child(otherUserID).child(myUser.getUid()).push().updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        messageRef.child(myUser.getUid()).child(otherUserID).push().updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if(task.isSuccessful())
                                {
                                    inputMessage.setText(null);
                                    Toast.makeText(ChatActivity.this,"Sms gönderildi",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            });
        }
     }

     private void LoadOtherUser() {
        //Toast.makeText(ChatActivity.this, ""+otherUserID, Toast.LENGTH_SHORT).show();
        myRef.child(otherUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {

                    otherUsername = snapshot.child("user").getValue().toString();
                    otherUserStatus = snapshot.child("connection").getValue().toString();
                    otherUserProfileUrl = snapshot.child("profileImage").getValue().toString();

                    Picasso.get().load(otherUserProfileUrl).into(userProfileImageAppBar);
                    usernameAppbar.setText(otherUsername);
                    statusAppbar.setText(otherUserStatus);

                }
                else
                {
                    Toast.makeText(ChatActivity.this, ""+snapshot.exists(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatActivity.this,""+error.toException(),Toast.LENGTH_SHORT).show();
            }
        });
     }
 }