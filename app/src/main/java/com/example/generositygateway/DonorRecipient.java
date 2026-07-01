package com.example.generositygateway;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DonorRecipient extends AppCompatActivity {

    private Button btndonate;
    private Button btnreceive;
    private Button btnlogout;
    private RecyclerView recyclerView;
    private RankingAdapter rankingAdapter;
    AlertDialog.Builder builder;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String username;
    private String userid;
    private ImageButton btnshare;
    private final String bioCheckURL = "https://lamp.ms.wits.ac.za/home/s2544615/getbio.php";
    private final String rankingURL = "https://lamp.ms.wits.ac.za/home/s2544615/leaderboard.php";
    private final String positionURL = "https://lamp.ms.wits.ac.za/home/s2544615/position_share.php";
    private boolean doubleBackToExitPressedOnce = false;
    private final Handler handler = new Handler();

    private final Runnable resetDoubleBackToExit = new Runnable() {
        @Override
        public void run() {
            doubleBackToExitPressedOnce = false;
        }
    };

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            finishAffinity();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();

        handler.postDelayed(resetDoubleBackToExit, 2000);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_as);

        btndonate = findViewById(R.id.btn_Donate);
        btnreceive = findViewById(R.id.btn_Receive);
        btnlogout = findViewById(R.id.btn_logout);
        btnshare = findViewById(R.id.btn_share);
        recyclerView = findViewById(R.id.recycler_view_ranking);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        rankingAdapter = new RankingAdapter(new ArrayList<>());
        recyclerView.setAdapter(rankingAdapter);

        swipeRefreshLayout=findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchRankingData();
            }
        });

        builder = new AlertDialog.Builder(this);

        Intent intent = getIntent();
        username = intent.getStringExtra("USERNAME");
        userid = intent.getStringExtra("USER_ID");

        btndonate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DonorRecipient.this, Donor.class);
                intent.putExtra("USERNAME", username);
                intent.putExtra("USER_ID",userid);
                startActivity(intent);
            }
        });

        btnshare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareUserRank();
            }
        });

        btnreceive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBio(username);
            }
        });

        btnlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                builder.setTitle("Confirm Logout")
                        .setMessage("Are you sure you want to logout?")
                        .setCancelable(true)
                        .setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(DonorRecipient.this, "You have been logged out.", Toast.LENGTH_LONG).show();
                                logout();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        })
                        .show();
            }
        });

        fetchRankingData();
    }

    public void logout(){
        SharedPreferences preferences = getSharedPreferences("user_session", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(DonorRecipient.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(resetDoubleBackToExit);
    }

    private void checkBio(String username) {
        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new FormBody.Builder()
                .add("USERNAME", username)
                .build();

        Request request = new Request.Builder()
                .url(bioCheckURL)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("DonorRecipient","Network error " + e.getMessage(),e);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(DonorRecipient.this, "Network error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    String responseData = response.body().string();
                    Log.d("DonorRecipient", "Response: " + responseData);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(DonorRecipient.this, BioPage.class);
                            intent.putExtra("USERNAME", username);
                            intent.putExtra("BIO", responseData);
                            intent.putExtra("USER_ID",userid);
                            startActivity(intent);
                        }
                    });
            }
        });
    }



    private void fetchRankingData() {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(rankingURL)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("DonorRecipient", "Network error " + e.getMessage(), e);
                runOnUiThread(() -> Toast.makeText(DonorRecipient.this, "Network error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    runOnUiThread(() -> parseAndDisplayRankingData(responseData));
                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    runOnUiThread(() -> Toast.makeText(DonorRecipient.this, "Failed to fetch ranking data", Toast.LENGTH_SHORT).show());
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    private void parseAndDisplayRankingData(String responseData) {
        try {
            JSONArray jsonArray = new JSONArray(responseData);
            List<RankingEntry> rankingList = new ArrayList<>();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String rank = jsonObject.getString("rank");
                String username = jsonObject.getString("username");
                String totalQuantity = jsonObject.getString("totalQuantity");
                rankingList.add(new RankingEntry(rank, username, totalQuantity));
            }

            rankingAdapter = new RankingAdapter(rankingList);
            recyclerView.setAdapter(rankingAdapter);

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error parsing ranking data", Toast.LENGTH_SHORT).show();
        }
    }

    private void shareUserRank() {
        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new FormBody.Builder()
                .add("USERNAME", username)
                .build();

        Request request = new Request.Builder()
                .url(positionURL)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> Toast.makeText(DonorRecipient.this, "Network error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    runOnUiThread(() -> handleShareResponse(responseData));
                } else {
                    runOnUiThread(() -> Toast.makeText(DonorRecipient.this, "Failed to fetch user rank", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void handleShareResponse(String responseData) {
        try {
            JSONObject jsonObject = new JSONObject(responseData);

            // Extracting user_info from the response
            if (jsonObject.has("rank")) {
                String rank = jsonObject.getString("rank");
                String totalQuantity = jsonObject.getString("totalQuantity");

                String shareText = "My rank is " + rank + " with a total donation of " + totalQuantity + " items.";
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
                shareIntent.setType("text/plain");
                startActivity(Intent.createChooser(shareIntent, "Share via"));
            } else {
                Toast.makeText(this, "User not found in the leaderboard", Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error parsing ranking data", Toast.LENGTH_SHORT).show();
        }
    }
}
