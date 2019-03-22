package com.example.boomq.musicplayer.Player;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.boomq.musicplayer.MyMusic;
import com.example.boomq.musicplayer.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by boomq on 2019/3/19.
 */

public class DiscView extends RelativeLayout {

    private ImageView mIvNeedle;
    private ViewPager mVpContain;
    private ViewPagerAdapter mViewPagerAdapter;
    private ObjectAnimator mNeedleAnimator;

    private List<View> mDiscLayouts=new ArrayList<>();
    private List<MyMusic> mMusicDatas=new ArrayList<>();
    private List<ObjectAnimator> mDiscAnimators=new ArrayList<>();

    //标记View Pager是否处于偏移的状态
    private boolean mViewPagerIsOffset=false;

    //标记唱针复位后，是否需要重新偏移到唱片处
    private boolean mIsNeed2StartPlayAnimators=false;

    private MusicStatus musicStatus=MusicStatus.STOP;
    public static final int DURATION_NEEDLE_ANIAMTOR=500;
    private NeedleAnimatorStatus needleAnimatorStatus=NeedleAnimatorStatus.IN_FAR_END;

    private IPlayInfo mIPlayInfo;

    private int mScreenWidth,mScreenHeight;


    //discview需要触发的音乐播放状态：播放，暂停，上下首，停止
    public enum MusicChangedStatus{
        PLAY,PAUSE,NEXT,LAST,STOP
    }

    //唱针当前所处的位置
    private enum NeedleAnimatorStatus{
        //移动时：从唱盘往远处移动
        TO_FAR_END,
        //移动时：从远处往唱盘移动
        TO_NEAR_END,
        //静止时：离开唱盘
        IN_FAR_END,
        //静止时：贴近唱盘
        IN_NEAR_END
    }

    public enum MusicStatus{
        PLAY,PAUSE,STOP
    }


    public interface IPlayInfo{
        //用于更新状态栏变化
        public void onMusicInfoChanged(String musicName,String musicAuthor);
        //用于更新背景图片
        public void onMusicPicChanged(int musicPicRes);
        //用于更新音乐播放状态
        public void onMusicChanged(MusicChangedStatus musicChangedStatus);
    }



    public DiscView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context,attrs,defStyleAttr);
        mScreenWidth= DisplayUtil.getScreenWidth(context);
        mScreenHeight=DisplayUtil.getScreenHeight(context);
    }

    public DiscView(Context context, AttributeSet attrs) {
        this(context,attrs,0);
    }
    public DiscView(Context context){
        this(context,null);
    }


    @Override
    protected void onFinishInflate(){
        super.onFinishInflate();
        initDiscBlackground();
        initViewPager();
        initNeedle();
        initObjectAnimator();

    }

    private void initDiscBlackground(){
        ImageView mDiscBlackground=(ImageView)findViewById(R.id.ivDiscBlackground);
        mDiscBlackground.setImageDrawable(getDiscBlackgroundDrawable());

        int marginTop=(int)(DisplayUtil.SCALE_DISC_MARGIN_TOP*mScreenHeight);
        RelativeLayout.LayoutParams layoutParams=(LayoutParams)mDiscBlackground.getLayoutParams();
        layoutParams.setMargins(0,marginTop,0,0);

        mDiscBlackground.setLayoutParams(layoutParams);
    }

    private void initViewPager(){
        mViewPagerAdapter=new ViewPagerAdapter();
        mVpContain=(ViewPager)findViewById(R.id.vpDiscContain);
        mVpContain.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mVpContain.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            int lastPositionOffsetPixels=0;
            int currentItem=0;
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //左滑
                if(lastPositionOffsetPixels>positionOffsetPixels){
                    if(positionOffset<0.5){
                        notifyMusicInfoChanged(position);
                    }
                    else{
                        notifyMusicInfoChanged(mVpContain.getCurrentItem());
                    }
                }
                //右滑
                else if(lastPositionOffsetPixels<positionOffsetPixels){
                    if(positionOffset>0.5){
                        notifyMusicInfoChanged(position+1);
                    }
                    else{
                        notifyMusicInfoChanged(position);
                    }
                }
                lastPositionOffsetPixels=positionOffsetPixels;
            }

            @Override
            public void onPageSelected(int position) {
                 resetOtherDiscAnimation(position);
                 notifyMusicPicChanged(position);
                 if(position>currentItem){
                     notifyMusicStatusChanged(MusicChangedStatus.NEXT);
                 }
                 else{
                     notifyMusicStatusChanged(MusicChangedStatus.LAST);
                 }
                 currentItem=position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                doWithAnimatorOnPageScroll(state);

            }
        });
        mVpContain.setAdapter(mViewPagerAdapter);

        RelativeLayout.LayoutParams layoutParams=(LayoutParams)mVpContain.getLayoutParams();
        int marginTop=(int)(DisplayUtil.SCALE_DISC_MARGIN_TOP*mScreenHeight);
        layoutParams.setMargins(0,marginTop,0,0);
        mVpContain.setLayoutParams(layoutParams);
    }

    //取消其他页面上的动画，并将图片旋转角度恢复
    private void resetOtherDiscAnimation(int position){
        for(int i=0;i<mDiscLayouts.size();i++){
            if(position==i)continue;
            mDiscAnimators.get(position).cancel();
            ImageView imageView=(ImageView)mDiscLayouts.get(i).findViewById(R.id.ivDisc);
            imageView.setRotation(0);
        }
    }

    private void doWithAnimatorOnPageScroll(int state){
        switch (state){
            case ViewPager.SCROLL_STATE_IDLE:
            case ViewPager.SCROLL_STATE_SETTLING:{
                mViewPagerIsOffset=false;
                if(musicStatus==MusicStatus.PLAY){
                    playAnimator();
                }
                break;
            }
            case ViewPager.SCROLL_STATE_DRAGGING:{
                mViewPagerIsOffset=true;
                pauseAnimator();
                break;
            }
        }
    }


    private void initNeedle(){
        mIvNeedle=(ImageView)findViewById(R.id.ivNeedle);

        int needleWidth=(int)(DisplayUtil.SCALE_NEEDLE_WIDTH*mScreenWidth);
        int needleHeight=(int)(DisplayUtil.SCALE_NEEDLE_HEIGHT*mScreenHeight);

        //设置手柄的外边距为负数，让其隐藏一部分
        int marginTop=(int)(DisplayUtil.SCALE_NEEDLE_MARGIN_TOP*mScreenHeight)-1;
        int marginLeft=(int)(DisplayUtil.SCALE_NEEDLE_MARGIN_LEFT*mScreenWidth);

        Bitmap originBitmap=BitmapFactory.decodeResource(getResources(),R.drawable.ic_needle_src);
        Bitmap bitmap=Bitmap.createScaledBitmap(originBitmap,needleWidth,needleHeight,false);

        RelativeLayout.LayoutParams layoutParams=(LayoutParams)mIvNeedle.getLayoutParams();
        layoutParams.setMargins(marginLeft,marginTop,0,0);

        int pivotX=(int)(DisplayUtil.SCALE_NEEDLE_PIVOT_X*mScreenWidth);
        int pivotY=(int)(DisplayUtil.SCALE_NEEDLE_PIVOT_Y*mScreenWidth);

        mIvNeedle.setPivotX(pivotX);
        mIvNeedle.setPivotY(pivotY);
        mIvNeedle.setRotation(DisplayUtil.ROTATION_INIT_NEEDLE);
        mIvNeedle.setImageBitmap(bitmap);
        mIvNeedle.setLayoutParams(layoutParams);
    }


    private void initObjectAnimator(){
        mNeedleAnimator=ObjectAnimator.ofFloat(mIvNeedle,View.ROTATION,DisplayUtil.ROTATION_INIT_NEEDLE,0);
        mNeedleAnimator.setDuration(DURATION_NEEDLE_ANIAMTOR);
        mNeedleAnimator.setInterpolator(new AccelerateInterpolator());
        mNeedleAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                //根据动画开始前Needle Animator Status的状态，即可得出动画进行时他的状态
                if(needleAnimatorStatus==NeedleAnimatorStatus.IN_FAR_END){
                    needleAnimatorStatus=NeedleAnimatorStatus.TO_NEAR_END;
                }
                else if(needleAnimatorStatus==NeedleAnimatorStatus.IN_NEAR_END){
                    needleAnimatorStatus=NeedleAnimatorStatus.TO_FAR_END;
                }
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if(needleAnimatorStatus==NeedleAnimatorStatus.TO_NEAR_END){
                    needleAnimatorStatus=NeedleAnimatorStatus.IN_NEAR_END;
                    int index=mVpContain.getCurrentItem();
                    playDiscAnimator(index);
                    musicStatus=MusicStatus.PLAY;
                }
                else if(needleAnimatorStatus==NeedleAnimatorStatus.TO_FAR_END){
                    needleAnimatorStatus=NeedleAnimatorStatus.IN_FAR_END;
                    if(musicStatus==MusicStatus.STOP){
                        mIsNeed2StartPlayAnimators=true;
                    }
                }
                if(mIsNeed2StartPlayAnimators){
                    mIsNeed2StartPlayAnimators=false;

                    //只有在view pager不处于偏移状态时，才开始唱盘旋转动画
                    if(!mViewPagerIsOffset){
                        //延时500ms
                        DiscView.this.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                playAnimator();
                            }
                        },50);
                    }
                }
            }


            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }


    public void setPlayInfoListener(IPlayInfo listener){
        this.mIPlayInfo=listener;
    }



    //得到唱盘背后半透明图片
    private Drawable getDiscBlackgroundDrawable(){
        int discSize=(int)(mScreenWidth*DisplayUtil.SCALE_DISC_SIZE);
        Bitmap bitmapDisc=Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.ic_disc_blackground_src),discSize,discSize,false);
        RoundedBitmapDrawable roundedDiscDrawable= RoundedBitmapDrawableFactory.create(getResources(),bitmapDisc);
        return roundedDiscDrawable;
    }

    //得到唱盘图片
    //唱盘图片由空心圆盘及音乐专辑图片“合成”得到
    private Drawable getDiscDrawable(int musicPicRes){
        int discSize=(int)(mScreenWidth*DisplayUtil.SCALE_DISC_SIZE);
        int musicPicSize=(int)(mScreenWidth*DisplayUtil.SCALE_MUSIC_PIC_SIZE);

        Bitmap bitmapDisc=Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.ic_disc_src),discSize,discSize,false);
        Bitmap bitmapMusicPic=getMusicPicBitmap(musicPicSize,musicPicRes);
        BitmapDrawable discDrawable=new BitmapDrawable(bitmapDisc);
        RoundedBitmapDrawable roundedMusicDrawable=RoundedBitmapDrawableFactory.create(getResources(),bitmapMusicPic);

        //抗锯齿
        discDrawable.setAntiAlias(true);
        roundedMusicDrawable.setAntiAlias(true);

        Drawable[] drawables=new Drawable[2];
        drawables[0]=roundedMusicDrawable;
        drawables[1]=discDrawable;

        LayerDrawable layerDrawable=new LayerDrawable(drawables);
        int musicPicMargin=(int)((DisplayUtil.SCALE_DISC_SIZE-DisplayUtil.SCALE_MUSIC_PIC_SIZE)*mScreenWidth/2);

        //调整专辑照片的四周边距，让其显示在正中
        layerDrawable.setLayerInset(0,musicPicMargin,musicPicMargin,musicPicMargin,musicPicMargin);

        return layerDrawable;
    }

    @Nullable
    private Bitmap getMusicPicBitmap(int musicPicSize, int musicPicRes) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        String mUriAlbums="content://media/external/audio/albums";
        String[] projection=new String[]{"album_art"};
        Cursor cur=getContext().getContentResolver().query(Uri.parse(mUriAlbums+"/"+Integer.toString(musicPicRes)),projection,null,null,null);
        String album_art=null;
        if(cur.getCount()>0&&cur.getColumnCount()>0){
            cur.moveToNext();
            album_art=cur.getString(0);
        }
        cur.close();
        if(album_art!=null){
            BitmapFactory.decodeFile(album_art,options);


            int imageWidth = options.outWidth;
            int sample = imageWidth / musicPicSize;
            int dstSample = 1;
            if (sample > dstSample) {
                dstSample = sample;
            }
            options.inJustDecodeBounds = false;
            //设置图片采样率
            options.inSampleSize = dstSample;
            //设置图片解码格式
            options.inPreferredConfig = Bitmap.Config.RGB_565;

            Bitmap bitmapMusicPic = BitmapFactory.decodeFile(album_art,options);
            Bitmap musicPic=Bitmap.createScaledBitmap(bitmapMusicPic,musicPicSize,musicPicSize,true);

            return musicPic;
        }

        else{
            BitmapFactory.decodeResource(getResources(),R.mipmap.unknown_music,options);

            int imageWidth = options.outWidth;
            int sample = imageWidth / musicPicSize;
            int dstSample = 1;
            if (sample > dstSample) {
                dstSample = sample;
            }
            options.inJustDecodeBounds = false;
            //设置图片采样率
            options.inSampleSize = dstSample;
            //设置图片解码格式
            options.inPreferredConfig = Bitmap.Config.RGB_565;

            Bitmap bitmapMusicPic = BitmapFactory.decodeResource(getResources(),R.mipmap.unknown_music,options);
            Bitmap musicPic=Bitmap.createScaledBitmap(bitmapMusicPic,musicPicSize,musicPicSize,true);

            return musicPic;
        }

    }


    public void setMusicDataList(List<MyMusic> musicDataList){
        if(musicDataList.isEmpty()){
            return;
        }

        mDiscLayouts.clear();
        mMusicDatas.clear();
        mDiscAnimators.clear();
        mMusicDatas.addAll(musicDataList);

        int i=0;
        for(MyMusic musicData:mMusicDatas){
            View discLayout= LayoutInflater.from(getContext()).inflate(R.layout.layout_disc,mVpContain,false);
            ImageView disc=(ImageView)discLayout.findViewById(R.id.ivDisc);
            disc.setImageDrawable(getDiscDrawable(musicData.getAlbum_Id()));

            mDiscAnimators.add(getDiscObjectAnimator(disc,i++));
            mDiscLayouts.add(discLayout);
        }
        mViewPagerAdapter.notifyDataSetChanged();

        MyMusic musicData=mMusicDatas.get(0);
        if(mIPlayInfo!=null){
            mIPlayInfo.onMusicInfoChanged(musicData.getMusicName(),musicData.getSinger());
            mIPlayInfo.onMusicPicChanged(musicData.getAlbum_Id());
        }
    }


    private ObjectAnimator getDiscObjectAnimator(ImageView disc,final int i){
        ObjectAnimator objectAnimator=ObjectAnimator.ofFloat(disc,View.ROTATION,0,360);
        objectAnimator.setRepeatCount(ValueAnimator.INFINITE);
        objectAnimator.setDuration(20*1000);
        objectAnimator.setInterpolator(new LinearInterpolator());

        return objectAnimator;
    }

    @Override
    protected void onDetachedFromWindow(){
        super.onDetachedFromWindow();
    }


    private void playAnimator(){
        //唱针处于远端时，直接播放动画
        if(needleAnimatorStatus==NeedleAnimatorStatus.IN_FAR_END){
            mNeedleAnimator.start();
        }
        //唱针处于往远端移动时，设置标记，等动画结束后再播放动画
        else if(needleAnimatorStatus==NeedleAnimatorStatus.TO_FAR_END){
            mIsNeed2StartPlayAnimators=true;
        }
    }

    private void pauseAnimator(){
        //播放时暂停动画
        if(needleAnimatorStatus==NeedleAnimatorStatus.IN_NEAR_END){
            int index=mVpContain.getCurrentItem();
            pauseDiscAnimatior(index);
        }
        //唱针往唱盘移动时暂停动画
        else if(needleAnimatorStatus==NeedleAnimatorStatus.TO_NEAR_END){
            mNeedleAnimator.reverse();
            needleAnimatorStatus=NeedleAnimatorStatus.TO_FAR_END;
        }
        //动画可能执行多次，只有音乐处于停止状态时，才执行暂停命令
        if(musicStatus==MusicStatus.STOP){
            notifyMusicStatusChanged(MusicChangedStatus.STOP);
        }
        else if(musicStatus==MusicStatus.PAUSE){
            notifyMusicStatusChanged(MusicChangedStatus.PAUSE);
        }
    }

    //播放唱盘动画
    private void playDiscAnimator(int index){
        ObjectAnimator objectAnimator=mDiscAnimators.get(index);
        if(objectAnimator.isPaused()){
            objectAnimator.resume();
        }
        else{
            objectAnimator.start();
        }

        //唱盘动画可能执行多次，只有在音乐不是播放状态，才回调执行播放
        if(musicStatus!=MusicStatus.PLAY){
            notifyMusicStatusChanged(MusicChangedStatus.PLAY);
        }
    }

    //暂停唱盘动画
    private void pauseDiscAnimatior(int index){
        ObjectAnimator objectAnimator=mDiscAnimators.get(index);
        objectAnimator.pause();
        mNeedleAnimator.reverse();
    }



    public void notifyMusicInfoChanged(int position){
        if(mIPlayInfo!=null){
            MyMusic musicData=mMusicDatas.get(position);
            mIPlayInfo.onMusicInfoChanged(musicData.getMusicName(),musicData.getSinger());
        }
    }

    public void notifyMusicPicChanged(int position){
        if(mIPlayInfo!=null){
            MyMusic musicData=mMusicDatas.get(position);
            mIPlayInfo.onMusicPicChanged(musicData.getAlbum_Id());
        }
    }

    public void notifyMusicStatusChanged(MusicChangedStatus musicChangedStatus){
        if(mIPlayInfo!=null){
            mIPlayInfo.onMusicChanged(musicChangedStatus);
        }
    }


    private void play(){
        playAnimator();
    }

    private void pause(){
        musicStatus=MusicStatus.PAUSE;
        pauseAnimator();
    }

    public void stop(){
        musicStatus=MusicStatus.STOP;
        pauseAnimator();
    }

    public void playOrPause(){
        if(musicStatus==MusicStatus.PLAY){
            pause();
        }
        else{
            play();
        }
    }

    public void next(){
        int currentItem=mVpContain.getCurrentItem();
        if(currentItem==mMusicDatas.size()-1){
            Toast.makeText(getContext(),"已经到达最后一首",Toast.LENGTH_SHORT).show();
        }
        else{
            selectMusicWithButton();
            mVpContain.setCurrentItem(currentItem+1,true);
        }
    }



    public void last(){
        int currentItem=mVpContain.getCurrentItem();
        if(currentItem==0){
            Toast.makeText(getContext(),"已经到达第一首",Toast.LENGTH_SHORT).show();
        }
        else{
            selectMusicWithButton();
            mVpContain.setCurrentItem(currentItem-1,true);
        }
    }

    public boolean isPlaying(){
        return musicStatus==MusicStatus.PLAY;
    }



    private void selectMusicWithButton(){
        if(musicStatus==MusicStatus.PLAY){
            mIsNeed2StartPlayAnimators=true;
            pauseAnimator();
        }
        else if(musicStatus==MusicStatus.PAUSE){
            play();
        }
    }





    class ViewPagerAdapter extends PagerAdapter{

        @Override
        public Object instantiateItem(ViewGroup container,int position){
            View discLayout=mDiscLayouts.get(position);
            container.addView(discLayout);
            return discLayout;
        }

        @Override
        public void destroyItem(ViewGroup container,int position,Object object){
            container.removeView(mDiscLayouts.get(position));
        }

        @Override
        public int getCount() {
            return mDiscLayouts.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }
    }
}
