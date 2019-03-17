package com.example.boomq.musicplayer;


import javax.security.auth.callback.Callback;

/**
 * Created by boomq on 2019/3/16.
 */

interface PlayerListContract {

    interface PlayerListView extends BaseView{
        void updateUI1();
        void updateUI2();
        void updateUI3(MyMusic myMusic);
        void updateUI4();
    }
    interface PlayerListModel extends BaseModel{
    }
    abstract class PlayerListPresenter extends BasePresenter<PlayerListView,PlayerListModel>{
        abstract void updateProgress();
        abstract void quertMusic();
    }
}
