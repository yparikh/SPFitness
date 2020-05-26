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

    private static final String TAG = "example_dialog";

    private Toolbar toolbar;
    private Button button;
    private TextInputLayout tilHeight;
    private TextInputLayout tilWeight;
    private TextInputEditText tietHeight;
    private TextInputEditText tietWeight;
    private TextInputEditText tietWaterGoal;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private Map<String, Object> usrData = new HashMap<>();
    private Map<String, Object> waterCollection = new HashMap<>();

    public static void display(FragmentManager fragmentManager) {
        MenuDialog menuDialog = new MenuDialog();
        menuDialog.show(fragmentManager, TAG);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        toolbar = view.findViewById(R.id.toolbar);
        button = view.findViewById(R.id.saveButton);
        tilHeight = view.findViewById(R.id.otfHeight);
        tilWeight = view.findViewById(R.id.otfWeight);
        tietWeight = view.findViewById(R.id.tietWeight);
        tietHeight = view.findViewById(R.id.tietHeight);
        tietWaterGoal = view.findViewById(R.id.tietWater);
        return view;
    }

    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar.setNavigationOnClickListener(v -> dismiss());
        //toolbar.inflateMenu(R.menu.);
        //toolbar.setOnMenuItemClickListener(item -> {
        //    dismiss();
        //    return true;
        //});
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

       //check if weight and height are not empty
        if(Objects.requireNonNull(tietWeight.getText()).toString().equals("")||
                Objects.requireNonNull(tietHeight.getText()).toString().equals("")){
            if(Objects.requireNonNull(tietWeight.getText()).toString().equals("")){
                tilWeight.setError("*Required");
            }
            else{
                tilHeight.setError("*Required");
            }
        }
        else{
            //user's UID number to
            String UID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
            userWeight = Integer.parseInt(tietWeight.getText().toString());
            userHeight = Integer.parseInt(tietHeight.getText().toString());
            if(Objects.requireNonNull(tietWaterGoal.getText()).toString().equals("")){
                //set default water goal
                waterGoal = 1893;
            }
            else{
                waterGoal = Integer.parseInt(tietWaterGoal.getText().toString());
            }
            usrData.put("userWeight", userWeight);
            usrData.put("userHeight", userHeight);
            waterCollection.put("waterGoal", waterGoal);

            db.collection("/healthData").document(UID).
                    collection("waterData").
                    document("waterGoal").set(waterCollection, SetOptions.merge()).
                    addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "DocumentSnapshot successfully written!");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error writing document", e);
                        }
            });

            db.collection("/healthData").document(UID).set(usrData, SetOptions.merge()).
                    addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "DocumentSnapshot successfully written!");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error writing document", e);
                        }
            });
            dismiss();
        }
    }
}