package cc.alonebo.lanc.service;



import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import cc.alonebo.lanc.Constants;
import cc.alonebo.lanc.MainActivity;
import cc.alonebo.lanc.R;
import cc.alonebo.lanc.model.tools.FTPTransTool;
import cc.alonebo.lanc.utils.NetUtils;
import cc.alonebo.lanc.utils.Utils;

public class FtpService extends Service {
    private FTPTransTool mFtpTransTool = FTPTransTool.getInstance();
    public FtpService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mFtpTransTool.startFTPServer();


        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("FTP Service Running!")
                .setContentText("ftp://"+ NetUtils.getLocalIpAddress()+":"+ Constants.PORT_FTP)
                .setWhen(Utils.getCurrentTime())
                .setLargeIcon(BitmapFactory.decodeResource(Utils.getContext().getResources(),R.drawable.svg_ftp_black))
             //   .setContentIntent(pi)
                .build();
        startForeground(Constants.TYPE_NOTIFICATION_FTP_RUNNING,getNotification("FTP Service Running!","ftp://"+ NetUtils.getLocalIpAddress()+":"+ Constants.PORT_FTP));


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mFtpTransTool.stopServer();
        stopForeground(true);
    }


    public Notification getNotification(String title, String des) {
        Intent i = new Intent(this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pi = PendingIntent.getActivity(this,0,i,0);
        android.support.v7.app.NotificationCompat.Builder builder = new android.support.v7.app.NotificationCompat.Builder(Utils.getContext());
        builder.setContentTitle(title);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setWhen(Utils.getCurrentTime());
        builder.setLargeIcon(BitmapFactory.decodeResource(Utils.getContext().getResources(), R.drawable.svg_ftp_black));
        builder.setContentText(des);
        //builder.setContentIntent(pi);
        return builder.build();
    }
}
