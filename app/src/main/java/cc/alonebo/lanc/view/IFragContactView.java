package cc.alonebo.lanc.view;

import cc.alonebo.lanc.MyContactList;
import cc.alonebo.lanc.model.bean.ContactBean;

/**
 * Created by alonebo on 17-5-16.
 */

public interface IFragContactView {
    void showEmptyContactView(boolean isShow);
    void showContactList(MyContactList myContactList);
    void updateContactList(MyContactList myContactList);
    void addContactItem(ContactBean contactBean);
    void removeContact(ContactBean contactBean);
    void updateContact(ContactBean contactBean);
    void hideRefresh();
    void updateContactAvatar(String deviceIdent,long avatarTime);
    void updateContactName(String deviceIdent,String name);
    void addLastChat(String deviceIdent,String message,int type,long chatTime,boolean isAddNotReadMsgCount);
    void updateNotReadCount(String deviceIdent, int count);
    void updateLastChat(String deviceIdent, String message, int type, long chatTime, int count);

    void updateLastChatMsg(String deviceIdent, String msg);
    void updateContactNameIPAvatar(String deviceIdent, String name, String ip,long newTime);
}
