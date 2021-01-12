package com.example.jsonform;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ResultActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        ArrayList<String> myList = (ArrayList<String>) getIntent().getSerializableExtra(
                "result");

        TextView txtResult = findViewById(R.id.txtResult);

        for (String result : myList) {
            txtResult.setText(txtResult.getText().toString() + "\n" + result);
        }
    }
}