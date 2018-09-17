package popup;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jf.geminjava.R;

import application.GeminiApplication;
import util.Logger;
import view.WaitView;


/**
 * Created by xdhwwdz20112163.com on 2018/3/27.
 */

public class WaitPopupWindow {

    private View mMainView;
    private PopupWindow mPopupWindow;
    private WaitView mWaitView;
    private TextView mTextView;
    private int mWidth;
    private int mHeight;

    public void setText(final String text) {
        mTextView.setText(text);
    }

    public void dismiss() {
        mPopupWindow.dismiss();
    }

    public void show(View parent) {

        ViewGroup group = (ViewGroup) mMainView.getParent();
        if (group != null) {
            group.removeAllViews();
        }
        mPopupWindow = new PopupWindow(mMainView, mWidth, mHeight, true);
        mPopupWindow.setOutsideTouchable(false);
        mTextView.setText("");
        mPopupWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
    }

    private WaitPopupWindow() {

        Context context = GeminiApplication.getAppContext();
        mMainView = LayoutInflater.from(context).inflate(R.layout.popup_wait, null);
        mWaitView = mMainView.findViewById(R.id.id_popup_wait_wait_view);
        mTextView = mMainView.findViewById(R.id.id_popup_wait_text_view);
        mWidth = (int) context.getResources().getDimension(R.dimen.p7000);
        mHeight = (int) context.getResources().getDimension(R.dimen.p8000);
    }

    public static WaitPopupWindow instance() {
        return InlineClass.sInstance;
    }

    private static class InlineClass {
        public static final WaitPopupWindow sInstance = new WaitPopupWindow();
    }

}
