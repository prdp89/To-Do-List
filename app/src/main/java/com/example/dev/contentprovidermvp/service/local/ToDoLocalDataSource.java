package com.example.dev.contentprovidermvp.service.local;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.example.dev.contentprovidermvp.data.ToDoContract;
import com.example.dev.contentprovidermvp.data.ToDoDbHelper;
import com.example.dev.contentprovidermvp.model.ToDo;
import com.example.dev.contentprovidermvp.service.ToDoDataSource;
import com.example.dev.contentprovidermvp.utils.AppUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by M1040033 on 7/25/2017.
 */

public class ToDoLocalDataSource implements ToDoDataSource {

    private static ToDoLocalDataSource INSTANCE;
    private Context mContext;

    private ToDoDbHelper mDbHelper;
    private SQLiteDatabase mDb;

    private ToDoLocalDataSource(@NonNull Context context) {
        mContext = context;
    }

    /**
     * Returns the single instance of this class, creating it if necessary.
     *
     * @param context the device storage data source
     * @return the {@link ToDoLocalDataSource} instance
     */
    public static ToDoLocalDataSource getInstance(@NonNull Context context) {
        if (INSTANCE == null) {
            INSTANCE = new ToDoLocalDataSource(context);
        }
        return INSTANCE;
    }

    @Nullable
    @Override
    public List<ToDo> getTasks() {
        List<ToDo> tasks = new ArrayList<ToDo>();

        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                ToDoContract.ToDoEntry.COLUMN_NAME_ENTRY_ID,
                ToDoContract.ToDoEntry.COLUMN_TODO_NAME,
                ToDoContract.ToDoEntry.COLUMN_TODO_TIME
        };

        Cursor c = mContext.getContentResolver().query(ToDoContract.ToDoEntry.CONTENT_URI,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);

        if (c != null && c.getCount() > 0) {
            while (c.moveToNext()) {
                String itemId = c
                        .getString(c.getColumnIndexOrThrow(ToDoContract.ToDoEntry.COLUMN_NAME_ENTRY_ID));
                String title = c
                        .getString(c.getColumnIndexOrThrow(ToDoContract.ToDoEntry.COLUMN_TODO_NAME));
                long dateValue =
                        c.getLong(c.getColumnIndexOrThrow(ToDoContract.ToDoEntry.COLUMN_TODO_TIME));

                String dateString = "";
                if (dateValue > 0)
                    dateString = AppUtils.convertLongToDateString(dateValue);

                ToDo task = new ToDo(itemId, title, dateString, dateValue > 0 ? new Date(dateValue) : null);
                tasks.add(task);
            }
        }
        if (c != null) {
            c.close();
        }

        return tasks;
    }

    @Nullable
    @Override
    public void createTask(ToDo newTask, InsertDataCallBack callBack) {
        try {
            checkNotNull(newTask);

            ContentValues values = new ContentValues();
            values.put(ToDoContract.ToDoEntry.COLUMN_NAME_ENTRY_ID, newTask.getmId());
            values.put(ToDoContract.ToDoEntry.COLUMN_TODO_NAME, newTask.getmTitle());
            values.put(ToDoContract.ToDoEntry.COLUMN_TODO_TIME, newTask.getmToDoDate() != null ? newTask.getmToDoDate().getTime() : 0f);

            Uri uri = mContext.getContentResolver().insert(ToDoContract.ToDoEntry.CONTENT_URI, values);
            if (uri != null) {
                callBack.dataChanged(ContentUris.parseId(uri));
            }
        } catch (IllegalStateException ex) {
        }
    }

    @Nullable
    @Override
    public void updateTask(ToDo newTask, InsertDataCallBack callBack) {
        try {
            checkNotNull(newTask);

            ContentValues values = new ContentValues();
            values.put(ToDoContract.ToDoEntry.COLUMN_TODO_NAME, newTask.getmTitle());
            values.put(ToDoContract.ToDoEntry.COLUMN_TODO_TIME, newTask.getmToDoDate() != null ? newTask.getmToDoDate().getTime() : 0f);

            String selection = ToDoContract.ToDoEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";
            String[] selectionArgs = {newTask.getmId()};

            int id = mContext.getContentResolver().update(ToDoContract.ToDoEntry.CONTENT_URI, values, selection, selectionArgs);
            if (id != -1) {
                callBack.dataChanged(id);
            }
        } catch (IllegalStateException ex) {
        }
    }

    @Nullable
    @Override
    public void deleteTask(ToDo deleteTask, InsertDataCallBack callBack) {
        try {
            checkNotNull(deleteTask);

            String selection = ToDoContract.ToDoEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";
            String[] selectionArgs = {deleteTask.getmId()};

            int id = mContext.getContentResolver().delete(ToDoContract.ToDoEntry.CONTENT_URI, selection, selectionArgs);
            if (id != -1) {
                callBack.dataChanged(id);
            }
        } catch (IllegalStateException ex) {
        }
    }

    @Nullable
    @Override
    public void deleteCompletedTask(List<String> completedToDos) {

        try {
            checkNotNull(completedToDos);

            String whereClause = String.format(ToDoContract.ToDoEntry.COLUMN_NAME_ENTRY_ID + " in (%s)", new Object[]{TextUtils.join(",", Collections.nCopies(completedToDos.size(), "?"))});

            mContext.getContentResolver().delete(ToDoContract.ToDoEntry.CONTENT_URI, whereClause, completedToDos.toArray(new String[0]));

        } catch (IllegalStateException ex) {
        }
    }
}
