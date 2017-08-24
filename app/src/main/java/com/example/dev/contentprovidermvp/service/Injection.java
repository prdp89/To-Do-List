package com.example.dev.contentprovidermvp.service;

import android.content.Context;
import android.support.annotation.NonNull;

import com.example.dev.contentprovidermvp.service.local.ToDoLocalDataSource;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by M1040033 on 7/24/2017.
 */

public class Injection {

    public static ToDoRepository provideTasksRepository(@NonNull Context context) {
        checkNotNull(context);
        return ToDoRepository.getInstance(ToDoLocalDataSource.getInstance(context));
    }
}
