package com.example.generositygateway;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BioPage extends AppCompatActivity {

    Button btnupdate;
    Button btnhistory;
    EditText bioEditText;
    String username;
    String userid;
    String bio;
    String updatebioURL = "https://lamp.ms.wits.ac.za/home/s2544615/update_bio.php";
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bio_dashboard);

        btnupdate = findViewById(R.id.btn_Update);
        btnhistory = findViewById(R.id.btn_rec_history);
        bioEditText = findViewById(R.id.bio);

        Intent intent = getIntent();
        username = intent.getStringExtra("USERNAME");
        bio = intent.getStringExtra("BIO");
        userid = intent.getStringExtra("USER_ID");

        if(bio.equals("The recipient has no biography")){
            bioEditText.setText("");
        }
        else{
            bioEditText.setText(bio);
        }


        btnupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                String bio = bioEditText.getText().toString();

                if (bio.isEmpty()) {
                    Toast.makeText(BioPage.this, "Please enter your bio", Toast.LENGTH_SHORT).show();
                } else {
                    submitBio(bio);
                }
            }
        });

        btnhistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BioPage.this, HistoryActivity.class);
                intent.putExtra("USERNAME", username);
                startActivity(intent);
            }
        });
    }

    private void submitBio(String bio) {
        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new FormBody.Builder()
                .add("USERNAME", username)
                .add("BIO", bio)
                .build();

        Request request = new Request.Builder()
                .url(updatebioURL)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(BioPage.this, "Network error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseData = response.body().string();
                Log.d("BioPage", "Response: " + responseData);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (responseData.trim().equals("Bio updated successfully")){
                            Toast.makeText(BioPage.this, responseData,Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(BioPage.this, Recipient.class);
                            intent.putExtra("USERNAME", username);
                            intent.putExtra("USER_ID",userid);
                             startActivity(intent);
                             finish();
                        }
                        else if(responseData.trim().equals("Bio inserted successfully")){
                            Toast.makeText(BioPage.this, responseData,Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(BioPage.this, Recipient.class);
                            intent.putExtra("USERNAME", username);
                            intent.putExtra("USER_ID",userid);
                            startActivity(intent);
                        }
                        else{
                            Toast.makeText(BioPage.this, responseData,Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}
