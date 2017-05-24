package cc.alonebo.lanc.model.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by alonebo on 17-5-16.
 */

public class UdpTransMsg implements Serializable {

    private static final long serialVersionUID = 2L;

    /**
     * 消息类型
     */
    private int transType;
    /**
     * 发送者别名
     */
    private String senderName;
    /**
     * 发送者ip
     */
    private String senderIP;
    /**
     * 发送者唯一标识
     */
    private String senderIdent;
    /**
     * 发送时间
     */
    private long sendTime;
    /**
     * 接收者IP
     */
    private String receiverIP;
    /**
     * 文件长度
     */
    private ArrayList<Long> fileSize;
    /**
     * 文本信息
     */
    private String message;

    private long avatarTime;



    public int getTransType() {
        return transType;
    }

    public long getAvatarTime() {
        return avatarTime;
    }

    public void setAvatarTime(long avatarTime) {
        this.avatarTime = avatarTime;
    }



    public String getSenderIdent() {
        return senderIdent;
    }

    public void setSenderIdent(String senderIdent) {
        this.senderIdent = senderIdent;
    }

    public ArrayList<String> getFileName() {
        return fileName;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public ArrayList<Long> getFileSize() {
        return fileSize;
    }

    public void setFileSize(ArrayList<Long> fileSize) {
        this.fileSize = fileSize;
    }

    public ArrayList<String> getFilePath() {
        return fileName;
    }

    public void setFileList(ArrayList<String> fileName) {
        this.fileName = fileName;
    }

    private ArrayList<String> fileName;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getSendTime() {
        return sendTime;
    }

    public void setSendTime(long sendTime) {
        this.sendTime = sendTime;
    }

    public String getReceiverIP() {
        return receiverIP;
    }

    public void setReceiverIP(String receiverIP) {
        this.receiverIP = receiverIP;
    }

    public String getSenderIP() {
        return senderIP;
    }

    public void setSenderIP(String senderIP) {
        this.senderIP = senderIP;
    }

    public void setTransType(int transType) {
        this.transType = transType;
    }

}
