package com.example.generositygateway;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DonationMatches extends AppCompatActivity {

    private ListView resultsListView;
    private ArrayList<String> displayList;
    private Map<String, String> fullDetailsMap;
    private ArrayAdapter<String> adapter;
    TextView donationCount;
    Button btndone;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onResume() {
        super.onResume();
        setContentView(R.layout.matches_dash);

        donationCount = findViewById(R.id.donation_details);
        resultsListView = findViewById(R.id.results_list_view);
        btndone = findViewById(R.id.btndone);

        // Retrieve results from intent extras
        Intent intent = getIntent();
        String results = intent.getStringExtra("SELECTED_ITEM");
        String donorusername = intent.getStringExtra("USERNAME");
        String userid = intent.getStringExtra("USER_ID");
        String donorQuantity = intent.getStringExtra("NEW_DONOR_QUANTITY");

        if (donorQuantity == null) {
            donorQuantity = intent.getStringExtra("DONOR_QUANTITY");
        }
        else if(donorQuantity.equals("0")){
            donorusername = intent.getStringExtra("USERNAME");
            intent = new Intent(DonationMatches.this, DonorRecipient.class);
            intent.putExtra("USERNAME",donorusername);
            intent.putExtra("USER_ID",userid);
            startActivity(intent);
            Toast.makeText(DonationMatches.this, "You have 0 items to donate. Thank you for donating :)", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        String newRecipientQuantity = intent.getStringExtra("NEW_QUANTITY");
        String updatedUsername = intent.getStringExtra("RECIPIENT_USERNAME");

        if(newRecipientQuantity == null){
            newRecipientQuantity = intent.getStringExtra("QUANTITY");
        }

        Log.d("DonationMatches", "Donor Quantity: " + donorQuantity);
        Log.d("DonationMatches", "Recipient Quantity: " + newRecipientQuantity);
        Log.d("DonationMatches", "Updated Username: " + updatedUsername);

        donationCount.setText("You have " + donorQuantity + " items to donate");

        // Initialize lists and adapter
        displayList = new ArrayList<>();
        fullDetailsMap = new HashMap<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, displayList);
        resultsListView.setAdapter(adapter);

        // Parse and display results
        parseResults(results);

        // Update the specific recipient's quantity
        if (newRecipientQuantity != null && updatedUsername != null) {
            updateRecipientQuantity(updatedUsername, newRecipientQuantity);
        }

        // Set item click listener for the ListView
        String finalDonorQuantity = donorQuantity;
        String finalDonorusername = donorusername;
        resultsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String displayText = displayList.get(position);
                String recipientusername = displayText.split(",")[0].trim();
                String fullDetails = fullDetailsMap.get(recipientusername);

                if (fullDetails != null) {
                    String[] parts = fullDetails.split("\\|");
                    try {
                        String qty = parts[2].split(":")[1].trim();
                        String requestid = parts[0].split(":")[1].trim();
                        String bio = parts[3].split(":")[1].trim();
                        String cell = parts[4].split(":")[1].trim();
                        String email = parts[5].split(":")[1].trim();

                        // Start DonationResults
                        Intent intent = new Intent(DonationMatches.this, DonationResults.class);
                        intent.putExtra("RECIPIENT_USERNAME",recipientusername);
                        intent.putExtra("BIO", bio);
                        intent.putExtra("CELL_NO", cell);
                        intent.putExtra("EMAIL", email);
                        intent.putExtra("REQUEST_ID", requestid);
                        intent.putExtra("SELECTED_ITEM", results);
                        intent.putExtra("USERNAME", finalDonorusername);
                        intent.putExtra("QUANTITY",qty);
                        intent.putExtra("USER_ID",userid);
                        intent.putExtra("DONOR_QUANTITY", finalDonorQuantity);
                        startActivity(intent);
                    } catch (Exception e) {
                        Log.e("DonationMatches", "Error parsing data: " + fullDetails, e);
                    }
                }
            }
        });
        btndone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent done;
                done = new Intent(DonationMatches.this, DonorRecipient.class);
                done.putExtra("USERNAME", finalDonorusername);
                done.putExtra("USER_ID",userid);
                startActivity(done);
                Toast.makeText(DonationMatches.this,"Thank you for your donation.",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void parseResults(String results) {
        if (results == null || results.contains("No requests found")) {
            Toast.makeText(this, "No requests found for the specified item.", Toast.LENGTH_LONG).show();
        } else {
            String[] lines = results.split("\n");
            displayList.clear();
            for (String line : lines) {
                if (!line.trim().isEmpty()) {
                    String[] parts = line.split("\\|");
                    if (parts.length >= 6) {
                        String recipientusername = parts[1].split(":")[1].trim();
                        String quantity = parts[2].split(":")[1].trim();
                        if (!quantity.equals("0")) {
                            displayList.add(recipientusername + ", Quantity needed: " + quantity);
                            fullDetailsMap.put(recipientusername, line.trim());
                        }
                    } else {
                        Log.e("DonationMatches", "Invalid line format: " + line);
                    }
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    public void updateRecipientQuantity(String username, String newQuantity) {
        Log.d("DonationMatches", "Updating recipient quantity for: " + username + " to: " + newQuantity);
        for (int i = 0; i < displayList.size(); i++) {
            if (displayList.get(i).contains(username)) {
                if (newQuantity.equals("0")) {
                    displayList.remove(i);
                    fullDetailsMap.remove(username);
                } else {
                    displayList.set(i, username + ", Quantity needed: " + newQuantity);

                    String fullDetails = fullDetailsMap.get(username);
                    if(fullDetails!=null){
                        String[] parts = fullDetails.split("\\|");
                        parts[2] = "Quantity: "+newQuantity;
                        fullDetailsMap.put(username,String.join("|",parts));
                    }
                }
                break;
            }
        }
        Log.d("DonationMatches", "Display list after update: " + displayList);
        adapter.notifyDataSetChanged();
    }
}
