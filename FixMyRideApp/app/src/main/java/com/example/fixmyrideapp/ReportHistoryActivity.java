package com.example.fixmyrideapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fixmyrideapp.ViewModel.ReportViewModel;
import com.example.fixmyrideapp.helpers.ReportsHistoryAdapter;

import java.util.ArrayList;

public class ReportHistoryActivity extends AppCompatActivity {

    private RecyclerView reportsRecyclerView;
    private ReportViewModel reportViewModel;
    private ReportsHistoryAdapter reportsHistoryAdapter;

    private TextView emptyStateText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_report_history);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String userId = getIntent().getStringExtra("userId");

        if(userId == null || userId.isEmpty()){
            Log.d("ReportActivity", "User ID not found");
            runOnUiThread(()->{
                Toast.makeText(this, "Report ID not found", Toast.LENGTH_LONG).show();
            });
            finish();
        }

        emptyStateText = findViewById(R.id.emptyStateText);

        reportsRecyclerView = findViewById(R.id.reportsRecyclerView);
        reportsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        reportsHistoryAdapter = new ReportsHistoryAdapter(new ArrayList<>(), this);
        reportsRecyclerView.setAdapter(reportsHistoryAdapter);

        reportViewModel = new ViewModelProvider(this).get(ReportViewModel.class);
        observeReports(userId);
    }

    private void observeReports(String userId) {
        reportViewModel.getReportsByUserIdLiveData(userId).observe(this, reports -> {
            if (reports != null && !reports.isEmpty()) {
                emptyStateText.setVisibility(View.GONE);
                reportsHistoryAdapter.updateReports(reports);
            }else{
                emptyStateText.setVisibility(View.VISIBLE);
            }
        });
    }
}