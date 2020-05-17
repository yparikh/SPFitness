package com.example.senior_proj.ui.health;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.senior_proj.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.DecoDrawEffect;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class HealthFragment extends Fragment {
    //TODO mutable works so update the userdata from the viewmodel for the userdrnk
    private DecoView mDecoView;
    private CircularProgressBar circularProgressBar;
    private int mBackIndex;
    private int mSeries1Index;
    private final float mSeriesMax = 50f;
    private Map<String, Object> healthData = new HashMap<>();
    private Map<String, Object> nestedWaterData = new HashMap<>();
    private String userVol = "ml";
    private float userGoal;
    private float userDrank;
    private int cstmAmt = 0;
    private HealthViewModel healthViewModel;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        healthViewModel =
                new ViewModelProvider(requireActivity()).get(HealthViewModel.class);
        View root = inflater.inflate(R.layout.fragment_health, container, false);
        circularProgressBar= root.findViewById(R.id.circularProgressBar);
        final TextView textPercentage = (TextView) root.findViewById(R.id.textPercentage);
        final TextView textToGo = (TextView) root.findViewById(R.id.textRemaining);
        healthData.put("User", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName());
        //create a dialog for the user for their water goal and preference first time
        healthViewModel.getFirstTime().observe(getViewLifecycleOwner(), firstTime -> {
           if(firstTime) {
               userDialogPreference(root);
           }
           else{
               healthViewModel.getUserWaterGoal().observe(
                       getViewLifecycleOwner(), userWaterGoal-> userGoal = userWaterGoal);
               healthViewModel.getUserDrank().observe(
                       getViewLifecycleOwner(), userWaterDrank-> userDrank = userWaterDrank);
               healthViewModel.getUserVolPref().observe(
                       getViewLifecycleOwner(), userVolPref-> userVol = userVolPref);
               float percentFilled = userDrank/userGoal;
               textPercentage.setText(String.format(Locale.getDefault(), "%.0f%%", percentFilled * 100f));
               textToGo.setText(String.format(Locale.getDefault(),"%.1f mL to goal", userGoal - userDrank));
               circularProgressBar.setProgress(userDrank);
               circularProgressBar.setProgressMax(userGoal);
        }
        });

        //draw the progress bar with data


        Button cupAmt = root.findViewById(R.id.cupBtn);
        cupAmt.setOnClickListener(v -> {
            userDrank += 237;
            healthViewModel.getUserDrank().setValue(userDrank);
            circularProgressBar.setProgressWithAnimation(userDrank, (long) 1000);
            float percentFilled = userDrank/userGoal;
            textPercentage.setText(String.format(Locale.getDefault(), "%.0f%%", percentFilled * 100f));
            textToGo.setText(String.format(Locale.getDefault(),"%.1f mL to goal", userGoal - userDrank));

        });
        Button cstmBtn = root.findViewById(R.id.customBtn);
        cstmBtn.setOnClickListener(v -> {
            final EditText userAmount = new EditText(v.getContext());
            userAmount.setInputType(InputType.TYPE_CLASS_NUMBER);
            new MaterialAlertDialogBuilder(v.getContext())
                    .setTitle("Enter custom amount of water")
                    .setView(userAmount)
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                        cstmAmt = Integer.parseInt(userAmount.getText().toString());
                        userDrank += cstmAmt;
                        healthViewModel.getUserDrank().setValue(userDrank);

                    })
                    .show();
        });
        return root;
    }

    private void userDialogPreference(View root){
        final String[] volItems = {"Milliliters (ml)", "Ounces (oz)"};
        final EditText inputGoal = new EditText(this.getContext());
        inputGoal.setInputType(InputType.TYPE_CLASS_NUMBER);
        inputGoal.setHint("Enter your daily water goal.");

        healthViewModel.getFirstTime().setValue(false);
        //if(FirebaseAuth.getInstance().getCurrentUser().getMetadata().getCreationTimestamp()
        //==FirebaseAuth.getInstance().getCurrentUser().getMetadata().getLastSignInTimestamp()){

            //Dialog
            //change hard code to resource strings
            new MaterialAlertDialogBuilder(root.getContext())
                    .setTitle("Set Water Preferences and Goal")
                    .setSingleChoiceItems(volItems, 0, (dialog, which) -> {
                        if(which==0){
                            userVol = "ml";
                        }
                        else{
                            userVol = "oz";
                            healthViewModel.getUserVolPref().setValue("oz");
                        }
                    })
                    .setView(inputGoal)
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                         userGoal = Integer.parseInt(inputGoal.getText().toString());
                         healthViewModel.getUserWaterGoal().setValue(userGoal);
                        final TextView textToGo = (TextView) root.findViewById(R.id.textRemaining);
                        final TextView textPercentage = (TextView) root.findViewById(R.id.textPercentage);
                        float percentFilled = userDrank/userGoal;
                        textPercentage.setText(String.format(Locale.getDefault(), "%.0f%%", percentFilled * 100f));
                        textToGo.setText(String.format(Locale.getDefault(),"%.1f mL to goal", userGoal - userDrank));
                         circularProgressBar.setProgress(userDrank);
                         circularProgressBar.setProgressMax(userGoal);
                        dialog.dismiss();
                    })
                    .show();
    }

    private void createBackSeries() {
        healthViewModel.getUserWaterGoal().observe(
                getViewLifecycleOwner(), userWaterGoal-> userGoal = userWaterGoal);
        SeriesItem seriesItem = new SeriesItem.Builder(Color.parseColor("#FFE2E2E2"))
                .setRange(0, userGoal, 0)
                .setInitialVisibility(true)
                .build();

        mBackIndex = mDecoView.addSeries(seriesItem);
    }

    private void createDataSeries1(View root) {
        healthViewModel.getUserWaterGoal().observe(
                getViewLifecycleOwner(), userWaterGoal-> userGoal = userWaterGoal);
        healthViewModel.getUserDrank().observe(
                getViewLifecycleOwner(), userWaterDrank-> userDrank = userWaterDrank);
        if(userDrank > userGoal){
            userDrank = userGoal;
        }
        final SeriesItem seriesItem = new SeriesItem.Builder(Color.parseColor("#FFFF8800"))
                .setRange(0, userGoal, userDrank)
                .setInitialVisibility(true)
                .build();

        final TextView textPercentage = (TextView) root.findViewById(R.id.textPercentage);
        seriesItem.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
            @Override
            public void onSeriesItemAnimationProgress(float percentComplete, float currentPosition) {
                float percentFilled = ((currentPosition - seriesItem.getMinValue()) / (seriesItem.getMaxValue() - seriesItem.getMinValue()));
                textPercentage.setText(String.format("%.0f%%", percentFilled * 100f));
            }

            @Override
            public void onSeriesItemDisplayProgress(float percentComplete) {

            }
        });

        final TextView textToGo = (TextView) root.findViewById(R.id.textRemaining);
        seriesItem.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
            @Override
            public void onSeriesItemAnimationProgress(float percentComplete, float currentPosition) {
                textToGo.setText(String.format("%.1f mL to goal", seriesItem.getMaxValue() - currentPosition));

            }

            @Override
            public void onSeriesItemDisplayProgress(float percentComplete) {

            }
        });

        mSeries1Index = mDecoView.addSeries(seriesItem);
    }

    private void createEvents() {
        //mDecoView.executeReset();
        mDecoView.addEvent(new DecoEvent.Builder(userGoal)
                .setIndex(mBackIndex)
                .build());
    }
}
