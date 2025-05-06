package com.example.mobilemonitoringapp.backend;

import static com.example.mobilemonitoringapp.backend.FileHelper.writeToFile;

import android.Manifest;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class issue10_helper {

    private final PackageManager packageManager;
    private final List<String> applicationsNames = new ArrayList<>();
    private Context context;
    private String resultsString;

    public issue10_helper(Context context) {
        this.context = context;
        this.packageManager = context.getPackageManager();
        checkAllAppsSecurityPolicy();
    }

    
    public static boolean hasInternetPermission(PackageManager pm, String packageName) {
        return pm.checkPermission(Manifest.permission.INTERNET, packageName)
                == PackageManager.PERMISSION_GRANTED;
    }



    
    public static boolean isUsingDefaultNetworkSecurityPolicy(ApplicationInfo appInfo) {
        try {
            Field field = ApplicationInfo.class.getField("networkSecurityConfigResId");
            int resId = field.getInt(appInfo);
          
            return resId == 1;
        } catch (NoSuchFieldException nsfe) {
            nsfe.printStackTrace();
            
            return false;
        } catch (IllegalAccessException iae) {
            iae.printStackTrace();
            return true;
        }
    }

    public void checkAllAppsSecurityPolicy() {
       
        List<ApplicationInfo> apps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        StringBuilder resultBuilder = new StringBuilder();

        for (ApplicationInfo appInfo : apps) {
            String packageName = appInfo.packageName;
            boolean hasInternet = issue10_helper.hasInternetPermission(packageManager, packageName);
            
            if (hasInternet) {
                
                boolean isDefaultNSP = issue10_helper.isUsingDefaultNetworkSecurityPolicy(appInfo);
                
                if (isDefaultNSP) {
                    resultBuilder.append("Package: ").append(packageName).append("\n");
                    resultBuilder.append("Internet Permission: Granted\n");
                    resultBuilder.append("Network Security Policy: Default (No custom NSP)\n\n");
                    applicationsNames.add(packageName);
                }
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            if(resultBuilder.isEmpty()){
                resultBuilder.append("No packages found with no NSP");
            }
        }

        resultsString = resultBuilder.toString();
        if(resultsString.isEmpty()){
            resultsString = "No Apps Found with internet permission and without NSP";
        }
        writeToFile(this.context,resultsString,"issue10.txt");
    }

    public List<String> getApplicationsNames () {
        return applicationsNames;
    }

    public String getResultsString() {
        return resultsString;
    }
}
