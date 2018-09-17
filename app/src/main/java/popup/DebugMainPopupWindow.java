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
 * Created by xdhwwdz20112163.com on 2018/3/19.
 */

public class DebugMainPopupWindow {

    private View mMainView;
    private PopupWindow mPopupWindow;
    private Button mButton;

    public void show(View parent, View.OnClickListener listener) {
        ViewGroup group = (ViewGroup) mMainView.getParent();
        if (group != null) {
            group.removeAllViews();
        }
        mButton.setOnClickListener(listener);
        Context context = GeminiApplication.getAppContext();
        mPopupWindow = new PopupWindow(mMainView,
                (int) context.getResources().getDimension(R.dimen.p4400),
                (int) context.getResources().getDimension(R.dimen.p6100),
                true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
    }

    private DebugMainPopupWindow() {
        mMainView = LayoutInflater.from(GeminiApplication.getAppContext()).inflate(R.layout.popup_main_debug, null);
        mButton = mMainView.findViewById(R.id.id_popup_main_debug_button);
    }

    public static DebugMainPopupWindow instance() {
        return InlineClass.sInstance;
    }

    private static class InlineClass {
        public static final DebugMainPopupWindow sInstance = new DebugMainPopupWindow();
    }
}
