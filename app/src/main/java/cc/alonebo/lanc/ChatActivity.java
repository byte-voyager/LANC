package cc.alonebo.lanc;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ActionProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cc.alonebo.lanc.model.bean.ChatMessageBean;
import cc.alonebo.lanc.model.bean.EventChoiceFile;
import cc.alonebo.lanc.model.listener.ChoiceFileResultListener;
import cc.alonebo.lanc.model.listener.KeyBoardListener;
import cc.alonebo.lanc.presenter.impl.ActChatPresenterImpl;
import cc.alonebo.lanc.utils.LogUtils;
import cc.alonebo.lanc.utils.SPUtils;
import cc.alonebo.lanc.utils.Utils;
import cc.alonebo.lanc.view.IActChatView;
import cc.alonebo.lanc.widget.MyEditText;
import cc.alonebo.lanc.widget.MyLinearLayout;
import de.hdodenhof.circleimageview.CircleImageView;
import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;

/**
 * Created by alonebo on 17-5-20.
 */

public class ChatActivity extends AppCompatActivity implements IActChatView, KeyBoardListener {

    private String TAG = ChatActivity.class.getName();

    public static final String EXTRAS_NAME_IP = "ip";
    public static final String EXTRAS_NAME_IDENT = "ident";
    public static final String EXTRAS_NAME_NAME = "name";
    public static final String EXTRAS_NAME_AVATAR_TIME = "avatarTime";


    @BindView(R.id.rv_chat) RecyclerView rv_chat;
    @BindView(R.id.tv_chat_name) TextView tv_chat_name;
    @BindView(R.id.ed_input_msg)
    MyEditText ed_input_msg;
    @BindView(R.id.myll_send_frame)
    MyLinearLayout myll_send_frame;

    private ActChatPresenterImpl mActChatPresenter;
    private String mName;
    private String mIp;
    private String mDeviceIdent;
    private long mAvatarTime;
    private RecyclerView.Adapter mAdapter;

    public void setFileResultListener(ChoiceFileResultListener fileResultistener) {
        this.mFileResultListener = fileResultistener;
    }

    private ChoiceFileResultListener mFileResultListener;

    private ArrayList<ChatMessageBean> mMessageList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        mActChatPresenter = new ActChatPresenterImpl(this);

        Intent intent = getIntent();
        mName = intent.getStringExtra(EXTRAS_NAME_NAME);
        mIp = intent.getStringExtra(EXTRAS_NAME_IP);
        mDeviceIdent = intent.getStringExtra(EXTRAS_NAME_IDENT);
        mAvatarTime = intent.getLongExtra(EXTRAS_NAME_AVATAR_TIME,0l);
        mActChatPresenter.setData(mName,mIp,mDeviceIdent,mAvatarTime);
        mActChatPresenter.showChatMessage();
        mActChatPresenter.init();
        setFileResultListener(mActChatPresenter);
    }

    @Override
    public void initView() {
        //load message s
        tv_chat_name.setText(mName);
        Toolbar toolbar_chat_activity = (Toolbar) findViewById(R.id.toolbar_chat_activity);
        setSupportActionBar(toolbar_chat_activity);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        myll_send_frame.setListener(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        Constants.CURRENT_CHAT_IDENT = mDeviceIdent;
    }

    @Override
    protected void onStop() {
        super.onStop();
        Constants.CURRENT_CHAT_IDENT = "";
    }

    @Override
    public void initData() {

    }

    @Override
    public void showChatMessage(ArrayList<ChatMessageBean> msgList) {
        mMessageList = msgList;
        LinearLayoutManager lm = new LinearLayoutManager(Utils.getContext());
        mAdapter = new ChatAdapter(mMessageList,mDeviceIdent,mAvatarTime,this);
        rv_chat.setLayoutManager(lm);
        rv_chat.setAdapter(mAdapter);
        rv_chat.scrollToPosition(mMessageList.size()-1);
    }

    @Override
    public void addMyChatMsg(String msg) {
        ChatMessageBean chatBean = new ChatMessageBean();
        chatBean.setMessageType(Constants.TYPE_ONESELF);
        chatBean.setMessage(msg);
        chatBean.setDeviceIdent(mDeviceIdent);
        chatBean.setMessageTime(Utils.getCurrentTime());
        mMessageList.add(chatBean);
        mAdapter.notifyItemInserted(mMessageList.size());

        rv_chat.scrollToPosition(mMessageList.size()-1);
    }

    @Override
    public void addOtherChatMsg(String msg) {
        ChatMessageBean chatBean = new ChatMessageBean();
        chatBean.setMessageType(Constants.TYPE_OTHER);
        chatBean.setMessage(msg);
        chatBean.setDeviceIdent(mDeviceIdent);
        chatBean.setMessageTime(Utils.getCurrentTime());
        mMessageList.add(chatBean);
        mAdapter.notifyItemInserted(mMessageList.size());

        rv_chat.scrollToPosition(mMessageList.size()-1);
    }

    @Override
    public void updateOtherAvatar(Bitmap bitmap) {

    }

    @Override
    public void updateOtherAvatar(long avatarTime) {

    }

    @Override
    public void removeOtherMsg(int pos) {

    }

    @Override
    public void removeMyMsg(int pos) {

    }

    @Override
    public void showNoSuccessSend(boolean isShow, int pos) {

    }

    @Override
    public void cleanEditText() {
        ed_input_msg.setText("");
    }

    @Override
    public void setEditTextContent(String content) {
        ed_input_msg.setText(content);
    }

    @Override
    public void showConfirmSendFile(final ArrayList<String> filePath) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("确认发送?")
                .setMessage("发送文件为:"+filePath.get(0))
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        mActChatPresenter.sendFile(filePath);
                    }
                }).show();
    }

    @Override
    public Activity getChatActivity() {
        return this;
    }

    @OnClick(R.id.bt_send_msg)
    public void clickSendMsg() {
        String message = ed_input_msg.getText().toString();
        if(message.trim().equals("")) {
            Toast.makeText(this,"消息为空",Toast.LENGTH_SHORT).show();
            return;
        }

        mActChatPresenter.sendMessage(message);
        ed_input_msg.setText("");

    }


    public static void actionStart(Context context, String name, String ip, String deviceIdent, long avatarTime) {
        Intent chatIntent = new Intent(context,ChatActivity.class);
        chatIntent.putExtra(EXTRAS_NAME_NAME,name);
        chatIntent.putExtra(EXTRAS_NAME_IP,ip);
        chatIntent.putExtra(EXTRAS_NAME_IDENT,deviceIdent);
        chatIntent.putExtra(EXTRAS_NAME_AVATAR_TIME,avatarTime);
        chatIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(chatIntent);
    }

    @Override
    public void onKeyBoardShow() {
        if (mMessageList!=null) {
            rv_chat.scrollToPosition(mMessageList.size()-1);
        }

    }


    public enum CHAT_ITEM_TYPE {
        CHAT_ITEM_TYPE_ME,
        CHAT_ITEM_TYPE_OTHER
    }

    public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        public Bitmap bmOther;
        public Bitmap bmMe;
        String deviceIdent;
        long avatarTime = 0;
        Activity activity;


        public String getIdent() {
            return deviceIdent;
        }


        private List<ChatMessageBean> msgList;
        public ChatAdapter(List<ChatMessageBean> msgList, String deviceIdent, long avatarTime, Activity activity) {
            this.msgList = msgList;
            this.deviceIdent = deviceIdent;
            this.avatarTime = avatarTime;
            this.activity = activity;
            initBitmap();
        }

        private void initBitmap() {
            boolean hasCustomAvatar = (boolean) SPUtils.get(Utils.getContext(),Constants.SP_HAVE_CUSTOM_AVATAR,false);
            int reqWidth = Utils.dip2px(Utils.getContext(),38);
            int reqHeight = Utils.dip2px(Utils.getContext(),38);
            String picPathMe = Utils.getMyAvatarAbsPath();
            String picPathOther = Utils.getAvatorPath(deviceIdent,avatarTime);
            LogUtils.e(TAG,"picPathMe:"+picPathMe);
            LogUtils.e(TAG,"picPathOther:"+picPathOther);
            if (hasCustomAvatar) {
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(Utils.getMyAvatarAbsPath());
                    bmMe = Utils.decodeSampledBitmapFromFile(picPathMe,reqWidth,reqHeight);
                } catch (FileNotFoundException e) {
                    LogUtils.e(TAG,"没有找到自己头像,将设置默认头像");
                } finally {
                    if (fis!=null) try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            FileInputStream fis = null;
            bmOther = BitmapFactory.decodeFile(picPathOther);
            try {
                fis = Utils.getContext().openFileInput(picPathOther);
                // bmOther =  Utils.decodeSampledBitmapFromFile(picPathOther,reqWidth,reqHeight);

                File file = new File(picPathOther);
                bmOther = BitmapFactory.decodeFile(picPathOther);
                LogUtils.e(TAG,"picPathOther:"+picPathOther);
                if (bmOther==null) {
                    LogUtils.e(TAG,"bmOtherNull");
                }else {
                    LogUtils.e(TAG,"bmOther不空");
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                LogUtils.e(TAG,"没有找到联系人图标,将设置默认头像");
            } finally {
                if (fis!=null) try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            if (viewType==CHAT_ITEM_TYPE.CHAT_ITEM_TYPE_ME.ordinal()){
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_me,parent,false);
                return  new ChatMeViewHolder(view);
            }

            if (viewType==CHAT_ITEM_TYPE.CHAT_ITEM_TYPE_OTHER.ordinal()) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_other,null,false);
                return new ChatOtherViewHolder(view);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
            ChatMessageBean message = msgList.get(position);

            if (holder instanceof ChatMeViewHolder) {
                ((ChatMeViewHolder)holder).tv_chat_msg_me.setText(message.getMessage());
//            Glide.with(activity).load(myAvatarFile).placeholder(R.drawable.svg_wo128).into(((ChatMeViewHolder) holder).iv_contact_me);
                if (bmMe!=null) {
                    ((ChatMeViewHolder) holder).iv_contact_me.setImageBitmap(bmMe);
                }else {
                    ((ChatMeViewHolder) holder).iv_contact_me.setImageResource(R.drawable.ic_avatar_default);
                }
                ((ChatMeViewHolder) holder).ll_message_me.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {

                        return false;
                    }
                });
                ((ChatMeViewHolder) holder).ll_message_me.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        String text = ((ChatMeViewHolder) holder).tv_chat_msg_me.getText().toString();
                        mActChatPresenter.copyText(text);
                        Utils.showToast(Utils.getContext(),"Copy Success!");
                        return false;
                    }
                });

            }
            if (holder instanceof ChatOtherViewHolder) {
                ((ChatOtherViewHolder)holder).tv_chat_msg_other.setText(message.getMessage());
//            if (otherAvatarFile==null) {
//                otherAvatarFile = new File(Utils.getAvatorPath(ident,avaratTime));
//            }
//            Glide.with(Utils.getContext()).load(otherAvatarFile).placeholder(R.drawable.svg_ta128).into(((ChatOtherViewHolder) holder).iv_contact_other);
                if (bmOther!=null) {
                    ((ChatOtherViewHolder) holder).iv_contact_other.setImageBitmap(bmOther);

                }else {
                    ((ChatOtherViewHolder) holder).iv_contact_other.setImageResource(R.drawable.ic_avatar_default);
                }

                ((ChatOtherViewHolder) holder).ll_message_other.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        String text = ((ChatOtherViewHolder) holder).tv_chat_msg_other.getText().toString();
                        mActChatPresenter.copyText(text);
                        Utils.showToast(Utils.getContext(),"Copy Success!");
                        return false;
                    }
                });

            }

        }

        @Override
        public int getItemViewType(int position) {
            ChatMessageBean message = msgList.get(position);
            if (message.getMessageType() == Constants.TYPE_ONESELF) {
                return CHAT_ITEM_TYPE.CHAT_ITEM_TYPE_ME.ordinal();
            }
            if (message.getMessageType() == Constants.TYPE_OTHER) {
                return CHAT_ITEM_TYPE.CHAT_ITEM_TYPE_OTHER.ordinal();
            }

            return super.getItemViewType(position);
        }

        @Override
        public int getItemCount() {
            if (msgList==null) return 0;
            return msgList.size();
        }

        public List<ChatMessageBean> getMsgList() {
            return msgList;
        }



    }
    static class ChatOtherViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.iv_contact_other)
        ImageView iv_contact_other;
        @BindView(R.id.ll_message_other)
        LinearLayout ll_message_other;

        @BindView(R.id.tv_chat_msg_other)
        TextView tv_chat_msg_other;

        public ChatOtherViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    static class ChatMeViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.iv_contact_me)
        CircleImageView iv_contact_me;
        @BindView(R.id.ll_message_me)
        LinearLayout ll_message_me;
        @BindView(R.id.tv_chat_msg_me)
        TextView tv_chat_msg_me;
        public ChatMeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            ActivityCompat.finishAfterTransition(this);
            return true;
        }

        if(item.getItemId() == R.id.menu_send_file) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chatactivity_menu,menu);
//        menu.add(getResources().getString(R.string.file_doc))
//                .setIcon(R.drawable.svg_file_doc)
//                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//                    @Override
//                    public boolean onMenuItemClick(MenuItem item) {
//                        choiceDocFile();
//                        return true;
//                    }
//                });
        return true;
    }

private ArrayList<String>  filePathList = new ArrayList<>();
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

//        if(resultCode== Activity.RESULT_OK && data!=null) {
//            filePathList.clear();
//            if (data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS )!= null && mFileResultListener !=null){
//                filePathList.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS ));
//                mFileResultListener.onPathResult(filePathList);
//            }
//        }

        if(requestCode==mActChatPresenter.REQUEST_FILE_ACTIVITY_CODE&&resultCode== Activity.RESULT_OK && data!=null) {
            Uri uri = data.getData();
            String path = getAbsolutePath(uri);
            if (path==null && mFileResultListener !=null) {
//                LogUtils.e("Tag",path);
                Utils.showToast(this,"文件路径获取失败,请更换其它文件管理器");
                return;
            }
            filePathList.clear();
            filePathList.add(path);
            mFileResultListener.onPathResult(filePathList);
        }
    }

    private void choiceDocFile() {
        filePathList.clear();
        FilePickerBuilder.getInstance().setMaxCount(1)
                .setSelectedFiles(filePathList)
                .enableDocSupport(true)
                .pickFile(this);
    }


    public String getAbsolutePath(Uri uri) {
        String result = "";
        Cursor c = this.getContentResolver().query(uri, new String[] { "_data" }, null, null, null);
        if(c!=null){
            c.moveToFirst();
            result = c.getString(0);
            c.close();
        }else{
            result=uri.getPath();
        }
        return result;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mActChatPresenter.unRegist();
    }
}
