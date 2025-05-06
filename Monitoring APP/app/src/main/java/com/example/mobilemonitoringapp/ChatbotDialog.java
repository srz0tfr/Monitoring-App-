package com.example.mobilemonitoringapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.widget.NestedScrollView;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class ChatbotDialog extends Dialog {
    private EditText userInput;
    private LinearLayout chatContainer;
    private Button sendButton;
    private NestedScrollView chatScrollView;

    public ChatbotDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_chatbot);

        if (getWindow() != null) {
            getWindow().setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT
            );
        }

        userInput = findViewById(R.id.chatInput);
        chatContainer = findViewById(R.id.chatContainer);
        sendButton = findViewById(R.id.sendButton);
        chatScrollView = findViewById(R.id.chatScrollView);

        
        addMessage("Welcome! How can I help you today?", false);

        sendButton.setOnClickListener(view -> {
            String input = userInput.getText().toString().trim();
            if (!input.isEmpty()) {
                
                addMessage(input, true);
               
                callGroqAPI(input);
                userInput.setText("");
            }
        });
    }

    private void addMessage(String message, boolean isUser) {
        View messageView = LayoutInflater.from(getContext()).inflate(R.layout.message_bubble, chatContainer, false);
        TextView messageText = messageView.findViewById(R.id.messageText);

        messageText.setText(message);
       
        messageText.setBackgroundResource(isUser ?
                R.drawable.user_message_bubble :
                R.drawable.bot_message_bubble);


        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) messageText.getLayoutParams();
        params.gravity = isUser ? Gravity.END : Gravity.START;
        messageText.setLayoutParams(params);

        chatContainer.addView(messageView);
        chatScrollView.post(() -> chatScrollView.fullScroll(View.FOCUS_DOWN));
    }

    private void callGroqAPI(String userMessage) {
        OkHttpClient client = new OkHttpClient();
        MediaType JSON_MEDIA = MediaType.get("application/json; charset=utf-8");

        JSONObject jsonPayload = new JSONObject();
        try {
            jsonPayload.put("model", "llama-3.3-70b-versatile");
            JSONArray messages = new JSONArray();
            JSONObject userMsg = new JSONObject();
            userMsg.put("role", "user");
            userMsg.put("content", userMessage);
            messages.put(userMsg);
            jsonPayload.put("messages", messages);
        } catch (JSONException e) {
            e.printStackTrace();
            runOnUiThreadSafe(() ->
                    addMessage("Error creating JSON payload: " + e.getMessage(), false));
            return;
        }

        RequestBody body = RequestBody.create(jsonPayload.toString(), JSON_MEDIA);
        Request request = new Request.Builder()
                .url("https://api.groq.com/openai/v1/chat/completions")
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer ")//enter your LLM key here
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThreadSafe(() ->
                        addMessage("Network Error: " + e.getMessage(), false));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();

                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonResponse = new JSONObject(responseData);
                        JSONArray choices = jsonResponse.getJSONArray("choices");
                        JSONObject firstChoice = choices.getJSONObject(0);
                        JSONObject messageObj = firstChoice.getJSONObject("message");
                        String botResponse = messageObj.getString("content");

                        runOnUiThreadSafe(() ->
                                addMessage(botResponse, false));

                    } catch (JSONException e) {
                        e.printStackTrace();
                        runOnUiThreadSafe(() ->
                                addMessage("JSON Parsing Error: " + e.getMessage(), false));
                    }
                } else {
                    runOnUiThreadSafe(() ->
                            addMessage("API Error: " + response.message(), false));
                }
            }
        });
    }

    private void runOnUiThreadSafe(Runnable runnable) {
        try {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                
                runnable.run();
            } else {
                
                new Handler(Looper.getMainLooper()).post(runnable);
            }
        } catch (Exception e) {
            Log.e("Chatbot", "Thread handling error", e);
        }
    }
}
