package com.example.mobilemonitoringapp.backend;


import static com.example.mobilemonitoringapp.backend.FileHelper.writeToFile;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;


public class issue7_helper {

    private final Context context;
    private final List<String> applicationNames = new ArrayList<>();
    private String resultsString;

    public issue7_helper(Context context) {
        this.context = context;
        checkSystemAppsForOverlayPermission();
    }

    public static boolean hasHideOverlayPermission(PackageManager pm, String packageName) {
        try {
            PackageInfo packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
            if (packageInfo.requestedPermissions != null) {
                for (String permission : packageInfo.requestedPermissions) {
                    if ("android.permission.HIDE_OVERLAY_WINDOWS".equals(permission)) {
                        return true;
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void checkSystemAppsForOverlayPermission() {
        PackageManager pm = context.getPackageManager();
        
        List<ApplicationInfo> apps = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        StringBuilder resultBuilder = new StringBuilder();

        for (ApplicationInfo appInfo : apps) {
           
            if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                String packageName = appInfo.packageName;
                boolean hasOverlay = issue7_helper.hasHideOverlayPermission(pm, packageName);
                if (hasOverlay) {
                    resultBuilder.append("System Package: ").append(packageName).append("\n");
                    resultBuilder.append("App has overlay permission \n\n");
                    applicationNames.add(packageName);
                }
            }
        }

        resultsString =  resultBuilder.toString();
        if(resultsString.isEmpty()){
            resultsString = "No System apps Found with overlay permissions";
        }
        writeToFile(this.context,resultsString,"issue7.txt");
    }

    public List<String> getApplicationNames() {
        return applicationNames;
    }

    public String getResultsString() {
        return resultsString;
    }
}
