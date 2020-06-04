package com.example.senior_proj.fdc;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Food {

    @SerializedName("fdcId")
    @Expose
    private Integer fdcId;
    @SerializedName("dataType")
    @Expose
    private String dataType;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("foodCode")
    @Expose
    private String foodCode;
    @SerializedName("foodNutrients")
    @Expose
    private List<FoodNutrient> foodNutrients = null;
    @SerializedName("publicationDate")
    @Expose
    private String publicationDate;
    @SerializedName("scientificName")
    @Expose
    private String scientificName;
    @SerializedName("brandOwner")
    @Expose
    private String brandOwner;
    @SerializedName("gtinUpc")
    @Expose
    private String gtinUpc;
    @SerializedName("ingredients")
    @Expose
    private String ingredients;
    @SerializedName("ndbNumber")
    @Expose
    private String ndbNumber;
    @SerializedName("additionalDescriptions")
    @Expose
    private String additionalDescriptions;
    @SerializedName("allHighlightFields")
    @Expose
    private String allHighlightFields;
    @SerializedName("score")
    @Expose
    private Double score;

    public Integer getFdcId() {
        return fdcId;
    }

    public void setFdcId(Integer fdcId) {
        this.fdcId = fdcId;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFoodCode() {
        return foodCode;
    }

    public void setFoodCode(String foodCode) {
        this.foodCode = foodCode;
    }

    public List<FoodNutrient> getFoodNutrients() {
        return foodNutrients;
    }

    public void setFoodNutrients(List<FoodNutrient> foodNutrients) {
        this.foodNutrients = foodNutrients;
    }

    public String getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(String publicationDate) {
        this.publicationDate = publicationDate;
    }

    public String getScientificName() {
        return scientificName;
    }

    public void setScientificName(String scientificName) {
        this.scientificName = scientificName;
    }

    public String getBrandOwner() {
        return brandOwner;
    }

    public void setBrandOwner(String brandOwner) {
        this.brandOwner = brandOwner;
    }

    public String getGtinUpc() {
        return gtinUpc;
    }

    public void setGtinUpc(String gtinUpc) {
        this.gtinUpc = gtinUpc;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getNdbNumber() {
        return ndbNumber;
    }

    public void setNdbNumber(String ndbNumber) {
        this.ndbNumber = ndbNumber;
    }

    public String getAdditionalDescriptions() {
        return additionalDescriptions;
    }

    public void setAdditionalDescriptions(String additionalDescriptions) {
        this.additionalDescriptions = additionalDescriptions;
    }

    public String getAllHighlightFields() {
        return allHighlightFields;
    }

    public void setAllHighlightFields(String allHighlightFields) {
        this.allHighlightFields = allHighlightFields;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

}
