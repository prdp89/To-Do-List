package com.example.dev.contentprovidermvp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by M1040033 on 7/22/2017.
 */

public final class ToDoContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private ToDoContract() {}

    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */
    public static final String CONTENT_AUTHORITY = "com.example.m1040033.contentprovidermvp";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Possible path (appended to base content URI for possible URI's)
     * For instance, content://com.example.android.m1040033.contentprovidermvp/todos/ is a valid path for
     * looking at todo data. content://com.example.android.m1040033.contentprovidermvp/staff/ will fail,
     * as the ContentProvider hasn't been given any information on what to do with "staff".
     */
    static final String PATH_TODOS = "todos";

    /**
     * Inner class that defines constant values for the todos database table.
     * Each entry in the table represents a single todo.
     */

    public static final class ToDoEntry implements BaseColumns {

        /** The content URI to access the todo data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_TODOS);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of todos.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TODOS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single todo.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TODOS;

        /** Name of database table for todo */
        public final static String TABLE_NAME = "todos";

        /**
         * Unique ID number for the todo (only for use in the database table).
         *
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Unique ID number for the todoEntry
         *
         * Type: INTEGER
         */
        public static final String COLUMN_NAME_ENTRY_ID = "entryId";

        /**
         * Name of the todo.
         *
         * Type: TEXT
         */
        public final static String COLUMN_TODO_NAME ="title";

        /**
         * Long Time value of the todo in milliseconds.
         *
         * Type: TEXT
         */
        public final static String COLUMN_TODO_TIME = "time";
    }
}
