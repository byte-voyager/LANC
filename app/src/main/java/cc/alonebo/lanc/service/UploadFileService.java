package cc.alonebo.lanc.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import cc.alonebo.lanc.Constants;
import cc.alonebo.lanc.R;
import cc.alonebo.lanc.model.bean.EventStopTFService;
import cc.alonebo.lanc.model.bean.UdpTransMsg;
import cc.alonebo.lanc.model.listener.UploadListener;
import cc.alonebo.lanc.utils.LogUtils;
import cc.alonebo.lanc.utils.Utils;


/**
 * Created by alonebo on 17-4-10.
 */

public class UploadFileService extends Service {
    private String TAG = UploadFileService.class.getName();
    private UploadTask mUploadTask;
    private boolean mUploadSuccess = false;
    private boolean mUploadFailed = false;
    private String mFileName = "";
    private String mReciver = "";
    private int TYPE_START_UPLOAD = 0;
    private int TYPE_SUCCESS_UPLOAD = 1;
    private int TYPE_FAILED_UPLOAD = 2;
    private int mProgress = 0;
    private Runnable command = new Runnable() {
        @Override
        public void run() {
                  if (mProgress<=0) {
                      mUploadTask.cancelUpload();
                  }
        }
    };

    private UploadListener listener  = new UploadListener() {
        @Override
        public void onProgress(int progress) {
            mProgress = progress;
            if (!mUploadSuccess && !mUploadFailed) {

                LogUtils.e(TAG,"上传进度:"+progress);
                getNotificationManager().notify(Constants.TYPE_NOTIFICATION_UPLOAD_FILE,getNotification("上传"+ mFileName +"中...",progress));
            }


        }

        @Override
        public void onSuccess() {
            Constants.IS_TRANSINT_FILE = false;
            mUploadTask = null;
            stopForeground(true);
            mUploadSuccess =true;
            getNotificationManager().cancel(Constants.TYPE_NOTIFICATION_UPLOAD_FILE);
            Utils.showToast(Utils.getContext(),"上传成功");
            stopService();
            showUploadNoitfy(TYPE_SUCCESS_UPLOAD);
        }

        @Override
        public void onFailed() {

            Constants.IS_TRANSINT_FILE = false;
            mUploadFailed = true;
            Utils.showToast(Utils.getContext(),"上传失败");
            stopForeground(true);
            getNotificationManager().cancel(Constants.TYPE_NOTIFICATION_UPLOAD_FILE);
            showUploadNoitfy(TYPE_FAILED_UPLOAD);
            stopService();
        }

        @Override
        public void onCanceled() {
            Constants.IS_TRANSINT_FILE = false;
            mUploadFailed = true;
            Utils.showToast(Utils.getContext(),"上传失败");
            stopForeground(true);
            getNotificationManager().cancel(Constants.TYPE_NOTIFICATION_UPLOAD_FILE);
            stopService();
        }
    };

    public Notification getNotification(String title, int progress) {

//        Intent intent = new Intent(this, CancelTransFile.class);
//        PendingIntent pi = PendingIntent.getActivity(this,0,intent,0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(Utils.getContext());
        builder.setContentTitle(title);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setLargeIcon(BitmapFactory.decodeResource(Utils.getContext().getResources(), R.mipmap.ic_launcher));
//        builder.setContentIntent(pi);
        if (progress>0) {
            builder.setContentText("已上传:"+progress+"%");
            builder.setProgress(100,progress,false);
        }

        return builder.build();
    }


    private NotificationManager getNotificationManager() {
        NotificationManager manager = (NotificationManager) Utils.getContext().getSystemService(NOTIFICATION_SERVICE);
        return manager;
    }

    private  UploadBinder uploadBinder = new UploadBinder();
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        LogUtils.e(TAG,"返回 uploadBinder");
        return uploadBinder;
    }

    public class UploadBinder extends Binder {
        public void startUpload(UdpTransMsg msg) {
            if (mUploadTask == null) {
                mUploadTask = new UploadTask(listener);
                if (msg==null) {
                    LogUtils.e(TAG,"msg为空");
                    return;
                }
                mFileName =Utils.getFileName(msg.getFilePath().get(0));
                mUploadTask.execute(msg);
                startForeground(Constants.TYPE_NOTIFICATION_UPLOAD_FILE,getNotification("上传"+ mFileName +"中...",0));
                showUploadNoitfy(TYPE_START_UPLOAD);
                ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
                scheduledExecutorService.schedule(command,6000, TimeUnit.MILLISECONDS);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.e(TAG,"服务销毁");
    }

    private void stopService(){
        Intent intent = new Intent(Utils.getContext(),UploadFileService.class);
        stopService(intent);
        Log.e(TAG,"UploadFileService has exec stop()");
        EventStopTFService event = new EventStopTFService(EventStopTFService.STOP_TYPE_UPLOAD);//TransPresenterImpl
        EventBus.getDefault().post(event);
    }

    private void showUploadNoitfy(int type) {
        NotificationManager manager = (NotificationManager) Utils.getContext().getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(Utils.getContext());
        Notification notification = null;
        String title = "";
        if (type == TYPE_START_UPLOAD) {
            title = "开始上传文件";
        }
        if (type == TYPE_SUCCESS_UPLOAD) {
            title = "上传文件成功";
        }
        if (type == TYPE_FAILED_UPLOAD) {
            title = "上传文件失败";
        }
        notification = builder
                .setContentTitle(title).setColor(Color.BLACK)
                .setContentText("文件名:"+ mFileName)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(Utils.getContext().getResources(), R.drawable.ic_upload_notifi_blue))
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setAutoCancel(true)
                .build();
        manager.notify(Constants.TYPE_NOTIFICATION_NEW_UPLOAD_FILE, notification);
    }
}
