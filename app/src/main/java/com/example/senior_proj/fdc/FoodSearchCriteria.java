package com.example.senior_proj.fdc;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FoodSearchCriteria {

    @SerializedName("query")
    @Expose
    private String query;
    @SerializedName("dataType")
    @Expose
    private List<String> dataType = null;
    @SerializedName("pageSize")
    @Expose
    private Integer pageSize;
    @SerializedName("pageNumber")
    @Expose
    private Integer pageNumber;
    @SerializedName("sortBy")
    @Expose
    private String sortBy;
    @SerializedName("sortOrder")
    @Expose
    private String sortOrder;
    @SerializedName("brandOwner")
    @Expose
    private String brandOwner;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public List<String> getDataType() {
        return dataType;
    }

    public void setDataType(List<String> dataType) {
        this.dataType = dataType;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getBrandOwner() {
        return brandOwner;
    }

    public void setBrandOwner(String brandOwner) {
        this.brandOwner = brandOwner;
    }
}
