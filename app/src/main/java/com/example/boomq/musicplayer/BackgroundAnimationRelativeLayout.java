package com.example.boomq.musicplayer;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.AccelerateInterpolator;
import android.widget.RelativeLayout;

/**
 * Created by boomq on 2019/3/20.
 */

public class BackgroundAnimationRelativeLayout extends RelativeLayout {

    private final int DURATION_ANIMATION=500;
    private final int INDEX_BACKGROUND=0;
    private final int INDEX_FOREGROUND=1;

    private LayerDrawable layerDrawable;
    private ObjectAnimator objectAnimator;
    private int musicPicRes=-1;

    public BackgroundAnimationRelativeLayout(Context context) {
        this(context,null);
    }

    public BackgroundAnimationRelativeLayout(Context context, AttributeSet attars) {
        this(context,attars,0);
    }

    public BackgroundAnimationRelativeLayout(Context context, AttributeSet attars, int defStyleAttr) {
        super(context,attars,defStyleAttr);
        initLayerDrawable();
        initObjectAnimator();

    }
    private void initLayerDrawable(){
        Drawable backgroundDrawable=getContext().getDrawable(R.drawable.ic_blackground_src);
        Drawable[] drawables=new Drawable[2];

        //初始化时先将前景与背景颜色设为一致
        drawables[INDEX_BACKGROUND]=backgroundDrawable;
        drawables[INDEX_FOREGROUND]=backgroundDrawable;
        layerDrawable=new LayerDrawable(drawables);
    }

    @SuppressLint("ObjectAnimatorBinding")
    private void initObjectAnimator(){
        objectAnimator=ObjectAnimator.ofFloat(this,"numbers",0f,1.0f);
        objectAnimator.setDuration(DURATION_ANIMATION);
        objectAnimator.setInterpolator(new AccelerateInterpolator());
        objectAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int foregroundAlpha2=(int)((float)animation.getAnimatedValue()*255);
                if(layerDrawable.getDrawable(INDEX_FOREGROUND)==null){
                    Log.e("ERROR", "onAnimationUpdate: " );
                }
                else{
                    //动态设置draw able的透明度，让前景图逐渐显示
                    layerDrawable.getDrawable(INDEX_FOREGROUND).setAlpha(foregroundAlpha2);
                    BackgroundAnimationRelativeLayout.this.setBackground(layerDrawable);
                }

            }
        });

        objectAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                layerDrawable.setDrawable(INDEX_BACKGROUND,layerDrawable.getDrawable(INDEX_FOREGROUND));
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    public void setForeground(Drawable drawable){
        layerDrawable.setDrawable(INDEX_FOREGROUND,drawable);
    }

    //对外提供方法，用于开始渐变动画
    public void beginAnimation(){
        objectAnimator.start();
    }

    public boolean isNeed2UpdateBackground(int musicPicRes){
        if(this.musicPicRes==-1){
            return true;
        }
        if(musicPicRes!=this.musicPicRes){
            return true;
        }
        return false;
        }
    }

