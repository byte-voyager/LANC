package cc.alonebo.lanc.fragment;

import android.app.Activity;
import android.support.v4.app.Fragment;

/**
 * Created by alonebo on 17-5-17.
 */

public class BaseFragment extends Fragment{
    protected Activity mActivity;
    @Override
    public void onAttach(Activity activity) {
        this.mActivity = activity;
        super.onAttach(activity);
    }
}
