package com.aaron_dbj.checkview;

import android.content.Context;

public class DisplayUtil {
    public static int dp2px(Context context, int dpValue){
        final int density = (int)context.getResources().getDisplayMetrics().density;
        return (int)(density*dpValue + 0.5f);
    }

    public static int px2dp(Context context, int pxValue){
        final int density = (int) context.getResources().getDisplayMetrics().density;
        return (int)(pxValue/density + 0.5f);
    }
}
