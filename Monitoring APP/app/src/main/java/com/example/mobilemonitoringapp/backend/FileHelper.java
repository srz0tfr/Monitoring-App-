package com.example.mobilemonitoringapp.backend;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class FileHelper {
    
    public static void writeToFile(Context context, List<String> dataList, String FILE_NAME) {
        File file = new File(context.getFilesDir(), FILE_NAME);

        try (FileWriter writer = new FileWriter(file, false)) {
            for (String line : dataList) {
                writer.write(line + "\n"); 
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeToFile(Context context, String dataList, String FILE_NAME) {
        File file = new File(context.getFilesDir(), FILE_NAME);

        try (FileWriter writer = new FileWriter(file, false)) { 
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    public static String readFromFile(Context context, String FILE_NAME) {
        File file = new File(context.getFilesDir(), FILE_NAME);
        StringBuilder stringBuilder = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stringBuilder.toString();
    }
}
