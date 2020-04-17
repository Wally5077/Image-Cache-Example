package com.home.cloudinteractiveinterview.models.repositories.image;


import android.graphics.Bitmap;
import android.util.LruCache;

import com.home.cloudinteractiveinterview.utils.BitmapTransformer;

import java.lang.ref.WeakReference;

public class InMemoryCacheImageRepository implements ImageRepository {

    private static LruCache<String, WeakReference<String>> imagesCache =
            new LruCache<>((int) (Runtime.getRuntime().maxMemory() / 1024) / 8);
    private ImageRepository delegate;

    public InMemoryCacheImageRepository(ImageRepository delegate) {
        this.delegate = delegate;
        initializeCache();
    }

    private void initializeCache() {
    }

    @Override
    public Bitmap downloadImage(String path) {
        synchronized (this) {
            Bitmap bitmap = get(path);
            if (bitmap == null) {
                bitmap = delegate.downloadImage(path);
                put(path, bitmap);
            }
            return bitmap;
        }
    }

    private Bitmap get(String key) {
        if (key != null) {
            WeakReference<String> weakReference = imagesCache.get(key);
            if (weakReference == null) {
                return null;
            }
            return BitmapTransformer.stringToBitmap(weakReference.get());
        } else {
            return null;
        }
    }

    private void put(String key, Bitmap value) {
        if (imagesCache != null && imagesCache.get(key) == null) {
            imagesCache.put(key, new WeakReference<>(BitmapTransformer.bitmapToString(value)));
        }
    }

    public void remove(String key) {
        imagesCache.remove(key);
    }

    public void clearCache() {
        if (imagesCache != null) {
            imagesCache.evictAll();
        }
    }
}