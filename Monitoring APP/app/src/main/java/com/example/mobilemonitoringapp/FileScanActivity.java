package com.example.mobilemonitoringapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mobilemonitoringapp.backend.ScanFile;

public class FileScanActivity extends AppCompatActivity {

    private static final int PICK_FILE_REQUEST = 1;

    private TextView fileNameText;
    private Button pickFileButton, scanFileButton;

    private Uri selectedFileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_scan);

        fileNameText = findViewById(R.id.fileNameText);
        pickFileButton = findViewById(R.id.pickFileButton);
        scanFileButton = findViewById(R.id.scanFileButton);

        pickFileButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            startActivityForResult(Intent.createChooser(intent, "Select File"), PICK_FILE_REQUEST);
        });

        scanFileButton.setOnClickListener(v -> {
            if (selectedFileUri != null) {
                new Thread(() -> {
                    String result = ScanFile.scanWithVirusTotal(
                            FileScanActivity.this,
                            selectedFileUri,
                            ""//when you run enter api here for file scan
                    );
                    runOnUiThread(() -> {
                        fileNameText.setText(result);
                    });
                }).start();

            } else {
                Toast.makeText(this, "Please select a file first", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedFileUri = data.getData();
            fileNameText.setText("Selected: " + selectedFileUri.getLastPathSegment());
        }
    }
}
