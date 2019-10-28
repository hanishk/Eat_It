package com.xyz.androideatit;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.xyz.androideatit.Common.Common;
import com.xyz.androideatit.Model.User;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    Button btnSignUp, btnSignIn;
    TextView textSlogan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSignUp = findViewById(R.id.btnSignUp);
        btnSignIn = findViewById(R.id.btnSignIn);

        textSlogan = findViewById(R.id.txtSlogan);


        //typeface is used to use the fonts style from asset folder
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/NABILA.TTF");
        textSlogan.setTypeface(face);

        // init paper
        Paper.init(this);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signUpIntent = new Intent(MainActivity.this, SignUp.class);
                startActivity(signUpIntent);
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = new Intent(MainActivity.this, SignIn.class);
                startActivity(signInIntent);
            }

        });

// check remember
        String user = Paper.book().read(Common.USER_KEY);
        String pwd = Paper.book().read(Common.PWD_KEY);

        if (user != null && pwd != null) {
            if (!user.isEmpty() && !pwd.isEmpty()) {
                login(user, pwd);
            }

        }
    }

    private void login(final String phone, final String pwd) {

        //Init Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User");

        // sign in method here
        if (Common.isConnectedToInternet(getBaseContext())) {
            final ProgressDialog mDialog = new ProgressDialog(MainActivity.this);
            mDialog.setMessage("Please Wait....");
            mDialog.show();

            table_user.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //Check if user not exist in database
                    if (dataSnapshot.child(phone).exists()) {
                        //get user information
                        mDialog.dismiss();

                        User user = dataSnapshot.child(phone).getValue(User.class);
                        user.setPhone(phone); //set the phone of user
                        if (user.getPassword().equals(pwd)) {

                            Intent homeIntent = new Intent(MainActivity.this, Home.class);
                            Common.currentUser = user;
                            startActivity(homeIntent);
                            finish();

                        } else {
                            Toast.makeText(MainActivity.this, "Wrong Password", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        mDialog.dismiss();
                        Toast.makeText(MainActivity.this, "User not exists", Toast.LENGTH_SHORT).show();

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            Toast.makeText(MainActivity.this, "Please Check Internet Connection", Toast.LENGTH_SHORT).show();
            return;
        }
    }
}
