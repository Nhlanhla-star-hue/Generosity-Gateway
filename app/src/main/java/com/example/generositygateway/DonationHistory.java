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

public class DonationHistory extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DonationAdapter donationAdapter;
    private List<DonationEntry> donationList;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onResume() {
        super.onResume();
        setContentView(R.layout.donation_history);

        recyclerView = findViewById(R.id.recycler_view_donations);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        donationList = new ArrayList<>();
        donationAdapter = new DonationAdapter(donationList);
        recyclerView.setAdapter(donationAdapter);

        fetchDataFromServer();
    }

    private void fetchDataFromServer() {
        OkHttpClient client = new OkHttpClient();
        String donorId = getIntent().getStringExtra("USER_ID");

        if (donorId == null || donorId.isEmpty()) {
            Log.e("DonationHistory", "User ID is null or empty");
            runOnUiThread(() -> Toast.makeText(DonationHistory.this, "User ID is missing", Toast.LENGTH_SHORT).show());
            return;
        }

        Log.d("DonationHistory", "Fetching data for donorId: " + donorId);
        String historyURL = "https://lamp.ms.wits.ac.za/home/s2544615/donationhistory_table.php?DONOR_ID=" + donorId;

        Request request = new Request.Builder()
                .url(historyURL)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("DonationHistory", "Network error: " + e.getMessage());
                runOnUiThread(() -> Toast.makeText(DonationHistory.this, "Network error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    Log.d("DonationHistory", "Response data: " + responseData);
                    parseDonationData(responseData);
                } else {
                    Log.e("DonationHistory", "Response not successful: " + response.code());
                    runOnUiThread(() -> Toast.makeText(DonationHistory.this, "Failed to fetch donation data", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void parseDonationData(String responseData) {
        try {
            JSONArray jsonArray = new JSONArray(responseData);
            List<DonationEntry> newDonationList = new ArrayList<>();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String item = jsonObject.getString("ITEM");
                int donatedQuantity = jsonObject.getInt("ITEM_RECEIVED_QUANTITY");
                String recipientName = jsonObject.getString("RECIPIENT_NAME");
                newDonationList.add(new DonationEntry(item, donatedQuantity, recipientName));
            }

            runOnUiThread(() -> {
                donationList.clear();
                donationList.addAll(newDonationList);
                donationAdapter.notifyDataSetChanged();
            });

        } catch (JSONException e) {
            Log.e("DonationHistory", "Error parsing donation data", e);
            runOnUiThread(() -> Toast.makeText(DonationHistory.this, "Error parsing donation data", Toast.LENGTH_SHORT).show());
        }
    }
}
