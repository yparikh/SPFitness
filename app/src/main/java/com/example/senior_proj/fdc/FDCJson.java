package com.example.senior_proj.fdc;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FDCJson {

    @SerializedName("foodSearchCriteria")
    @Expose
    private FoodSearchCriteria foodSearchCriteria;
    @SerializedName("totalHits")
    @Expose
    private Integer totalHits;
    @SerializedName("currentPage")
    @Expose
    private Integer currentPage;
    @SerializedName("totalPages")
    @Expose
    private Integer totalPages;
    @SerializedName("foods")
    @Expose
    private List<Food> foods = null;

    public FoodSearchCriteria getFoodSearchCriteria() {
        return foodSearchCriteria;
    }

    public void setFoodSearchCriteria(FoodSearchCriteria foodSearchCriteria) {
        this.foodSearchCriteria = foodSearchCriteria;
    }

    public Integer getTotalHits() {
        return totalHits;
    }

    public void setTotalHits(Integer totalHits) {
        this.totalHits = totalHits;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public List<Food> getFoods() {
        return foods;
    }

    public void setFoods(List<Food> foods) {
        this.foods = foods;
    }

}
