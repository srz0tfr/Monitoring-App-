package com.example.mobilemonitoringapp.backend;

import static com.example.mobilemonitoringapp.backend.FileHelper.writeToFile;

import android.app.DownloadManager;
import android.database.Cursor;
import android.net.Uri;

import java.io.InputStream;

import android.content.Context;
import android.provider.OpenableColumns;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.*;
import okio.BufferedSink;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class ScanFile {

    private static final String API_UPLOAD_URL = "https://www.virustotal.com/api/v3/files";
    private static final String API_ANALYSIS_URL = "https://www.virustotal.com/api/v3/analyses/";
    private static final String API_FILE_DETAILS_URL = "https://www.virustotal.com/api/v3/files/";

    List<String> result = new ArrayList<>();

    public static String scanWithVirusTotal(Context context, Uri fileUri, String apiKey) {
        OkHttpClient client = new OkHttpClient();

        try {
      
            String fileName = getFileName(context, fileUri);
            InputStream inputStream = context.getContentResolver().openInputStream(fileUri);
            if (inputStream == null) return "Failed to open file.";

            
            RequestBody fileBody = new InputStreamRequestBody("application/octet-stream", inputStream);
            MultipartBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", fileName, fileBody)
                    .build();

            
            Request uploadRequest = new Request.Builder()
                    .url(API_UPLOAD_URL)
                    .post(requestBody)
                    .addHeader("x-apikey", apiKey)
                    .addHeader("accept", "application/json")
                    .build();

            Response uploadResponse = client.newCall(uploadRequest).execute();
            if (!uploadResponse.isSuccessful()) {
                return "Upload failed: " + uploadResponse.code();
            }

            String uploadResult = Objects.requireNonNull(uploadResponse.body()).string();
            JsonObject uploadJson = JsonParser.parseString(uploadResult).getAsJsonObject();
            String analysisId = uploadJson.getAsJsonObject("data").get("id").getAsString();

            
            String status = "queued";
            int maxRetries = 10;
            int sleepMs = 5000;
            JsonObject finalAttr = null;
            String finalAnalysisJsonStr = null;

            while (maxRetries-- > 0 && !"completed".equals(status)) {
                Request analysisRequest = new Request.Builder()
                        .url(API_ANALYSIS_URL + analysisId)
                        .get()
                        .addHeader("x-apikey", apiKey)
                        .build();

                Response analysisResponse = client.newCall(analysisRequest).execute();
                if (!analysisResponse.isSuccessful()) {
                    return "Scan result fetch failed: " + analysisResponse.code();
                }

                finalAnalysisJsonStr = Objects.requireNonNull(analysisResponse.body()).string();
                JsonObject analysisJson = JsonParser.parseString(finalAnalysisJsonStr).getAsJsonObject();
                finalAttr = analysisJson.getAsJsonObject("data").getAsJsonObject("attributes");
                status = finalAttr.get("status").getAsString();

                if ("completed".equals(status)) {
                    break;
                }

                
                Thread.sleep(sleepMs);
            }

            if (!"completed".equals(status)) {
                return "Analysis still not completed after waiting.";
            }

            
            Log.d("MyAppTag", "Final Analysis JSON:\n" + finalAnalysisJsonStr);

            
            StringBuilder analysisInfoBuilder = new StringBuilder("Analysis Info:\n");
            if (finalAttr.has("stats")) {
                JsonObject statsObj = finalAttr.getAsJsonObject("stats");
                int malicious = statsObj.get("malicious").getAsInt();
                int harmless = statsObj.get("harmless").getAsInt();
                int suspicious = statsObj.get("suspicious").getAsInt();
                int undetected = statsObj.get("undetected").getAsInt();

                analysisInfoBuilder.append("Malicious: ").append(malicious).append("\n")
                        .append("Harmless: ").append(harmless).append("\n")
                        .append("Suspicious: ").append(suspicious).append("\n")
                        .append("Undetected: ").append(undetected).append("\n");
            } else {
               
                analysisInfoBuilder.append("(No stats available)\n");
            }
            String analysisInfo = analysisInfoBuilder.toString();

           
            String sha256 = "";
            if (finalAttr.has("sha256")) {
                sha256 = finalAttr.get("sha256").getAsString();
            }
            if (sha256.isEmpty()) {
               
                JsonObject analysisJson = JsonParser.parseString(finalAnalysisJsonStr).getAsJsonObject();
                if (analysisJson.has("meta")) {
                    JsonObject metaObj = analysisJson.getAsJsonObject("meta");
                    if (metaObj.has("file_info")) {
                        JsonObject fileInfo = metaObj.getAsJsonObject("file_info");
                        if (fileInfo.has("sha256")) {
                            sha256 = fileInfo.get("sha256").getAsString();
                        }
                    }
                }
            }
            if (sha256.isEmpty()) {
                
                return analysisInfo + "\nNo SHA256 available, cannot fetch file details.";
            }

            
            Request fileDetailsRequest = new Request.Builder()
                    .url(API_FILE_DETAILS_URL + sha256)
                    .get()
                    .addHeader("x-apikey", apiKey)
                    .build();

            Response fileDetailsResponse = client.newCall(fileDetailsRequest).execute();
            if (!fileDetailsResponse.isSuccessful()) {
                return analysisInfo + "\nError fetching file details: " + fileDetailsResponse.code();
            }

            String fileDetailsJsonStr = Objects.requireNonNull(fileDetailsResponse.body()).string();
            JsonObject fileDetailsJson = JsonParser.parseString(fileDetailsJsonStr).getAsJsonObject();
            JsonObject fileData = fileDetailsJson.getAsJsonObject("data");
            JsonObject fileAttributes = fileData.getAsJsonObject("attributes");

            
            String md5 = fileAttributes.has("md5") ? fileAttributes.get("md5").getAsString() : "N/A";
            String sha1 = fileAttributes.has("sha1") ? fileAttributes.get("sha1").getAsString() : "N/A";
            String vhash = fileAttributes.has("vhash") ? fileAttributes.get("vhash").getAsString() : "N/A";
            String ssdeep = fileAttributes.has("ssdeep") ? fileAttributes.get("ssdeep").getAsString() : "N/A";
            String tlsh = fileAttributes.has("tlsh") ? fileAttributes.get("tlsh").getAsString() : "N/A";
            String fileType = fileAttributes.has("type_description") ? fileAttributes.get("type_description").getAsString() : "N/A";
            long fileSize = fileAttributes.has("size") ? fileAttributes.get("size").getAsLong() : 0;

            
            StringBuilder detailsBuilder = new StringBuilder("\nFile Details:\n");
            detailsBuilder.append("MD5: ").append(md5).append("\n")
                    .append("SHA-1: ").append(sha1).append("\n")
                    .append("SHA-256: ").append(sha256).append("\n")
                    .append("Vhash: ").append(vhash).append("\n")
                    .append("SSDEEP: ").append(ssdeep).append("\n")
                    .append("TLSH: ").append(tlsh).append("\n")
                    .append("File Type: ").append(fileType).append("\n")
                    .append("File Size: ").append(fileSize).append(" bytes\n");

            String fileDetails = detailsBuilder.toString();

           
            String finalOutput = analysisInfo + "\n" + fileDetails;
            writeToFile(context, finalOutput, "scanfileresult.txt");

            return finalOutput;

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    
    private static String getFileName(Context context, Uri uri) {
        String result = null;
        try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (columnIndex != -1) {
                    result = cursor.getString(columnIndex);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result != null ? result : "file";
    }

   
    static class InputStreamRequestBody extends RequestBody {
        private final String contentType;
        private final InputStream inputStream;

        public InputStreamRequestBody(String contentType, InputStream inputStream) {
            this.contentType = contentType;
            this.inputStream = inputStream;
        }

        @Nullable
        @Override
        public MediaType contentType() {
            return MediaType.parse(contentType);
        }

        @Override
        public void writeTo(BufferedSink sink) {
            try (InputStream is = inputStream) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    sink.write(buffer, 0, bytesRead);
                }
            } catch (Exception e) {
                throw new RuntimeException("Error writing request body", e);
            }
        }
    }
}