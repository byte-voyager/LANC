package cc.alonebo.lanc.fragment;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AlertDialog;
import android.widget.ProgressBar;

import java.io.File;

import cc.alonebo.lanc.R;
import cc.alonebo.lanc.utils.Utils;


/**
 * Created by alonebo on 17-4-30.
 */

public class PrefFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
        Preference key_download_dir = findPreference("key_download_dir");
        key_download_dir.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
               openAssignFolder(Utils.getInnerSDCardPath());
                return false;
            }
        });

        Preference key_check_update = findPreference("key_check_update");
        key_check_update.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
//                ProgressDialog pd = new ProgressDialog(getActivity());
//                pd.setTitle("检查更新中...");
//                pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//                pd.setView(new ProgressBar(Utils.getContext()));
//                pd.show();
                ProgressDialog dialog = ProgressDialog.show(getActivity(), "提示", "检查更新中...", true, true, null);
                return false;
            }
        });
    }

    private void openAssignFolder(String path){
        File file = new File(path);
        if(null==file || !file.exists()){
            return;
        }
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(file), "file/*");
        try {
            startActivity(intent);
            startActivity(Intent.createChooser(intent,"选择浏览工具"));
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }
}
