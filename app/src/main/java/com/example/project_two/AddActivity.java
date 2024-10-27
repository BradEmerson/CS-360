package com.example.project_two;

import static java.lang.Float.parseFloat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AddActivity extends AppCompatActivity {

    private static final int SMS_PERMISSION_CODE = 1;
    EditText edit_date, edit_time, edit_weight;
    Button button_add_weight2;
    GoalDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add);

        // Initializes EditText fields for date, time, and weight (BME 10/16/2024)
        edit_date = findViewById(R.id.edit_date);
        edit_time = findViewById(R.id.edit_time);
        edit_weight = findViewById(R.id.edit_weight);
        dbHelper = new GoalDatabaseHelper(this);

        // Initializes add weight button and sets its click listener (BME 10/16/2024)
        button_add_weight2 = findViewById(R.id.button_add_weight2);
        button_add_weight2.setOnClickListener(new View.OnClickListener() {
            @Override
            // Adds new weight entry to the database (BME 10/16/2024)
            public void onClick(View view) {
                try (database_info dbInfo = new database_info(AddActivity.this)) {
                    dbInfo.addWeight(edit_date.getText().toString().trim(),
                            edit_time.getText().toString().trim(),
                            edit_weight.getText().toString().trim());
                }

                // Checks for SMS permission before trying to send the SMS (BME 10/19/2024)
                if (ContextCompat.checkSelfPermission(AddActivity.this, Manifest.permission.SEND_SMS)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(AddActivity.this,
                            new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_CODE);
                } else {
                    // Permission is already granted, proceed to send the SMS (BME 10/19/2024)
                    sendSms();
                }

                // Navigates back to MainActivity2 after adding the weight (BME 10/15/2024)
                Intent intent = new Intent(AddActivity.this, MainActivity2.class);
                intent.putExtra("dataUpdated", true);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        // Sets touch listener to hide the keyboard when clicking outside input fields (BME 10/16/2024)
        findViewById(R.id.main).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    if (getCurrentFocus() != null && imm != null) {
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0); // Hides the keyboard (BME 10/16/2024)
                    }
                }
                return false;
            }
        });

        // Applies padding for system bars to ensure edge-to-edge content (BME 10/16/2024)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Handles the result of the SMS permission request and sends SMS if granted (BME 10/15/2024)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendSms(); // Sends SMS after permission is granted (BME 10/16/2024)
            } else {
                Toast.makeText(this, "Permission denied. SMS feature will not be available.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Sends an SMS when goal weight is reached by the user (BME 10/19/2024)
    private void sendSms() {
        Float goalWeight = parseFloat(String.valueOf(dbHelper.getGoalWeight()));
        Float newWeight = parseFloat(edit_weight.getText().toString().trim());
        // If goal weight is within .5 lbs of added weight, this sends an SMS (BME 10/19/2024)
        if (Math.abs(goalWeight - newWeight) <= .5) {
            try {
                SmsManager smsManager = getSystemService(SmsManager.class);
                String message = "You've reached your goal weight of: " + goalWeight + " !";
                String phoneNumber = "6058589460";
                // Sends SMS with goal weight (BME 10/19/2024)
                smsManager.sendTextMessage(phoneNumber, null, message, null, null);
                Toast.makeText(this, "You've reached your goal weight of: " + goalWeight + "! SMS sent!", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "Failed to send SMS: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

}