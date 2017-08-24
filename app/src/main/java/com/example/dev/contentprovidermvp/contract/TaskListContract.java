package com.example.dev.contentprovidermvp.contract;

import com.example.dev.contentprovidermvp.BasePresenter;
import com.example.dev.contentprovidermvp.BaseView;
import com.example.dev.contentprovidermvp.model.ToDo;

import java.util.List;

/**
 * Created by M1040033 on 7/23/2017.
 */

public interface TaskListContract {

    interface View extends BaseView<Presenter> {
        void initializeDefaultView();
        void showAddTask();
        void showToDoListing(List<ToDo> toDoList);
    }

    interface Presenter extends BasePresenter {
        void addNewTask();
        void resetLoader(boolean loderStatus);
        void clearCompletedToDo();
        List<ToDo> getUpdatedList();
    }
}
