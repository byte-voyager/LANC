package cc.alonebo.lanc.presenter;

import java.util.ArrayList;

/**
 * Created by alonebo on 17-5-20.
 */

public interface IActChatPresenter {
    void getChatMsg();
    void showChatMessage();
    void sendMessage(String message);
    void init();
    void sendFile(ArrayList<String> filePath);
    void copyText(String content);
}
