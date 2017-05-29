package cc.alonebo.lanc.model.impl;

import java.util.ArrayList;

import cc.alonebo.lanc.Constants;
import cc.alonebo.lanc.model.IUdpTransMsgModel;
import cc.alonebo.lanc.model.tools.UdpTransTool;
import cc.alonebo.lanc.model.bean.UdpTransMsg;
import cc.alonebo.lanc.utils.UdpTransMsgFactory;

/**
 * Created by alonebo on 17-5-16.
 */

public class UdpTransMsgModelImpl implements IUdpTransMsgModel{
    private UdpTransTool mUdpTransTool;
    private String TAG = UdpTransMsgModelImpl.class.getName();

    private String localIdent;

    public UdpTransMsgModelImpl() {
        mUdpTransTool = UdpTransTool.getInstance();
    }


    @Override
    public void sendUdpTransMsg(UdpTransMsg udpTransMsg) {

    }

    @Override
    public void sendOnLineMsg() {
        mUdpTransTool.sendMsg(UdpTransMsgFactory.getOnLineTransMsg());
    }

    @Override
    public void sendRespOnLineMsg(String who) {
        mUdpTransTool.sendMsg(UdpTransMsgFactory.getRespOnlineTransMsg(who));
    }

    @Override
    public void sendRequestAvatarMsg(String receiverIP) {
        mUdpTransTool.sendMsg(UdpTransMsgFactory.getRequestAvatarTransMsg(receiverIP));
    }


    @Override
    public void sendChatMessage(UdpTransMsg udpTransMsg) {

    }

    @Override
    public void sendChatMessage(String mIp, String message) {
        mUdpTransTool.sendMsg(UdpTransMsgFactory.getMessageTransMsg(mIp,message));
    }

    @Override
    public void sendRequestReceiveFile(ArrayList<String> fileName, String receiveIp,ArrayList<Long> fileSize) {
        mUdpTransTool.sendMsg(UdpTransMsgFactory.getRequestReceiveFileTransMsg(receiveIp,fileName,fileSize));
    }

    @Override
    public void sendTransIngFileMsg(String receiverIP) {
        mUdpTransTool.sendMsg(UdpTransMsgFactory.getTransMsg(receiverIP, Constants.TRANS_TYPE_TRANSING_FILE));
    }

    @Override
    public void sendCommandMsg(String mIp, String message) {
        mUdpTransTool.sendMsg(UdpTransMsgFactory.getCommandTransMsg(mIp,message));
    }
}
