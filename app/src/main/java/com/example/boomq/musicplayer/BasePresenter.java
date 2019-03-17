package com.example.boomq.musicplayer;

import android.support.annotation.UiThread;

/**
 * Created by boomq on 2019/3/16.
 */

public abstract class BasePresenter<V extends BaseView,M extends BaseModel>{
    protected V view;
    protected M model;

    public BasePresenter(){
    }

    void attachView(V view){
        this.view=view;
    }
    void detachView(){
        this.view=null;
    }
}
interface BaseView{
}
interface BaseModel{
}
