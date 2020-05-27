package com.example.senior_proj.ui.water;

import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.senior_proj.AAChartCore.AAChartCoreLib.AAChartCreator.AAChartModel;
import com.example.senior_proj.AAChartCore.AAChartCoreLib.AAChartCreator.AAChartView;
import com.example.senior_proj.AAChartCore.AAChartCoreLib.AAChartCreator.AASeriesElement;
import com.example.senior_proj.AAChartCore.AAChartCoreLib.AAChartEnum.AAChartType;
import com.example.senior_proj.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class WaterFragment extends Fragment {
    private static final String TAG = "waterFragmentTag" ;
    private CircularProgressBar circularProgressBar;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Map<String, Object> healthData = new HashMap<>();
    private Map<String, Object> nestedWaterData = new HashMap<>();
    private float userGoal;
    private float userDrank =0;
    private int cstmAmt = 0;
    private WaterViewModel waterViewModel;
    private AAChartModel aaChartModel;
    private AAChartView aaChartView;
    private AAChartView aaChartView2;
    private String UID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    private Date currentTime = Calendar.getInstance().getTime();
    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    private String formattedDate = df.format(currentTime);

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        waterViewModel =
                new ViewModelProvider(requireActivity()).get(WaterViewModel.class);
        View root = inflater.inflate(R.layout.fragment_water, container, false);

        //add today's
//        DocumentReference docIdRef = db.collection("/healthData")
////                .document(UID).collection("waterData").document(formattedDate);
////        docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
////            @Override
////            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
////                if (task.isSuccessful()) {
////                    DocumentSnapshot document = task.getResult();
////                    //assert document != null;
////                    if (document.exists()) {
////                        Log.d(TAG, "Document exists!");
////                    } else {
////                        Log.d(TAG, "Document does not exist!");
////                        Map<String, Object> newDateData = new HashMap<>();
////                        newDateData.put("userDrank", 0);
////                        db.collection("/healthData")
////                                .document(UID).collection("waterData")
////                                .document(formattedDate).set(newDateData);
////                    }
////                } else {
////                    Log.d(TAG, "Failed with: ", task.getException());
////                }
////            }
////        });

        circularProgressBar = root.findViewById(R.id.circularProgressBar);
        final TextView textPercentage = root.findViewById(R.id.textPercentage);
        final TextView textToGo = root.findViewById(R.id.textRemaining);
        //final AAChartView aaChartView = root.findViewById(R.id.AAChartView);
        TabLayout tabLayout = root.findViewById(R.id.tabs);
        Button cupAmt = root.findViewById(R.id.cupBtn);
        Button cstmBtn = root.findViewById(R.id.customBtn);

        waterViewModel.getUserDrank().observe(
                getViewLifecycleOwner(), userWaterDrank -> {
                    Float goal = waterViewModel.getUserWaterGoal().getValue();
                    if (goal != null) {
                        circularProgressBar.setProgressMax(goal);
                        circularProgressBar.setProgress(userWaterDrank);
                        float percentFilled = userWaterDrank / goal;
                        textPercentage.setText(String.format(Locale.getDefault(), "%.0f%%", percentFilled * 100f));
                        textToGo.setText(String.format(Locale.getDefault(), "%.1f mL to goal", goal - userWaterDrank));


                    }
                });

//        MediatorLiveData mediatorLiveData = new MediatorLiveData<>();
//        mediatorLiveData.addSource(waterViewModel.getUserWaterGoal(), new Observer() {
//                    @Override
//                    public void onChanged(Object o) {
//
//                    }
//                }


                //@todo use livemediator instead of standard 1 to view both userwatergoal and userdrank


                tabListener(tabLayout);

        //draw the progress bar with data
        aaChartView = root.findViewById(R.id.AAChartView);
        //aaChartView.callBack = (AAChartView.AAChartViewCallBack) this;
        AAChartModel aaChartModel = new AAChartModel()
                .chartType(AAChartType.Line)
                .title("Weekly Water Percentage")
                .backgroundColor("#FFFFFF")
                .categories(new String[]{"Sun", "Mon", "Tue", "Wed", "Thu",
                        "Fri", "Sat"})
                .dataLabelsEnabled(true)
                .yAxisVisible(false)
                .series(new AASeriesElement[]{
                        new AASeriesElement()
                                .name("Amount Drank")
                                .data(new Object[]{700, 600, 900, 1400, 720, 540, 350})
                });
        aaChartView.aa_drawChartWithChartModel(aaChartModel);

        cupBtnListener(cupAmt, textPercentage, textToGo);

        customBtnListener(cstmBtn);
        return root;
    }

    private void tabListener(TabLayout tabLayout){
        //can separate to tab listeners
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        aaChartView.setVisibility(View.VISIBLE);
                        //aaChartView2.setVisibility(View.INVISIBLE);
                        break;
                    case 1:
                        aaChartView.setVisibility(View.INVISIBLE);
                        //aaChartView2.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void cupBtnListener(Button cupAmt, TextView textPercentage, TextView textToGo){
        cupAmt.setOnClickListener(v -> {
            Map<String, Object> waterData = new HashMap<>();
            Float drank = waterViewModel.getUserDrank().getValue();
            if(drank != null){
                userDrank = drank;
                userDrank += 237;
            }
            waterViewModel.getUserDrank().setValue(userDrank);
            waterData.put("userDrank", userDrank);
            circularProgressBar.setProgressWithAnimation(userDrank, (long) 1000);
            db.collection("/healthData").document(UID)
                    .collection("waterData").document(formattedDate)
                    .set(waterData, SetOptions.merge());
            //float pfilled = userDrank / userGoal;
            //textPercentage.setText(String.format(Locale.getDefault(), "%.0f%%", pfilled * 100f));
            //textToGo.setText(String.format(Locale.getDefault(), "%.1f mL to goal", userGoal - userDrank));

        });
    }

    private void customBtnListener(Button cstmBtn) {
        cstmBtn.setOnClickListener(v -> {
            final EditText userAmount = new EditText(v.getContext());
            userAmount.setInputType(InputType.TYPE_CLASS_NUMBER);
            new MaterialAlertDialogBuilder(v.getContext())
                    .setTitle("Enter custom amount of water")
                    .setView(userAmount)
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                        cstmAmt = Integer.parseInt(userAmount.getText().toString());
                        userDrank += cstmAmt;
                        waterViewModel.getUserDrank().setValue(userDrank);

                    })
                    .show();
        });
    }
}