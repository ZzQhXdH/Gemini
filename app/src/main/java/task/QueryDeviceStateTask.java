package task;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import protocol.AbstractProtocol;
import protocol.AbstractStrongProtocol;
import protocol.QueryStatusProtocol;
import util.Logger;

/**
 * Created by xdhwwdz20112163.com on 2018/3/26.
 */

public class QueryDeviceStateTask implements Runnable {

    private int switchCode = 0;

    public static final void start() {

        Log.d("查询状态", "开始查询");
        HandlerTaskManager.instance().getHandler().post(new QueryDeviceStateTask());
    }

    @Override
    public void run() {

        switch (switchCode) {
            case 0x00: AbstractProtocol.vWriteCommand12(); switchCode ++; break;
            case 0x01: QueryStatusProtocol.vQueryTemperature(0); switchCode ++; break;
            case 0x02: QueryStatusProtocol.vQueryTemperature(1); switchCode = 0; break;
        }
        HandlerTaskManager.instance().getHandler().postDelayed(this, 1000);
    }

    public static final void stop() {

        Log.d("查询状态", "停止查询");
        HandlerTaskManager.instance().getHandler().removeMessages(0);
    }

}
