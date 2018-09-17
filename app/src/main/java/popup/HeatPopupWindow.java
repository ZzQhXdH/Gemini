package popup;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;

import com.jf.geminjava.R;

import application.GeminiApplication;

/**
 * Created by xdhwwdz20112163.com on 2018/3/19.
 */

public class HeatPopupWindow {

    private View mMainView;
    private PopupWindow mPopupWindow;
    private RadioButton mRadioButtonOk;
    private RadioButton mRadioButtonCancel;
    private Button mButtonOk;
    private TextView mTextView;
    private boolean mHeatFlag;
    private OnHeatListener mOnHeatListener = null;


    public void show(View parent) {

        ViewGroup group = (ViewGroup) mMainView.getParent();
        if (group != null) {
            group.removeAllViews();
        }
        Context context = GeminiApplication.getAppContext();
        mPopupWindow = new PopupWindow(mMainView,
                (int) context.getResources().getDimension(R.dimen.p3970),
                (int) context.getResources().getDimension(R.dimen.p7650),
                true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow.showAtLocation(parent, Gravity.CENTER_HORIZONTAL, 0,
               - (int) context.getResources().getDimension(R.dimen.p4500));
    }

    public void setCount(int count) {
        if (mPopupWindow == null || (!mPopupWindow.isShowing())) {
            return;
        }
        mTextView.setText(String.format("剩余时间: %d秒", count));
    }

    public void setOnHeatListener(OnHeatListener listener) {
        mOnHeatListener = listener;
    }

    private HeatPopupWindow() {
        mMainView = LayoutInflater.from(GeminiApplication.getAppContext()).inflate(R.layout.popup_heat, null);
        mRadioButtonOk = mMainView.findViewById(R.id.id_popup_heat_radio_button_ok);
        mRadioButtonCancel = mMainView.findViewById(R.id.id_popup_heat_radio_button_cancel);
        mButtonOk = mMainView.findViewById(R.id.id_popup_heat_button_ok);
        mTextView = mMainView.findViewById(R.id.id_popup_heat_text_view);
        mRadioButtonCancel.setOnCheckedChangeListener(((buttonView, isChecked) -> mHeatFlag = isChecked));
        mRadioButtonOk.setOnCheckedChangeListener(((buttonView, isChecked) -> mHeatFlag = isChecked));
        mButtonOk.setOnClickListener(v -> {
            if (mOnHeatListener != null) {
                mOnHeatListener.onClick(mHeatFlag);
            }
            mPopupWindow.dismiss();
        });
    }

    public interface OnHeatListener {
        void onClick(boolean flag);
    }

    public static HeatPopupWindow instance() {
        return InlineClass.sInstance;
    }

    private static class InlineClass {
        public static final HeatPopupWindow sInstance = new HeatPopupWindow();
    }
}
