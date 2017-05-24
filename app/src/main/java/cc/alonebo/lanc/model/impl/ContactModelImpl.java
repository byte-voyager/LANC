package cc.alonebo.lanc.model.impl;

import cc.alonebo.lanc.Constants;
import cc.alonebo.lanc.db.dao.ContactDao;
import cc.alonebo.lanc.model.IContactModel;
import cc.alonebo.lanc.MyContactList;
import cc.alonebo.lanc.model.bean.ContactBean;
import cc.alonebo.lanc.utils.Utils;

/**
 * Created by alonebo on 17-5-16.
 */

public class ContactModelImpl implements IContactModel {
    private ContactDao mContactDao;

    public ContactModelImpl() {
        mContactDao = ContactDao.getInstance(Utils.getContext());
    }

    @Override
    public MyContactList queryContact() {

        return null;
    }

    @Override
    public void addContact(ContactBean contactBean) {
        // 插入到数据库里面,用于下次读取
        mContactDao.insertOnlineContact(contactBean.getIp(),contactBean.getName(),contactBean.getDeviceIdent(),contactBean.getOnLineTime());
    }

    @Override
    public MyContactList getContactList() {
        MyContactList myContactList = mContactDao.queryAll();
        return myContactList;
    }

    @Override
    public void updateLastChatMsg(String deviceIdent,String lastChatMsg, long lastChatTime,int msgType,boolean isAddNotReadCount) {
        mContactDao.updateLastChatMsg(deviceIdent,lastChatMsg,lastChatTime,msgType,isAddNotReadCount);
    }

    @Override
    public void updateLastChatMsg(String deviceIdent, boolean isCleanCount) {
        mContactDao.updateLastChatMsg(deviceIdent, ContactDao.MSG_TYPE_NORMAL,0);
    }

    @Override
    public void delContact(String deviceIdent) {
        mContactDao.deleteContact(deviceIdent);
    }


}
