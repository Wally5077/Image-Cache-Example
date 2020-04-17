package com.home.cloudinteractiveinterview.utils;

import android.os.Build;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.home.cloudinteractiveinterview.models.entities.Photo;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.function.Consumer;

public class GsonStreaming {

    public void downloadPhotos(String jsonUrl, Consumer<Photo> photoConsumer) throws IOException {

        URL url = new URL(jsonUrl);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        int responseCode = httpConn.getResponseCode();

        // always check HTTP response code first
        if (responseCode == HttpURLConnection.HTTP_OK) {
            String fileName = "";
            String disposition = httpConn.getHeaderField("Content-Disposition");
            String contentType = httpConn.getContentType();
            int contentLength = httpConn.getContentLength();

            // opens input stream from the HTTP connection
            InputStream inputStream = httpConn.getInputStream();
            streamPhotos(inputStream, photoConsumer);
            httpConn.disconnect();
        }
    }

    private void streamPhotos(InputStream inputStream, Consumer<Photo> photoConsumer)
            throws IOException {

        JsonReader reader = new JsonReader(new InputStreamReader(inputStream));
        reader.beginArray();

        JsonToken jsonToken;
        Photo currentPhoto = null;
        while ((jsonToken = reader.peek()) != JsonToken.END_ARRAY) {
            switch (jsonToken) {
                case BEGIN_OBJECT:
                    reader.beginObject();
                    currentPhoto = new Photo();
                    break;
                case NAME:  // key
                    String key = reader.nextName();
                    assert currentPhoto != null;
                    switch (key) {
                        case "albumId":
                            reader.nextInt();
                            break;
                        case "url":
                            reader.nextString();
                            break;
                        case "id":
                            currentPhoto.setId(String.valueOf(reader.nextInt()));
                            break;
                        case "title":
                            currentPhoto.setTitle(reader.nextString());
                            break;
                        case "thumbnailUrl":
                            currentPhoto.setThumbnailUrl(reader.nextString());
                            break;
                    }
                    break;
                case END_OBJECT:
                    reader.endObject();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        photoConsumer.accept(currentPhoto);
                    }
                    currentPhoto = null;
            }
        }
    }
}
