package cc.alonebo.lanc.presenter.impl;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;

import cc.alonebo.lanc.Constants;
import cc.alonebo.lanc.db.dao.ContactDao;
import cc.alonebo.lanc.model.bean.ChatMessageBean;
import cc.alonebo.lanc.model.bean.EventChatMessage;
import cc.alonebo.lanc.model.bean.EventChoiceFile;
import cc.alonebo.lanc.model.bean.EventUpdateLastChat;
import cc.alonebo.lanc.model.impl.ChatMessageModelImpl;
import cc.alonebo.lanc.model.impl.ContactModelImpl;
import cc.alonebo.lanc.model.impl.UdpTransMsgModelImpl;
import cc.alonebo.lanc.model.listener.ChoiceFileResultListener;
import cc.alonebo.lanc.presenter.IActChatPresenter;
import cc.alonebo.lanc.utils.LogUtils;
import cc.alonebo.lanc.utils.Utils;
import cc.alonebo.lanc.view.IActChatView;
import droidninja.filepicker.FilePickerBuilder;

/**
 * Created by alonebo on 17-5-20.
 */

public class ActChatPresenterImpl implements IActChatPresenter,ChoiceFileResultListener {

    private String TAG = ActChatPresenterImpl.class.getName();

    private String mName;
    private String mIp;
    private String mDeviceIdent;
    private long mAvatarTime;
    private Activity mActivity;
    public static final int REQUEST_FILE_ACTIVITY_CODE = 3;
    private ChatMessageModelImpl mChatMessageModel;
    private UdpTransMsgModelImpl mUdpTransModel;
    private IActChatView mActChatView;
    private ContactModelImpl mContactModel;
    public ActChatPresenterImpl(IActChatView actChatView) {
        this.mChatMessageModel = new ChatMessageModelImpl();
        this.mUdpTransModel = new UdpTransMsgModelImpl();
        this.mActChatView = actChatView;
        this.mContactModel = new ContactModelImpl();
        mActivity = mActChatView.getChatActivity();
        EventBus.getDefault().register(this);
    }

    @Override
    public void getChatMsg() {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventNewChatMessage(EventChatMessage event){
        LogUtils.e(TAG,"eventNewChatMessage executed!");
        mActChatView.addOtherChatMsg(event.getChatMessageBean().getMessage());
    }

    @Override
    public void showChatMessage() {
        ArrayList<ChatMessageBean> chatMessageList = mChatMessageModel.getChatMessage(mDeviceIdent);
        LogUtils.e(TAG,"chatMessageList.size():: "+chatMessageList.size());
        mActChatView.showChatMessage(chatMessageList);
    }

    @Override
    public void sendMessage(String message) {
        mActChatView.addMyChatMsg(message);
        ChatMessageBean chatBean = new ChatMessageBean();
        chatBean.setMessageType(Constants.TYPE_ONESELF);
        chatBean.setMessage(message);
        chatBean.setDeviceIdent(mDeviceIdent);
        chatBean.setMessageTime(Utils.getCurrentTime());
        mChatMessageModel.saveChatMessage(chatBean);

        mUdpTransModel.sendChatMessage(mIp,message);

        EventBus.getDefault().post(new EventUpdateLastChat(EventUpdateLastChat.TYPE_UPDATE_LAST_CHAT_MSG_ONLYMSG)
                .setDeviceIdent(mDeviceIdent)
                .setMsg(message));//ContactPresenterImpl#

        mContactModel.updateLastChatMsg(mDeviceIdent,message,Utils.getCurrentTime(), ContactDao.MSG_TYPE_NORMAL,
            false
        );
    }

    @Override
    public void init() {
        mActChatView.initData();
        mActChatView.initView();
        EventUpdateLastChat event = new EventUpdateLastChat(EventUpdateLastChat.TYPE_UPDATE_LAST_CHAT_MSG_ONLYCLEANCOUNT)
                .setDeviceIdent(mDeviceIdent);
        EventBus.getDefault().post(event);

        mContactModel.updateLastChatMsg(mDeviceIdent,true);
    }

    @Override
    public void sendFile(ArrayList<String> filePath) {
        ArrayList<Long> fileSize = new ArrayList<>();
        File file  = null;
        for (int i = 0; i < filePath.size(); i++) {
            file = new File(filePath.get(i));
            fileSize.add(i,  file.length());
        }
        mUdpTransModel.sendRequestReceiveFile(filePath,mIp,fileSize);
    }

    @Override
    public void copyText(String content) {
        ClipboardManager cmb = (ClipboardManager) Utils.getContext() .getSystemService(Context.CLIPBOARD_SERVICE);

        cmb.setText(content);
    }

    public void setData(String name, String ip, String deviceIdent, long avatarTime){
        this.mName = name;
        this.mIp = ip;
        this.mDeviceIdent = deviceIdent;
        this.mAvatarTime = avatarTime;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventChoiceFile(EventChoiceFile event) {
        switch (event.getType()) {
            case EventChoiceFile.EVENT_TYPES_CHOICE_DOC:
                choiceDocFile();
                break;
            case EventChoiceFile.EVENT_TYPES_CHOICE_ZIP:
                choiceZipFile();
                break;
            case EventChoiceFile.EVENT_TYPES_CHOICE_APK:
                choiceAPKFile();
                break;
            case EventChoiceFile.EVENT_TYPES_CHOICE_VIDEO:
                choiceVideoFile();
                break;
            case EventChoiceFile.EVENT_TYPES_CHOICE_CUSTOM:
                choiceCustomFile();
                break;
        }
    }

    private ArrayList<String> filepathList = new ArrayList<>();
    private void choiceCustomFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");//设置类型
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            mActivity.startActivityForResult(Intent.createChooser(intent, "选择文件"), REQUEST_FILE_ACTIVITY_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(mActivity, "亲，木有文件管理器", Toast.LENGTH_SHORT).show();
        }
    }

    private void choiceVideoFile() {
        filepathList.clear();
        FilePickerBuilder.getInstance().setMaxCount(1)
                .setSelectedFiles(filepathList)
                .enableDocSupport(false)
                .addFileSupport("AVI",new String[]{"avi","AVI"})
                .addFileSupport("MP4",new String[]{"mp4","MP4"})
                .pickFile(mActivity);
    }

    private void choiceAPKFile() {
        filepathList.clear();
        FilePickerBuilder.getInstance().setMaxCount(1)
                .setSelectedFiles(filepathList)
                .enableDocSupport(false)
                .addFileSupport("APK",new String[]{"apk","APK"})
                .pickFile(mActivity);
    }

    private void choiceZipFile() {
        filepathList.clear();
        FilePickerBuilder.getInstance().setMaxCount(1)
                .setSelectedFiles(filepathList)
                .enableDocSupport(false)
                .addFileSupport("ZIP",new String[]{"zip","ZIP"})
                .addFileSupport("RAR",new String[]{"rar","RAR"})
                .pickFile(mActivity);
    }

    private void choiceDocFile() {
        filepathList.clear();
        FilePickerBuilder.getInstance().setMaxCount(1)
                .setSelectedFiles(filepathList)
                .enableDocSupport(true)
                .pickFile(mActivity);
    }


    @Override
    public void onPathResult(ArrayList<String> filePath) {
        if (filePath==null || filePath.size()==0) {
            LogUtils.e(TAG,"filepath is null!");
            return;
        }
        LogUtils.e(TAG,filePath.get(0));
        mActChatView.showConfirmSendFile(filePath);
    }

    public void unRegist(){
        EventBus.getDefault().unregister(this);
    }
}
