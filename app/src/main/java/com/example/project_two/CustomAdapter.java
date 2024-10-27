package com.example.project_two;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

// Defines a custom adapter for the RecyclerView (BME 10/16/2024)
public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    private Context context;
    Activity activity;
    private ArrayList weight_id, weight_date, weight_time, weight;

    int position;
    // Initializes the adapter with activity, context, and weight data lists (BME 10/16/2024)
    CustomAdapter(Activity activity, Context context, ArrayList weight_id, ArrayList weight_date, ArrayList weight_time, ArrayList weight) {
        this.activity = activity;
        this.context = context;
        this.weight_id = weight_id;
        this.weight_date = weight_date;
        this.weight_time = weight_time;
        this.weight = weight;
    }

    @NonNull
    @Override
    // Creates and returns a new MyViewHolder with the inflated view (BME 10/16/2024)
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_row, parent, false);
        return new MyViewHolder(view); // Inflates view for each row (BME 10/16/2024)
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        this.position = position;

        // Sets weight data for each item in the RecyclerView (BME 10/16/2024)
        holder.weight_id_txt.setText(String.valueOf(weight_id.get(position)));
        holder.weight_date_txt.setText("DATE: " + String.valueOf(weight_date.get(position)));
        holder.weight_time_txt.setText("TIME: " + String.valueOf(weight_time.get(position)));
        holder.weight_txt.setText("WEIGHT: " + String.valueOf(weight.get(position)));

        // Adds delete functionality to the delete button (BME 10/16/2024)
        holder.floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = String.valueOf(weight_id.get(position));

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete Entry")
                        .setMessage("Are you sure you want to delete this entry?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                database_info dbInfo = new database_info(context);
                                dbInfo.deleteOneRow(id); // Deletes selected entry from the database (BME 10/16/2024)

                                weight_id.remove(position); // Removes deleted entry from weight_id list (BME 10/16/2024)
                                weight_date.remove(position); // Removes deleted entry from weight_date list (BME 10/16/2024)
                                weight_time.remove(position); // Removes deleted entry from weight_time list (BME 10/16/2024)
                                weight.remove(position); // Removes deleted entry from weight list (BME 10/16/2024)

                                notifyItemRemoved(position); // Notifies adapter about removed item (BME 10/16/2024)
                                notifyItemRangeChanged(position, weight_id.size()); // Notifies adapter about range changes (BME 10/16/2024)

                                Toast.makeText(context, "Entry deleted successfully", Toast.LENGTH_SHORT).show(); // Shows deletion confirmation (BME 10/16/2024)
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });

        // Sets click listener to navigate to UpdateActivity when item is clicked (BME 10/16/2024)
        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, UpdateActivity.class);
                intent.putExtra("id", String.valueOf(weight_id.get(position)));
                intent.putExtra("date", String.valueOf(weight_date.get(position)));
                intent.putExtra("time", String.valueOf(weight_time.get(position)));
                intent.putExtra("weight", String.valueOf(weight.get(position)));
                activity.startActivityForResult(intent, 1); // Starts UpdateActivity for result (BME 10/16/2024)
            }
        });
    }

    @Override
    public int getItemCount() {
        return weight_id.size(); // Returns the size of the weight_id list (BME 10/16/2024)
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView weight_id_txt, weight_date_txt, weight_time_txt, weight_txt;
        LinearLayout mainLayout;
        FloatingActionButton floatingActionButton;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            weight_id_txt = itemView.findViewById(R.id.weight_id_txt); // Initializes weight_id TextView (BME 10/16/2024)
            weight_date_txt = itemView.findViewById(R.id.weight_date_txt); // Initializes weight_date TextView (BME 10/16/2024)
            weight_time_txt = itemView.findViewById(R.id.weight_time_txt); // Initializes weight_time TextView (BME 10/16/2024)
            weight_txt = itemView.findViewById(R.id.weight_txt); // Initializes weight TextView (BME 10/16/2024)
            mainLayout = itemView.findViewById(R.id.mainLayout); // Initializes main layout for each item (BME 10/16/2024)
            floatingActionButton = itemView.findViewById(R.id.floatingActionButton); // Initializes delete button (BME 10/16/2024)
        }
    }
}
