package com.example.generositygateway;
import static android.app.ProgressDialog.show;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
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

public class SignUpPage extends Activity{
    EditText usernameEditText,emailEditText,phoneEditText,password1EditText,password2EditText;
    Button btndone;
    String sendURL = "https://lamp.ms.wits.ac.za/home/s2544615/new_user.php";
    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_page);

        btndone = findViewById(R.id.Btn_Done);
        usernameEditText = findViewById(R.id.userinp);
        emailEditText = findViewById(R.id.emailinp);
        phoneEditText = findViewById(R.id.phoneinp);
        password1EditText = findViewById(R.id.passinp1);
        password2EditText = findViewById(R.id.passinp2);

        btndone.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String username = usernameEditText.getText().toString();
                String email = emailEditText.getText().toString();
                String phone = phoneEditText.getText().toString();
                String password1 = password1EditText.getText().toString();
                String password2 = password2EditText.getText().toString();

                if(username.isEmpty() || email.isEmpty() || phone.isEmpty() || password1.isEmpty() || password2.isEmpty()){
                    Toast.makeText(SignUpPage.this,"Please fill in all fields", Toast.LENGTH_SHORT).show();
                }
                else if (password1.length() < 7) {
                    Toast.makeText(SignUpPage.this,"Password should be at least 7 characters long",Toast.LENGTH_SHORT).show();
                }
                else if(!password1.equals(password2)) {
                    Toast.makeText(SignUpPage.this,"Passwords do not match",Toast.LENGTH_SHORT).show();
                }
                else if (phone.length() != 10 || !phone.matches("[0-9]+")) {
                    Toast.makeText(SignUpPage.this,"Enter a valid 10-digit phone number",Toast.LENGTH_SHORT).show();
                }
                else{
                    SignUpRequest(username,email,phone,password1);

                }

            }
        });
    }

    public void SignUpRequest(String username, String email, String phone, String password) {
        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new FormBody.Builder()
                .add("USERNAME", username)
                .add("EMAIL", email)
                .add("PASSWORD", password)
                .add("CELL_NO", phone)
                .build();

        // Build HTTP request
        Request request = new Request.Builder()
                .url(sendURL)
                .post(requestBody)
                .build();

        // Make the HTTP request asynchronously
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Handle network errors
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SignUpPage.this, "Network error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseData = response.body().string();
                Log.d("SignUp", "Response: " + responseData); // Log the response for debugging

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (responseData.trim().equals("Sign up Successful")) {
                            Toast.makeText(SignUpPage.this, "Sign up successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SignUpPage.this, LoginPage.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(SignUpPage.this, responseData, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}