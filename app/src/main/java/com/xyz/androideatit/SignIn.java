package com.xyz.androideatit;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.widget.CheckBox;
import com.xyz.androideatit.Common.Common;
import com.xyz.androideatit.Model.User;

import io.paperdb.Paper;

public class SignIn extends AppCompatActivity {
    EditText edtPhone, edtPassword;
    Button btnSignIn;
    CheckBox ckbRemember;
    TextView forgetPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        edtPassword = findViewById(R.id.editPassword);
        edtPhone = findViewById(R.id.editPhoneNumber);
        btnSignIn = findViewById(R.id.btnSignIn);
        ckbRemember = findViewById(R.id.ckbRemeber);
        forgetPassword = findViewById(R.id.forgetPassword);

        // init paper
        Paper.init(this);

        //Init Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User");


        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Common.isConnectedToInternet(getBaseContext())) {
                    // save user and password
                    if (ckbRemember.isChecked()) {
                        Paper.book().write(Common.USER_KEY, edtPhone.getText().toString());
                        Paper.book().write(Common.PWD_KEY, edtPassword.getText().toString());
                    }


                    final ProgressDialog mDialog = new ProgressDialog(SignIn.this);
                    mDialog.setMessage("Please Wait....");
                    mDialog.show();

                    table_user.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //Check if user not exist in database
                            if (dataSnapshot.child(edtPhone.getText().toString()).exists()) {
                                //get user information
                                mDialog.dismiss();

                                User user = dataSnapshot.child(edtPhone.getText().toString()).getValue(User.class);
                                user.setPhone(edtPhone.getText().toString()); //set the phone of user
                                if (user.getPassword().equals(edtPassword.getText().toString())) {

                                    Intent homeIntent = new Intent(SignIn.this, Home.class);
                                    Common.currentUser = user;
                                    startActivity(homeIntent);
                                    finish();

                                } else {
                                    Toast.makeText(SignIn.this, "Wrong Password", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                mDialog.dismiss();
                                Toast.makeText(SignIn.this, "User not exists", Toast.LENGTH_SHORT).show();

                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                } else {
                    Toast.makeText(SignIn.this, "Please Check Internet Connection", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Common.isConnectedToInternet(getBaseContext())) {
                    Intent forgetIntent = new Intent(SignIn.this, forgetPassword.class);
                    startActivity(forgetIntent);
                } else {
                    Toast.makeText(SignIn.this, "Please Check Internet Connection", Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        });
    }
}