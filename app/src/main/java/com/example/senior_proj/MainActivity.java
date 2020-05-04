package com.example.senior_proj;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.example.senior_proj.ui.fitness.FitnessFragment;
import com.example.senior_proj.ui.health.HealthFragment;
import com.example.senior_proj.ui.home.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "mainactivity" ;
    private FirebaseAuth mAuth;
    private ChipNavigationBar bottomNav;
    private FragmentManager fragmentManager;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);
        setBottomNavUI(savedInstanceState);
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
        return super.onOptionsItemSelected(item);
    }

    public void signOutUser(){
        mAuth.getInstance().signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        //startActivity(intent);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void setBottomNavUI(Bundle savedInstanceState){
        toolbar = findViewById(R.id.toolbar);
        bottomNav = findViewById(R.id.bottom_nav);
        toolbar.setBackgroundColor(Color.parseColor("#f7b63c"));
        toolbar.setTitleTextColor(Color.parseColor("#254175"));
        setStatusBarColor("#BC7F2A");
        if(savedInstanceState==null){
            bottomNav.setItemSelected(R.id.navigation_home, true);
            fragmentManager = getSupportFragmentManager();
            HomeFragment homefragmment = new HomeFragment();
            fragmentManager.beginTransaction().
                    replace(R.id.fragment_container, homefragmment).commit();


        }

        bottomNav.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int id) {
                Fragment fragment = null;
                switch (id){
                    case R.id.navigation_home:
                        fragment = new HomeFragment();
                        toolbar.setBackgroundColor(Color.parseColor("#f7b63c"));
                        toolbar.setTitle("Home");
                        toolbar.setTitleTextColor(Color.parseColor
                                ("#254175"));
                        setStatusBarColor("#BC7F2A");
                        break;
                    case R.id.navigation_health:
                        fragment = new HealthFragment();
                        toolbar.setBackgroundColor(Color.parseColor("#70ac60"));
                        toolbar.setTitle("Health");
                        toolbar.setTitleTextColor(Color.parseColor("#c9ecf3"));
                        setStatusBarColor("#4E7E43");
                        break;
                    case R.id.navigation_fitness:
                        fragment = new FitnessFragment();
                        toolbar.setBackgroundColor(Color.parseColor("#973111"));
                        toolbar.setTitle("Fitness");
                        toolbar.setTitleTextColor(Color.parseColor("#e0b46e"));
                        setStatusBarColor("#7C220C");
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
            }
        });
    }

    public void setStatusBarColor(String color){
        Window window =getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(Color.parseColor(color));
    }


}
