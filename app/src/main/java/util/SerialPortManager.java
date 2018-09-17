package util;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

import android_serialport_api.SerialPort;
import application.GeminiApplication;
import serialport.ISerialPort;
import serialport.PL232UsbTo232;
import serialport.RawSerialPort;
import service.SerialPortService;
import task.HandlerTaskManager;

/**
 * Created by xdhwwdz20112163.com on 2018/3/20.
 */

public class SerialPortManager {

    private static final String TAG = SerialPortManager.class.getSimpleName();
    private static final String USB_PERMISSION = "usb.permission.uart";

    private ISerialPort mSerialPort;

    public ISerialPort getSerialPort() {
        return mSerialPort;
    }

    private SerialPortManager() {

        Context context = GeminiApplication.getAppContext();
        UsbManager manager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        Iterator<UsbDevice> iterator = manager.getDeviceList().values().iterator();
        UsbDevice device = null;
        while (iterator.hasNext()) {

            device = iterator.next();
            mSerialPort = PL232UsbTo232.instance(device);

            if (mSerialPort != null) {
                break;
            }
        }

        if (mSerialPort != null) { // PL2302已经插入

            boolean result = manager.hasPermission(device);
            if (!result) { // 如果没有获取权限
                Intent intent = new Intent(USB_PERMISSION);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
                manager.requestPermission(device, pendingIntent);
                Logger.instance().d(TAG, "还没有获取Usb权限");
                return;
            }
            Logger.instance().d(TAG, "已经获取Usb权限");
            mSerialPort.xOpen(9600, 8, 0, 1, 0);
            return;
        }

        // PL2302不存在 使用原生的串口
        mSerialPort = RawSerialPort.instance();
        if (mSerialPort == null) {
            mSerialPort = new Port();
            Logger.instance().d(TAG, "原生串口不存在");
            return;
        }
        Logger.instance().d(TAG, "使用原生串口");
        return;
    }

    public static final SerialPortManager instance() {
        return InlineClass.sInstance;
    }

    private static final class InlineClass {
        public static final SerialPortManager sInstance = new SerialPortManager();
    }

    private static final class Port implements ISerialPort {

        @Override
        public boolean xOpen(int baud_rate, int data_bits, int priority, int stop_bits, int flow_control) {
            return false;
        }

        @Override
        public int xWrite(byte[] bytes) {
            return 0;
        }

        @Override
        public int xRead(byte[] bytes) {
            return 0;
        }

        @Override
        public boolean isPermission() {
            return false;
        }
    }

}
