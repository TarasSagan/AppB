package com.bigdig.appbbigdig.imageActivity;

import com.hannesdorfmann.mosby3.mvp.MvpView;


public interface IImageActivityView extends MvpView {
    void onShowImage(String url);
    void onShowAndSaveImage(String url);
    void onShowInfo(String info);
    void checkPermissionWrite();
}
