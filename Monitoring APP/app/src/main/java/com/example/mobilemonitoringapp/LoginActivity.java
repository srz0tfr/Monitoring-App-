package com.example.mobilemonitoringapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mobilemonitoringapp.backend.AuthDatabaseHelper;

public class LoginActivity extends AppCompatActivity {

    private Button loginButton;
    private Button signUpButton;
    private Button forgotButton;
    private EditText usernameInput;
    private EditText passwordInput;

    private AuthDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        if (getIntent().getBooleanExtra("SIGNUP_SUCCESS", false)) {
            Toast.makeText(this, "Sign up successful. Please log in.", Toast.LENGTH_LONG).show();
        }

        if (getIntent().getBooleanExtra("LOGOUT_SUCCESS", false)) {
            Toast.makeText(this, "You have logged out.", Toast.LENGTH_LONG).show();
        }

        if (getIntent().getBooleanExtra("UPDATE_SUCCESS", false)) {
            Toast.makeText(this, "Password has been updated. Please log in again.", Toast.LENGTH_LONG).show();
        }

        dbHelper = new AuthDatabaseHelper(this);

        usernameInput = findViewById(R.id.usernameTextBox);
        passwordInput = findViewById(R.id.passwordTextBox);
        loginButton = findViewById(R.id.loginButton);
        signUpButton = findViewById(R.id.signUpButton);
        forgotButton = findViewById(R.id.forgotPassButton);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = usernameInput.getText().toString().trim();
                String password = passwordInput.getText().toString().trim();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please enter both username and password", Toast.LENGTH_LONG).show();
                    return;
                }

                if (dbHelper.authenticateUser(username, password)) {
                    
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("USERNAME", username);
                    startActivity(intent);
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid username or password", Toast.LENGTH_LONG).show();
                }
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

        forgotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ForgotpassActivity.class);
                startActivity(intent);
            }
        });

    }
}