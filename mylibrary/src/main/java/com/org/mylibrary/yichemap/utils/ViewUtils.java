package com.org.mylibrary.yichemap.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.TypedValue;

import java.io.ByteArrayOutputStream;

/**
 * Created by Administrator on 2017/10/20/020.
 */

public class ViewUtils {
    public ViewUtils() {
    }

    public static int dip2px(Context context, float dipValue) {
        Resources r = context.getResources();
        return (int) TypedValue.applyDimension(1, dipValue, r.getDisplayMetrics());
    }

    public static int sp2px(Context context, float dipValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int)(dipValue * fontScale + 0.5F);
    }

    public static Bitmap stringtoBitmap(String string) {
        Bitmap bitmap = null;

        try {
            byte[] e = Base64.decode(string, 0);
            bitmap = BitmapFactory.decodeByteArray(e, 0, e.length);
        } catch (Exception var3) {
            var3.printStackTrace();
        }

        return bitmap;
    }

    public static String bitmaptoString(Bitmap bitmap) {
        String string = null;
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bStream);
        byte[] bytes = bStream.toByteArray();
        string = Base64.encodeToString(bytes, 0);
        return string;
    }
}
