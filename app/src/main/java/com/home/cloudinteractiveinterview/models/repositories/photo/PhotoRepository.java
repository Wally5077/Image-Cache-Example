package com.home.cloudinteractiveinterview.models.repositories.photo;

import com.home.cloudinteractiveinterview.models.entities.Photo;

import java.util.List;

public interface PhotoRepository {
    List<Photo> downloadPhotos(int page);
}
