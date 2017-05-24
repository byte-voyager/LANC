package cc.alonebo.lanc.utils;

import android.os.Build;

import java.util.ArrayList;

import cc.alonebo.lanc.Constants;
import cc.alonebo.lanc.model.bean.UdpTransMsg;


/**
 * Created by alonebo on 17-4-15.
 */

public class UdpTransMsgFactory {
    /**
     * @return 生产一个上线消息
     */
    public  static UdpTransMsg getOnLineTransMsg() {
        UdpTransMsg transMsg = getTransMsg(NetUtils.getBroadCastIP(), Constants.TRANS_TYPE_ONLINE);
        transMsg.setAvatarTime(Utils.getMyAvatarTime());
        return transMsg;
    }

    public  static UdpTransMsg getRespOnlineTransMsg(String receiverIp) {
        UdpTransMsg transMsg = getTransMsg(receiverIp, Constants.TRANS_TYPE_RESP_ONLINE);
        transMsg.setAvatarTime(Utils.getMyAvatarTime());
        return transMsg;
    }

    public static  UdpTransMsg getTransMsg(String receiverIp, int transType) {
        UdpTransMsg transMsg = new UdpTransMsg();

        transMsg.setSendTime(Utils.getCurrentTime());//发送时间

        transMsg.setSenderIdent((String) SPUtils.get(Utils.getContext(),Constants.SP_DEVICE_ID, Build.SERIAL));//发送者ident
        transMsg.setSenderName((String) SPUtils.get(Utils.getContext(),Constants.SP_DEVICE_NAME, Build.MODEL));//发送者名字
        transMsg.setSenderIP(NetUtils.getLocalIpAddress());//发送者ip

        transMsg.setReceiverIP(receiverIp);//接收者ip

        transMsg.setTransType(transType);//传输类型

        return transMsg;
    }

    public static  UdpTransMsg getMessageTransMsg(String receiverIp, String message) {
        UdpTransMsg transMsg = getTransMsg(receiverIp, Constants.TRANS_TYPE_MESSAGE);
        transMsg.setMessage(message);
        return transMsg;
    }


    public static  UdpTransMsg getRequestReceiveFileTransMsg(String receiverIp, ArrayList<String> fileList, ArrayList<Long> fileSize) {
        UdpTransMsg transMsg = getTransMsg(receiverIp, Constants.TRANS_TYPE_REQUEST_RECEIVE_FILE);
        transMsg.setFileSize(fileSize);
        transMsg.setFileList(fileList);

        return transMsg;
    }


    public static  UdpTransMsg getReadyReciveFileTransMsg(String receiverIp, ArrayList<String> fileName) {
        UdpTransMsg transMsg = getTransMsg(receiverIp, Constants.TRANS_TYPE_REQUEST_TRANS_FILE_OK);
        transMsg.setFileList(fileName);
        return transMsg;
    }

    public static  UdpTransMsg getRequestAvatarTransMsg(String receiverIp) {
        UdpTransMsg transMsg = getTransMsg(receiverIp, Constants.TRANS_TYPE_REQUEST_DETAIL_MSG);

        return transMsg;
    }

    public static UdpTransMsg getUpdateAvatarMsg() {
        UdpTransMsg transMsg = getTransMsg(NetUtils.getBroadCastIP(), Constants.TRANS_TYPE_UPDATE_AVATAR);
        transMsg.setAvatarTime(Utils.getMyAvatarTime());
        return transMsg;
    }
}
