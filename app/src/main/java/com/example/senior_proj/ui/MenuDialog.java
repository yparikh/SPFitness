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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class MenuDialog extends DialogFragment {

    private static final String TAG = "menu_dialog";

    private Toolbar toolbar;
    private Button button;
    private TextInputLayout tilHeight;
    private TextInputLayout tilWeight;
    private TextInputLayout tilSex;
    private TextInputLayout tilActivityLevel;
    private TextInputLayout tilAge;
    private TextInputEditText tietAge;
    private TextInputEditText tietHeight;
    private TextInputEditText tietWeight;
    private TextInputEditText tietWaterGoal;
    AutoCompleteTextView ACTVsex;
    AutoCompleteTextView ACTVactivity;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private Map<String, Object> usrData = new HashMap<>();
    private Map<String, Object> waterCollection = new HashMap<>();
    private Map<String, Object> foodCollection = new HashMap<>();

    public static void display(FragmentManager fragmentManager) {
        MenuDialog menuDialog = new MenuDialog();
        menuDialog.setCancelable(false);
        menuDialog.show(fragmentManager, TAG);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        toolbar = view.findViewById(R.id.toolbar);
        button = view.findViewById(R.id.saveButton);
        tilHeight = view.findViewById(R.id.TILHeight);
        tilWeight = view.findViewById(R.id.TILWeight);
        tilActivityLevel = view.findViewById(R.id.TILActivityLevel);
        tilSex = view.findViewById(R.id.TILSex);
        tilAge = view.findViewById(R.id.TILAge);
        tietAge = view.findViewById(R.id.TIETAge);
        tietWeight = view.findViewById(R.id.TIETWeight);
        tietHeight = view.findViewById(R.id.TIETHeight);
        tietWaterGoal = view.findViewById(R.id.TIETWater);
        ACTVsex = view.findViewById(R.id.ACTVsex);
        ACTVactivity= view.findViewById(R.id.ACTVActivityLevel);

        String[] sexArray = new String[] {"Female", "Male"};
        String[] activityArray = new String[] {"Sedentary", "Light Exercise 1-2 Days/Week",
                "Moderate Exercise 2-3 Days/Week", "Hard Exercise 4-5 Days/Week",
                "Very Hard Exercise 6-7 Days/Week", "Professional Athlete"};

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

        ACTVsex = view.findViewById(R.id.ACTVsex);
        ACTVsex.setText("Female", false);
        ACTVsex.setInputType(InputType.TYPE_NULL);
        ACTVsex.setAdapter(adapter);

        ACTVactivity = view.findViewById(R.id.ACTVActivityLevel);
        ACTVactivity.setText("Sedentary", false);
        ACTVactivity.setInputType(InputType.TYPE_NULL);
        ACTVactivity.setAdapter(adapterActivity);


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
        int userWeight = 0;
        int userHeight = 0;
        int waterGoal = 0;
        String userSex = "Female";
        int userAge = 10;
        Double userActivityScale = 0.0;
        Double userCalories = 0.0;
        Double userFats = 0.0;
        Double userCarbs = 0.0;
        Double userProtein = 0.0;
       //check if weight, height, sex, and activity level are not empty
        if(Objects.requireNonNull(tietWeight.getText()).toString().equals("")||
                Objects.requireNonNull(tietHeight.getText()).toString().equals("")||
                ACTVsex.getText().toString().equals("")||
                ACTVactivity.getText().toString().equals("")){
            if(Objects.requireNonNull(tietWeight.getText()).toString().equals("")){
                tilWeight.setError("*Required");
            }
            if(Objects.requireNonNull(tietHeight.getText()).toString().equals("")){
                tilHeight.setError("*Required");
            }
            if(Objects.requireNonNull(ACTVsex.getText()).toString().equals("")){
                tilSex.setError("*Required");
            }
            if(Objects.requireNonNull(ACTVactivity.getText()).toString().equals("")){
                tilActivityLevel.setError("*Required");
            }
            if(Objects.requireNonNull(tietAge.getText()).toString().equals("")){
                tilAge.setError("Required");
            }
        }
        else{
            //user's UID number to
            String UID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
            userWeight = Integer.parseInt(tietWeight.getText().toString());
            userHeight = Integer.parseInt(tietHeight.getText().toString());
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

        }
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
}