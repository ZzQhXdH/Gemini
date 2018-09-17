package view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.jf.geminjava.R;

/**
 * Created by xdhwwdz20112163.com on 2018/1/5.
 */

public class ScheduleView extends View {

    private int mCurrentIndex = 0;
    private int mCount = 3;
    private Paint mPaint = null;
    private int mIndexColor = 0xFFF06600;

    public ScheduleView(Context context) {
        super(context);
        init();
    }

    public ScheduleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ScheduleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
    }

    public void setCurrentIndex(int index) {

        if ((index < 0) || (index >= mCount)) {
            return;
        }
        mCurrentIndex = index;
        invalidate();
    }

    public void setCount(int count) {
        mCount = count;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        float rawWidth = getMeasuredWidth();
        float rawHeight = getMeasuredHeight();
        float x0 = (rawWidth - (mCount + 1) * 35) / 2;
        float y0 = (rawHeight - 20) / 2;
        float x = x0 + 35;
        float y = y0 + 10;
        mPaint.setColor(mIndexColor);
        for (int i = 0; i < mCount; i ++) {
            if (i == mCurrentIndex) {
                mPaint.setColor(Color.WHITE);
                canvas.drawCircle(x, y, 10, mPaint);
                mPaint.setColor(mIndexColor);
            } else {
                canvas.drawCircle(x, y, 10, mPaint);
            }
            x += 35;
        }
    }

}
