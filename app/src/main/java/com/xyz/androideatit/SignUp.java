package com.xyz.androideatit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.style.TtsSpan;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.xyz.androideatit.Common.Common;
import com.xyz.androideatit.Model.User;

public class SignUp extends AppCompatActivity {

    MaterialEditText edtPhone, edtName, edtPassword;
    Button SignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        edtPhone = findViewById(R.id.editPhoneNumber);
        edtName = findViewById(R.id.editName);
        edtPassword = findViewById(R.id.editPassword);
        SignUp = findViewById(R.id.btnSignUp);

        // init the firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User");

        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Common.isConnectedToInternet(getBaseContext())) {

                    final ProgressDialog mDialog = new ProgressDialog(SignUp.this);
                    mDialog.show();

                    table_user.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            // check if user already exits  through phone number

                            if (dataSnapshot.child(edtPhone.getText().toString()).exists()) {
                                mDialog.dismiss();
                                Toast.makeText(SignUp.this, "User already exists", Toast.LENGTH_SHORT).show();
                            } else {
                                mDialog.dismiss();
                                // add the user with phone number as key value with name and password as child.
                                // use user class to get the name and password and save it to user object

                                User user = new User(edtName.getText().toString(), edtPassword.getText().toString());
                                // below this table user have phone number with value name and pasword through obj user
                                table_user.child(edtPhone.getText().toString()).setValue(user);
                                Toast.makeText(SignUp.this, "Sign Up Successfully", Toast.LENGTH_SHORT).show();
                                finish();

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else {
                    Toast.makeText(SignUp.this, "Please Check Internet Connection", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }
}
