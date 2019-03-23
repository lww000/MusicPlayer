package com.example.boomq.musicplayer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.example.boomq.musicplayer.PlayerList.PlayerListActivity;

/**
 * Created by boomq on 2019/3/23.
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener,View.OnTouchListener{
    private TextView localMusic,historyMusic,likeMusic;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.mainactivity_layout);

        localMusic=findViewById(R.id.local_music);
        localMusic.setOnTouchListener(this);
        localMusic.setOnClickListener(this);

        historyMusic=findViewById(R.id.history_music);
        historyMusic.setOnTouchListener(this);
        historyMusic.setOnClickListener(this);

        likeMusic=findViewById(R.id.like_music);
        likeMusic.setOnTouchListener(this);
        likeMusic.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
         switch (view.getId()){
             case R.id.local_music:
                 Intent intent=new Intent(MainActivity.this, PlayerListActivity.class);
                 startActivity(intent);
         }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if(motionEvent.getAction()==MotionEvent.ACTION_UP){
            onClick(view);
        }
        return false;
    }
}
