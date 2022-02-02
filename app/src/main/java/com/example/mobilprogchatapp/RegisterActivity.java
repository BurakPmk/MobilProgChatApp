package com.example.mobilprogchatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout inputEmail,inputPassword,inputConfirmPassword;
    Button bttnRegister;
    TextView alreadyHaveAccount;
    FirebaseAuth myAuth;
    ProgressDialog myLoadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        inputConfirmPassword = findViewById(R.id.inputConfirmPassword);
        bttnRegister = findViewById(R.id.bttnRegister);
        alreadyHaveAccount = findViewById(R.id.alreadyHaveAccount);
        myAuth =FirebaseAuth.getInstance();
        myLoadingBar = new ProgressDialog(this);

        bttnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AtempRegistartion();
            }
        });

        alreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void AtempRegistartion()
    {
        String email = inputEmail.getEditText().getText().toString();
        String password = inputPassword.getEditText().getText().toString();
        String confirmPassword = inputConfirmPassword.getEditText().getText().toString();

        if(email.isEmpty() || !email.contains("@") || !email.contains(".com"))
        {
            showErrow(inputEmail,"Geçerli bir mail adresi giriniz!");
        }else if(password.isEmpty() || password.length()<6)
        {
            showErrow(inputPassword,"Şifre 6 karakterden uzun olmalıdır!");
        }
        else if(!password.equals(confirmPassword))
        {
            showErrow(inputConfirmPassword,"Şifre doğrulanamadı!");
        }
        else
        {
            myLoadingBar.setTitle("Kayıt Alınıyor");
            myLoadingBar.setMessage("Bilgileriniz kontrol ediliyor, lütfen bekleyin");
            myLoadingBar.setCanceledOnTouchOutside(false);
            myLoadingBar.show();
            myAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        myLoadingBar.dismiss();
                        Toast.makeText(RegisterActivity.this,"Kayıt Tamamlandı!",Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(RegisterActivity.this,LoginActivity.class);
                        startActivity(i);
                    }
                    else
                    {

                        myLoadingBar.dismiss();
                        Toast.makeText(RegisterActivity.this,"Kayıt yapılırken hata oluştu!!",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void showErrow(TextInputLayout field, String s)
    {
        field.setError(s);
        field.requestFocus();
    }
}