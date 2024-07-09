package com.aulicious.gvood;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisActivity extends AppCompatActivity {

    private EditText etUsername, etEmail, etPassword, etConfirmPassword;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regis);

        etUsername = findViewById(R.id.username);
        etEmail = findViewById(R.id.Email);
        etPassword = findViewById(R.id.Password);
        etConfirmPassword = findViewById(R.id.Confifrmpassword);
        btnRegister = findViewById(R.id.Registration);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString();;
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                String confirmPassword = etConfirmPassword.getText().toString();

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

                Toast.makeText(RegisActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();

                // Navigate to login activity
                Intent intent = new Intent(RegisActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}
