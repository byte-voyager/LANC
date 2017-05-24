package cc.alonebo.lanc.presenter.impl;

import android.content.Context;
import android.content.Intent;

import cc.alonebo.lanc.presenter.IActMainPresenter;
import cc.alonebo.lanc.service.TransService;
import cc.alonebo.lanc.utils.LogUtils;
import cc.alonebo.lanc.view.IActMainView;

/**
 * Created by alonebo on 17-5-16.
 */

public class ActMainPresenterImpl implements IActMainPresenter {
    private String TAG = ActMainPresenterImpl.class.getName();

    IActMainView mainView;

    public ActMainPresenterImpl(IActMainView mainView){
        this.mainView = mainView;
    }

    @Override
    public void showView() {
        mainView.initToolBar();
        mainView.initView();
        mainView.initData();
    }


    @Override
    public void startTransService(Context context) {
        context.startService(new Intent(context, TransService.class));
        LogUtils.e(TAG,"TransService 开启TransService成功");
    }


}
