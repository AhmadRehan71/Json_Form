package com.example.jsonform;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.szagurskii.patternedtextwatcher.PatternedTextWatcher;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> allViewsList;
    private HashMap<Integer, String> allViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        allViewsList = new ArrayList<>();
        allViews = new HashMap<>();

        LinearLayout container = findViewById(R.id.container);

        JSONObject jsonObject;
        JSONArray jsonArray;
        try {
            jsonObject = new JSONObject(CONST.FORM_DATA);
            jsonArray =
                    jsonObject.getJSONArray("form_builder_json").getJSONObject(0).getJSONArray("data");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject field;
                field = jsonArray.getJSONObject(i);
                String type = field.get("type").toString();
                container.addView(generatedTextView(field));
                switch (type) {
                    case "text":
                        if (field.has("enabled") && field.get("enabled").equals("true")) {
                            EditText editText = generatedEditText(field, i);
                            editText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                            container.addView(editText);
                        }
                        break;
                    case "date":
                        container.addView(generatedDateTimePicker(i));
                        break;
                    case "textArea":
                        container.addView(generatedEditText(field, i));
                        break;
                    case "formated_number":
                        container.addView(formattedEditText(field, i));
                        break;
                    case "select":
                        container.addView(generatedSpinner(field, i));
                        break;
                }
            }
            container.addView(myButton());
        } catch (Exception e) {
            Log.d("MyForm", e.getMessage());
            e.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    private Button myButton() {
        Button button = new Button(this);
        button.setText("Submit");
        button.setPadding(20, 20, 20, 20);
        button.setTextSize(14f);
        button.setOnClickListener(v -> {
            if (validateInputs()) {
                startActivity(new Intent(
                        MainActivity.this, ResultActivity.class
                ).putExtra("result", allViewsList));
            }
        });
        return button;
    }

    private Boolean validateInputs() {
        Log.d("MyForm", "" + allViews.size());
        for (Map.Entry<Integer, String> item : allViews.entrySet()) {
            if (item.getValue().equals("ET")) {
                EditText editText = findViewById(item.getKey());
                if (editText.getText().toString().isEmpty()) {
                    editText.setError("Please Enter Value");
                    editText.requestFocus();
                    allViewsList.clear();
                    break;
                } else {
                    allViewsList.add(editText.getText().toString());
                    Log.d("MyForm", editText.getText().toString());
                }
            } else if (item.getValue().equals("SP")) {
                Spinner spinner = findViewById(item.getKey());
                allViewsList.add(spinner.getSelectedItem().toString());
                Log.d("MyForm", spinner.getSelectedItem().toString());
            }
        }
        return allViewsList.size() > 0;
    }

    private TextView generatedTextView(JSONObject jsonObject) throws JSONException {
        TextView textView = new TextView(this);
        textView.setGravity(Gravity.CENTER);
        textView.setText(jsonObject.get("title").toString());
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(14F);
        textView.setPadding(10, 10, 10, 10);
        return textView;
    }

    private EditText generatedEditText(JSONObject jsonObject, int id) throws JSONException {
        EditText editText = new EditText(this);
        if (jsonObject.has("placeholder")) {
            editText.setHint(jsonObject.get("placeholder").toString());
        }
        editText.setId(id);
        allViews.put(id, "ET");
        return editText;
    }

    private EditText formattedEditText(JSONObject jsonObject, int id) throws JSONException {
        EditText editText = new EditText(this);
        editText.setInputType(InputType.TYPE_CLASS_PHONE);
        String pattern = "(###) ###-####";
        if (jsonObject.has("formatted_numeric_formate")) {
            pattern = jsonObject.get("formatted_numeric_formate").toString();
        }
        //editText.keyListener = DigitsKeyListener.getInstance("(012) 345-6789")
        editText.addTextChangedListener(new PatternedTextWatcher(pattern));
        editText.setId(id);
        allViews.put(id, "ET");
        return editText;
    }

    private Spinner generatedSpinner(JSONObject jsonObject, int id) throws JSONException {
        Spinner spinner = new Spinner(this);
        ArrayList<String> paths = new ArrayList<>();
        JSONObject options = new JSONObject(jsonObject.get("list_options").toString());
        for (Iterator<String> it = options.keys(); it.hasNext(); ) {
            String item = it.next();
            paths.add(options.get(item).toString());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                paths);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setGravity(Gravity.CENTER);
        spinner.setId(id);
        allViews.put(id, "SP");
        return spinner;
    }

    private EditText generatedDateTimePicker(int id) {
        EditText editText = new EditText(this);
        editText.setClickable(true);
        editText.setFocusable(false);
        editText.setInputType(InputType.TYPE_NULL);
        editText.setOnClickListener(v -> {
            Calendar myCalendar = Calendar.getInstance();
            int mYear = myCalendar.get(Calendar.YEAR);
            int mMonth = myCalendar.get(Calendar.MONTH);
            int mDay = myCalendar.get(Calendar.DAY_OF_MONTH);
            new DatePickerDialog(MainActivity.this, (view, year, month, dayOfMonth) -> {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "MM/dd/yy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                editText.setText(sdf.format(myCalendar.getTime()));
            }, mYear, mMonth, mDay).show();
        });
        editText.setId(id);
        allViews.put(id, "ET");
        return editText;
    }
}