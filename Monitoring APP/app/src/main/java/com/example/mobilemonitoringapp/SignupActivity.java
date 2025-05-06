package com.example.mobilemonitoringapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.mobilemonitoringapp.backend.AuthDatabaseHelper;

public class SignupActivity extends AppCompatActivity {

    private Button signupButton;
    private Button cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        signupButton = findViewById(R.id.confirmSignUp);
        cancelButton = findViewById(R.id.cancelButton);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

    }

    private void registerUser() {
        EditText email = findViewById(R.id.emailBox);
        EditText passwordBox = findViewById(R.id.passBox);
        EditText passwordBox2 = findViewById(R.id.passBox2);

        String username = email.getText().toString().trim();
        String password = passwordBox.getText().toString().trim();
        String confirmPassword = passwordBox2.getText().toString().trim();

        
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_LONG).show();
            return;
        }

        
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_LONG).show();
            return;
        }

        AuthDatabaseHelper dbHelper = new AuthDatabaseHelper(this);
        boolean success = dbHelper.addUser(username, password);

        if (success) {
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            intent.putExtra("SIGNUP_SUCCESS", true);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Signup failed. Username may already exist.", Toast.LENGTH_LONG).show();
        }
    }

}