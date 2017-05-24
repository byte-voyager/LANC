package cc.alonebo.lanc.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.IBinder;
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
import cc.alonebo.lanc.model.listener.ReceiveListener;
import cc.alonebo.lanc.utils.LogUtils;
import cc.alonebo.lanc.utils.Utils;

public class ReceiveFileService extends Service {


    private String TAG = ReceiveFileService.class.getName();

    private ReceiveTask mReceiveTask;
    private boolean mReceiveSuccess = false;
    private boolean mReceiveFailed = false;
    private String fileName = "";
    private int TYPE_START_RECEIVE = 0;
    private int TYPE_SUCCESS_RECEIVE = 1;
    private int TYPE_FAILED_RECEIVE = 2;
    private String filePath = "";

    private  int mProgress = 0;

    private Runnable command = new Runnable() {
        @Override
        public void run() {
            if (mProgress<=0) {
                mReceiveTask.cancelReceive();
            }
        }
    };

    private ReceiveListener listener = new ReceiveListener() {
        @Override
        public void onProgress(int progress) {
            mProgress = progress;
            if (!mReceiveSuccess && !mReceiveFailed){
                LogUtils.e(TAG,"..------->>"+progress);
                getNotificationManager().notify(Constants.TYPE_NOTIFICATION_RECIVE_FILE,getNotification("下载"+fileName+"中...",progress));
            }
        }

        @Override
        public void onSuccess() {

            Constants.IS_TRANSINT_FILE = false;
            mReceiveTask = null;
            stopForeground(true);
            mReceiveSuccess =true;
            Utils.showToast(Utils.getContext(),"下载成功");
            stopService();
            showReceiveNotify(TYPE_SUCCESS_RECEIVE);
            getNotificationManager().cancel(Constants.TYPE_NOTIFICATION_RECIVE_FILE);
        }
        @Override
        public void onFailed() {

            Constants.IS_TRANSINT_FILE = false;
            Utils.showToast(Utils.getContext(),"下载失败");
            mReceiveFailed = true;
            stopForeground(true);
            showReceiveNotify(TYPE_FAILED_RECEIVE);
            getNotificationManager().cancel(Constants.TYPE_NOTIFICATION_RECIVE_FILE);
            stopService();
        }

        @Override
        public void onCanceled() {

            Constants.IS_TRANSINT_FILE = false;
            mReceiveFailed = true;
            Utils.showToast(Utils.getContext(),"下载失败");
            getNotificationManager().cancel(Constants.TYPE_NOTIFICATION_RECIVE_FILE);
            stopForeground(true);
            stopService();
        }
    };



    private void stopService() {
        Intent intent = new Intent(Utils.getContext(),ReceiveFileService.class);
        stopService(intent);
        Log.e(TAG,"ReceiveFileService has exec stop()");
        EventStopTFService event = new EventStopTFService(EventStopTFService.STOP_TYPE_RECEIVE);//TransPresenterImpl
        EventBus.getDefault().post(event);
    }


    public ReceiveFileService() {

    }


    private ReceiveBinder receiveBinder = new ReceiveBinder();
    @Override
    public IBinder onBind(Intent intent) {
        return receiveBinder;
    }

    public class ReceiveBinder extends Binder {
        public void startReceive(UdpTransMsg msg){
            LogUtils.e(TAG,"开始接收文件");
            mReceiveTask = new ReceiveTask(listener);
            if (msg==null) {
                LogUtils.e(TAG,"msg为空");
                return;
            }
            fileName =Utils.getFileName(msg.getFilePath().get(0));
            filePath = msg.getFilePath().get(0);
            mReceiveTask.execute(msg);
            startForeground(Constants.TYPE_NOTIFICATION_RECIVE_FILE,getNotification("准备下载"+fileName+"中...",0));
            showReceiveNotify(TYPE_START_RECEIVE);
            ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
            scheduledExecutorService.schedule(command,6000, TimeUnit.MILLISECONDS);
        }
    }

    public Notification getNotification(String title, int progress) {

//        Intent intent = new Intent(this, CancelTransFile.class);
//        PendingIntent pi = PendingIntent.getActivity(this,0,intent,0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(Utils.getContext());
        builder.setContentTitle(title);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setLargeIcon(BitmapFactory.decodeResource(Utils.getContext().getResources(), R.drawable.ic_download_notif_blue));
//        builder.setContentIntent(pi);
        if (progress>0) {
            builder.setContentText("已下载:"+progress+"%");
            builder.setProgress(100,progress,false);
        }

        return builder.build();
    }


    private NotificationManager getNotificationManager() {
        NotificationManager manager = (NotificationManager) Utils.getContext().getSystemService(NOTIFICATION_SERVICE);
        return manager;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.e(TAG,"ReceiveFileService销毁");
    }

    private void showReceiveNotify(int type) {
        NotificationManager manager = (NotificationManager) Utils.getContext().getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(Utils.getContext());
        Notification notification = null;
        String title = "";
        if (type == TYPE_START_RECEIVE) {
            title = "开始下载文件";
        }
        if (type == TYPE_SUCCESS_RECEIVE) {
            title = "下载文件成功";
        }
        if (type == TYPE_FAILED_RECEIVE) {
            title = "下载文件失败";
        }
        notification = builder
                .setContentTitle(title)
                .setContentText("文件名:"+fileName)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(Utils.getContext().getResources(), R.mipmap.ic_launcher))
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setAutoCancel(true)
                .build();
        manager.notify(Constants.TYPE_NOTIFICATION_NEW_RECIVE_FILE, notification);
    }
}
