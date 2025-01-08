package com.example.fixmyrideapp.entity;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import com.example.fixmyrideapp.ImagesActivity;

@Entity(tableName = "images", foreignKeys = @ForeignKey(entity = Report.class, parentColumns = "report_id", childColumns = "image_report_id", onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.CASCADE))
public class Image {

    public Image(byte[] image, int reportId) {
        this.image = image;
        this.reportId = reportId;
    }

    public Image() {

    }
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name= "image_id")
    private int imageId; // Maps to image_id

    @ColumnInfo(name= "images")
    private byte[] image; // Maps to image (BYTEA)

    @ColumnInfo(name= "image_report_id")
    private int reportId; // Maps to report_id (foreign key)

    // Getters and Setters
    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public int getReportId() {
        return reportId;
    }

    public void setReportId(int reportId) {
        this.reportId = reportId;
    }
}

