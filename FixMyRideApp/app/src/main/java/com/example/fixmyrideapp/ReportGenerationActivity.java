package com.example.fixmyrideapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.fixmyrideapp.entity.Report;
import com.example.fixmyrideapp.helpers.DatabaseManager;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.io.IOException;
import java.sql.Date;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ReportGenerationActivity extends AppCompatActivity {

    private final List<String> predefinedTags = Arrays.asList(
            "Bumper Dent", "Tail Light", "Windshield Crack", "Door Scratch", "Prediction", "Prediction1", "Prediction2", "Prediction3", "Prediction4", "Prediction5"
    );

    private static final String vmIp = "192.168.1.2";
    private static final String postUrl = "http://" + vmIp + ":" + "5000" + "/";

    String responseBody = "";

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

        int brandPosition = adapterBrandItems.getPosition(getIntent().getStringExtra("brand"));
        spinnerBrand.setSelection(brandPosition);

        int modelPosition = adapterModelItems.getPosition(getIntent().getStringExtra("model"));
        spinnerModel.setSelection(modelPosition);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.report_generation_container), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        generateReportButton.setOnClickListener(v -> {
            createReport();
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

    private RequestBody createRequestBody(String selectedTags, String selectedBrand, String selectedModel, String selectedYear) {
        return new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("hasImage", "false")
                .addFormDataPart("selectedBrand", selectedBrand)
                .addFormDataPart("selectedModel", selectedModel)
                .addFormDataPart("selectedYear", selectedYear)
                .addFormDataPart("selectedTags", selectedTags)
                .build();
    }

    void postRequest(String postUrl, RequestBody postBody) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(postUrl)
                .post(postBody)
                .build();
        Log.d("REQUEST", "URL: " + postUrl);
        Log.d("REQUEST", "Body: " + postBody.toString());

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                call.cancel();
                Log.d("FAIL", Objects.requireNonNull(e.getMessage()));
                runOnUiThread(() -> Toast.makeText(ReportGenerationActivity.this, "Failed to Connect to Server(ReportGeneration). Please Try Again.", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull final Response response){
                try {
                    assert response.body() != null;
                    responseBody = response.body().string();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Log.d("SUCCESS", "Response: " + responseBody);
                updateReportInfoAfterResponse(responseBody);
                runOnUiThread(() -> {
                    Toast.makeText(ReportGenerationActivity.this, "Report Created Successfully! damage: " + (response.body()).toString(), Toast.LENGTH_LONG).show();
                });
            }
        });
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
        postRequest(postUrl, createRequestBody(selectedTags.toString(), selectedBrand, selectedModel, selectedYear));

        // update info for the current report, selectedDamage, selectedBrand, selectedModel, selectedYear and add estimated cost, time and links for parts

        updateReportInfoBeforeResponse(selectedTags.toString(), selectedBrand, selectedModel, selectedYear);

        Intent intent = new Intent(ReportGenerationActivity.this, ReportActivity.class);
        intent.putExtra("ReportInfo", responseBody);
        startActivity(intent);
    }

    // Helper to update the report info before the response
    private void updateReportInfoBeforeResponse(String selectedDamage, String selectedBrand, String selectedModel, String selectedYear) {
        // update info for the current report, selectedDamage, selectedBrand, selectedModel, selectedYear and add estimated cost, time and links for parts
        String userId = getIntent().getStringExtra("userId");
            AsyncTask.execute(() -> {
            DatabaseManager db = DatabaseManager.getInstance(getApplicationContext());
            Report report = db.reportDao().getUnfinishedReportByUserId(userId);
            report.setDamagedLocation(selectedDamage);
            report.setCarBrand(selectedBrand);
            report.setCarModel(selectedModel);
            report.setCarYear(selectedYear);
            db.reportDao().update(report);
            });
    }


    // Helper to update the report info after the response
    private void updateReportInfoAfterResponse(String responseBody) {
        // update info for the current report, selectedDamage, selectedBrand, selectedModel, selectedYear and add estimated cost, time and links for parts

        String[] splitResponse = responseBody.split(",");
        float estimatedCost = Float.parseFloat(splitResponse[0]);
        String links = splitResponse[1] + "," + splitResponse[2];

        String userId = getIntent().getStringExtra("userId");
        String timestamp = new Date(System.currentTimeMillis()).toString();
        AsyncTask.execute(() -> {
            DatabaseManager db = DatabaseManager.getInstance(getApplicationContext());
            Report report = db.reportDao().getUnfinishedReportByUserId(userId);
            report.setEstimatedCost(estimatedCost);
            report.setPartLinks(links);
            report.setCreatedAt(timestamp);
            report.setFinished(true);
            db.reportDao().update(report);
        });

        //PRINT the updated report
        AsyncTask.execute(() -> {
            DatabaseManager db = DatabaseManager.getInstance(getApplicationContext());
            Report report = db.reportDao().getReportsByUserId(userId).get(0);
            Log.d("DB Report", "Report ID: " + report.getReportId());
            Log.d("Report", "User ID: " + report.getUserId());
            Log.d("Report", "Car Brand: " + report.getCarBrand());
            Log.d("Report", "Car Model: " + report.getCarModel());
            Log.d("Report", "Car Year: " + report.getCarYear());
            Log.d("Report", "Damaged Location: " + report.getDamagedLocation());
            Log.d("Report", "Part Links: " + report.getPartLinks());
            Log.d("Report", "Estimated Cost: " + report.getEstimatedCost());
            Log.d("Report", "Created At: " + report.getCreatedAt());
            Log.d("Report", "Is Finished: " + report.isFinished());
        });
    }

}