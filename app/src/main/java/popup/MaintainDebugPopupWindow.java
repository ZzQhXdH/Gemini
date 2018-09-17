package popup;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;

import com.jf.geminjava.R;

import application.GeminiApplication;

/**
 * Created by xdhwwdz20112163.com on 2018/3/23.
 */

public class MaintainDebugPopupWindow {

    private View mMainView;
    private PopupWindow mPopupWindow;
    private Button mButtonReplenish;
    private Button mButtonDevice;
    private Button mButtonFinish;
    private Button mButtonDebug;
    private OnDebugListener mOnDebugListener;

    public void show(View parent, OnDebugListener listener) {

        mOnDebugListener = listener;

        ViewGroup group = (ViewGroup) mMainView.getParent();
        if (group != null) {
            group.removeAllViews();
        }
        Context context = GeminiApplication.getAppContext();
        mPopupWindow = new PopupWindow(mMainView,
                (int) context.getResources().getDimension(R.dimen.p4640),
                (int) context.getResources().getDimension(R.dimen.p2520),
                true);
        mPopupWindow.setAnimationStyle(R.style.style_maintain_debug_popup);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.showAtLocation(parent, Gravity.CENTER, 0,
                - (int) context.getResources().getDimension(R.dimen.p5000));
    }

    private MaintainDebugPopupWindow() {
        Context context = GeminiApplication.getAppContext();
        mMainView = LayoutInflater.from(context).inflate(R.layout.popup_maintain_debug, null);
        mButtonReplenish = mMainView.findViewById(R.id.id_popup_maintain_debug_replenish_button);
        mButtonDevice = mMainView.findViewById(R.id.id_popup_maintain_debug_device_button);
        mButtonFinish = mMainView.findViewById(R.id.id_popup_maintain_debug_finish_button);
        mButtonDebug = mMainView.findViewById(R.id.id_popup_maintain_debug_debug_button);

        mButtonReplenish.setOnClickListener(v -> {
            mPopupWindow.dismiss();
            mOnDebugListener.onClickReplenish();
        });
        mButtonDebug.setOnClickListener(v -> {
            mPopupWindow.dismiss();
            mOnDebugListener.onClickDebug();
        });
        mButtonDevice.setOnClickListener(v -> {
            mPopupWindow.dismiss();
            mOnDebugListener.onClickDevice();
        });
        mButtonFinish.setOnClickListener(v -> {
            mPopupWindow.dismiss();
            mOnDebugListener.onClickFinish();
        });
    }

    public interface OnDebugListener {

        void onClickReplenish();

        void onClickDevice();

        void onClickFinish();

        void onClickDebug();
    }

    public static MaintainDebugPopupWindow instance() {
        return InlineClass.sInstance;
    }

    private static final class InlineClass {
        public static final MaintainDebugPopupWindow sInstance = new MaintainDebugPopupWindow();
    }
}
