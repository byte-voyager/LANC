package cc.alonebo.lanc.model;

import java.util.ArrayList;

import cc.alonebo.lanc.model.bean.UdpTransMsg;

/**
 * Created by alonebo on 17-5-16.
 */

public interface IUdpTransMsgModel {
    void sendUdpTransMsg(UdpTransMsg udpTransMsg);
    void sendOnLineMsg();
    void sendRespOnLineMsg(String receiverIP);
    void sendRequestAvatarMsg(String receiverIP);
    void sendChatMessage(UdpTransMsg udpTransMsg);

    void sendChatMessage(String mIp, String message);

    void sendRequestReceiveFile(ArrayList<String> fileName,String receiveIp,ArrayList<Long> fileSize);

    void sendTransIngFileMsg(String receiverIP);
}
