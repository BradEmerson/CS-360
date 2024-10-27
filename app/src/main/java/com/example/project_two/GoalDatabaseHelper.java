package com.example.project_two;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;

// Implements a database to store the goal weight value in (BME 10/15/2024)
public class GoalDatabaseHelper extends SQLiteOpenHelper {

    // Initializes the necessary variables (BME 10/15/2024)
    private static final String DATABASE_NAME = "goal_weight.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "goal_weight";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_GOAL_WEIGHT = "goal";

    // Constructor to initialize the database helper (BME 10/15/2024)
    public GoalDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    // Creates the database table for storing the goal weight (BME 10/15/2024)
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_GOAL_WEIGHT + " REAL)";
        db.execSQL(createTable); // Executes SQL command to create the table (BME 10/16/2024)
    }

    @Override
    // Upgrades the database if the version changes (BME 10/15/2024)
    // FIXME: Implement changes to back up data before update (BME 10/15/2024)
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME); // Drops the existing table if it exists (BME 10/16/2024)
        onCreate(db); // Recreates the table with the new structure (BME 10/16/2024)
    }

    // Sets the goal weight value in the database (BME 10/15/2024)
    public void setGoalWeight(double goalWeight) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME); // Always store a single value (BME 10/16/2024)
        ContentValues values = new ContentValues();
        values.put(COLUMN_GOAL_WEIGHT, goalWeight);
        db.insert(TABLE_NAME, null, values); // Inserts the goal weight value into the table (BME 10/16/2024)
        db.close(); // Closes the database after operation is complete (BME 10/16/2024)
    }

    // Retrieves the goal weight value from the database (BME 10/15/2024)
    public double getGoalWeight() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_GOAL_WEIGHT + " FROM " + TABLE_NAME + " LIMIT 1", null);
        double goalWeight = -1; // Default value if no goal is set (BME 10/16/2024)
        if (cursor.moveToFirst()) {
            goalWeight = cursor.getDouble(0); // Retrieves the goal weight from the first row (BME 10/16/2024)
        }
        cursor.close(); // Closes the cursor to release resources (BME 10/16/2024)
        db.close(); // Closes the database after reading is complete (BME 10/16/2024)
        return goalWeight;
    }
}
