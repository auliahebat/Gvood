package com.aulicious.gvood;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisActivity extends AppCompatActivity {

    private EditText etUsername, etEmail, etPassword, etConfirmPassword;
    private Button btnRegister;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regis);

        FirebaseApp.initializeApp(this);

        etUsername = findViewById(R.id.username);
        etEmail = findViewById(R.id.Email);
        etPassword = findViewById(R.id.Password);
        etConfirmPassword = findViewById(R.id.Confifrmpassword);
        btnRegister = findViewById(R.id.Registration);

//        database = FirebaseDatabase.getInstance().getReferenceFromUrl("https://gvood-project-firebase-default-rtdb.asia-southeast1.firebasedatabase.app/");

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = String.valueOf(etUsername.getText());
                String email = String.valueOf(etEmail.getText());
                String password = String.valueOf(etPassword.getText());
                String confirmPassword = String.valueOf(etConfirmPassword.getText());

                if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(RegisActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    Toast.makeText(RegisActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Perform registration logic here
                // For example, you can store the user data in a database or send it to a server
                database = FirebaseDatabase.getInstance().getReference("users");
                database.child(username).child("username").setValue(username);
                database.child(username).child("email").setValue(email);
                database.child(username).child("password").setValue(password);

                Toast.makeText(RegisActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();

                // Navigate to login activity
                Intent intent = new Intent(RegisActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}
