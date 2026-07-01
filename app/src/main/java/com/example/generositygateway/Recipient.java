package com.example.generositygateway;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.PixelCopy;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Recipient extends AppCompatActivity {

    Button btnincrement;
    Button btndecrement;
    Button btnrequest;
    Spinner item;
    EditText quantity;
    String username;
    String userid;
    OkHttpClient client;
    String requestURL = "https://lamp.ms.wits.ac.za/home/s2544615/request.php";

    @SuppressLint("MissingInflatedId")
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipient_dashboard);

        item = findViewById(R.id.spinneritems);
        btnincrement = findViewById(R.id.increment);
        btndecrement = findViewById(R.id.decrement);
        quantity = findViewById(R.id.value);
        btnrequest = findViewById(R.id.btn_request);
        client = new OkHttpClient();

        Intent intent = getIntent();
        username = intent.getStringExtra("USERNAME");
        userid = intent.getStringExtra("USER_ID");

        quantity.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode != KeyEvent.KEYCODE_DEL) {
                    String input = quantity.getText().toString();
                    if (input.startsWith("0") && !input.equals("0")) {
                        quantity.setText(input.substring(1));
                        quantity.setSelection(quantity.getText().length());
                        return true;
                    }
                }
                return false;
            }
        });

        quantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().isEmpty()){
                    quantity.setText("0");
                    quantity.setSelection(quantity.getText().length());
                }
            }
        });

        //quantity buttons
        btnincrement.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                increment();
            }
        });

        btndecrement.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                decrement();
            }
        });

        //the menu
        String[] items = getResources().getStringArray(R.array.Items);
        String[] itemsprompt = new String[items.length+1];
        itemsprompt[0] = getResources().getString(R.string.spinner_prompt);
        System.arraycopy(items,0,itemsprompt,1,items.length);

        ArrayAdapter<String> adapter =  new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,itemsprompt);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        item.setAdapter(adapter);

        btnrequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendRequest();
            }
        });
    }

    public void decrement(){
        int qty = Integer.parseInt(quantity.getText().toString());
        if(qty > 0){
            qty--;
            quantity.setText(String.valueOf(qty));
        }
    }

    public void increment(){
        int qty = Integer.parseInt(quantity.getText().toString());
        qty++;
        quantity.setText(String.valueOf(qty));
    }

    public void sendRequest(){
        String selecteditem = item.getSelectedItem().toString();
        String qtyText = quantity.getText().toString();

        if (selecteditem.equals(getResources().getString(R.string.spinner_prompt))) {
            Toast.makeText(Recipient.this, "Please select an item", Toast.LENGTH_SHORT).show();
            return;
        }

        if(qtyText.equals("0")){
            Toast.makeText(Recipient.this,"Please enter quantity", Toast.LENGTH_SHORT).show();
            return;
        }

        int qty = Integer.parseInt(qtyText);

        RequestBody requestBody = new FormBody.Builder()
                .add("ITEM", selecteditem)
                .add("USERNAME",username)
                .add("ITEM_QUANTITY", String.valueOf(qty))
                .build();

        Request request = new Request.Builder()
                .url(requestURL)
                .post(requestBody)
                .build();

        Log.d("SendResponse", "Sending request to: " + requestURL);
        Log.d("SendResponse", "Request body: ITEM=" + selecteditem + ", ITEM_QUANTITY=" + qty + ", INITIAL_QUANTITY=" + qty);

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Recipient.this, "Network error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                Log.d("SendRequest", "Response: " + responseData);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (responseData.trim().equals("Request submitted successfully")) {
                            Toast.makeText(Recipient.this, "Request submitted successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Recipient.this,DonorRecipient.class);
                            intent.putExtra("USERNAME",username);
                            intent.putExtra("BIO_CHECK_REQUIRED",true);
                            intent.putExtra("USER_ID",userid);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(Recipient.this, responseData, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

}
