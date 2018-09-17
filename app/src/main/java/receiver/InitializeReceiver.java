package receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import application.GeminiApplication;
import popup.BootInitPopupWindow;
import protocol.AbstractProtocol;
import protocol.AbstractResult;
import service.SerialPortService;

import task.GetBarcodeTask;
import task.InitDeviceTask;
import task.QueryDeviceStateTask;
import util.DeviceManager;
import util.GoodsTypeManager;
import util.Logger;

/**
 * Created by xdhwwdz20112163.com on 2018/3/26.
 */

public class InitializeReceiver extends BroadcastReceiver {

    private static final String TAG = InitializeReceiver.class.getSimpleName();

    public static final String BAR_CODE_FINISH = "broadcast.bar.code.finish";

    public static void registerReceiver() {

        Context context = GeminiApplication.getAppContext();
        IntentFilter filter = new IntentFilter();
        filter.addAction(AbstractResult.TYPE_ACK);
        filter.addAction(AbstractResult.TYPE_BAR_CODE);
        filter.addAction(BAR_CODE_FINISH);
        LocalBroadcastManager.getInstance(context).registerReceiver(InlineClass.sInstance, filter);

        GoodsTypeManager.instance().clear();
        InitDeviceTask.start();
    }

    public static void unregisterReceiver() {

        Context context = GeminiApplication.getAppContext();
        LocalBroadcastManager.getInstance(context).unregisterReceiver(InlineClass.sInstance);
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        final String action = intent.getAction();

        switch (action) {

            case AbstractResult.TYPE_ACK:
                onAck();
                break;

            case BAR_CODE_FINISH:
                onBarcodeFinish();
                break;

            case AbstractResult.TYPE_BAR_CODE:
                onBarCode(intent);
                break;
        }
    }

    private void onBarCode(Intent intent) { // 获取到一条条码信息

        byte[] bytes = intent.getByteArrayExtra(SerialPortService.RECV);
        AbstractResult.BarCode barCode = new AbstractResult.BarCode(bytes);
        GoodsTypeManager.instance().addGoodsType(barCode.toGoodsType());
    }

    private void onBarcodeFinish() { // 条码扫描完成

        DeviceManager.sState ++;
        unregisterReceiver();
        Logger.instance().d(TAG, "开始售卖");
        BootInitPopupWindow.instance().dismiss();
        GoodsTypeManager.instance().print();
    }

    private void onAck() {

        switch (DeviceManager.sState) {

            case DeviceManager.BOOT:
                InitDeviceTask.stop();
                QueryDeviceStateTask.start();
                DeviceManager.sState ++;
                Logger.instance().d(TAG, "开始初始化");
                break;

            case DeviceManager.INIT:
                Logger.instance().d(TAG, "初始化成功");
                DeviceManager.sState ++;
                QueryDeviceStateTask.stop();
                GetBarcodeTask.start();
                break;
        }
    }

    private static final class InlineClass {
        public static final InitializeReceiver sInstance = new InitializeReceiver();
    }
}
