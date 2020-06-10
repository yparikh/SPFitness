package com.example.senior_proj.fdc;

import com.example.senior_proj.BuildConfig;
import com.example.senior_proj.fdc.AbridgedFoodItem;
import com.example.senior_proj.fdc.FDCJson;
import com.example.senior_proj.fdc.FoodSearchCriteria;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface JsonPlaceHolderApi {
    String APIKEY = BuildConfig.API_KEY;


    @Headers("Content-Type: application/json")
    @POST("/fdc/v1/foods/search?api_key="+APIKEY)
    Call<FDCJson> getFoodSearch(@Body FoodSearchCriteria body);

    @Headers("Content-Type: application/json")
    @GET("/fdc/v1/food/{fdcid}?api_key="+APIKEY)
    Call<AbridgedFoodItem> getFoodNutrients(@Path("fdcid") Integer fdcID,
                                            @Query("format") String abridged,
                                            @Query("nutrients") ArrayList<Integer> foodNutrients);
}
