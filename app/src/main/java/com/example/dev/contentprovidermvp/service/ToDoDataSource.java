package com.example.dev.contentprovidermvp.service;

import android.support.annotation.Nullable;

import com.example.dev.contentprovidermvp.model.ToDo;

import java.util.List;

/**
 * Created by M1040033 on 7/24/2017.
 */

public interface ToDoDataSource {

    @Nullable
    List<ToDo> getTasks();

    @Nullable
    void createTask(ToDo newTask, InsertDataCallBack callBack);

    @Nullable
    void updateTask(ToDo newTask, InsertDataCallBack callBack);

    @Nullable
    void deleteTask(ToDo deleteTask, InsertDataCallBack callBack);

    @Nullable
    void deleteCompletedTask(List<String> completedToDos);

    interface InsertDataCallBack<T> {
        void dataChanged(T t);
    }
}
