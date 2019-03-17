package com.example.boomq.musicplayer;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSeekBar;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.List;

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



    @Override
    public void onClick(View view) {

    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void updateUI1() {

    }
}
