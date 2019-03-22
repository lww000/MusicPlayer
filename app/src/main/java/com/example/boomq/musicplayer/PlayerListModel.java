package com.example.boomq.musicplayer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Message;
import android.os.Handler;
import android.provider.MediaStore;


/**
 * Created by boomq on 2019/3/17.
 */

public class PlayerListModel implements PlayerListContract.PlayerListModel {
    private Context mContext;
    private PlayerListCallback playerListCallback=null;
    public  PlayerListModel(PlayerListCallback playerListCallback, Context context){
        this.playerListCallback = playerListCallback;
        mContext=context;
    }

    //实现seekbar的动态更新
    @SuppressLint("HandlerLeak")
    Handler  mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
                playerListCallback.onResult1();
                updateProgress();
            return true;
        }
    });


    //每秒发送一个空的message，提示handler更新
    public void updateProgress() {
        mHandler.sendMessageDelayed(Message.obtain(),1000);
    }

    public void queryMusic(){
        //通过Cursor找出本地音乐文件
        Cursor cursor=mContext.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null,null,null,MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        if(cursor!=null){

            playerListCallback.onResult2();

            while(cursor.moveToNext()){
                String id=cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                String musicName=cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                String singer=cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                String album=cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
                String path=cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                int time=cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                int size=cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));
                int albumId=cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));

                MyMusic myMusic=new MyMusic(id,musicName,singer,path,size,time,album,getAlbumArt(albumId),albumId);
                playerListCallback.onResult3(myMusic);

            }

                playerListCallback.onResult4();

        }
    }

    private Bitmap getAlbumArt(int album_id){
        String mUriAlbums="content://media/external/audio/albums";
        String[] projection=new String[]{"album_art"};
        Cursor cur=mContext.getContentResolver().query(Uri.parse(mUriAlbums+"/"+Integer.toString(album_id)),projection,null,null,null);
        String album_art=null;
        if(cur.getCount()>0&&cur.getColumnCount()>0){
            cur.moveToNext();
            album_art=cur.getString(0);
        }
        cur.close();
        Bitmap mBitmap=null;
        if(album_art!=null){
            mBitmap= BitmapFactory.decodeFile(album_art);
        }
        else{
            mBitmap=BitmapFactory.decodeResource(mContext.getResources(),R.mipmap.unknown_music);
        }
        return mBitmap;
    }
 }

