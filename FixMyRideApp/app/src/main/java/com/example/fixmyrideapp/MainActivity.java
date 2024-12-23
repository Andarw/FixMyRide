package com.example.fixmyrideapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button signInButton;
    private Button loginButton;
    private Button logoutButton;

    private Button createReportButton;
    private View noAuthButtonsContainer;

    private View loggedInButtonsContainer;

    private Button reportHistoryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        signInButton = findViewById(R.id.signInButton);
        loginButton = findViewById(R.id.loginButton);
        logoutButton = findViewById(R.id.logoutButton);
        createReportButton = findViewById(R.id.createNewReportButton);
        reportHistoryButton = findViewById(R.id.reportHistoryButton);
        noAuthButtonsContainer = findViewById(R.id.noAuthButtonsContainer);
        loggedInButtonsContainer = findViewById(R.id.loggedInButtonsContainer);

        // Set click listeners
        signInButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
            startActivity(intent);
        });

        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LogInActivity.class);
            startActivity(intent);
        });

        logoutButton.setOnClickListener(v -> {
            mAuth.signOut();
            updateUI(null);
        });

        createReportButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ImagesActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            noAuthButtonsContainer.setVisibility(View.GONE);
            loggedInButtonsContainer.setVisibility(View.VISIBLE);
        } else {
            noAuthButtonsContainer.setVisibility(View.VISIBLE);
            loggedInButtonsContainer.setVisibility(View.GONE);
        }
    }
}