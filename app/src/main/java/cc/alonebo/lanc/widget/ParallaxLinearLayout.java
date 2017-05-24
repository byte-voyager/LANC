package cc.alonebo.lanc.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;

import com.nineoldandroids.animation.ValueAnimator;

import cc.alonebo.lanc.MainActivity;
import cc.alonebo.lanc.model.listener.LoadUriSuccessListener;
import cc.alonebo.lanc.utils.LogUtils;

/**
 * Created by alonebo on 17-4-21.
 */

public class ParallaxLinearLayout extends LinearLayout {
    private String TAG = ParallaxLinearLayout.class.getName();
    private ViewGroup.MarginLayoutParams layoutParams;
    private int mCurrentY = 0;
    private int mStartY = 0;
    private int mOffsetY = 0;
    private  int MAX_OFFSET_Y = 1500 * 4;
    DisplayMetrics dm = new DisplayMetrics();

    public ParallaxLinearLayout(Context context) {
        this(context,null);
    }

    public ParallaxLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ParallaxLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {


        dm = getResources().getDisplayMetrics();
        int height = dm.heightPixels;
        if (height>=1){
            MAX_OFFSET_Y = height/2;
            LogUtils.e(TAG, "MAX_OFFSET_Y:"+MAX_OFFSET_Y);
        }else {
            LogUtils.e(TAG, "MAX_OFFSET_Y---Not:"+MAX_OFFSET_Y);
        }
    }




    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
//        MAX_OFFSET_Y = h / 2;
        layoutParams = (ViewGroup.MarginLayoutParams) getLayoutParams();
        LogUtils.e(TAG, "MAX_OFFSET_Y:"+MAX_OFFSET_Y);
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_UP:
                startAnim();
                break;
            case MotionEvent.ACTION_MOVE:
                mCurrentY = (int) event.getRawY();

                mOffsetY = (mCurrentY - mStartY);//得到滑动的差值
                if(mOffsetY > MAX_OFFSET_Y) {
                    mOffsetY = MAX_OFFSET_Y;
                }
                layoutParams.topMargin = (mOffsetY /2);// /3吃力效果
                requestLayout();
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 滑动动画
     */
    private void startAnim() {
        ValueAnimator animator = ValueAnimator.ofInt(layoutParams.topMargin,0);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                int animatedValue = (Integer) animator.getAnimatedValue();
                layoutParams.topMargin = animatedValue;
                requestLayout();
            }
        });
        animator.setInterpolator(new OvershootInterpolator(4));//弹性的插值器
        animator.setDuration(400);
        animator.start();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        return super.onInterceptTouchEvent(ev);
    }
}
