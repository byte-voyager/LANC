package cc.alonebo.lanc.presenter;

import android.content.Context;

import cc.alonebo.lanc.MainActivity;
import cc.alonebo.lanc.view.IActMainView;

/**
 * Created by alonebo on 17-5-16.
 */

public interface IActMainPresenter {

    void showView();

    void startTransService(Context context);
}
