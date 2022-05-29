package com.example.senior_proj.ui.water;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.senior_proj.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.MonthDay;
import java.time.Period;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class WaterViewModel extends ViewModel {

    private static final String TAG = "waterViewModelTag";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private String UID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    private ListenerRegistration registration;

    private MutableLiveData<Float> userWaterDrank;
    private MutableLiveData<Float> userWaterGoal;
    private MutableLiveData<List<Object>> userWaterWeek;
    private MutableLiveData<List<String>> userWaterWeekLabel;
    private MutableLiveData<List<Object>> userWaterMonth;
    private MutableLiveData<List<String>> userWaterMonthLabel;

    private LocalDate localDate = LocalDate.now();
    private YearMonth localYearMonth = YearMonth.from(localDate);

    public WaterViewModel() {
        userWaterGoal = new MutableLiveData<>();
        userWaterDrank = new MutableLiveData<>();
        userWaterWeek = new MutableLiveData<>();
        userWaterWeekLabel = new MutableLiveData<>();
        userWaterMonth = new MutableLiveData<>();
        userWaterMonthLabel = new MutableLiveData<>();

        //retrieve the water goal
        db.collection("/healthData")
                .document(UID).collection("waterData")
                .document("waterGoal").get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        DocumentSnapshot doc = task.getResult();
                        assert doc != null;
                        if(doc.exists()){
                            Number tempGoal = (Number) doc.get("waterGoal");
                            assert tempGoal != null;
                            Float waterGoal = tempGoal.floatValue();
                            userWaterGoal.setValue(waterGoal);
                        }
                    }
                });

        //retrieve the water drank data for the graphs
        DocumentReference docRef = db.collection("/healthData")
                .document(UID).collection("waterData")
                .document(String.valueOf(localYearMonth));
        //https://stackoverflow.com/questions/51765985/get-all-fields-in-a-document-in-a-list-firestore-java
            registration = docRef.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                Log.w(TAG, "Listen failed.", e);
                return;
            }
            if (snapshot != null && snapshot.exists()) {
                Log.d(TAG, "Current data: " + snapshot.getData());
                Map<String, Object> mapUserDrank = snapshot.getData();
                Number savedGoal = (Number) snapshot.get(String.valueOf(localDate));
                if (savedGoal != null) {
                    float convertedGoal = savedGoal.floatValue();
                    userWaterDrank.setValue(convertedGoal);
                }
                assert mapUserDrank != null;
                List<String> keyList = new ArrayList<>(mapUserDrank.keySet());
                List<String> listString7 = new ArrayList<>();
                List<String> listString = new ArrayList<>();
                List<Object> listobj7 = new ArrayList<>();
                List<Object> listobj = new ArrayList<>();
                Collections.sort(keyList);
                for (final String key : keyList) {

                    LocalDate tempDate = LocalDate.parse(key,
                            DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    MonthDay monthDay = MonthDay.from(tempDate);
                    String formattedDate = monthDay.format(DateTimeFormatter.ofPattern("MM-dd"));
                    LocalDate startOfWeek = localDate.minus(Period.ofDays(6));
                    if (tempDate.isAfter(startOfWeek)|| tempDate.isEqual(startOfWeek)) {
                        listString7.add(String.valueOf(formattedDate));
                        listobj7.add(mapUserDrank.get(key));
                    }
                    listString.add(String.valueOf(formattedDate));
                    Number tempNum = (Number) mapUserDrank.get(key);
                    listobj.add(tempNum);
                }
                userWaterWeek.setValue(listobj7);
                userWaterWeekLabel.setValue(listString7);
                userWaterMonth.setValue(listobj);
                userWaterMonthLabel.setValue(listString);

            } else {
                Log.d(TAG, "Current data: null");

            }
        });

    }

    @Override
    protected void onCleared() {
        super.onCleared();
        registration.remove();
    }

    MutableLiveData<Float> getUserDrank() {return userWaterDrank;}
    MutableLiveData<Float> getUserWaterGoal() {return userWaterGoal;}
    MutableLiveData<List<Object>> getUserWaterWeek() {return userWaterWeek;}
    MutableLiveData<List<String>> getUserWaterWeekLabel() {return userWaterWeekLabel;}
    MutableLiveData<List<Object>> getUserWaterMonth() {return userWaterMonth;}
    MutableLiveData<List<String>> getUserWaterMonthLabel() {return userWaterMonthLabel;}
}