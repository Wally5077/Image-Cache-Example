package com.home.cloudinteractiveinterview.models.repositories.image;


import android.graphics.Bitmap;

public interface ImageRepository {
    Bitmap downloadImage(String path);
}
