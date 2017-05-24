package cc.alonebo.lanc.model.impl;

import java.io.File;

import cc.alonebo.lanc.model.IFileModel;
import cc.alonebo.lanc.utils.LogUtils;

/**
 * Created by alonebo on 17-5-24.
 */

public class FileModelImpl implements IFileModel {
    private String TAG = FileModelImpl.class.getName();
    @Override
    public void delAvatarFile(String deviceIdent,String dir) {
        File file = new File(dir);
        File[] files = file.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].getName().startsWith(deviceIdent)) {
                files[i].delete();
                LogUtils.e(TAG,"删除头像成功:"+deviceIdent);
            }
        }

    }
}
