package cc.alonebo.lanc.model.bean;

/**
 * Created by alonebo on 17-5-20.
 */

public class EventUpdateLastChat {
    public static final int TYPE_UPDATE_LAST_CHAT_MSG_ADDCOUNT= 1;
    public static final int TYPE_UPDATE_LAST_CHAT_MSG_ONLYCLEANCOUNT = 2;
    public static final int TYPE_UPDATE_LAST_CHAT_MSG_ONLYMSG = 3;
    private String msg;
    private long time;
    private int type;

    public String getDeviceIdent() {
        return deviceIdent;
    }

    public EventUpdateLastChat setDeviceIdent(String deviceIdent) {
        this.deviceIdent = deviceIdent;
        return this;
    }

    private String deviceIdent;

    public String getMsg() {
        return msg;
    }

    public EventUpdateLastChat setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public long getTime() {
        return time;
    }

    public EventUpdateLastChat setTime(long time) {
        this.time = time;
        return this;
    }



    public EventUpdateLastChat(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
