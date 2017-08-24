package com.example.dev.contentprovidermvp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.dev.contentprovidermvp.data.ToDoContract.ToDoEntry;

/**
 * Created by M1040033 on 7/22/2017.
 */

public class ToDoDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = ToDoDbHelper.class.getSimpleName();

    /** Name of the database file */
    private static final String DATABASE_NAME = "todoContainer.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@link ToDoDbHelper}.
     *
     * @param context of the app
     */

    public ToDoDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        try {
            // Create a String that contains the SQL statement to create the todos table
            String SQL_CREATE_TODO_TABLE =  "CREATE TABLE IF NOT EXISTS "
                    + ToDoEntry.TABLE_NAME + " ( "
                    + ToDoEntry._ID + " TEXT PRIMARY KEY, "
                    + ToDoEntry.COLUMN_NAME_ENTRY_ID + " TEXT, "
                    + ToDoEntry.COLUMN_TODO_NAME + " TEXT NOT NULL, "
                    + ToDoEntry.COLUMN_TODO_TIME + " BIGINTEGER "
                    + " );";

            // Execute the SQL statement
            sqLiteDatabase.execSQL(SQL_CREATE_TODO_TABLE);
        }
        catch (Exception ex)
        {}
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int olderVersion, int newerVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ToDoEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
