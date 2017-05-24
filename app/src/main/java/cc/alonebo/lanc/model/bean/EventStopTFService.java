package cc.alonebo.lanc.model.bean;

/**
 * Created by alonebo on 17-5-21.
 */

public class EventStopTFService {
    public static final int STOP_TYPE_UPLOAD = 1;
    public static final int STOP_TYPE_RECEIVE= 2;

    public int getStopType() {
        return stopType;
    }

    private int stopType;

    public EventStopTFService(int stopType) {
        this.stopType = stopType;
    }
}
