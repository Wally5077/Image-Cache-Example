package com.home.cloudinteractiveinterview.utils;

import com.home.cloudinteractiveinterview.models.repositories.image.ImageRepository;
import com.home.cloudinteractiveinterview.models.repositories.photo.PhotoRepository;
import com.home.cloudinteractiveinterview.models.repositories.image.RetrofitImageRepository;
import com.home.cloudinteractiveinterview.models.repositories.photo.RetrofitPhotoRepository;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public abstract class Global {

    private static Retrofit photoRetrofit = new Retrofit
            .Builder().client(new OkHttpClient
            .Builder().addInterceptor(new HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY))
            .connectTimeout(60000, TimeUnit.MILLISECONDS)
            .readTimeout(60000, TimeUnit.MILLISECONDS).build())
            .baseUrl("https://jsonplaceholder.typicode.com")
            .addConverterFactory(GsonConverterFactory.create()).build();

    private static PhotoRepository photoRepository;

    public static PhotoRepository photoRepository() {
        return photoRepository = (photoRepository == null) ?
                new RetrofitPhotoRepository(photoRetrofit) : photoRepository;
    }

    private static Retrofit imageRetrofit = new Retrofit
            .Builder().client(new OkHttpClient
            .Builder().addInterceptor(new HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY))
            .connectTimeout(60000, TimeUnit.MILLISECONDS)
            .readTimeout(60000, TimeUnit.MILLISECONDS).build())
            .baseUrl("https://via.placeholder.com")
            .addConverterFactory(GsonConverterFactory.create()).build();

    private static ImageRepository imageRepository;

    public static ImageRepository imageRepository() {
        return imageRepository = (imageRepository == null) ?
                new RetrofitImageRepository(imageRetrofit) : imageRepository;
    }

    private static ThreadExecutor threadExecutor;

    public static ThreadExecutor threadExecutor() {
        return threadExecutor = (threadExecutor == null) ?
                new AndroidThreadExecutor() : threadExecutor;
    }
}
