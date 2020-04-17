package com.home.cloudinteractiveinterview.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.home.cloudinteractiveinterview.R;
import com.home.cloudinteractiveinterview.models.entities.Photo;
import com.home.cloudinteractiveinterview.models.repositories.image.InMemoryCacheImageRepository;
import com.home.cloudinteractiveinterview.models.repositories.image.ImageRepository;
import com.home.cloudinteractiveinterview.models.repositories.image.SharedPreferencesImageRepository;
import com.home.cloudinteractiveinterview.models.repositories.photo.InMemoryCachePhotoRepository;
import com.home.cloudinteractiveinterview.models.repositories.photo.PhotoRepository;
import com.home.cloudinteractiveinterview.models.repositories.photo.SharedPreferencesPhotoRepository;
import com.home.cloudinteractiveinterview.presenters.PhotoPresenter;
import com.home.cloudinteractiveinterview.utils.Global;
import com.home.cloudinteractiveinterview.utils.PhotoAdapter;

import java.util.LinkedList;
import java.util.List;

import static com.home.cloudinteractiveinterview.presenters.PhotoPresenter.*;

public class PhotoActivity extends AppCompatActivity implements PhotoView {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        findViews();
        init();
    }

    private RecyclerView photoRecycler;
    private ProgressBar photoProgressBar;

    private void findViews() {
        photoRecycler = findViewById(R.id.photoRecycler);
        photoProgressBar = findViewById(R.id.photoProgressBar);
    }

    private PhotoPresenter photoPresenter;

    private void init() {
        setUpPhotoPresenter();
        setUpImageRecycler();
        queryDictionaryList();
    }

    private void setUpPhotoPresenter() {
        SharedPreferences sp =
                getSharedPreferences("cloudInteractiveInterview", MODE_PRIVATE);

        PhotoRepository photoRepository = new InMemoryCachePhotoRepository(
                new SharedPreferencesPhotoRepository(
                        new Gson(), sp, Global.photoRepository()));

        ImageRepository imageRepository = new InMemoryCacheImageRepository(
                new SharedPreferencesImageRepository(sp, Global.imageRepository()));

        photoPresenter = new PhotoPresenter(photoRepository,
                imageRepository, Global.threadExecutor());
        photoPresenter.setPhotoView(this);
    }

    private List<Photo> photoList;
    private PhotoAdapter photoAdapter;
    private boolean isPullToRefreshTriggered = false;

    private void setUpImageRecycler() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
        photoRecycler.setHasFixedSize(true);
        photoRecycler.setLayoutManager(gridLayoutManager);
        photoList = new LinkedList<>();
        photoAdapter = new PhotoAdapter(photoList);
        photoRecycler.setAdapter(photoAdapter);
        photoRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView,
                                             int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int lastVisibleItemPosition =
                        gridLayoutManager.findLastVisibleItemPosition();
                if (lastVisibleItemPosition + 1 == photoAdapter.getItemCount()) {
                    if (!isPullToRefreshTriggered) {
                        isPullToRefreshTriggered = true;
                        queryDictionaryList();
                    }
                }
            }
        });
    }

    private int currentPage = 1;

    private void queryDictionaryList() {
        photoProgressBar.setVisibility(View.VISIBLE);
        photoPresenter.downloadPhotos(currentPage);
    }

    @Override
    public void onPhotosRead(List<Photo> photoList) {
        isPullToRefreshTriggered = false;
        currentPage++;
        this.photoList.addAll(photoList);
        photoAdapter.notifyDataSetChanged();
        photoProgressBar.setVisibility(View.INVISIBLE);
        photoPresenter.downloadPhotoImages(photoList);
        photoAdapter.notifyDataSetChanged();
    }

    @Override
    public void onImageDownloaded(Photo photo, Bitmap bitmap) {
        photo.setBitmap(bitmap);
        photoAdapter.notifyDataSetChanged();
    }

    @Override
    public void onImageDownloadError(Exception err) {
        Log.d(this.getClass().getSimpleName(),
                "onImageDownloadError: " + err.getMessage());
    }
}
