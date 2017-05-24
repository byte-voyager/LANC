package cc.alonebo.lanc.presenter.impl;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.nfc.Tag;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import cc.alonebo.lanc.Constants;
import cc.alonebo.lanc.MyApplication;
import cc.alonebo.lanc.R;
import cc.alonebo.lanc.model.Net;
import cc.alonebo.lanc.model.bean.EventAvatar;
import cc.alonebo.lanc.model.bean.EventIp;
import cc.alonebo.lanc.model.bean.UdpTransMsg;
import cc.alonebo.lanc.model.impl.AvatarModelImpl;
import cc.alonebo.lanc.model.impl.DownloadManager;
import cc.alonebo.lanc.model.impl.FtpModelImpl;
import cc.alonebo.lanc.model.listener.LoadUriSuccessListener;
import cc.alonebo.lanc.model.tools.FTPTransTool;
import cc.alonebo.lanc.model.tools.UdpTransTool;
import cc.alonebo.lanc.presenter.IFragOperatePresenter;
import cc.alonebo.lanc.utils.LogUtils;
import cc.alonebo.lanc.utils.SPUtils;
import cc.alonebo.lanc.utils.UdpTransMsgFactory;
import cc.alonebo.lanc.utils.Utils;
import cc.alonebo.lanc.view.IFragOperateView;
import droidninja.filepicker.FilePickerBuilder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;

/**
 * Created by alonebo on 17-5-17.
 */

public class FragOperatePresenterImpl implements IFragOperatePresenter,LoadUriSuccessListener {
    private String TAG = FragOperatePresenterImpl.class.getName();
    private IFragOperateView mFragOperateView;
    private ArrayList<String> mAvatarList = new ArrayList<>();
    private AvatarModelImpl mAvatarModel;
    private UdpTransTool mUdpTransTool = UdpTransTool.getInstance();
    private FtpModelImpl mFtpModel;

    public FragOperatePresenterImpl(IFragOperateView fragOperateView){
        mFragOperateView = fragOperateView;
        mAvatarModel = new AvatarModelImpl();
        mFtpModel = new FtpModelImpl();
        EventBus.getDefault().register(this);
    }

    /**
     * From MainActivity
     * @param eventAvatar
     */
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void eventUpdateAvatar(final EventAvatar eventAvatar) {

        File file = new File(eventAvatar.getAvatarPath());
        if (file.length()/1024>200) {
            Utils.runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    Utils.showToast(Utils.getContext(),Utils.getString(R.string.toast_too_big_avatar));
                }
            });
        }else {
            mAvatarModel.setMyAvatar(eventAvatar.getAvatarPath());
            Utils.runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    mFragOperateView.updateAvatar(BitmapFactory.decodeFile(eventAvatar.getAvatarPath()));
                }
            });
            mUdpTransTool.sendMsg(UdpTransMsgFactory.getUpdateAvatarMsg());
        }
    }

    /**
     * From NetWorkBroadCast
     * @param eventIp
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventUpdateIp(EventIp eventIp) {
        mFragOperateView.updateIp(eventIp.getIp());
    }

    @Override
    public void setAvatar(Activity activity) {
        FilePickerBuilder.getInstance().setMaxCount(1)
                .setSelectedFiles(mAvatarList)
                .enableDocSupport(false)
                .pickPhoto(activity);
    }

    @Override
    public void setName(String name) {
        SPUtils.put(Utils.getContext(), Constants.SP_DEVICE_NAME,name);
        mFragOperateView.updateName(name);
    }

    @Override
    public void switchFtp(boolean isOpen) {
        if (isOpen) {
          mFtpModel.start();
        }else {
          mFtpModel.stop();
        }
       mFragOperateView.showFtp(isOpen);

    }

    @Override
    public void cleanData() {

    }

    @Override
    public void exitApp() {
        for (int i = 0; i < MyApplication.activityList.size(); i++) {
            if (!MyApplication.activityList.get(i).isFinishing()) {
                MyApplication.activityList.get(i).finish();
            }
        }
        System.exit(0);
    }

    @Override
    public void setIp() {

    }

    @Override
    public void setParallaxBackground() {
        boolean isOpenGetBingPic = (boolean) SPUtils.get(Utils.getContext(),Utils.getContext().getResources().getString(R.string.is_open_bing),true);
        Net net = new Net();
        net.getBintPicLink(this);
        if (isOpenGetBingPic) {
//            Net net = new Net();
//            net.getBintPicLink(this);
        }else {
            String picLink = (String) SPUtils.get(Utils.getContext(),Constants.SP_BING_LINK,"");
            mFragOperateView.showParallaxBackground(picLink);
        }


    }
    private boolean mDwonloadIng = false;
    @Override
    public void setBingWall() {
        if (!mDwonloadIng) {
            String picLink = (String) SPUtils.get(Utils.getContext(),Constants.SP_BING_LINK,"");
            if ("".equals(picLink)){
                Utils.showToast(Utils.getContext(),"设置壁纸异常!");
                return;
            }
            DownloadManager downloadManager = DownloadManager.get();
            downloadManager.download(picLink, Utils.getExtSDCardDownloadPath(), new DownloadManager.OnDownloadListener() {
                @Override
                public void onDownloadSuccess() {
                    Utils.showToast(Utils.getContext(),"已经下载到:"+Utils.getExtSDCardDownloadPath()+"目录");
                }

                @Override
                public void onDownloading(int progress) {
                    LogUtils.e(TAG,"download ing!");
                }

                @Override
                public void onDownloadFailed() {
                    LogUtils.e(TAG,"download onDownloadFailed!");
                }
            });
        }else {
            Utils.showToast(Utils.getContext(),"Downloading...");
        }


    }

    @Override
    public void onSuccess(final String result) {
        SPUtils.put(Utils.getContext(),Constants.SP_BING_LINK,result);

        Utils.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                //下载壁纸

                mFragOperateView.showParallaxBackground(result);
            }
        });
    }

    @Override
    public void onFailure() {
        final String picLink = (String) SPUtils.get(Utils.getContext(),Constants.SP_BING_LINK,"");
        Utils.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                mFragOperateView.showParallaxBackground(picLink);
            }
        });
    }

    public void unRegist(){
        EventBus.getDefault().unregister(this);
    }
}
