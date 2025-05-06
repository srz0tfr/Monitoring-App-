package com.example.mobilemonitoringapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

interface ExplanationCallback {
    void onComplete(String fullString);
    void onExplanationReceived(String explanation);

}

public class IssueItem {
    private final String issueName;
    private final List<String> applications;

    public IssueItem(String issueName, List<String> applications) {
        this.issueName = issueName;
        this.applications = applications;
    }

    public String getIssueName() {
        return this.issueName;
    }

    public void getApplicationString(ExplanationCallback callback) {
        StringBuilder applicationsAsString = new StringBuilder();
        AtomicInteger remainingRequests = new AtomicInteger(applications.size());

        for (String a : applications) {
            applicationsAsString.append(a).append("\n\n");

            getIssueExplanation(a, new ExplanationCallback() {
                @Override
                public void onComplete(String fullString) {

                }

                @Override
                public void onExplanationReceived(String explanation) {
                    synchronized (applicationsAsString) {
                        if (!explanation.trim().isEmpty()) {
                            applicationsAsString.append("Explanation: ").append(explanation);
                        }
                        applicationsAsString.append("\n\n---\n\n");

                        if (remainingRequests.decrementAndGet() == 0) {
                            callback.onComplete(applicationsAsString.toString());
                        }
                    }
                }
            });
        }
    }
    private void getIssueExplanation(String issue, ExplanationCallback callback) {
        new Thread(() -> {
            OkHttpClient client = new OkHttpClient();
            MediaType JSON_MEDIA = MediaType.get("application/json; charset=utf-8");

            JSONObject jsonPayload = new JSONObject();
            try {
                jsonPayload.put("model", "llama-3.3-70b-versatile");
                JSONArray messages = new JSONArray();
                JSONObject userMsg = new JSONObject();
                userMsg.put("role", "user");
                userMsg.put("content", "Explain this mobile app issue in simple terms: " + issue);
                messages.put(userMsg);
                jsonPayload.put("messages", messages);
            } catch (JSONException e) {
                e.printStackTrace();
                callback.onExplanationReceived("Error creating explanation request");
                return;
            }

            RequestBody body = RequestBody.create(jsonPayload.toString(), JSON_MEDIA);
            Request request = new Request.Builder()
                    .url("https://api.groq.com/openai/v1/chat/completions")
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Bearer ")//enter your LLM key here
                    .post(body)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    JSONObject jsonResponse = new JSONObject(responseData);
                    JSONArray choices = jsonResponse.getJSONArray("choices");
                    JSONObject firstChoice = choices.getJSONObject(0);
                    JSONObject messageObj = firstChoice.getJSONObject("message");
                    callback.onExplanationReceived(messageObj.getString("content"));
                } else {
                    callback.onExplanationReceived("Could not get explanation: " + response.message());
                }
            } catch (Exception e) {
                callback.onExplanationReceived(" ");
            }
        }).start();
    }
}
