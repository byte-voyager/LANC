package cc.alonebo.lanc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.greenrobot.eventbus.EventBus;

import cc.alonebo.lanc.model.bean.EventIp;
import cc.alonebo.lanc.utils.LogUtils;
import cc.alonebo.lanc.utils.NetUtils;
import cc.alonebo.lanc.utils.SPUtils;
/**
 * Created by alonebo on 17-5-17.
 */

public class NetWorkBroadCast extends BroadcastReceiver{
    private String TAG = NetWorkBroadCast.class.getName();
    @Override
    public void onReceive(Context context, Intent intent) {
        //重新设置IP
        String newIp = NetUtils.getLocalIpAddress();
        SPUtils.put(context, Constants.SP_LOCAL_IP,newIp);
        LogUtils.e(TAG,"重新修改IP成功!");
        EventBus.getDefault().post(new EventIp().setIp(NetUtils.getLocalIpAddress()));
    }
}
