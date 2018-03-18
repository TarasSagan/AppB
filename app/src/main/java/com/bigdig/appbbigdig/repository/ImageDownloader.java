package com.bigdig.appbbigdig.repository;


import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

public class ImageDownloader implements Target {
    private IImageLoaderCallback callback;
    private  String name ;
    private ImageView imageView;
    private String pathToSave = "/BIGDIG/test/B/";

    public ImageDownloader(ImageView imageView, IImageLoaderCallback callback) {
        this.imageView = imageView;
        this.callback = callback;
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        imageView.setImageBitmap(bitmap);
        name = "image_" + UUID.randomUUID().toString() + ".jpg";
        File folder = new File(Environment.getExternalStorageDirectory() +
                File.separator + "BIGDIG/test/B/");
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdirs();
        }if (success) {
            File file = new File(Environment.getExternalStorageDirectory().getPath() + pathToSave + name);
            try {
                file.createNewFile();
                FileOutputStream ostream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
                ostream.close();
                callback.onImageLoaded();

            } catch (Exception e) {
                callback.onImageError(e);
            }
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
