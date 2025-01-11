package com.example.fixmyrideapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
            "Bumper Dent", "Bumper Scratch", "Tail Lamp", "Head Lamp", "Glass Shatter", "Door Scratch", "Door Dent"
    );

    private static final String vmIp = "192.168.100.15";
    private static final String postUrl = "http://" + vmIp + ":" + "5000" + "/";

    String responseBody = "";

    private int reportId;

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
        Button discardUnfinishedReportButton = (Button) findViewById(R.id.btn_discard_report);

        ArrayAdapter<CharSequence> adapterBrandItems = ArrayAdapter.createFromResource(
                this,
                R.array.Brand_spinner_items,
                R.layout.colored_spinner_layout
        );
//        ArrayAdapter<CharSequence> FullModelItems;
        ArrayAdapter<CharSequence> AdapterModelItems = ArrayAdapter.createFromResource(
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
        adapterYearItems.setDropDownViewResource(R.layout.spinner_dropdown_layout);

        spinnerBrand.setAdapter(adapterBrandItems);
        spinnerBrand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ArrayAdapter<CharSequence> FullModelItems = ArrayAdapter.createFromResource(
                        getApplicationContext(),
                        R.array.Model_spinner_items,
                        R.layout.colored_spinner_layout
                );
                if(position == 6) {
                    FullModelItems = ArrayAdapter.createFromResource(
                            getApplicationContext(),
                            R.array.VW_spinner_items,
                            R.layout.colored_spinner_layout
                    );
                }
                else if(position == 1) {
                    FullModelItems = ArrayAdapter.createFromResource(
                            getApplicationContext(),
                            R.array.bmw_spinner_items,
                            R.layout.colored_spinner_layout
                    );
                }
                else if(position == 0) {
                    FullModelItems = ArrayAdapter.createFromResource(
                            getApplicationContext(),
                            R.array.audi_spinner_items,
                            R.layout.colored_spinner_layout
                    );
                }
                else if(position == 2) {
                    FullModelItems = ArrayAdapter.createFromResource(
                            getApplicationContext(),
                            R.array.mazda_spinner_items,
                            R.layout.colored_spinner_layout
                    );
                }
                else if(position == 3) {
                    FullModelItems = ArrayAdapter.createFromResource(
                            getApplicationContext(),
                            R.array.mercedes_spinner_items,
                            R.layout.colored_spinner_layout
                    );
                }
                else if(position == 4) {
                    FullModelItems = ArrayAdapter.createFromResource(
                            getApplicationContext(),
                            R.array.mini_spinner_items,
                            R.layout.colored_spinner_layout
                    );
                }
                else if(position == 5) {
                    FullModelItems = ArrayAdapter.createFromResource(
                            getApplicationContext(),
                            R.array.toyota_spinner_items,
                            R.layout.colored_spinner_layout
                    );
                }

                FullModelItems.setDropDownViewResource(R.layout.spinner_dropdown_layout);
                spinnerModel.setAdapter(FullModelItems);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerYear.setAdapter(adapterYearItems);

        int brandPosition = adapterBrandItems.getPosition(getIntent().getStringExtra("brand"));
        spinnerBrand.setSelection(brandPosition);

        int modelPosition = AdapterModelItems.getPosition(getIntent().getStringExtra("model"));
        spinnerModel.setSelection(modelPosition);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.report_generation_container), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        generateReportButton.setOnClickListener(v -> {
            createReport();
        });

        discardUnfinishedReportButton.setOnClickListener(v -> {
            deleteUnfinishedReport();
            Intent intent = new Intent(ReportGenerationActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
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
                deleteUnfinishedReport();
                Log.d("FAIL ReportGenAct", Objects.requireNonNull(e.getMessage()));
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

        updateReportInfoBeforeResponse(selectedTags.toString(), selectedBrand, selectedModel, selectedYear);

        postRequest(postUrl, createRequestBody(selectedTags.toString(), selectedBrand, selectedModel, selectedYear));

        Intent intent = new Intent(ReportGenerationActivity.this, ReportActivity.class);
        intent.putExtra("reportId", reportId);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    // Helper to update the report info before the response
    private void updateReportInfoBeforeResponse(String selectedDamage, String selectedBrand, String selectedModel, String selectedYear) {
        // update info for the current report, selectedDamage, selectedBrand, selectedModel, selectedYear and add estimated cost, time and links for parts
        String userId = getIntent().getStringExtra("userId");
            AsyncTask.execute(() -> {
            DatabaseManager db = DatabaseManager.getInstance(getApplicationContext());
            Report report = db.reportDao().getUnfinishedReportByUserId(userId);
            reportId = report.getReportId();
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
        String fullModel = splitResponse[0];
        String estimatedCost = splitResponse[1];
        StringBuilder linksBuilder = new StringBuilder();
        for(int i = 2; i < splitResponse.length; i++){
            linksBuilder.append(splitResponse[i]);
            linksBuilder.append(",");
        }
        String links = linksBuilder.toString();

        String userId = getIntent().getStringExtra("userId");
        String timestamp = new Date(System.currentTimeMillis()).toString();
        AsyncTask.execute(() -> {
            DatabaseManager db = DatabaseManager.getInstance(getApplicationContext());
            Report report = db.reportDao().getUnfinishedReportByUserId(userId);
            report.setCarModel(fullModel);
            report.setEstimatedCost(estimatedCost);
            report.setPartLinks(links);
            report.setCreatedAt(timestamp);
            report.setFinished(true);
            db.reportDao().update(report);
        });
    }

    void deleteUnfinishedReport() {
        String userId = getIntent().getStringExtra("userId");
        AsyncTask.execute(() -> {
            DatabaseManager db = DatabaseManager.getInstance(getApplicationContext());
            Report report = db.reportDao().getUnfinishedReportByUserId(userId);
            db.reportDao().delete(report);
        });
        Log.d("ReportGenerationActivity", "Unfinished report deleted");
    }

}