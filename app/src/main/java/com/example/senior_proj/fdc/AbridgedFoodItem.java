package com.example.senior_proj.fdc;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AbridgedFoodItem {

    @SerializedName("dataType")
    @Expose
    private String dataType;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("fdcId")
    @Expose
    private Integer fdcId;
    @SerializedName("foodNutrients")
    @Expose
    private List<FoodNutrient> foodNutrients = null;
    @SerializedName("publicationDate")
    @Expose
    private String publicationDate;
    @SerializedName("brandOwner")
    @Expose
    private String brandOwner;
    @SerializedName("gtinUpc")
    @Expose
    private String gtinUpc;
    @SerializedName("ndbNumber")
    @Expose
    private String ndbNumber;
    @SerializedName("foodCode")
    @Expose
    private String foodCode;

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

    public Integer getFdcId() {
        return fdcId;
    }

    public void setFdcId(Integer fdcId) {
        this.fdcId = fdcId;
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

    public String getNdbNumber() {
        return ndbNumber;
    }

    public void setNdbNumber(String ndbNumber) {
        this.ndbNumber = ndbNumber;
    }

    public String getFoodCode() {
        return foodCode;
    }

    public void setFoodCode(String foodCode) {
        this.foodCode = foodCode;
    }

}