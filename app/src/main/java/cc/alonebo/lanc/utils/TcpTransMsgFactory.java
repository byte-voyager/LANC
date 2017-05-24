package cc.alonebo.lanc.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import cc.alonebo.lanc.Constants;
import cc.alonebo.lanc.model.bean.TcpTransMsg;


/**
 * Created by alonebo on 17-4-15.
 */

public class TcpTransMsgFactory {
    /**
     * @param receiverIp 接收者的ip
     * @return
     */
    public static TcpTransMsg getRequestDetailMsg(String receiverIp) {

       // return tcpTransMsg;
        return getTcpTransMsg(receiverIp, Constants.TRANS_TYPE_TCP_REQUEST_DETAIL_MSG);
    }

    /**
     * @param reciverIp 接收者的ip
     * @param reciverTcpProt 接收者的端口
     * @return
     */
    public static TcpTransMsg getRespDetailTcpTransMsg(String reciverIp, int reciverTcpProt) {
        return getTcpTransMsg(reciverIp,Constants.TRANS_TYPE_TCP_RESP_DETAIL_MSG);
    }

    /**
     * @param receiverIp 接收者的ip
     * @param transType 传输类型
     * @return
     */
    public static TcpTransMsg getTcpTransMsg(String receiverIp, int transType) {
        TcpTransMsg tcpTransMsg = new TcpTransMsg();
        if (transType==Constants.TRANS_TYPE_TCP_REQUEST_DETAIL_MSG||transType == Constants.TRANS_TYPE_TCP_RESP_DETAIL_MSG) {
            boolean haveCustomAvatar = (boolean) SPUtils.get(Utils.getContext(), Constants.SP_HAVE_CUSTOM_AVATAR, false);
            if (haveCustomAvatar) {
                Bitmap avatar = null;
                FileInputStream fis = null;
                try {
                    fis = Utils.getContext().openFileInput(Constants.NAME_AVATAR);
                    avatar = BitmapFactory.decodeStream(fis);
                } catch (FileNotFoundException e) {
                    avatar = null;
                    e.printStackTrace();
                }finally {
                    try{
                        if (fis!=null) fis.close();
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                tcpTransMsg.setSenderAvatar(Utils.Bitmap2Bytes(avatar));

                if (avatar!=null&& !avatar.isRecycled()) {
                    avatar.recycle();
                }

            }
        }
        tcpTransMsg.setSenderIp(NetUtils.getLocalIpAddress());
        tcpTransMsg.setSenderIdent((String) SPUtils.get(Utils.getContext(),Constants.SP_DEVICE_ID, Build.SERIAL));
        tcpTransMsg.setReceiverIp(receiverIp);

        tcpTransMsg.setTransType(transType);

        tcpTransMsg.setAvatarTime(Utils.getMyAvatarTime());

        return tcpTransMsg;

    }
}
