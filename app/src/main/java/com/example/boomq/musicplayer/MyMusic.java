package com.example.boomq.musicplayer;

import android.graphics.Bitmap;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * Created by boomq on 2019/3/16.
 */

public class MyMusic extends DataSupport implements Serializable {

    private int musicRes;

    private int album_Id;

    private String musicId;

    private String musicName;

    private String singer;




    private String path;

    private int size;

    private int time;

    private String album;

    private boolean isCheck = false;

    private Bitmap albumPic;

    public MyMusic(){
    }

    public MyMusic(String id, String musicName, String singer, String path, int size, int time, String album, Bitmap albumPic,int album_Id){
        this.musicId=id;
        this.musicName=musicName;
        this.singer=singer;
        this.path=path;
        this.size=size;
        this.time=time;
        this.album=album;
        this.albumPic=albumPic;
        this.album_Id=album_Id;
    }

    public MyMusic(String id, String musicName, String singer, String path, int size, int time, String album,int album_Id,int musicRes){
        this.musicId=id;
        this.musicName=musicName;
        this.singer=singer;
        this.path=path;
        this.size=size;
        this.time=time;
        this.album=album;
        this.album_Id=album_Id;
        this.musicRes=musicRes;
    }

    public int getMusicRes() {
        return musicRes;
    }

    public void setMusicRes(int musicRes) {
        this.musicRes = musicRes;
    }

    public void setAlbum_Id(int album_Id) {
        this.album_Id = album_Id;
    }

    public int getAlbum_Id() {
        return album_Id;
    }

    public boolean isCheck(){
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public Bitmap getAlbumPic() {
        return albumPic;
    }

    public void setAlbumPic(Bitmap albumPic) {
        this.albumPic = albumPic;
    }

    public boolean getIsCheck(){
        return isCheck;
    }

    public void setIsCheck(boolean isCheck){
        this.isCheck=isCheck;
    }

    public String getMusicId() {
        return musicId;
    }

    public void setMusicId(String musicId) {
        this.musicId = musicId;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getMusicName() {
        return musicName;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public MyMusic(String musicId, String musicName, String singer, String path, int size, int time, String album, boolean isCheck, Bitmap albumPic){
        this.musicId=musicId;
        this.musicName=musicName;
        this.singer=singer;
        this.path=path;
        this.size=size;
        this.time=time;
        this.album=album;
        this.isCheck=isCheck;
        this.albumPic=albumPic;
    }
}
