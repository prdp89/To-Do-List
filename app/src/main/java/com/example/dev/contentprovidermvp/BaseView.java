package com.example.dev.contentprovidermvp;

/**
 * Created by M1034284 on 4/25/2017.
 */

public interface BaseView<T> {

    void setPresenter(T presenter);

    void triggerManualSync();

}
