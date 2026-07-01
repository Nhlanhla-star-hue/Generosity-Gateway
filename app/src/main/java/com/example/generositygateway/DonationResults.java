package com.example.generositygateway;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DonationResults extends AppCompatActivity {

    AlertDialog.Builder builder;
    Button btnmakedono;

    String donationURL = "https://lamp.ms.wits.ac.za/home/s2544615/transactions.php";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.donation_bio_dash);

        btnmakedono = findViewById(R.id.btn_make_dono);
        builder = new AlertDialog.Builder(DonationResults.this);

        // Initialize views
        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        TextView usernameTextView = findViewById(R.id.username_text_view);
        TextView bioTextView = findViewById(R.id.bio_text_view);
        TextView cellTextView = findViewById(R.id.cell_text_view);
        TextView emailTextView = findViewById(R.id.email_text_view);

        // Retrieve the intent extras
        Intent intent = getIntent();
        String recipientusername = intent.getStringExtra("RECIPIENT_USERNAME");
        String bio = intent.getStringExtra("BIO");
        String cell = intent.getStringExtra("CELL_NO");
        String email = intent.getStringExtra("EMAIL");
        String qty = intent.getStringExtra("QUANTITY");

        // Set the recipient details
        usernameTextView.setText(recipientusername+"'s request"+"("+qty+")");
        bioTextView.setText("Bio: \n"+bio);
        cellTextView.setText("Cellphone: "+cell);
        emailTextView.setText("Email: "+email);

        btnmakedono.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MakeDonation();
            }
        });
    }
    public void MakeDonation() {
        Log.d("DonatePage", "Preparing to show dialog");
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_input, null);

        EditText inputNumber = dialogView.findViewById(R.id.input_number);

        builder.setView(dialogView)
                .setTitle("How much would you like to donate?")
                .setCancelable(true)
                .setPositiveButton("Donate", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String donationAmount = inputNumber.getText().toString();

                        if (donationAmount.isEmpty() || donationAmount.equals("0")) {
                            Toast.makeText(DonationResults.this, "Please enter a valid amount", Toast.LENGTH_LONG).show();
                            return;
                        }

                        try {
                            Intent intent = getIntent();
                            String userid = intent.getStringExtra("USER_ID");
                            String donorQuantity = intent.getStringExtra("DONOR_QUANTITY");
                            String recipientQuantity = intent.getStringExtra("QUANTITY");
                            String donorusername = intent.getStringExtra("USERNAME");
                            String requestid = intent.getStringExtra("REQUEST_ID");
                            String selecteditem = intent.getStringExtra("SELECTED_ITEM");
                            String recipientusername = intent.getStringExtra("RECIPIENT_USERNAME");

                            if (donorQuantity == null || recipientQuantity == null) {
                                Toast.makeText(DonationResults.this, "Invalid quantity data. Please try again.", Toast.LENGTH_LONG).show();
                                return;
                            }

                            int donation = Integer.parseInt(donationAmount);
                            int donorQty = Integer.parseInt(donorQuantity);
                            int recipientQty = Integer.parseInt(recipientQuantity);

                            if (donation > donorQty) {
                                String message = "You can only donate " + donorQuantity + " items. Try editing your amount first";
                                Toast.makeText(DonationResults.this, message, Toast.LENGTH_LONG).show();
                            } else if (donation > recipientQty) {
                                String message = "The recipient only needs " + recipientQty + " items.";
                                Toast.makeText(DonationResults.this, message, Toast.LENGTH_LONG).show();
                            } else {

                                RequestBody requestBody = new FormBody.Builder()
                                        .add("request_id", requestid)
                                        .add("item_quantity", donationAmount)
                                        .add("donor_username", donorusername)
                                        .build();

                                Request request = new Request.Builder()
                                        .url(donationURL)
                                        .post(requestBody)
                                        .build();

                                OkHttpClient client = new OkHttpClient();
                                client.newCall(request).enqueue(new Callback() {
                                    @Override
                                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                        e.printStackTrace();
                                    }

                                    @Override
                                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                int newDonorQuantity = donorQty - donation;
                                                int newRecipientQuantity = recipientQty - donation;
                                                Log.d("DonationResults", "New Donor Quantity: " + newDonorQuantity);
                                                Log.d("DonationResults", "New Recipient Quantity: " + newRecipientQuantity);
                                                Toast.makeText(DonationResults.this, "Donation successful: You donated " + donationAmount + " items.", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(DonationResults.this, DonationMatches.class);
                                                intent.putExtra("NEW_DONOR_QUANTITY", String.valueOf(newDonorQuantity));
                                                intent.putExtra("NEW_QUANTITY", String.valueOf(newRecipientQuantity));
                                                intent.putExtra("USERNAME", donorusername);
                                                intent.putExtra("RECIPIENT_USERNAME",recipientusername);
                                                intent.putExtra("SELECTED_ITEM", selecteditem);
                                                intent.putExtra("USER_ID",userid);
                                                startActivity(intent);
                                                finish();
                                            }
                                        });
                                    }
                                });
                            }
                        } catch (NumberFormatException e) {
                            Toast.makeText(DonationResults.this, "Invalid quantity entered. Please try again.", Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(DonationResults.this, "An unexpected error occurred. Please try again.", Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
