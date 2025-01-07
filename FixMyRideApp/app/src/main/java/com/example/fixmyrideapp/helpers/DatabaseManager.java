package com.example.fixmyrideapp.helpers;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.fixmyrideapp.DaoInterface.ImageDao;
import com.example.fixmyrideapp.DaoInterface.ReportDao;
import com.example.fixmyrideapp.entity.Image;
import com.example.fixmyrideapp.entity.Report;

@Database(entities = {Report.class, Image.class}, version = 1)
public abstract class DatabaseManager extends RoomDatabase {
    public abstract ReportDao reportDao();
    public abstract ImageDao imageDao();
    private static volatile DatabaseManager INSTANCE;

    static DatabaseManager getInstance(Context context){
        if(INSTANCE == null){
            synchronized (DatabaseManager.class){
                if(INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), DatabaseManager.class, "FixMyRideDB").build();
                }
            }
        }
        return INSTANCE;
    }
}
