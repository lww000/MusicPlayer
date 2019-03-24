package com.example.boomq.musicplayer.Player;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.example.boomq.musicplayer.HistoryPlay.MusicSQLiteHelper;
import com.example.boomq.musicplayer.LikeMusic.LikeMusicAdapter;
import com.example.boomq.musicplayer.LikeMusic.LikeMusicHelper;
import com.example.boomq.musicplayer.LrcView.DefaultLrcBulider;
import com.example.boomq.musicplayer.LrcView.GetLrc;
import com.example.boomq.musicplayer.LrcView.LrcUtil;
import com.example.boomq.musicplayer.LrcView.LrcView;
import com.example.boomq.musicplayer.MyMusic;
import com.example.boomq.musicplayer.Player.PlayerContract;
import com.example.boomq.musicplayer.R;
import com.example.boomq.musicplayer.LrcView.DefaultLrcBulider;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.boomq.musicplayer.Player.DiscView.*;

/**
 * Created by boomq on 2019/3/19.
 */

public class PlayerActivity extends AppCompatActivity implements DiscView.IPlayInfo, View.OnClickListener,PlayerContract.PlayerView {

    private LrcView lrcView;
    private DiscView mDisc;
    private Toolbar mToolbar;
    private SeekBar mSeekbar;
    private ImageView mIvPlayOrPause,mIvNext,mIvLast,likeMusic;
    private TextView mMusicDuration,mTotalMusicDuration;
    private BackgroundAnimationRelativeLayout mRootLayout;
    public static final int MUSIC_MESSAGE=0;
    public static final String PARAM_MUSIC_LIST="PARAM_MUSIC_LIST";
    private PlayerPresenter playerPresenter;
    private LikeMusicHelper likeMusicHelper;
    private LikeMusicAdapter likeMusicAdapter;
    private okhttp3.Callback callback;
    public MediaPlayer mMediaPlayer=new MediaPlayer();


    private List<Map<String, Object>> allMusic = new ArrayList<>();
    private List<Map<String, Object>> storedMusic = new ArrayList<>();

    private MusicReceiver mMusicReceiver=new MusicReceiver();
    private List<MyMusic> mMusicDatas=new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_layout);
        playerPresenter=new PlayerPresenter(this,this);
        likeMusicHelper=new LikeMusicHelper(this,"store_music.db",null,1);


        getStoredMusic(storedMusic,likeMusicHelper);
        getAllMusicFromDb();


        requestUsePermission();
        initMusicDatas();
        initView();
        initMusicReceiver();
        makeStatusBarTransparent();
    }





    private void getMusic(Cursor musicCursor) {
        while (musicCursor.moveToNext()) {
            Map<String, Object> item = new HashMap<String, Object>();
            long id = musicCursor.getLong(musicCursor
                    .getColumnIndex(MediaStore.Audio.Media._ID));
            String title = musicCursor.getString(musicCursor
                    .getColumnIndex(MediaStore.Audio.Media.TITLE));
            String singer = musicCursor.getString(musicCursor
                    .getColumnIndex(MediaStore.Audio.Media.ARTIST));
            if (singer != null && singer.equals("<unknown>")) {
                continue;
            }
            int duration = musicCursor.getInt(musicCursor
                    .getColumnIndex(MediaStore.Audio.Media.DURATION));
            long size = musicCursor.getLong(musicCursor
                    .getColumnIndex(MediaStore.Audio.Media.SIZE));
            String path = musicCursor.getString(musicCursor
                    .getColumnIndex(MediaStore.Audio.Media.DATA));
            int isMusic = musicCursor.getInt(musicCursor
                    .getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));
            if (isMusic != 0) {
                item.put("id", id);
                item.put("title", title);
                item.put("singer", singer);
                item.put("duration", duration2Time(duration));
                item.put("size", size);
                item.put("path",path);
                allMusic.add(item);
            }

        }
    }

    private void getAllMusicFromDb() {
        if (allMusic.size() > 0)
            allMusic.clear();
        Cursor cursor=this.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null,null,null,MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        getMusic(cursor);
    }

    private void getStoredMusic(List<Map<String,Object>> storedMusic,LikeMusicHelper likeMusicHelper){
        if (storedMusic.size() > 0)
            storedMusic.clear();
        Cursor cursor=likeMusicHelper.getReadableDatabase().rawQuery("select * from like_music",null );
        while(cursor.moveToNext()){
            Map<String,Object> item=new HashMap<>();
            item.put("title",cursor.getString(1));
            item.put("singer",cursor.getString(2));
            item.put("duration",cursor.getString(3));
            item.put("path",cursor.getString(4));

            storedMusic.add(item);
        }
    }

    protected boolean checkIfStored(String path) {
        for (Map<String, Object> map : allMusic) {
            if (path.equals((String) map.get("path"))) {
                return true;
            }
        }
        return false;
    }


    private void refreshStoredMusic(Map<String, Object> musicInfo) {
        int i = 0;
        for (; i < storedMusic.size(); i++) {
            Map<String, Object> map = storedMusic.get(i);
            String path = (String) map.get("path");
            if (path.equals((String) musicInfo.get("path"))) {
                Log.d("TAG", "remove index =" + i);
                storedMusic.remove(i);
                return;
            }
        }
        storedMusic.add(musicInfo);
    }
    private class MyAsyncTask extends AsyncTask<String, Void, Void> {
        private ImageView likeMusicView;
        private Map<String, Object> musicInfo;
        // 标记收藏，true表示收藏音乐成功，false表示取消收藏音乐
        private boolean storeSuccess;

        public MyAsyncTask(ImageView starView, Map<String, Object> musicInfo) {
            this.likeMusicView = starView;
            this.musicInfo = musicInfo;
        }

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        protected Void doInBackground(String... params) {
            Log.d("TAG", "doInBackground");
            Cursor cursor = likeMusicHelper.getReadableDatabase().rawQuery(
                    "select * from like_music", null);
            while (cursor.moveToNext()) {
                String title = cursor.getString(1);
                String singer = cursor.getString(2);
                Log.d("TAG", "title = " + title + " singer = " + singer);
                Log.d("TAG",
                        "musicInfo.title = " + (String) musicInfo.get("title")
                                + " musicInfo.singer = "
                                + (String) musicInfo.get("singer"));
                if (cursor.getString(4).equals((String) musicInfo.get("path"))) {
                    // 已经收藏的音乐取消收藏并移出收藏音乐表格-stored_music
                    likeMusicHelper.getReadableDatabase()
                            .execSQL(
                                    "delete from like_music where title like ? and singer like ?",
                                    new String[] { title, singer });
                    storeSuccess = false;
                    return null;
                }
            }
            // 未收藏的音乐加入到收藏中
            likeMusicHelper.getReadableDatabase().execSQL(
                    "insert into like_music values(null, ?, ?, ?, ?)",
                    new Object[] { musicInfo.get("title"),
                            musicInfo.get("singer"), musicInfo.get("duration"),
                            musicInfo.get("path") });
            storeSuccess = true;
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (storeSuccess) {
                likeMusicView.setImageResource(R.drawable.xihuan);
                refreshStoredMusic(musicInfo);
                Toast.makeText(getBaseContext(), "收藏成功", Toast.LENGTH_LONG).show();
            } else {
                likeMusicView.setImageResource(R.drawable.xihuan2);
                refreshStoredMusic(musicInfo);
                Toast.makeText(getBaseContext(), "取消收藏", Toast.LENGTH_LONG).show();
            }
        }

    }










    private void requestUsePermission(){
        if(ContextCompat.checkSelfPermission(PlayerActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(PlayerActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        if(requestCode==1){
            if(grantResults.length>0 && grantResults[0] !=PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"拒绝权限将无法使用",Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void initMusicReceiver(){
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(MusicService.ACTION_STATUS_MUSIC_PLAY);
        intentFilter.addAction(MusicService.ACTION_STATUS_MUSIC_PAUSE);
        intentFilter.addAction(MusicService.ACTION_STATUS_MUSIC_DURATION);
        intentFilter.addAction(MusicService.ACTION_STATUS_MUSIC_COMPLETE);

        //注册本地广播
        LocalBroadcastManager.getInstance(this).registerReceiver(mMusicReceiver,intentFilter);
    }



    private void initView(){
        mDisc=(DiscView)findViewById(R.id.discview);
        //mDisc.setVisibility(VISIBLE);
        //lrcView.findViewById(R.id.lrcview);

        //lrcView.setVisibility(INVISIBLE);
        mIvNext=(ImageView)findViewById(R.id.ivNext);
        mIvLast=(ImageView)findViewById(R.id.ivLast);
        mIvPlayOrPause=(ImageView)findViewById(R.id.ivPlayOrPause);
        likeMusic=(ImageView)findViewById(R.id.like_music);
        likeMusic.setImageResource(R.drawable.xihuan2);

        for(int i=0;i<allMusic.size();i++){
        final Map<String,Object> item=allMusic.get(i);
        if(checkIfStored((String) item.get("path"))){
            likeMusic.setImageResource(R.drawable.xihuan2);
        }
        else{
            likeMusic.setImageResource(R.drawable.xihuan);
        }

        likeMusic.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                new MyAsyncTask(likeMusic,item).execute();

            }
        });}






        mMusicDuration=(TextView)findViewById(R.id.CurrentTime);
        mTotalMusicDuration=(TextView)findViewById(R.id.totalTime);
        mSeekbar=(SeekBar)findViewById(R.id.musicSeekBar);
        mRootLayout=(BackgroundAnimationRelativeLayout)findViewById(R.id.rootLayout);

        mToolbar=(Toolbar)findViewById(R.id.toolBar);
        setSupportActionBar(mToolbar);

        mDisc.setPlayInfoListener(this);
        mDisc.setOnClickListener(this);
        mIvLast.setOnClickListener(this);
        mIvNext.setOnClickListener(this);
        mIvPlayOrPause.setOnClickListener(this);

        mSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                mMusicDuration.setText(duration2Time(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                playerPresenter.stopUpdateSeekBarProgress();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekTo(seekBar.getProgress());
                playerPresenter.startUpdateSeekBarProgress();
            }
        });

        mMusicDuration.setText(duration2Time(0));
        mTotalMusicDuration.setText(duration2Time(0));
        mDisc.setMusicDataList(mMusicDatas);
    }


    //刷新收藏列表
    private void refreshStoreMusic(Map<String,Object> musicInfo){
        int i=0;
        for(i=0;i<storedMusic.size();i++){
            Map<String,Object> map=storedMusic.get(i);
            String path=(String)map.get("path");
            if(path.equals((String) musicInfo.get("path"))){
                storedMusic.remove(i);
             //   this.sendBroadcast();
                likeMusicAdapter.notifyDataSetChanged();
                return;
            }
        }

        storedMusic.add(musicInfo);
        likeMusicAdapter.notifyDataSetChanged();
    }











    //设置透明状态栏
    private void makeStatusBarTransparent(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            Window window=getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        else if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    private void initMusicDatas(){

        playerPresenter.queryMusicData();

        Intent intent=new Intent(this,MusicService.class);
        intent.putExtra(PARAM_MUSIC_LIST,(Serializable)mMusicDatas);
        startService(intent);
    }

    private void try2UpdateMusicPicBackground(final int musicPicRes){
        if(mRootLayout.isNeed2UpdateBackground(musicPicRes)){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final Drawable foregroundDrawable=getForegroundDrawable(musicPicRes);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mRootLayout.setForeground(foregroundDrawable);
                            mRootLayout.beginAnimation();
                        }
                    });
                }
            }).start();
        }
    }

    private Drawable getForegroundDrawable(int musicPicRes){
        //得到屏幕的宽高比，以便切割图片
        final float widthHeightSize=(float)(DisplayUtil.getScreenWidth(PlayerActivity.this)*1.0/DisplayUtil.getScreenHeight(this)*1.0);

        Bitmap bitmap=getForegroundBitmap(musicPicRes);
        int cropBitmapWidth=0;
        int cropBitmapWidthX=0;


        Bitmap cropBitmap=null;
        Bitmap scaleBitmap=null;


        cropBitmapWidth = (int) (widthHeightSize * bitmap.getHeight());
        cropBitmapWidthX = (int) ((bitmap.getWidth() - cropBitmapWidth) / 2.0);

        //切割部分图片
        cropBitmap = Bitmap.createBitmap(bitmap, cropBitmapWidthX, 0, cropBitmapWidth, bitmap.getHeight());
            //缩小图片
        scaleBitmap = Bitmap.createScaledBitmap(cropBitmap, bitmap.getWidth() / 50, bitmap.getHeight() / 50, false);


        final Bitmap blurBitmap= FastBlurUtil.doBlur(scaleBitmap,8,true);

        final Drawable foregroundDrawable=new BitmapDrawable(blurBitmap);
        //加入灰色遮罩层，避免图片过亮影响其他图片
        foregroundDrawable.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
        return foregroundDrawable;
    }


    private Bitmap getForegroundBitmap(int musicPicRes){

        String mUriAlbums="content://media/external/audio/albums";
        String[] projection=new String[]{"album_art"};
        Cursor cur=this.getContentResolver().query(Uri.parse(mUriAlbums+"/"+Integer.toString(musicPicRes)),projection,null,null,null);

        String album_art=null;
        if(cur.getCount()>0&&cur.getColumnCount()>0){
            cur.moveToNext();
            album_art=cur.getString(0);
        }
        cur.close();

        int screenWidth = DisplayUtil.getScreenWidth(this);
        int screenHeight = DisplayUtil.getScreenHeight(this);


        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        if(album_art!=null){
            BitmapFactory.decodeFile(album_art,options);
            int imageWidth = options.outWidth;
            int imageHeight = options.outHeight;

            if (imageWidth < screenWidth && imageHeight < screenHeight) {
                return BitmapFactory.decodeFile(album_art);
            }
            int sample = 2;
            int sampleX = imageWidth / DisplayUtil.getScreenWidth(this);
            int sampleY = imageHeight / DisplayUtil.getScreenHeight(this);


            if (sampleX > sampleY && sampleY > 1) {
                sample = sampleX;
            } else if (sampleY > sampleX && sampleX > 1) {
                sample = sampleY;
            }


            options.inJustDecodeBounds = false;
            options.inSampleSize = sample;
            options.inPreferredConfig = Bitmap.Config.RGB_565;

            return BitmapFactory.decodeFile(album_art,options);
        }
        else{
            BitmapFactory.decodeResource(this.getResources(),R.mipmap.unknown_music, options);
            int imageWidth = options.outWidth;
            int imageHeight = options.outHeight;


            if (imageWidth < screenWidth && imageHeight < screenHeight) {
                return BitmapFactory.decodeResource(this.getResources(),R.mipmap.unknown_music);
            }

            int sample = 2;
            int sampleX = imageWidth / DisplayUtil.getScreenWidth(this);
            int sampleY = imageHeight / DisplayUtil.getScreenHeight(this);


            if (sampleX > sampleY && sampleY > 1) {
                sample = sampleX;
            } else if (sampleY > sampleX && sampleX > 1) {
                sample = sampleY;
            }



            options.inJustDecodeBounds = false;
            options.inSampleSize = sample;
            options.inPreferredConfig = Bitmap.Config.RGB_565;

            return BitmapFactory.decodeResource(this.getResources(),R.mipmap.unknown_music,options);
        }
     }





    @Override
    public void onMusicInfoChanged(String musicName, String musicAuthor) {
        getSupportActionBar().setTitle(musicName);
        getSupportActionBar().setSubtitle(musicAuthor);
    }

    @Override
    public void onMusicPicChanged(int musicPicRes) {
        try2UpdateMusicPicBackground(musicPicRes);
    }

    @Override
    public void onMusicChanged(DiscView.MusicChangedStatus musicChangedStatus) {
        switch (musicChangedStatus){
            case PLAY:{
                play();
                break;
            }
            case PAUSE:{
                pause();
                break;
            }
            case NEXT:{
                next();
                break;
            }
            case LAST:{
                last();
                break;
            }
            case STOP:{
                stop();
                break;
            }
        }
    }

    @Override
    public void onClick(View view) {
        if(view==mIvPlayOrPause){
            mDisc.playOrPause();
        }
        else if(view==mIvNext){
            mDisc.next();
        }
        else if(view==mIvLast){
            mDisc.last();
        }
        else if(view==mDisc){

            //mDisc.setVisibility(INVISIBLE);

            DefaultLrcBulider bulider=new DefaultLrcBulider();

            //lrcView.setVisibility(VISIBLE);
            lrcView.setLrc(bulider.getLrcRows(GetLrc.getLrcFromAssets(GetLrc.parseJOSNWithGSON(mMusicDatas.get(4).getMusicName(),4))).toString());

            lrcView.setPlayer(mMediaPlayer);

            lrcView.init();

        }
    }


    private void play(){
        optMusic(MusicService.ACTION_OPT_MUSIC_PLAY);
        playerPresenter.startUpdateSeekBarProgress();
    }
    private void pause(){
        optMusic(MusicService.ACTION_OPT_MUSIC_PAUSE);
        playerPresenter.stopUpdateSeekBarProgress();
    }
    private void stop(){
        playerPresenter.stopUpdateSeekBarProgress();
        mIvPlayOrPause.setImageResource(R.drawable.ic_play_src);
        mMusicDuration.setText(duration2Time(0));
        mTotalMusicDuration.setText(duration2Time(0));
        mSeekbar.setProgress(0);
    }
    private void next(){
        mRootLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                optMusic(MusicService.ACTION_OPT_MUSIC_NEXT);
            }
        },DURATION_NEEDLE_ANIAMTOR);
        playerPresenter.stopUpdateSeekBarProgress();
        mMusicDuration.setText(duration2Time(0));
        mTotalMusicDuration.setText(duration2Time(0));
    }

    private void last(){
        mRootLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                optMusic(MusicService.ACTION_OPT_MUSIC_LAST);
            }
        },DURATION_NEEDLE_ANIAMTOR);
        playerPresenter.stopUpdateSeekBarProgress();
        mMusicDuration.setText(duration2Time(0));
        mTotalMusicDuration.setText(duration2Time(0));
    }

    private void complete(boolean isOver){
        if(isOver){
            mDisc.stop();
        }
        else{
            mDisc.next();
        }
    }


    private void optMusic(final String action){
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(action));
    }

    private void seekTo(int position){
        Intent intent=new Intent(MusicService.ACTION_OPT_MUSIC_SEEK_TO);
        intent.putExtra(MusicService.PARAM_MUSIC_SEEK_TO,position);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }





    @Override
    public void updateUI1() {
        mSeekbar.setProgress(mSeekbar.getProgress()+1000);
        mMusicDuration.setText(duration2Time(mSeekbar.getProgress()));
    }

    private String duration2Time(int duration){
        int min=duration/1000/60;
        int sec=duration/1000%60;

        return (min<10?"0"+min:min+"")+":"+(sec<10?"0"+sec:sec+"");
    }

    @Override
    public void updateUI2(MyMusic myMusic) {
         mMusicDatas.add(myMusic);
    }


    private void updateMusicDurationInfo(int totalDuration){
        mSeekbar.setProgress(0);
        mSeekbar.setMax(totalDuration);
        mTotalMusicDuration.setText(duration2Time(totalDuration));
        mMusicDuration.setText(duration2Time(0));
        playerPresenter.startUpdateSeekBarProgress();
    }


    class MusicReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String action=intent.getAction();
            if(action.equals(MusicService.ACTION_STATUS_MUSIC_PLAY)){
                mIvPlayOrPause.setImageResource(R.drawable.ic_pause_src);
                int currentPosition=intent.getIntExtra(MusicService.PARAM_MUSIC_CURRENT_POSITION,0);
                mSeekbar.setProgress(currentPosition);
                if(!mDisc.isPlaying()){
                    mDisc.playOrPause();
                }
            }
            else if(action.equals(MusicService.ACTION_STATUS_MUSIC_PAUSE)){
                mIvPlayOrPause.setImageResource(R.drawable.ic_play_src);
                if(mDisc.isPlaying()){
                    mDisc.playOrPause();
                }
            }
            else if(action.equals(MusicService.ACTION_STATUS_MUSIC_DURATION)){
                int duration=intent.getIntExtra(MusicService.PARAM_MUSIC_DURATION,0);
                updateMusicDurationInfo(duration);
            }
            else if(action.equals(MusicService.ACTION_STATUS_MUSIC_COMPLETE)){
                boolean isOver=intent.getBooleanExtra(MusicService.PARAM_MUSIC_IS_OVER,true);
                complete(isOver);
            }
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMusicReceiver);
    }


}
