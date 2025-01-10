package com.example.fixmyrideapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fixmyrideapp.ViewModel.ImageViewModel;
import com.example.fixmyrideapp.ViewModel.ReportViewModel;
import com.example.fixmyrideapp.entity.Image;
import com.example.fixmyrideapp.entity.Report;
import com.example.fixmyrideapp.helpers.BitmapImageAdapter;
import com.example.fixmyrideapp.helpers.PartLinkAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReportActivity extends AppCompatActivity {

    ReportViewModel reportViewModel;
    ImageViewModel imageViewModel;
    private TextView brandValue, modelValue, yearValue, damagedPartsRecyclerView, estimatedCostValue;
    private RecyclerView partLinksRecyclerView, imagesRecyclerView;
    private PartLinkAdapter partLinksAdapter;
    private BitmapImageAdapter imageAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_report);
        Log.d("ReportActivity", "onCreate  "+ getIntent().getIntExtra("reportId", 0));
        int reportId = getIntent().getIntExtra("reportId", 0);

        if(reportId == 0){
            Log.d("ReportActivity", "Report ID not found");
            runOnUiThread(()->{
                Toast.makeText(this, "Report ID not found", Toast.LENGTH_LONG).show();
            });
            finish();
        }

        initializeViews();
        setupRecyclerViews();



        RecyclerView partLinksRecyclerView = findViewById(R.id.partsLinksRecyclerView);
        partLinksRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        reportViewModel = new ViewModelProvider(this).get(ReportViewModel.class);
        imageViewModel = new ViewModelProvider(this).get(ImageViewModel.class);

        observeData(reportId);
        Button generateReportButton = (Button) findViewById(R.id.homeButton);
        generateReportButton.setOnClickListener(v -> {
            Intent intent = new Intent(ReportActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void initializeViews() {
        brandValue = findViewById(R.id.brandValue);
        modelValue = findViewById(R.id.modelValue);
        yearValue = findViewById(R.id.yearValue);
        damagedPartsRecyclerView = findViewById(R.id.damagedPartsRecyclerView);
        estimatedCostValue = findViewById(R.id.estimatedCostValue);
        partLinksRecyclerView = findViewById(R.id.partsLinksRecyclerView);
        imagesRecyclerView = findViewById(R.id.imagesRecyclerView);
    }

    private void setupRecyclerViews() {
        // Setup Parts Links RecyclerView
        partLinksAdapter = new PartLinkAdapter(new ArrayList<>());
        partLinksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        partLinksRecyclerView.setAdapter(partLinksAdapter);

        // Setup Images RecyclerView
        imageAdapter = new BitmapImageAdapter(new ArrayList<>());
        imagesRecyclerView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );
        imagesRecyclerView.setAdapter(imageAdapter);
    }

    private void observeData(int reportId) {
        // Observe Report Data
        reportViewModel.getReportByIdLiveData(reportId).observe(this, report -> {
            if(report != null) {
                updateReportUI(report);
            } else {
                Log.d("ReportActivity", "Report not found");
                finish();
            }
        });

        // Observe Images
        imageViewModel.getImagesByReportIdLiveData(reportId).observe(this, images -> {
            if(images != null) {
                Log.d("ReportActivity", "Images found");
                updateImagesUI(images);
            }
        });
    }

    private void updateReportUI(Report report) {
        brandValue.setText(report.getCarBrand());
        modelValue.setText(report.getCarModel());
        yearValue.setText(report.getCarYear());
        damagedPartsRecyclerView.setText(report.getDamagedLocation());
        estimatedCostValue.setText(report.getEstimatedCost());

        // Handle parts links
        if (report.getPartLinks() != null && !report.getPartLinks().isEmpty()) {
            List<String> linksList = Arrays.asList(report.getPartLinks().split(","));
            partLinksAdapter.updateLinks(linksList);
        }
    }

    private void updateImagesUI(List<Image> images) {
        List<Bitmap> bitmaps = new ArrayList<>();
        for (Image image : images) {
            byte[] imageData = image.getImage();
            if (imageData != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                bitmaps.add(bitmap);
            }
        }
        imageAdapter.updateImages(bitmaps);
    }
}