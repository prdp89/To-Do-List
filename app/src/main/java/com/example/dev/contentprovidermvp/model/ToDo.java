package com.example.dev.contentprovidermvp.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by M1040033 on 7/24/2017.
 */

public class ToDo implements Serializable {

    @NonNull
    private String mId = null;

    @NonNull
    private String mTitle = null;

    @NonNull
    public Date getmToDoDate() {
        return mToDoDate;
    }

    @NonNull
    private Date mToDoDate = null;

    @NonNull
    public String getmId() {
        return mId;
    }

    @NonNull
    public String getmTitle() {
        return mTitle;
    }

    @Nullable
    public String getmTime() {
        return mTime;
    }

    @Nullable
    private String mTime = null;

    private boolean isEdited;

    public boolean isEdited() {
        return isEdited;
    }

    public void setEdited(boolean edited) {
        isEdited = edited;
    }


    /**
     * Use this constructor to create / update a Task.
     *
     * @param id          id of the task
     * @param title       title of the task
     * @param time        time of the task
     */
    public ToDo(@Nullable Date toDoDate, @Nullable String title, @Nullable String id) {
        mToDoDate = toDoDate;
        mId = id;
        mTitle = title;
    }

    /**
     * Use this constructor to get a Task.
     *
     * @param id          id of the task
     * @param title       title of the task
     * @param time        time of the task
     */
    public ToDo(@NonNull String id, @NonNull String title, @Nullable String time, @Nullable Date date) {
        mId = id;
        mTitle = title;
        mTime = time;
        mToDoDate = date;
    }

    @Override
    public String toString() {
        return mTitle;
    }
}
