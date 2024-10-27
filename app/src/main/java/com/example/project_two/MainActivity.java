package com.example.project_two;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    // Instantiates variables used for this class (BME 10/15/2024)
    private EditText usernameEditText, passwordEditText;
    private CheckBox autoLoginCheckBox;
    private TextView loginErrorMessage;

    // Creates new instance of LoginHelper class (BME 10/15/2024)
    private LoginHelper loginHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Initializes the UI components (BME 10/15/2024)
        usernameEditText = findViewById(R.id.editTextText);
        passwordEditText = findViewById(R.id.editTextTextPassword);
        Button loginButton = findViewById(R.id.button_login);
        Button createAccountButton = findViewById(R.id.button4);
        autoLoginCheckBox = findViewById(R.id.checkBox);
        loginErrorMessage = findViewById(R.id.login_error_message);

        // Set up TextViews with hyperlinks (BME 10/16/2024)
        TextView forgotUsername = findViewById(R.id.textView);
        forgotUsername.setText(Html.fromHtml("<a href='https://www.youtube.com/watch?v=dQw4w9WgXcQ'>Forgot Username?</a>", Html.FROM_HTML_MODE_LEGACY));
        forgotUsername.setMovementMethod(LinkMovementMethod.getInstance());

        TextView forgotPassword = findViewById(R.id.textView2);
        forgotPassword.setText(Html.fromHtml("<a href='https://www.youtube.com/watch?v=dQw4w9WgXcQ'>Forgot Password?</a>", Html.FROM_HTML_MODE_LEGACY));
        forgotPassword.setMovementMethod(LinkMovementMethod.getInstance());

        // Initializes the database LoginHelper object (BME 10/16/2024)
        loginHelper = new LoginHelper(this);

        // Checks for auto-login preference (BME 10/16/2024)
        SharedPreferences sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        boolean autoLogin = sharedPreferences.getBoolean("autoLogin", false);
        if (autoLogin) {
            String savedUsername = sharedPreferences.getString("username", "");
            String savedPassword = sharedPreferences.getString("password", "");
            if (loginHelper.checkUser(savedUsername, savedPassword)) {
                Toast.makeText(MainActivity.this, "Auto-login Successful!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                startActivity(intent);
                finish();
            }
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = usernameEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                // Checks if the username and password match the database (BME 10/15/2024)
                if (loginHelper.checkUser(username, password)) {
                    Toast.makeText(MainActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                    if (autoLoginCheckBox.isChecked()) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("autoLogin", true);
                        editor.putString("username", username);
                        editor.putString("password", password);
                        editor.apply(); // Save auto-login preferences (BME 10/16/2024)
                    }
                    Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                    startActivity(intent);
                    finish();
                } else {
                    loginErrorMessage.setText("Invalid username or password!");
                    loginErrorMessage.setVisibility(View.VISIBLE); // Show error message (BME 10/16/2024)
                    loginErrorMessage.postDelayed(() -> loginErrorMessage.setVisibility(View.GONE), 2000); // Hide error message after delay (BME 10/16/2024)
                }
            }
        });

        // Sets up touch listener to minimize keyboard when clicking outside input fields (BME 10/16/2024)
        findViewById(R.id.main).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    if (getCurrentFocus() != null && imm != null) {
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0); // Hide the keyboard (BME 10/16/2024)
                    }
                }
                return false;
            }
        });

        // Creates the "Create Account" button listener (BME 10/15/2024)
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                // Check if username and password are not empty (BME 10/16/2024)
                if (username.isEmpty() && password.isEmpty()) {
                    loginErrorMessage.setText("Please enter a username and password!");
                    loginErrorMessage.setVisibility(View.VISIBLE); // Shows error message (BME 10/16/2024)
                    loginErrorMessage.postDelayed(() -> loginErrorMessage.setVisibility(View.GONE), 2000); // Hide error message after delay (BME 10/16/2024)
                } else if (username.isEmpty()) {
                    loginErrorMessage.setText("Please enter a username!");
                    loginErrorMessage.setVisibility(View.VISIBLE); // Shows error message (BME 10/16/2024)
                    loginErrorMessage.postDelayed(() -> loginErrorMessage.setVisibility(View.GONE), 2000); // Hide error message after delay (BME 10/16/2024)
                } else if (password.isEmpty()) {
                    loginErrorMessage.setText("Please enter a password!");
                    loginErrorMessage.setVisibility(View.VISIBLE); // Shows error message (BME 10/16/2024)
                    loginErrorMessage.postDelayed(() -> loginErrorMessage.setVisibility(View.GONE), 2000); // Hide error message after delay (BME 10/16/2024)
                } else {
                    // FIXME: Add more specific error handling for account creation failures
                    //  (e.g., "Username already exists") (BME 10/15/2024)
                    if (loginHelper.addUser(username, password)) {
                        Toast.makeText(MainActivity.this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        loginErrorMessage.setText("Account creation failed. Try again.");
                        loginErrorMessage.setVisibility(View.VISIBLE); // Show error message (BME 10/16/2024)
                        loginErrorMessage.postDelayed(() -> loginErrorMessage.setVisibility(View.GONE), 2000); // Hide error message after delay (BME 10/16/2024)
                    }
                }
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom); // Applies padding for system bars (BME 10/16/2024)
            return insets;
        });
    }
}