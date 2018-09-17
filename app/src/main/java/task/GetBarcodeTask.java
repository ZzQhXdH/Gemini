package task;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import android_serialport_api.SerialPort;
import application.GeminiApplication;
import protocol.AbstractProtocol;
import protocol.AbstractResult;
import receiver.InitializeReceiver;
import service.SerialPortService;
import util.Custom;
import util.Logger;

/**
 * Created by xdhwwdz20112163.com on 2018/3/26.
 */

public class GetBarcodeTask implements Runnable {

    public static final String BAR_CODE_FINISH = "get.bar.code.finish";

    private byte mCol = 1;
    private byte mRow = 1;

    public static final void start() {
        HandlerTaskManager.instance().getHandler().post(new GetBarcodeTask());
    }

    @Override
    public void run() {

        if (mCol > 4) {

            Logger.instance().d("GetBarcodeTask", "获取条码任务完成");

            Intent intent = new Intent(BAR_CODE_FINISH);

            LocalBroadcastManager.getInstance(GeminiApplication.getAppContext()).sendBroadcast(intent);

            return;
        }

        AbstractProtocol.vWriteCommand11(mCol, mRow);

        mRow ++;

        if (mRow > 22) {
            mRow = 1;
            mCol ++;
        }

        HandlerTaskManager.instance().getHandler().postDelayed(this, 300);
    }


}
