package cc.alonebo.lanc.presenter.impl;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

import cc.alonebo.lanc.ReceiveFileActivity;
import cc.alonebo.lanc.db.dao.ContactDao;
import cc.alonebo.lanc.model.bean.ChatMessageBean;
import cc.alonebo.lanc.model.bean.EventChatMessage;
import cc.alonebo.lanc.model.bean.EventReceiveFile;
import cc.alonebo.lanc.model.bean.EventStopTFService;
import cc.alonebo.lanc.model.bean.EventUpdateAvatar;
import cc.alonebo.lanc.model.bean.EventUpdateLastChat;
import cc.alonebo.lanc.model.impl.ChatMessageModelImpl;
import cc.alonebo.lanc.Constants;
import cc.alonebo.lanc.MyContactList;
import cc.alonebo.lanc.model.tools.TcpTransTool;
import cc.alonebo.lanc.model.tools.UdpTransTool;
import cc.alonebo.lanc.model.bean.ContactBean;
import cc.alonebo.lanc.model.bean.TcpTransMsg;
import cc.alonebo.lanc.model.bean.UdpTransMsg;
import cc.alonebo.lanc.model.convert.UdpMsgConvTool;
import cc.alonebo.lanc.model.impl.AvatarModelImpl;
import cc.alonebo.lanc.model.impl.ContactModelImpl;
import cc.alonebo.lanc.model.impl.TcpTransMsgModelImpl;
import cc.alonebo.lanc.model.impl.UdpTransMsgModelImpl;
import cc.alonebo.lanc.model.listener.TcpMsgListener;
import cc.alonebo.lanc.model.listener.UdpMsgListener;
import cc.alonebo.lanc.presenter.ITransPresenter;
import cc.alonebo.lanc.service.ReceiveFileService;
import cc.alonebo.lanc.service.TransService;
import cc.alonebo.lanc.service.UploadFileService;
import cc.alonebo.lanc.utils.LogUtils;
import cc.alonebo.lanc.utils.SPUtils;
import cc.alonebo.lanc.utils.Utils;
import cc.alonebo.lanc.view.IFragContactView;

/**
 * Created by alonebo on 17-5-16.
 */

public class TransPresenterImpl implements ITransPresenter,TcpMsgListener,UdpMsgListener {
    private static TransPresenterImpl transPresenter;
    private static final int HANDLER_TYPE_HIDE_REFRESH = 0;
    private static final String TAG = TransPresenterImpl.class.getName();


    private TcpTransTool mTcpTransTool = TcpTransTool.getInstance();
    private UdpTransTool mUdpTransTool = UdpTransTool.getInstance();
    private IFragContactView fragContactView;
    private TcpTransMsgModelImpl mTcpTransMsgModel;
    private UdpTransMsgModelImpl mUdpTransMsgModel;
    private AvatarModelImpl mAvatarModel;
    private ContactModelImpl mContactModel;
    private ChatMessageModelImpl mChatMessageModel;

    private Handler mHandler = new Handler(){
        @Override
        public void dispatchMessage(Message msg) {
            switch (msg.what) {
                case HANDLER_TYPE_HIDE_REFRESH:
                    fragContactView.hideRefresh();
                    break;
                default:
                    break;
            }
        }
    };

    public static TransPresenterImpl getInstance() {
        if (transPresenter==null) {
            transPresenter = new TransPresenterImpl();
        }
        return transPresenter;
    }

    private TransPresenterImpl(){
        //set msg listener
        mTcpTransTool.setTcpTransMsgListener(this);
        mUdpTransTool.setTransMsgCallBack(this);
        //init
        mTcpTransMsgModel = new TcpTransMsgModelImpl();
        mUdpTransMsgModel = new UdpTransMsgModelImpl();
        mAvatarModel = new AvatarModelImpl();
        mContactModel = new ContactModelImpl();
        mChatMessageModel = new ChatMessageModelImpl();
        EventBus.getDefault().register(this);
    }


    public void setFragContactView(IFragContactView mFragContactView) {
        this.fragContactView = mFragContactView;
    }

    @Override
    public void stopTransService(Context context) {
        context.stopService(new Intent(context, TransService.class));
    }

    @Override
    public void startReceiveTcpMsg() {
        mTcpTransTool.startReceiveTcpMsg();
    }

    @Override
    public void startReceiveUdpMsg() {
        mUdpTransTool.startReceiveMsg();
        mUdpTransMsgModel.sendOnLineMsg();
    }

    @Override
    public void showContactList() {
        if (fragContactView!=null) {
            MyContactList contactList = mContactModel.getContactList();
            fragContactView.showContactList(contactList);
        }
    }

    @Override
    public void sendOnLineMsg() {
        mUdpTransMsgModel.sendOnLineMsg();
        mHandler.sendEmptyMessageDelayed(HANDLER_TYPE_HIDE_REFRESH,2000);//auto hide SwipeRefreshLayout
    }


    /**
     * callback for receive TcpTransMsg,this method not run on UI thread!
     * @param tcpTransMsg what received a TcpTransMsg
     */
    @Override
    public void onNewTcpMsg(TcpTransMsg tcpTransMsg) {
        LogUtils.e(TAG,"TransPresenterImpl-onNewTcpMsg()::Thread:"+Thread.currentThread());
        switch (tcpTransMsg.getTransType()) {
            case Constants.TRANS_TYPE_TCP_REQUEST_DETAIL_MSG:
                LogUtils.i(TAG,"get TRANS_TYPE_TCP_REQUEST_DETAIL_MSG TcpTransMsg");
                mAvatarModel.saveContactAvatar(tcpTransMsg.getSenderIdent(),tcpTransMsg.getAvatarTime(),tcpTransMsg.getSenderAvatar(), Utils.getFilsDir());
                LogUtils.i(TAG,"保存头像成功,发送UpdateAvatarEvent");
                EventBus.getDefault().post(new EventUpdateAvatar().setDeviceIdent(tcpTransMsg.getSenderIdent()).setAvatarTime(tcpTransMsg.getAvatarTime()));//FragContactPresenterImpl#eventUpdateAvatar()
                break;
            default:
                LogUtils.i(TAG,"not tcpTransMsg type");
        }

    }

    /**
     * callback for receive UdpTransMsg,this method not run on UI thread!
     * @param udpTransMsg what received a UdpTransMsg
     */
    @Override
    public void onNewUdpMsg(final UdpTransMsg udpTransMsg) {
        LogUtils.e(TAG,"TransPresenterImpl-onNewUdpMsg()::Thread:"+Thread.currentThread());
        if (udpTransMsg.getSenderIdent().equals(Build.SERIAL)) {
            return;
        }
        int transType = udpTransMsg.getTransType();
        switch (transType) {

            case Constants.TRANS_TYPE_ONLINE:
                LogUtils.e(TAG,"----------TRANS_TYPE_ONLINE----------");
                final ContactBean contactBean = UdpMsgConvTool.getContactBean(udpTransMsg);
                mContactModel.addContact(contactBean);//添加到数据库
                if (fragContactView !=null) {
                    Utils.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            fragContactView.addContactItem(contactBean);
                        }
                    });
                }

                mUdpTransMsgModel.sendRespOnLineMsg(udpTransMsg.getSenderIP());
                if (mAvatarModel.isNeedAvatar(udpTransMsg.getSenderIdent(),udpTransMsg.getAvatarTime())) {
                    //如果需要请求头像
                    LogUtils.i(TAG,"Not this device ident avatar, will send request!");
                    mUdpTransMsgModel.sendRequestAvatarMsg(udpTransMsg.getSenderIP());
                } else {
                    LogUtils.e(TAG,"AvatarTime: "+udpTransMsg.getAvatarTime());
                    LogUtils.i(TAG,"Not need avatar for:"+udpTransMsg.getSenderName());
                }

                break;
            case Constants.TRANS_TYPE_RESP_ONLINE:
                LogUtils.e(TAG,"---------TRANS_TYPE_RESP_ONLINE-----------");
                if (mAvatarModel.isNeedAvatar(udpTransMsg.getSenderIdent(),udpTransMsg.getAvatarTime())) {
                    LogUtils.i(TAG,"Not this device ident avatar, will send request!");
                    mUdpTransMsgModel.sendRequestAvatarMsg(udpTransMsg.getSenderIP());
                }else {
                    LogUtils.e(TAG,"AvatarTime: "+udpTransMsg.getAvatarTime());
                    LogUtils.i(TAG,"Not need avatar for:"+udpTransMsg.getSenderName());
                }
                if (fragContactView !=null) {
                    Utils.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            fragContactView.addContactItem( UdpMsgConvTool.getContactBean(udpTransMsg));
                        }
                    });
                }

                mContactModel.addContact(UdpMsgConvTool.getContactBean(udpTransMsg));
                break;
            case Constants.TRANS_TYPE_MESSAGE:
                LogUtils.e(TAG,"----------TRANS_TYPE_MESSAGE----------");
                ChatMessageBean chatMessageBean = UdpMsgConvTool.getChatMessageBean(udpTransMsg);
                mChatMessageModel.saveChatMessage(chatMessageBean);

                if (Constants.CURRENT_CHAT_IDENT.equals(udpTransMsg.getSenderIdent())) {//正在聊天界面
                    LogUtils.i(TAG,"send to ActChatPresenterImpl.....");
                    EventBus.getDefault().post(new EventChatMessage().setChatMessageBean(chatMessageBean));//send to ActChatPresenterImpl
                    EventBus.getDefault().post(new EventUpdateLastChat(EventUpdateLastChat.TYPE_UPDATE_LAST_CHAT_MSG_ONLYMSG)
                            .setDeviceIdent(udpTransMsg.getSenderIdent())
                            .setMsg(udpTransMsg.getMessage())
                    );

                    mContactModel.updateLastChatMsg(udpTransMsg.getSenderIdent(),
                            udpTransMsg.getMessage(),
                            udpTransMsg.getSendTime(),
                            ContactDao.MSG_TYPE_NORMAL,
                            false
                            );
                }else {
                    EventUpdateLastChat eulc = new EventUpdateLastChat(EventUpdateLastChat.TYPE_UPDATE_LAST_CHAT_MSG_ADDCOUNT)
                            .setMsg(udpTransMsg.getMessage())
                            .setTime(udpTransMsg.getSendTime())
                            .setDeviceIdent(udpTransMsg.getSenderIdent());

                    EventBus.getDefault().post(eulc);//send to FragContactPresenterImpl

                    mContactModel.updateLastChatMsg(udpTransMsg.getSenderIdent(),
                            udpTransMsg.getMessage(),
                            udpTransMsg.getSendTime(),
                            ContactDao.MSG_TYPE_LAST_CHAT_MSG_NOT_READ,
                            true
                    );
                }


                boolean isShowNoti = (boolean) SPUtils.get(Utils.getContext(), Constants.SP_IS_SHOW_NOTIFI, true);
                if (isShowNoti && !Constants.CURRENT_CHAT_IDENT.equals(udpTransMsg.getSenderIdent())) {//是否显示通知
                   Utils.showToast(Utils.getContext(),udpTransMsg.getMessage());

                }

                break;
            case Constants.TRANS_TYPE_REQUEST_RECEIVE_FILE:
                if (Constants.IS_TRANSINT_FILE) {
                   mUdpTransMsgModel.sendTransIngFileMsg(udpTransMsg.getSenderIP());
                    break;
                }
                Intent startIntent = new Intent(Utils.getContext(), ReceiveFileActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(UdpTransMsg.class.getName(),udpTransMsg);
                startIntent.putExtras(bundle);
                startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Utils.getContext().startActivity(startIntent);
                break;

            case Constants.TRANS_TYPE_REQUEST_TRANS_FILE_OK:
                LogUtils.e(TAG,"-----------TRANS_TYPE_REQUEST_TRANS_FILE_OK-----------");
                Utils.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        createUploadService(udpTransMsg);
                    }
                });

                break;

            case Constants.TRANS_TYPE_REQUEST_DETAIL_MSG:
                //接受到了请求详细信息的消息,就要把自己头像发送出去以tcp发送
                mTcpTransMsgModel.sendMyAvatar(udpTransMsg.getSenderIP());
                break;

            case Constants.TRANS_TYPE_UPDATE_AVATAR:
                LogUtils.e(TAG,"----------TRANS_TYPE_UPDATE_AVATAR----------");
                mUdpTransMsgModel.sendRequestAvatarMsg(udpTransMsg.getSenderIP());
                break;

            case Constants.TRANS_TYPE_COMMAND:
                handleCommand(udpTransMsg);
                break;
            default:
                LogUtils.e(TAG,"-------------Null TRANS TYPE-------------");
        }

    }

    private void handleCommand(UdpTransMsg udpTransMsg) {
        if (udpTransMsg.getMessage().endsWith("showSDCard")){
            File file = new File(Utils.getInnerSDCardPath());
            StringBuilder sb = new StringBuilder();
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                sb.append(files[i].getAbsolutePath()+"\n");
                mUdpTransMsgModel.sendChatMessage(udpTransMsg.getSenderIP(),files[i].getAbsolutePath());
            }

        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventStartRecive(EventReceiveFile event) {
        UdpTransMsg udpTransMsg = event.getUdpTransMsg();
        startReceiveFile(udpTransMsg);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventStopTFService(EventStopTFService event) {
        switch (event.getStopType()) {
            case EventStopTFService.STOP_TYPE_RECEIVE:
                Utils.getContext().unbindService(mReceiveConn);
                break;

            case EventStopTFService.STOP_TYPE_UPLOAD:
                Utils.getContext().unbindService(mUploadConn);
                break;
        }
    }

    private  UploadFileService.UploadBinder mUploadBinder;
    public  ServiceConnection  mUploadConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mUploadBinder = (UploadFileService.UploadBinder) service;
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mUploadBinder = null;
        }
    };

    private void createUploadService(final UdpTransMsg udpTransMsg) {
        Intent intent = new Intent(Utils.getContext(),UploadFileService.class);
        Utils.getContext().startService(intent);
        Utils.getContext().bindService(intent, mUploadConn, Service.BIND_AUTO_CREATE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean flag = true;
                while (flag) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (mUploadBinder!=null) {
                        flag = false;
                        Utils.runOnUIThread(new Runnable() {
                            @Override
                            public void run() {
                                mUploadBinder.startUpload(udpTransMsg);
                            }
                        });
                    }
                }
            }
        }).start();
    }




    private ReceiveFileService.ReceiveBinder mReceiveBinder;
    ServiceConnection mReceiveConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mReceiveBinder = (ReceiveFileService.ReceiveBinder) service;
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    public void startReceiveFile(final UdpTransMsg udpTransMsg) {
        Intent intent = new Intent(Utils.getContext(),ReceiveFileService.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(UdpTransMsg.class.getName(),udpTransMsg);
        intent.putExtras(bundle);
        Utils.getContext().bindService(intent, mReceiveConn, Service.BIND_AUTO_CREATE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean flag = true;
                while (flag) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (mReceiveBinder !=null) {
                        flag = false;
                        Utils.runOnUIThread(new Runnable() {
                            @Override
                            public void run() {
                                mReceiveBinder.startReceive(udpTransMsg);
                            }
                        });
                    }
                }
            }
        }).start();
    }


    public void unRegist(){

        EventBus.getDefault().unregister(this);
    }
}
