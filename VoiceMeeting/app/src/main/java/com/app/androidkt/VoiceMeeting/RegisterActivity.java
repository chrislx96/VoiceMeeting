package com.app.androidkt.VoiceMeeting;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends Activity{

    private EditText mName, mEmail, mPassword, mPhone;
    private Button mRegisterBtn;
    private TextView mLoginBtn;
    private FirebaseAuth fAuth;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mName = findViewById(R.id.register_edit_name);
        mEmail = findViewById(R.id.register_edit_email);
        mPassword = findViewById(R.id.register_edit_pwd);
        mPhone = findViewById(R.id.register_edit_phone);
        mRegisterBtn = findViewById(R.id.register_btn_register);
        mLoginBtn = findViewById(R.id.register_tv_login);

        fAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);

        if(fAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(),RecordingActivity.class));
            finish();
        }

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    mEmail.setError("Email is required.");
                }

                if(TextUtils.isEmpty(password)){
                    mPassword.setError("Password is required.");
                }

                if(password.length() < 0){
                    mPassword.setError("Password must be more than 6 characters");
                }

                progressBar.setVisibility(View.VISIBLE);

                // register the user in firebase
                fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                          if(task.isSuccessful())  {
                              Toast.makeText(RegisterActivity.this,"User registered", Toast.LENGTH_LONG).show();
                              startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                          }else{
                              Toast.makeText(RegisterActivity.this,"ERROR" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                          }
                    }
                });
            }
        });

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });





    }
}
