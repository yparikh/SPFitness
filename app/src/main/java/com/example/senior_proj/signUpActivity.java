package com.example.senior_proj;


import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class signUpActivity extends AppCompatActivity {
    private static final String TAG = "signUpTAG" ;
    String PREF_NAME = "seniorProjPref";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Map<String, Object> usrData = new HashMap<>();

    /**
     * This method sets up the activity's layout with the layout xml, colors the status bar,
     * and initializes the mAuth variable to display the user sign up page.
     *
     * @param savedInstanceState Bundle object containing activity's previously saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        statusBarColor("#fab2be");
        mAuth = FirebaseAuth.getInstance();
    }

    /**
     * This method clears the previous color of the status bar and redraws it
     * with the color specified.
     *
     * @param color This is the color that will be used to color in the status bar. This color
     *              should be darker than the Title Bar color.
     */
    public void statusBarColor(String color){
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor(color));
    }

    /**
     * This method implements the Firebase method: createUserWithEmailAndPassword to create
     * a new user with the given email and password provided by the user on this page.
     * The given name will then be assigned as a display name for this account.
     * Once the sign up is successful, send an email to the user to verify their account.
     * After an email is sent, go to the addUserData method to write basic userData to
     * the Firestore database.
     *
     * @param email Given email address to be used to contact the user
     * @param password Given password to be used to login to the account
     * @param name Given display name that will be used to address the User on the home page
     */
    public void newUser(String email, String password, final String name){
        //Firebase method for creating a new user
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {

                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();

                        //set the displayName with what the user has given
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest
                                .Builder()
                                .setDisplayName(name)
                                .build();

                        assert user != null;
                        //use the profileUpdate above to change the display name
                        user.updateProfile(profileUpdates)
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        Log.d(TAG, "User profile updated.");
                                    }
                                });

                        //send an email to the new User to verify their account
                        user.sendEmailVerification()
                                .addOnCompleteListener(task12 -> {
                                    if (task12.isSuccessful()) {
                                        Log.d(TAG, "Email sent.");
                                    }
                                });
                        //go to the addUserData to add basic data about the user to the database
                        addUserData();

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText
                                (signUpActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * This method is a listener for the sign up button. It will take the username, email, and
     * password given by the user to the newUser method for account creation. Once account has been
     * created, send the user back to the LoginActivity to login.
     *
     * @param v View that has been clicked
     */
    public void signUpIClick(View v){
        EditText emailText = findViewById(R.id.et_su_email);
        EditText passText = findViewById(R.id.et_su_pass);
        EditText nameText = findViewById(R.id.et_name);
        //convert all the texts to String
        String email = emailText.getText().toString();
        String pass = passText.getText().toString();
        String name = nameText.getText().toString();
        //The account creation will continue if all fields are filled out
        if(!nameText.getText().toString().isEmpty() && !emailText.getText().toString().isEmpty()
                && !passText.getText().toString().isEmpty())
        {
            newUser(email, pass, name);

            //take the user back to the login page to sign in
            Intent i=new Intent(this, LoginActivity.class);
            startActivity(i);
            finish();
        }
    }

    /**
     * This method will create a document with the user's unique ID and will then add the user's
     * display name to it. This method will also add a value to the SharedPreference document.
     * This document will be used to check if whether or not it is the first time a
     * user has logged in. The SharedPreference file will be used to determine if a menu should
     * be displayed after a successful login.
     */
    private void addUserData(){
        String UID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        String name = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        SharedPreferences sharedPreferences = signUpActivity.this.getSharedPreferences(
                PREF_NAME, Context.MODE_PRIVATE);

        usrData.put("Name", name);
        assert name != null;
        //add the user's display name to the database
        db.collection("/healthData").document(UID).set(usrData,
                SetOptions.merge()).addOnSuccessListener(aVoid ->
                        Log.d(TAG, "DocumentSnapshot successfully written!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));
        //This SharedPreference file will be used to track
        //      if this is the user's first time logging in.
        //It is set true for when the user will log in
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(UID, true);
        editor.apply();
    }
}
