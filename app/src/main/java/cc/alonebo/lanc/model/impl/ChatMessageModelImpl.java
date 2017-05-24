package cc.alonebo.lanc.model.impl;

import java.util.ArrayList;

import cc.alonebo.lanc.db.dao.ChatMessageDao;
import cc.alonebo.lanc.model.IChatMessageModel;
import cc.alonebo.lanc.model.bean.ChatMessageBean;
import cc.alonebo.lanc.utils.Utils;

/**
 * Created by alonebo on 17-5-17.
 */

public class ChatMessageModelImpl implements IChatMessageModel {


    private ChatMessageDao mChatMessageDao = ChatMessageDao.getInstance(Utils.getContext());

    @Override
    public ArrayList<ChatMessageBean> getChatMessage(String deviceIdent) {
        return mChatMessageDao.queryChatMessage(deviceIdent);
    }

    @Override
    public void saveChatMessage(ChatMessageBean chatMessageBean) {
        mChatMessageDao.insertChatMessage(chatMessageBean.getMessage(),
                chatMessageBean.getMessageTime(),
                chatMessageBean.getIp(),
                chatMessageBean.getDeviceIdent(),
                chatMessageBean.getMessageType());
    }

    @Override
    public void delContactChat(String deviceIdent) {
        mChatMessageDao.deleteChatMessage(deviceIdent);
    }
}
