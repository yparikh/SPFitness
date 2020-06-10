package com.example.senior_proj.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.example.senior_proj.R;
import com.example.senior_proj.ui.nutrition.NutritionFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class SettingsDialog extends DialogFragment {

    private static final String TAG = "settings_dialog";

    private Toolbar toolbar;
    private Button button;
    private TextInputEditText tietAge;
    private TextInputEditText tietHeight;
    private TextInputEditText tietWeight;
    private TextInputEditText tietWaterGoal;
    AutoCompleteTextView ACTVsex;
    AutoCompleteTextView ACTVactivity;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String UID = Objects.requireNonNull(FirebaseAuth.
            getInstance().getCurrentUser()).getUid();

    private Map<String, Object> waterCollection = new HashMap<>();
    private Map<String, Object> foodCollection = new HashMap<>();

    public static void display(FragmentManager fragmentManager) {
        SettingsDialog settingsDialog = new SettingsDialog();
        settingsDialog.show(fragmentManager, TAG);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        toolbar = view.findViewById(R.id.settingsToolBar);
        button = view.findViewById(R.id.settingsSaveButton);
        tietAge = view.findViewById(R.id.settingsTIETAge);
        tietWeight = view.findViewById(R.id.settingsTIETWeight);
        tietHeight = view.findViewById(R.id.settingsTIETHeight);
        tietWaterGoal = view.findViewById(R.id.settingsTIETWater);
        ACTVsex = view.findViewById(R.id.settingsACTVsex);
        ACTVactivity= view.findViewById(R.id.settingsACTVActivityLevel);

        String[] sexArray = new String[] {"Female", "Male"};
        String[] activityArray = new String[] {"Sedentary", "Light Exercise 1-2 Days/Week",
                "Moderate Exercise 2-3 Days/Week", "Hard Exercise 4-5 Days/Week",
                "Very Hard Exercise 6-7 Days/Week", "Professional Athlete"};

        readSettingsData((userWeight, userHeight, waterGoal, userSex, userAge, userActivity) -> {
            tietAge.setText(String.valueOf(userAge));
            tietHeight.setText(String.valueOf(userHeight));
            tietWeight.setText(String.valueOf(userWeight));
            tietWaterGoal.setText(String.valueOf(waterGoal));
            ACTVactivity.setText(userActivity);
            ACTVsex.setText(userSex);

            ArrayAdapter<String> adapter =
                    new ArrayAdapter<>(
                            requireContext(),
                            R.layout.list_item,
                            sexArray);
            ArrayAdapter<String> adapterActivity =
                    new ArrayAdapter<>(
                            requireContext(),
                            R.layout.list_item,
                            activityArray);

            ACTVsex = view.findViewById(R.id.settingsACTVsex);
            ACTVsex.setInputType(InputType.TYPE_NULL);
            ACTVsex.setAdapter(adapter);

            ACTVactivity = view.findViewById(R.id.settingsACTVActivityLevel);
            ACTVactivity.setInputType(InputType.TYPE_NULL);
            ACTVactivity.setAdapter(adapterActivity);
        });

        return view;
    }

    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar.setNavigationOnClickListener(v -> dismiss());


        button.setOnClickListener(this::onClick);
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

    private void onClick(View view){
        int userWeight;
        int userHeight;
        int waterGoal;
        String userSex;
        int userAge;
        Double userActivityScale;
        Double userCalories;
        double userFats;
        double userCarbs;
        double userProtein;

        //user's UID number to
        String UID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        userWeight = Integer.parseInt(Objects.requireNonNull(tietWeight.getText()).toString());
        userHeight = Integer.parseInt(Objects.requireNonNull(tietHeight.getText()).toString());
        userAge = Integer.parseInt(Objects.requireNonNull(tietAge.getText()).toString());
        userSex = ACTVsex.getText().toString();
        if(Objects.requireNonNull(tietWaterGoal.getText()).toString().equals("")){
            //set default water goal
            waterGoal = 1893;
        }
        else{
            waterGoal = Integer.parseInt(tietWaterGoal.getText().toString());
        }
        userActivityScale = activityScale(ACTVactivity.getText().toString());
        userCalories = calculateCalories(userHeight, userWeight, userAge, userSex, userActivityScale);
        //user recommended macronutrients in grams
        userFats = (userCalories * 0.3) / 9;
        userCarbs = (userCalories * 0.45) / 4;
        userProtein = (userCalories * 0.25) / 4;
        foodCollection.put("userWeight", userWeight);
        foodCollection.put("userHeight", userHeight);
        foodCollection.put("userActivityLevel", ACTVactivity.getText().toString());
        foodCollection.put("userAge", userAge);
        foodCollection.put("userSex", userSex);

        foodCollection.put("userCalories", userCalories);
        foodCollection.put("userFats", userFats);
        foodCollection.put("userCarbs", userCarbs);
        foodCollection.put("userProtein", userProtein);
        foodCollection.put("waterGoal", waterGoal);
        waterCollection.put("waterGoal", waterGoal);

        db.collection("/healthData").document(UID).
                collection("waterData").
                document("waterGoal").set(waterCollection, SetOptions.merge()).
                addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully written!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));

        db.collection("/healthData").document(UID).
                collection("foodData").
                document("dailyValues").set(foodCollection, SetOptions.merge()).
                addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully written!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));

        dismiss();
    }

    public Double activityScale(String activityLevel){
        switch (activityLevel) {
            case "Sedentary":
                return 1.2;
            case "Light Exercise 1-2 Days/Week":
                return 1.4;
            case "Moderate Exercise 2-3 Days/Week":
                return 1.6;
            case "Hard Exercise 4-5 Days/Week":
                return 1.75;
            case "Very Hard Exercise 6-7 Days/Week":
                return 2.0;
            case "Professional Athlete":
                return 2.3;
            default:
                return 0.0;
        }
    }

    public Double calculateCalories(int userHeight, int userWeight, int userAge,
                                    String userSex, Double userActivityScale ){

        double heightToCM = userHeight * 2.54;
        double weightToKG = userWeight / 2.205;

        //Mifflin - St Jeor equation
        double heightWeightCalculation = 10 * weightToKG / 1 + 6.25 * heightToCM / 1;
        if(userSex.equals("Female")){
            return (heightWeightCalculation - 5 * userAge - 161) * userActivityScale;
        }
        else if(userSex.equals("Male")){
            return ((heightWeightCalculation) - 5 * userAge + 5) * userActivityScale;
        }
        else{
            return 0.0;
        }
    }

    private void readSettingsData(FirestoreCallbackSettings firestoreCallbackSettings){
        CollectionReference collectionReference = db.collection("/healthData")
                .document(UID)
                .collection("foodData");

        collectionReference
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                int userWeight = 0;
                int userHeight = 0;
                int waterGoal = 0;
                String userSex = "empty";
                int userAge = 0;
                String userActivity = "empty";
                for (QueryDocumentSnapshot document :
                        Objects.requireNonNull(task.getResult())) {
                    if (document.getId().equals("dailyValues")) {
                        Number tempVar;
                        userActivity = (String) document.get("userActivityLevel");
                        userSex = (String) document.get("userSex");

                        tempVar = (Number) document.get("userAge");
                        assert tempVar != null;
                        userAge = tempVar.intValue();
                        tempVar = (Number) document.get("userWeight");
                        assert tempVar != null;
                        userWeight = tempVar.intValue();
                        tempVar = (Number) document.get("userHeight");
                        assert tempVar != null;
                        userHeight = tempVar.intValue();
                        tempVar = (Number) document.get("waterGoal");
                        assert tempVar != null;
                        waterGoal = tempVar.intValue();
                    } else {
                        Log.d(TAG, "readDateData: No document of this date was found");
                    }
                }
                firestoreCallbackSettings.onCallback(userWeight, userHeight, waterGoal,
                        userSex, userAge, userActivity);
            } else {
                Log.d(TAG, "Error getting document", task.getException());
            }
        });
    }

    private interface FirestoreCallbackSettings{
        void onCallback(int userWeight, int userHeight, int waterGoal, String userSex, int userAge,
        String userActivity);
    }
}