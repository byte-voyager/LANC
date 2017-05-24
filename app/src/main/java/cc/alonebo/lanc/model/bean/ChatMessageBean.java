package cc.alonebo.lanc.model.bean;

/**
 * Created by alonebo on 17-5-17.
 */

public class ChatMessageBean {
    private String message;
    private long messageTime;
    private int messageType;
    private String ip;

    private String deviceIdent;


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }


    public String getDeviceIdent() {
        return deviceIdent;
    }

    public void setDeviceIdent(String deviceIdent) {
        this.deviceIdent = deviceIdent;
    }


    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
