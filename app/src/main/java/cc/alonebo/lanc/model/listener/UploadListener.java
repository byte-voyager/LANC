package cc.alonebo.lanc.model.listener;

/**
 * Created by alonebo on 17-3-30.
 */

public interface UploadListener {
    void onProgress(int progress);
    void onSuccess();
    void onFailed();
    void onCanceled();
}
