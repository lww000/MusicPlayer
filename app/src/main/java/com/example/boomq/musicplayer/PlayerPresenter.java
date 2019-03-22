package com.example.boomq.musicplayer;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * Created by boomq on 2019/3/21.
 */

public class PlayerPresenter extends PlayerContract.PlayerPresenter {
    private Context context;
    private PlayerContract.PlayerView view;
    private PlayerModel model;

    public PlayerPresenter(PlayerContract.PlayerView view,Context context){
        this.context=context;
        this.view=view;
        model=new PlayerModel(new PlayerCallback() {
            @Override
            public void onResult1() {
                PlayerPresenter.this.view.updateUI1();
            }

            @Override
            public void onResult2(MyMusic myMusic) {
                PlayerPresenter.this.view.updateUI2(myMusic);
            }
        },context);
    }


    @Override
    void stopUpdateSeekBarProgress() {
        model.stopUpdateSeekBarProgress();
    }

    @Override
    void startUpdateSeekBarProgress() {
        model.startUpdateSeekBarProgress();
    }

    @Override
    void queryMusicData() {
        model.queryMusicData();
    }

    @Override
    Bitmap getAlbumArt(int album_id) {
        return model.getAlbumArt(album_id);
    }
}
