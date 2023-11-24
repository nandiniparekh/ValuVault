package com.example.cmput301groupproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private EditText usernameEditText;
    private Button loginButton;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = FirebaseFirestore.getInstance();

        usernameEditText = findViewById(R.id.usernameEditText);
        loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredUsername = usernameEditText.getText().toString().trim();

                // Check if the entered username is in the "users" collection
                checkUsernameInDatabase(enteredUsername);
            }
        });
    }

    private void checkUsernameInDatabase(final String username) {
        db.collection("users")
                .document(username)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Username found in the database, start MainActivity
                        startMainActivity(username);
                    } else {
                        // Username not found, add it to the database and start MainActivity
                        addUsernameToDatabase(username);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this, "Error checking username: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addUsernameToDatabase(final String username) {
        Map<String, Object> user = new HashMap<>();
        // You can add additional user information here if needed
        // For example: user.put("email", userEmail);

        db.collection("users")
                .document(username)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Username added to the database, start MainActivity
                        startMainActivity(username);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this, "Error adding username: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void startMainActivity(String username) {
        // Start MainActivity with the username as the collectionPath
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("userDoc", username);
        startActivity(intent);
        finish(); // Finish the LoginActivity to prevent going back to it when pressing back in MainActivity
    }
}

