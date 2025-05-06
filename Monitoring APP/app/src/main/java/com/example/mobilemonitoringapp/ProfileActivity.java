package com.example.mobilemonitoringapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.EditText;
import android.widget.Toast;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.mobilemonitoringapp.backend.AuthDatabaseHelper;

public class ProfileActivity extends AppCompatActivity {

    private Button update;
    private Button back;
    private TextView user;
    private EditText oldPass;
    private EditText newPass;
    private AuthDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        String username = getIntent().getStringExtra("USERNAME");
        dbHelper = new AuthDatabaseHelper(this);

        update = findViewById(R.id.updateButton);
        back = findViewById(R.id.backButton);
        user = findViewById(R.id.userDisplay);
        oldPass = findViewById(R.id.oldPass);
        newPass = findViewById(R.id.newPass);

        user.setText(username);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePassword(username);
            }
        });

        back.setOnClickListener(view -> {
            Intent intent = new Intent(ProfileActivity.this, SideMenuActivity.class);
            startActivity(intent);
        });

    }

    private void changePassword(String username) {
        String oldPassInput = oldPass.getText().toString().trim();
        String newPassInput = newPass.getText().toString().trim();

        if (oldPassInput.isEmpty() || newPassInput.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_LONG).show();
            return;
        }

    
        if (dbHelper.authenticateUser(username, oldPassInput)) {
            
            if (dbHelper.changePassword(username, newPassInput)) {
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                intent.putExtra("UPDATE_SUCCESS", true);
                startActivity(intent);
            }
        } else {
            Toast.makeText(this, "Current password is incorrect. Please try again.", Toast.LENGTH_LONG).show();
        }
    }

}