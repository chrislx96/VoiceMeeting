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

// Login uses the Google API. This class just shows the logic of login on the client side.
public class LoginActivity extends Activity {

    private EditText mEmail, mPassword;
    private Button mLoginBtn;
    private TextView mCreateBtn;
    private ProgressBar mProgressBar;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmail = findViewById(R.id.login_edit_account);
        mPassword = findViewById(R.id.login_edit_pwd);
        mLoginBtn = findViewById(R.id.login_btn_login);
        mCreateBtn = findViewById(R.id.login_tv_register);
        mProgressBar = findViewById(R.id.progressBar);
        fAuth = FirebaseAuth.getInstance();

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                // Check if the email is typed in.
                if(TextUtils.isEmpty(email)){
                    mEmail.setError("Email is required.");
                }
                // Check if the password is typed in.
                if(TextUtils.isEmpty(password)){
                    mPassword.setError("Password is required.");
                }
                // Check if the password is more than 6 characters.
                if(password.length() < 0){
                    mPassword.setError("Password must be more than 6 characters");
                }

                mProgressBar.setVisibility(View.VISIBLE);

                //authenticate user
                fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(LoginActivity.this,"Logged in successfully", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getApplicationContext(),RecordingActivity.class));
                        }else{
                            Toast.makeText(LoginActivity.this,"ERROR!" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
        // If the user have not registered yet, guide him/her to the registration page.
        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),RegisterActivity.class  ));
            }
        });

    }
}
