package cc.alonebo.lanc.presenter.impl;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cc.alonebo.lanc.db.dao.ContactDao;
import cc.alonebo.lanc.model.bean.EventIp;
import cc.alonebo.lanc.model.bean.EventUpdateAvatar;
import cc.alonebo.lanc.model.bean.EventUpdateLastChat;
import cc.alonebo.lanc.model.impl.ChatMessageModelImpl;
import cc.alonebo.lanc.model.impl.ContactModelImpl;
import cc.alonebo.lanc.presenter.IFragContactPresenter;
import cc.alonebo.lanc.utils.NetUtils;
import cc.alonebo.lanc.utils.Utils;
import cc.alonebo.lanc.view.IFragContactView;

/**
 * Created by alonebo on 17-5-17.
 */

public class FragContactPresenterImpl implements IFragContactPresenter {
    private IFragContactView mFragContactView;
    private TransPresenterImpl mTransPresenter;
    private ContactModelImpl mContactModel;
    private ChatMessageModelImpl mChatMessageModel;
    public FragContactPresenterImpl(IFragContactView fragContactView){
        mFragContactView = fragContactView;
        mTransPresenter = TransPresenterImpl.getInstance();
        mContactModel = new ContactModelImpl();
        mChatMessageModel = new ChatMessageModelImpl();
        EventBus.getDefault().register(this);
    }

    @Override
    public void refreshContact() {
        EventBus.getDefault().post(new EventIp().setIp(NetUtils.getLocalIpAddress()));
        mTransPresenter.sendOnLineMsg();

    }

    @Override
    public void hideRefersh() {

        Utils.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                mFragContactView.hideRefresh();
            }
        });
    }

    @Override
    public void delContact(String deviceIdent) {
        mContactModel.delContact(deviceIdent);
        mChatMessageModel.delContactChat(deviceIdent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventUpdateAvatar(EventUpdateAvatar event) {
        mFragContactView.updateContactAvatar(event.getDeviceIdent(),event.getAvatarTime());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventUpdateLastChat(EventUpdateLastChat event) {
       switch (event.getType()) {
           case EventUpdateLastChat.TYPE_UPDATE_LAST_CHAT_MSG_ADDCOUNT:
               mFragContactView.addLastChat(
                       event.getDeviceIdent(),
                       event.getMsg(),
                       ContactDao.MSG_TYPE_LAST_CHAT_MSG_NOT_READ,
                       event.getTime(),
                       true
               );
               break;
           case EventUpdateLastChat.TYPE_UPDATE_LAST_CHAT_MSG_ONLYCLEANCOUNT:
               mFragContactView.updateNotReadCount(event.getDeviceIdent(),0);
               break;
           case EventUpdateLastChat.TYPE_UPDATE_LAST_CHAT_MSG_ONLYMSG:
               mFragContactView.updateLastChatMsg(event.getDeviceIdent(),event.getMsg());
               break;
       }
    }

    public void unRegist(){
        EventBus.getDefault().unregister(this);
    }
}
