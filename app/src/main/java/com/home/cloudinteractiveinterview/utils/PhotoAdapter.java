package com.home.cloudinteractiveinterview.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.home.cloudinteractiveinterview.models.entities.Photo;
import com.home.cloudinteractiveinterview.R;

import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ImageItemViewHolder> {

    private List<Photo> photoList;
    private Context context;

    public PhotoAdapter(List<Photo> photoList) {
        this.photoList = photoList;
    }

    @NonNull
    @Override
    public ImageItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = (context == null) ? parent.getContext() : context;
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_photo_item, parent, false);
        return new ImageItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageItemViewHolder holder, int position) {
        holder.setData(photoList.get(position));
    }

    @Override
    public int getItemCount() {
        return photoList.size();
    }

    protected class ImageItemViewHolder extends RecyclerView.ViewHolder {

        public ImageItemViewHolder(@NonNull View itemView) {
            super(itemView);
            findViews(itemView);
        }

        private TextView photoId;
        private TextView photoTitle;
        private View photoBackground;

        private void findViews(View itemView) {
            photoBackground = itemView.findViewById(R.id.photoBackground);
            photoId = itemView.findViewById(R.id.photoId);
            photoTitle = itemView.findViewById(R.id.photoTitle);
        }

        public void setData(Photo photo) {
            photoId.setText(photo.getId());
            photoTitle.setText(photo.getTitle());
            setUpPhotoBackground(photo);
        }

        private void setUpPhotoBackground(Photo photo) {
            Bitmap bitmap = photo.getBitmap();
            if (bitmap == null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    photoBackground.setBackground(
                            context.getDrawable(R.drawable.ic_launcher_foreground));
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    photoBackground.setBackground(new BitmapDrawable(bitmap));
                }
            }
        }
    }
}
