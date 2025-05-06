package com.example.mobilemonitoringapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ContactSupportActivity extends AppCompatActivity {

    private EditText subjectInput, messageInput;
    private Button sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_support);

        subjectInput = findViewById(R.id.subjectInput);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendSupportButton);

        sendButton.setOnClickListener(v -> {
            String subject = subjectInput.getText().toString().trim();
            String message = messageInput.getText().toString().trim();

            if (subject.isEmpty() || message.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Message sent to support!", Toast.LENGTH_LONG).show();
                subjectInput.setText("");
                messageInput.setText("");
               
            }
        });
    }
}
