package cc.alonebo.lanc.service;

import android.os.AsyncTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import cc.alonebo.lanc.Constants;
import cc.alonebo.lanc.model.bean.UdpTransMsg;
import cc.alonebo.lanc.model.listener.UploadListener;
import cc.alonebo.lanc.utils.LogUtils;
import cc.alonebo.lanc.utils.Utils;


/**
 * Created by alonebo on 17-3-30.
 */

public class UploadTask extends AsyncTask<UdpTransMsg,Integer,Integer> {
    private String TAG = UploadTask.class.getName();
    private UploadListener mListener;
    private static final int TYPE_SUCCESS = 0;
    private static final int TYPE_FAILED = 1;
    private static final int TYPE_CANCEL = 2;
    private static boolean mIsCancel = false;
    private int mProgress = 0;

    public UploadTask(UploadListener listener) {
        this.mListener = listener;
    }

    @Override
    protected Integer doInBackground(UdpTransMsg... params) {
        LogUtils.e(TAG,"开始发送文件.....");
        UdpTransMsg msg = params[0];
        String getSenderIP = msg.getSenderIP();
        String filePath = msg.getFilePath().get(0);
        File file = new File(filePath);
        if (!file.exists()){
            LogUtils.e(TAG,"File Not Found!");
        }
        //创建一个Socket来上传服务
        double fileSize = file.length();//k
        Socket s = null;
        try {
            LogUtils.e(TAG,"创建Socket");
            s = new Socket(getSenderIP, Constants.PROT_TCP_FILE_PROT);

            Constants.IS_TRANSINT_FILE = true;

            OutputStream os = s.getOutputStream();
            FileInputStream fis = new FileInputStream(file);
            byte[] buff = new byte[1024];
            double total = 0;
            int len = -1;

            new Thread(new Runnable() {
                boolean flag = true;
                @Override
                public void run() {
                    while (mProgress <=100 && flag) {
                        if (mProgress ==100) flag=false;
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Utils.runOnUIThread(new Runnable() {
                            @Override
                            public void run() {
                                mListener.onProgress(mProgress);
                            }
                        });
                    }
                }
            }).start();

            while ((len = fis.read(buff))!=-1) {
                if (mIsCancel) {
                    return TYPE_CANCEL;
                }else {
                    total += len;
                }
                os.write(buff,0,len);
                mProgress =  (int)((total/fileSize)*100);
               // LogUtils.e(TAG,"mProgress--------------->>>"+ mProgress);
            }

            s.shutdownOutput();
            if (total==fileSize)
                return TYPE_SUCCESS;
            else
                return TYPE_FAILED;
        } catch (IOException e) {
            e.printStackTrace();
            return TYPE_FAILED;
        } finally {
            try {
                if (s!=null)
                    s.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPostExecute(Integer result) {
        switch (result) {
            case TYPE_CANCEL:
                mListener.onCanceled();
                break;
            case TYPE_FAILED:
                mListener.onFailed();
                break;
            case TYPE_SUCCESS:
                mListener.onSuccess();
                break;
        }
    }

    public void cancelUpload() {
        mIsCancel = true;
    }

}
