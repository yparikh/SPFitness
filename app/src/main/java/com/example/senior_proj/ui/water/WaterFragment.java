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
import androidx.fragment.app.Fragment;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.time.LocalDate;
import java.time.MonthDay;
import java.time.Period;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class WaterFragment extends Fragment {
    private static final String TAG = "waterFragmentTag" ;
    private CircularProgressBar circularProgressBar;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private float userGoal;
    private float userDrank =0;
    private int cstmAmt = 0;
    private WaterViewModel waterViewModel;
    private AAChartModel aaChartModel = new AAChartModel();
    private AAChartModel aaChartMonthModel = new AAChartModel();
    private AAChartView aaChartView;
    private AAChartView aaChartMonthView;
    private String UID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    private LocalDate localDate = LocalDate.now();
    private YearMonth localYearMonth= YearMonth.from(localDate);
    private int dayInCurrentMonth = localDate.getDayOfMonth();


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        waterViewModel =
                new ViewModelProvider(requireActivity()).get(WaterViewModel.class);
        View root = inflater.inflate(R.layout.fragment_water, container, false);

        circularProgressBar = root.findViewById(R.id.circularProgressBar);
        aaChartView = root.findViewById(R.id.AAChartView);
        aaChartMonthView = root.findViewById(R.id.AAChartMonthView);
        final TextView textPercentage = root.findViewById(R.id.textPercentage);
        final TextView textToGo = root.findViewById(R.id.textRemaining);
        TabLayout tabLayout = root.findViewById(R.id.tabs);
        Button cupAmt = root.findViewById(R.id.cupBtn);
        Button cstmBtn = root.findViewById(R.id.customBtn);

        aaChartMonthView.setVisibility(View.INVISIBLE);

        if(waterViewModel.getUserDrank().getValue() == null){
            readData((value, valueDrank, list, list7, stringList, stringList7) -> {
                circularProgressBar.setProgressMax(value);
                circularProgressBar.setProgressWithAnimation(valueDrank, (long) 1000);
                String[] weekDates = stringList7.toArray(new String[0]);
                String[] monthDates = stringList.toArray(new String[0]);
                float percentFilled = valueDrank / value;
                textPercentage.setText(String.format(Locale.getDefault(),
                        "%.0f%%", percentFilled * 100f));
                textToGo.setText(String.format(Locale.getDefault(),
                        "%.1f mL to goal", value - valueDrank));
                aaChartModel.chartType(AAChartType.Line)
                        .title("Weekly Water Percentage")
                        .backgroundColor("#FFFFFF")
                        .categories(weekDates)
                        .dataLabelsEnabled(true)
                        .colorsTheme(new String[]{"#3917e7"})
                        .yAxisVisible(false)
                        .series(new AASeriesElement[]{
                                new AASeriesElement()
                                        .name("Amount Drank")
                                        .data(list7.toArray())
                        });
                aaChartView.aa_drawChartWithChartModel(aaChartModel);
                aaChartMonthModel.chartType(AAChartType.Line)
                        .title("Monthly Water Percentage")
                        .backgroundColor("#FFFFFF")
                        .categories(monthDates)
                        .dataLabelsEnabled(true)
                        .colorsTheme(new String[]{"#3917e7"})
                        .yAxisVisible(false)
                        .series(new AASeriesElement[]{
                                new AASeriesElement()
                                        .name("Amount Drank")
                                        .data(list.toArray())
                        });
                aaChartMonthView.aa_drawChartWithChartModel(aaChartMonthModel);
                Log.d(TAG, "week start "+ Arrays.toString(weekDates));
                Log.d(TAG, "week names start " + Arrays.toString(list7.toArray()));
                Log.d(TAG, "month start " + Arrays.toString(monthDates));
                Log.d(TAG, "month start " + Arrays.toString(list.toArray()));
            });
        }

        waterViewModel.getUserDrank().observe(
                getViewLifecycleOwner(), userWaterDrank -> {
                    Float goal = waterViewModel.getUserWaterGoal().getValue();

                    if (goal != null) {
                        List<Object> tempObj = waterViewModel.getUserWaterWeek().getValue();
                        List<String> tempStringNames = waterViewModel.getUserWaterWeekLabel().getValue();
                        List<Object> monthObj = waterViewModel.getUserWaterMonth().getValue();
                        List<String> monthStringNames = waterViewModel.getUserWaterMonthLabel().getValue();
                        assert tempStringNames != null;
                        assert monthStringNames != null;
                        String[] weekDates = tempStringNames.toArray(new String[0]);
                        String[] monthDates = monthStringNames.toArray(new String[0]);
                        circularProgressBar.setProgressMax(goal);
                        circularProgressBar.setProgressWithAnimation(userWaterDrank, (long) 1000);
                        float percentFilled = userWaterDrank / goal;
                        textPercentage.setText(String.format(Locale.getDefault(),
                                "%.0f%%", percentFilled * 100f));
                        textToGo.setText(String.format(Locale.getDefault(),
                                "%.1f mL to goal", goal - userWaterDrank));

                        assert tempObj != null;
                        aaChartModel.chartType(AAChartType.Line)
                                .title("Weekly Water Percentage")
                                .backgroundColor("#FFFFFF")
                                .categories(weekDates)
                                .dataLabelsEnabled(true)
                                .colorsTheme(new String[]{"#3917e7"})
                                .yAxisVisible(false)
                                .series(new AASeriesElement[]{
                                        new AASeriesElement()
                                                .name("Amount Drank")
                                                .data(tempObj.toArray())
                                });
                        aaChartView.aa_drawChartWithChartModel(aaChartModel);
                        assert monthObj != null;
                        aaChartMonthModel.chartType(AAChartType.Line)
                                .title("Monthly Water Percentage")
                                .backgroundColor("#FFFFFF")
                                .categories(monthDates)
                                .dataLabelsEnabled(true)
                                .colorsTheme(new String[]{"#3917e7"})
                                .yAxisVisible(false)
                                .series(new AASeriesElement[]{
                                        new AASeriesElement()
                                                .name("Amount Drank")
                                                .data(monthObj.toArray())
                                });

                        aaChartMonthView.aa_drawChartWithChartModel(aaChartMonthModel);
                        Log.d(TAG, "month" + Arrays.toString(monthDates));
                        Log.d(TAG, "month" + Arrays.toString(monthStringNames.toArray()));
                        Log.d(TAG, "week"+ Arrays.toString(weekDates));
                        Log.d(TAG, "week names" + Arrays.toString(tempObj.toArray()));
                    }

                });


        tabListener(tabLayout);

        cupBtnListener(cupAmt);

        customBtnListener(cstmBtn);

        return root;
    }

    private void readData(FirestoreCallback firestoreCallback){

       CollectionReference collectionReference = db.collection("/healthData")
               .document(UID)
                .collection("waterData");


        collectionReference
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        float uDrank = 0.0f;
                        float uGoal = 0.0f;
                        List<String> listString = new ArrayList<>();
                        List<Object> listobj = new ArrayList<>();
                        List<String> listString7 = new ArrayList<>();
                        List<Object> listobj7 = new ArrayList<>();
                        for (QueryDocumentSnapshot document :
                                Objects.requireNonNull(task.getResult())) {
                            if (document.getId().equals(String.valueOf(localYearMonth))) {
                                //GOAL get the MONTH DOCUMENT data
                                Map<String, Object> tempMap;
                                Map<String, Object> mismatchMap = new HashMap<>();
                                tempMap = document.getData();
                                if (dayInCurrentMonth != tempMap.size()) {
                                    //for each day passed
                                    int setflag = 0;
                                    if (tempMap.size() == 0) {
                                        for (int addDay = 0; addDay < dayInCurrentMonth; addDay++) {
                                            LocalDate tempDate = localDate.minus(Period.ofDays(addDay));
                                            mismatchMap.put(String.valueOf(tempDate), 0);
                                        }
                                    } else {
                                        for (int daysPassed = 0; daysPassed < dayInCurrentMonth; daysPassed++) {
                                            for (Map.Entry<String, Object> key : tempMap.entrySet()) {
                                                //if today's date is equivalent to the field
                                                LocalDate tempDate = localDate.minus(Period.ofDays(daysPassed));
                                                if (String.valueOf(tempDate).equals(key.getKey()) &&
                                                        String.valueOf(tempDate).equals(String.valueOf(localDate))) {
                                                    //get the current userdrank number
                                                    Number tempNum = (Number) key.getValue();
                                                    uDrank = tempNum.floatValue();
                                                    //add it to the temporary map
                                                    mismatchMap.put(key.getKey(), key.getValue());
                                                    break;
                                                }
                                                //else if the date is equal to the field
                                                else if (String.valueOf(tempDate).equals(key.getKey())) {
                                                    mismatchMap.put(key.getKey(), key.getValue());
                                                    break;
                                                }
                                                //else this date is not in the database and should be initalized
                                                else if (setflag == tempMap.size()) {
                                                    if (String.valueOf(tempDate).equals(String.valueOf(localDate))) {
                                                        uDrank = 0.0f;
                                                        //add it to the temporary map
                                                        mismatchMap.put(String.valueOf(localDate), 0);
                                                        setflag = 0;
                                                        break;
                                                    } else {
                                                        mismatchMap.put(String.valueOf(tempDate), 0);
                                                        setflag = 0;
                                                        break;
                                                    }
                                                }
                                                setflag++;
                                            }
                                        }
                                    }
                                    db.collection("/healthData")
                                            .document(UID).collection("waterData")
                                            .document(String.valueOf(localYearMonth))
                                            .set(mismatchMap, SetOptions.merge());

                                    List<String> keyList = new ArrayList<>(mismatchMap.keySet());
                                    Collections.sort(keyList);
                                    for (final String key : keyList) {

                                        LocalDate tempDate = LocalDate.parse(key,
                                                DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                                        MonthDay monthDay = MonthDay.from(tempDate);
                                        String formattedDate = monthDay.format(DateTimeFormatter.ofPattern("MM-dd"));
                                        LocalDate startOfWeek = localDate.minus(Period.ofDays(7));
                                        if (tempDate.getDayOfMonth() > startOfWeek.getDayOfMonth() || tempDate.getDayOfMonth() <8) {
                                            listString7.add(String.valueOf(formattedDate));
                                            listobj7.add(mismatchMap.get(key));
                                        }
                                        listString.add(String.valueOf(formattedDate));
                                        Number tempNum = (Number) mismatchMap.get(key);
                                        listobj.add(tempNum);
                                    }

                                } else {
                                    List<String> keyList = new ArrayList<>(tempMap.keySet());
                                    Collections.sort(keyList);
                                    for (final String key : keyList) {
                                        if (key.equals(String.valueOf(localDate))) {
                                            Number tempNum = (Number) tempMap.get(key);
                                            assert tempNum != null;
                                            uDrank = tempNum.floatValue();
                                        }

                                        LocalDate tempDate = LocalDate.parse(key,
                                                DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                                        MonthDay monthDay = MonthDay.from(tempDate);
                                        String formattedDate = monthDay.format(DateTimeFormatter.ofPattern("MM-dd"));
                                        LocalDate startOfWeek = localDate.minus(Period.ofDays(7));
                                        if (tempDate.getDayOfMonth() > startOfWeek.getDayOfMonth() || tempDate.getDayOfMonth() < 8) {
                                            listString7.add(String.valueOf(formattedDate));
                                            listobj7.add(tempMap.get(key));
                                        }
                                        listString.add(String.valueOf(formattedDate));
                                        Number tempNum = (Number) tempMap.get(key);
                                        listobj.add(tempNum);
                                    }
                                }
                            }

                            if (document.getId().equals("waterGoal")) {
                                Number tempGoal = (Number) document.get("waterGoal");
                                assert tempGoal != null;
                                uGoal = tempGoal.floatValue();
                            }
                        }
                        firestoreCallback.onCallback(uGoal, uDrank, listobj, listobj7, listString, listString7);
                    } else {
                        Log.d(TAG, "Error getting document", task.getException());
                    }
                });

    }

    private interface FirestoreCallback{
        void onCallback(Float goalValue, Float drankValue, List<Object> monthValues,
                        List<Object> weekValues, List<String> monthNames, List<String> weekNames);
    }

    private void tabListener(TabLayout tabLayout){
        //can separate to tab listeners
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        aaChartView.setVisibility(View.VISIBLE);
                        aaChartMonthView.setVisibility(View.INVISIBLE);
                        aaChartView.aa_refreshChartWithChartModel(aaChartModel);
                        break;
                    case 1:
                        aaChartView.setVisibility(View.INVISIBLE);
                        aaChartMonthView.setVisibility(View.VISIBLE);
                        aaChartMonthView.aa_refreshChartWithChartModel(aaChartMonthModel);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        aaChartView.setVisibility(View.INVISIBLE);
                        aaChartMonthView.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        aaChartView.setVisibility(View.VISIBLE);
                        aaChartMonthView.setVisibility(View.INVISIBLE);
                        break;
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        aaChartView.setVisibility(View.VISIBLE);
                        aaChartMonthView.setVisibility(View.INVISIBLE);
                        break;
                    case 1:
                        aaChartView.setVisibility(View.INVISIBLE);
                        aaChartMonthView.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });
    }

    private void cupBtnListener(Button cupAmt){
        cupAmt.setOnClickListener(v -> {
            Map<String, Float> waterDate = new HashMap<>();
            Float drank = waterViewModel.getUserDrank().getValue();
            if(drank != null){
                userDrank = drank;
                userDrank += 237;
            }
            waterViewModel.getUserDrank().setValue(userDrank);
            waterDate.put(String.valueOf(localDate), userDrank);
            db.collection("/healthData").document(UID)
                    .collection("waterData")
                    .document(String.valueOf(localYearMonth))
                    .set(waterDate, SetOptions.merge());
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
                        if(!userAmount.getText().toString().equals("")) {
                            cstmAmt = Integer.parseInt(userAmount.getText().toString());
                            Map<String, Float> waterData = new HashMap<>();
                            Float drank = waterViewModel.getUserDrank().getValue();
                            if (drank != null) {
                                userDrank = drank;
                                userDrank += cstmAmt;
                            }
                            waterViewModel.getUserDrank().setValue(userDrank);
                            waterData.put(String.valueOf(localDate), userDrank);
                            db.collection("/healthData").document(UID)
                                    .collection("waterData")
                                    .document(String.valueOf(localYearMonth))
                                    .set(waterData, SetOptions.merge());
                        }
                    })
                    .show();
        });
    }
}