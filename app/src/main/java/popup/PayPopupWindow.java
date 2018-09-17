package popup;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jf.geminjava.R;

import application.GeminiApplication;

import application.MainCallback;
import application.MainHandler;
import task.HandlerTaskManager;
import task.QueryPayStateTask;
import task.TaskManager;
import util.Http;
import util.PayManager;

/**
 * Created by xdhwwdz20112163.com on 2018/3/19.
 */

public class PayPopupWindow {

    private static final int CALLBACK_QRCODE = 45;

    private View mMainView;
    private PopupWindow mPopupWindow;
    private ImageView mImageViewQrCode;
    private ProgressBar mProgressBar;
    private FrameLayout mFrameLayoutBackground;
    private TextView mTextView;
    private Button mButtonOk;
    private boolean mShowFlag;
    private PayStateListener mStateListener;

    private void showQRCode(final Bitmap bitmap) {

        if (mPopupWindow == null || (!mPopupWindow.isShowing())) {
            return;
        }
        mTextView.setVisibility(View.GONE);
        mImageViewQrCode.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
        mImageViewQrCode.setImageBitmap(bitmap);
    }

    private void showError() {

        if (mPopupWindow == null || (!mPopupWindow.isShowing())) {
            return;
        }
        mTextView.setVisibility(View.VISIBLE);
        mImageViewQrCode.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
    }

    private MainCallback mPayCallback = new MainCallback() {

        @Override
        public void onCall() {

            if (! mShowFlag) {
                return;
            }

            switch (what) {

                case CALLBACK_QRCODE:
                    onCallQrCode(); return;

                case QueryPayStateTask.QUERY_ERROR:
                    mPopupWindow.setOnDismissListener(null);
                    mPopupWindow.dismiss();
                    mShowFlag = false;
                    mStateListener.onQRCodeError(); return; // 支付错误

                case QueryPayStateTask.QUERY_SUCC:
                    mPopupWindow.setOnDismissListener(null);
                    mPopupWindow.dismiss();
                    mShowFlag = false;
                    mStateListener.onPaySuccess(); return; // 支付成功

                case QueryPayStateTask.QUERY_TIME_OUT:
                    mPopupWindow.setOnDismissListener(null);
                    mPopupWindow.dismiss();
                    mShowFlag = false;
                    mStateListener.onPayTimeOut(); return; // 支付超时
            }
        }

        private void onCallQrCode() { // 获取二维码后的回调

            Bitmap bitmap = (Bitmap) obj;
            if (bitmap == null) { // 如果为null则表示获取二维码失败 显示错误
                showError();
                mStateListener.onQRCodeError();
                return;
            }
            showQRCode(bitmap);
            TaskManager.instance().runTask(new QueryPayStateTask(mPayCallback)); // 开始查询支付是否成功
        }
    };

    public void show(View parent, PayStateListener listener) {

        mStateListener = listener;

        QueryPayStateTask.sQueryFlag = true;
        mShowFlag = true;

        int type = PayManager.instance().getPayType();

        if (type == PayManager.PAY_TYPE_ALIPAY) {
            mFrameLayoutBackground.setBackgroundResource(R.drawable.alipay_background);
        } else if (type == PayManager.PAY_TYPE_WECHAT) {
            mFrameLayoutBackground.setBackgroundResource(R.drawable.wechat_background);
        } else {
            return;
        }

        mProgressBar.setVisibility(View.VISIBLE);
        mImageViewQrCode.setVisibility(View.GONE);
        mTextView.setVisibility(View.GONE);

        ViewGroup group = (ViewGroup) mMainView.getParent();
        if (group != null) {
            group.removeAllViews();
        }

        Context context = GeminiApplication.getAppContext();
        mPopupWindow = new PopupWindow(mMainView,
                (int) context.getResources().getDimension(R.dimen.p3760),
                (int) context.getResources().getDimension(R.dimen.p7350),
                true);

        mPopupWindow.setOnDismissListener(() -> {
            QueryPayStateTask.sQueryFlag = false;
            mShowFlag = false;
            mStateListener.onPayCancel();
        });

        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow.setAnimationStyle(R.style.style_pay_popup);
        mPopupWindow.showAtLocation(parent, Gravity.CENTER_HORIZONTAL, 0,
                -(int) context.getResources().getDimension(R.dimen.p4500));

        // 在另外一个线程中获取二维码信息
        TaskManager.instance().runTask(() -> {
            Bitmap bitmap = Http.instance().getQRCodeBitmap();
            Message message = Message.obtain();
            mPayCallback.obj = bitmap;
            mPayCallback.what = CALLBACK_QRCODE;
            message.obj = mPayCallback;
            GeminiApplication.getMainHandler().sendMessage(message);
        });
    }

    private PayPopupWindow() {

        mMainView = LayoutInflater.from(GeminiApplication.getAppContext()).inflate(R.layout.popup_pay, null);
        mFrameLayoutBackground = mMainView.findViewById(R.id.id_popup_pay_frame_layout);
        mImageViewQrCode = mMainView.findViewById(R.id.id_popup_pay_image_view_qrcode);
        mProgressBar = mMainView.findViewById(R.id.id_popup_pay_progress_bar);
        mTextView = mMainView.findViewById(R.id.id_popup_pay_text_view);
        mButtonOk = mMainView.findViewById(R.id.id_popup_pay_button_ok);
        mShowFlag = false;

        mButtonOk.setOnClickListener(v -> {
            mPopupWindow.setOnDismissListener(null);
            mPopupWindow.dismiss();
            QueryPayStateTask.sQueryFlag = false;
            mShowFlag = false;
            mStateListener.onPaySuccess();
        });
    }

    public interface PayStateListener {

        void onPaySuccess();

        void onPayCancel();

        void onPayTimeOut();

        void onPayError();

        void onQRCodeError();
    }

    public static PayPopupWindow instance() {
        return InlineClass.sInstance;
    }

    private static class InlineClass {
        public static final PayPopupWindow sInstance = new PayPopupWindow();
    }
}
