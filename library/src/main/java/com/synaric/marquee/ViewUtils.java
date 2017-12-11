package com.synaric.marquee;

import android.content.Context;

/**
 * <br/><br/>Created by Synaric on 2016/10/9 0009.
 */
public class ViewUtils {

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素) 。
     */
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
