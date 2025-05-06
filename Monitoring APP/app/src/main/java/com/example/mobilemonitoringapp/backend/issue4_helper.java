package com.example.mobilemonitoringapp.backend;

import static com.example.mobilemonitoringapp.backend.FileHelper.writeToFile;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public class issue4_helper {

    private final Context context;
    private static final String TAG = "PackageAnalyzer";
    private final List<String> applicationNames = new ArrayList<>();
    private List<String> resultsList;

    public issue4_helper(Context context) {
        this.context = context;
        getPackagesWithUnprotectedProviders();
    }

    public void getPackagesWithUnprotectedProviders() {
        List<String> result = new ArrayList<>();
        PackageManager pm = context.getPackageManager();

        try {
            List<PackageInfo> packages = pm.getInstalledPackages(PackageManager.GET_PROVIDERS);
            for (PackageInfo packageInfo : packages) {
                if (packageInfo.providers != null) {
                    for (ProviderInfo provider : packageInfo.providers) {
                        if (provider != null && (provider.pathPermissions == null)) {
                            result.add("Package: " + packageInfo.packageName +
                                    "\nProvider: " + provider.authority +
                                    "\nPermission: None\n");
                            applicationNames.add(packageInfo.packageName);
                            Log.d(TAG, "Unprotected Provider Found: " + provider.authority);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching providers", e);
        }
        resultsList =  result;
        if(resultsList.isEmpty()){
            resultsList.add("No Packages Found with unprotected Content Providers");
        }
        writeToFile(this.context,resultsList,"issue4.txt");
    }

    public List<String> getApplicationNames() {
        return applicationNames;
    }

    public List<String> getResultsList() {
        return resultsList;
    }
}