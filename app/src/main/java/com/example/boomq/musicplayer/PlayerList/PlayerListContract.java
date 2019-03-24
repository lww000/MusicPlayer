package com.example.boomq.musicplayer.PlayerList;


import com.example.boomq.musicplayer.Base.BaseModel;
import com.example.boomq.musicplayer.Base.BasePresenter;
import com.example.boomq.musicplayer.Base.BaseView;
import com.example.boomq.musicplayer.HistoryPlay.MusicSQLiteHelper;
import com.example.boomq.musicplayer.MyMusic;

/**
 * Created by boomq on 2019/3/16.
 */

interface PlayerListContract {

    interface PlayerListView extends BaseView {
        void updateUI1();
        void updateUI2();
        void updateUI3(MyMusic myMusic);
        void updateUI4();
    }
    interface PlayerListModel extends BaseModel {
    }
    abstract class PlayerListPresenter extends BasePresenter<PlayerListView,PlayerListModel> {
        abstract void updateProgress();
        abstract void queryMusic();
        abstract void onWritePlayLog(String path, long time, MusicSQLiteHelper mHelper);
    }
}
