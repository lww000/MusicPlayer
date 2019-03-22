package com.example.boomq.musicplayer.PlayerList;

import com.example.boomq.musicplayer.MyMusic;

/**
 * Created by boomq on 2019/3/17.
 */

public interface PlayerListCallback {
    void onResult1();
    void onResult2();
    void onResult3(MyMusic myMusic);
    void onResult4();
}
