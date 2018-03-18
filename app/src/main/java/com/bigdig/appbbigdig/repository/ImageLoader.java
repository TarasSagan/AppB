package com.bigdig.appbbigdig.repository;


import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;


public class ImageLoader implements Target {
    private IImageLoaderCallback callback;
    private ImageView imageView;

    public ImageLoader(ImageView imageView, IImageLoaderCallback callback) {
        this.imageView = imageView;
        this.callback = callback;
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        try {
            imageView.setImageBitmap(bitmap);
            callback.onImageLoaded();
        } catch (Exception e) {
            callback.onImageError(e);
        }
    }

    @Override
    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
        callback.onImageFailed(e);
    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {
    }
}
