package com.example.boomq.musicplayer.HistoryPlay;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.boomq.musicplayer.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by boomq on 2019/3/23.
 */

public class PlayHistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_history_layout);


        MusicSQLiteHelper mHelper=new MusicSQLiteHelper(this,"media.db",null,4);
        Cursor cursor=mHelper.query("select * from playhistory order by playdate desc",null);


        List<Map<String,Object>> list=new ArrayList<Map<String,Object>>();


        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        while (cursor.moveToNext()){
            Map<String,Object> map=new HashMap<String,Object>();
            String path=cursor.getString(cursor.getColumnIndex("data"));
            long time=cursor.getLong(cursor.getColumnIndex("playdate"));
            String title=path.substring(path.lastIndexOf("/")+1,path.lastIndexOf("."));
            map.put("title",title);
            map.put("path",path);
            map.put("playdate",simpleDateFormat.format(new Date(time)));
            list.add(map);
        }
        cursor.close();

        RecyclerView playhistoryListView=(RecyclerView)findViewById(R.id.play_history_list);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        playhistoryListView.setLayoutManager(layoutManager);
        HistoryMusicAdapter historyMusicAdapter=new HistoryMusicAdapter(list);
        playhistoryListView.setAdapter(historyMusicAdapter);



    }
}
