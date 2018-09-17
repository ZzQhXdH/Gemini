package popup;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jf.geminjava.R;

import application.GeminiApplication;

/**
 * Created by xdhwwdz20112163.com on 2018/3/23.
 */

public class TakeFoodPopupWindow {

    private View mMainView;
    private PopupWindow mPopupWindow;
    private final Button[] mButtons = new Button[10];
    private Button mButtonClear;
    private TextView mTextViewInput;

    public void show(View parent, OnTakeFoodListener listener) {

        ViewGroup group = (ViewGroup) mMainView.getParent();
        if (group != null) {
            group.removeAllViews();
        }
        Context context = GeminiApplication.getAppContext();
        mTextViewInput.setText("");
        mPopupWindow = new PopupWindow(mMainView,
                (int) context.getResources().getDimension(R.dimen.p5760),
                (int) context.getResources().getDimension(R.dimen.p7320),
                true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow.setAnimationStyle(R.style.style_take_popup);
        mPopupWindow.showAtLocation(parent, Gravity.CENTER, 0,
                - (int) context.getResources().getDimension(R.dimen.p4500));
    }

    private TakeFoodPopupWindow() {
        Context context = GeminiApplication.getAppContext();
        mMainView = LayoutInflater.from(context).inflate(R.layout.popup_take_food, null);
        mButtons[0] = mMainView.findViewById(R.id.id_popup_take_btn_0);
        mButtons[1] = mMainView.findViewById(R.id.id_popup_take_btn_1);
        mButtons[2] = mMainView.findViewById(R.id.id_popup_take_btn_2);
        mButtons[3] = mMainView.findViewById(R.id.id_popup_take_btn_3);
        mButtons[4] = mMainView.findViewById(R.id.id_popup_take_btn_4);
        mButtons[5] = mMainView.findViewById(R.id.id_popup_take_btn_5);
        mButtons[6] = mMainView.findViewById(R.id.id_popup_take_btn_6);
        mButtons[7] = mMainView.findViewById(R.id.id_popup_take_btn_7);
        mButtons[8] = mMainView.findViewById(R.id.id_popup_take_btn_8);
        mButtons[9] = mMainView.findViewById(R.id.id_popup_take_btn_9);
        mButtonClear = mMainView.findViewById(R.id.id_popup_take_btn_clear);
        mTextViewInput = mMainView.findViewById(R.id.id_popup_take_text_view_input);

        for (int i = 0; i < 10; i ++) {

            mButtons[i].setOnClickListener(v -> {
                String s = ((Button) v).getText().toString();
                mTextViewInput.append(s);
            });
        }

        mButtonClear.setOnClickListener(v -> mTextViewInput.setText(""));
    }

    public interface OnTakeFoodListener {

        void onInputError();

        void onInputSuccess();
    }

    public static TakeFoodPopupWindow instance() {
        return InlineClass.sInstance;
    }

    private static final class InlineClass {
        public static final TakeFoodPopupWindow sInstance = new TakeFoodPopupWindow();
    }
}
