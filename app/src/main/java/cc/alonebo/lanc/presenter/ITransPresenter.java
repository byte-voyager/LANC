package cc.alonebo.lanc.presenter;

import android.content.Context;

/**
 * Created by alonebo on 17-5-16.
 */

public interface ITransPresenter {

void stopTransService(Context context);
    void startReceiveTcpMsg();
    void startReceiveUdpMsg();

    void showContactList();

    void sendOnLineMsg();
}
