package com.example.project_two;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;
import androidx.annotation.Nullable;

class database_info extends SQLiteOpenHelper {

    private Context context;
    private static final String DATABASE_NAME = "WeightRecords.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "weight_table";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_DATE = "Date";
    private static final String COLUMN_TIME = "Time";
    private static final String COLUMN_WEIGHT = "Weight";

    // Initializes database_info with context, database name, and version (BME 10/16/2024)
    database_info(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    // Creates the weight_table in the database (BME 10/16/2024)
    public void onCreate(SQLiteDatabase db) {
        String query =
                "CREATE TABLE " + TABLE_NAME +
                        " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_DATE + " TEXT, " +
                        COLUMN_TIME + " TEXT, " +
                        COLUMN_WEIGHT + " TEXT);";
        db.execSQL(query);
    }

    @Override
    // Upgrades the database by dropping the existing table and creating a new one (BME 10/16/2024)
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Adds a weight record to the database (BME 10/16/2024)
    void addWeight(String date, String time, String weight) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_DATE, date);
        cv.put(COLUMN_TIME, time);
        cv.put(COLUMN_WEIGHT, weight);
        long result = db.insert(TABLE_NAME, null, cv);
        if (result == -1) {
            Toast.makeText(context, "Operation Failed!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Operation Success!", Toast.LENGTH_SHORT).show();
        }
    }

    // Reads all weight records from the database (BME 10/16/2024)
    Cursor readAllDate() {
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    // Updates a weight record in the database (BME 10/16/2024)
    void updateData(String row_id, String date, String time, String weight) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_DATE, date);
        cv.put(COLUMN_TIME, time);
        cv.put(COLUMN_WEIGHT, weight);

        long result = db.update(TABLE_NAME, cv, "_id=?", new String[]{row_id});
        if (result == -1) {
            Toast.makeText(context, "Failed to Update!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Update Successful!", Toast.LENGTH_SHORT).show();
        }
    }

    // Deletes a specific weight record from the database (BME 10/16/2024)
    public void deleteOneRow(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLE_NAME, COLUMN_ID + "=?", new String[]{id});
        if (result == -1) {
            Toast.makeText(context, "Failed to Delete.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Successfully Deleted.", Toast.LENGTH_SHORT).show();
        }
    }

    // Deletes all data from the database and resets the autoincrement value (BME 10/16/2024)
    public boolean deleteAllData() {
        SQLiteDatabase db = getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_NAME, null, null);
        db.execSQL("DELETE FROM sqlite_sequence WHERE name='" + TABLE_NAME + "'");
        db.close();
        return rowsDeleted > 0;
    }

    // Retrieves the most recent weight value from the database (BME 10/16/2024)
    double getMostRecentWeight() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_WEIGHT + " FROM " + TABLE_NAME + " ORDER BY " + COLUMN_ID + " DESC LIMIT 1";
        Cursor cursor = db.rawQuery(query, null);
        double weight = 0.0;
        if (cursor != null && cursor.moveToFirst()) {
            try {
                weight = Double.parseDouble(cursor.getString(0));
            } catch (NumberFormatException e) {
                weight = 0.0;
            }
            cursor.close();
        }
        db.close();
        return weight;
    }

    // Retrieves the first weight value from the database (BME 10/16/2024)
    double getFirstWeight() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_WEIGHT + " FROM " + TABLE_NAME + " ORDER BY " + COLUMN_ID + " ASC LIMIT 1";
        Cursor cursor = db.rawQuery(query, null);
        double weight = 0.0;
        if (cursor != null && cursor.moveToFirst()) {
            try {
                weight = Double.parseDouble(cursor.getString(0));
            } catch (NumberFormatException e) {
                weight = 0.0;
            }
            cursor.close();
        }
        db.close();
        return weight;
    }

    // Calculates the progress towards the goal weight (BME 10/16/2024)
    int calculateProgress(double goalWeight) {
        double currentWeight = getMostRecentWeight();
        double firstWeight = getFirstWeight();

        if (goalWeight <= 0 || currentWeight <= 0 || firstWeight <= 0) {
            return 0;
        }

        int progress;
        if (currentWeight < goalWeight) { // User is trying to gain weight (BME 10/16/2024)
            progress = (int) ((currentWeight / goalWeight) * 100);
        } else { // User is trying to lose weight (BME 10/16/2024)
            double weightLost = firstWeight - currentWeight;
            double totalLossNeeded = firstWeight - goalWeight;
            progress = (int) ((weightLost / totalLossNeeded) * 100);
        }

        return Math.min(Math.max(progress, 0), 100); // Ensures progress is between 0 and 100 (BME 10/16/2024)
    }
}
