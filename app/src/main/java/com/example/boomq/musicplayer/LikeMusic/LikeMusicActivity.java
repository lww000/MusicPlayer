package com.example.boomq.musicplayer.LikeMusic;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.boomq.musicplayer.HistoryPlay.HistoryMusicAdapter;
import com.example.boomq.musicplayer.HistoryPlay.MusicSQLiteHelper;
import com.example.boomq.musicplayer.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by boomq on 2019/3/24.
 */

public class LikeMusicActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.like_music_layout);


        MusicSQLiteHelper mHelper=new MusicSQLiteHelper(this,"store_music.db",null,1);
        Cursor cursor=mHelper.query("select * from like_music",null);


        List<Map<String,Object>> list=new ArrayList<Map<String,Object>>();

        while (cursor.moveToNext()){
            Map<String,Object> map=new HashMap<String,Object>();
            String path=cursor.getString(cursor.getColumnIndex("path"));
            String singer=cursor.getString(cursor.getColumnIndex("singer"));
            String title=path.substring(path.lastIndexOf("/")+1,path.lastIndexOf("."));
            map.put("title",title);
            map.put("path",path);
            map.put("singer",singer);
            list.add(map);
        }
        cursor.close();

        RecyclerView likeMusicListView=(RecyclerView)findViewById(R.id.like_music_list);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        likeMusicListView.setLayoutManager(layoutManager);
        LikeMusicAdapter likeMusicAdapter=new LikeMusicAdapter(list);
        likeMusicListView.setAdapter(likeMusicAdapter);

    }
}
