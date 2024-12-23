package com.example.fixmyrideapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.Arrays;
import java.util.List;

public class ReportGenerationActivity extends AppCompatActivity {

    private final List<String> predefinedTags = Arrays.asList(
            "Bumper Dent", "Tail Light", "Windshield Crack", "Door Scratch", "Prediction", "Prediction1", "Prediction2", "Prediction3", "Prediction4", "Prediction5"
    );

    ChipGroup selectedTagsChipGroup;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_report_generation);

        selectedTagsChipGroup = findViewById(R.id.selectedTagsChipGroup);
        AutoCompleteTextView tagAutoCompleteTextView = findViewById(R.id.tagAutoCompleteTextView);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                predefinedTags
        );

        tagAutoCompleteTextView.setAdapter(adapter);
        addTagsToChipGroup(tokenizeString(getIntent().getStringExtra("damage")), selectedTagsChipGroup);

        tagAutoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedTag = adapter.getItem(position);
            if (selectedTag != null && !isTagAlreadyAdded(selectedTag, selectedTagsChipGroup)) {
                addTagToChipGroup(selectedTag, selectedTagsChipGroup);
            } else {
                Toast.makeText(ReportGenerationActivity.this, "Tag already added", Toast.LENGTH_SHORT).show();
            }
            tagAutoCompleteTextView.setText("");
        });

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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.report_generation_container), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Helper to check if a tag is already added
    private boolean isTagAlreadyAdded(String tag, ChipGroup chipGroup) {
        for (int i = 0; i < chipGroup.getChildCount(); i++) {
            Chip chip = (Chip) chipGroup.getChildAt(i);
            if (chip.getText().toString().equals(tag)) {
                return true;
            }
        }
        return false;
    }

    // Helper to add a tag as a Chip
    private void addTagsToChipGroup(List<String> tags, ChipGroup chipGroup) {
        for (String tag : tags) {
            addTagToChipGroup(tag, chipGroup);
        }
    }

    private void addTagToChipGroup(String tag, ChipGroup chipGroup) {
        Chip chip = new Chip(this);
        chip.setText(tag);
        chip.setCloseIconVisible(true); // Show close icon
        chip.setOnCloseIconClickListener(v -> chipGroup.removeView(chip)); // Remove chip on close
        chipGroup.addView(chip); // Add chip to ChipGroup
    }

    // Helper to tokenize the prediction string and create a list with the tokens
    private List<String> tokenizeString(String str) {
        if (str == null) return List.of();
        return Arrays.asList(str.split("\\s*,\\s*"));
    }

    private void createReport() {
        StringBuilder selectedTags = new StringBuilder();
        for (int i = 0; i < selectedTagsChipGroup.getChildCount(); i++) {
            Chip chip = (Chip) selectedTagsChipGroup.getChildAt(i);
            selectedTags.append(chip.getText().toString());
            if (i != selectedTagsChipGroup.getChildCount() - 1) {
                selectedTags.append(", ");
            }
        }

        Log.d("ReportGenerationActivity", "Selected tags: " + selectedTags.toString());
        // Get the selected brand
        Spinner spinnerBrand = findViewById(R.id.spinner_brand);
        String selectedBrand = spinnerBrand.getSelectedItem().toString();
        Log.d("ReportGenerationActivity", "Selected brand: " + selectedBrand);
        // Get the selected model
        Spinner spinnerModel = findViewById(R.id.spinner_model);
        String selectedModel = spinnerModel.getSelectedItem().toString();
        Log.d("ReportGenerationActivity", "Selected model: " + selectedModel);
        // Get the selected year
        Spinner spinnerYear = findViewById(R.id.spinner_year);
        String selectedYear = spinnerYear.getSelectedItem().toString();
        Log.d("ReportGenerationActivity", "Selected year: " + selectedYear);
    }
}