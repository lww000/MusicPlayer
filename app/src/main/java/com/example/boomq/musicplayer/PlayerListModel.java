package com.example.boomq.musicplayer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.os.Message;
import android.os.Handler;
import android.provider.MediaStore;


/**
 * Created by boomq on 2019/3/17.
 */

public class PlayerListModel implements PlayerListContract.PlayerListModel {
    private Context mContext;
    private Callback1 callback1;
    public  PlayerListModel(Callback1 callback1,Context context){
        this.callback1=callback1;
        mContext=context;
    }

    //实现seekbar的动态更新
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            callback1.onResult1();
            updateProgress();
            return true;
        }
    });
    //每秒发送一个空的message，提示handler更新
    public void updateProgress() {
        handler.sendMessageDelayed(Message.obtain(),1000);
    }

    public void queryMusic(){
        //通过Cursor找出本地音乐文件
        Cursor cursor=mContext.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null,null,null,MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        if(cursor!=null){
            callback1.onResult2();
            while(cursor.moveToNext()){
                String id=cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                String musicName=cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                String singer=cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                String album=cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
                String path=cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                int time=cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                int size=cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));

                MyMusic myMusic=new MyMusic(id,musicName,singer,path,size,time,album);
                callback1.onResult3(myMusic);
            }
            callback1.onResult4();
        }
    }
 }

