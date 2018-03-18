package com.bigdig.appbbigdig.repository;


public interface IImageLoaderCallback {
    void onImageLoaded();
    void onImageFailed(Exception e);
    void onImageError(Exception e);
}
