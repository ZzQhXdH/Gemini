package popup;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jf.geminjava.R;

import activity.HomeActivity;
import android_serialport_api.SerialPort;
import application.GeminiApplication;
import protocol.AbstractProtocol;
import protocol.AbstractResult;
import service.SerialPortService;
import task.GetBarcodeTask;
import task.HandlerTaskManager;
import task.InitDeviceTask;
import task.QueryDeviceStateTask;
import task.UpdateBarcodeTask;
import util.GoodsTypeManager;
import util.Http;
import util.Logger;
import view.WaitView;

/**
 * Created by xdhwwdz20112163.com on 2018/3/29.
 */

public class InitializePopupWindow {

    private static final String TAG = InitializePopupWindow.class.getSimpleName();

    private int mInitSteps;
    private View mMainView;
    private PopupWindow mPopupWindow;
    private WaitView mWaitView;
    private TextView mTextView;
    private int mWidth;
    private int mHeight;

    private void register() {

        Context context = GeminiApplication.getAppContext();
        IntentFilter filter = new IntentFilter();
        filter.addAction(AbstractResult.TYPE_ACK);
        filter.addAction(AbstractResult.TYPE_INIT_STATUS);
        filter.addAction(GetBarcodeTask.BAR_CODE_FINISH);
        filter.addAction(AbstractResult.TYPE_BAR_CODE);
        filter.addAction(HomeActivity.UPDATE_DATA);
        LocalBroadcastManager.getInstance(context).registerReceiver(mBroadcastReceiver, filter);
        mInitSteps = 0;
        InitDeviceTask.start(); // 发送初始化命令
    }

    private void unregister() {

        Context context = GeminiApplication.getAppContext();
        LocalBroadcastManager.getInstance(context).unregisterReceiver(mBroadcastReceiver);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            final String action = intent.getAction();

            switch (action) {

                case AbstractResult.TYPE_BAR_CODE:
                    onBarcode(intent);
                    break;

                case AbstractResult.TYPE_ACK:
                    onAck();
                    break;

                case GetBarcodeTask.BAR_CODE_FINISH:
                    onBarcodeFinish();
                    break;

                case AbstractResult.TYPE_INIT_STATUS:
                    final byte[] bytes = intent.getByteArrayExtra(SerialPortService.RECV);
                    onStatus(bytes);
                    break;

                case HomeActivity.UPDATE_DATA:
                    if (mPopupWindow != null) {
                        mPopupWindow.dismiss();
                    }
                    break;
            }
        }

        private void onBarcode(Intent intent) {

            byte[] bytes = intent.getByteArrayExtra(SerialPortService.RECV);
            AbstractResult.BarCode result = new AbstractResult.BarCode(bytes);
            GoodsTypeManager.instance().addBarcode(result.toGoodsType());
        }

        private void onBarcodeFinish() {
            // 条码读取已经完成
            mTextView.setText("条码读取完成");
            Logger.instance().d(TAG, "条码读取已经完成");
            QueryDeviceStateTask.start(); // 开始读取机器状态
        }

        private void onAck() {

            switch (mInitSteps) {

                case 0: // 表示机器开始初始化操作
                    InitDeviceTask.stop();
                    mInitSteps++;
                    QueryDeviceStateTask.start(); // 查询机器状态
                    Logger.instance().d(TAG, "开始初始化");
                    break;

                case 1: // 表示初始化结束
                    QueryDeviceStateTask.stop(); // 停止查询状态
                    mInitSteps++; // 开始读取条码
                    GetBarcodeTask.start();
                    mTextView.setText("正在自检");
                    Logger.instance().d(TAG, "初始化结束, 开始读取条码");
                    break;

                case 2: //
                    QueryDeviceStateTask.stop();
                    mInitSteps++;
                    mTextView.setText("准备开始售卖");
                   // unregister();
                   // mPopupWindow.dismiss();
                    Logger.instance().d(TAG, "初始化命令结束, 可以开始售卖");
             //       QueryDeviceStateTask.start(); // 启动状态查询

                    HandlerTaskManager.instance().getHandler().post(new UpdateBarcodeTask());

                    break;
            }
        }

        private void onStatus(final byte[] bytes) {

            if (mInitSteps > 1) {
                return;
            }
            AbstractResult.InitStatus result = new AbstractResult.InitStatus(bytes);
            mTextView.setText(result.getStatus());
        }
    };

    public void show(View parent) {

    //    QueryDeviceStateTask.stop(); // 停止状态查询

        ViewGroup group = (ViewGroup) mMainView.getParent();
        if (group != null) {
            group.removeAllViews();
        }

        mTextView.setText("正在初始化");
        mPopupWindow = new PopupWindow(mMainView, mWidth, mHeight, true);
        mPopupWindow.setOutsideTouchable(false);
        mPopupWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
        mPopupWindow.setOnDismissListener(() -> unregister());
        register();
    }

    private InitializePopupWindow() {

        Context context = GeminiApplication.getAppContext();
        mWidth = (int) context.getResources().getDimension(R.dimen.p10800);
        mHeight = (int) context.getResources().getDimension(R.dimen.p19200);
        mMainView = LayoutInflater.from(context).inflate(R.layout.popup_initialize, null);
        mWaitView = mMainView.findViewById(R.id.id_popup_initialize_wait_view);
        mTextView = mMainView.findViewById(R.id.id_popup_initialize_text_view);
    }

    public static InitializePopupWindow instance() {
        return InlineClass.sInstance;
    }

    private static final class InlineClass {
        public static final InitializePopupWindow sInstance = new InitializePopupWindow();
    }
}
