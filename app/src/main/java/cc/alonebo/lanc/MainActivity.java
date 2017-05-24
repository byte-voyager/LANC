package cc.alonebo.lanc;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cc.alonebo.lanc.fragment.ContactFragment;
import cc.alonebo.lanc.fragment.OperateFragment;
import cc.alonebo.lanc.model.bean.EventAvatar;
import cc.alonebo.lanc.model.listener.AvatarListener;
import cc.alonebo.lanc.presenter.impl.ActMainPresenterImpl;
import cc.alonebo.lanc.utils.LogUtils;
import cc.alonebo.lanc.utils.NetUtils;
import cc.alonebo.lanc.utils.Utils;
import cc.alonebo.lanc.view.IActMainView;
import droidninja.filepicker.FilePickerConst;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements IActMainView,EasyPermissions.PermissionCallbacks{

    private static final int PERMISSION_CODE = 20;
    private String TAG = MainActivity.class.getName();

    @BindView(R.id.fl_content)
    FrameLayout fl_content;
    @BindView(R.id.vp_content)
    ViewPager vp_content;

    private ActMainPresenterImpl mActMainPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        test();
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        init();
    }

    private void test() {
        Utils.showToast(this, NetUtils.getLocalIpAddress());
    }

    private void init() {
        mActMainPresenter = new ActMainPresenterImpl(this);
        mActMainPresenter.showView();
        mActMainPresenter.startTransService(this);

    }

    @Override
    public void initView() {
        final Fragment[] fragments = new Fragment[2];
        fragments[0] = new ContactFragment();
        fragments[1] = new OperateFragment();

        ViewPager viewPager = (ViewPager) findViewById(R.id.vp_content);
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments[position];
            }

            @Override
            public int getCount() {
                return fragments.length;
            }
        });
    }

    @Override
    public void initToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_main);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setTitle(R.string.app_name);
    }

    @Override
    public void initData() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            LogUtils.i(TAG," hasPermissions WRITE_EXTERNAL_STORAGE");
        } else {
            EasyPermissions.requestPermissions(this, "APP权限",
                    PERMISSION_CODE, perms);
        }
    }

    private ArrayList<String> avatarPath = new ArrayList<String>();
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode== Activity.RESULT_OK && data!=null) {
            avatarPath.clear();
            avatarPath.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA ));
            if (avatarPath.size()==0) return;//可能没有选择
            EventBus.getDefault().post(new EventAvatar().setAvatarPath(avatarPath.get(0)));//post to FragOperatePresenterImpl#eventUpdateAvatar
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if (requestCode==PERMISSION_CODE) {

        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
            if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
                new AppSettingsDialog.Builder(this).build().show();
            }
    }
}
