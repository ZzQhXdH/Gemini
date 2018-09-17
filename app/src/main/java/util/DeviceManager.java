package util;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import protocol.AbstractResult;

/**
 * Created by xdhwwdz20112163.com on 2018/3/19.
 */

public class DeviceManager {

    public static final int BOOT = 300; // 开机阶段
    public static final int INIT = 301; // 初始化阶段
    public static final int BARCODE = 302; // 获取条码阶段
    public static final int START = 303; // 正式售卖阶段

    public static volatile int sState = BOOT;

    private String mMacAddress;
    private volatile AbstractResult.DoorStatus mDoorResult;
    private volatile AbstractResult.Fault mFaultResult;


    public String getMacAddress() {
        return mMacAddress;
    }

    public AbstractResult.DoorStatus getDoorResult() {
        return mDoorResult;
    }

    public void setDoorResult(AbstractResult.DoorStatus doorResult) {
        mDoorResult = doorResult;
    }

    public AbstractResult.Fault getFaultResult() {
        return mFaultResult;
    }

    public void setFaultResult(AbstractResult.Fault faultResult) {
        mFaultResult = faultResult;
    }

    private DeviceManager() {
        mMacAddress = getLocalEthernetMacAddress().toLowerCase();
        if (mMacAddress == null) {
            mMacAddress = "11:22:33:44:55:66";
        }
    }

    private static String getLocalEthernetMacAddress() {

        String mac=null;
        try {
            Enumeration localEnumeration = NetworkInterface.getNetworkInterfaces();

            while (localEnumeration.hasMoreElements()) {

                NetworkInterface localNetworkInterface=(NetworkInterface) localEnumeration.nextElement();
                String interfaceName=localNetworkInterface.getDisplayName();

                if (interfaceName == null) {
                    continue;
                }

                if (interfaceName.equals("eth0")) {
                    mac = convertToMac(localNetworkInterface.getHardwareAddress());
                    if (mac!=null&&mac.startsWith("0:")) {
                        mac="0"+mac;
                    }
                    break;
                }

            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return mac;
    }

    private static String convertToMac(byte[] mac) {
        StringBuilder sb=new StringBuilder();
        for (int i=0; i<mac.length; i++) {
            byte b=mac[i];
            int value=0;
            if (b>=0&&b<=16) {
                value=b;
                sb.append("0"+Integer.toHexString(value));
            } else if (b>16) {
                value=b;
                sb.append(Integer.toHexString(value));
            } else {
                value=256+b;
                sb.append(Integer.toHexString(value));
            }
            if (i!=mac.length-1) {
                sb.append(":");
            }
        }
        return sb.toString();
    }

    public static DeviceManager instance() {
        return InlineClass.sInstance;
    }

    private static final class InlineClass {
        public static final DeviceManager sInstance = new DeviceManager();
    }
}
