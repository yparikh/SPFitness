package com.example.senior_proj.ui.nutrition;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.senior_proj.fdc.AbridgedFoodItem;
import com.example.senior_proj.fdc.FDCJson;
import com.example.senior_proj.fdc.FoodNutrient;
import com.example.senior_proj.fdc.FoodSearchCriteria;
import com.example.senior_proj.fdc.JsonPlaceHolderApi;
import com.example.senior_proj.R;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputEditText;

import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.akexorcist.roundcornerprogressbar.TextRoundCornerProgressBar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import static android.app.Activity.RESULT_OK;

public class NutritionFragment extends Fragment {

    private static final String TAG = "nutritionFragmentTAG";
    private NutritionViewModel nutritionViewModel;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String UID = Objects.requireNonNull(FirebaseAuth.
            getInstance().getCurrentUser()).getUid();
    private LocalDate localDate = LocalDate.now();
    private LocalDate selectedDate = localDate;
    private YearMonth localYearMonth= YearMonth.from(localDate);

    Button BtnSearch;
    Button BtnDatePicker;
    TextInputEditText TIETfoodsearch;
    TextRoundCornerProgressBar PBcalories;
    TextRoundCornerProgressBar PBcarbs;
    TextRoundCornerProgressBar PBfats;
    TextRoundCornerProgressBar PBprotein;
    RecyclerView RVuserFoodList;
    RecyclerView.Adapter mAdapter;
    MaterialDatePicker.Builder<Long> dateBuilder = MaterialDatePicker.Builder.datePicker();

    String abridged = "abridged";
    String FDCid = null;
    private float currentCalories = 0;
    private float currentCarbs = 0;
    private float currentProtein = 0;
    private float currentFats = 0;
    private List<String> currentFoodItems;
    private List<String> userFoodList;

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://api.nal.usda.gov")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dateBuilder.setTheme(R.style.MaterialCalendarTheme);
        nutritionViewModel = new ViewModelProvider(requireActivity()).get(NutritionViewModel.class);
        View root = inflater.inflate(R.layout.fragment_nutrition, container, false);

        BtnSearch = root.findViewById(R.id.foodSearchBtn);
        BtnDatePicker = root.findViewById(R.id.BtnDatePicker);
        TIETfoodsearch = root.findViewById(R.id.TIETfoodSearch);
        PBcalories = root.findViewById(R.id.TRCPBcalories);
        PBcarbs = root.findViewById(R.id.TRCPBcarbs);
        PBfats = root.findViewById(R.id.TRCPBfats);
        PBprotein = root.findViewById(R.id.TRCPBprotein);
        BtnDatePicker.setText(String.valueOf(localDate));
        dateBuilder.setTitleText(String.valueOf(localDate));
        MaterialDatePicker<Long> datePicker = dateBuilder.build();
        RVuserFoodList = root.findViewById(R.id.RVuserFoodList);

        //startup
        if(nutritionViewModel.getUserCalories().getValue() == null){
            readStartData((storedFoodListItems, currentCalories, currentCarbs, currentFats,
                           currentProtein, maxCalories, maxCarbs, maxFats, maxProtein) -> {
                initializeProgressBars(maxCalories,maxCarbs, maxFats, maxProtein,
                        currentCalories, currentCarbs, currentFats, currentProtein);

                userFoodList = storedFoodListItems;
                mAdapter = new FoodStoredRecyclerViewAdapter(userFoodList);
                RVuserFoodList.setAdapter(mAdapter);
                RVuserFoodList.setLayoutManager(new LinearLayoutManager
                        (NutritionFragment.this.getActivity()));
            });

        }

        if(nutritionViewModel.getUserCalories().getValue() !=null) {
            nutritionViewModel.getUserCalories().observe(getViewLifecycleOwner(), userCalories -> {
                Float maxCalories = nutritionViewModel.getUserTotalCalories().getValue();
                Float maxCarbs = nutritionViewModel.getUserTotalCarbs().getValue();
                Float maxFats = nutritionViewModel.getUserTotalFats().getValue();
                Float maxProtein = nutritionViewModel.getUserTotalProtein().getValue();
                Float currentCalories = nutritionViewModel.getUserCalories().getValue();
                Float currentCarbs = nutritionViewModel.getUserCarbs().getValue();
                Float currentFats = nutritionViewModel.getUserFats().getValue();
                Float currentProtein = nutritionViewModel.getUserProtein().getValue();
                List<String> savedFoodList = nutritionViewModel.getUserFoodItems().getValue();

                initializeProgressBars(maxCalories, maxCarbs, maxFats, maxProtein, currentCalories,
                        currentCarbs, currentFats, currentProtein);

                userFoodList = savedFoodList;
                mAdapter = new FoodStoredRecyclerViewAdapter(userFoodList);
                RVuserFoodList.setAdapter(mAdapter);
                RVuserFoodList.setLayoutManager(new LinearLayoutManager
                        (NutritionFragment.this.getActivity()));

            });
        }
        //when the food item value is returned from the search
        //get the nutrients
        //store the values to the database
        //
        if(FDCid != null){
            fetchFoodNutrients(FDCid);
        }

        BtnDatePicker.setOnClickListener(v -> {
            datePicker.show(getParentFragmentManager(), datePicker.toString());

            datePicker.addOnPositiveButtonClickListener(selection -> {
                Instant instant = Instant.ofEpochMilli(selection);
                selectedDate = LocalDateTime.ofInstant(instant, ZoneOffset.UTC).toLocalDate();
                BtnDatePicker.setText(String.valueOf(selectedDate));

                NutritionFragment.this.readDateData(storedFoodListItems -> {
                    userFoodList = storedFoodListItems;
                    mAdapter = new FoodStoredRecyclerViewAdapter(userFoodList);
                    RVuserFoodList.setAdapter(mAdapter);
                    RVuserFoodList.setLayoutManager(new LinearLayoutManager(NutritionFragment.this.getActivity()));

                });
            });
        });

        //on search, fetch list of results
        BtnSearch.setOnClickListener(v -> {
            if(TIETfoodsearch.getText() != null){
                NutritionFragment.this.fetchFoodSearch();
            }
        });

        return root;
    }

    private void initializeProgressBars(Float maxCalories, Float maxCarbs, Float maxFats,
                                        Float maxProtein, Float currentCalories, Float currentCarbs,
                                        Float currentFats, Float currentProtein) {

        if(maxCalories != null && maxCarbs != null && maxFats  != null && maxProtein  != null
                && currentCarbs  != null && currentFats  != null && currentProtein != null){
            float roundedMaxCalories = (float) Math.round(maxCalories * 100)/ 100;
            float roundedMaxCarbs = (float)
                    Math.round(maxCarbs * 100)/ 100;
            float roundedMaxFats = (float)
                    Math.round(maxFats * 100)/ 100;
            float roundedMaxProtein = (float)
                    Math.round(maxProtein * 100)/ 100;
            float roundedCurrentCalories = (float)
                    Math.round(currentCalories * 100)/ 100;
            float roundedCurrentCarbs = (float)
                    Math.round(currentCarbs * 100)/ 100;
            float roundedCurrentFats = (float)
                    Math.round(currentFats * 100)/ 100;
            float roundedCurrentProtein = (float)
                    Math.round(currentProtein * 100)/ 100;

            PBcalories.setMax(roundedMaxCalories);
            PBcalories.setProgress(roundedCurrentCalories);
            PBcalories.setProgressText(roundedCurrentCalories + " / " + roundedMaxCalories);

            PBcarbs.setMax(roundedMaxCarbs);
            PBcarbs.setProgress(roundedCurrentCarbs);
            PBcarbs.setProgressText(roundedCurrentCarbs + " / " + roundedMaxCarbs);

            PBfats.setMax(roundedMaxFats);
            PBfats.setProgress(roundedCurrentFats);
            PBfats.setProgressText(roundedCurrentFats + " / " + roundedMaxFats);

            PBprotein.setMax(roundedMaxProtein);
            PBprotein.setProgress(roundedCurrentProtein);
            PBprotein.setProgressText(roundedCurrentProtein + " / " + roundedMaxProtein);
        }
    }


    private void fetchFoodSearch() {

        String query = Objects.requireNonNull(TIETfoodsearch.getText()).toString();
        FoodSearchCriteria fsc = new FoodSearchCriteria();
        fsc.setQuery(query);
        Call<FDCJson> listCall = jsonPlaceHolderApi.getFoodSearch(fsc);

        listCall.enqueue(new Callback<FDCJson>() {
            @Override
            public void onResponse(@NotNull Call<FDCJson> call,
                                   @NotNull Response<FDCJson> response) {
                if (!response.isSuccessful()) {
                    String responseMsg = "Code " + response.code() + " " + response.message();
                    Log.d(TAG, "onResponse: " + responseMsg);
                    return;
                }

                FDCJson posts = response.body();


                assert posts != null;
                FoodResultFragment nextFrag= new FoodResultFragment(posts.getFoods());
                nextFrag.setTargetFragment(NutritionFragment.this, 111);
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, nextFrag, "foodListFragment")
                        .addToBackStack(null)
                        .commit();

            }

            @Override
            public void onFailure(@NotNull Call<FDCJson> call, @NotNull Throwable t) {
                Log.d(TAG, "onResponse: " + t.getMessage());
            }
        });

    }

    private void fetchFoodNutrients(String FDCid){
        //initialize with nutrient codes: 208(Energy), 203(Protein), 204 (Total Lipid[Fat]), and
        // 205 (Carbohydrates)

       ArrayList<Integer> nutrientsList = new ArrayList<>(
               Arrays.asList(203, 204, 205, 208));

        Call<AbridgedFoodItem> listCall = jsonPlaceHolderApi.
                getFoodNutrients(Integer.parseInt(FDCid), abridged, nutrientsList);

        listCall.enqueue(new Callback<AbridgedFoodItem>() {
            @Override
            public void onResponse(@NotNull Call<AbridgedFoodItem> call,
                                   @NotNull Response<AbridgedFoodItem> response) {
                if (!response.isSuccessful()) {
                    String responseMsg = "Code " + response.code() + " " + response.message();
                    Log.d(TAG, "onResponse: " + responseMsg);
                    return;
                }

                AbridgedFoodItem foodData = response.body();

                assert foodData != null;
                currentFoodItems = nutritionViewModel.getUserFoodItems().getValue();
                if(currentFoodItems == null){
                    currentFoodItems = new ArrayList<>();
                }
                currentFoodItems.add(foodData.getDescription());

                Map<String, Object> foodUpdate = new HashMap<>();
                Map<String, Object> nutrientUpdate = new HashMap<>();

                for (FoodNutrient nutrient: foodData.getFoodNutrients()) {
                    switch (nutrient.getName()){
                        case("Total lipid (fat)"):
                            Float fats = nutritionViewModel.getUserFats().getValue();
                            if(fats != null){
                                currentFats = fats;
                            }
                            else{
                                currentFats = 0;
                            }
                            currentFats += nutrient.getAmount();
                            break;
                        case("Carbohydrate, by difference"):
                            Float carbs = nutritionViewModel.getUserCarbs().getValue();
                            if(carbs !=null){
                                currentCarbs = carbs;
                            }
                            else{
                                currentCarbs = 0;
                            }
                            currentCarbs += nutrient.getAmount();
                            break;
                        case("Protein"):
                            Float protein = nutritionViewModel.getUserProtein().getValue();
                            if(protein !=null){
                                currentProtein = protein;
                            }
                            else{
                                currentProtein = 0;
                            }
                            currentProtein += nutrient.getAmount();
                            break;
                        case("Energy"):
                            Float calories = nutritionViewModel.getUserCalories().getValue();
                            if(calories !=null){
                                currentCalories = calories;
                            }
                            else{
                                currentCalories = 0;
                            }
                            currentCalories += nutrient.getAmount();

                            Log.d(TAG, "onResponse: " + nutrient.getAmount());
                            break;
                    }
                    Log.d(TAG, "onResponse: " + nutrient.getName());
                }

                nutrientUpdate.put("foodItemlist", currentFoodItems);
                nutrientUpdate.put("dailyCalories", currentCalories);
                nutrientUpdate.put("dailyCarbs", currentCarbs);
                nutrientUpdate.put("dailyProtein", currentProtein);
                nutrientUpdate.put("dailyFats", currentFats);
                foodUpdate.put(String.valueOf(localDate), nutrientUpdate );
                db.collection("/healthData").document(UID)
                        .collection("foodData")
                        .document(String.valueOf(localYearMonth))
                        .set(foodUpdate, SetOptions.merge());

            }

            @Override
            public void onFailure(@NotNull Call<AbridgedFoodItem> call, @NotNull Throwable t) {
                Log.d(TAG, "onResponse: " + t.getMessage());
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == 111){
                assert data != null;
                FDCid = data.getStringExtra("FDCID");
                Log.d(TAG, "onActivityResult: " + FDCid);
            }
        }
    }

    private void readStartData(FirestoreCallbackStart firestoreCallbackStart){
        CollectionReference collectionReference = db.collection("/healthData")
                .document(UID)
                .collection("foodData");


        collectionReference
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> storedFoodListItems = new ArrayList<>();
                        float currentCalories = 0;
                        float currentCarbs = 0;
                        float currentProtein = 0;
                        float currentFats = 0;
                        float maxCalories = 0;
                        float maxCarbs = 0;
                        float maxFats = 0;
                        float maxProtein = 0;
                        for (QueryDocumentSnapshot document :
                                Objects.requireNonNull(task.getResult())) {
                            if (document.getId().equals(String.valueOf(localYearMonth))) {
                                if (document.get(localDate + ".foodItemlist") != null) {
                                    Log.d(TAG, "readDateData: the food list was retrieved");
                                    storedFoodListItems = (List<String>) document.get(localDate + ".foodItemlist");
                                    Number tempCalories = (Number) document.get(localDate + ".dailyCalories" );
                                    assert tempCalories != null;
                                    currentCalories = tempCalories.floatValue();
                                    Number tempCarbs = (Number) document.get(localDate + ".dailyCarbs" );
                                    assert tempCarbs != null;
                                    currentCarbs = tempCarbs.floatValue();
                                    Number tempFats = (Number) document.get(localDate + ".dailyFats" );
                                    assert tempFats != null;
                                    currentFats = tempFats.floatValue();
                                    Number tempProtein = (Number) document.get(localDate + ".dailyProtein" );
                                    assert tempProtein != null;
                                    currentProtein = tempProtein.floatValue();

                                }

                            }
                            else if(document.getId().equals("dailyValues")){
                                Number tempCalories = (Number) document.get("userCalories");
                                assert tempCalories != null;
                                maxCalories = tempCalories.floatValue();
                                Number tempCarbs = (Number) document.get("userCarbs");
                                assert tempCarbs != null;
                                maxCarbs = tempCarbs.floatValue();
                                Number tempFats = (Number) document.get("userFats");
                                assert tempFats != null;
                                maxFats = tempFats.floatValue();
                                Number tempProtein = (Number) document.get("userProtein");
                                assert tempProtein != null;
                                maxProtein = tempProtein.floatValue();

                            }
                            else{
                                Log.d(TAG, "readDateData: No document of this date was found");
                                //assert storedFoodListItems != null;
                                //storedFoodListItems.add("No food items were found for this date.");
                            }
                        }
                        firestoreCallbackStart.onCallback(storedFoodListItems, currentCalories,
                                currentCarbs, currentFats, currentProtein, maxCalories, maxCarbs,
                                maxFats, maxProtein);
                    } else {
                        Log.d(TAG, "Error getting document", task.getException());
                    }
                });
    }

    private interface FirestoreCallbackStart{
        void onCallback(List<String> storedFoodListItems, float currentCalories, float currentCarbs,
                        float currentFats, float currentProtein, float maxCalories, float maxCarbs,
                        float maxFats, float maxProtein);
    }

    private void readDateData(FirestoreCallbackDate firestoreCallbackDate){
        CollectionReference collectionReference = db.collection("/healthData")
                .document(UID)
                .collection("foodData");


        collectionReference
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<String> storedFoodListItems = new ArrayList<>();
                for (QueryDocumentSnapshot document :
                        Objects.requireNonNull(task.getResult())) {
                    if (document.getId().equals(String.valueOf(localYearMonth))) {
                        storedFoodListItems = (List<String>) document.get(selectedDate + ".foodItemlist");

                    } else {
                        Log.d(TAG, "readDateData: No document of this date was found");
                        //assert storedFoodListItems != null;
                        //storedFoodListItems.add("No food items were found for this date.");
                    }
                }
                firestoreCallbackDate.onCallback(storedFoodListItems);
            } else {
                Log.d(TAG, "Error getting document", task.getException());
            }
        });
    }

    private interface FirestoreCallbackDate{
        void onCallback(List<String> storedFoodListItems);
    }


}
