package com.aaron_dbj.checkview;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.TimeInterpolator;
import android.view.View;

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
     * 设置圆环上的进度动画
     * @param propertyName
     * @param duration
     * @param interpolator
     * @param values
     * @return
     */
    public ObjectAnimator ringProcessAnimator(String propertyName, int duration, TimeInterpolator interpolator, int...values){
        ObjectAnimator processAnimator = ObjectAnimator.ofInt(target, propertyName, values);
        processAnimator.setInterpolator(interpolator);
        processAnimator.setDuration(duration);
        return processAnimator;
    }

}
