package com.example.mobilprogchatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    private TextInputLayout inputEmail,inputPassword;
    Button bttnLogin;
    TextView createAccount,forgotPassword;
    ProgressDialog myLoadingBar;
    FirebaseAuth myAuth;
    DatabaseReference myRef;
    FirebaseUser myUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        bttnLogin = findViewById(R.id.bttnLogin);
        createAccount = findViewById(R.id.createAccount);
        forgotPassword = findViewById(R.id.forgotPassword);
        myLoadingBar = new ProgressDialog(this);
        myAuth = FirebaseAuth.getInstance();
        myUser = myAuth.getCurrentUser();
        myRef = FirebaseDatabase.getInstance().getReference().child("Users");

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(i);
            }
        });

        bttnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AtampLogin();
            }
        });
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this,ForgotPasswordActivity.class);
                startActivity(i);
            }
        });
    }

    private void AtampLogin()
    {

        String email = inputEmail.getEditText().getText().toString();
        String password = inputPassword.getEditText().getText().toString();

        if(email.isEmpty() || !email.contains("@") || !email.contains(".com"))
        {
            showError(inputEmail,"Geçerli bir mail adresi giriniz!");
        }else if(password.isEmpty() || password.length()<6)
        {
            showError(inputPassword,"Şifre 6 karakterden uzun olmalıdır!");
        }
        else
        {
            myLoadingBar.setTitle("Giriş yapılıyor!");
            myLoadingBar.setMessage("Bilgileriniz kontrol ediliyor, lütfen bekleyin");
            myLoadingBar.setCanceledOnTouchOutside(false);
            myLoadingBar.show();
            myAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                    myLoadingBar.dismiss();
                     Toast.makeText(LoginActivity.this,"Giriş başarılı!",Toast.LENGTH_SHORT).show();
                     Intent i = new Intent(LoginActivity.this,MainActivity.class);
                     startActivity(i);
                        /*myLoadingBar.dismiss();
                        myLoadingBar.setTitle("Kontrol ediliyor!");
                        myLoadingBar.setMessage("Bilgileriniz kontrol ediliyor, lütfen bekleyin");
                        myLoadingBar.setCanceledOnTouchOutside(false);
                        myLoadingBar.show();

                        if(myUser!=null)
                        {
                            myRef.child(myUser.getUid()).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists())
                                    {
                                        myLoadingBar.dismiss();
                                        Intent i = new Intent(LoginActivity.this,HomeActivity.class);
                                        startActivity(i);
                                        finish();
                                    }
                                    else
                                    {
                                        myLoadingBar.dismiss();
                                        Intent i = new Intent(LoginActivity.this,ProfileSettingsActivity.class);
                                        startActivity(i);
                                        finish();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                        else {
                            Toast.makeText(LoginActivity.this,"BURADA OLUYOR",Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(i);
                        }*/

                     }
                    else
                    {
                        myLoadingBar.dismiss();
                        Toast.makeText(LoginActivity.this,task.getException().toString(),Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void showError(TextInputLayout field, String s)
    {
        field.setError(s);
        field.requestFocus();
    }
}