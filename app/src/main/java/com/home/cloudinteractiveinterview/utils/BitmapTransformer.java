package com.home.cloudinteractiveinterview.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public abstract class BitmapTransformer {

    public static String bitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        //第二步:利用Base64将字节数组输出流中的数据转换成字符串String
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public static Bitmap stringToBitmap(String bitmapKey) {
        if (bitmapKey == null) {
            return null;
        }
        byte[] byteArray = Base64.decode(bitmapKey, Base64.DEFAULT);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);
        //第三步:利用ByteArrayInputStream生成Bitmap
        return BitmapFactory.decodeStream(byteArrayInputStream);
    }

}
