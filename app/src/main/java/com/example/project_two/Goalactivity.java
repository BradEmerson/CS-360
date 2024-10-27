package com.example.project_two;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

// Activity to handle setting goal weight (BME 10/15/2024)
public class Goalactivity extends AppCompatActivity {

    EditText editTextGoalWeight;
    Button buttonSetGoal;
    TextView textViewGoalWeight;
    ProgressBar progressBar;
    GoalDatabaseHelper dbHelper;
    database_info dbInfo;

    // Initializes the activity and sets up views and button click listener (BME 10/15/2024)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_goal);

        // Initializes EditText, Button, TextView, ProgressBar, and database helpers (BME 10/15/2024)
        editTextGoalWeight = findViewById(R.id.editTextText5);
        buttonSetGoal = findViewById(R.id.button_set_goal2);
        textViewGoalWeight = findViewById(R.id.textViewGoalWeight);
        progressBar = findViewById(R.id.progressBar);
        dbHelper = new GoalDatabaseHelper(this);
        dbInfo = new database_info(this);

        // Displays the current goal weight (BME 10/15/2024)
        displayGoalWeight();

        // Sets click listener for button to set the goal weight (BME 10/15/2024)
        buttonSetGoal.setOnClickListener(view -> {
            String goalWeightStr = editTextGoalWeight.getText().toString().trim();
            if (!goalWeightStr.isEmpty()) {
                double goalWeight = Double.parseDouble(goalWeightStr);
                dbHelper.setGoalWeight(goalWeight); // Sets the new goal weight in the database (BME 10/15/2024)
                Toast.makeText(Goalactivity.this, "Goal weight set successfully!", Toast.LENGTH_SHORT).show();
                displayGoalWeight(); // Updates the displayed goal weight (BME 10/15/2024)
                updateProgressBar(goalWeight); // Updates the progress bar based on the new goal weight (BME 10/16/2024)
            } else {
                Toast.makeText(Goalactivity.this, "Please enter a goal weight.", Toast.LENGTH_SHORT).show();
            }
        });

        // Applies padding for system bars to ensure edge-to-edge content (BME 10/16/2024)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom); // Applies padding to the view for insets (BME 10/16/2024)
            return insets;
        });
    }

    // Displays the current goal weight in the TextView (BME 10/15/2024)
    private void displayGoalWeight() {
        String goalWeight = String.valueOf(dbHelper.getGoalWeight());
        if (!goalWeight.equals("-1")) {
            String goalText = getString(R.string.current_goal, goalWeight);
            textViewGoalWeight.setText(goalText); // Updates the TextView with the current goal weight (BME 10/15/2024)
            updateProgressBar(Double.parseDouble(goalWeight)); // Updates the progress bar with the current goal weight (BME 10/16/2024)
        } else {
            textViewGoalWeight.setText("No goal weight set."); // Displays fallback text if no goal weight is set (BME 10/15/2024)
            progressBar.setProgress(0); // Resets progress bar if no goal weight is set (BME 10/16/2024)
        }
    }

    // Updates the progress bar based on the goal weight and the most recent weight (BME 10/15/2024)
    private void updateProgressBar(double goalWeight) {
        int progress = dbInfo.calculateProgress(goalWeight);
        progressBar.setProgress(progress); // Sets the progress bar to reflect current progress (BME 10/16/2024)
    }
}
