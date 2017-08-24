package com.example.dev.contentprovidermvp.data;

/**
 * Created by M1040033 on 7/22/2017.
 */

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.dev.contentprovidermvp.data.ToDoContract.ToDoEntry;

/**
 * {@link ContentProvider} for TODO app.
 */
public class ToDoProvider extends ContentProvider {

    /** Tag for the log messages */
    public static final String LOG_TAG = ToDoProvider.class.getSimpleName();

    /** URI matcher code for the content URI for the todo table */
    private static final int TODOS = 100;

    /** URI matcher code for the content URI for a single todo in the todos table */
    private static final int TODO_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        // The content URI of the form "content://com.example.android.TODOS/TODOS" will map to the
        // integer code {@link #TODOS}. This URI is used to provide access to MULTIPLE rows
        // of the TODOS table.
        sUriMatcher.addURI(ToDoContract.CONTENT_AUTHORITY, ToDoContract.PATH_TODOS, TODOS);

        // The content URI of the form "content://com.example.android.TODOS/TODOS/#" will map to the
        // integer code {@link #TODO_ID}. This URI is used to provide access to ONE single row
        // of the TODOS table.
        //
        // In this case, the "#" wildcard is used where "#" can be substituted for an integer.
        // For example, "content://com.example.android.TODOS/TODOS/3" matches, but
        // "content://com.example.android.TODOS/TODOS" (without a number at the end) doesn't match.
        sUriMatcher.addURI(ToDoContract.CONTENT_AUTHORITY, ToDoContract.PATH_TODOS + "/#", TODO_ID);
    }

    /** Database helper object */
    private ToDoDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new ToDoDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case TODOS:
                // For the TODOS code, query the TODOS table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the TODOS table.
                cursor = database.query(ToDoEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            case TODO_ID:
                // For the TODO_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.TODOS/TODOS/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = ToDoEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // This will perform a query on the TODOS table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(ToDoEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        // Set notification URI on the Cursor,
        // so we know what content URI the Cursor was created for.
        // If the data at this URI changes, then we know we need to update the Cursor.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return the cursor
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TODOS:
                return insertToDo(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a ToDo into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertToDo(Uri uri, ContentValues values) {

        try {
            // Check that the name is not null
            String name = values.getAsString(ToDoEntry.COLUMN_TODO_NAME);
            if (name == null) {
                throw new IllegalArgumentException("TODO requires a name");
            }

            //no need to check for TODO time since we have option to set it null or empty

            // Get writeable database
            SQLiteDatabase database = mDbHelper.getWritableDatabase();

            // Insert the new todo with the given values
            long id = database.insertOrThrow(ToDoEntry.TABLE_NAME, null, values);
            // If the ID is -1, then the insertion failed. Log an error and return null.
            if (id == -1) {
                Log.e(LOG_TAG, "Failed to insert row for " + uri);
                return null;
            }

            // Notify all listeners that the data has changed for the todo content URI
            getContext().getContentResolver().notifyChange(uri, null);

            // Return the new URI with the ID (of the newly inserted row) appended at the end
            return ContentUris.withAppendedId(uri, id);
        }
        catch (Exception ex)
        {

        }
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Track the number of rows that were deleted
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TODOS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(ToDoEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TODO_ID:
                // Delete a single row given by the ID in the URI
                selection = ToDoEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(ToDoEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows deleted
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TODOS:
                return updateTODo(uri, contentValues, selection, selectionArgs);
            case TODO_ID:
                // For the TODO_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = ToDoEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateTODo(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Update todos in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more todo).
     * Return the number of rows that were successfully updated.
     */
    private int updateTODo(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // If the {@link TODoEntry#COLUMN_ToDo_NAME} key is present,
        // check that the name value is not null.
        if (values.containsKey(ToDoEntry.COLUMN_TODO_NAME)) {
            String name = values.getAsString(ToDoEntry.COLUMN_TODO_NAME);
            if (name == null) {
                throw new IllegalArgumentException("TODO requires a name");
            }
        }

        // No need to check the TODO time, any value is valid (including null).

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writeable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(ToDoEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows updated
        return rowsUpdated;
    }
}
