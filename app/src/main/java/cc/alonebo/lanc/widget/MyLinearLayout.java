package cc.alonebo.lanc.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import cc.alonebo.lanc.model.listener.KeyBoardListener;


/**
 * Created by alonebo on 17-4-16.
 */

public class MyLinearLayout extends LinearLayout {

    private KeyBoardListener listener;

    public void setListener(KeyBoardListener listener) {
        this.listener = listener;
    }

    public MyLinearLayout(Context context) {
        super(context);
        init();
    }

    public MyLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public MyLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        addOnLayoutChangeListener(new OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
               /*
               * 初始化: 1.  top:1910;oldTop:0
               *        2.   top:1910;oldTop:1910
               *  弹出:  top:1206;oldTop:1910 top<oldtop && oldtop-top>500
               *  收起:    top:1910;oldTop:1206
               * */

                /*
                * 初始化 :top:1719;oldTop:0 ;   1719;oldTop:1719
                *
                * */
                if (top<oldTop && oldTop-top>500) {
                    if (listener!=null) {
                        listener.onKeyBoardShow();
                    }
                }
            }
        });
    }
}
