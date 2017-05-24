package cc.alonebo.lanc.model.bean;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by alonebo on 17-5-16.
 */

public class TcpTransMsg implements Serializable{

    private static final long serialVersionUID = 1L;

    private byte[] senderAvatar;
    private String senderIdent;
    private String receiverIp;
    private int transType;
    private String senderIp;
    private long avatarTime;

    public long getAvatarTime() {
        return avatarTime;
    }

    public void setAvatarTime(long avatarTime) {
        this.avatarTime = avatarTime;
    }

    public String getSenderIp() {
        return senderIp;
    }

    public void setSenderIp(String senderIp) {
        this.senderIp = senderIp;
    }

    public int getTransType() {
        return transType;
    }

    public void setTransType(int transType) {
        this.transType = transType;
    }

    public String getSenderIdent() {
        return senderIdent;
    }

    public void setSenderIdent(String senderIdent) {
        this.senderIdent = senderIdent;
    }

    public String getReceiverIp() {
        return receiverIp;
    }

    public void setReceiverIp(String receiverIp) {
        this.receiverIp = receiverIp;
    }

    public byte[] getSenderAvatar() {
        return senderAvatar;
    }

    public void setSenderAvatar(byte[] senderAvatar) {
        this.senderAvatar = senderAvatar;
    }



    @Override
    public String toString() {
        return "TcpTransMsg{" +
                "senderAvatar=" + Arrays.toString(senderAvatar) +
                ", senderIdent='" + senderIdent + '\'' +
                ", receiverIp='" + receiverIp + '\'' +
                ", transType=" + transType +
                ", senderIp='" + senderIp + '\'' +
                '}';
    }
}
