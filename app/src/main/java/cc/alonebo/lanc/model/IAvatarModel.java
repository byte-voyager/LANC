package cc.alonebo.lanc.model;

import android.graphics.Bitmap;

/**
 * Created by alonebo on 17-5-17.
 */

public interface IAvatarModel {
    boolean isNeedAvatar(String deviceIdent,long avatarTime);
    void saveContactAvatar(String deviceIdent, long newTime, byte[] avatar, String saveDir);
    byte[] getAvatar(String deviceIdent);
    Bitmap getAvatarBitmap(String deviceIdent);
    void setMyAvatar(String filePath);
}
