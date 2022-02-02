package com.example.mobilprogchatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobilprogchatapp.Utills.Friends;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    FirebaseRecyclerOptions<Friends> options;
    FirebaseRecyclerAdapter<Friends,FriendMyViewHolder> adapter;
    RecyclerView recyclerView;
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    FirebaseAuth myAuth;
    FirebaseUser myUser;
    DatabaseReference myUserRef;
    DatabaseReference friendRef;

    String profileImageUrlV,userNameV,userStatusV;

    CircleImageView profileImageHeader;
    TextView userNameHeader;
    TextView userStatusHeader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = findViewById(R.id.appBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Mobil Prog Chat App");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//buton koymaya yarıyo
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);

        recyclerView = findViewById(R.id.recyclerView1);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        myAuth = FirebaseAuth.getInstance();
        myUser = myAuth.getCurrentUser();
        myUserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        friendRef = FirebaseDatabase.getInstance().getReference().child("Friends");
        LoadFriends("");

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navView);

        View view = navigationView.inflateHeaderView(R.layout.drawer_header);

        profileImageHeader = view.findViewById(R.id.profileImageHeader);
        userNameHeader = view.findViewById(R.id.userNameHeader);
        userStatusHeader = view.findViewById(R.id.userStatusHeader);

        navigationView.setNavigationItemSelectedListener(this);

    }

    private void LoadFriends(String s) {
        Toast.makeText(HomeActivity.this,"Burda problem yok",Toast.LENGTH_SHORT).show();
        Query query = friendRef.child(myUser.getUid()).orderByChild("username").startAt(s).endAt(s+"\uf8ff");
        options = new FirebaseRecyclerOptions.Builder<Friends>().setQuery(query,Friends.class).build();
        adapter = new FirebaseRecyclerAdapter<Friends, FriendMyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FriendMyViewHolder holder, int position, @NonNull Friends model) {

                Picasso.get().load(model.getProfileImageUrl()).into(holder.profileImageUrl);
                holder.username.setText(model.getUsername());
                holder.status.setText(model.getStatus());
                int pos = position;
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(HomeActivity.this,ChatActivity.class);
                        intent.putExtra("userKey",getRef(pos).getKey().toString());
                        startActivity(intent);

                    }
                });
            }

            @NonNull
            @Override
            public FriendMyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view_friend,parent,false);
                return new FriendMyViewHolder(view);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(myUser==null)
        {
            SendUserToLoginActivity();
        }
        else
        {
            myUserRef.child(myUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists())
                    {
                        profileImageUrlV=snapshot.child("profileImage").getValue().toString();
                        userNameV=snapshot.child("user").getValue().toString();
                        userStatusV=snapshot.child("status").getValue().toString();
                        Picasso.get().load(profileImageUrlV).into(profileImageHeader);
                        userNameHeader.setText(userNameV);
                        userStatusHeader.setText(userStatusV);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(HomeActivity.this,"Bir şeyler yanlış gitti!",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void SendUserToLoginActivity() {
        Intent i = new Intent(HomeActivity.this,LoginActivity.class);
        startActivity(i);
        finish();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.profile:
                startActivity(new Intent(HomeActivity.this,ProfileSettingsActivity.class));
                break;
            case R.id.friends:
                startActivity(new Intent(HomeActivity.this,FriendActivity.class));
                break;
            case R.id.findFriends:
                startActivity(new Intent(HomeActivity.this,FindFriendActivity.class));
                break;
            case R.id.logout:
                myAuth.signOut();
                Intent i = new Intent(HomeActivity.this,LoginActivity.class);
                startActivity(i);
                finish();
                break;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home)
        {
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        return true;
    }
}