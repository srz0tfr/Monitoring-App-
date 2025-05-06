package com.example.mobilemonitoringapp.backend;

import static com.example.mobilemonitoringapp.backend.FileHelper.writeToFile;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import java.util.ArrayList;
import java.util.List;
public class issue1_helper {
    private final PackageManager packageManager;

    private final List<String> applicationNames = new ArrayList<>();
    private List<String> resultsList;
    private Context context;

    public issue1_helper(Context context) {
        this.context = context;
        this.packageManager = context.getPackageManager();
        getPackagesAndServices("");
    }

    
    public void getPackagesAndServices(String packageName) {
        List<String> results = new ArrayList<>();

        try {
            if (packageName == null || packageName.isEmpty()) {
                
                List<PackageInfo> installedPackages = packageManager.getInstalledPackages(PackageManager.GET_SERVICES);
                for (PackageInfo pkg : installedPackages) {
                    if(!pkg.packageName.startsWith("com.google") &&
                            !pkg.packageName.startsWith("com.android") ) { 
                        results.addAll(getServicesForPackage(pkg));
                        applicationNames.add(pkg.packageName);
                    }
                }
            } else {
                
                PackageInfo pkg = packageManager.getPackageInfo(packageName, PackageManager.GET_SERVICES);
                results.addAll(getServicesForPackage(pkg));
            }
        } catch (PackageManager.NameNotFoundException e) {
            results.add("Package not found: " + packageName);
        }

        resultsList = results;
    }

   
    private List<String> getServicesForPackage(PackageInfo pkg) {
        List<String> servicesList = new ArrayList<>();
        servicesList.add("\n📦 Package: " + pkg.packageName);

        if (pkg.services != null) {
            for (ServiceInfo service : pkg.services) {
                servicesList.add("    ↳ Service: " + service.name);
            }
        } else {
            servicesList.add("    ↳ No services found.");
        }
        if(servicesList.isEmpty()){
            servicesList.add("No Services Found");
        }
        writeToFile(this.context,servicesList,"issue1.txt");
        return servicesList;
    }

    public List<String> getApplicationNames() {
        return applicationNames;
    }

    public List<String> getResultsList() {
        return resultsList;
    }
}
