package com.example.generositygateway;
import android.annotation.SuppressLint;
import android.app.Activity;
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

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginPage extends Activity{

    EditText emailEdittext,passowrdEditText;
    Button btnlogin;
    String loginURL = "https://lamp.ms.wits.ac.za/home/s2544615/new_login.php";
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        emailEdittext = findViewById(R.id.email);
        passowrdEditText = findViewById(R.id.password);
        btnlogin = (Button) findViewById(R.id.buttonLogin);

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String logemail = emailEdittext.getText().toString();
                String logpass = passowrdEditText.getText().toString();

                if(logemail.isEmpty() || logpass.isEmpty()){
                    Toast.makeText(LoginPage.this,"Please fill in all fields", Toast.LENGTH_SHORT).show();
                }
                else{
                    LoginRequest(logemail,logpass);
                }
            }
        });
    }

    public void LoginRequest(String logemail, String logpass){
        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new FormBody.Builder()
                .add("EMAIL", logemail)
                .add("PASSWORD", logpass)
                .build();

        Request request = new Request.Builder()
                .url(loginURL)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LoginPage.this,"Network error: " + e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseData = response.body().string();
                Log.d("LoginPage", "Response: " + responseData);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(responseData.trim().startsWith("Login Successful|")){
                            String[] parts = responseData.split("\\|");
                            if(parts.length>1) {
                                String username = parts[1].trim();
                                String userid = parts[2].trim();

                                SharedPreferences sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("USERNAME", username);
                                editor.putString("USER_ID",userid);
                                editor.apply();


                                Intent intent = new Intent(LoginPage.this, DonorRecipient.class);
                                intent.putExtra("USERNAME", username);
                                intent.putExtra("USER_ID",userid);
                                Toast.makeText(LoginPage.this, "Welcome back " + username, Toast.LENGTH_SHORT).show();
                                startActivity(intent);
                            }
                            else {
                                Toast.makeText(LoginPage.this, "Login successful, but username not found in response", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(LoginPage.this, responseData, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

}

