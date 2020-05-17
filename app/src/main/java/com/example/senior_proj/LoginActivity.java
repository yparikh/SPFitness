package com.example.senior_proj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.app.Activity;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "emailpass";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private Map<String, Object> usrData = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        statusBarColor("#4fa8f2");
        mAuth = FirebaseAuth.getInstance();
    }

    public void statusBarColor(String color){
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor(color));
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        updateUI(currentUser);
    }

    public void signIn(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            addUserData();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

    public void logInClick(View v){
        EditText emailTxt = findViewById(R.id.emailText);
        EditText passTxt = findViewById(R.id.passText);

        //if the email text and password text fields are not empty, continue to the sign in method
        if (!emailTxt.getText().toString().isEmpty() && !passTxt.getText().toString().isEmpty()) {
            signIn(emailTxt.getText().toString(), passTxt.getText().toString());
        }
    }

    public void signUpClick(View v){

        Intent intent = new Intent(LoginActivity.this, signUpActivity.class);
        startActivity(intent);
    }


    private void updateUI(FirebaseUser currentUser) {
        if(currentUser != null){
            if(currentUser.isEmailVerified()) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
            else{
                //email has not been verified, notify user
                Log.d(TAG, "emailVerified:false");
                Toast.makeText(LoginActivity.this,
                        "Email has not been verified. Please check your email for link",
                        Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void addUserData(){
        //store UID of current user to firestone
        String UID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        String name = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        //add user's display name for reference
        usrData.put("Name", name);
        assert name != null;
        db.collection("/healthData").document(UID).set(usrData, SetOptions.merge()).
                addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error writing document", e);
            }
        });
    }



}
