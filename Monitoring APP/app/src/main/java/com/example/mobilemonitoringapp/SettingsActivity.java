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
import android.widget.Switch;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    private Button back;
    private Switch aiSwitch;
    private Switch notif;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

        back = findViewById(R.id.backButton2);
        aiSwitch = findViewById(R.id.aiSwitch);
        notif = findViewById(R.id.notifSwitch);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this, SideMenuActivity.class);
                startActivity(intent);
            }
        });

        aiSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String message = isChecked ? "AI Assistant Enabled" : "AI Assistant Disabled";
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        });

        notif.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String message = isChecked ? "Notifications Enabled" : "Notifications Disabled";
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        });
    }
}