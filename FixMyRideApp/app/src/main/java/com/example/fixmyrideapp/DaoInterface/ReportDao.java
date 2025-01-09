package com.example.fixmyrideapp.DaoInterface;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.fixmyrideapp.entity.Report;

import java.util.List;

@Dao
public interface ReportDao {
    @Insert
    void insert(Report report);
    @Insert
    void insertAll(Report... reports);
    @Delete
    void delete(Report report);
    @Update
    void update(Report report);
    @Query("SELECT * FROM reports WHERE report_id = :reportId")
    Report getReportById(int reportId);
    @Query("SELECT * FROM reports WHERE user_id = :userId")
    List<Report> getReportsByUserId(String userId);
    @Query("SELECT * FROM reports WHERE user_id = :userId AND is_finished = 0 LIMIT 1")
    Report getUnfinishedReportByUserId(String userId);
    @Query("SELECT * FROM reports WHERE report_id = :reportId")
    LiveData<Report> getReportByIdLiveData(int reportId);
    @Query("SELECT * FROM reports WHERE user_id = :userId")
    LiveData<List<Report>> getReportsByUserIdLiveData(String userId);
}
