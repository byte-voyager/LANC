package cc.alonebo.lanc;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Handler;

import java.util.ArrayList;

/**
 * Created by alonebo on 17-5-16.
 */

public class MyApplication extends Application {
    private static Context context;
    private static Handler handler;
    private static int mainThreadId;
    public static ArrayList<Activity> activityList = new ArrayList<Activity>();
    private NetWorkBroadCast mNetWorkBroadCast;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        handler = new Handler();
        mainThreadId = android.os.Process.myTid();
        regNetWorkBroadCast();
    }

    private void regNetWorkBroadCast() {
        mNetWorkBroadCast = new NetWorkBroadCast();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mNetWorkBroadCast, intentFilter);
    }

    public static Context getContext() {
        return context;
    }

    public static Handler getHandler() {
        return handler;
    }

    public static int getMainThreadId() {
        return mainThreadId;
    }
}
