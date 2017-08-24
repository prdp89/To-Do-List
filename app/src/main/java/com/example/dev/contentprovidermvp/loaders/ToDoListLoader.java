package com.example.dev.contentprovidermvp.loaders;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.AsyncTaskLoader;

import com.example.dev.contentprovidermvp.model.ToDo;
import com.example.dev.contentprovidermvp.service.ToDoRepository;

import java.util.List;

/**
 * Created by M1040033 on 7/24/2017.
 */

public class ToDoListLoader extends AsyncTaskLoader<List<ToDo>> {

    private Context mContext;
    private ToDoRepository mRepository;

    public ToDoListLoader(@NonNull Context context, @NonNull ToDoRepository repository) {
        super(context);

        mContext = context;
        mRepository = repository;
    }

    @Override
    public List<ToDo> loadInBackground()
    {
        return mRepository.getTasks();
    }

    @Override
    protected void onStartLoading()
    {
        forceLoad();
    }
}
