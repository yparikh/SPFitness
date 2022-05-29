package com.example.senior_proj.ui.nutrition;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class NutritionViewModel extends ViewModel {
    private static final String TAG = "healthViewModelTAG";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String UID = Objects.requireNonNull(FirebaseAuth.
            getInstance().getCurrentUser()).getUid();
    private ListenerRegistration registration;

    private MutableLiveData<Float> userTotalCalories;
    private MutableLiveData<Float> userTotalCarbs;
    private MutableLiveData<Float> userTotalFats;
    private MutableLiveData<Float> userTotalProtein;
    private MutableLiveData<Float> userCalories;
    private MutableLiveData<Float> userCarbs;
    private MutableLiveData<Float> userFats;
    private MutableLiveData<Float> userProtein;
    private MutableLiveData<List<String>> userFoodItems;

    private LocalDate localDate = LocalDate.now();
    private YearMonth localYearMonth = YearMonth.from(localDate);

    private Number tempCal, tempCarbs, tempProtein, tempFats;
    private Float dbCal, dbCarbs, dbProtein, dbFats;

    public NutritionViewModel() {
        userTotalCalories = new MutableLiveData<>();
        userTotalCarbs = new MutableLiveData<>();
        userTotalFats = new MutableLiveData<>();
        userTotalProtein = new MutableLiveData<>();
        userCalories = new MutableLiveData<>();
        userCarbs = new MutableLiveData<>();
        userFats = new MutableLiveData<>();
        userProtein = new MutableLiveData<>();
        userFoodItems = new MutableLiveData<>();

        db.collection("/healthData")
                .document(UID).collection("foodData")
                .document("dailyValues").get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        DocumentSnapshot doc = task.getResult();
                        assert doc != null;
                        if(doc.exists()){
                            tempCal = (Number) doc.get("userCalories");
                            assert tempCal != null;
                            dbCal = tempCal.floatValue();
                            userTotalCalories.setValue(dbCal);
                            tempCarbs = (Number) doc.get("userCarbs");
                            assert tempCarbs != null;
                            dbCarbs = tempCarbs.floatValue();
                            userTotalCarbs.setValue(dbCarbs);
                            tempFats = (Number) doc.get("userFats");
                            assert tempFats != null;
                            dbFats = tempFats.floatValue();
                            userTotalFats.setValue(dbFats);
                            tempProtein = (Number) doc.get("userProtein");
                            assert tempProtein != null;
                            dbProtein = tempProtein.floatValue();
                            userTotalProtein.setValue(dbProtein);
                        }
                    }
                });

        DocumentReference docRef = db.collection("/healthData")
                .document(UID).collection("foodData")
                .document(String.valueOf(localYearMonth));
        registration = docRef.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                Log.w(TAG, "Listen failed.", e);
                return;
            }
            if (snapshot != null && snapshot.exists()) {
                Log.d(TAG, "Current data: " + snapshot.getData());
                Number savedCalories = (Number) snapshot.get(localDate +".dailyCalories");
                if (savedCalories != null) {
                    float convertedCalories = savedCalories.floatValue();
                    userCalories.setValue(convertedCalories);
                }
                Number savedCarbs = (Number) snapshot.get(localDate +".dailyCarbs");
                if (savedCarbs != null) {
                    float convertedCarbs = savedCarbs.floatValue();
                    userCarbs.setValue(convertedCarbs);
                }
                Number savedFats = (Number) snapshot.get(localDate +".dailyFats");
                if (savedFats != null) {
                    float convertedFats = savedFats.floatValue();
                    userFats.setValue(convertedFats);
                }
                Number savedProtein = (Number) snapshot.get(localDate +".dailyProtein");
                if (savedProtein != null) {
                    float convertedProtein = savedProtein.floatValue();
                    userProtein.setValue(convertedProtein);
                }
                List<String> savedFoodItems = (List<String>) snapshot.get(localDate +".foodItemlist");
                userFoodItems.setValue(savedFoodItems);

            } else {
                Log.d(TAG, "Current data: null");
                Map<String, Object> foodData = new HashMap<>();
                Map<String, Object> nestedFoodData = new HashMap<>();
                foodData.put(String.valueOf(localDate), 0);


                nestedFoodData.put("foodItemlist", Collections.emptyList());
                nestedFoodData.put("dailyCalories", 0);
                nestedFoodData.put("dailyCarbs", 0);
                nestedFoodData.put("dailyProtein", 0);
                nestedFoodData.put("dailyFats", 0);
                foodData.put(String.valueOf(localDate), nestedFoodData );

                db.collection("/healthData")
                        .document(UID).collection("foodData")
                        .document(String.valueOf(localYearMonth)).set(foodData);
            }
        });

    }

    @Override
    protected void onCleared() {
        super.onCleared();
        registration.remove();
    }

    MutableLiveData<Float> getUserTotalCalories() {return userTotalCalories;}
    MutableLiveData<Float> getUserTotalCarbs() {return userTotalCarbs;}
    MutableLiveData<Float> getUserTotalFats() {return userTotalFats;}
    MutableLiveData<Float> getUserTotalProtein() {return userTotalProtein;}
    MutableLiveData<Float> getUserCalories() {return userCalories;}
    MutableLiveData<Float> getUserCarbs() {return userCarbs;}
    MutableLiveData<Float> getUserFats() {return userFats;}
    MutableLiveData<Float> getUserProtein() {return userProtein;}
    MutableLiveData<List<String>> getUserFoodItems() {return userFoodItems;}
}
