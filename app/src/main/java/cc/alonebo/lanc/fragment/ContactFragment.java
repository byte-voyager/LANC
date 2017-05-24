package cc.alonebo.lanc.fragment;

import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import cc.alonebo.lanc.ChatActivity;
import cc.alonebo.lanc.R;
import cc.alonebo.lanc.MyContactList;
import cc.alonebo.lanc.db.dao.ContactDao;
import cc.alonebo.lanc.model.bean.ContactBean;
import cc.alonebo.lanc.presenter.impl.FragContactPresenterImpl;
import cc.alonebo.lanc.presenter.impl.TransPresenterImpl;
import cc.alonebo.lanc.utils.LogUtils;
import cc.alonebo.lanc.utils.Utils;
import cc.alonebo.lanc.view.IFragContactView;
import cc.alonebo.lanc.widget.NewsView;
import de.hdodenhof.circleimageview.CircleImageView;

import static cc.alonebo.lanc.R.id.container;

/**
 * Created by alonebo on 17-5-16.
 */

public class ContactFragment extends BaseFragment implements IFragContactView, SwipeRefreshLayout.OnRefreshListener {
    private String TAG = ContactFragment.class.getName();
    @BindView(R.id.rv_contact)
    RecyclerView rv_contact;
    @BindView(R.id.fl_empty_contact)
    FrameLayout fl_empty_contact;
    @BindView(R.id.spl_refresh_contacts)
    SwipeRefreshLayout spl_refresh_contacts;

    private MyContactList mMyContactList;

    private ContactRecycleViewAdapter mAdapter;

    private TransPresenterImpl mTransPresenter = TransPresenterImpl.getInstance();
    private FragContactPresenterImpl mFragContactpresenter;
    ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    private boolean isNeedDelContact = false;
    private String needDelDeviceIdent = "";
    private Runnable command = new Runnable() {
        @Override
        public void run() {
            if (isNeedDelContact) {
                LogUtils.e(TAG,"开始删除:"+needDelDeviceIdent);
                mFragContactpresenter.delContact(needDelDeviceIdent);
                needDelDeviceIdent = "";
                isNeedDelContact = false;
            }else {
                LogUtils.e(TAG,"不删除:"+needDelDeviceIdent);
                needDelDeviceIdent = "";
            }

        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact,container,false);
        ButterKnife.bind(this,view);
        mFragContactpresenter = new FragContactPresenterImpl(this);
        mTransPresenter.setFragContactView(this);
        mTransPresenter.showContactList();//show contacts list
        spl_refresh_contacts.setOnRefreshListener(this);
        spl_refresh_contacts.setColorSchemeColors(Color.GREEN,Color.BLACK);
        return view;
    }

    @Override
    public void showEmptyContactView(boolean isShow) {
        if (isShow) {
            rv_contact.setVisibility(View.GONE);
            fl_empty_contact.setVisibility(View.VISIBLE);
            LogUtils.e(TAG,"isShow:"+isShow);
        } else {
            rv_contact.setVisibility(View.VISIBLE);
            fl_empty_contact.setVisibility(View.GONE);
            LogUtils.e(TAG,"isShow:"+isShow);
        }
    }

    @Override
    public void showContactList(MyContactList myContactList) {
        mMyContactList = myContactList;
        GridLayoutManager lm = new GridLayoutManager(Utils.getContext(),1);
        rv_contact.setLayoutManager(lm);
        mAdapter = new ContactRecycleViewAdapter(mMyContactList);
        rv_contact.setAdapter(mAdapter);
        checkShowEmptyView();
    }

    @Override
    public void updateContactList(MyContactList myContactList) {
        mMyContactList = myContactList;
        GridLayoutManager lm = new GridLayoutManager(Utils.getContext(),1);
        rv_contact.setLayoutManager(lm);
        mAdapter = new ContactRecycleViewAdapter(mMyContactList);
        rv_contact.setAdapter(mAdapter);
    }

    @Override
    public void addContactItem(ContactBean contactBean) {
        int identPos = mMyContactList.getIdentPos(contactBean.getDeviceIdent());
        if (mMyContactList.isExist(contactBean.getDeviceIdent())) {// 如果存在联系人,就更新
            updateContactNameIPAvatar(contactBean.getDeviceIdent(),contactBean.getName(),contactBean.getIp(),contactBean.getAvatarTime());
        }else {
        //否则
            if (identPos!=-1) {
                updateContact(contactBean);
            } else if(mAdapter!=null) {
                mMyContactList.add(contactBean);
                LogUtils.e(TAG,"添加联系人");
                mAdapter.notifyItemInserted(mMyContactList.size());
            }
        }
        checkShowEmptyView();

    }

    @Override
    public void removeContact(ContactBean contactBean) {
        int identPos = mMyContactList.getIdentPos(contactBean.getDeviceIdent());
        if (mAdapter!=null && identPos!=-1) {
            int pos = mMyContactList.getIdentPos(contactBean.getDeviceIdent());
            mMyContactList.removeContact(pos);
            mAdapter.notifyItemRemoved(pos);
        }
        checkShowEmptyView();
        //带按钮的




//纯文本的
        //Snackbar.make(container, "This is Snackbar", Snackbar.LENGTH_SHORT).show();
    }

    private void checkShowEmptyView() {
        if (mMyContactList.size()==0) {
            showEmptyContactView(true);
        }else {
            showEmptyContactView(false);
        }
    }

    @Override
    public void updateContact(ContactBean contactBean) {
        if (mAdapter!=null) {
            int pos = mMyContactList.getIdentPos(contactBean.getDeviceIdent());
            mMyContactList.update(pos,contactBean);
            mAdapter.notifyItemChanged(pos);
        }
        checkShowEmptyView();
    }

    @Override
    public void hideRefresh() {
        spl_refresh_contacts.setRefreshing(false);
    }

    @Override
    public void updateContactAvatar(String deviceIdent,long avatarTime) {
        if (mAdapter!=null) {

            int pos = mMyContactList.getIdentPos(deviceIdent);
            ContactBean contactBean = mMyContactList.get(pos);
            contactBean.setAvatarTime(avatarTime);
            mAdapter.notifyItemChanged(pos);
            LogUtils.i(TAG,"updateContactAvatar executed...");
        }
        checkShowEmptyView();
    }

    @Override
    public void updateContactName(String deviceIdent, String name) {

    }

    @Override
    public void addLastChat(String deviceIdent, String message, int type, long chatTime, boolean isAddNotReadMsgCount) {
        if (mAdapter==null) {
            return;
        }
        if (isAddNotReadMsgCount) {
            int pos = mMyContactList.getIdentPos(deviceIdent);
            ContactBean contactBean = mMyContactList.get(pos);
            contactBean.setMsgType(ContactDao.MSG_TYPE_LAST_CHAT_MSG_NOT_READ);
            contactBean.setLastChatMsg(message);
            contactBean.setLastChatTime(chatTime);
            int newCount = contactBean.getNotReadCount();
            newCount++;
            contactBean.setNotReadCount(newCount);
            mAdapter.notifyItemChanged(pos);
        }
        checkShowEmptyView();
    }

    @Override
    public void updateNotReadCount(String deviceIdent, int count) {
        if (mAdapter==null) {
            return;
        }

        int pos = mMyContactList.getIdentPos(deviceIdent);
        ContactBean contactBean = mMyContactList.get(pos);
        contactBean.setMsgType(ContactDao.MSG_TYPE_NORMAL);
        contactBean.setNotReadCount(count);
        mAdapter.notifyItemChanged(pos);

    }

    @Override
    public void updateLastChat(String deviceIdent, String message, int type, long chatTime, int count) {

    }

    @Override
    public void updateLastChatMsg(String deviceIdent, String msg) {
        if (mAdapter==null) {
            return;
        }
        int pos = mMyContactList.getIdentPos(deviceIdent);
        ContactBean contactBean = mMyContactList.get(pos);
        contactBean.setMsgType(ContactDao.MSG_TYPE_NORMAL);
        contactBean.setLastChatMsg(msg);
        mAdapter.notifyItemChanged(pos);
    }

    @Override
    public void updateContactNameIPAvatar(String deviceIdent, String name, String ip,long newTime) {
        if (mAdapter==null ) {
            return;
        }
        int pos = mMyContactList.getIdentPos(deviceIdent);
        boolean isNeedFresh = false;
        ContactBean contactBean = mMyContactList.get(pos);
        if (!contactBean.getIp().equals(ip)) {
            contactBean.setIp(ip);
            isNeedFresh = true;
        }
        if (!contactBean.getName().equals(name)) {
            contactBean.setName(name);
            isNeedFresh = true;
        }
        if (contactBean.getAvatarTime()!=newTime) {
            contactBean.setAvatarTime(newTime);
            isNeedFresh=true;
        }

        if (isNeedFresh){
            mAdapter.notifyItemChanged(pos);
        }
        checkShowEmptyView();

    }

    @Override
    public void onRefresh() {

        mTransPresenter.sendOnLineMsg();

    }

    class ContactRecycleViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
        private MyContactList contactList;
        public ContactRecycleViewAdapter(MyContactList contactList) {
            this.contactList = contactList;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact,parent,false);
            ContactHolder holder = new ContactHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
            LogUtils.i(TAG,"onBindViewHolder executed.");
            final ContactBean contact = contactList.get(position);
            File file = null;
            try {
                 file = new File(Utils.getAvatorPath(contact.getDeviceIdent(),contact.getAvatarTime()));
            }catch (Exception e){

            }
            LogUtils.e(TAG,"deviceIdent:"+contact.getDeviceIdent()+" AvatarPath:"+file.getAbsolutePath());

            //绑定布局
            if (holder instanceof ContactHolder) {
                LogUtils.e(TAG,"onBindViewHolder----------------");
                ((ContactHolder)holder).tv_online_contact_name.setText(contact.getName());
                ((ContactHolder)holder).tv_online_contact_ip.setText(contact.getIp());
                CircleImageView civ_online_contact = ((ContactHolder) holder).civ_online_contact;
                if (contact.getIsOnline()== ContactDao.IS_ONLINE_TYPE_OFFLINE) {
                    ColorMatrix colorMatrix = new ColorMatrix();
                    colorMatrix.setSaturation(0);
                    ColorMatrixColorFilter colorFilter = new ColorMatrixColorFilter(colorMatrix);
                    civ_online_contact.setColorFilter(colorFilter);
                }
            Glide.with(ContactFragment.this).load(file).error(R.drawable.svg_ta128).into(civ_online_contact);
                ((ContactHolder)holder).cv_contact_online.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ChatActivity.actionStart(Utils.getContext(),contact.getName(),contact.getIp(),contact.getDeviceIdent(),contact.getAvatarTime());
                    }
                });
                ((ContactHolder) holder).cv_contact_online.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
//                       v.setVisibility(View.GONE);
                        removeContact(contact);
                        isNeedDelContact = true;
                        needDelDeviceIdent = contact.getDeviceIdent();
                        Snackbar.make(((ContactHolder) holder).cv_contact_online, "删除成功!", Snackbar.LENGTH_LONG)
                                .setAction("撤销", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        addContactItem(contact);
                                        isNeedDelContact = false;
                                    }
                                }).show();
                        executorService.schedule(command,5000, TimeUnit.MILLISECONDS);
                        return false;
                    }
                });
                ((ContactHolder)holder).nv_not_read_view.setCount(contact.getNotReadCount()==0?"0":contact.getNotReadCount()+"");
                if (contact.getLastChatMsg()!=null && contact.getMsgType()== ContactDao.MSG_TYPE_LAST_CHAT_MSG_NOT_READ) {
                    ((ContactHolder) holder).tv_last_chat_msg.setTextColor(Color.BLUE);
                    ((ContactHolder) holder).tv_last_chat_msg.setText(contact.getLastChatMsg());
                }else {
                    ((ContactHolder) holder).tv_last_chat_msg.setTextColor(Color.GRAY);
                    ((ContactHolder) holder).tv_last_chat_msg.setText(contact.getLastChatMsg()==null?"":contact.getLastChatMsg());
                }

                ((ContactHolder) holder).tv_last_chat_time.setText(contact.getLastChatTime()==0?Utils.getCurrentTime(System.currentTimeMillis()):Utils.getCurrentTime(contact.getLastChatTime()));

            }
        }

        @Override
        public int getItemCount() {
            return contactList.size();
        }

        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id){
                case R.id.cv_contact_online:
                    break;
            }
        }


    }
    static class ContactHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.cv_contact_online)
        CardView cv_contact_online;
        @BindView(R.id.civ_online_contact)
        CircleImageView civ_online_contact;
        @BindView(R.id.tv_online_contact_name)
        TextView tv_online_contact_name;
        @BindView(R.id.tv_online_contact_ip)
        TextView tv_online_contact_ip;
        @BindView(R.id.tv_last_chat_msg)
        TextView tv_last_chat_msg;
        @BindView(R.id.tv_last_chat_time)
        TextView tv_last_chat_time;
        @BindView(R.id.nv_not_read_view)
        NewsView nv_not_read_view;
        public ContactHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mFragContactpresenter.unRegist();
    }
}
