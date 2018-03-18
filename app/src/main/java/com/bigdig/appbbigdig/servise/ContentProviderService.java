package com.bigdig.appbbigdig.servise;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.bigdig.appbbigdig.R;

import java.util.Calendar;
import java.util.GregorianCalendar;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ContentProviderService extends Service {
    private NotificationManager nm;
    private CountDownTimer countDownTimer;
    public static final Uri URI_HISTORY = Uri.parse(
            "content://" + "com.bigdig.appabigdig.repository" + "/" + "history");
    private int STATUS_VALUE = 0;
    private final String STATUS_KEY = "status";
    private String URL_VALUE;
    private final String URL_KEY = "url";
    private final String TIME_KEY = "openTime";
    private long TIME_VALUE;
    private long _ID = 0;
    private String TASK;

    public ContentProviderService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("TAG", "onStartCommand");
        prepareData(intent);
        doTask();
        return super.onStartCommand(intent, flags, startId);
    }
    private void prepareData(Intent intent){
        URL_VALUE = intent.getStringExtra(getString(R.string.intent_filter_key_url));
        STATUS_VALUE = intent.getIntExtra(getString(R.string.intent_filter_key_state), 0);
        _ID = intent.getLongExtra(getString(R.string.intent_filter_key_ID), 0);
        TASK = intent.getStringExtra(getString(R.string.string_status));
        TIME_VALUE  = new GregorianCalendar().getTimeInMillis();
    }

    void doTask(){
        if(TextUtils.equals(TASK, getString(R.string.string_status_add))){
            add();
        }else  if(TextUtils.equals(TASK, getString(R.string.string_status_delete))){
            delete();
        }else  if(TextUtils.equals(TASK, getString(R.string.string_status_update))){
            update();
        }
    }

    private void add(){
        Single add = Single.create(emitter -> {
            ContentValues contentValues = new ContentValues();
            contentValues.put(URL_KEY, URL_VALUE);
            contentValues.put(TIME_KEY,  TIME_VALUE);
            contentValues.put(STATUS_KEY, STATUS_VALUE);
            emitter.onSuccess(getContentResolver().insert(URI_HISTORY, contentValues));
        } );
        add.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> {
                    onDestroy();
                });
    }

    private void delete(){
        Single update = Single.create(emitter -> {
            Uri uri = ContentUris.withAppendedId(URI_HISTORY, _ID);
            emitter.onSuccess(getContentResolver().delete(uri,  null, null));
        } );
        update.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> {
                    toDoAfterDelete();
                });
    }

    private void update(){
        Single update = Single.create(emitter -> {
            ContentValues contentValues = new ContentValues();
            contentValues.put(URL_KEY, URL_VALUE);
            contentValues.put(TIME_KEY,  TIME_VALUE);
            contentValues.put(STATUS_KEY, STATUS_VALUE);
            Uri uri = ContentUris.withAppendedId(URI_HISTORY, _ID);
            emitter.onSuccess(getContentResolver().update(uri, contentValues, null, null));
        } );
        update.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> {
                    onDestroy();
                });
    }
    void toDoAfterDelete(){
        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        long timeToClose = 15 * 1000;
        long timeTickInterval = 1000;
        countDownTimer = new CountDownTimer(timeToClose, timeTickInterval) {
            @Override
            public void onTick(long millisUntilFinished) {
            }
            @Override
            public void onFinish() {
                sendNotif();
            }
        };

        countDownTimer.start();
    }
    void sendNotif(){
        Notification.Builder builder = new Notification.Builder(getApplicationContext());
        builder.setTicker(getText(R.string.text_image_removed))
                .setWhen(TIME_VALUE)
                .setAutoCancel(true)
                .setContentTitle(getText(R.string.text_image_removed));
        Notification notification = builder.build();
        nm.notify(564, notification);

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
            }
