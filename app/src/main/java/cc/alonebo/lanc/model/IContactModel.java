package cc.alonebo.lanc.model;

import cc.alonebo.lanc.MyContactList;
import cc.alonebo.lanc.model.bean.ContactBean;

/**
 * Created by alonebo on 17-5-16.
 */

public interface IContactModel {
    MyContactList queryContact();

    void addContact(ContactBean contactBean);

    MyContactList getContactList();

    void updateLastChatMsg(String deviceIdent,String lastChatMsg, long lastChatTime,int msgType,boolean isAddNotReadCount);

    void updateLastChatMsg(String deviceIdent, boolean isCleanCount);
    void delContact(String deviceIdent);
}
