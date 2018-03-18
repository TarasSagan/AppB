package com.bigdig.appbbigdig.imageActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;
import com.bigdig.appbbigdig.R;
import com.bigdig.appbbigdig.repository.ImageDownloader;
import com.bigdig.appbbigdig.repository.ImageLoader;
import com.hannesdorfmann.mosby3.mvp.MvpActivity;
import com.squareup.picasso.Picasso;

public class ImageActivity extends MvpActivity<IImageActivityView, ImageActivityPresenter> implements IImageActivityView {
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        imageView = findViewById(R.id.imageView);

        getPresenter().init();

    }

    @NonNull
    @Override
    public ImageActivityPresenter createPresenter() {
        return new ImageActivityPresenter(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPresenter().init(); //After accept permissions
    }

    @Override
    public void onShowImage(String url) {
        final ImageLoader imageLoader = new ImageLoader(imageView, getPresenter());
        imageView.setTag(imageLoader);
        Picasso.get()
                .load(url)
                .placeholder(getDrawable(R.drawable.ic_placeholder))
                .error(getDrawable(R.drawable.ic_error))
                .into(imageLoader);
    }

    @Override
    public void onShowAndSaveImage(String url) {
        final ImageDownloader imageDownloader = new ImageDownloader(imageView, getPresenter());
        imageView.setTag(imageDownloader);
        Picasso.get()
                .load(url)
                .placeholder(getDrawable(R.drawable.ic_placeholder))
                .error(getDrawable(R.drawable.ic_error))
                .into(imageDownloader);
    }

    @Override
    public void onShowInfo(String info) {
        Snackbar.make(imageView, info, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void checkPermissionWrite() {
        if(hasPermissions()){
            getPresenter().setPERMISSIONS(true);
            getPresenter().init();
        }else {
            requestPermissionWithRationale();
        }
    }


    //PERMISSIONS
    private static final int PERMISSIONS_REQUEST_CODE = 123;

    private boolean hasPermissions(){
        //String array of permissions
        String[] permissions = new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE};

        for (String perms : permissions){
            if (!(checkCallingOrSelfPermission(perms) == PackageManager.PERMISSION_GRANTED)){
                return false;
            }
        }
        return true;
    }
    private void requestPerms(){
        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            requestPermissions(permissions, PERMISSIONS_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean allowed = true;

        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE:
                for (int res : grantResults){
                    // if user granted all permissions.
                    allowed = allowed && (res == PackageManager.PERMISSION_GRANTED);
                }
                break;
            default:
                // if user not granted permissions.
                allowed = false;
                break;
        }
        if (allowed){
            //TO DO..
            getPresenter().setPERMISSIONS(true);
            getPresenter().init();
        }else {
            // we will give warning to user that they haven't granted permissions.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    onShowInfo(getString(R.string.permissions_storage_denied));
                }else {
                    showNoStoragePermissionsSnackbar();
                }
            }
        }
    }
    private void showNoStoragePermissionsSnackbar(){
        Snackbar.make(imageView, getString(R.string.permissions_storage_not_granted),Snackbar.LENGTH_LONG)
                .setAction("SETTINGS", v -> {
                    openApplicationSettings();

                    Toast.makeText(getApplicationContext(),
                            getString(R.string.permissions_grant_storage), Toast.LENGTH_LONG).show();
                }).show();
    }
    private void openApplicationSettings(){
        Intent appSettingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + getPackageName()));
        startActivityForResult(appSettingsIntent, PERMISSIONS_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PERMISSIONS_REQUEST_CODE){
            //TO DO..
            getPresenter().setPERMISSIONS(true);
            getPresenter().init();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void requestPermissionWithRationale(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)){

            Snackbar.make(imageView, getString(R.string.why_needed_grant), Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.GRANT), v -> requestPerms())
                    .show();
        }else {
            requestPerms();
        }
    }
}
