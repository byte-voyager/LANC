package cc.alonebo.lanc.view;

import android.graphics.Bitmap;

/**
 * Created by alonebo on 17-5-17.
 */

public interface IFragOperateView {
    void updateAvatar(Bitmap bitmap);
    void updateName(String name);
    void showFtp(boolean isShow);
    void updateIp(String ip);
    void showParallaxBackground(String image);
}
