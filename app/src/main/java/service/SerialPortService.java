package service;

import android.app.IntentService;
import android.bluetooth.BluetoothClass;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;


import protocol.AbstractProtocol;
import protocol.AbstractResult;

import serialport.ISerialPort;
import task.HandlerTaskManager;
import task.InitDeviceTask;
import task.QueryDeviceStateTask;
import task.UpdateFaultTask;
import util.Custom;

import util.DeviceManager;
import util.GoodsTypeManager;
import util.Logger;
import util.SerialPortManager;

/**
 * Created by xdhwwdz20112163.com on 2018/3/20.
 */

public class SerialPortService extends IntentService {

    public static final String RECV = "serialport.recv";

    private static final String TAG = SerialPortService.class.getSimpleName();

    private static boolean mRunFlag = false;
    private byte[] mSerialPortReceiverData = new byte[64];
    private int mSerialPortReceiverLength = 0;
    private LocalBroadcastManager mLocalBroadcastManager;

    public static void start(Context context) {
        mRunFlag = true;
        Intent intent = new Intent(context, SerialPortService.class);
        context.startService(intent);
    }

    public static void stop() {
        mRunFlag = false;
    }

    public SerialPortService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        ISerialPort serialPort = SerialPortManager.instance().getSerialPort();

        if (serialPort == null || (!serialPort.isPermission())) {
            return;
        }
        QueryDeviceStateTask.start(); // 状态查询启动
        Logger.instance().d(TAG, "串口打开成功");

        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        int len;
        byte[] bytes = new byte[64];

        while (mRunFlag) {
            len = serialPort.xRead(bytes);
            if (len <= 0) {
                continue;
            }
            for (int i = 0; i < len; i ++) {
                onParse(bytes[i]);
            }
        }
    }

    private void onRecvBytes(byte[] bytes, int len) {

        byte[] buffer = new byte[len];
        System.arraycopy(bytes, 0, buffer, 0, len);
        Logger.instance().d("recv", Custom.fromByteArrayExt(buffer));
    }

    private boolean isEnd() {

        if (mSerialPortReceiverLength < 5) {
            return false;
        }

        if (mSerialPortReceiverData[0] == 0x02 && mSerialPortReceiverData[1] == (mSerialPortReceiverLength - 1)) {
            return true;
        }

        byte d1 = mSerialPortReceiverData[mSerialPortReceiverLength-2];
        byte d2 = mSerialPortReceiverData[mSerialPortReceiverLength-1];
        byte h = mSerialPortReceiverData[1];
        if ((d1 == 0x0D) && (d2 == 0x0A) && (mSerialPortReceiverLength == (int) h)) {
            return true;
        }
        return false;
    }

    private void onParse(byte d) {

        if (mSerialPortReceiverLength == 0 && (d == 0x2B || d == 0x1C || d == 0x02)) {
            mSerialPortReceiverData[mSerialPortReceiverLength] = d;
            mSerialPortReceiverLength ++;
            return;
        }

        if (mSerialPortReceiverLength == 0) {
            return;
        }

        mSerialPortReceiverData[mSerialPortReceiverLength] = d;
        mSerialPortReceiverLength ++;

        if (isEnd()) {
            onRecvEnd();
            mSerialPortReceiverLength = 0;
            return;
        }

        if (mSerialPortReceiverLength > 0x1B) { // 接收的数据最长也就0x1B
            mSerialPortReceiverLength = 0;
            return;
        }
    }

    private void onRecvEnd() {

        byte[] bytes = new byte[mSerialPortReceiverLength];
        System.arraycopy(mSerialPortReceiverData, 0, bytes, 0, mSerialPortReceiverLength);

        String tmp = Custom.fromByteArrayExt(bytes);
        Logger.instance().file(tmp);
        Logger.instance().d(TAG, tmp);

        AbstractResult result = AbstractResult.parse(bytes);

        if (result == null) {
            Logger.instance().d("接收解析", "解析失败");
            return;
        }

        final String type = result.getType();
        vUpdateBroadcast(type, bytes);

        switch (type) {
            case AbstractResult.TYPE_FAULT:
                AbstractResult.Fault fault = (AbstractResult.Fault) result;
                onFault(fault);
                break;
            case AbstractResult.TYPE_DOOR_STATUS:
                DeviceManager.instance().setDoorResult((AbstractResult.DoorStatus) result);
                break;
            case AbstractResult.TYPE_ACK:
                DeviceManager.instance().setFaultResult(null);
                break;
        }
    }

    private void onFault(AbstractResult.Fault fault) {

        DeviceManager.instance().setFaultResult(fault);
        HandlerTaskManager.instance().getHandler().post(new UpdateFaultTask());
        if (fault.isFault9()) {
            AbstractProtocol.vWriteCommand6();
        }
    }

    private void vUpdateBroadcast(String action, byte[] bytes) {

        Intent intent = new Intent(action);
        intent.putExtra(RECV, bytes);
        mLocalBroadcastManager.sendBroadcast(intent);
    }

}
