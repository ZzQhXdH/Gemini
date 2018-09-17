package task;

import protocol.AbstractProtocol;
import protocol.AbstractResult;

/**
 * Created by xdhwwdz20112163.com on 2018/3/26.
 */

public class InitDeviceTask implements Runnable {

    public static void start() {
        HandlerTaskManager.instance().getHandler().post(new InitDeviceTask());
    }

    public static void stop() {
        HandlerTaskManager.instance().getHandler().removeMessages(0);
    }

    @Override
    public void run() {

        AbstractProtocol.vWriteCommand14();
        HandlerTaskManager.instance().getHandler().postDelayed(this, 1000);
    }



}
