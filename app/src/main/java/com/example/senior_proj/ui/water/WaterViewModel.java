package com.example.senior_proj.ui.water;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class WaterViewModel extends ViewModel {

    private static final String TAG = "waterViewModelTag";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private Map<String, Object> usrData = new HashMap<>();
    private String UID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

    private MutableLiveData<Float> userWaterDrank;
    private MutableLiveData<Float> userWaterGoal;

    private Date currentTime = Calendar.getInstance().getTime();
    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    private String formattedDate = df.format(currentTime);

    public WaterViewModel() {
        userWaterGoal = new MutableLiveData<>();
        userWaterDrank = new MutableLiveData<>();
        //userWaterDrank.setValue((float) 0);
        DocumentReference docRef = db.collection("/healthData")
                .document(UID).collection("waterData").document(formattedDate);
        // Get the document, forcing the SDK to use the offline cache
        docRef.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                Log.w(TAG, "Listen failed.", e);
                return;
            }

            if (snapshot != null && snapshot.exists()) {
                Log.d(TAG, "Current data: " + snapshot.getData());
                float savedGoal = (long) snapshot.get("u");
                //userWaterGoal.setValue(savedGoal);
            } else {
                Log.d(TAG, "Current data: null");
            }
        });





        //userWaterGoal.setValue((float) 1893);
    }

    MutableLiveData<Float> getUserDrank() {return userWaterDrank;}
    MutableLiveData<Float> getUserWaterGoal() {return userWaterGoal;}
}