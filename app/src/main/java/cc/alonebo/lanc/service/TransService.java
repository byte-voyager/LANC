package cc.alonebo.lanc.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import org.greenrobot.eventbus.EventBus;

import cc.alonebo.lanc.presenter.impl.TransPresenterImpl;
import cc.alonebo.lanc.utils.LogUtils;

public class TransService extends Service {
    private String TAG = TransService.class.getName();



    TransPresenterImpl transPresenter = TransPresenterImpl.getInstance();
    public TransService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.e(TAG,"onCreate Executed");
        transPresenter.startReceiveTcpMsg();
        transPresenter.startReceiveUdpMsg();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        transPresenter.unRegist();
    }
}
