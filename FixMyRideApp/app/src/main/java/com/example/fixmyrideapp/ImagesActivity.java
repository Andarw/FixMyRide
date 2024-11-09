package com.example.fixmyrideapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;

public class ImagesActivity extends AppCompatActivity {

    private ImageView imageView;
    private Uri imageUri;  // URI for the captured photo

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);

        imageView = findViewById(R.id.placeholderImage);
//        Button galleryButton = findViewById(R.id.galleryButton);
//        Button cameraButton = findViewById(R.id.cameraButton);
//        Button uploadButton = findViewById(R.id.uploadButton);

//        galleryButton.setOnClickListener(v -> openGallery());
//        cameraButton.setOnClickListener(v -> capturePhoto());
//        uploadButton.setOnClickListener(v -> uploadImage());
    }

    // Launcher to get the image from the gallery
    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri selectedImage = result.getData().getData();
                    imageView.setImageURI(selectedImage);
                    imageUri = selectedImage;
                } else {
                    Toast.makeText(this, "Failed to pick image", Toast.LENGTH_SHORT).show();
                }
            });

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    // Launcher to capture a photo with the camera
    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    imageView.setImageURI(imageUri);
                } else {
                    Toast.makeText(this, "Failed to capture image", Toast.LENGTH_SHORT).show();
                }
            });

    private void capturePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            try {
                File photoFile = File.createTempFile("IMG_", ".jpg", getExternalFilesDir(null));
                imageUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                cameraLauncher.launch(intent);
            } catch (IOException e) {
                Toast.makeText(this, "Error creating image file", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadImage() {
        if (imageUri != null) {
            // Code to upload the image to Firebase or local storage.
            // Placeholder message:
            Toast.makeText(this, "Image uploaded!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }
}
