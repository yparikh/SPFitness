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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HealthFragment extends Fragment {

    private DecoView mDecoView;
    private int mBackIndex;
    private int mSeries1Index;
    private final float mSeriesMax = 50f;
    private Map<String, Object> healthData = new HashMap<>();
    private Map<String, Object> nestedWaterhData = new HashMap<>();
    private String userVol = "ml";
    private int userGoal = 1893;
    public int userDrank = 0;
    public int cstmAmt = 0;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_health, container, false);
        mDecoView = root.findViewById(R.id.dynamicArcView);
        healthData.put("User", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName());
        //create a dialog for the user for their water goal and preference first time

        userDialogPreference(root);
        //draw the progress bar with data
        createBackSeries();
        createDataSeries1(root);
        createEvents();
        Button cupAmt = (Button) root.findViewById(R.id.cupBtn);
        cupAmt.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                userDrank += 237;
                mDecoView.addEvent(new DecoEvent.Builder(userDrank)
                        .setIndex(mSeries1Index)
                        .build());
            }
        });
        Button cstmBtn = (Button) root.findViewById(R.id.customBtn);
        cstmBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final EditText userAmount = new EditText(v.getContext());
                userAmount.setInputType(InputType.TYPE_CLASS_NUMBER);
                new MaterialAlertDialogBuilder(v.getContext())
                        .setTitle("Enter custom amount of water")
                        .setView(userAmount)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                cstmAmt = Integer.parseInt(userAmount.getText().toString());
                                dialog.dismiss();
                            }
                        })
                        .show();
                userDrank += cstmAmt;
            }
        });
        return root;
    }

    private void userDialogPreference(View root){
        final String[] volItems = {"Milliliters (ml)", "Ounces (oz)"};
        final EditText inputGoal = new EditText(this.getContext());
        inputGoal.setInputType(InputType.TYPE_CLASS_NUMBER);
        inputGoal.setHint("Enter your daily water goal.");
        if(FirebaseAuth.getInstance().getCurrentUser().getMetadata().getCreationTimestamp()
        ==FirebaseAuth.getInstance().getCurrentUser().getMetadata().getLastSignInTimestamp()){
            //Dialog
            //change hard code to resource strings
            new MaterialAlertDialogBuilder(root.getContext())
                    .setTitle("Set Water Preferences and Goal")
                    .setSingleChoiceItems(volItems, 0, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(which==0){
                                userVol = "ml";
                            }
                            else{
                                userVol = "oz";
                            }
                        }
                    })
                    .setView(inputGoal)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                             userGoal = Integer.parseInt(inputGoal.getText().toString());
                            dialog.dismiss();
                        }
                    })
                    .show();
        }
    }

    private void createBackSeries() {
        SeriesItem seriesItem = new SeriesItem.Builder(Color.parseColor("#FFE2E2E2"))
                .setRange(0, mSeriesMax, 0)
                .setInitialVisibility(true)
                .build();

        mBackIndex = mDecoView.addSeries(seriesItem);
    }

    private void createDataSeries1(View root) {
        final SeriesItem seriesItem = new SeriesItem.Builder(Color.parseColor("#FFFF8800"))
                .setRange(0, userGoal, 0)
                .setInitialVisibility(false)
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
        mDecoView.executeReset();

        mDecoView.addEvent(new DecoEvent.Builder(userGoal)
                .setIndex(mBackIndex)
                .setDuration(3000)
                .setDelay(100)
                .build());

        mDecoView.addEvent(new DecoEvent.Builder(DecoDrawEffect.EffectType.EFFECT_SPIRAL_EXPLODE)
                .setIndex(mSeries1Index)
                .setDelay(21000)
                .setDuration(3000)
                .setDisplayText("GOAL!")
                .setListener(new DecoEvent.ExecuteEventListener() {
                    @Override
                    public void onEventStart(DecoEvent decoEvent) {

                    }

                    @Override
                    public void onEventEnd(DecoEvent decoEvent) {
                        createEvents();
                    }
                })
                .build());
    }

    public void cupClick(View root){
        userDrank += 237;
        mDecoView.addEvent(new DecoEvent.Builder(userDrank)
                .setIndex(mBackIndex)
                .setDuration(3000)
                .setDelay(100)
                .build());
    }

    public void customClick(View root){


    }

}
