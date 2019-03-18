package com.example.boomq.musicplayer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.multidex.MultiDex;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.LitePal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;

/**
 * Created by boomq on 2019/3/16.
 */

public class PlayerListActivity extends AppCompatActivity implements View.OnClickListener,SeekBar.OnSeekBarChangeListener, MediaPlayer.OnCompletionListener,PlayerListContract.PlayerListView{
   private MediaPlayer mediaPlayer;
   private MusicAdapter musicAdapter;
   private List<MyMusic> musicList;
   private AppCompatSeekBar seekBar;
   private TextView timeStart,timeEnd;
   private int mPosition=-1;
   private Button playButton;
   private PlayerListPresenter presenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playerlist_layout);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.hide();
        }
        requestPermission();
        initView();
        presenter=new PlayerListPresenter(this,this);
        presenter.quertMusic();
    }

    private void requestPermission(){
        if(ContextCompat.checkSelfPermission(PlayerListActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(PlayerListActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,@NonNull int[] grantResults){
        if(requestCode==1){
            if(grantResults.length>0 && grantResults[0] !=PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"拒绝权限将无法使用",Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }

    private  void initView(){
        timeStart=findViewById(R.id.time_start);
        timeEnd=findViewById(R.id.time_end);
        seekBar=findViewById(R.id.seek_bar);
        seekBar.setOnSeekBarChangeListener(this);

        playButton=findViewById(R.id.music_play);
        Button lastButton=findViewById(R.id.last_music);
        Button nextButton=findViewById(R.id.next_music);

        playButton.setOnClickListener(this);
        lastButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);

        final RecyclerView musicListView=findViewById(R.id.music_list);
        musicList=new ArrayList<>();
        musicListView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        musicAdapter=new MusicAdapter(musicList);
        musicListView.setAdapter(musicAdapter);
        musicAdapter.setSelected(-1);
        musicAdapter.setOnItemClickListener(new MusicAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                changeMusic(position);
                mPosition=position;
            }
        });
        musicListView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
    }

    private void playMusic(MyMusic myMusic){
        try{
            if(mediaPlayer==null){//判断是否为空，避免重复创建
                mediaPlayer=new MediaPlayer();
                mediaPlayer.setOnCompletionListener(this);
            }
            mediaPlayer.reset();//播放前重置播放器
            mediaPlayer.setDataSource(myMusic.getPath());//设置播放源
            mediaPlayer.prepare();
            mediaPlayer.start();
            timeEnd.setText(parseDate(mediaPlayer.getDuration()));
            seekBar.setMax(mediaPlayer.getDuration());
            presenter.updateProgress();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void changeMusic(int position){
        if(position<0){
            mPosition=musicList.size()-1;
            playMusic(musicList.get(mPosition));
        }
        else if(position>musicList.size()-1){
            mPosition=0;
            playMusic(musicList.get(0));
        }
        else{
            playMusic(musicList.get(position));
        }

        musicAdapter.setSelected(mPosition);//设置选中音乐
        //更新recycler view，因为设置了两个布局，正在播放的音乐行布局变更
        musicAdapter.notifyDataSetChanged();
        playButton.setBackgroundResource(R.mipmap.ic_music_play);

    }

    private void startOrPause(){//播放或暂停逻辑实现
        if(mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            playButton.setBackgroundResource(R.mipmap.ic_pause);
        }
        else{
            mediaPlayer.start();
            playButton.setBackgroundResource(R.mipmap.ic_music_play);
        }
    }



    @Override
    public void onClick(View view) {

        switch(view.getId()){
            case R.id.last_music:
                changeMusic(--mPosition);
                break;
            case R.id.music_play:
                if(mediaPlayer==null){
                    changeMusic(0);
                }
                else{
                    startOrPause();
                }
                break;
            case R.id.next_music:
                changeMusic(++mPosition);
                break;
        }

    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        //实现轮播效果
        changeMusic(++mPosition);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

        mediaPlayer.seekTo(seekBar.getProgress());//将音乐定位到seekbar指定的位置
        presenter.updateProgress();
    }

    @Override
    public void updateUI1() {
        seekBar.setProgress(mediaPlayer.getCurrentPosition());
        timeStart.setText(parseDate(mediaPlayer.getCurrentPosition()));
    }

    @Override
    public void updateUI2() {
        musicList.clear();
    }

    @Override
    public void updateUI3(MyMusic myMusic) {
        musicList.add(myMusic);
    }

    @Override
    public void updateUI4() {
        musicAdapter.notifyDataSetChanged();
    }

    private String parseDate(int time){
        time=time/1000;
        int min=time/60;
        int second=time%60;
        return min+":"+second;
    }

    @Override
    protected void attachBaseContext(Context base){
        super.attachBaseContext(base);
        MultiDex.install(base);
    }
}
