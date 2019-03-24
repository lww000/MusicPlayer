package com.example.boomq.musicplayer.LrcView;

import java.util.List;

/**
 * Created by boomq on 2019/3/24.
 */

public interface ILrcBuilder {
    List<LrcRow> getLrcRows(String rawLrc);
}
