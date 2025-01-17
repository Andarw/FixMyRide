package com.example.fixmyrideapp.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.sql.Date;
import java.sql.Timestamp;

@Entity(tableName = "reports")
public class Report {

    public Report(@NonNull String userId, boolean isFinished) {
        this.userId = userId;
        this.isFinished = isFinished;
    }
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "report_id")
    private int reportId; // Maps to report_id
    @ColumnInfo(name = "user_id")
    @NonNull
    private String userId; // Maps to user_id
    @ColumnInfo(name = "car_brand")
    private String carBrand; // Maps to car_brand
    @ColumnInfo(name = "car_model")
    private String carModel; // Maps to car_model
    @ColumnInfo(name = "car_year")
    private String carYear; // Maps to car_year
    @ColumnInfo(name = "damaged_location")
    private String damagedLocation; // Maps to damaged_location
    @ColumnInfo(name = "part_links")
    private String partLinks; // Maps to part_links
    @ColumnInfo(name = "estimated_cost")
    private String estimatedCost; // Maps to estimated_cost
    @ColumnInfo(name = "created_at")
    private String createdAt; // Maps to created_at (use type converters for timestamp)
    @ColumnInfo(name = "is_finished")
    @NonNull
    private boolean isFinished;

    public int getReportId() {
        return reportId;
    }

    public void setReportId(int reportId) {
        this.reportId = reportId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCarBrand() {
        return carBrand;
    }

    public void setCarBrand(String carBrand) {
        this.carBrand = carBrand;
    }

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public String getDamagedLocation() {
        return damagedLocation;
    }

    public void setDamagedLocation(String damagedLocation) {
        this.damagedLocation = damagedLocation;
    }

    public String getPartLinks() {
        return partLinks;
    }

    public void setPartLinks(String partLinks) {
        this.partLinks = partLinks;
    }

    public String getEstimatedCost() {
        return estimatedCost;
    }

    public void setEstimatedCost(String estimatedCost) {
        this.estimatedCost = estimatedCost;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }

    public String getCarYear() {
        return carYear;
    }

    public void setCarYear(String carYear) {
        this.carYear = carYear;
    }
}
