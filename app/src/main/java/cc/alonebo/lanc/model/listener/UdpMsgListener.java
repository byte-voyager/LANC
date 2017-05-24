package cc.alonebo.lanc.model.listener;

import cc.alonebo.lanc.model.bean.UdpTransMsg;

/**
 * Created by alonebo on 17-5-16.
 */

public interface UdpMsgListener  {
    void onNewUdpMsg(UdpTransMsg udpTransMsg);
}
