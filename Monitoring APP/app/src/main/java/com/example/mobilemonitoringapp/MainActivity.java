package com.example.mobilemonitoringapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.widget.Button;
import android.widget.TextView;

import com.example.mobilemonitoringapp.backend.issue10_helper;
import com.example.mobilemonitoringapp.backend.issue1_helper;
import com.example.mobilemonitoringapp.backend.issue4_helper;
import com.example.mobilemonitoringapp.backend.issue5_helper;
import com.example.mobilemonitoringapp.backend.issue7_helper;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView deviceInfo;
    private Speedometer speedometer;
    private Button scanButton;
    private Button chatbotButton;
    private Button menuButton;
    private RecyclerView issuesRecyclerView;

    private static final String phoneManufacturer = Build.MANUFACTURER;
    private static final String phoneModel = Build.MODEL;
    private static final String osRelease = Build.VERSION.RELEASE;
    private static final int osSDK = Build.VERSION.SDK_INT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String username = getIntent().getStringExtra("USERNAME");

        deviceInfo = findViewById(R.id.deviceInfo);
        speedometer = findViewById(R.id.speedometer);
        scanButton = findViewById(R.id.scanButton);
        chatbotButton = findViewById(R.id.chatbotButton);
        menuButton = findViewById(R.id.menuButton);
        issuesRecyclerView = findViewById(R.id.issuesRecyclerView);

        
        String deviceInfoString = "Device Model: " + phoneManufacturer + " " + phoneModel + "\n" +
                "Operating System: Android " + osRelease + " SDK " + osSDK;
        SpannableString styledInfo = new SpannableString(deviceInfoString);

        int startModel = deviceInfoString.indexOf("Device Model:");
        int endModel = startModel + "Device Model:".length();
        int startOS = deviceInfoString.indexOf("Operating System:");
        int endOS = startOS + "Operating System:".length();

        styledInfo.setSpan(new StyleSpan(Typeface.BOLD), startModel, endModel, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        styledInfo.setSpan(new StyleSpan(Typeface.BOLD), startOS, endOS, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        deviceInfo.setText(styledInfo);

        
        scanButton.setOnClickListener(view -> {
                onClickScanButton();
        });

        
        chatbotButton.setOnClickListener(view -> {
            ChatbotDialog dialog = new ChatbotDialog(MainActivity.this);
            dialog.show();
        });

       
        menuButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, SideMenuActivity.class);
            intent.putExtra("USERNAME", username);
            startActivity(intent);
        });
    }

    public void onClickScanButton() {
        List<IssueItem> identifiedIssues = new ArrayList<>();

        issue1_helper issue1 = new issue1_helper(this);
        issue4_helper issue4 = new issue4_helper(this);
        issue5_helper issue5 = new issue5_helper(this);
        issue7_helper issue7 = new issue7_helper(this);
        issue10_helper issue10 = new issue10_helper(this);

        List<String> issue1AppNames = issue1.getApplicationNames();
        List<String> issue4AppNames = issue4.getApplicationNames();
        List<String> issue5AppNames = issue5.getApplicationNames();
        List<String> issue7AppNames = issue7.getApplicationNames();
        List<String> issue10AppNames = issue10.getApplicationsNames();

        if (!issue1AppNames.isEmpty()) {
            identifiedIssues.add(new IssueItem("App running background services.", issue1AppNames));
        }

        if (!issue4AppNames.isEmpty()) {
            identifiedIssues.add(new IssueItem("App contains unprotected package providers.", issue4AppNames));
        }

        if (!issue5AppNames.isEmpty()) {
            identifiedIssues.add(new IssueItem("App contains packages with vulnerable log permissions.", issue5AppNames));
        }

        if (!issue7AppNames.isEmpty()) {
            identifiedIssues.add(new IssueItem("App has hidden overlay permission enabled.", issue7AppNames));
        }

        if (!issue10AppNames.isEmpty()) {
            identifiedIssues.add(new IssueItem("App is configured with default network security policy.", issue10AppNames));
        }

        renderIssueList(identifiedIssues);
        speedometer.redrawLine(identifiedIssues.size() * 20);
    }

    public void renderIssueList(List<IssueItem> issues) {
        issuesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        issuesRecyclerView.setAdapter(new IssueAdapter(issues));
    }
}