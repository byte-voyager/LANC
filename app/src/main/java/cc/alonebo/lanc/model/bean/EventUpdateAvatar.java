package cc.alonebo.lanc.model.bean;

/**
 * Created by alonebo on 17-5-20.
 */

public class EventUpdateAvatar {

    private String deviceIdent;

    public long getAvatarTime() {
        return avatarTime;
    }

    public EventUpdateAvatar setAvatarTime(long avatarTime) {
        this.avatarTime = avatarTime;
        return this;
    }

    public long avatarTime;

    public String getDeviceIdent() {
        return deviceIdent;
    }

    public EventUpdateAvatar setDeviceIdent(String deviceIdent) {
        this.deviceIdent = deviceIdent;
        return this;
    }

    public EventUpdateAvatar() {

    }
}
