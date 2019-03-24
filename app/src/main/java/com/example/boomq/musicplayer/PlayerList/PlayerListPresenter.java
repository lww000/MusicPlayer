package com.example.boomq.musicplayer.PlayerList;

import android.content.Context;

import com.example.boomq.musicplayer.HistoryPlay.MusicSQLiteHelper;
import com.example.boomq.musicplayer.MyMusic;

/**
 * Created by boomq on 2019/3/17.
 */

public class PlayerListPresenter extends PlayerListContract.PlayerListPresenter {
    private Context context;
    private PlayerListContract.PlayerListView view;
    private PlayerListModel model;

    public PlayerListPresenter(PlayerListContract.PlayerListView view,Context context){
        this.context=context;
        this.view=view;
        model=new PlayerListModel(new PlayerListCallback() {
            @Override
            public void onResult1() {
                PlayerListPresenter.this.view.updateUI1();
            }

            @Override
            public void onResult2() {
                PlayerListPresenter.this.view.updateUI2();
            }

            @Override
            public void onResult3(MyMusic myMusic) {
                PlayerListPresenter.this.view.updateUI3(myMusic);

            }

            @Override
            public void onResult4() {
                PlayerListPresenter.this.view.updateUI4();

            }
        },context);
    }

    @Override
    void updateProgress() {
        model.updateProgress();
    }

    @Override
    void queryMusic() {
       model.queryMusic();
    }

    @Override
    void onWritePlayLog(String path, long time, MusicSQLiteHelper mHelper) {
        model.onWritePlayLog(path, time, mHelper);
    }
}
