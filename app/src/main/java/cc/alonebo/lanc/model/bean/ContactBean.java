package cc.alonebo.lanc.model.bean;

/**
 * Created by alonebo on 17-5-16.
 */

public class ContactBean {
    private String ip;
    private String name;
    private String deviceIdent;
    private int isOnline;
    private long lastChatTime;
    private String lastChatMsg;
    private int isTransing;
    private int notReadCount;
    private int msgType;
    private long onLineTime;
    private long avatarTime;

    public int getMsgType() {
        return msgType;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getMsg_type() {
        return msgType;
    }

    public void setMsgType(int msg_type) {
        this.msgType = msg_type;
    }

    public int getIsTransing() {
        return isTransing;
    }

    public void setIsTransing(int isTransing) {
        this.isTransing = isTransing;
    }

    public int getNotReadCount() {
        return notReadCount;
    }

    public void setNotReadCount(int notReadCount) {
        this.notReadCount = notReadCount;
    }

    public String getLastChatMsg() {
        return lastChatMsg;
    }

    public void setLastChatMsg(String lastChatMsg) {
        this.lastChatMsg = lastChatMsg;
    }

    public long getLastChatTime() {
        return lastChatTime;
    }

    public void setLastChatTime(long lastChatTime) {
        this.lastChatTime = lastChatTime;
    }

    public int getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(int isOnline) {
        this.isOnline = isOnline;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDeviceIdent() {
        return deviceIdent;
    }

    public void setDeviceIdent(String device_serial) {
        this.deviceIdent = device_serial;
    }

    public long getOnLineTime() {
        return onLineTime;
    }

    public void setOnLineTime(long onLineTime) {
        this.onLineTime = onLineTime;
    }

    public long getAvatarTime() {
        return avatarTime;
    }

    public void setAvatarTime(long avatarTime) {
        this.avatarTime = avatarTime;
    }

}
