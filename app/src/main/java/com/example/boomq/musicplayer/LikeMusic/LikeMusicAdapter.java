package com.example.boomq.musicplayer.LikeMusic;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.boomq.musicplayer.HistoryPlay.HistoryMusicAdapter;
import com.example.boomq.musicplayer.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by boomq on 2019/3/24.
 */

public class LikeMusicAdapter extends RecyclerView.Adapter<LikeMusicAdapter.ViewHolder>{
    List<Map<String,Object>> likeMusicList=new ArrayList<Map<String,Object>>();
    private Map<String, Object> map;


    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView likeMusicName;
        TextView likeMusicSinger;

        public ViewHolder(View itemView) {
            super(itemView);
            likeMusicName=(TextView)itemView.findViewById(R.id.like_music_name);
            likeMusicSinger=(TextView)itemView.findViewById(R.id.like_music_singer);

        }

    }

    public LikeMusicAdapter(List<Map<String,Object>> likeMusicList){
        this.likeMusicList=likeMusicList;
    }



    @Override
    public LikeMusicAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.like_music_item_layout,parent,false);
        LikeMusicAdapter.ViewHolder holder=new LikeMusicAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull LikeMusicAdapter.ViewHolder holder, int position) {
        Map<String,Object> map=likeMusicList.get(position);
        holder.likeMusicName.setText(map.get("title").toString());
        holder.likeMusicSinger.setText(map.get("singer").toString());
    }

    @Override
    public int getItemCount() {
        return likeMusicList.size();
    }
}
