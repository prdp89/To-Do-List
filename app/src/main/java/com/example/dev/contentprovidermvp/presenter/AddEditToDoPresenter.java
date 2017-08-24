package com.example.dev.contentprovidermvp.presenter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.dev.contentprovidermvp.contract.TaskListContract;
import com.example.dev.contentprovidermvp.contract.ToDoEntryContract;
import com.example.dev.contentprovidermvp.data.ToDoContract;
import com.example.dev.contentprovidermvp.model.ToDo;
import com.example.dev.contentprovidermvp.service.ToDoDataSource;
import com.example.dev.contentprovidermvp.service.ToDoRepository;

import java.util.Date;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by M1040033 on 8/7/2017.
 */

public class AddEditToDoPresenter implements ToDoEntryContract.Presenter {

    private ToDoEntryContract.View mAddEditView;
    private ToDoRepository mRepository;
    private String mToDoEntryId;

    public AddEditToDoPresenter(@NonNull ToDoEntryContract.View todoEntryView, @NonNull ToDoRepository toDoRepository) {
        mAddEditView = checkNotNull(todoEntryView, "todoListView cannot be null");
        mRepository = checkNotNull(toDoRepository, "toDoRepo cannot be null");

        mAddEditView.setPresenter(this);
    }

    //region TaskListContract.Presenter implementation
    @Override
    public void start() {
        mAddEditView.initializeDefaultView();
    }

    @Override
    public void saveEditNewTask(String titleText, Date timeValue, @Nullable String isEdited) {

        boolean isEmpty = isEdited == null || isEdited.trim().length() == 0;
        if (isEmpty) {
            createTask(titleText, timeValue);
        } else
            updateTask(titleText, timeValue, isEdited);
    }

    private void updateTask(String titleText, final Date timeValue, String isEditedID) {
        mToDoEntryId = isEditedID;
        ToDo newTask = new ToDo(timeValue, titleText, isEditedID);
        mRepository.updateTask(newTask, new ToDoDataSource.InsertDataCallBack<Integer>() {
            @Override
            public void dataChanged(Integer isSuccess) {
                if (isSuccess != -1)
                    mAddEditView.finishActivity(timeValue);
            }
        });
    }

    @Override
    public String getToDoEntryID() {
        return mToDoEntryId;
    }

    //endregion

    private void createTask(String titleText, final Date timeValue) {

        mToDoEntryId = UUID.randomUUID().toString();

        ToDo newTask = new ToDo(timeValue, titleText, mToDoEntryId);

        mRepository.createTask(newTask, new ToDoDataSource.InsertDataCallBack<Long>() {
            @Override
            public void dataChanged(Long aLong) {
                mAddEditView.finishActivity(timeValue);
            }
        });
    }
}
