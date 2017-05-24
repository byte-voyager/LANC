package cc.alonebo.lanc.model.impl;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import cc.alonebo.lanc.Constants;
import cc.alonebo.lanc.R;
import cc.alonebo.lanc.db.dao.AvatarDao;
import cc.alonebo.lanc.db.dao.ContactDao;
import cc.alonebo.lanc.model.IAvatarModel;
import cc.alonebo.lanc.utils.LogUtils;
import cc.alonebo.lanc.utils.SPUtils;
import cc.alonebo.lanc.utils.Utils;

/**
 * Created by alonebo on 17-5-17.
 */

public class AvatarModelImpl implements IAvatarModel{

    private String TAG = AvatarModelImpl.class.getName();
    private AvatarDao mAvatarDao = AvatarDao.getIntance(Utils.getContext());
    private ContactDao mContactDao = ContactDao.getInstance(Utils.getContext());
    private FileModelImpl mFileModel = new FileModelImpl();

    @Override
    public boolean isNeedAvatar(String deviceIdent, long avatarTime) {
        if (avatarTime==0) {
            LogUtils.e(TAG,"对方头像时间为0,不需要请求");
            return false;
        }
        return mAvatarDao.isNeedUpdateAvatar(deviceIdent,avatarTime);

    }

    @Override
    public void saveContactAvatar(String deviceIdent, long newTime, byte[] avatar, String saveDir) {

        //default save to package name/files/ dir
        if (avatar==null) {
            return;
        }
        mFileModel.delAvatarFile(deviceIdent,Utils.getFilsDir());

        final File file = new File(saveDir+deviceIdent+"_"+newTime+".png");
        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(file));
            try {
                bos.write(avatar,0,avatar.length);
                bos.flush();
                mAvatarDao.insertAvatar(deviceIdent,newTime);
                mContactDao.updateAvaratTime(deviceIdent,newTime);
                LogUtils.e(TAG,"save: "+file.getName()+" success!");

            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }finally {
            if (bos!=null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    public byte[] getAvatar(String deviceIdent) {
        return new byte[0];
    }

    @Override
    public Bitmap getAvatarBitmap(String deviceIdent) {
        return null;
    }

    @Override
    public void setMyAvatar(String filePath) {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        BufferedInputStream bis = null;
        final File file = new File(filePath);

        try {
            fis = new FileInputStream(file);
            bis = new BufferedInputStream(fis);
            fos = Utils.getContext().openFileOutput(Constants.NAME_AVATAR, Context.MODE_PRIVATE);
            int len = -1;
            byte[] buff = new byte[1024];
            while ((len=bis.read(buff))!=-1) {
                fos.write(buff,0,len);
            }

            LogUtils.i(TAG,"拷贝成功");

            SPUtils.put(Utils.getContext(),Constants.SP_HAVE_CUSTOM_AVATAR,true);
            SPUtils.put(Utils.getContext(),Constants.SP_AVATAR_TIME,Utils.getCurrentTime());
        } catch (Exception e) {

            e.printStackTrace();
        }
        finally {
            try{
                if (fis!=null) fis.close();
                if (bis!=null) bis.close();
                if (fos!=null) fos.close();
            }catch (Exception e )
            {
                e.printStackTrace();
            }

        }
    }


}
