package cc.alonebo.lanc.model.convert;

import cc.alonebo.lanc.Constants;
import cc.alonebo.lanc.db.dao.ContactDao;
import cc.alonebo.lanc.model.bean.ChatMessageBean;
import cc.alonebo.lanc.model.bean.ContactBean;
import cc.alonebo.lanc.model.bean.UdpTransMsg;

/**
 * Created by alonebo on 17-5-17.
 */

public class UdpMsgConvTool {
    public static ContactBean getContactBean(UdpTransMsg udpTransMsg) {
        ContactBean contactBean = new ContactBean();
        contactBean.setIp(udpTransMsg.getSenderIP());
        contactBean.setDeviceIdent(udpTransMsg.getSenderIdent());
        contactBean.setName(udpTransMsg.getSenderName());
        contactBean.setOnLineTime(udpTransMsg.getSendTime());

        contactBean.setLastChatMsg(udpTransMsg.getMessage());
        if (true) {
            contactBean.setMsgType(ContactDao.MSG_TYPE_NORMAL);
        }
        contactBean.setLastChatTime(udpTransMsg.getSendTime());
        contactBean.setAvatarTime(udpTransMsg.getAvatarTime());
        contactBean.setIsOnline(ContactDao.IS_ONLINE_TYPE_ONLINE);
        return contactBean;
    }

    public static ChatMessageBean getChatMessageBean(UdpTransMsg udpTransMsg) {
        ChatMessageBean cb = new ChatMessageBean();
        cb.setDeviceIdent(udpTransMsg.getSenderIdent());
        cb.setIp(udpTransMsg.getSenderIP());
        cb.setMessage(udpTransMsg.getMessage());
        cb.setMessageTime(udpTransMsg.getSendTime());
        cb.setMessageType(Constants.TYPE_OTHER);
        return cb;
    }
}
