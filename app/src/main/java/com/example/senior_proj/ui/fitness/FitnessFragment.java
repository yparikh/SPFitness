package com.example.senior_proj.ui.fitness;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.ContextThemeWrapper;
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

public class FitnessFragment extends Fragment {

    private FitnessViewModel fitnessViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        fitnessViewModel =
                ViewModelProviders.of(this).get(FitnessViewModel.class);
        View root = inflater.inflate(R.layout.fragment_fitness, container, false);
        final TextView textView = root.findViewById(R.id.text_notifications);
        fitnessViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });



        return root;

    }




}
