package com.example.boomq.musicplayer;

import android.graphics.Bitmap;

interface PlayerContract {
    interface PlayerView extends BaseView{
        void updateUI1();
        void updateUI2(MyMusic myMusic);
    }
    interface PlayerModel extends BaseModel{

    }
    abstract class PlayerPresenter extends BasePresenter<PlayerView,PlayerModel>{
        abstract void stopUpdateSeekBarProgress();
        abstract void startUpdateSeekBarProgress();
        abstract void queryMusicData();
        abstract Bitmap getAlbumArt(int album_id);
    }
}
