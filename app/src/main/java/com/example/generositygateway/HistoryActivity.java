package com.example.generositygateway;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecipientAdapter recipientAdapter;
    private List<RecipientEntry> recipientList;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onResume() {
        super.onResume();
        setContentView(R.layout.request_history);

        recyclerView = findViewById(R.id.recycler_view_request);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recipientList = new ArrayList<>();
        recipientAdapter = new RecipientAdapter(recipientList);
        recyclerView.setAdapter(recipientAdapter);

        fetchRecipientHistory();
    }

    private void fetchRecipientHistory() {
        OkHttpClient client = new OkHttpClient();
        String recipientUsername = getIntent().getStringExtra("USERNAME");

        if (recipientUsername == null || recipientUsername.isEmpty()) {
            Log.e("HistoryActivity", "Recipient username is null or empty");
            runOnUiThread(() -> Toast.makeText(HistoryActivity.this, "Recipient username is missing", Toast.LENGTH_SHORT).show());
            return;
        }

        Log.d("HistoryActivity", "Fetching data for recipientUsername: " + recipientUsername);
        String historyURL = "https://lamp.ms.wits.ac.za/home/s2544615/recipienthist.php?RECIPIENT_USERNAME=" + recipientUsername;

        Request request = new Request.Builder()
                .url(historyURL)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("HistoryActivity", "Network error: " + e.getMessage());
                runOnUiThread(() -> Toast.makeText(HistoryActivity.this, "Network error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    Log.d("HistoryActivity", "Response data: " + responseData);
                    parseRecipientData(responseData);
                } else {
                    Log.e("HistoryActivity", "Response not successful: " + response.code());
                    runOnUiThread(() -> Toast.makeText(HistoryActivity.this, "Failed to fetch recipient data", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void parseRecipientData(String responseData) {
        try {
            JSONArray jsonArray = new JSONArray(responseData);
            List<RecipientEntry> newRecipientList = new ArrayList<>();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String item = jsonObject.getString("item");
                int initialQuantity = jsonObject.getInt("initial_quantity");
                int receivedQuantity = jsonObject.getInt("received_quantity");
                String donorUsername = jsonObject.getString("donor_username");
                newRecipientList.add(new RecipientEntry(item, initialQuantity, receivedQuantity, donorUsername));
            }

            runOnUiThread(() -> {
                recipientList.clear();
                recipientList.addAll(newRecipientList);
                recipientAdapter.notifyDataSetChanged();
            });

        } catch (JSONException e) {
            Log.e("HistoryActivity", "Error parsing recipient data", e);
            runOnUiThread(() -> Toast.makeText(HistoryActivity.this, "Error parsing recipient data", Toast.LENGTH_SHORT).show());
        }
    }
}
