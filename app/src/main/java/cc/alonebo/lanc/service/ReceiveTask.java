package cc.alonebo.lanc.service;

import android.os.AsyncTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import cc.alonebo.lanc.Constants;
import cc.alonebo.lanc.model.bean.UdpTransMsg;
import cc.alonebo.lanc.model.listener.ReceiveListener;
import cc.alonebo.lanc.model.tools.UdpTransTool;
import cc.alonebo.lanc.utils.LogUtils;
import cc.alonebo.lanc.utils.UdpTransMsgFactory;
import cc.alonebo.lanc.utils.Utils;


/**
 * Created by alonebo on 17-3-30.
 * 用来接受文件,此类应该先要创建,此类实现是ServerSocket,端口为8902
 */

public class ReceiveTask extends AsyncTask<UdpTransMsg,Integer,Integer> {

    private String TAG = ReceiveTask.class.getName();

    private static final int TYPE_SUCCESS = 0;
    private static final int TYPE_FAILED = 1;
    private static final int TYPE_CANCEL = 2;
    private  boolean mIsCancel = false;
    private int mProgress = 0;
    private  boolean mFlag = true;
    private String mFilePath = "";

    private ReceiveListener listener;

    public ReceiveTask(ReceiveListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected Integer doInBackground(UdpTransMsg... params) {
        UdpTransMsg msg = params[0];//得到msg
        ArrayList<String> fileNameList = msg.getFilePath();
        LogUtils.e(TAG,fileNameList.get(0));
        String fileName = fileNameList.get(0).substring(fileNameList.get(0).lastIndexOf("/"));
        File file = new File(Utils.getExtSDCardDownloadPath()+fileName);

        fileName = checkFile(file,fileName);
        file = new File(Utils.getExtSDCardDownloadPath()+fileName);

        mFilePath = Utils.getExtSDCardDownloadPath()+fileName;

        ServerSocket ss = null;

        InputStream is = null;
        FileOutputStream fos = null;
        double fileSize = msg.getFileSize().get(0);


        new Thread(new Runnable() {

            @Override
            public void run() {
                while (mProgress <=100 && mFlag) {
                    if (mProgress ==100) mFlag =false;
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Utils.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onProgress(mProgress);
                        }
                    });
                }
            }
        }).start();

        //此方法就是在子线程
        try {
            ss = new ServerSocket(Constants.PROT_TCP_FILE_PROT);//创建一个服务端接收文件,指定端口
            sendReady(msg);

            Constants.IS_TRANSINT_FILE= true;

            LogUtils.e(TAG,"ss.accept();//阻塞");
            Socket s = ss.accept();//阻塞

            LogUtils.e(TAG,"在ReciveTask类得到了一个连接对象,对象的ip:"+s.getInetAddress().getHostAddress());
            is = s.getInputStream();//得到输入流,没有得到会阻塞
            byte[] buff = new byte[1024];
            fos = new FileOutputStream(file);
            int len = -1;
            double total = 0;
            LogUtils.e("Tag","开始文件的传输");
            while ((len = is.read(buff))!=-1) {
                if (mIsCancel) {
                    return TYPE_CANCEL;
                }else {
                    total += len;
                }
                fos.write(buff,0,len);
                mProgress =  (int)((total/fileSize)*100);
            }
            fos.flush();
            LogUtils.e(TAG,"已经完成文件的传输");
            if (total==fileSize)
                return TYPE_SUCCESS;
            else
                return TYPE_FAILED;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
             try {
                 //关闭各种流
                 if (is!=null) is.close();
                 if (fos!=null) fos.close();
                 if (ss!=null) ss.close();
            } catch (IOException e) {
                e.printStackTrace();
                 return TYPE_FAILED;
            }
        }
        return TYPE_FAILED;
    }

    private int count = 1;
    private String checkFile(File file, String fileName) {
        if (fileName.lastIndexOf('.')!=-1) {
            StringBuilder sb = new StringBuilder(fileName.substring(0,fileName.lastIndexOf(".")));//不带点//包前不包后
            if (file.exists()&&count<=3) {

                LogUtils.e(TAG,"文件存在....");
                count++;
                //文件存在
                sb.append("[").append(count).append("]").append(fileName.substring(fileName.lastIndexOf(".")));//带点
                LogUtils.e(TAG,sb.toString());
                file = new File(Utils.getExtSDCardDownloadPath()+sb.toString());
                checkFile(file,fileName);
            }
            return sb.append(fileName.substring(fileName.lastIndexOf("."))).toString();
        } else {
            if (file.exists()&&count<=3) {
                count++;
                file = new File(Utils.getExtSDCardDownloadPath()+fileName+"["+count+"]");
                checkFile(file,fileName+"["+count+"]");
            }
            return fileName+"["+count+"]";
        }

    }


    @Override
    protected void onPostExecute(Integer result) {
        switch (result) {
            case TYPE_CANCEL:
                mFlag = false;
                deleteCacheFile();
                listener.onCanceled();
                break;
            case TYPE_FAILED:
                deleteCacheFile();
                mFlag = false;
                listener.onFailed();
                break;
            case TYPE_SUCCESS:
                mFlag = false;
                listener.onSuccess();
                break;
        }
    }


    /**
     * @param msg
     * 发送一个OK
     */
    private void sendReady(UdpTransMsg msg) {
        UdpTransTool.getInstance().sendMsg(UdpTransMsgFactory.getReadyReciveFileTransMsg(msg.getSenderIP(),msg.getFileName()));
        LogUtils.e(TAG,"发送了OK");
    }


    public void cancelReceive() {
        mIsCancel = true;
    }

    private void deleteCacheFile() {
        if ("".equals(mFilePath)) return;
        File file = new File(mFilePath);
        if (file.exists()) {
            file.delete();
            LogUtils.e(TAG,"删除文件成功!");
        }
    }

}
