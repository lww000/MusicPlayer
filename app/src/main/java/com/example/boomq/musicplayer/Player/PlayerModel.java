package com.example.boomq.musicplayer.Player;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;

import com.example.boomq.musicplayer.HistoryPlay.MusicSQLiteHelper;
import com.example.boomq.musicplayer.MyMusic;
import com.example.boomq.musicplayer.Player.PlayerCallback;
import com.example.boomq.musicplayer.Player.PlayerContract;
import com.example.boomq.musicplayer.R;

/**
 * Created by boomq on 2019/3/21.
 */

public class PlayerModel implements PlayerContract.PlayerModel{
        private Context mContext;
        private PlayerCallback playerCallback=null;
        public PlayerModel(PlayerCallback playerCallback,Context context){
            this.playerCallback=playerCallback;
            mContext=context;
        }

        //实现seekbar的动态更新
        @SuppressLint("HandlerLeak")
        Handler mHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                playerCallback.onResult1();
                startUpdateSeekBarProgress();
                return true;
            }
        });

        public void stopUpdateSeekBarProgress(){
            mHandler.removeMessages(0);
        }
        public void startUpdateSeekBarProgress(){
            //避免重复发送message
            stopUpdateSeekBarProgress();
            mHandler.sendEmptyMessageDelayed(0,1000);
        }


        public void queryMusicData(){
            //通过Cursor找出本地音乐文件
            Cursor cursor=mContext.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null,null,null,MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
            if(cursor!=null){
                while(cursor.moveToNext()){
                    String id=cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                    int musicRes=cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                    String musicName=cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                    String singer=cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                    String album=cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
                    String path=cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                    int time=cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                    int size=cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));
                    int albumId=cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));

                    MyMusic myMusic=new MyMusic(id,musicName,singer,path,size,time,album,albumId,musicRes);

                    playerCallback.onResult2(myMusic);

                }

            }
        }

    public Bitmap getAlbumArt(int album_id){
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
            mBitmap=BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.unknown_music);
        }
        return mBitmap;
    }

//    public void onWritePlayLog(String path, long time, MusicSQLiteHelper mHelper){
//        String sql="select*from playhistory where data=?";
//        Cursor cursor=mHelper.query(sql,new String[]{path});
//        if(!cursor.moveToFirst()){
//            ContentValues values=new ContentValues();
//            values.put("data",path);
//            values.put("playdate",time);
//            mHelper.insert("playhistory",values);
//            Log.i("TAG", "log insert ok! ");
//        }
//        else{
//            sql="update playhistory set playdate=? where data=?";
//            mHelper.update(sql,new String[]{String.valueOf(time),path});
//            Log.i("TAG", "log update ok! ");
//        }
//        cursor.close();
//    }

}


