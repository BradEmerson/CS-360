package com.example.project_two;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class SettingsActivity extends AppCompatActivity {

    private static final int SMS_PERMISSION_CODE = 100;
    private static final int NOTIFICATION_PERMISSION_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initializes switches for SMS, notifications, and auto-login (BME 10/16/2024)
        Switch switchSms = findViewById(R.id.switch_sms);
        Switch switchNotification = findViewById(R.id.switch_notification);
        Switch switchAutoLogin = findViewById(R.id.switch1);

        // Sets up SharedPreferences to store user preferences (BME 10/16/2024)
        SharedPreferences sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);

        // Sets up auto-login switch state and listener (BME 10/16/2024)
        boolean autoLogin = sharedPreferences.getBoolean("autoLogin", false);
        switchAutoLogin.setChecked(autoLogin);
        switchAutoLogin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (isChecked) {
                    editor.putBoolean("autoLogin", true); // Enables auto-login (BME 10/16/2024)
                    Toast.makeText(SettingsActivity.this, "Auto-login enabled", Toast.LENGTH_SHORT).show();
                } else {
                    editor.putBoolean("autoLogin", false); // Disables auto-login (BME 10/16/2024)
                    editor.remove("username"); // Removes stored username (BME 10/16/2024)
                    editor.remove("password"); // Removes stored password (BME 10/16/2024)
                    Toast.makeText(SettingsActivity.this, "Auto-login disabled", Toast.LENGTH_SHORT).show();
                }
                editor.apply(); // Applies changes to SharedPreferences (BME 10/16/2024)
            }
        });

        // Sets up SMS switch state and listener (BME 10/16/2024)
        boolean smsEnabled = sharedPreferences.getBoolean("smsEnabled", false);
        switchSms.setChecked(smsEnabled);
        switchSms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (isChecked) {
                    // Requests SMS permission if not already granted (BME 10/16/2024)
                    if (ContextCompat.checkSelfPermission(SettingsActivity.this, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(SettingsActivity.this, new String[]{android.Manifest.permission.SEND_SMS}, SMS_PERMISSION_CODE);
                    } else {
                        Toast.makeText(SettingsActivity.this, "SMS Permission Already Granted", Toast.LENGTH_SHORT).show();
                    }
                    editor.putBoolean("smsEnabled", true); // Enables SMS setting (BME 10/16/2024)
                } else {
                    // Notifies user that SMS permission must be disabled manually (BME 10/16/2024)
                    Toast.makeText(SettingsActivity.this, "SMS Permission must be disabled manually from settings", Toast.LENGTH_LONG).show();
                    editor.putBoolean("smsEnabled", false); // Disables SMS setting (BME 10/16/2024)
                }
                editor.apply(); // Applies changes to SharedPreferences (BME 10/16/2024)
            }
        });

        // Sets up notification switch state and listener (BME 10/16/2024)
        boolean notificationEnabled = sharedPreferences.getBoolean("notificationEnabled", false);
        switchNotification.setChecked(notificationEnabled);
        switchNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (isChecked) {
                    // Requests notification permission if not already granted (Android 13+) (BME 10/16/2024)
                    if (ContextCompat.checkSelfPermission(SettingsActivity.this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(SettingsActivity.this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_CODE);
                    } else {
                        Toast.makeText(SettingsActivity.this, "Notification Permission Already Granted", Toast.LENGTH_SHORT).show();
                    }
                    editor.putBoolean("notificationEnabled", true); // Enables notifications (BME 10/16/2024)
                } else {
                    // Notifies user that notification permission must be disabled manually (BME 10/16/2024)
                    Toast.makeText(SettingsActivity.this, "Notification Permission must be disabled manually from settings", Toast.LENGTH_LONG).show();
                    editor.putBoolean("notificationEnabled", false); // Disables notifications (BME 10/16/2024)
                }
                editor.apply(); // Applies changes to SharedPreferences (BME 10/16/2024)
            }
        });

        // Shows popup message about SMS permissions requirement (BME 10/15/2024)
        showSmsRequirementPopup();
    }

    // Shows a popup message about SMS permissions requirement (BME 10/15/2024)
    private void showSmsRequirementPopup() {
        new AlertDialog.Builder(this)
                .setTitle("SMS Permission Required")
                .setMessage(getString(R.string.sms_manifest))
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); // Dismisses the dialog (BME 10/16/2024)
                    }
                })
                .setCancelable(false)
                .show(); // Displays the alert dialog (BME 10/16/2024)
    }
}
