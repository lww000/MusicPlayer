package com.example.boomq.musicplayer.Player;

import android.graphics.Bitmap;

import com.example.boomq.musicplayer.Base.BaseModel;
import com.example.boomq.musicplayer.Base.BasePresenter;
import com.example.boomq.musicplayer.Base.BaseView;
import com.example.boomq.musicplayer.MyMusic;

interface PlayerContract {
    interface PlayerView extends BaseView {
        void updateUI1();
        void updateUI2(MyMusic myMusic);
    }
    interface PlayerModel extends BaseModel {

    }
    abstract class PlayerPresenter extends BasePresenter<PlayerView,PlayerModel> {
        abstract void stopUpdateSeekBarProgress();
        abstract void startUpdateSeekBarProgress();
        abstract void queryMusicData();
        abstract Bitmap getAlbumArt(int album_id);
    }
}
