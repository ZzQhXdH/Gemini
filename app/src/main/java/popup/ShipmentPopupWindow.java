package popup;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jf.geminjava.R;

import android_serialport_api.SerialPort;
import application.GeminiApplication;
import data.Wares;
import protocol.AbstractProtocol;
import protocol.AbstractResult;
import service.SerialPortService;
import task.HandlerTaskManager;
import task.QueryDeviceStateTask;
import task.ReportWaresShipment;
import util.Custom;
import util.GoodsTypeTest;
import util.Logger;
import util.WaresManager;
import view.CountDownView;

/**
 * Created by xdhwwdz20112163.com on 2018/3/22.
 */

public class ShipmentPopupWindow implements CountDownView.OnTimeOutListener {

    private static final String TAG = ShipmentPopupWindow.class.getSimpleName();

    private View mMainView;
    private PopupWindow mPopupWindow;
    private CountDownView mCountDownView;
    private TextView mTextView;
    private ImageView mImageView;
    private ImageView mImageViewState;
    private OnShipmentListener mShipmentListener;
    private boolean mShipmentFlag = false;

    private void onAck() {

        if (mShipmentFlag) { // 如果出货成功了
            onShipmentSuccess();
            mShipmentFlag = false;
        }
    }

    private void onNck() {

        onShipmentError();
    }

    private void onOtherStatus() { // Busy and 指定货道无货

        onShipmentError();
    }

    private void onShipmentStatus(byte[] bytes) {

        AbstractResult.ShipmentStatus result = new AbstractResult.ShipmentStatus(bytes);
        int code = result.getStatusCode();
        Logger.instance().d(TAG, result.getStatus());
        switch (code) {
            case 0x10: mShipmentFlag = true; break; // 出货完成
            case 0x04: mTextView.setText("正在微波加热"); break;
        }
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            final String action = intent.getAction();
            final byte[] bytes = intent.getByteArrayExtra(SerialPortService.RECV);

            switch (action) {

                case AbstractResult.TYPE_ACK: onAck();
                    break;

                case AbstractResult.TYPE_SHIPMENT_STATUS: onShipmentStatus(bytes);
                    break;

                case AbstractResult.TYPE_OTHER_STATUS: onOtherStatus();
                    break;

                case AbstractResult.TYPE_NCK: onNck();
                    break;
            }
        }
    };

    private void registerBroadcast() {

        IntentFilter filter = new IntentFilter();
        filter.addAction(AbstractResult.TYPE_ACK);
        filter.addAction(AbstractResult.TYPE_OTHER_STATUS);
        filter.addAction(AbstractResult.TYPE_SHIPMENT_STATUS);
        filter.addAction(AbstractResult.TYPE_NCK);
        LocalBroadcastManager.getInstance(GeminiApplication.getAppContext()).registerReceiver(mBroadcastReceiver, filter);

   //     GeminiApplication.getMainHandler().postDelayed(() -> QueryDeviceStateTask.start(), 500);
    }

    private void unregisterBroadcast() {

     //   GeminiApplication.getMainHandler().postDelayed(() -> QueryDeviceStateTask.stop(), 500);
        Logger.instance().d("出货", "停止查询状态");
        LocalBroadcastManager.getInstance(GeminiApplication.getAppContext()).unregisterReceiver(mBroadcastReceiver);
    }

    private void onShipmentSuccess() {

        unregisterBroadcast();
        GeminiApplication.getMainHandler().postDelayed(() -> {
            mPopupWindow.dismiss();
            mShipmentListener.onShipmentSuccess();
        }, 1000);

        mCountDownView.setOnTimeOutListener(null);
        mCountDownView.cancel();
        mCountDownView.setVisibility(View.GONE);
        mImageViewState.setVisibility(View.VISIBLE);
        mImageViewState.setImageResource(R.drawable.delivery_succ);
        mTextView.setText("出货成功,谢谢惠顾!");
      //
        final String goodsType = WaresManager.instance().getSelectWares().getGoodsType();
        WaresManager.instance().getSelectWares().subCounter(); // 扣掉一个库存
        HandlerTaskManager.instance().getHandler().post(new ReportWaresShipment(goodsType));

     //   QueryDeviceStateTask.start();
    }

    private void onShipmentError() {

        unregisterBroadcast();
        GeminiApplication.getMainHandler().postDelayed(() -> {
            mPopupWindow.dismiss();
            mShipmentListener.onShipmentError();
        }, 1000);

        mCountDownView.setOnTimeOutListener(null);
        mCountDownView.cancel();
        mCountDownView.setVisibility(View.GONE);
        mImageViewState.setVisibility(View.VISIBLE);
        mImageViewState.setImageResource(R.drawable.delivery_error);
        mTextView.setText("抱歉,出货失败!\r\n支付将于2小时内返还");

    //    QueryDeviceStateTask.start();
    }

    private void initUi() {

        Wares wares = WaresManager.instance().getSelectWares();

        Glide.with(mCountDownView.getContext()).load(wares.getMaxImageUrl()).into(mImageView);

      //  mImageView.setImageResource(wares.getMaxImageId());

        mCountDownView.setVisibility(View.VISIBLE);
        mImageViewState.setVisibility(View.GONE);
        mTextView.setText("正在出货请稍后 ......");
        mCountDownView.setOnTimeOutListener(this);
        mCountDownView.setMaxCount(150);

       // byte[] bytes = GoodsTypeTest.instance().getGoodsType();
        byte[] bytes = Custom.getGoodsType(wares.getGoodsType());
        byte heat = wares.getHeatTime();
        AbstractProtocol.vWriteCommand15(bytes[0], bytes[1], heat);
    }

    public void show(View parent, OnShipmentListener listener) {

    //    QueryDeviceStateTask.stop();

        ViewGroup group = (ViewGroup) mMainView.getParent();
        if (group != null) {
            group.removeAllViews();
        }

        Context context = GeminiApplication.getAppContext();

        mPopupWindow = new PopupWindow(mMainView,
                (int) context.getResources().getDimension(R.dimen.p5800),
                (int) context.getResources().getDimension(R.dimen.p9000),
                true);
        mPopupWindow.setOutsideTouchable(false);
        mPopupWindow.setAnimationStyle(R.style.style_shipment_popup);
        mPopupWindow.showAtLocation(parent, Gravity.CENTER, 0,
                - (int) context.getResources().getDimension(R.dimen.p4500));

        initUi();

        mShipmentListener = listener;
        mShipmentFlag = false;

        registerBroadcast();
    }

    private ShipmentPopupWindow() {

        Context context = GeminiApplication.getAppContext();
        mMainView = LayoutInflater.from(context).inflate(R.layout.popup_shipment, null);
        mImageView = mMainView.findViewById(R.id.id_popup_shipment_image_view);
        mImageViewState = mMainView.findViewById(R.id.id_popup_shipment_image_view_state);
        mTextView = mMainView.findViewById(R.id.id_popup_shipment_text_view);
        mCountDownView = mMainView.findViewById(R.id.id_popup_shipment_count_down);
    }

    @Override
    public void onTimeOut() {

        GeminiApplication.getMainHandler().postDelayed(() -> {
            mPopupWindow.dismiss();
            mShipmentListener.onShipmentTimeOut();
        }, 1000);
    }

    public interface OnShipmentListener {

        void onShipmentError();

        void onShipmentSuccess();

        void onShipmentTimeOut();
    }

    public static ShipmentPopupWindow instance() {
        return InlineClass.sInstance;
    }

    private static final class InlineClass {
        public static ShipmentPopupWindow sInstance = new ShipmentPopupWindow();
    }
}
