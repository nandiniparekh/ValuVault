package com.example.cmput301groupproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * LoginActivity class represents the activity for user login.
 * It allows users to enter a username and checks whether the username
 * exists in the Firestore database. If the username exists, it starts
 * the MainActivity; otherwise, it adds the username to the database
 * and then starts the MainActivity.
 */
public class LoginActivity extends AppCompatActivity {
    private EditText usernameEditText;
    private Button loginButton;
    private FirebaseFirestore db;

    /**
     * Overrides the onCreate method to initialize the activity.
     * Sets the layout, retrieves views, and sets up the click
     * listener for the login button.
     *
     * @param savedInstanceState The saved instance state of the activity.
     */
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

                // Check if the entered username is blank
                if (enteredUsername.isEmpty()) {
                    // Show a toast indicating that the username is required
                    showCenteredToast("Please enter a username (minimum 1 alphanumeric character)");
                } else {
                    // Proceed with checking the username in the database
                    checkUsernameInDatabase(enteredUsername);
                }
            }
        });
    }

    /**
     * Displays a centered custom Toast message.
     *
     * @param message The message to be displayed in the Toast.
     */
    private void showCenteredToast(String message) {
        // Inflate the custom layout
        View toastView = getLayoutInflater().inflate(R.layout.custom_toast_layout, null);

        // Set the message in the TextView
        TextView messageTextView = toastView.findViewById(R.id.toastMessageTextView);
        messageTextView.setText(message);

        // Create and show the custom Toast
        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(toastView);
        toast.show();
    }

    /**
     * Checks if the entered username exists in the Firestore database.
     * If the username exists, it starts the MainActivity; otherwise,
     * it adds the username to the database and then starts MainActivity.
     *
     * @param username The entered username to be checked.
     */
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

    /**
     * Adds the username to the Firestore database and starts the
     * MainActivity upon successful addition.
     *
     * @param username The username to be added to the database.
     */
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

    /**
     * Starts the MainActivity with the provided username as the
     * collectionPath. Also, finishes the LoginActivity to prevent
     * going back to it when pressing back in MainActivity.
     *
     * @param username The username used as the collectionPath.
     */
    private void startMainActivity(String username) {
        // Start MainActivity with the username as the collectionPath
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("userDoc", username);
        startActivity(intent);
        finish(); // Finish the LoginActivity to prevent going back to it when pressing back in MainActivity
    }
}

