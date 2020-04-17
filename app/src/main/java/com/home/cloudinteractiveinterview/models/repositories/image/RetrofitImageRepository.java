package com.home.cloudinteractiveinterview.models.repositories.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Url;

public class RetrofitImageRepository implements ImageRepository {

    private ImageAPi imageAPi;

    public RetrofitImageRepository(Retrofit retrofit) {
        imageAPi = retrofit.create(ImageAPi.class);
    }

    private interface ImageAPi {
        @GET
        Call<ResponseBody> downloadImage(@Url String path);
    }

    @Override
    public Bitmap downloadImage(String path) {
        Call<ResponseBody> imageCall = imageAPi.downloadImage(path);
        Bitmap bitmap = null;
        try {
            Response<ResponseBody> imageResponse = imageCall.execute();
            InputStream inputStream = imageResponse.body().byteStream();
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
