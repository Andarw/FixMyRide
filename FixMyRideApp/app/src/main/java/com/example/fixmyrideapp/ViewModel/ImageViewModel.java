package com.example.fixmyrideapp.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.fixmyrideapp.entity.Image;
import com.example.fixmyrideapp.helpers.DatabaseManager;

import java.util.List;

public class ImageViewModel extends AndroidViewModel {
    DatabaseManager databaseManager;
    public ImageViewModel(@NonNull Application application) {
        super(application);
        databaseManager = DatabaseManager.getInstance(application.getApplicationContext());
    }

    public LiveData<List<Image>> getImagesByReportIdLiveData(int reportId){
        return databaseManager.imageDao().getImagesByReportIdLiveData(reportId);
    }
}
