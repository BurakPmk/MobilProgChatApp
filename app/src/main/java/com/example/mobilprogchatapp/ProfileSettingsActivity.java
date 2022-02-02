package com.example.mobilprogchatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import android.app.ProgressDialog;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Debug;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileSettingsActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 1;
    CircleImageView profileImage;
    EditText inputName,inputStatus;
    Button bttnSave;
    Uri imageUri;


    FirebaseAuth myAuth;
    FirebaseUser myUser;
    DatabaseReference myDbRef;
    StorageReference storageRef;
    ProgressDialog myLoadingBar;

    Toolbar toolbar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);

        toolbar = findViewById(R.id.appBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Profili Düzenle");

        profileImage = findViewById(R.id.profile_image);
        inputName = findViewById(R.id.inputName);
        inputStatus = findViewById(R.id.inputStatus);
        bttnSave = findViewById(R.id.bttnSave);

        myAuth = FirebaseAuth.getInstance();
        myUser = myAuth.getCurrentUser();
        myDbRef = FirebaseDatabase.getInstance().getReference().child("Users");
        storageRef = FirebaseStorage.getInstance().getReference().child("ProfileImage");

        myLoadingBar = new ProgressDialog(this);




        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("image/*");
                startActivityForResult(i,REQUEST_CODE);
            }
        });

        bttnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });
    }



    private void saveData()
    {
        String name = inputName.getText().toString();
        String status = inputStatus.getText().toString();

        if (name.isEmpty() || name.length()<3)
        {
            showError(inputName,"Geçerli bir isim giriniz!");
        }
        else if (status.isEmpty() || status.length()<3)
        {
            showError(inputStatus,"Geçerli bir durum giriniz!");
        }
        else if(imageUri==null)
        {
            Toast.makeText(this,"Lütfen fotoğraf seçin!",Toast.LENGTH_SHORT).show();
            //imageUri = Uri.parse("android.resource://com.example.mobilprogchatapp/drawable/defaultprofile");
        }
        else
        {

            myLoadingBar.setTitle("Bilgiler Kaydediliyor");
            myLoadingBar.setCanceledOnTouchOutside(false);
            myLoadingBar.show();
            storageRef.child(myUser.getUid()).putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful())
                    {
                        //System.out.println("buraya kadar sorun yok!");
                        storageRef.child(myUser.getUid()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                HashMap hashMap = new HashMap();
                                hashMap.put("user",name);
                                hashMap.put("status",status);
                                hashMap.put("profileImage",uri.toString());
                                hashMap.put("connection","offline");

                                myDbRef.child(myUser.getUid()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener() {
                                    @Override
                                    public void onSuccess(Object o) {
                                        myLoadingBar.dismiss();
                                        Toast.makeText(ProfileSettingsActivity.this,"Bilgileriniz Kaydedildi!",Toast.LENGTH_SHORT).show();
                                        Intent i = new Intent(ProfileSettingsActivity.this,MainActivity.class);
                                        startActivity(i);

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        myLoadingBar.dismiss();
                                        Toast.makeText(ProfileSettingsActivity.this,e.toString(),Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    }
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CODE && resultCode==RESULT_OK && data!=null)
        {
            imageUri= data.getData();
            profileImage.setImageURI(imageUri);
        }

    }

    private void showError(EditText  field, String s)
    {
        field.setError(s);
        field.requestFocus();
    }
}