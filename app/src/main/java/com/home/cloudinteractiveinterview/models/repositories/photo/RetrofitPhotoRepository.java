package com.home.cloudinteractiveinterview.models.repositories.photo;

import com.home.cloudinteractiveinterview.models.entities.Photo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.GET;

public class RetrofitPhotoRepository implements PhotoRepository {

    private PhotoApi photoApi;

    public RetrofitPhotoRepository(Retrofit retrofit) {
        photoApi = retrofit.create(PhotoApi.class);
    }

    private interface PhotoApi {
        @GET("/photos")
        Call<List<Photo>> downloadImageItems();
    }

    @Override
    public List<Photo> downloadPhotos(int page) {
        List<Photo> photoList = new ArrayList<>(5000);
        if (photoList.isEmpty()) {
            Call<List<Photo>> imageItemListCall = photoApi.downloadImageItems();
            try {
                Response<List<Photo>> imageItemListResponse = imageItemListCall.execute();
                if (imageItemListResponse.code() == 200) {
                    photoList.addAll(imageItemListResponse.body());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return photoList;
    }
}
