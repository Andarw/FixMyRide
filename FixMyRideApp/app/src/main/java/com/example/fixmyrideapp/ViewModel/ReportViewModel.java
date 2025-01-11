package com.example.fixmyrideapp.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.fixmyrideapp.entity.Report;
import com.example.fixmyrideapp.helpers.DatabaseManager;

import java.util.List;

public class ReportViewModel extends AndroidViewModel {

    DatabaseManager databaseManager;
    public ReportViewModel(@NonNull Application application) {
        super(application);
        databaseManager = DatabaseManager.getInstance(application.getApplicationContext());
    }

    public LiveData<List<Report>> getReportsByUserIdLiveData(String userId){
        return databaseManager.reportDao().getReportsByUserIdLiveData(userId);
    }

    public LiveData<Report> getReportByIdLiveData(int reportId){
        return databaseManager.reportDao().getReportByIdLiveData(reportId);
    }

    public void deleteReport(Report report){
        databaseManager.reportDao().delete(report);
    }
}
