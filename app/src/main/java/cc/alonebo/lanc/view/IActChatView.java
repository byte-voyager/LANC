package cc.alonebo.lanc.view;

import android.app.Activity;
import android.graphics.Bitmap;

import java.util.ArrayList;

import cc.alonebo.lanc.model.bean.ChatMessageBean;

/**
 * Created by alonebo on 17-5-20.
 */

public interface IActChatView {
    void initView();
    void initData();
    void showChatMessage(ArrayList<ChatMessageBean> msgList);
    void addMyChatMsg(String msg);
    void addOtherChatMsg(String msg);
    void updateOtherAvatar(Bitmap bitmap);
    void updateOtherAvatar(long avatarTime);
    void removeOtherMsg(int pos);
    void removeMyMsg(int pos);
    void showNoSuccessSend(boolean isShow,int pos);
    void cleanEditText();
    void setEditTextContent(String content);
    void showConfirmSendFile(ArrayList<String> filePath);
    Activity getChatActivity();

}
