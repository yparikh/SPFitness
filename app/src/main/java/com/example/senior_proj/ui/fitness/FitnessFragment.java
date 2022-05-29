package com.example.senior_proj.ui.fitness;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.WorkManager;

import com.example.senior_proj.R;
import com.example.senior_proj.ui.AddWorkoutDialog;
import com.example.senior_proj.ui.ScheduleWorkoutDialog;
import com.example.senior_proj.ui.StopwatchDialog;
import com.example.senior_proj.ui.nutrition.FoodStoredRecyclerViewAdapter;
import com.example.senior_proj.ui.nutrition.NutritionFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;


import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;


import static android.content.Context.SENSOR_SERVICE;


public class FitnessFragment extends Fragment implements SensorEventListener, StepListener {

    private static final String TAG = "FitnessFragmentTAG";
    private FitnessViewModel fitnessViewModel;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String UID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

    private StepDetector stepDetector;
    private SensorManager sensorManager;
    private Sensor accel;

    private int maxSteps = 7500;
    private LocalDate localDate = LocalDate.now();
    private LocalDate selectedDate;
    private YearMonth localYearMonth= YearMonth.from(localDate);
    MaterialDatePicker.Builder<Long> dateBuilder = MaterialDatePicker.Builder.datePicker();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        fitnessViewModel =
                new ViewModelProvider(requireActivity()).get(FitnessViewModel.class);
        View root = inflater.inflate(R.layout.fragment_fitness, container, false);

        Button stopwatchBtn = root.findViewById(R.id.stopwatchBtn);
        Button scheduleWorkoutBtn = root.findViewById(R.id.scheduleWorkoutBtn);
        //Button workoutDatePicker = root.findViewById(R.id.BtnWorkoutDatePicker);
        CircularProgressBar cpBPedometer = root.findViewById(R.id.circularPedometerProgressBar);
        TextView textPercentage = root.findViewById(R.id.pedometerTextPercentage);
        TextView textToGo = root.findViewById(R.id.pedometerTextRemaining);
        ExtendedFloatingActionButton addWorkoutFAB = root.findViewById(R.id.addWorkoutFAB);
        //RecyclerView RVuserWorkoutList = root.findViewById(R.id.RVuserWorkoutList);

        cpBPedometer.setProgressMax(maxSteps);

        //workoutDatePicker.setText(String.valueOf(localDate));
        //dateBuilder.setTitleText(String.valueOf(localDate));
       // dateBuilder.setTheme(R.style.MaterialCalendarTheme);
       // MaterialDatePicker<Long> datePicker = dateBuilder.build();


        sensorManager = (SensorManager) requireActivity().getSystemService(SENSOR_SERVICE);
        assert sensorManager != null;
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        stepDetector = new StepDetector();
        stepDetector.registerListener(this);

        sensorManager.registerListener(this,
                accel, SensorManager.SENSOR_DELAY_NORMAL);



        if(fitnessViewModel.getStepsRecorded().getValue()==null){
            readData(stepsReturned -> {
                cpBPedometer.setProgressWithAnimation(stepsReturned, (long)1000 );
                float currentSteps = stepsReturned;
                float percentFilled = currentSteps / (float) maxSteps;
                textPercentage.setText(String.format(Locale.getDefault(),
                        "%.0f", stepsReturned));
                textToGo.setText(String.format(Locale.getDefault(),
                        "%.1f steps to goal", maxSteps - stepsReturned));
                Log.d(TAG, "onCreateView: initalized with steps: " + stepsReturned);
            });
        }
        else{
            fitnessViewModel.getStepsRecorded().observe(getViewLifecycleOwner(), stepsRecorded -> {
                cpBPedometer.setProgressWithAnimation(stepsRecorded, (long)1000 );
                float currentSteps = stepsRecorded;
                float percentFilled = currentSteps / (float) maxSteps;
                textPercentage.setText(String.format(Locale.getDefault(),
                        "%.0f", currentSteps));
                textToGo.setText(String.format(Locale.getDefault(),
                        "%.1f steps to goal", maxSteps - currentSteps));
            });
        }


        stopwatchBtn.setOnClickListener(v -> {
            StopwatchDialog.display(getParentFragmentManager());
        });

        scheduleWorkoutBtn.setOnClickListener(v -> {
            ScheduleWorkoutDialog.display(getParentFragmentManager());
        });

        addWorkoutFAB.setOnClickListener(v -> {
            AddWorkoutDialog.display(getParentFragmentManager());
        });

//        workoutDatePicker.setOnClickListener(v -> {
//            datePicker.show(getParentFragmentManager(), datePicker.toString());
//
//            datePicker.addOnPositiveButtonClickListener(selection -> {
//                Instant instant = Instant.ofEpochMilli(selection);
//                LocalDate selectedDate;
//                selectedDate = LocalDateTime.ofInstant(instant, ZoneOffset.UTC).toLocalDate();
//                workoutDatePicker.setText(String.valueOf(selectedDate));
//
//                readDateData(storedWorkoutData -> {
//                    userWorkoutList = storedWorkoutData;
//                    mAdapter = new FoodStoredRecyclerViewAdapter(userWorkoutList);
//                    RVuserWorkoutList.setAdapter(mAdapter);
//                    RVuserWorkoutList.setLayoutManager(new LinearLayoutManager(FitnessFragment.this.getActivity()));
//
//                });
//            });
//        });

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        sensorManager.registerListener(this,
                accel, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onStop(){
        super.onStop();
        sensorManager.unregisterListener(this);
        if(fitnessViewModel.getStepsRecorded().getValue() !=null) {
            if (fitnessViewModel.getStepsRecorded().getValue() >= 50) {
                Map<String, Float> fitnessData = new HashMap<>();
                float stepsRecorded = fitnessViewModel.getStepsRecorded().getValue();
                fitnessData.put(String.valueOf(localDate), stepsRecorded);
                db.collection("/healthData").document(UID)
                        .collection("fitnessData")
                        .document(String.valueOf(localYearMonth))
                        .set(fitnessData, SetOptions.merge()).addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d(TAG, "onComplete: Wrote steps to DB");
                            }
                        }
                );
            }
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            stepDetector.updateAccel(
                    event.timestamp, event.values[0], event.values[1],
                    event.values[2]);
            //Log.d(TAG, "onSensorChanged: sensor updated");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    @Override
    public void step(long timeNs) {
        float userSteps;
        if(fitnessViewModel.getStepsRecorded().getValue() == null){
            userSteps =0;
        }
        else{
            userSteps = fitnessViewModel.getStepsRecorded().getValue();
        }
        userSteps++;
        Log.d(TAG, "UPDATED STEPS: " + userSteps);
        fitnessViewModel.getStepsRecorded().setValue(userSteps);
    }

    private void readData(FirestoreCallbackSteps firestoreCallbackSteps){
        CollectionReference collectionReference = db.collection("/fitnessData")
                .document(UID)
                .collection("foodData");


        collectionReference
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                float stepsReturned = 0;
                for (QueryDocumentSnapshot document :
                        Objects.requireNonNull(task.getResult())) {
                    if (document.getId().equals(String.valueOf(localYearMonth))) {
                        Number tempSteps = (Number) document.get(String.valueOf(localDate));
                        assert tempSteps != null;
                        stepsReturned = tempSteps.floatValue();

                    } else {
                        Log.d(TAG, "readDateData: No document of this date was found");
                        //assert storedFoodListItems != null;
                        //storedFoodListItems.add("No food items were found for this date.");
                    }
                }
                firestoreCallbackSteps.onCallback( stepsReturned);
            } else {
                Log.d(TAG, "Error getting document", task.getException());
            }
        });
    }

    private interface FirestoreCallbackSteps{
        void onCallback(Float stepsReturned);
    }

//    private void readDateData(FirestoreCallbackDate firestoreCallbackDate){
//        CollectionReference collectionReference = db.collection("/healthData")
//                .document(UID)
//                .collection("foodData");
//
//
//        collectionReference
//                .get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                List<Workouts> storedFoodListItems = new ArrayList<>();
//                for (QueryDocumentSnapshot document :
//                        Objects.requireNonNull(task.getResult())) {
//                    if (document.getId().equals(String.valueOf(localYearMonth))) {
//                        storedFoodListItems = (List<Workouts>) document.get(selectedDate + ".foodItemlist");
//
//                    } else {
//                        Log.d(TAG, "readDateData: No document of this date was found");
//                        //assert storedFoodListItems != null;
//                        //storedFoodListItems.add("No food items were found for this date.");
//                    }
//                }
//                firestoreCallbackDate.onCallback(workoutsList);
//            } else {
//                Log.d(TAG, "Error getting document", task.getException());
//            }
//        });
//    }
//
//    private interface FirestoreCallbackDate{
//        void onCallback(List<Workouts> workoutsList);
//    }


}
