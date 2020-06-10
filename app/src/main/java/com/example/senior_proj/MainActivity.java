package com.example.senior_proj;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.example.senior_proj.ui.CreditsDialog;
import com.example.senior_proj.ui.MenuDialog;
import com.example.senior_proj.ui.SettingsDialog;
import com.example.senior_proj.ui.fitness.FitnessFragment;
import com.example.senior_proj.ui.nutrition.NutritionFragment;
import com.example.senior_proj.ui.water.WaterFragment;
import com.example.senior_proj.ui.home.HomeFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActTAG";
    String PREF_NAME = "seniorProjPref";

    private FragmentManager fragmentManager;
    private Toolbar toolbar;
    SharedPreferences settings;
    boolean firstTime;

    /**
     * This method will set up the display of the Main Activity by setting the view with the layout
     * xml, adding a toolbar and giving it a title, and set up the bottom navigation UI that will
     * be used to navigate the different fragments. This method will also check if the LoginActivity
     * has included extra data through intent. IF this is the first time entering the MainActivity,
     * the user will be shown a menu Dialog, otherwise the user will be shown the home screen.
     *
     * @param savedInstanceState Bundle object containing activity's previously saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        settings = MainActivity.this.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        firstTime = settings.getBoolean( Objects.requireNonNull(FirebaseAuth.getInstance().
                getCurrentUser()).getUid(), true);
        boolean firstTimeExtra = getIntent().getBooleanExtra
                ("firstTime", false);

        //toolbar setup
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);

        //set up the bottom navigation
        setBottomNavUI(savedInstanceState);

        //true for testing else firstTimeExtra
        //If the extra data attached to the intent is true, show the menu dialog to the user
        if(firstTimeExtra){
            showDialog();
        }
    }

    /**
     * This method will display the welcome menu dialog to the user. The details will be included
     *  in the MenuDialog Fragment Class. This menu dialog will ask the user to input their
     *  weight, height, and water goal. This information will be used to set up all of the fragments
     */
    public void showDialog() {
        //display the menuDialog to the user

        MenuDialog.display(getSupportFragmentManager());

        //if this is the first time the user has logged in, change the value to false so the menu
        // does not appear again
        if (firstTime) {
            SharedPreferences.Editor editor = settings.edit();
            //changes the boolean value from true to false in the sharedpreference file
            editor.putBoolean(Objects.requireNonNull(FirebaseAuth.getInstance().
                    getCurrentUser()).getUid(), false);
            editor.apply();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == R.id.mn_signOut) {
            signOutUser();
            return true;
        }
        else if (item.getItemId() == R.id.mn_settings){
            showSettings();
            return true;
        }
        else if(item.getItemId() == R.id.mn_credits){
            showCredits();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void signOutUser(){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void showSettings(){
        SettingsDialog.display(getSupportFragmentManager());
    }

    public void showCredits(){
        CreditsDialog.display(getSupportFragmentManager());
    }

    public void setBottomNavUI(Bundle savedInstanceState){
        toolbar = findViewById(R.id.toolbar);
        ChipNavigationBar bottomNav = findViewById(R.id.bottom_nav);
        toolbar.setBackgroundColor(Color.parseColor("#f1b902"));
        toolbar.setTitleTextColor(Color.parseColor("#5d36c1"));
        setStatusBarColor("#B68C02");
        if(savedInstanceState==null){
            bottomNav.setItemSelected(R.id.navigation_home, true);
            fragmentManager = getSupportFragmentManager();
            HomeFragment homefragment = new HomeFragment();
            fragmentManager.beginTransaction().
                    replace(R.id.fragment_container, homefragment).commit();


        }

        bottomNav.setOnItemSelectedListener(id -> {
            Fragment fragment = null;
            switch (id){
                case R.id.navigation_home:
                    fragment = new HomeFragment();
                    toolbar.setBackgroundColor(Color.parseColor("#f1b902"));
                    toolbar.setTitle("Home");
                    toolbar.setTitleTextColor(Color.parseColor
                            ("#5d36c1"));
                    Objects.requireNonNull(toolbar.getOverflowIcon()).setTint(Color.parseColor("#5d36c1"));
                    setStatusBarColor("#B68C02");
                    break;
                case R.id.navigation_water:
                    fragment = new WaterFragment();
                    toolbar.setBackgroundColor(Color.parseColor("#3917e7"));
                    toolbar.setTitle("Water");
                    Objects.requireNonNull(toolbar.getOverflowIcon()).setTint(Color.parseColor("#d4f8fd"));
                    toolbar.setTitleTextColor(Color.parseColor("#d4f8fd"));
                    setStatusBarColor("#2A11A7");
                    break;
                case R.id.navigation_health:
                    fragment = new NutritionFragment();
                    toolbar.setBackgroundColor(Color.parseColor("#dcae63"));
                    toolbar.setTitle("Nutrition");
                    toolbar.setTitleTextColor(Color.parseColor("#f8f2dc"));
                    Objects.requireNonNull(toolbar.getOverflowIcon()).setTint(Color.parseColor("#f8f2dc"));
                    setStatusBarColor("#D59F48");
                    break;
                case R.id.navigation_fitness:
                    fragment = new FitnessFragment();
                    toolbar.setBackgroundColor(Color.parseColor("#ae343e"));
                    toolbar.setTitle("Fitness");
                    toolbar.setTitleTextColor(Color.parseColor("#f6ddcd"));
                    Objects.requireNonNull(toolbar.getOverflowIcon()).setTint(Color.parseColor("#f6ddcd"));
                    setStatusBarColor("#9d2d35");
                    break;
            }
            if(fragment!=null){
                fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().
                        replace(R.id.fragment_container, fragment).commit();
            }
            else{
                Log.e(TAG, "error fragment creation");
            }
        });
    }

    /**
     * This method clears the previous color of the status bar and redraws it
     * with the color specified.
     *
     * @param color This is the color that will be used to color in the status bar. This color
     *              should be darker than the Title Bar color.
     */
    public void setStatusBarColor(String color){
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(Color.parseColor(color));
    }
}
