package com.example.boomq.musicplayer.HistoryPlay;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.boomq.musicplayer.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by boomq on 2019/3/23.
 */

public class HistoryMusicAdapter extends RecyclerView.Adapter<HistoryMusicAdapter.ViewHolder>{
    List<Map<String,Object>> historyList=new ArrayList<Map<String,Object>>();
    private Map<String, Object> map;


    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView historyMusicName;
        TextView historyMusicTime;

        public ViewHolder(View itemView) {
            super(itemView);
            historyMusicName=(TextView)itemView.findViewById(R.id.history_music_name);
            historyMusicTime=(TextView)itemView.findViewById(R.id.history_music_time);

        }

    }

    public HistoryMusicAdapter(List<Map<String,Object>> historyList){
        this.historyList=historyList;
    }



    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.history_music_item_layout,parent,false);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String,Object> map=historyList.get(position);
        holder.historyMusicName.setText(map.get("title").toString());
        holder.historyMusicTime.setText(map.get("playdate").toString());
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }


}
