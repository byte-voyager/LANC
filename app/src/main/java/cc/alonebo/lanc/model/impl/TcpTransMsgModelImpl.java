package cc.alonebo.lanc.model.impl;

import cc.alonebo.lanc.model.ITcpTcptransMsgModel;
import cc.alonebo.lanc.model.tools.TcpTransTool;
import cc.alonebo.lanc.utils.TcpTransMsgFactory;

/**
 * Created by alonebo on 17-5-16.
 */

public class TcpTransMsgModelImpl implements ITcpTcptransMsgModel {


    private String TAG = TcpTransMsgModelImpl.class.getName();
    private TcpTransTool mTcpTransTool = TcpTransTool.getInstance();

    @Override
    public void sendMyAvatar(String receiverIp) {
        mTcpTransTool.sendTcpMsg(TcpTransMsgFactory.getRequestDetailMsg(receiverIp));
    }


}
