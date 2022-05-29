package com.example.senior_proj.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.senior_proj.NotificationWorker;
import com.example.senior_proj.R;
import com.example.senior_proj.ui.nutrition.FoodStoredRecyclerViewAdapter;
import com.example.senior_proj.ui.nutrition.NutritionFragment;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;

import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


public class ScheduleWorkoutDialog extends DialogFragment {

    private static final String TAG = "scheduleWorkoutDialogTAG";

    private Toolbar toolbar;
    private Button scheduleWorkoutBtn;
    private Button datePickerBtn;
    private TextInputEditText TIETworkoutName;
    private MaterialDatePicker.Builder<Long> dateBuilder = MaterialDatePicker.Builder.datePicker();
    private LocalDate selectedDate;

    public static void display(FragmentManager fragmentManager) {
        ScheduleWorkoutDialog scheduleWorkoutDialog = new ScheduleWorkoutDialog();
        scheduleWorkoutDialog.show(fragmentManager, TAG);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_scheduleworkout, container, false);

        dateBuilder.setTheme(R.style.MaterialCalendarTheme);
        toolbar = view.findViewById(R.id.scheduleWorkoutToolbar);
        scheduleWorkoutBtn = view.findViewById(R.id.saveWorkoutBtn);
        datePickerBtn = view.findViewById(R.id.calendarBtn);
        TIETworkoutName = view.findViewById(R.id.TIETworkoutName);


        return view;
    }

    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar.setNavigationOnClickListener(v -> dismiss());

        selectedDate = LocalDate.now().plus(1, ChronoUnit.DAYS);
        MaterialDatePicker<Long> datePicker = dateBuilder.build();
        datePickerBtn.setText(String.valueOf(selectedDate));

        datePickerBtn.setOnClickListener(v -> {
            datePicker.show(getParentFragmentManager(), datePicker.toString());

            datePicker.addOnPositiveButtonClickListener(selection -> {
                Instant instant = Instant.ofEpochMilli(selection);
                selectedDate = LocalDateTime.ofInstant(instant, ZoneOffset.UTC).toLocalDate();
                datePickerBtn.setText(String.valueOf(selectedDate));

            });
        });

        scheduleWorkoutBtn.setOnClickListener(v -> {
            if(!Objects.requireNonNull(TIETworkoutName.getText()).toString().equals("")){
                LocalDate userWorkoutDate;
                Instant instant;
                userWorkoutDate = LocalDate.parse(datePickerBtn.getText().toString());
                String notificationDesc = "Reminder! Workout: " +
                        TIETworkoutName.getText().toString() + " today.";
                instant = userWorkoutDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
                long dateToMilli = instant.toEpochMilli();
                Data data = new Data.Builder()
                        .putString(NotificationWorker.TASK_DESC,notificationDesc)
                        .build();

                final OneTimeWorkRequest workoutReminderRequest = new OneTimeWorkRequest.Builder(
                        NotificationWorker.class)
                        .setInputData(data)
                        //.setInitialDelay(dateToMilli, TimeUnit.MILLISECONDS)
                        .addTag("workoutReminder")
                        .build();
                WorkManager.getInstance().enqueue(workoutReminderRequest);

                Log.d(TAG, "onViewCreated: notification reminder will be sent on"
                        + String.valueOf(userWorkoutDate) + " with msg " + notificationDesc);

                dismiss();
            }
        });


    }


    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            Objects.requireNonNull(dialog.getWindow()).setLayout(width, height);
        }
    }



}