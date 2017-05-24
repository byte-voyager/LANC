package cc.alonebo.lanc.model.tools;


import android.content.Context;


import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import cc.alonebo.lanc.Constants;
import cc.alonebo.lanc.R;
import cc.alonebo.lanc.utils.LogUtils;
import cc.alonebo.lanc.utils.MD5Utils;
import cc.alonebo.lanc.utils.Utils;

/**
 * Created by alonebo on 17-5-1.
 */

public class FTPTransTool {
    private int mPort;
    private FtpServer mFtpServer;
    private static final String TAG = FTPTransTool.class.getName();
    public boolean mIsFtpRunning = false;
    public static final String FTP_PROP_NAME = "users.properties";
    public static final String FTP_PROP_SAVE_DIR = Utils.getFilsDir()+"/";
    public static final String FTP_PROP_SAVE_ABS_PATH = Utils.getContext().getFilesDir() +"/"+ FTP_PROP_NAME;
    private static FTPTransTool mFtpTransTool;

    public static FTPTransTool getInstance() {
        if (mFtpTransTool ==null) {
            synchronized (FTPTransTool.class) {
                if (mFtpTransTool ==null) {
                    mFtpTransTool = new FTPTransTool(Constants.PORT_FTP);
                }
            }
        }
        return mFtpTransTool;
    }

    private FTPTransTool(int port) {
        this.mPort = port;
        File file = new File(FTP_PROP_SAVE_ABS_PATH);
        if (!file.exists()) {
            copyPropToLocal();
        }
    }

    private void copyPropToLocal() {
        InputStream fin = Utils.getContext().getResources().openRawResource(R.raw.users);
        FileOutputStream fos = null;
        int length;
        try {
            fos = new FileOutputStream(new File(FTP_PROP_SAVE_ABS_PATH));
            byte[] buffer = new byte[1024];
            while ((length = fin.read(buffer)) != -1) {
                fos.write(buffer, 0, length);
            }
            LogUtils.e(TAG, "拷贝FTP配置信息成功!");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fin != null) {
                try {
                    fin.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * start ftp service,the default share directory is phone inner sdcard path
     */
    public void startFTPServer() {
        if (mFtpServer != null) {
            return;
        }
        FtpServerFactory serverFactory = new FtpServerFactory();
        ListenerFactory factory = new ListenerFactory();
        PropertiesUserManagerFactory userManagerFactory = new PropertiesUserManagerFactory();

        File files = new File(FTP_PROP_SAVE_ABS_PATH);

        userManagerFactory.setFile(files);
        serverFactory.setUserManager(userManagerFactory.createUserManager());
        // set the mPort of the listener
        factory.setPort(mPort);

        // replace the default listener
        serverFactory.addListener("default", factory.createListener());

        // start the server
        FtpServer server = serverFactory.createServer();
        mFtpServer =  server;
        try {
            server.start();
        } catch (FtpException e) {
            e.printStackTrace();
        }
        mIsFtpRunning = true;
    }


    public void stopServer() {
        if(mFtpServer==null) {
          return;
        }
        mFtpServer.stop();
        mFtpServer = null;
        mIsFtpRunning = false;
        LogUtils.e(TAG,"stop ftp server success!");
    }

    public void setUserName(String name) {

    }

    public static void setUserPwd(String filePath, String fileName, String userName, String pwd) {
        String content = userName+".userpassword";
        BufferedReader br = null;
        BufferedWriter bw = null;
        try {
            File bwFile = new File(filePath+"users_tmp");
            File brFile = new File(filePath+fileName);
            bw = new BufferedWriter(new FileWriter(bwFile));
            br = new BufferedReader(new FileReader(brFile));
            String line = "";
            String md5Pwd = MD5Utils.encrypt(pwd);
            while((line = br.readLine())!=null) {
                if(line.contains(content)) {
                    int index = line.lastIndexOf('=');
                    String sub = line.substring(0, index);
                    System.out.println(sub);
                    String newLine = sub+"="+md5Pwd;
                    System.out.println(newLine);
                    line = newLine;
                }
                bw.write(line+"\n");
                bw.flush();
            }

            brFile.delete();
            bwFile.renameTo(brFile);
            LogUtils.e(TAG,"set ftp pwd success");
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                if(br!=null) {
                    br.close();
                }
                if(bw!=null) {
                    bw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public  void setDefaultFTPPwd(String filePath, String pwd) {
        setUserPwd(filePath, FTP_PROP_NAME, Constants.NAME_DEFAULT_FTP_USER_NAME, pwd);
    }

    public  void setFTPShareDir(String filePath, String fileName, String shareDir) {
        String content = ".homedirectory=";
        BufferedReader br = null;
        BufferedWriter bw = null;
        try {
            File bwFile = new File(filePath+"users_tmp");
            File brFile = new File(filePath+fileName);
            bw = new BufferedWriter(new FileWriter(bwFile));
            br = new BufferedReader(new FileReader(brFile));
            String line = "";
            while((line = br.readLine())!=null) {
                if(line.contains(content)) {
                    int index = line.lastIndexOf('=');
                    String sub = line.substring(0, index);
                    System.out.println(sub);
                    String newLine = sub+"="+shareDir;
                    System.out.println(newLine);
                    line = newLine;
                }
                bw.write(line+"\n");
                bw.flush();
            }

            brFile.delete();
            bwFile.renameTo(brFile);
            System.out.println("rename success!");
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                if(br!=null) {
                    br.close();
                }
                if(bw!=null) {
                    bw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public boolean isRunning() {
        return mIsFtpRunning;
    }


    public void setFTPShareDir(String shareDir) {
        setFTPShareDir(FTP_PROP_SAVE_DIR,FTP_PROP_NAME,shareDir);
    }
}
