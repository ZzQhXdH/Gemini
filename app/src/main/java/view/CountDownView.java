package view;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.jf.geminjava.R;

import util.Logger;

/**
 * Created by xdhwwdz20112163.com on 2018/1/11.
 */

public class CountDownView extends View implements Animator.AnimatorListener {

    private int mMaxCount = 100;
    private float mCurrentCount = 0;
    private OnTimeOutListener mOnTimeOutListener = null;
    private ObjectAnimator mAnimator;

    public CountDownView(Context context) {
        super(context);
    }

    public CountDownView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

    }

    public CountDownView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setMaxCount(int maxCount) {
        mMaxCount = maxCount;
        startAnimator(maxCount);
        invalidate();
    }

    public void setCurrentCount(float currentCount) {

        mCurrentCount = currentCount;
        invalidate();
    }

    public void setOnTimeOutListener(OnTimeOutListener listener) {
        mOnTimeOutListener = listener;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        float width = getMeasuredWidth();
        float height = getMeasuredHeight();

        float d = (width < height ? width : height) * 0.8f;
        float r = d / 2;
        float x0 = (width - d) / 2;
        float y0 = (height - d) / 2;
        float cx = width / 2;
        float cy = height / 2;
        float ccd = r * 0.3f;
        float r1 = ccd / 2;
        float cx1 = cx;
        float cy1 = cy - r;
        float a = mCurrentCount / mMaxCount * 360;

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(ccd);
        paint.setColor(Color.WHITE);
        canvas.drawCircle(cx, cy, r, paint);

        paint.setColor(0xFFFF6347);
        RectF rectF = new RectF(x0, y0, x0 + d, y0 + d);
        canvas.drawArc(rectF, -90, a, false, paint);

        paint.setStrokeWidth(1);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(cx1, cy1, r1, paint);

        x0 = (float) (cx + r * Math.sin(a / 180 * Math.PI));
        y0 = (float) (cy - r * Math.cos(a / 180 * Math.PI));
        canvas.drawCircle(x0, y0, r1, paint);

        paint.setColor(0xFF363636);
        String text = ((int) mCurrentCount) + "S";
        paint.setTextAlign(Paint.Align.CENTER);
        Rect rect = new Rect();
        paint.setTextSize(getResources().getDimension(R.dimen.p500));
        paint.getTextBounds(text, 0, text.length(), rect);
        canvas.drawText(text, 0, text.length(), cx, cy + rect.height() / 2, paint);
    }

    private void startAnimator(int maxCount) {

        mAnimator = ObjectAnimator.ofFloat(this, "currentCount", 0, maxCount);
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.setDuration(150 * 1000);
        mAnimator.addListener(this);
        mAnimator.start();
    }

    @Override
    public void onAnimationStart(Animator animation) {
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        Logger.instance().d(CountDownView.class.getSimpleName(), "动画结束");
        if (mOnTimeOutListener != null) {
            mOnTimeOutListener.onTimeOut();
            mOnTimeOutListener = null;
        }
    }

    public void cancel() {
        mAnimator.cancel();
    }

    @Override
    public void onAnimationCancel(Animator animation) {
    }

    @Override
    public void onAnimationRepeat(Animator animation) {
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
    }

    public interface OnTimeOutListener {

        void onTimeOut();
    }
}
