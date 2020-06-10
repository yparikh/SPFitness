package com.example.senior_proj.ui.home;

import android.app.ActionBar;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.senior_proj.MainActivity;
import com.example.senior_proj.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

public class HomeFragment extends Fragment {
    private LocalTime localTime = LocalTime.now();
    //private LocalTime localTime = LocalTime.of(21, 01, 01);

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        TextView Title = root.findViewById(R.id.TV_home_title);
        ImageView IV = root.findViewById(R.id.IVTitle);
        String welcomeText = "Welcome "+ Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName();
        Title.setText(welcomeText);
        //
        if(localTime.isAfter(LocalTime.NOON)&&
                localTime.isBefore(LocalTime.of(20, 0, 0))){
            IV.setImageResource(R.drawable.ic_cityscapes_afternoon);
            IV.invalidate();

        }
        if(localTime.isAfter(LocalTime.of(20, 0, 0))||
                localTime.isBefore(LocalTime.of(4, 0, 0))){
            IV.setImageResource(R.drawable.ic_cityscapes_night);
            IV.invalidate();
        }
        return root;
    }
}
