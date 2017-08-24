package com.example.dev.contentprovidermvp.service;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.dev.contentprovidermvp.model.ToDo;

import java.util.List;

/**
 * Created by M1040033 on 7/24/2017.
 */

public class ToDoRepository implements ToDoDataSource {

    private static ToDoRepository INSTANCE = null;
    private final ToDoDataSource mTasksLocalDataSource;

    private ToDoRepository(@NonNull ToDoDataSource tasksLocalDataSource)
    {
        mTasksLocalDataSource = tasksLocalDataSource;
    }

    /**
     * Returns the single instance of this class, creating it if necessary.
     *
     * @param tasksLocalDataSource  the device storage data source
     * @return the {@link ToDoRepository} instance
     */
    public static ToDoRepository getInstance(ToDoDataSource tasksLocalDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new ToDoRepository(tasksLocalDataSource);
        }
        return INSTANCE;
    }

    @Nullable
    @Override
    public List<ToDo> getTasks() {
        return mTasksLocalDataSource.getTasks();
    }

    @Override
    public void createTask(ToDo newTask, InsertDataCallBack callBack) {
        mTasksLocalDataSource.createTask(newTask, callBack);
    }

    @Nullable
    @Override
    public void updateTask(ToDo newTask, InsertDataCallBack callBack) {
        mTasksLocalDataSource.updateTask(newTask, callBack);
    }

    @Nullable
    @Override
    public void deleteTask(ToDo deleteTask, InsertDataCallBack callBack) {
        mTasksLocalDataSource.deleteTask(deleteTask, callBack);
    }

    @Nullable
    @Override
    public void deleteCompletedTask(List<String> completedToDos) {
        mTasksLocalDataSource.deleteCompletedTask(completedToDos);
    }
}
