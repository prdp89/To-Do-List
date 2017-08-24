package com.example.dev.contentprovidermvp.presenter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.example.dev.contentprovidermvp.adapter.ToDoListAdapter;
import com.example.dev.contentprovidermvp.contract.TaskListContract;
import com.example.dev.contentprovidermvp.data.ToDoContract;
import com.example.dev.contentprovidermvp.loaders.ToDoListLoader;
import com.example.dev.contentprovidermvp.model.ToDo;
import com.example.dev.contentprovidermvp.service.ToDoRepository;
import com.example.dev.contentprovidermvp.view.ToDoListFragment;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by M1040033 on 7/24/2017.
 */

public class ToDoListPresenter implements TaskListContract.Presenter,
        LoaderManager.LoaderCallbacks<List<ToDo>> {

    private final static int TASKS_QUERY = 1;

    private TaskListContract.View mToDoListView;
    private LoaderManager mLoaderManager;
    private ToDoRepository mRepository;
    private ToDoListLoader mToDoListLoader;
    private Context mContext;
    private boolean isResetAllowed;

    public ToDoListPresenter(@NonNull TaskListContract.View todoListView, @NonNull LoaderManager loaderManager
            , ToDoRepository repository, @NonNull ToDoListFragment toDoListFragment) {

        mToDoListView = checkNotNull(todoListView, "todoListView cannot be null");
        mLoaderManager = checkNotNull(loaderManager, "loader manager cannot be null");
        mRepository = checkNotNull(repository, "repository cannot be null");
        // mToDoListLoader = checkNotNull(toDoListLoader, "Custom list loader cannot be null");
        mContext = toDoListFragment.getContext();

        mToDoListView.setPresenter(this);
    }

    //region BasePresenter Interface implementation
    @Override
    public void start() {
        mToDoListView.initializeDefaultView();

        if (isResetAllowed)
            mLoaderManager.restartLoader(TASKS_QUERY, null, this);
        else
            mLoaderManager.initLoader(TASKS_QUERY, null, this);

    }
    //endregion

    @Override
    public void addNewTask() {
        mToDoListView.showAddTask();
    }

    @Override
    public void resetLoader(boolean loaderStatus) {
        isResetAllowed = loaderStatus;
    }

    @Override
    public void clearCompletedToDo() {
        List<ToDo> toDoList = getUpdatedList();
        List<String> completedToDos = new ArrayList<>();

        for (ToDo toDo : toDoList) {
            if (toDo.getmToDoDate() != null && System.currentTimeMillis() > toDo.getmToDoDate().getTime())
                completedToDos.add(toDo.getmId());
        }

        if (null != completedToDos && completedToDos.size() > 0)
            mRepository.deleteCompletedTask(completedToDos);
    }

    @Override
    public List<ToDo> getUpdatedList() {
        return mRepository.getTasks();
    }

    //region Loader Manager Callbacks
    @Override
    public Loader<List<ToDo>> onCreateLoader(int id, Bundle args) {
        return new ToDoListLoader(mContext, mRepository);
    }

    @Override
    public void onLoadFinished(Loader<List<ToDo>> loader, List<ToDo> data) {
        mToDoListView.showToDoListing(data);
    }

    @Override
    public void onLoaderReset(Loader<List<ToDo>> loader) {

    }
    //endregion
}
