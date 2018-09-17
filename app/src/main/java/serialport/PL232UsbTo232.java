package serialport;

import android.content.Context;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;

import application.GeminiApplication;

/**
 * Created by xdhwwdz20112163.com on 2018/3/23.
 */
public class PL232UsbTo232 implements ISerialPort {

    public static final int PID = 0x2303;
    public static final int VID = 0x067B;
    public static final int INTERFACE_ID = 0;
    public static final int EP_OUT_ADDR = 0x02;
    public static final int EP_IN_ADDR = 0x83;

    public static final int PL2303_RX_MAX_SIZE = 64;
    public static final int PL2303_TX_MAX_SIZE = 64;

    private static final int USB_READ_TIMEOUT_MILLIS = 200;
    private static final int USB_WRITE_TIMEOUT_MILLIS = 5000;

    private static final int USB_RECIP_INTERFACE = 0x01;

    private static final int PROLIFIC_VENDOR_READ_REQUEST = 0x01;
    private static final int PROLIFIC_VENDOR_WRITE_REQUEST = 0x01;

    private static final int PROLIFIC_VENDOR_OUT_REQTYPE = UsbConstants.USB_DIR_OUT
            | UsbConstants.USB_TYPE_VENDOR;

    private static final int PROLIFIC_VENDOR_IN_REQTYPE = UsbConstants.USB_DIR_IN
            | UsbConstants.USB_TYPE_VENDOR;

    private static final int PROLIFIC_CTRL_OUT_REQTYPE = UsbConstants.USB_DIR_OUT
            | UsbConstants.USB_TYPE_CLASS | USB_RECIP_INTERFACE;

    private static final int FLUSH_RX_REQUEST = 0x08;
    private static final int FLUSH_TX_REQUEST = 0x09;

    private static final int SET_LINE_REQUEST = 0x20;
    private static final int SET_CONTROL_REQUEST = 0x22;

    private UsbDevice mUsbDevice;
    private UsbInterface mUsbInterface;
    private UsbEndpoint mEpOut;
    private UsbEndpoint mEpIn;
    private UsbDeviceConnection mConnection;

    @Override
    public boolean xOpen(int baud_rate, int data_bits, int priority, int stop_bits, int flow_control) {

        mUsbInterface = findTargetInterface(mUsbDevice, INTERFACE_ID);
        mEpIn = findTargetEp(mUsbInterface, EP_IN_ADDR);
        mEpOut = findTargetEp(mUsbInterface, EP_OUT_ADDR);
        Context context = GeminiApplication.getAppContext();
        UsbManager manager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        mConnection = manager.openDevice(mUsbDevice);
        mConnection.claimInterface(mUsbInterface, true);
        setControlLines(0);
        resetDevice();
        doBlackMagic();
        setParameters(baud_rate, data_bits, stop_bits, priority);
        return true;
    }

    @Override
    public boolean isPermission() {
        return mConnection != null;
    }

    @Override
    public synchronized int xWrite(byte[] bytes) {
        return mConnection.bulkTransfer(mEpOut, bytes, bytes.length, USB_WRITE_TIMEOUT_MILLIS);
    }

    @Override
    public int xRead(byte[] bytes) {
        return mConnection.bulkTransfer(mEpIn, bytes, PL2303_RX_MAX_SIZE, USB_READ_TIMEOUT_MILLIS);
    }


    public static UsbInterface findTargetInterface(UsbDevice device, int id) {

        int count = device.getInterfaceCount();
        UsbInterface usbInterface;
        for (int i = 0; i < count; i ++) {
            usbInterface = device.getInterface(i);
            if (usbInterface.getId() == id) {
                return usbInterface;
            }
        }
        return null;
    }

    public static UsbEndpoint findTargetEp(UsbInterface usbInterface, int address) {

        UsbEndpoint ep;
        int count = usbInterface.getEndpointCount();
        for (int i = 0; i < count; i ++) {
            ep = usbInterface.getEndpoint(i);
            if (ep.getAddress() == address) {
                return ep;
            }
        }
        return null;
    }

    public static ISerialPort instance(UsbDevice device) {

        int pid = device.getProductId();
        int vid = device.getVendorId();

        if (pid == PID && vid == VID) {
            return new PL232UsbTo232(device);
        }

        return null;
    }

    private PL232UsbTo232(UsbDevice device) {
        mUsbDevice = device;
    }

    public void setParameters(int baudRate, int dataBits, int stopBits, int parity) {

        byte[] lineRequestData = new byte[7];

        lineRequestData[0] = (byte) (baudRate & 0xff);
        lineRequestData[1] = (byte) ((baudRate >> 8) & 0xff);
        lineRequestData[2] = (byte) ((baudRate >> 16) & 0xff);
        lineRequestData[3] = (byte) ((baudRate >> 24) & 0xff);

        switch (stopBits) {
            case 1:
                lineRequestData[4] = 0;
                break;

            case 3:
                lineRequestData[4] = 1;
                break;

            case 2:
                lineRequestData[4] = 2;
                break;

            default:
                lineRequestData[4] = 0;
                break;
        }

        switch (parity) {
            case 0:
                lineRequestData[5] = 0;
                break;

            case 1:
                lineRequestData[5] = 1;
                break;

            case 2:
                lineRequestData[5] = 2;
                break;

            default:
                lineRequestData[5] = 0;
                break;
        }

        lineRequestData[6] = (byte) dataBits;

        ctrlOut(SET_LINE_REQUEST, 0, 0, lineRequestData);

        resetDevice();
    }

    private void setControlLines(int newControlLinesValue) {

        ctrlOut(SET_CONTROL_REQUEST, newControlLinesValue, 0, null);
    }

    private void doBlackMagic() {

        vendorIn(0x8484, 0, 1);
        vendorOut(0x0404, 0, null);
        vendorIn(0x8484, 0, 1);
        vendorIn(0x8383, 0, 1);
        vendorIn(0x8484, 0, 1);
        vendorOut(0x0404, 1, null);
        vendorIn(0x8484, 0, 1);
        vendorIn(0x8383, 0, 1);
        vendorOut(0, 1, null);
        vendorOut(1, 0, null);
        vendorOut(2, 0x44, null);
    }

    private void resetDevice() {
        purgeHwBuffers(true, true);
    }

    private boolean purgeHwBuffers(boolean purgeReadBuffers, boolean purgeWriteBuffers) {

        if (purgeReadBuffers) {
            vendorOut(FLUSH_RX_REQUEST, 0, null);
        }
        if (purgeWriteBuffers) {
            vendorOut(FLUSH_TX_REQUEST, 0, null);
        }
        return true;
    }

    private byte[] inControlTransfer(int requestType, int request,
                                     int value, int index, int length) {
        byte[] buffer = new byte[length];
        int result = mConnection.controlTransfer(requestType, request, value,
                index, buffer, length, USB_READ_TIMEOUT_MILLIS);

        return buffer;
    }

    private final void outControlTransfer(int requestType, int request,
                                          int value, int index, byte[] data) {
        int length = (data == null) ? 0 : data.length;
        int result = mConnection.controlTransfer(requestType, request, value,
                index, data, length, USB_WRITE_TIMEOUT_MILLIS);
    }

    private final byte[] vendorIn(int value, int index, int length) {
        return inControlTransfer(PROLIFIC_VENDOR_IN_REQTYPE,
                PROLIFIC_VENDOR_READ_REQUEST, value, index, length);
    }

    private final void vendorOut(int value, int index, byte[] data) {
        outControlTransfer(PROLIFIC_VENDOR_OUT_REQTYPE,
                PROLIFIC_VENDOR_WRITE_REQUEST, value, index, data);
    }

    private final void ctrlOut(int request, int value, int index, byte[] data) {
        outControlTransfer(PROLIFIC_CTRL_OUT_REQTYPE, request, value, index,
                data);
    }

}


