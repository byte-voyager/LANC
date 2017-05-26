package cc.alonebo.lanc.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import cc.alonebo.lanc.R;
import cc.alonebo.lanc.utils.LogUtils;

/**
 * Created by alonebo on 17-4-22.
 */


public class NewsView extends View {

    private Paint mPaint;
    private String count = "1"; //= "↑";
    private Rect bounds = new Rect();
    private int circleColor = Color.BLACK;

    public NewsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }


    public NewsView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public NewsView(Context context) {
        this(context,null);
    }


    private void init(Context context, AttributeSet attrs, int defStyleAttr) {

        TypedArray ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.NewsView, defStyleAttr, 0);
        int index = ta.getIndex(R.styleable.NewsView_count);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        count = ta.getString(index);
        if ("0".equals(count)) {
//            circleColor = Color.WHITE;
            setVisibility(GONE);
        } else {
//            circleColor = Color.BLACK;
            setVisibility(VISIBLE);
        }
        mPaint.getTextBounds(count, 0, count.length(), bounds);
    }

    public void setCount(String count) {
        this.count = count;
        if ("0".equals(count)) {
           // circleColor = Color.WHITE;
            setVisibility(GONE);
        } else {
            //circleColor = Color.BLACK;
            setVisibility(VISIBLE);
        }
        invalidate();

    }

    public void addCount(String count) {
        this.count = count;
        if ("0".equals(count)) {
            circleColor = Color.WHITE;
        } else {
            circleColor = Color.BLACK;
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int cy = getMeasuredHeight() / 2;
        mPaint.setColor(circleColor);
        canvas.drawCircle(getMeasuredWidth()/2,getMeasuredHeight() / 2,cy, mPaint);
        mPaint.setColor(Color.WHITE);
        mPaint.setTextSize(getMeasuredWidth()/2);
        mPaint.setTextAlign(Paint.Align.CENTER);
        float x = getMeasuredWidth()/2;
        float y = getMeasuredHeight()/2;
        canvas.drawText(count,x,y + Math.abs(bounds.top),mPaint);
		canvas.drawPoint(x, y, mPaint);
    }

}

