package com.example.mobilemonitoringapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.TextView;

public class SideMenuActivity extends AppCompatActivity {

    private Button homeButton;
    private Button scanButton;
    private Button profileButton;
    private Button settingsButton;
    private Button logOut;
    private TextView user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_side_menu);

        String username = getIntent().getStringExtra("USERNAME");

        homeButton = findViewById(R.id.homeButton);
        profileButton = findViewById(R.id.profileButton);
        scanButton = findViewById(R.id.fileScanButton);
        settingsButton = findViewById(R.id.settingsButton);
        logOut = findViewById(R.id.logOut);
        user = findViewById(R.id.usernameTitle);

        user.setText("Hello, " + username);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SideMenuActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SideMenuActivity.this, FileScanActivity.class);
                startActivity(intent);
            }
        });

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SideMenuActivity.this, ProfileActivity.class);
                intent.putExtra("USERNAME", username);
                startActivity(intent);
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SideMenuActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SideMenuActivity.this, LoginActivity.class);
                intent.putExtra("LOGOUT_SUCCESS", true);
                startActivity(intent);
            }
        });

    }
}