package com.example.fixmyrideapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private EditText emailEditText, passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize Firebase Auth and Database Reference
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        // Initialize views
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        Button loginButton = findViewById(R.id.loginButton);
        Button signUpButton = findViewById(R.id.signUpButton);

        // Set listeners for buttons
        loginButton.setOnClickListener(v -> navigateToLogin());
        signUpButton.setOnClickListener(v -> signUpUser());
    }

    private void navigateToLogin() {
        // Start LoginActivity
        Intent intent = new Intent(SignUpActivity.this, LogInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish(); // Optionally finish SignUpActivity to prevent returning to it
    }

    private void signUpUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Validate email and password input
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(SignUpActivity.this, "Please enter email and password",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Firebase requires password to be at least 6 characters
        if (password.length() < 6) {
            Toast.makeText(SignUpActivity.this, "Password must be at least 6 characters",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Attempt to create a new user with Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign up success, add user data to the database
                        FirebaseUser user = mAuth.getCurrentUser();
                        addUserToDatabase(user);
                        Toast.makeText(SignUpActivity.this, "Sign up successful!",
                                Toast.LENGTH_SHORT).show();
                        updateUI(user);
                    } else {
                        // If sign-up fails, display an error message
                        Toast.makeText(SignUpActivity.this, "Sign up failed: " +
                                        Objects.requireNonNull(task.getException()).getMessage(),
                                Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }
                });
    }

    private void addUserToDatabase(FirebaseUser user) {
        if (user != null) {
            String userId = user.getUid();
            String email = user.getEmail();

            // Create a map to hold the user's data
            Map<String, String> userData = new HashMap<>();
            userData.put("userId", userId);
            userData.put("email", email);

            // Store the user data under the "Users" node, with userId as the key
            databaseReference.child(userId).setValue(userData)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(SignUpActivity.this, "User added to database.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SignUpActivity.this, "Failed to add user to database.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
