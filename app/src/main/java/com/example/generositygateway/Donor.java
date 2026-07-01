package com.example.generositygateway;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

public class Donor extends AppCompatActivity {

    Spinner item;
    Button btnincrement;
    Button btndecrement;
    Button btndonate;
    Button btnhistory;
    String username;
    String userid;
    OkHttpClient client;
    String donateURL = "https://lamp.ms.wits.ac.za/home/s2544615/getdetails.php";
    EditText value;

    @SuppressLint("MissingInflatedId")
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.donor_dashboard);

        item = findViewById(R.id.spinneritems);
        btnincrement = findViewById(R.id.increment);
        btndecrement = findViewById(R.id.decrement);
        btndonate = findViewById(R.id.btn_Donate);
        btnhistory = findViewById(R.id.btn_history);
        value = findViewById(R.id.value);
        client = new OkHttpClient();

        Intent intent = getIntent();
        username = intent.getStringExtra("USERNAME");
        userid=intent.getStringExtra("USER_ID");

        value.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode != KeyEvent.KEYCODE_DEL) {
                    String input = value.getText().toString();
                    if (input.startsWith("0") && !input.equals("0")) {
                        value.setText(input.substring(1));
                        value.setSelection(value.getText().length());
                        return true;
                    }
                }
                return false;
            }
        });

        value.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().isEmpty()){
                    value.setText("0");
                    value.setSelection(value.getText().length());
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

        btndonate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                matches();
            }
        });

        btnhistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Donor.this,DonationHistory.class);
                intent.putExtra("USER_ID",userid);
                startActivity(intent);
            }
        });
    }

    public void decrement(){
        int qty = Integer.parseInt(value.getText().toString());
        if(qty > 0){
            qty--;
            value.setText(String.valueOf(qty));
        }
    }

    public void increment(){
        int qty = Integer.parseInt(value.getText().toString());
        qty++;
        value.setText(String.valueOf(qty));
    }

    public void matches(){
        String selecteditem = item.getSelectedItem().toString();
        String qtyText = value.getText().toString();

        if (selecteditem.equals(getResources().getString(R.string.spinner_prompt))) {
            Toast.makeText(Donor.this, "Please select an item", Toast.LENGTH_SHORT).show();
            return;
        }

        if(qtyText.equals("0")){
            Toast.makeText(Donor.this,"Please enter quantity", Toast.LENGTH_SHORT).show();
            return;
        }

        Request request = new Request.Builder()
                .url(donateURL + "?SELECTED_ITEM=" +selecteditem + "&DONOR_USERNAME=" + username)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                    final String responseData = response.body().string();
                    String[] responseParts = responseData.split("\\|");
                    String requestid = responseParts[0];
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(Donor.this, DonationMatches.class);
                            intent.putExtra("USERNAME",username);
                            intent.putExtra("SELECTED_ITEM",responseData);
                            intent.putExtra("DONOR_QUANTITY",qtyText);
                            intent.putExtra("USER_ID",userid);
                            intent.putExtra("REQUEST_ID",requestid);
                            startActivity(intent);
                        }
                    });
            }
        });
    }
}
