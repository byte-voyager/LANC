package cc.alonebo.lanc.fragment;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.ProgressBar;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import cc.alonebo.lanc.R;
import cc.alonebo.lanc.utils.Utils;


/**
 * Created by alonebo on 17-4-30.
 */

public class PrefFragment extends PreferenceFragment {

    private String TAG = PrefFragment.class.getName();
    private final String JSON_URL = "http://ogxh35je5.bkt.clouddn.com/lanc2.json";

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

                ProgressDialog dialog = ProgressDialog.show(getActivity(), "提示", "检查更新中...", true, true, null);
                checkVersion(dialog);
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


    private void checkVersion(final ProgressDialog dialog ) {
        RequestQueue mQueue = Volley.newRequestQueue(getActivity());
        final JsonObjectRequest request = new JsonObjectRequest(JSON_URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG,response.toString());
                        try {
                            int version = response.getInt("version");
                            if (version>Utils.getVersionCode()){
                                String updateDes = response.getString("version_des");
                                showDownloadDialog(updateDes);
                            }else {
                                Utils.showToast(getActivity(),"不需要更新");
                            }
                            dialog.dismiss();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        dialog.dismiss();
                        Utils.showToast(getActivity(),"NetWork Error!");
                    }
                });
        request.setShouldCache(false);
        mQueue.add(request);
    }

    private void showDownloadDialog(String updateDes) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("新版本详情：");
        builder.setMessage(updateDes);

        builder.setNegativeButton("再看看吧", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("现在更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }


}
