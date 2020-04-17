package com.home.cloudinteractiveinterview.models.repositories.photo;

import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.home.cloudinteractiveinterview.models.entities.Photo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SharedPreferencesPhotoRepository implements PhotoRepository {

    private final Gson gson;
    private SharedPreferences sp;
    private PhotoRepository delegate;

    public SharedPreferencesPhotoRepository(Gson gson, SharedPreferences sp,
                                            PhotoRepository delegate) {
        this.gson = gson;
        this.delegate = delegate;
        this.sp = sp;
        photoList = new ArrayList<>(5000);
    }

    private static final String EXPIRY_KEY = "CACHED PHOTOS EXPIRY";
    private static final String PHOTOS_KEY = "CACHED PHOTOS";
    private static final long DEFAULT_EXPIRY_DURATION = TimeUnit.DAYS.toMillis(3);
    private final List<Photo> photoList;
    private static final int PAGE_LIMIT = 28;

    @Override
    public List<Photo> downloadPhotos(int page) {
        synchronized (photoList) {
            if (photoList.isEmpty()) {
                if (sp.contains(EXPIRY_KEY)) {
                    if (System.currentTimeMillis() - Long.parseLong(sp.getString(EXPIRY_KEY, "0"))
                            < DEFAULT_EXPIRY_DURATION) {
                        if (sp.contains(PHOTOS_KEY)) {
                            photoList.addAll(gson.fromJson(sp.getString(PHOTOS_KEY, ""),
                                    new TypeToken<List<Photo>>() {
                                    }.getType()));
                        } else {
                            delegateDownloadPhotos(page);
                        }
                    } else {
                        delegateDownloadPhotos(page);
                    }
                } else {
                    delegateDownloadPhotos(page);
                }
            }
            return photoList.subList((page - 1) * PAGE_LIMIT,
                    Math.min((page * PAGE_LIMIT), photoList.size() - 1));
        }
    }

    private void delegateDownloadPhotos(int page) {
        photoList.addAll(delegate.downloadPhotos(page));
        sp.edit().putString(EXPIRY_KEY, String.valueOf(System.currentTimeMillis()))
                .putString(PHOTOS_KEY, gson.toJson(photoList)).apply();
    }
}
