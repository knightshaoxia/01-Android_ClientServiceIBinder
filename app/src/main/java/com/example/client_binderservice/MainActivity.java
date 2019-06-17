package com.example.client_binderservice;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.client_binderservice.Service.IManager;
import com.example.client_binderservice.Service.LocalService;


public class MainActivity extends AppCompatActivity implements ICallback {
    private static final String TAG = "MainActivity";

    private static final int MSG_UPDATE_PROGRESSBAR   = 200;
    private static final int MSG_DOWNLOAD_COMPLETION  = 201;
    private static final int MSG_ERROR_MSG            = 202;

    private Context  mContext;
    private IManager mManager;
    private MyHandler mMyHandler;
    private ProgressBar mProgressBar;

    private ServiceConnection mConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "onServiceConnected: succeed to connect service.");
            mManager = ((LocalService.LocalBinder)service).getService();
            mManager.registerCallback(MainActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "onServiceDisconnected: Disconnect service.");
            mManager = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "onCreate: -->");

        mProgressBar = findViewById(R.id.progress_bar);
        mContext = getApplicationContext();

        mMyHandler = new MyHandler();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart: --->");
        mProgressBar.setVisibility(View.INVISIBLE);
        Intent intent = new Intent(mContext, LocalService.class);
        bindService(intent, mConn, BIND_AUTO_CREATE);
        Message.obtain(mMyHandler, MSG_ERROR_MSG, 0, 0, "Debug--->").sendToTarget();
    }

    @Override
    protected void onStop() {
        Log.i(TAG, "onStop: --->");
        if (mManager != null) {
            unbindService(mConn);
        }

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy: --->");
        mMyHandler = null;
        super.onDestroy();
    }

    @Override
    public void onError(String errMsg) {

    }

    //Callback from Service.
    @Override
    public void onUpdateProgressBar(int progress) {
        Log.i(TAG, "onUpdateProgressBar: -->");
        Message.obtain(mMyHandler, MSG_UPDATE_PROGRESSBAR, progress, 0, null).sendToTarget();
    }

    @Override
    public void onComplete() {
        Log.i(TAG, "onComplete: -->");
        Message.obtain(mMyHandler, MSG_DOWNLOAD_COMPLETION, 0, 0, null).sendToTarget();
    }

    //Callback from UI
    void onGetName(View view) {
        if (mManager != null) {
            Toast.makeText(mContext, " Get name from service: " + mManager.getName(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, " Service is not connected", Toast.LENGTH_SHORT).show();
        }
    }

    void onStartDownload(View view) {
        if (mManager != null) {
            Toast.makeText(mContext, " Start to download from service.", Toast.LENGTH_SHORT).show();
            mManager.download();
        } else {
            Toast.makeText(mContext, " Service is not connected", Toast.LENGTH_SHORT).show();
        }
    }


    private class MyHandler extends Handler {
        @Override
        public void dispatchMessage(Message msg) {
            switch (msg.what) {
                case MSG_UPDATE_PROGRESSBAR:
                    Log.i(TAG, "dispatchMessage: MSG_UPDATE_PROGRESSBAR: " + msg.arg1);
                    mProgressBar.setVisibility(View.VISIBLE);
                    Log.i(TAG, "dispatchMessage: update progress bar position:" + msg.arg1);
                    mProgressBar.setProgress(msg.arg1);
                    break;
                case MSG_DOWNLOAD_COMPLETION:
                    Log.i(TAG, "dispatchMessage: MSG_DOWNLOAD_COMPLETION.");
                    mProgressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(mContext, "Download complete!", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_ERROR_MSG:
                    Log.i(TAG, "dispatchMessage: MSG_ERROR_MSG: " + msg.obj);
                    Toast.makeText(mContext, "Error: " + msg.obj, Toast.LENGTH_SHORT).show();
                    break;
            }
        }

    }
}
