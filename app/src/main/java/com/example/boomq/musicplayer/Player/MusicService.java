package com.example.boomq.musicplayer.Player;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import com.example.boomq.musicplayer.MyMusic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by boomq on 2019/3/21.
 */

public class MusicService extends Service implements MediaPlayer.OnCompletionListener {

    //操作指令
    public static final String ACTION_OPT_MUSIC_PLAY="ACTION_OPT_MUSIC_PLAY";
    public static final String ACTION_OPT_MUSIC_PAUSE="ACTION_OPT_MUSIC_PAUSE";
    public static final String ACTION_OPT_MUSIC_NEXT="ACTION_OPT_MUSIC_NEXT";
    public static final String ACTION_OPT_MUSIC_LAST="ACTION_OPT_MUSIC_LAST";
    public static final String ACTION_OPT_MUSIC_SEEK_TO="ACTION_OPT_MUSIC_SEEK_TO";

    //状态指令
    public static final String ACTION_STATUS_MUSIC_PLAY="ACTION_STATUS_MUSIC_PLAY";
    public static final String ACTION_STATUS_MUSIC_PAUSE="ACTION_STATUS_MUSIC_PAUSE";
    public static final String ACTION_STATUS_MUSIC_COMPLETE="ACTION_STATUS_MUSIC_COMPLETE";
    public static final String ACTION_STATUS_MUSIC_DURATION="ACTION_STATUS_MUSIC_DURATION";

    public static final String PARAM_MUSIC_DURATION="PARAM_MUSIC_DURATION";
    public static final String PARAM_MUSIC_SEEK_TO="PARAM_MUSIC_SEEK_TO";
    public static final String PARAM_MUSIC_CURRENT_POSITION="PARAM_MUSIC_CURRENT_POSITION";
    public static final String PARAM_MUSIC_IS_OVER="PARAM_MUSIC_IS_OVER";

    private int currentMusicIndex=0;
    private boolean isMusicPause=false;
    private List<MyMusic> mMusicDatas=new ArrayList<>();

    private MusicReceiver mMusicReceiver=new MusicReceiver();
    private MediaPlayer mMediaPlayer=new MediaPlayer();




    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent,int flags,int startId){
        initMusicDatas(intent);
        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    public void onCreate(){
        super.onCreate();
        initBroadCastReceiver();
    }

    private void initMusicDatas(Intent intent){
        if(intent==null)return;
        List<MyMusic> musicDatas=(List<MyMusic>)intent.getSerializableExtra(PlayerActivity.PARAM_MUSIC_LIST);
        mMusicDatas.addAll(musicDatas);
    }


    private void initBroadCastReceiver(){
        IntentFilter intentFilter=new IntentFilter();

        intentFilter.addAction(ACTION_OPT_MUSIC_PLAY);
        intentFilter.addAction(ACTION_OPT_MUSIC_PAUSE);
        intentFilter.addAction(ACTION_OPT_MUSIC_NEXT);
        intentFilter.addAction(ACTION_OPT_MUSIC_LAST);
        intentFilter.addAction(ACTION_OPT_MUSIC_SEEK_TO);

        LocalBroadcastManager.getInstance(this).registerReceiver(mMusicReceiver,intentFilter);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mMediaPlayer.release();
        mMediaPlayer=null;
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMusicReceiver);
    }


    private void play(final int index){
        if(index>=mMusicDatas.size())return;
        if(currentMusicIndex==index&& isMusicPause){
            mMediaPlayer.start();
        }
        else{
            mMediaPlayer.stop();
            if(mMediaPlayer==null){
                mMediaPlayer=new MediaPlayer();
            }
            else{
                mMediaPlayer.reset();
            }

            try {

                mMediaPlayer.setOnCompletionListener(this);
//                mMediaPlayer.reset();
                mMediaPlayer.setDataSource(mMusicDatas.get(index).getPath());
                mMediaPlayer.prepare();
                mMediaPlayer.start();

                currentMusicIndex=index;
                isMusicPause=false;

                int duration=mMediaPlayer.getDuration();
                sendMusicDurationBroadCast(duration);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        sendMusicStatusBroadCast(ACTION_STATUS_MUSIC_PLAY);
    }

    private void pause(){
        mMediaPlayer.pause();
        isMusicPause=true;
        sendMusicStatusBroadCast(ACTION_STATUS_MUSIC_PAUSE);
    }


    private void stop(){
        mMediaPlayer.stop();
    }


    private void next(){
        if(currentMusicIndex+1<mMusicDatas.size()){
            play(currentMusicIndex+1);
        }
        else{
            stop();
        }
    }

    private void last(){
        if(currentMusicIndex!=0){
            play(currentMusicIndex-1);
        }
    }


    private void seekTo(Intent intent){
        if(mMediaPlayer.isPlaying()){
            int position=intent.getIntExtra(PARAM_MUSIC_SEEK_TO,0);
            mMediaPlayer.seekTo(position);
        }
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        sendMusicCompleteBroadCast();
    }

    class MusicReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String action=intent.getAction();
            if(action.equals(ACTION_OPT_MUSIC_PLAY)){
                play(currentMusicIndex);
            }
            else if(action.equals(ACTION_OPT_MUSIC_PAUSE)){
                pause();
            }
            else if(action.equals(ACTION_OPT_MUSIC_LAST)){
                last();
            }
            else if(action.equals(ACTION_OPT_MUSIC_NEXT)){
                next();
            }
            else if(action.equals(ACTION_OPT_MUSIC_SEEK_TO)){
                seekTo(intent);
            }
        }
    }


    private void sendMusicCompleteBroadCast(){
        Intent intent=new Intent(ACTION_STATUS_MUSIC_COMPLETE);
        intent.putExtra(PARAM_MUSIC_IS_OVER,(currentMusicIndex==mMusicDatas.size()-1));
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void sendMusicDurationBroadCast(int duration){
        Intent intent=new Intent(ACTION_STATUS_MUSIC_DURATION);
        intent.putExtra(PARAM_MUSIC_DURATION,duration);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void sendMusicStatusBroadCast(String action){
        Intent intent=new Intent(action);
        if(action.equals(ACTION_STATUS_MUSIC_PLAY)){
            intent.putExtra(PARAM_MUSIC_CURRENT_POSITION,mMediaPlayer.getCurrentPosition());
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }


}
