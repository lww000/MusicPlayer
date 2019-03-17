package com.example.boomq.musicplayer;


import javax.security.auth.callback.Callback;

/**
 * Created by boomq on 2019/3/16.
 */

interface PlayerListContract {

    interface PlayerListView extends BaseView{
        void updateUI1();
    }
    interface PlayerListModel extends BaseModel{
        void getData1(Callback1 callback1);
    }
    interface PlayerListPresenter extends BasePresenter<PlayerListView,PlayerListModel>{
        void request1();
    }
}
