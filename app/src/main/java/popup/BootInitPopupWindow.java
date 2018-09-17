package popup;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jf.geminjava.R;

import application.GeminiApplication;

/**
 * Created by xdhwwdz20112163.com on 2018/3/27.
 */

public class BootInitPopupWindow implements PopupWindow.OnDismissListener {

    private View mMainView;
    private PopupWindow mPopupWindow;
    private TextView mTextView;
    private AnimationSet mAnimationSet;
    private Activity mTargetActivity;

    private void setBackgroundAlpha(float alpha) {
        WindowManager.LayoutParams params = mTargetActivity.getWindow().getAttributes();
        params.alpha = alpha;
        mTargetActivity.getWindow().setAttributes(params);
    }

    public void dismiss() {
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
        }
    }

    public void show(View parent, Activity activity) {

        Context context = GeminiApplication.getAppContext();
        ViewGroup group = (ViewGroup) mMainView.getParent();
        if (group != null) {
            group.removeAllViews();
        }
        mPopupWindow = new PopupWindow(mMainView,
                (int) context.getResources().getDimension(R.dimen.p8000),
                (int) context.getResources().getDimension(R.dimen.p8000), true);
        mPopupWindow.setOnDismissListener(this);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
        mTextView.setAnimation(mAnimationSet);
        mTargetActivity = activity;
        setBackgroundAlpha(0.4f);
    }

    @Override
    public void onDismiss() {
        mAnimationSet.cancel();
        setBackgroundAlpha(1.0f);
        mTargetActivity = null;
    }

    private BootInitPopupWindow() {
        Context context = GeminiApplication.getAppContext();
        mMainView = LayoutInflater.from(context).inflate(R.layout.popup_boot_init, null);
        mTextView = mMainView.findViewById(R.id.id_popup_boot_init_text_view);

        RotateAnimation rotateAnimation = new RotateAnimation(0, 360, RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(2000);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setRepeatCount(RotateAnimation.INFINITE);
        rotateAnimation.setRepeatMode(RotateAnimation.REVERSE);

        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 0.3f, 1.0f, 0.3f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(2000);
        scaleAnimation.setInterpolator(new LinearInterpolator());
        scaleAnimation.setRepeatCount(RotateAnimation.INFINITE);
        scaleAnimation.setRepeatMode(RotateAnimation.REVERSE);

        mAnimationSet = new AnimationSet(true);
        mAnimationSet.addAnimation(scaleAnimation);
        mAnimationSet.addAnimation(rotateAnimation);
    }

    public static BootInitPopupWindow instance() {
        return InlineClass.sInstance;
    }

    private static final class InlineClass {
        public static final BootInitPopupWindow sInstance = new BootInitPopupWindow();
    }

}
