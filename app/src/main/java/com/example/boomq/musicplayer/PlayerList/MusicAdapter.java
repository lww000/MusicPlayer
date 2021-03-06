package com.example.boomq.musicplayer.PlayerList;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.boomq.musicplayer.MyMusic;
import com.example.boomq.musicplayer.R;

import java.util.List;

/**
 * Created by boomq on 2019/3/16.
 */

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHolder> implements View.OnClickListener{
    private int selectedMusic;
    private List<MyMusic> musicPlayList;
    private OnItemClickListener onItemClickListener;
    private Context mContext;

    @Override
    public MusicAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext=parent.getContext();
        View view=LayoutInflater.from(mContext).inflate(R.layout.item_music_content,parent,false);
        ViewHolder holder=new ViewHolder(view);
        view.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(MusicAdapter.ViewHolder holder, int position) {
        MyMusic music=musicPlayList.get(position);
        holder.content.removeAllViews();
        if(position==selectedMusic){
            holder.content.addView(addFocusMusicView());
            TextView textView=holder.content.findViewById(R.id.music_playing);
            textView.setText("正在播放:"+music.getMusicName());
        }
        else{
            holder.content.addView(addNormalMusicView());
            TextView musicName=holder.content.findViewById(R.id.music_name);
            TextView musicSinger=holder.content.findViewById(R.id.music_singer);
            musicName.setText(music.getMusicName());
            musicSinger.setText(music.getSinger());
        }

        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return musicPlayList.size();
    }

    @Override
    public void onClick(View view) {
        if(onItemClickListener !=null){
            onItemClickListener.onItemClick(view,(Integer)view.getTag());
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        RelativeLayout content;

        public ViewHolder(View itemView) {
            super(itemView);
            content=itemView.findViewById(R.id.content);
        }
    }

    private View addFocusMusicView(){
        return LayoutInflater.from(mContext).inflate(R.layout.item_music_focus,null,false);
    }

    private View addNormalMusicView(){
        return LayoutInflater.from(mContext).inflate(R.layout.item_music_list,null,false);
    }
    public void setselectedMusic(int selectedMusic){
        this.selectedMusic=selectedMusic;
    }

    public MusicAdapter(List<MyMusic> musicPlayList){
        this.musicPlayList=musicPlayList;
    }
    public interface OnItemClickListener{
        void onItemClick(View v,int position);
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener= onItemClickListener;
    }
}
