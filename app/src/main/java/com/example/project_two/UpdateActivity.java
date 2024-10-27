package com.example.project_two;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class UpdateActivity extends AppCompatActivity {

    EditText edit_date, edit_time, edit_weight;
    Button button_update;

    String id, date, time, weight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update);

        edit_date = findViewById(R.id.edit_date2);
        edit_time = findViewById(R.id.edit_time2);
        edit_weight = findViewById(R.id.edit_weight2);
        button_update = findViewById(R.id.button_update);

        // Calls getters and setters first (BME 10/2/2024)
        getAndSetIntentData();

        button_update.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                // Pulls data from update fields prior to pushing data to dbInfo.updateDate method. (BME 10/2/2024)
                date = edit_date.getText().toString();
                time = edit_time.getText().toString();
                weight = edit_weight.getText().toString();

                // Creates new database_info object (BME 10/2/2024)
                database_info dbInfo = new database_info(UpdateActivity.this);

                // Calls updates after getters/setters  (BME 10/2/2024)
                dbInfo.updateData(id, date, time, weight);

                // Navigates back to MainActivity2 after editing the weight (BME 10/15/2024)
                Intent intent = new Intent(UpdateActivity.this, MainActivity2.class);
                intent.putExtra("dataUpdated", true);
                setResult(RESULT_OK, intent);
                finish();

            }
        });



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    void getAndSetIntentData(){
        if(getIntent().hasExtra("id") && getIntent().hasExtra("date") && getIntent().hasExtra("time") && getIntent().hasExtra("weight")){
            // Gets data from intent (BME 10/2/2024)
            id = getIntent().getStringExtra("id");
            date = getIntent().getStringExtra("date");
            time = getIntent().getStringExtra("time");
            weight = getIntent().getStringExtra("weight");

            // Sets intent data (BME 10/2/2024)
            edit_date.setText(date);
            edit_time.setText(time);
            edit_weight.setText(weight);

        }else{
            Toast.makeText(this, "No data.", Toast.LENGTH_SHORT).show();
        }
    }

}