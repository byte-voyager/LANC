package cc.alonebo.lanc.utils;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import cc.alonebo.lanc.Constants;


/**
 * Created by alonebo on 17-3-18.
 */

public class NetUtils {

    /**
     * @return 返回本地局域网IP
     */
    public static String getLocalSPIp() {
        return (String) SPUtils.get(Utils.getContext(), Constants.SP_LOCAL_IP,"0.0.0.0");
    }
    /**
     * @return 返回广播IP
     */
    public static String getBroadCastIP() {
        String ip = getLocalIpAddress().substring(0,
                getLocalIpAddress().lastIndexOf(".") + 1)
                + "255";
        return ip;
    }


    /*
     * 得到本机IP地址
     * */
    public static String getLocalIpAddress(){
        if (isWifiApOpen(Utils.getContext())) {
            return "192.168.43.1";
        }
        Enumeration<NetworkInterface> en = null;
        try {
            en = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        while(en.hasMoreElements()){
            NetworkInterface nif = en.nextElement();
            Enumeration<InetAddress> enumIpAddr = nif.getInetAddresses();
            while(enumIpAddr.hasMoreElements()){
                InetAddress mInetAddress = enumIpAddr.nextElement();
                //if(!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                if(!mInetAddress.isLoopbackAddress() && mInetAddress instanceof Inet4Address){
                    return mInetAddress.getHostAddress().toString();
                }
            }
        }

        return "0.0.0.0";
    }


    public static boolean isWifiApOpen(Context context) {
        try {
            WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            //通过放射获取 getWifiApState()方法
            Method method = manager.getClass().getDeclaredMethod("getWifiApState");
            //调用getWifiApState() ，获取返回值
            int state = 0;
            try {
                state = (int) method.invoke(manager);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            //通过放射获取 WIFI_AP的开启状态属性
            Field field = manager.getClass().getDeclaredField("WIFI_AP_STATE_ENABLED");
            //获取属性值
            int value = (int) field.get(manager);
            //判断是否开启
            if (state == value) {
                return true;
            } else {
                return false;
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return false;
    }

    //判断WiFi是否打开
    public static boolean isWiFiOpen(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }


    //判断网络连接是否可用
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
        } else {
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
            if (networkInfo != null&&networkInfo.length>0) {
                for (int i = 0; i < networkInfo.length; i++) {
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
