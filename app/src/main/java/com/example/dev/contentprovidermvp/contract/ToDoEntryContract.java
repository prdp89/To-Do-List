package com.example.dev.contentprovidermvp.contract;

import android.support.annotation.Nullable;

import com.example.dev.contentprovidermvp.BasePresenter;
import com.example.dev.contentprovidermvp.BaseView;
import com.example.dev.contentprovidermvp.model.ToDo;

import java.util.Date;

/**
 * Created by M1040033 on 7/31/2017.
 */

public interface ToDoEntryContract {

    interface View extends BaseView<ToDoEntryContract.Presenter> {
        void initializeDefaultView();
        void finishActivity(Date timeValue);
        void showTimePicker();
        void showDatePicker();
    }

    interface Presenter extends BasePresenter {
        void saveEditNewTask(String titleText, Date timeValue, @Nullable String isEdited);
        String getToDoEntryID();
    }
}
