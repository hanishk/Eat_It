package com.xyz.androideatit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class forgetPassword extends AppCompatActivity {

    Button btnResetPassword;
    EditText editEmail;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        editEmail = findViewById(R.id.email);
        btnResetPassword = findViewById(R.id.btnResetPassword);

        firebaseAuth = FirebaseAuth.getInstance();

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


//                firebaseAuth.sendPasswordResetEmail(editEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()) {
//                            Toast.makeText(forgetPassword.this, "Password sent to your email", Toast.LENGTH_SHORT).show();
//                        } else {
//                            Toast.makeText(forgetPassword.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
            }
        });


    }
}
