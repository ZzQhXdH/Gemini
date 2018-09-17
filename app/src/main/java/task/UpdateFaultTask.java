package task;

import java.io.IOException;

import protocol.AbstractResult;
import util.DeviceManager;
import util.Http;
import util.Logger;

/**
 * Created by xdhwwdz20112163.com on 2018/3/30.
 */

public class UpdateFaultTask implements Runnable {

    private final AbstractResult.Fault mFault;
    private final static byte[] NormalBytes = new byte[] {
            0x2b, 0x12,
            0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00,
            0x00, 0x00,
            0x0d, 0x0a,
    };

    public UpdateFaultTask() {
        mFault = DeviceManager.instance().getFaultResult();
    }

    @Override
    public void run() {

        if (mFault == null) {
            Logger.instance().d("上传异常数据", "还没有获取异常数据");
            try {
                Http.instance().vUpdateFaultData(new AbstractResult.Fault(NormalBytes));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                Http.instance().vUpdateFaultData(mFault);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        HandlerTaskManager.instance().getDelayHandler().postDelayed(new UpdateFaultTask(), 180 * 1000);
    }
}
