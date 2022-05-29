package com.example.senior_proj.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.senior_proj.NotificationWorker;
import com.example.senior_proj.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class AddWorkoutDialog extends DialogFragment {

    private static final String TAG = "scheduleWorkoutDialogTAG";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String UID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

    private Toolbar toolbar;
    private Button addWorkoutBtn;
    private Button datePickerBtn;
    private TextInputEditText TIETexerciseName;
    private TextInputEditText TIETexerciseName2;
    private TextInputEditText TIETexerciseName3;
    private TextInputEditText TIETreps;
    private TextInputEditText TIETreps2;
    private TextInputEditText TIETreps3;
    private TextInputEditText TIETsets;
    private TextInputEditText TIETsets2;
    private TextInputEditText TIETsets3;
    private MaterialDatePicker.Builder<Long> dateBuilder = MaterialDatePicker.Builder.datePicker();
    private LocalDate selectedDate;

    public static void display(FragmentManager fragmentManager) {
        AddWorkoutDialog scheduleWorkoutDialog = new AddWorkoutDialog();
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
        View view = inflater.inflate(R.layout.fragment_addworkout, container, false);

        dateBuilder.setTheme(R.style.MaterialCalendarTheme);
        toolbar = view.findViewById(R.id.addWorkoutToolbar);
        addWorkoutBtn = view.findViewById(R.id.addWorkoutBtn);
        datePickerBtn = view.findViewById(R.id.exerciseCalendarBtn);
        TIETexerciseName = view.findViewById(R.id.TIETexerciseName);
        TIETexerciseName2 = view.findViewById(R.id.TIETexerciseName2);
        TIETexerciseName3 = view.findViewById(R.id.TIETexerciseName3);
        TIETreps = view.findViewById(R.id.TIETreps);
        TIETreps2 = view.findViewById(R.id.TIETreps2);
        TIETreps3 = view.findViewById(R.id.TIETreps3);
        TIETsets = view.findViewById(R.id.TIETsets);
        TIETsets2 = view.findViewById(R.id.TIETsets2);
        TIETsets3 = view.findViewById(R.id.TIETsets3);


        return view;
    }

    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar.setNavigationOnClickListener(v -> dismiss());

        selectedDate = LocalDate.now();
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

       addWorkoutBtn.setOnClickListener(v -> {
           List<Workouts> userWorkout = new ArrayList<>();
            if(!Objects.requireNonNull(TIETexerciseName.getText()).toString().equals("")
                    && !Objects.requireNonNull(TIETsets.getText()).toString().equals("")
                    && !Objects.requireNonNull(TIETreps.getText()).toString().equals("")){
                Workouts workouts1 = new Workouts();
                workouts1.setWorkoutName(TIETexerciseName.getText().toString());
                workouts1.setWorkoutReps(Integer.parseInt(TIETreps.getText().toString()));
                workouts1.setWorkoutSets(Integer.parseInt(TIETsets.getText().toString()));
                userWorkout.add(workouts1);
            }
           if(!Objects.requireNonNull(TIETexerciseName2.getText()).toString().equals("")
                   && !Objects.requireNonNull(TIETsets2.getText()).toString().equals("")
                   && !Objects.requireNonNull(TIETreps2.getText()).toString().equals("")){
               Workouts workouts2 = new Workouts();
               workouts2.setWorkoutName(TIETexerciseName2.getText().toString());
               workouts2.setWorkoutReps(Integer.parseInt(TIETreps2.getText().toString()));
               workouts2.setWorkoutSets(Integer.parseInt(TIETsets2.getText().toString()));
               userWorkout.add(workouts2);
           }
           if(!Objects.requireNonNull(TIETexerciseName3.getText()).toString().equals("")
                   && !Objects.requireNonNull(TIETsets3.getText()).toString().equals("")
                   && !Objects.requireNonNull(TIETreps3.getText()).toString().equals("")){
               Workouts workouts3 = new Workouts();
               workouts3.setWorkoutName(TIETexerciseName3.getText().toString());
               workouts3.setWorkoutReps(Integer.parseInt(TIETreps3.getText().toString()));
               workouts3.setWorkoutSets(Integer.parseInt(TIETsets3.getText().toString()));
               userWorkout.add(workouts3);
           }
           LocalDate selectedDate = LocalDate.parse(datePickerBtn.getText().toString());
           YearMonth localYearMonth= YearMonth.from(selectedDate);

           Map<String, Object> workoutData = new HashMap<>();
           workoutData.put(datePickerBtn.getText().toString(), userWorkout);
           db.collection("/healthData")
                   .document(UID).collection("workoutData")
                   .document(String.valueOf(localYearMonth))
                   .set(workoutData, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
               @Override
               public void onComplete(@NonNull Task<Void> task) {
                   if(task.isSuccessful()){
                       Log.d(TAG, "onComplete: completed backup to db");
                   }

               }
           });
           dismiss();
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

    private class Workouts{
        private String workoutName;
        private int workoutReps;
        private int workoutSets;

        public Workouts(){}

        public Workouts(String workoutName, int workoutReps, int workoutSets){
            this.workoutName = workoutName;
            this.workoutReps = workoutReps;
            this.workoutSets = workoutSets;
        }


        public String getWorkoutName() {
            return workoutName;
        }

        public void setWorkoutName(String workoutName) {
            this.workoutName = workoutName;
        }

        public int getWorkoutReps() {
            return workoutReps;
        }

        public void setWorkoutReps(int workoutReps) {
            this.workoutReps = workoutReps;
        }

        public int getWorkoutSets() {
            return workoutSets;
        }

        public void setWorkoutSets(int workoutSets) {
            this.workoutSets = workoutSets;
        }
    }


}