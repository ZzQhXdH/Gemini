package serialport;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android_serialport_api.SerialPort;
import util.Custom;
import util.Logger;

/**
 * Created by xdhwwdz20112163.com on 2018/3/23.
 */

public class RawSerialPort implements ISerialPort {

    private static final String SERIAL_PORT_PATH2 = "/dev/ttyS3";
    private static final String SERIAL_PORT_PATH = "/dev/ttymxc2";

    private SerialPort mSerialPort;
    private OutputStream mOut;
    private InputStream mIn;

    private RawSerialPort(SerialPort serialPort) {
        mSerialPort = serialPort;
        mOut = mSerialPort.getOutputStream();
        mIn = mSerialPort.getInputStream();
    }

    @Override
    public boolean xOpen(int baud_rate, int data_bits, int priority, int stop_bits, int flow_control) {
        return true;
    }

    @Override
    public int xWrite(byte[] bytes) {

        String tmp = Custom.fromByteArrayExt(bytes);
        Logger.instance().file("串口发送" + tmp);
        Logger.instance().d("串口发送", tmp);
        try {
            mOut.write(bytes);
            return bytes.length;
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public int xRead(byte[] bytes) {

        try {
            return mIn.read(bytes);
        } catch (IOException e) {
            return -1;
        }
    }

    @Override
    public boolean isPermission() {
        return true;
    }

    public static final ISerialPort instance() {

        File file = new File(SERIAL_PORT_PATH);
        SerialPort serialPort;

        try {
            serialPort = new SerialPort(file, 9600, 0);
            return new RawSerialPort(serialPort);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
