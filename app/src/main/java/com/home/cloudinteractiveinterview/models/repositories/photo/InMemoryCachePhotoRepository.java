package com.home.cloudinteractiveinterview.models.repositories.photo;

import com.home.cloudinteractiveinterview.models.entities.Photo;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class InMemoryCachePhotoRepository implements PhotoRepository {

    private PhotoRepository delegate;

    public InMemoryCachePhotoRepository(PhotoRepository delegate) {
        this.delegate = delegate;
    }

    private static final int DEFAULT_EXPIRY_DURATION = 60000;
    private static final Map<Integer, List<Photo>> pageCaches = new WeakHashMap<>();  // <page, page's photos>
    private static final Map<Integer, Long> cacheLoadedMap = new WeakHashMap<>();  // <page, the time the cache loaded>

    @Override
    public List<Photo> downloadPhotos(int page) {
        synchronized (cacheLoadedMap) {
            if (cacheLoadedMap.containsKey(page)) {
                if (System.currentTimeMillis() - cacheLoadedMap.get(page) < DEFAULT_EXPIRY_DURATION) {
                    return pageCaches.get(page);
                }
            }
            List<Photo> photos = delegate.downloadPhotos(page);
            cacheLoadedMap.put(page, System.currentTimeMillis());
            pageCaches.put(page, photos);
            return photos;
        }
    }
}
