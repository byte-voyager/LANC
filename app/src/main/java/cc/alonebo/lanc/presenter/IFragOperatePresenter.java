package cc.alonebo.lanc.presenter;

import android.app.Activity;

/**
 * Created by alonebo on 17-5-17.
 */

public interface IFragOperatePresenter {
    void setAvatar(Activity context);
    void setName(String name);
    void switchFtp(boolean isOpen);
    void cleanData();
    void exitApp();
    void setIp();
    void setParallaxBackground();
    void setBingWall();

}
