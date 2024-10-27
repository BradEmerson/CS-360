package com.example.project_two;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity2 extends AppCompatActivity {

    RecyclerView recyclerView;
    FloatingActionButton button_add_weight;
    FloatingActionButton button_add_goal;
    CustomAdapter customAdapter;

    database_info dbInfo;
    ArrayList<String> weight_id, weight_date, weight_time, weight;
    ImageButton button_settings;

    boolean deleteInProgress = false; // Prevents multiple delete triggers (BME 10/15/2024)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Sets content view to activity_main2 layout (BME 10/1/2024)
        setContentView(R.layout.activity_main2);

        // Initializes add goal button and sets its click listener (BME 10/15/2024)
        button_add_goal = findViewById(R.id.button_add_goal);
        button_add_goal.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity2.this, Goalactivity.class);
                startActivity(intent); // Starts Goalactivity to add a new goal (BME 10/16/2024)
            }
        });

        // Initializes settings button and sets its click listener (BME 10/16/2024)
        button_settings = findViewById(R.id.button_settings);
        button_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity2.this, SettingsActivity.class);
                startActivity(intent); // Starts SettingsActivity (BME 10/16/2024)
            }
        });

        // Initializes RecyclerView to display weight entries (BME 10/16/2024)
        recyclerView = findViewById((R.id.recyclerView));

        // Initializes add weight button and sets its click listener (BME 10/16/2024)
        button_add_weight = findViewById(R.id.button_add_weight);
        button_add_weight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity2.this, AddActivity.class);
                startActivity(intent); // Starts AddActivity to add a new weight entry (BME 10/16/2024)
            }
        });

        // Initializes database and data lists (BME 10/16/2024)
        dbInfo = new database_info(MainActivity2.this);
        weight_id = new ArrayList<>();
        weight_date = new ArrayList<>();
        weight_time = new ArrayList<>();
        weight = new ArrayList<>();

        // Enables edge-to-edge UI (BME 10/16/2024)
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom); // Applies padding for system bars (BME 10/16/2024)
            return insets;
        });

        // Stores weight data from database into arrays (BME 10/16/2024)
        storeDataInArrays();

        // Sets up the RecyclerView adapter to display weight entries (BME 10/16/2024)
        customAdapter = new CustomAdapter(MainActivity2.this, this, weight_id, weight_date, weight_time, weight);
        recyclerView.setAdapter(customAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity2.this));

        // Deletes all entries button functionality (BME 10/15/2024)
        FloatingActionButton button_delete_all = findViewById(R.id.button_delete_all);
        button_delete_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!deleteInProgress) {
                    showDeleteConfirmationDialog(); // Shows confirmation dialog before deleting all entries (BME 10/16/2024)
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshData(); // Refreshes the RecyclerView when returning to MainActivity2 (BME 10/15/2024)
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1) {
            refreshData(); // Refreshes data if returning from an activity that requires updates (BME 10/16/2024)
        }
    }

    // Stores weight data from the database into arrays (BME 10/16/2024)
    void storeDataInArrays(){
        Cursor cursor = dbInfo.readAllDate();
        if(cursor.getCount() == 0) {
            Toast.makeText(this, " No data.", Toast.LENGTH_SHORT).show(); // Shows message if no data is found (BME 10/16/2024)
        }else{
            while (cursor.moveToNext()){
                weight_id.add(cursor.getString(0));
                weight_date.add(cursor.getString(1));
                weight_time.add(cursor.getString(2));
                weight.add(cursor.getString(3));
            }
        }
    }

    // Refreshes data and updates the RecyclerView (BME 10/15/2024)
    void refreshData() {
        weight_id.clear();
        weight_date.clear();
        weight_time.clear();
        weight.clear();
        storeDataInArrays(); // Repopulates data arrays (BME 10/16/2024)
        customAdapter.notifyDataSetChanged(); // Notifies adapter of data change (BME 10/16/2024)
    }

    // Shows a delete confirmation dialog (BME 10/15/2024)
    void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Warning")
                .setMessage("You are about to delete all of your weight entries, this cannot be undone. Do you wish to proceed?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteInProgress = true; // Sets flag to prevent re-triggering (BME 10/15/2024)
                        deleteAllEntries(); // Deletes all entries from the database (BME 10/16/2024)
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteInProgress = false; // Resets flag if user cancels (BME 10/15/2024)
                    }
                })
                .show();
    }

    // Deletes all entries from the database (BME 10/15/2024)
    void deleteAllEntries() {
        if (dbInfo.deleteAllData()) {  // Checks if data deletion was successful (BME 10/15/2024)
            refreshData();  // Refreshes the RecyclerView (BME 10/15/2024)
            Toast.makeText(this, "All entries deleted.", Toast.LENGTH_SHORT).show(); // Shows confirmation toast (BME 10/16/2024)
        } else {
            Toast.makeText(this, "Error deleting entries.", Toast.LENGTH_SHORT).show(); // Shows error toast if deletion fails (BME 10/16/2024)
        }
        deleteInProgress = false; // Resets flag after deletion (BME 10/15/2024)
    }

}