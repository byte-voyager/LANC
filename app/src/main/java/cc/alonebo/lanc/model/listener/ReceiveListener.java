package cc.alonebo.lanc.model.listener;

/**
 * Created by alonebo on 17-4-11.
 */

public interface ReceiveListener {
    void onProgress(int progress);
    void onSuccess();
    void onFailed();
    void onCanceled();
}
