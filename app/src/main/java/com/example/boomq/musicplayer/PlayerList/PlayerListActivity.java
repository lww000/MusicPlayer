package com.example.boomq.musicplayer.PlayerList;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.boomq.musicplayer.MyMusic;
import com.example.boomq.musicplayer.Player.PlayerActivity;
import com.example.boomq.musicplayer.PlayerList.PlayerListContract;
import com.example.boomq.musicplayer.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by boomq on 2019/3/16.
 */

public class PlayerListActivity extends AppCompatActivity implements View.OnTouchListener, View.OnClickListener,SeekBar.OnSeekBarChangeListener, MediaPlayer.OnCompletionListener,PlayerListContract.PlayerListView {
   private MediaPlayer mediaPlayer;
   private MusicAdapter musicAdapter;
   private List<MyMusic> musicPlayList;
   private AppCompatSeekBar seekBar;
   private TextView musicTimeStart,musicTimeEnd,bottomMusicName,bottomMusicSinger;
   private ImageView bottomMusicPic;
   private int musicPosition=-1;
   private Button playButton;
   private PlayerListPresenter presenter;
   private RelativeLayout bottomMusic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playerlist_layout);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.hide();
        }
        requestUsePermission();
        initView();
        presenter=new PlayerListPresenter(this,this);
        presenter.queryMusic();
    }

    private void requestUsePermission(){
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
        musicTimeStart=findViewById(R.id.time_start);
        musicTimeEnd=findViewById(R.id.time_end);
        bottomMusicName=findViewById(R.id.music_name2);
        bottomMusicSinger=findViewById(R.id.music_singer2);
        bottomMusicPic=findViewById(R.id.musicAlbumPic);

        seekBar=findViewById(R.id.seek_bar);
        seekBar.setOnSeekBarChangeListener(this);

        playButton=findViewById(R.id.music_play);
        Button lastMusicButton=findViewById(R.id.last_music);
        Button nextMusicButton=findViewById(R.id.next_music);

        playButton.setOnClickListener(this);
        lastMusicButton.setOnClickListener(this);
        nextMusicButton.setOnClickListener(this);

        bottomMusic=findViewById(R.id.bottom_set);
        bottomMusic.setOnTouchListener(this);
        bottomMusic.setOnClickListener(this);

        final RecyclerView musicPlayListView=findViewById(R.id.music_list);
        musicPlayList=new ArrayList<>();
        musicPlayListView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        musicAdapter=new MusicAdapter(musicPlayList);
        musicPlayListView.setAdapter(musicAdapter);
        musicAdapter.setselectedMusic(-1);
        musicAdapter.setOnItemClickListener(new MusicAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                changePlayingMusic(position);
                musicPosition=position;
            }
        });
        musicPlayListView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
    }

    private void musicPlayer(MyMusic myMusic){
        try{
            if(mediaPlayer==null){//判断是否为空，避免重复创建
                mediaPlayer=new MediaPlayer();
                mediaPlayer.setOnCompletionListener(this);
            }
            mediaPlayer.reset();//播放前重置播放器
            mediaPlayer.setDataSource(myMusic.getPath());//设置播放源
            mediaPlayer.prepare();
            mediaPlayer.start();
            musicTimeEnd.setText(parseDate(mediaPlayer.getDuration()));
            bottomMusicName.setText(myMusic.getMusicName());
            bottomMusicSinger.setText(myMusic.getSinger());
            bottomMusicPic.setImageBitmap(myMusic.getAlbumPic());
            seekBar.setMax(mediaPlayer.getDuration());
            presenter.updateProgress();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void changePlayingMusic(int position){
        if(position<0){
            position=musicPlayList.size()-1;
            musicPlayer(musicPlayList.get(position));
        }
        else if(position>musicPlayList.size()-1){
            position=0;
            musicPlayer(musicPlayList.get(0));
        }
        else{
            musicPlayer(musicPlayList.get(position));
        }

        musicAdapter.setselectedMusic(position);//设置选中音乐
        //更新recycler view，因为设置了两个布局，正在播放的音乐行布局变更
        musicAdapter.notifyDataSetChanged();
        playButton.setBackgroundResource(R.mipmap.ic_music_play);

    }

    private void pauseOrStart(){//播放或暂停逻辑实现
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
                changePlayingMusic(--musicPosition);
                break;
            case R.id.music_play:
                if(mediaPlayer==null){
                    changePlayingMusic(0);
                }
                else{
                    pauseOrStart();
                }
                break;
            case R.id.next_music:
                changePlayingMusic(++musicPosition);
                break;
            case R.id.bottom_set:
                Intent intent=new Intent(PlayerListActivity.this,PlayerActivity.class);
                startActivity(intent);
        }

    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        //实现轮播效果
        changePlayingMusic(++musicPosition);
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
        musicTimeStart.setText(parseDate(mediaPlayer.getCurrentPosition()));
    }

    @Override
    public void updateUI2() {
        musicPlayList.clear();
    }

    @Override
    public void updateUI3(MyMusic myMusic) {
        musicPlayList.add(myMusic);
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
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if(motionEvent.getAction()==MotionEvent.ACTION_UP){
              onClick(view);
        }
        return false;
    }
}
