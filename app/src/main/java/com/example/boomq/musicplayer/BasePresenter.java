package com.example.boomq.musicplayer;

import android.support.annotation.UiThread;

/**
 * Created by boomq on 2019/3/16.
 */

public interface BasePresenter<V extends BaseView,M extends BaseModel> {
    @UiThread
    void attachView(V view);
    @UiThread
    void detachView(boolean retainInstance);
}
interface BaseView{
}
interface BaseModel{
}
