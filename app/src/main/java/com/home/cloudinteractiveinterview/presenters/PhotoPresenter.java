package com.home.cloudinteractiveinterview.presenters;

import android.graphics.Bitmap;

import com.home.cloudinteractiveinterview.models.entities.Photo;
import com.home.cloudinteractiveinterview.models.repositories.image.ImageRepository;
import com.home.cloudinteractiveinterview.models.repositories.photo.PhotoRepository;
import com.home.cloudinteractiveinterview.utils.ThreadExecutor;

import java.util.List;

public class PhotoPresenter {

    private PhotoView photoView;
    private PhotoRepository photoRepository;
    private ImageRepository imageRepository;
    private ThreadExecutor threadExecutor;

    public PhotoPresenter(PhotoRepository photoRepository,
                          ImageRepository imageRepository,
                          ThreadExecutor threadExecutor) {
        this.photoRepository = photoRepository;
        this.imageRepository = imageRepository;
        this.threadExecutor = threadExecutor;
    }

    public PhotoPresenter(PhotoView photoView,
                          PhotoRepository photoRepository,
                          ThreadExecutor threadExecutor) {
        this.photoView = photoView;
        this.photoRepository = photoRepository;
        this.threadExecutor = threadExecutor;
    }

    public void setPhotoView(PhotoView photoView) {
        this.photoView = photoView;
    }

    public void downloadPhotos(int page) {
        threadExecutor.execute(() -> {
            try {
                List<Photo> photoList = photoRepository.downloadPhotos(page);
                threadExecutor.executeUiThread(
                        () -> photoView.onPhotosRead(photoList));
            } catch (Exception err) {
                err.printStackTrace();
            }
        });
    }

    public void downloadPhotoImages(List<Photo> photos) {
        for (Photo photo : photos) {
            threadExecutor.execute(() -> {
                try {
                    Bitmap bitmap = imageRepository.downloadImage(photo.getThumbnailUrl());
                    threadExecutor.executeUiThread(
                            () -> photoView.onImageDownloaded(photo, bitmap));
                } catch (Exception err) {
                    threadExecutor.executeUiThread(() ->
                            photoView.onImageDownloadError(err));
                }
            });
        }
    }

    public interface PhotoView {

        void onPhotosRead(List<Photo> photoList);

        void onImageDownloaded(Photo photo, Bitmap bitmap);

        void onImageDownloadError(Exception err);
    }
}
