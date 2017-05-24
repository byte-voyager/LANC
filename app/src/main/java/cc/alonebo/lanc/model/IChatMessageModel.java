package cc.alonebo.lanc.model;

import java.util.ArrayList;

import cc.alonebo.lanc.model.bean.ChatMessageBean;

/**
 * Created by alonebo on 17-5-17.
 */

public interface IChatMessageModel {
    ArrayList<ChatMessageBean> getChatMessage(String deviceIdent);
    void saveChatMessage(ChatMessageBean chatMessageBean);
    void delContactChat(String deviceIdent);
}
