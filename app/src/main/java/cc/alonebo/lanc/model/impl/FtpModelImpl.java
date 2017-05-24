package cc.alonebo.lanc.model.impl;

import android.content.Intent;

import org.apache.ftpserver.ftplet.FtpException;

import cc.alonebo.lanc.model.IFtpModel;
import cc.alonebo.lanc.model.tools.FTPTransTool;
import cc.alonebo.lanc.service.FtpService;
import cc.alonebo.lanc.utils.Utils;

/**
 * Created by alonebo on 17-5-21.
 */

public class FtpModelImpl implements IFtpModel {
    FTPTransTool mFtpTransTool = FTPTransTool.getInstance();
    public static boolean IS_FTP_RUNNING = false;
    @Override
    public boolean start() {
        Utils.getContext().startService(new Intent(Utils.getContext(), FtpService.class));
        IS_FTP_RUNNING = true;
        return false;
    }

    @Override
    public void stop() {
        Utils.getContext().stopService(new Intent(Utils.getContext(),FtpService.class));
        IS_FTP_RUNNING = false;
    }

    @Override
    public void setUserPwd(String pwd) {

    }

    @Override
    public String getPwd() {
        return null;
    }


    @Override
    public void setShareDir(String shareDir) {
        mFtpTransTool.setFTPShareDir(shareDir);
    }

    @Override
    public void setUserName(String name) {
        mFtpTransTool.setUserName(name);
    }


}
