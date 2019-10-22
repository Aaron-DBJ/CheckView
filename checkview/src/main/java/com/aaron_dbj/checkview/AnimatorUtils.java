package com.aaron_dbj.checkview;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.TimeInterpolator;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

public class AnimatorUtils {
    private View target;
    private static AnimatorUtils Instance;

    private AnimatorUtils(View target){
        this.target = target;
    }

    public static AnimatorUtils getInstance(View target){
        if (Instance == null){
            synchronized (AnimatorUtils.class){
                if (Instance == null){
                    Instance = new AnimatorUtils(target);
                }
            }
        }
        return Instance;
    }

    /**
     * 心跳回弹动画
     * @param mDuration
     * @param interpolator
     * @return
     */
    public ObjectAnimator heartBeatAnimator(int mDuration, TimeInterpolator interpolator){
        PropertyValuesHolder h1 = PropertyValuesHolder.ofFloat("scaleX", 1.0f, 0.8f, 1.0f, 1.2f, 1.0f);
        PropertyValuesHolder h2 = PropertyValuesHolder.ofFloat("scaleY", 1.0f, 0.8f, 1.0f, 1.2f, 1.0f);
        ObjectAnimator heartBeat = ObjectAnimator.ofPropertyValuesHolder(target, h1, h2);
        heartBeat.setDuration(mDuration);
        heartBeat.setInterpolator(interpolator);
        return heartBeat;
    }

    /**
     * 通用的属性动画
     * @param propertyName
     * @param duration
     * @param interpolator
     * @param values
     * @return
     */
    public ObjectAnimator createAnimator(String propertyName, int duration, TimeInterpolator interpolator, int...values){
        ObjectAnimator animator = ObjectAnimator.ofInt(target, propertyName, values);
        animator.setInterpolator(interpolator);
        animator.setDuration(duration);
        return animator;
    }

    public ObjectAnimator createAnimator(String propertyName, int duration, int...values){
        return createAnimator(propertyName, duration, new AccelerateDecelerateInterpolator(), values);
    }

    public AnimatorSet rotateFadeInAnimator(int duration, TimeInterpolator interpolator){
        ObjectAnimator rotateAnimator = ObjectAnimator.ofFloat(target, "rotation", 0, 360);
        ObjectAnimator fadeAnimator = ObjectAnimator.ofFloat(target, "alpha", 1f,  0f,  1f);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(rotateAnimator, fadeAnimator);
        set.setInterpolator(interpolator);
        set.setDuration(duration);
        return set;
    }
}
