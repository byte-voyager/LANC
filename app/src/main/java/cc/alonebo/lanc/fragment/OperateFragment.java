package cc.alonebo.lanc.fragment;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import cc.alonebo.lanc.Constants;
import cc.alonebo.lanc.MyApplication;
import cc.alonebo.lanc.PrefActivity;
import cc.alonebo.lanc.R;
import cc.alonebo.lanc.model.impl.FtpModelImpl;
import cc.alonebo.lanc.presenter.IFragOperatePresenter;
import cc.alonebo.lanc.presenter.impl.FragOperatePresenterImpl;
import cc.alonebo.lanc.utils.NetUtils;
import cc.alonebo.lanc.utils.SPUtils;
import cc.alonebo.lanc.utils.Utils;
import cc.alonebo.lanc.view.IFragOperateView;

/**
 * Created by alonebo on 17-5-16.
 */

public class OperateFragment extends BaseFragment implements IFragOperateView{
    private Bitmap mAvatar;
    @BindView(R.id.iv_avatar)
    ImageView iv_avatar;
    @BindView(R.id.tv_device_name)
    TextView tv_device_name;
    @BindView(R.id.tv_device_ip)
    TextView tv_device_ip;
    @BindView(R.id.ll_ftp_des)
    LinearLayout ll_ftp_des;
    @BindView(R.id.sc_ftp)
    SwitchCompat sc_ftp;
    @BindView(R.id.tv_ftp_state_des)
    TextView tv_ftp_state_des;
    @BindView(R.id.iv_parallax_bg)
    ImageView iv_parallax_bg;

    private FragOperatePresenterImpl mFragOperatePresenterImpl;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_operate,container,false);
        ButterKnife.bind(this,view);
        mFragOperatePresenterImpl = new FragOperatePresenterImpl(this);
        initView();
        return view;
    }

    private void initView() {
        tv_device_ip.setText(NetUtils.getLocalIpAddress());
        updateAvatar(BitmapFactory.decodeFile(Utils.getMyAvatarAbsPath()));
        tv_device_name.setText((String)SPUtils.get(Utils.getContext(),Constants.SP_DEVICE_NAME,Build.MODEL));
        sc_ftp.setChecked(FtpModelImpl.IS_FTP_RUNNING?true:false);
        if (sc_ftp.isChecked()) {
            String des = Utils.getString(R.string.ftp_state_des_on);
            des = des +"\nftp://" + NetUtils.getLocalIpAddress() + ":" + Constants.PORT_FTP;
            tv_ftp_state_des.setText(des);
        } else {

            tv_ftp_state_des.setText(R.string.ftp_state_des_off);
        }
        showFtp(sc_ftp.isChecked());
        mFragOperatePresenterImpl.setParallaxBackground();
    }

    @OnClick(R.id.iv_avatar)
    public void clickAvatar(){

        if (ContextCompat.checkSelfPermission(mActivity, android.Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
            getPermission();
        }else {
            //show dialog to select
            String[] items = new String[] {Utils.getString(R.string.dialog_take)};
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0:
                            mFragOperatePresenterImpl.setAvatar(mActivity);
                            break;
                        default:
                            break;
                    }
                    dialog.dismiss();
                }
            });
            builder.show();
        }

    }

    @OnClick(R.id.iv_edit_name)
    public void clickEditName() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        View view = LayoutInflater.from(mActivity).inflate(R.layout.dialog_name, null, false);
        final EditText et_device_name = (EditText) view.findViewById(R.id.et_device_name);
        String name = (String) SPUtils.get(mActivity, Constants.SP_DEVICE_NAME, Build.MODEL);
        et_device_name.setText(name);
        builder.setView(view);
        builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        })
        .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = et_device_name.getText().toString().trim();
                if ("".equals(name)) {
                    Utils.showToast(mActivity,Utils.getString(R.string.not_empty));
                }else {
                    mFragOperatePresenterImpl.setName(name);
                    dialog.dismiss();
                }

            }
        });
        builder.show();
    }

    @OnClick(R.id.sc_ftp)
    public void clickFtp(){
        mFragOperatePresenterImpl.switchFtp(sc_ftp.isChecked());
        if (sc_ftp.isChecked()) {
            String des = Utils.getString(R.string.ftp_state_des_on);
            des = des +"\nftp://" + NetUtils.getLocalIpAddress() + ":" + Constants.PORT_FTP;
            tv_ftp_state_des.setText(des);
        } else {

            tv_ftp_state_des.setText(R.string.ftp_state_des_off);
        }
    }

    @OnLongClick(R.id.tv_ftp_state_des)
    public boolean longClickFtpDes(){
        if (sc_ftp.isChecked()){
            ClipboardManager cmb = (ClipboardManager) Utils.getContext() .getSystemService(Context.CLIPBOARD_SERVICE);
            String content = "ftp://" + NetUtils.getLocalIpAddress() + ":" + Constants.PORT_FTP;

            cmb.setText(content);
            Utils.showToast(Utils.getContext(),"Success copy!");
        }

        return true;
    }

    @OnClick(R.id.ll_setting)
    public void clickSetting() {
        Intent intent = new Intent(Utils.getContext(), PrefActivity.class);
        mActivity.startActivity(intent);
    }

    @OnClick(R.id.bt_set_wall)
    public void clickSetWal(){
        mFragOperatePresenterImpl.setBingWall();
    }

    @OnClick(R.id.bt_exit_app)
    public void clickExitApp() {

        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle("退出程序?");
        builder.setMessage("退出程序将不会占用任何资源,也不会接收到局域网其他人的信息。");
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mFragOperatePresenterImpl.exitApp();

                dialog.dismiss();
            }
        });
        builder.show();

    }

    @Override
    public void updateAvatar(Bitmap bitmap) {
        if (bitmap==null) return;
        if (mAvatar!=null && !mAvatar.isRecycled()){
            mAvatar.recycle();
        }
        mAvatar = bitmap;
        iv_avatar.setImageBitmap(bitmap);
    }

    @Override
    public void updateName(String name) {
        tv_device_name.setText(name);
    }

    @Override
    public void showFtp(boolean isShow) {
        ll_ftp_des.setVisibility(isShow?View.VISIBLE:View.GONE);
    }

    @Override
    public void updateIp(String ip) {
        tv_device_ip.setText(ip);
    }

    @Override
    public void showParallaxBackground(String image) {
        Glide.with(this).load(image).error(R.drawable.girl).into(iv_parallax_bg);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mFragOperatePresenterImpl.unRegist();
    }

    private void getPermission() {
        if (ContextCompat.checkSelfPermission(mActivity, android.Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mActivity,new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length>0 && grantResults[0] !=  PackageManager.PERMISSION_GRANTED) {
                    Utils.showToast(mActivity,"无法获得权限,应用不能正常运行!");
                }
                break;
        }
    }
}
