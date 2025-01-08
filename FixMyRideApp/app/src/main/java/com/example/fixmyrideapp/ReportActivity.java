package com.example.fixmyrideapp;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.fixmyrideapp.helpers.DatabaseManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ReportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_report);
//        DatabaseManager db = DatabaseManager.getInstance(getApplicationContext());
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        assert user != null;
//        String userId = user.getUid();
//        db.reportDao().getReportsByUserId(userId);

    }


}