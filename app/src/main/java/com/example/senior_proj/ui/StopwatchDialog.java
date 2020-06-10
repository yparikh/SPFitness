package com.example.senior_proj.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.example.senior_proj.R;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;


public class StopwatchDialog extends DialogFragment {

    private static final String TAG = "stopwatch_dialog";

    private Toolbar toolbar;
    private Chronometer chronometer;
    private Button startBtn;
    private Button stopBtn;
    private Button resetBtn;

    public static void display(FragmentManager fragmentManager) {
        StopwatchDialog stopwatchDialog = new StopwatchDialog();
        stopwatchDialog.show(fragmentManager, TAG);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_stopwatch, container, false);


        toolbar = view.findViewById(R.id.stopwatchToolBar);
        chronometer = view.findViewById(R.id.ChronometerStopwatch);
        startBtn = view.findViewById(R.id.startBtn);
        stopBtn = view.findViewById(R.id.stopBtn);
        resetBtn = view.findViewById(R.id.resetBtn);

        return view;
    }

    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar.setNavigationOnClickListener(v -> dismiss());

        chronometer.setBase(SystemClock.elapsedRealtime());

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //chronometer.setBase(SystemClock.elapsedRealtime());
                chronometer.start();
                Log.d(TAG, "onClick: chronometer started");
            }
        });

        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chronometer.stop();
            }
        });

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chronometer.stop();
                chronometer.setBase(SystemClock.elapsedRealtime());
            }
        });


    }


    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            Objects.requireNonNull(dialog.getWindow()).setLayout(width, height);
        }
    }

}