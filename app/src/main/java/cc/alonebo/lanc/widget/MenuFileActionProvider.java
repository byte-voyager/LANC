package cc.alonebo.lanc.widget;

import android.content.Context;
import android.support.v4.view.ActionProvider;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;

import org.greenrobot.eventbus.EventBus;

import cc.alonebo.lanc.Constants;
import cc.alonebo.lanc.R;
import cc.alonebo.lanc.model.bean.EventChoiceFile;
import cc.alonebo.lanc.utils.Utils;


/**
 * Created by alonebo on 17-4-6.
 */

public class MenuFileActionProvider extends ActionProvider {

    private Context mContext;

    /**
     * Creates a new instance.
     *
     * @param context Context for accessing resources.
     */
    public MenuFileActionProvider(Context context) {
        super(context);
        this.mContext = context;
    }

    @Override
    public View onCreateActionView() {
        return null;
    }



    @Override
    public void onPrepareSubMenu(SubMenu subMenu) {
        subMenu.clear();
//        subMenu.add(mContext.getResources().getString(R.string.file_doc))
//                .setIcon(R.drawable.svg_file_doc)
//                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//                    @Override
//                    public boolean onMenuItemClick(MenuItem item) {
//                        return clickEvent(EventChoiceFile.EVENT_TYPES_CHOICE_DOC);
//                    }
//                });
//
//        subMenu.add(mContext.getResources().getString(R.string.file_zip))
//                .setIcon(R.drawable.svg_file_zip)
//                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//                    @Override
//                    public boolean onMenuItemClick(MenuItem item) {
//                        return clickEvent(EventChoiceFile.EVENT_TYPES_CHOICE_ZIP);
//                    }
//                });
//
//        subMenu.add(mContext.getResources().getString(R.string.file_apk))
//                .setIcon(R.drawable.svg_file_apk)
//                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//                    @Override
//                    public boolean onMenuItemClick(MenuItem item) {
//                        return clickEvent(EventChoiceFile.EVENT_TYPES_CHOICE_APK);
//                    }
//                });
//
//        subMenu.add(mContext.getResources().getString(R.string.file_video))
//                .setIcon(R.drawable.svg_file_video)
//                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//                    @Override
//                    public boolean onMenuItemClick(MenuItem item) {
//                        return clickEvent(EventChoiceFile.EVENT_TYPES_CHOICE_VIDEO);
//                    }
//                });

        subMenu.add(mContext.getResources().getString(R.string.file_custom))
                .setIcon(R.drawable.svg_file_custom)
                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        return clickEvent(EventChoiceFile.EVENT_TYPES_CHOICE_CUSTOM);
                    }
                });

        super.onPrepareSubMenu(subMenu);
    }

    @Override
    public boolean hasSubMenu() {
        return true;
    }

    private boolean clickEvent(int eventType) {
        if (Constants.IS_TRANSINT_FILE){
            Utils.showToast(Utils.getContext(),"有文件正在传输!");
            return false;
        }
        EventBus.getDefault().post(new EventChoiceFile(eventType));//send to ActChatPresenterImpl
        return false;
    }
}
