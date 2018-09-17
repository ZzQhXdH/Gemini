package popup;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.jf.geminjava.R;

import application.GeminiApplication;
import data.Wares;
import util.PayManager;
import util.WaresManager;

/**
 * Created by xdhwwdz20112163.com on 2018/3/16.
 */

public class OrderPopupWindow {

    private View mPopupView = null;
    private PopupWindow mPopupWindow = null;
    private ImageView mImageView = null;
    private TextView mTextViewDescribe = null;
    private ImageView[] mImageViews = new ImageView[5];
    private TextView mTextViewName = null;
    private TextView mTextViewPrice = null;
    private ImageButton mImageButtonWechat = null;
    private ImageButton mImageButtonAlipay = null;
    private ProgressBar mProgressBar = null;

    private void setUi() {

        Wares wares = WaresManager.instance().getSelectWares();

        mProgressBar.setVisibility(View.VISIBLE);
        mImageView.setVisibility(View.GONE);

        Glide.with(mPopupView.getContext())
                .load(wares.getMaxImageUrl())
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(true)
                .into(new SimpleTarget<Bitmap>(456, 407) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {

                        mProgressBar.setVisibility(View.GONE);
                        mImageView.setVisibility(View.VISIBLE);
                        mImageView.setImageBitmap(resource);
                    }
                });

        //mImageView.setImageResource(wares.getMaxImageId());
        setStarValue(wares.getStarValue());
        mTextViewPrice.setText(String.format("¥%s", wares.getPrice()));
        mTextViewName.setText(wares.getName());
        mTextViewDescribe.setText(wares.getDescription());
    }

    public void show(View parent, OnPayListener listener) {

        setUi();
        ViewGroup group = (ViewGroup) mPopupView.getParent();
        if (group != null) {
            group.removeAllViews();
        }
        Context context = GeminiApplication.getAppContext();
        mPopupWindow = new PopupWindow(mPopupView,
                (int) context.getResources().getDimension(R.dimen.p5800),
                (int) context.getResources().getDimension(R.dimen.p8160), true);
        mPopupWindow.setOnDismissListener(() -> listener.onPayCanael());
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow.setAnimationStyle(R.style.style_order_popup);
        mPopupWindow.showAtLocation(parent, Gravity.CENTER_HORIZONTAL, 0,
                - (int) context.getResources().getDimension(R.dimen.p4500));

        mImageButtonAlipay.setOnClickListener(v -> {
            PayManager.instance().setPayType(PayManager.PAY_TYPE_ALIPAY);
            mPopupWindow.setOnDismissListener(null);
            mPopupWindow.dismiss();
            listener.onAlipay();
        });

        mImageButtonWechat.setOnClickListener(v -> {
            PayManager.instance().setPayType(PayManager.PAY_TYPE_WECHAT);
            mPopupWindow.setOnDismissListener(null);
            mPopupWindow.dismiss();
            listener.onWechat();
        });
    }

    private void setStarValue(int value) {

        for (int i = 0; i < 5; i ++) {
            if (i < value) {
                mImageViews[i].setVisibility(View.VISIBLE);
            } else {
                mImageViews[i].setVisibility(View.INVISIBLE);
            }
        }
    }

    private OrderPopupWindow() {

        mPopupView = LayoutInflater.from(GeminiApplication.getAppContext()).inflate(R.layout.popup_order, null);
        mImageView = mPopupView.findViewById(R.id.id_popup_order_image_view);
        mTextViewDescribe = mPopupView.findViewById(R.id.id_popup_order_text_view_describe);
        mImageViews[0] = mPopupView.findViewById(R.id.id_popup_order_image_view_star_1);
        mImageViews[1] = mPopupView.findViewById(R.id.id_popup_order_image_view_star_2);
        mImageViews[2] = mPopupView.findViewById(R.id.id_popup_order_image_view_star_3);
        mImageViews[3] = mPopupView.findViewById(R.id.id_popup_order_image_view_star_4);
        mImageViews[4] = mPopupView.findViewById(R.id.id_popup_order_image_view_star_5);
        mTextViewName = mPopupView.findViewById(R.id.id_popup_order_text_view_name);
        mTextViewPrice = mPopupView.findViewById(R.id.id_popup_order_text_view_price);
        mImageButtonWechat = mPopupView.findViewById(R.id.id_popup_order_image_button_wechat);
        mImageButtonAlipay = mPopupView.findViewById(R.id.id_popup_order_image_button_alipay);
        mProgressBar = mPopupView.findViewById(R.id.id_popup_order_progress_bar);
    }

    public interface OnPayListener {

        void onPayCanael();

        void onAlipay();

        void onWechat();
    }

    public static OrderPopupWindow instance() {
        return InlineClass.sInstance;
    }

    private static class InlineClass {
        public static OrderPopupWindow sInstance = new OrderPopupWindow();
    }
}
