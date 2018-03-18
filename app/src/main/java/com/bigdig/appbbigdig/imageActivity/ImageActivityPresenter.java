package com.bigdig.appbbigdig.imageActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.bigdig.appbbigdig.R;
import com.bigdig.appbbigdig.repository.IImageLoaderCallback;
import com.bigdig.appbbigdig.servise.ContentProviderService;
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;


public class ImageActivityPresenter extends MvpBasePresenter<IImageActivityView> implements IImageLoaderCallback{
    private volatile boolean HAS_PERMISSIONS_WRITE = false;
    private ImageActivity imageActivity;
    private Intent intent;
    private String URL;
    private long _ID = 0;
    private int CURRENT_STATUS = 0;
    private final int status_first_load = 0;
    private final int statusWork = 1;
    private final int statusError = 2;
    private final int statusUnknown = 3;

    public ImageActivityPresenter(ImageActivity imageActivity) {
        this.imageActivity = imageActivity;
        intent = imageActivity.getIntent();

//
//        if (!TextUtils.isEmpty(imageActivity.getIntent().getStringExtra(
//                imageActivity.getResources().getString(R.string.intent_filter_key_url)))) {
//            URL = imageActivity.getIntent().getStringExtra(
//                    imageActivity.getResources().getString(R.string.intent_filter_key_url));
//        }
//        _ID = intent.getLongExtra(imageActivity.getString(R.string.intent_filter_key_ID), 5);
//
//        CURRENT_STATUS = imageActivity.getIntent().getIntExtra(
//                imageActivity.getResources().getString(R.string.intent_filter_key_state), status_first_load);


    }

    public void init(){
        Bundle bundle = intent.getBundleExtra(imageActivity.getResources().getString(R.string.intent_filter_key_bundle));
        String TMP_URL = "" ;
        if (bundle.containsKey(imageActivity.getResources().getString(R.string.intent_filter_key_url))){
            TMP_URL = bundle.getString(imageActivity.getResources().getString(R.string.intent_filter_key_url));
        }
        if (bundle.containsKey(imageActivity.getResources().getString(R.string.intent_filter_key_ID))){
            _ID = bundle.getLong(imageActivity.getResources().getString(R.string.intent_filter_key_ID));
        }
        if (bundle.containsKey(imageActivity.getResources().getString(R.string.intent_filter_key_state))){
            CURRENT_STATUS = bundle.getInt(imageActivity.getResources().getString(R.string.intent_filter_key_state));
        }
        if (!TextUtils.isEmpty(TMP_URL)){
        URL = TMP_URL;}
        Log.d("TAG","URL = " + URL + " ID = " + Long.toString(_ID));

        if(CURRENT_STATUS == status_first_load){
             initFirstShowImage();
        }else if (CURRENT_STATUS == statusWork){
            initNonFirstImageShow();
        }else if(CURRENT_STATUS == statusError || CURRENT_STATUS == statusUnknown){
            initFirstShowImage();
        }
    }

    public void setPERMISSIONS(boolean HAS_PERMISSIONS) {
        this.HAS_PERMISSIONS_WRITE = HAS_PERMISSIONS;
    }

    private void initFirstShowImage(){
        if (isNetworkConnected()){
            if (!TextUtils.isEmpty(URL)) {
                ifViewAttached(view -> view.onShowImage(URL));
            }}else{
            ifViewAttached(view -> view.onShowInfo(imageActivity.getString(R.string.no_internet)));
        }
    }

    private void initNonFirstImageShow(){
        if (!HAS_PERMISSIONS_WRITE){
            ifViewAttached(view -> view.checkPermissionWrite());
        }
        if (!TextUtils.isEmpty(URL)) {
            ifViewAttached(view -> view.onShowAndSaveImage(URL));
        }
    }


    @Override
    public void onImageLoaded() {
        Intent intent = new Intent(imageActivity, ContentProviderService.class);
        intent.putExtra(imageActivity.getResources().getString(R.string.intent_filter_key_url), URL);
            //For saved in bd
        if(CURRENT_STATUS == status_first_load) {
            intent.putExtra(imageActivity.getResources().getString(R.string.intent_filter_key_state), statusWork);
            intent.putExtra(imageActivity.getResources().getString(R.string.string_status),
                    imageActivity.getResources().getString(R.string.string_status_add));
        }
            //For deleted from bd
        if(CURRENT_STATUS == statusWork){
            intent.putExtra(imageActivity.getResources().getString(R.string.intent_filter_key_ID), _ID);
            intent.putExtra(imageActivity.getResources().getString(R.string.string_status),
                        imageActivity.getResources().getString(R.string.string_status_delete));
        }
            //For updated in bd
        if (CURRENT_STATUS == statusError || CURRENT_STATUS == statusUnknown){
                intent.putExtra(imageActivity.getResources().getString(R.string.intent_filter_key_state), statusWork);
                intent.putExtra(imageActivity.getResources().getString(R.string.intent_filter_key_ID), _ID);
                intent.putExtra(imageActivity.getResources().getString(R.string.string_status),
                        imageActivity.getResources().getString(R.string.string_status_update));
        }
        imageActivity.startService(intent);
}

    @Override
    public void onImageFailed(Exception e) {
        Intent intent = new Intent(imageActivity, ContentProviderService.class);
        intent.putExtra(imageActivity.getResources().getString(R.string.intent_filter_key_url), URL);
        intent.putExtra(imageActivity.getResources().getString(R.string.intent_filter_key_state), statusError);
        if(_ID != 0){
            intent.putExtra(imageActivity.getResources().getString(R.string.intent_filter_key_ID), _ID);
            intent.putExtra(imageActivity.getResources().getString(R.string.string_status),
                    imageActivity.getResources().getString(R.string.string_status_update));
        }else {
            intent.putExtra(imageActivity.getResources().getString(R.string.string_status),
                    imageActivity.getResources().getString(R.string.string_status_add));}
        imageActivity.startService(intent);
}

    @Override
    public void onImageError(Exception e) {
        Intent intent = new Intent(imageActivity, ContentProviderService.class);
        intent.putExtra(imageActivity.getResources().getString(R.string.intent_filter_key_url), URL);
        intent.putExtra(imageActivity.getResources().getString(R.string.intent_filter_key_state), statusUnknown);
        if(_ID != 0){
            intent.putExtra(imageActivity.getResources().getString(R.string.intent_filter_key_ID), _ID);
            intent.putExtra(imageActivity.getResources().getString(R.string.string_status),
                    imageActivity.getResources().getString(R.string.string_status_update));
        }else {
            intent.putExtra(imageActivity.getResources().getString(R.string.string_status),
                    imageActivity.getResources().getString(R.string.string_status_add));}
        imageActivity.startService(intent);
}

    private boolean isNetworkConnected() {
        ConnectivityManager connMgr = (ConnectivityManager)
                imageActivity.getSystemService(Context.CONNECTIVITY_SERVICE); // 1
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo(); // 2
        return networkInfo != null && networkInfo.isConnected(); // 3
    }
}
