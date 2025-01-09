package com.example.fixmyrideapp.DaoInterface;

import com.example.fixmyrideapp.entity.Image;
import androidx.lifecycle.LiveData;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ImageDao {

    @Insert
    void insert(Image image);

    @Insert
    void insertAll(Image... images);

    @Delete
    void delete(Image image);

    @Update
    void update(Image image);

    @Query("SELECT * FROM images WHERE image_id = :imageId")
    Image getImageById(int imageId);
    @Query("SELECT * FROM images WHERE image_report_id = :reportId")
    List<Image> getImagesByReportId(int reportId);
    @Query("SELECT * FROM images WHERE image_report_id = :reportId")
    LiveData<List<Image>> getImagesByReportIdLiveData(int reportId);
}
