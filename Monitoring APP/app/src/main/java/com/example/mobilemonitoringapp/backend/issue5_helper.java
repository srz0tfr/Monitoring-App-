package com.example.mobilemonitoringapp.backend;

import static com.example.mobilemonitoringapp.backend.FileHelper.writeToFile;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public class issue5_helper {

    private final Context context;
    private static final String TAG = "PackageAnalyzer";
    private final List<String> applicationNames = new ArrayList<>();
    private List<String> resultsList;

    public issue5_helper(Context context) {
        this.context = context;
        getPackagesWithReadLogsPermission();
    }

    public void getPackagesWithReadLogsPermission() {
        List<String> result = new ArrayList<>();
        PackageManager pm = context.getPackageManager();

        try {
            List<PackageInfo> packages = pm.getInstalledPackages(PackageManager.GET_PERMISSIONS);
            for (PackageInfo packageInfo : packages) {
                if (packageInfo.requestedPermissions != null) {
                    for (String permission : packageInfo.requestedPermissions) {
                        if (permission.equals(android.Manifest.permission.READ_LOGS)) {
                            result.add("Package: " + packageInfo.packageName + "\n");
                            applicationNames.add(packageInfo.packageName);
                            Log.d(TAG, "Package with READ_LOGS: " + packageInfo.packageName);
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching packages", e);
        }
        resultsList = result;
        if(resultsList.isEmpty()){
            resultsList.add("No Packages Found with READ_LOGS permission");
        }
        writeToFile(this.context,resultsList,"issue5.txt");
    }

    public List<String> getApplicationNames() {
        return applicationNames;
    }

    public List<String> getResultsList() {
        return resultsList;
    }
}