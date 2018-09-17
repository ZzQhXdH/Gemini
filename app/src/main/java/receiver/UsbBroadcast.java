package receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbManager;

import service.SerialPortService;
import util.Logger;
import util.SerialPortManager;

/**
 * Created by xdhwwdz20112163.com on 2018/3/24.
 */

public class UsbBroadcast extends BroadcastReceiver {

    private static final String TAG = UsbBroadcast.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean ok = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false);
        Logger.instance().d(TAG, "Usb:" + ok);
        if (ok) {
            SerialPortManager.instance().getSerialPort().xOpen(9600, 8, 0, 1, 0);
            Logger.instance().d(TAG, "获取到了Usb权限");
            SerialPortService.start(context);
        }
    }
}
