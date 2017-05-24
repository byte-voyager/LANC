package cc.alonebo.lanc.model;

/**
 * Created by alonebo on 17-5-21.
 */

public interface IFtpModel {
    boolean start();
    void stop();
    void setUserPwd(String pwd);
    String getPwd();
    void setShareDir(String shareDir);
    void setUserName(String name);
}
