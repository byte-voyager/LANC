package cc.alonebo.lanc.model.bean;

/**
 * Created by alonebo on 17-5-20.
 */

public class EventReceiveFile {
    private UdpTransMsg udpTransMsg;

    public UdpTransMsg getUdpTransMsg() {
        return udpTransMsg;
    }

    public EventReceiveFile setUdpTransMsg(UdpTransMsg udpTransMsg) {
        this.udpTransMsg = udpTransMsg;
        return this;
    }
}
