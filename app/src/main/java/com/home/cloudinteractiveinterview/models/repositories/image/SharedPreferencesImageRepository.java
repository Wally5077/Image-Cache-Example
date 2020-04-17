package com.home.cloudinteractiveinterview.models.repositories.image;

import android.content.SharedPreferences;
import android.graphics.Bitmap;

import com.home.cloudinteractiveinterview.utils.BitmapTransformer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class SharedPreferencesImageRepository implements ImageRepository {

    private ImageRepository delegate;
    private SharedPreferences sp;

    public SharedPreferencesImageRepository(SharedPreferences sp,
                                            ImageRepository delegate) {
        this.delegate = delegate;
        this.sp = sp;
    }

    private static final String EXPIRY_KEY = "CACHED IMAGES EXPIRY";
    private static final String IMAGE_KEY = "CACHED IMAGES";
    private static final long DEFAULT_EXPIRY_DURATION = TimeUnit.DAYS.toMillis(1);

    @Override
    public Bitmap downloadImage(String path) {
        synchronized (EXPIRY_KEY) {
            String[] pathArray = path.split("/");
            String imageKey = pathArray[pathArray.length - 1];
            String imageExpiryKey = EXPIRY_KEY + imageKey;
            String imageCacheKey = IMAGE_KEY + imageKey;
            if (sp.contains(imageExpiryKey)) {
                if (System.currentTimeMillis() -
                        Long.parseLong(sp.getString(imageExpiryKey, "0"))
                        < DEFAULT_EXPIRY_DURATION) {
                    if (sp.contains(imageCacheKey)) {
                        return BitmapTransformer.stringToBitmap(sp.getString(imageCacheKey, ""));
                    } else {
                        return delegateDownloadImage(path);
                    }
                } else {
                    sp.edit().remove(imageExpiryKey).remove(imageCacheKey).apply();
                    return delegateDownloadImage(path);
                }
            } else {
                return delegateDownloadImage(path);
            }
        }
    }

    private Bitmap delegateDownloadImage(String path) {
        Bitmap bitmap = delegate.downloadImage(path);
        String bitmapString = BitmapTransformer.bitmapToString(bitmap);
        String[] pathArray = path.split("/");
        String imageKey = pathArray[pathArray.length - 1];
        sp.edit().putString(EXPIRY_KEY + imageKey, String.valueOf(System.currentTimeMillis()))
                .putString(IMAGE_KEY + imageKey, bitmapString).apply();
        return bitmap;
    }
}
