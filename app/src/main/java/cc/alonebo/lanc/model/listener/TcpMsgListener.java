package cc.alonebo.lanc.model.listener;

import cc.alonebo.lanc.model.bean.TcpTransMsg;

/**
 * Created by alonebo on 17-5-16.
 */

public interface TcpMsgListener {
    void onNewTcpMsg(TcpTransMsg tcpTransMsg);
}
