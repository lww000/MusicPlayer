package com.example.boomq.musicplayer.Player;

import com.example.boomq.musicplayer.MyMusic;

/**
 * Created by boomq on 2019/3/21.
 */

public interface PlayerCallback {
    void onResult1();
    void onResult2(MyMusic myMusic);
}
