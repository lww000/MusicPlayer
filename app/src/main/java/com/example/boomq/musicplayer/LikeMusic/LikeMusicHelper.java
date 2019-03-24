package com.example.boomq.musicplayer.LikeMusic;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by boomq on 2019/3/23.
 */

public class LikeMusicHelper extends SQLiteOpenHelper{


    private final String CREATE_TABLE_LIKE_MUSIC="create table like_music("+"_id integer primary key autoincrement,title,singer,duration,path)";


    public LikeMusicHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_LIKE_MUSIC);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("drop table if exists like_music");
        onCreate(sqLiteDatabase);
    }
}
