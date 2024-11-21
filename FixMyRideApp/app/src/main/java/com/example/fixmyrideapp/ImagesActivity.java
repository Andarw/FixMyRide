package com.example.fixmyrideapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import android.widget.Toast;
import android.Manifest;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fixmyrideapp.helpers.ImageAdapter;
import com.example.fixmyrideapp.helpers.UnsafeOkHttpClient;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ImagesActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST_CODE = 1;
    private RecyclerView recyclerView;
    private ImageAdapter imageAdapter;
    private List<Uri> imagesUri = new ArrayList<>();
    ActivityResultLauncher<Uri> takePictureLauncher;
    private Uri photoUri;
    private ImageButton uploadButton;
    private ImageButton clearImagesButton;

    String damage_area = "";

    // Variables for the server connection
    private static final String vmIp = "192.168.50.106";
//    private final String vmIp = "192.168.100.15";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        imageAdapter = new ImageAdapter(this, imagesUri);
        recyclerView.setAdapter(imageAdapter);
        photoUri = createUri();

        registerPictureLauncher();

        ImageButton galleryButton = findViewById(R.id.galleryButton);
        ImageButton cameraButton = findViewById(R.id.cameraButton);
        ImageButton logoutButton = findViewById(R.id.logoutButton);
        uploadButton = findViewById(R.id.createReportButton);
        clearImagesButton = findViewById(R.id.clearImagesButton);

        updateUi();

        galleryButton.setOnClickListener(v -> openGallery());
        cameraButton.setOnClickListener(v -> checkCameraPermissionAndOpenCamera());
        uploadButton.setOnClickListener(v -> CreateReport());
        clearImagesButton.setOnClickListener(v -> clearImages());
        logoutButton.setOnClickListener(v -> logout());
    }

    private void logout() {
        imagesUri.clear();
        Intent intent = new Intent(this, LogInActivity.class);
        startActivity(intent);
    }

    // Update the UI
    @SuppressLint("NotifyDataSetChanged")
    private void updateUi() {
        if (imagesUri.isEmpty()) {
            uploadButton.setVisibility(View.GONE);
            clearImagesButton.setVisibility(View.GONE);
        } else {
            uploadButton.setVisibility(View.VISIBLE);
            clearImagesButton.setVisibility(View.VISIBLE);
        }
        imageAdapter.notifyDataSetChanged();
    }

    // Clear the images list HELPER METHOD
    @SuppressLint("NotifyDataSetChanged")
    private void clearImages() {
        imagesUri.clear();
        imageAdapter.notifyDataSetChanged();
        updateUi();
    }

    // Update the image list HELPER METHOD
    @SuppressLint("NotifyDataSetChanged")
    private void updateImageList(List<Uri> selectedImages) {
        if (selectedImages.isEmpty()) {
            Toast.makeText(this, "No images selected", Toast.LENGTH_SHORT).show();
        } else {
            imagesUri.clear();
            imagesUri.addAll(selectedImages);
            imageAdapter.notifyDataSetChanged();
            updateUi();
        }
    }


    // Register the gallery launcher method HELPER METHOD
    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    // Create a list to store selected image URIs
                    List<Uri> selectedImages = new ArrayList<>();

                    // Check if multiple items were selected
                    if (result.getData().getClipData() != null) {
                        int count = result.getData().getClipData().getItemCount();
                        for (int i = 0; i < count; i++) {
                            System.out.println("Image URI: " + result.getData().getClipData().getItemAt(i).getUri());
                            Uri imagesUri = result.getData().getClipData().getItemAt(i).getUri();
                            selectedImages.add(imagesUri);
                        }
                    } else if (result.getData().getData() != null) {
                        // Single image selected
                        Uri imagesUri = result.getData().getData();
                        selectedImages.add(imagesUri);
                    }
                    updateImageList(selectedImages);
                } else {
                    Toast.makeText(this, "Failed to pick images", Toast.LENGTH_SHORT).show();
                }
            }
    );

    // Open the gallery to select images
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); // Allow multiple selections
        galleryLauncher.launch(intent);
    }

    // Register the picture launcher method HELPER METHOD
    private void registerPictureLauncher() {
        takePictureLauncher = registerForActivityResult(new ActivityResultContracts.TakePicture(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                try {
                    if (result) {
                        imagesUri.add(photoUri);
                        photoUri = createUri();
                        updateUi();
                    }
                } catch (Exception e) {
                    e.getStackTrace();
                }
            }
        });
    }


    // Check if the camera permission is granted, if not request it
    private void checkCameraPermissionAndOpenCamera() {
        if (ActivityCompat.checkSelfPermission(ImagesActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ImagesActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        } else {
            takePictureLauncher.launch(photoUri);
        }
    }

    // Request permission result method, if permission is not already granted, request it HELPER METHOD
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePictureLauncher.launch(photoUri);
            } else {
                Toast.makeText(ImagesActivity.this, "Camera permission is required to use the camera", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Create a URI for the image file HELPER METHOD
    private Uri createUri() {
        File imageFile = new File(getApplicationContext().getFilesDir(), "camera_photo.jpg");
        return FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", imageFile);
    }

    public void connectServer() {
        String postUrl = "http://" + vmIp + ":" + "5000" + "/";
//        String postUrl = "https://andreidragos029-a625bdec-c5a2-45ce-8f5f-7a8aecc5eed6.socketxp.com/";
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            if(imagesUri.isEmpty()){
                Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
                return;
            }
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imagesUri.get(0));
            imagesUri.clear();
            updateUi();
            bitmap.compress(Bitmap.CompressFormat.WEBP, 100, stream);
        } catch (Exception e) {
            Toast.makeText(this, "Please Make Sure the Selected File is an Image.", Toast.LENGTH_SHORT).show();
            return;
        }
        byte[] byteArray = stream.toByteArray();
        RequestBody postBodyImage = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", "brandImage.jpg", RequestBody.create(byteArray, MediaType.parse("image/*jpg")))
                .build();
        postRequest(postUrl, postBodyImage);
    }

    // Create Report method, this is called when the user clicks the "Create Report" button
    private void CreateReport() {
        if (!imagesUri.isEmpty()) {
            updateUi();
            connectServer();
            imagesUri.clear();
        } else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    void postRequest(String postUrl, RequestBody postBody) {
//        OkHttpClient client = UnsafeOkHttpClient.getUnsafeOkHttpClient();
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ImagesActivity.this, "Failed to Connect to Server. Please Try Again.", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull final Response response){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            assert response.body() != null;
                            damage_area = damage_area + response.body().string();
                            Log.d("SUCCESS", "Response: " + damage_area);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        Toast.makeText(ImagesActivity.this, "Report Created Successfully! damage: " + (response.body()).toString(), Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(ImagesActivity.this, ReportGenerationActivity.class);
                        intent.putExtra("damage", damage_area);
                        startActivity(intent);
                    }
                });
            }
        });
    }
}
