package cc.alonebo.lanc.model.bean;

/**
 * Created by alonebo on 17-5-20.
 */

public class EventChoiceFile {
    public static final int EVENT_TYPES_CHOICE_DOC = 9;
    public static final int EVENT_TYPES_CHOICE_ZIP = 11;
    public static final int EVENT_TYPES_CHOICE_APK = 12;
    public static final int EVENT_TYPES_CHOICE_VIDEO = 13;
    public static final int EVENT_TYPES_CHOICE_CUSTOM = 14;

    private int type;

    public EventChoiceFile(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public EventChoiceFile setType(int type) {
        this.type = type;
        return this;
    }
}
