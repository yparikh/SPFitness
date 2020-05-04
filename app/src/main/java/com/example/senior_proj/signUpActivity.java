package com.example.senior_proj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.*;
import com.google.firebase.auth.UserProfileChangeRequest;

public class signUpActivity extends AppCompatActivity {
    private static final String TAG = "emailpass" ;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        statusBarColor("#f2cf9a");
        mAuth = FirebaseAuth.getInstance();
    }

    public void statusBarColor(String color){
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor(color));
    }


    public void newUser(String email, String password, final String name){
        //final Intent intent = new Intent(this,  MainActivity.class);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //set the displayName with what the user has given
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                    .build();
                            assert user != null;
                            //use the profileUpdate above to change the display name
                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(TAG, "User profile updated.");
                                            }
                                        }
                                    });
                            //display verification button
                            Button verify = findViewById(R.id.BT_verify);
                            Button signUP = findViewById(R.id.BT_signup);
                            verify.setVisibility(View.VISIBLE);
                            signUP.setVisibility(View.INVISIBLE);
                            signUP.setEnabled(false);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(signUpActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    public void signUpIClick(View v){
        EditText emailText = findViewById(R.id.et_su_email);
        EditText passText = findViewById(R.id.et_su_pass);
        EditText nameText = findViewById(R.id.et_name);
        String email = emailText.getText().toString();
        String pass = passText.getText().toString();
        String name = nameText.getText().toString();

        if(!nameText.getText().toString().isEmpty() && !emailText.getText().toString().isEmpty() && !passText.getText().toString().isEmpty()){
            newUser(email, pass, name);

        }
    }


    public void verifyEmail(View v){
        EditText emailText= findViewById(R.id.et_su_email);
        //String email = emailText.getText().toString();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        assert user != null;
        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Email sent.");
                        }
                    }
                });
        Intent i=new Intent(this, LoginActivity.class);
        //i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }

}
