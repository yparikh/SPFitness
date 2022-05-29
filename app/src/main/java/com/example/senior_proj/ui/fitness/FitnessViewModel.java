package com.example.senior_proj.ui.fitness;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FitnessViewModel extends ViewModel {

    private static final String TAG = "FitnessViewModelTAG";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String UID = Objects.requireNonNull(FirebaseAuth.
            getInstance().getCurrentUser()).getUid();

    private LocalDate localDate = LocalDate.now();
    private YearMonth localYearMonth = YearMonth.from(localDate);

    private MutableLiveData<Float> stepsRecorded;

    public FitnessViewModel() {
        stepsRecorded = new MutableLiveData<>();

        db.collection("/healthData")
                .document(UID).collection("fitnessData")
                .document(String.valueOf(localYearMonth)).get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        DocumentSnapshot doc = task.getResult();
                        assert doc != null;
                        if(doc.exists()){
                            Number tempSteps;
                            tempSteps = (Number) doc.get(String.valueOf(localDate));
                            if(tempSteps != null){
                                stepsRecorded.setValue(tempSteps.floatValue());
                                Log.d(TAG, "FitnessViewModel: steps" + stepsRecorded);
                            }
                            else{
                                Map<String, Float> fitnessData = new HashMap<>();
                                fitnessData.put(String.valueOf(localDate), 0f);

                                db.collection("/healthData")
                                        .document(UID).collection("fitnessData")
                                        .document(String.valueOf(localYearMonth)).set(fitnessData, SetOptions.merge())
                                        .addOnCompleteListener(task1 -> Log.d(TAG, "FitnessViewModel: should have written to db"));
                            }
                        }
                        else{

                            Map<String, Float> fitnessData = new HashMap<>();
                            fitnessData.put(String.valueOf(localDate), 0f);

                            db.collection("/healthData")
                                    .document(UID).collection("fitnessData")
                                    .document(String.valueOf(localYearMonth)).set(fitnessData, SetOptions.merge())
                                    .addOnCompleteListener(task1 -> Log.d(TAG, "FitnessViewModel: should have written to db"));

                        }
                    }

                });
    }


    public MutableLiveData<Float> getStepsRecorded() {
        return stepsRecorded;
    }
}