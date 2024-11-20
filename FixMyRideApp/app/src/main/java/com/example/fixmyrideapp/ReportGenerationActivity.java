package com.example.fixmyrideapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class ReportGenerationActivity extends AppCompatActivity {

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_report_generation);
        Spinner spinnerBrand = (Spinner) findViewById(R.id.spinner_brand);
        Spinner spinnerModel = (Spinner) findViewById(R.id.spinner_model);
        Spinner spinnerYear = (Spinner) findViewById(R.id.spinner_year);
        Button generateReportButton = (Button) findViewById(R.id.btn_create_report);
        ArrayAdapter<CharSequence> adapterBrandItems = ArrayAdapter.createFromResource(
                this,
                R.array.Brand_spinner_items,
                R.layout.colored_spinner_layout
        );
        ArrayAdapter<CharSequence> adapterModelItems = ArrayAdapter.createFromResource(
                this,
                R.array.Model_spinner_items,
                R.layout.colored_spinner_layout
        );
        ArrayAdapter<CharSequence> adapterYearItems = ArrayAdapter.createFromResource(
                this,
                R.array.Year_spinner_items,
                R.layout.colored_spinner_layout
        );
        adapterBrandItems.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        adapterModelItems.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        adapterYearItems.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        spinnerBrand.setAdapter(adapterBrandItems);
        spinnerModel.setAdapter(adapterModelItems);
        spinnerYear.setAdapter(adapterYearItems);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
    }
}