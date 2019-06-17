package com.example.client_binderservice.Service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.example.client_binderservice.ICallback;

public class LocalService extends Service implements IManager {
    private static final String TAG = "LocalService";

    private ICallback mCallback = null;
    private MyThread  mMyThread = null;

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return new LocalBinder();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public String getName() {
        return "Hello World";
    }

    @Override
    public void download() {
        mMyThread = new MyThread();
        mMyThread.start();
    }

    @Override
    public void registerCallback(ICallback callback) {
        mCallback = callback;
    }

    public class LocalBinder extends Binder {
        public LocalService getService() {
            return LocalService.this;
        }
    }

    private class MyThread extends Thread {
        @Override
        public void run() {
            for (int i = 0; i <= 100; i+=10) {
                try {
                    sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (mCallback != null) {
                    mCallback.onUpdateProgressBar(i);
                }
            }

            mCallback.onComplete();


        }
    }

}
