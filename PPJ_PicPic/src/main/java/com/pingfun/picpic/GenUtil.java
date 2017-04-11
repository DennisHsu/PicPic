package com.pingfun.picpic;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.util.TypedValue;

public class GenUtil {

    public static int dpToPixel(Context context, int dp) {

        Resources resources = context.getResources();// 取得系統資源

        float pixel = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, // 轉換單位:dp值
                dp, // 輸入的dp值
                resources.getDisplayMetrics());

        return (int) pixel;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        if (height > width) {

            int tmpH = reqHeight;

            reqHeight = reqWidth;
            reqWidth = tmpH;
        }
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }
}
