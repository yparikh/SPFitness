package com.example.senior_proj.ui.health;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.senior_proj.fdc.AbridgedFoodItem;
import com.example.senior_proj.fdc.FDCJson;
import com.example.senior_proj.fdc.FoodNutrient;
import com.example.senior_proj.fdc.FoodSearchCriteria;
import com.example.senior_proj.fdc.JsonPlaceHolderApi;
import com.example.senior_proj.R;
import com.google.android.material.textfield.TextInputEditText;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.app.Activity.RESULT_OK;

public class HealthFragment extends Fragment {

    private static final String TAG = "healthFragmentTAG";
    private HealthViewModel healthViewModel;
    String abridged = "abridged";
    TextView textView;
    Button button;
    TextInputEditText TIETfoodsearch;
    String FDCID = null;

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://api.nal.usda.gov")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        healthViewModel = new ViewModelProvider(requireActivity()).get(HealthViewModel.class);
        View root = inflater.inflate(R.layout.fragment_health, container, false);

        textView = root.findViewById(R.id.TVFoodSearchReturn);
        button = root.findViewById(R.id.foodSearchBtn);
        TIETfoodsearch = root.findViewById(R.id.TIETfoodSearch);
        //test value
        textView.setText(FDCID);

        //when the food item value is returned from the search
        if(FDCID != null){
            fetchFoodNutrients(FDCID);
        }

        button.setOnClickListener(v -> {
            if(TIETfoodsearch.getText() != null){
                HealthFragment.this.fetchFoodSearch();
            }
        });


        return root;

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
                    textView.setText("Code " + response.code() + " " + response.message());
                    return;
                }

                FDCJson posts = response.body();


                FoodResultFragment nextFrag= new FoodResultFragment(posts.getFoods());
                nextFrag.setTargetFragment(HealthFragment.this, 111);
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, nextFrag, "foodListFragment")
                        .addToBackStack(null)
                        .commit();

            }

            @Override
            public void onFailure(@NotNull Call<FDCJson> call, @NotNull Throwable t) {
                textView.setText(t.getMessage());
            }
        });

    }

    private void fetchFoodNutrients(String FDCID){
        //initialize with nutrient codes: 208(Energy), 203(Protein), 204 (Total Lipid[Fat]), and
        // 205 (Carbohydrates)

       ArrayList<Integer> nutrientsList = new ArrayList<>(
               Arrays.asList(203, 204, 205, 208));


        Call<AbridgedFoodItem> listCall = jsonPlaceHolderApi.getFoodNutrients(Integer.parseInt(FDCID), abridged, nutrientsList);

        listCall.enqueue(new Callback<AbridgedFoodItem>() {
            @Override
            public void onResponse(@NotNull Call<AbridgedFoodItem> call,
                                   @NotNull Response<AbridgedFoodItem> response) {
                if (!response.isSuccessful()) {
                    textView.setText("Code " + response.code() + " " + response.message());
                    return;
                }

                AbridgedFoodItem foodData = response.body();

                assert foodData != null;
                for (FoodNutrient nutrient:
                     foodData.getFoodNutrients()) {
                    Log.d(TAG, "onResponse: " + nutrient.getName());
                }



                //textView.setText(appendx.toString());

            }

            @Override
            public void onFailure(@NotNull Call<AbridgedFoodItem> call, @NotNull Throwable t) {
                textView.setText(t.getMessage());
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == 111){
                assert data != null;
                FDCID = data.getStringExtra("FDCID");
                Log.d(TAG, "onActivityResult: " + FDCID);
            }
        }
    }
}
