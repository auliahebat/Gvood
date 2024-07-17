package com.aulicious.gvood;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisActivity extends AppCompatActivity {

    private EditText etUsername, etEmail, etPhone, etPassword, etConfirmPassword;
    private Button btnRegister;
    private DatabaseReference database;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regis);

        FirebaseApp.initializeApp(this);

        etUsername = findViewById(R.id.Username);
        etEmail = findViewById(R.id.Email);
        etPhone = findViewById(R.id.Phone);
        etPassword = findViewById(R.id.Password);
        etConfirmPassword = findViewById(R.id.Confifrmpassword);
        btnRegister = findViewById(R.id.Registration);

        mAuth = FirebaseAuth.getInstance();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString();
                String email = etEmail.getText().toString();
                String phone = etPhone.getText().toString();
                String password = etPassword.getText().toString();
                String confirmPassword = etConfirmPassword.getText().toString();

                if (username.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(RegisActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    Toast.makeText(RegisActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create a new user with Firebase Authentication
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(RegisActivity.this, task -> {
                            if (task.isSuccessful()) {
                                // Registration successful, save user data to Firebase Realtime Database
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    database = FirebaseDatabase.getInstance().getReference("users");
                                    database.child(user.getUid()).child("username").setValue(username);
                                    database.child(user.getUid()).child("email").setValue(email);
                                    database.child(user.getUid()).child("phone").setValue(phone);
                                    database.child(user.getUid()).child("photo");

                                    Toast.makeText(RegisActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();

                                    // Navigate to login activity
                                    Intent intent = new Intent(RegisActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    finish(); // Close the registration activity
                                }
                            } else {
                                // If registration fails, display a message to the user.
                                Toast.makeText(RegisActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}
