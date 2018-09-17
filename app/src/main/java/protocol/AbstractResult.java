package protocol;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.CharArrayReader;
import java.util.HashMap;
import java.util.Map;

import data.GoodsType;
import util.DeviceManager;

/**
 * Created by xdhwwdz20112163.com on 2018/3/20.
 */

public abstract class AbstractResult {

    public static final String TYPE_ACK = "type.ack"; // ACK
    public static final String TYPE_NCK = "type.nck"; // NCK
    public static final String TYPE_BAR_CODE = "type.bar.code"; // 条码查询返回
    public static final String TYPE_IRDA = "type.irda"; // 红外
    public static final String TYPE_GOODS_TYPE = "type.goods.type"; // 货道查询返回
    public static final String TYPE_INIT_STATUS = "type.init.status"; // 初始化阶段的状态查询返回
    public static final String TYPE_SHIPMENT_STATUS = "type.shipment.status"; // 出货阶段的状态查询命令返回
    public static final String TYPE_DOOR_STATUS = "type.door.status"; // 门状态查询返回
    public static final String TYPE_OTHER_STATUS = "type.other.status"; // 忙状态, 指定货道无货
    public static final String TYPE_FAULT = "type.fault"; // 故障返回
    public static final String TYPE_TEMPERATURE = "type.temperature"; // 温度查询返回

    protected String mType;
    protected byte[] mRawData;

    public String getType() {
        return mType;
    }

    public AbstractResult(byte[] data, String type) {
        mRawData = data;
        mType = type;
    }

    private static AbstractResult parseStatus(final byte[] rawData) {

        byte h = rawData[2];
        switch (h) {
            case (byte) 0x90: return new InitStatus(rawData);
            case (byte) 0x91: return new ShipmentStatus(rawData);
            case (byte) 0x00: return new OtherStatus(rawData);
            case (byte) 0x84: return new DoorStatus(rawData);
        }
        return null;
    }

    public static AbstractResult parseOther(final byte[] rawData) {

        int ackCode = 0;
        int cmd = ((int) rawData[2]) & 0xFF;
        if (rawData.length > 5) {
            ackCode = rawData[5];
        }
        if (cmd == 0xC3 || ackCode == 0x02) {
            return new Temperature(rawData);
        }

        return null;
    }

    public static AbstractResult parse(final byte[] rawData) {

        byte h = rawData[0];
        byte l = rawData[1];

        if (h == (byte) 0x2B) {

            switch (l) {

                case (byte) 0x0D: // 条码扫描返回
                    return new BarCode(rawData);

                case (byte) 0x1B: // 货道查询返回
                    return new GoodsType(rawData);

                case (byte) 0x06:
                    return parseStatus(rawData);

                case (byte) 0x12:
                    return new Fault(rawData);

                case (byte) 0x05:
                    return new Irda(rawData);
            }
        }

        if (h == 0x02) {
            return parseOther(rawData);
        }

        if (h != (byte) 0x1C) {
            return null;
        }

        l = rawData[2];

        switch (l) {
            case (byte) 0x00:
                return new Ack(rawData);
            case (byte) 0x01:
                return new Nck(rawData);
        }

        return null;
    }

    public static final class Ack extends AbstractResult {
        public Ack(byte[] data) {
            super(data, TYPE_ACK);
        }
    }

    public static final class Nck extends AbstractResult {
        public Nck(byte[] data) {
            super(data, TYPE_NCK);
        }
    }

    public static final class Fault extends AbstractResult {

        private static final String[] FAULT_LIST = new String[] {
                "nsj", "xz", "wsj", "ntp", "nth",
                "bwm", "wth", "qwm", "whjc", "qwmhw",
                "cshzd", "qwmcs", "gbcs",
                "wbyc", "trouble"
        };

        private boolean mFaultFlag = false;

        private static final String[] FAULT_STATUS_LIST = new String[] {
                "无故障", "堵转", "超时"
        };

        public Fault(byte[] data) {
            super(data, TYPE_FAULT);
        }

        public boolean isFault9() {
            return mRawData[10] != 0x00;
        }

        public String toJsonString() throws JSONException {

            JSONObject object = new JSONObject();
            object.put("macAddr", DeviceManager.instance().getMacAddress());
            int v;
            for (int i = 2; i < 16; i ++) {
                v = mRawData[i];
                if (v != 0) {
                    mFaultFlag = true;
                }
                object.put(FAULT_LIST[i - 2], FAULT_STATUS_LIST[v]);
            }
            if (mFaultFlag) {
                object.put(FAULT_LIST[14], "是");
            } else {
                object.put(FAULT_LIST[14], "否");
            }
            return object.toString();
        }

    }

    // 条码查询返回
    public static final class BarCode extends AbstractResult {

        public BarCode(byte[] data) {
            super(data, TYPE_BAR_CODE);
        }

        public String getGoodsType() {

            StringBuilder builder = new StringBuilder();
            builder.append(mRawData[2]);
            builder.append("-");
            builder.append(mRawData[3]);

            return builder.toString();
        }

        public String getBarcode() {
            StringBuilder builder = new StringBuilder();
            for (int i = 4; i < 11; i ++) {
                builder.append(String.format("%02x", mRawData[i]));
            }
            return builder.toString();
        }

        public data.GoodsType toGoodsType() {
            int col = mRawData[2];
            int row = mRawData[3];
            String goodType = String.format("%d-%d", col, row);
            StringBuilder builder = new StringBuilder();
            for (int i = 4; i < 11; i ++) {
                builder.append(String.format("%02x", mRawData[i]));
            }
            return new data.GoodsType(goodType, builder.toString());
        }
    }

    // 货道查询返回
    public static final class GoodsType extends AbstractResult {

        public GoodsType(byte[] data) {
            super(data, TYPE_GOODS_TYPE);
        }

        public byte[] getStatus() {
            byte[] bytes = new byte[22];
            for (int i = 0; i < 22; i ++) {
                bytes[i] = mRawData[3 + i];
            }
            return bytes;
        }

        public String getInfo() {

            byte[] bytes = getStatus();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i ++) {
                sb.append(getCol());
                sb.append("-");
                sb.append(i + 1);
                sb.append(":");
                sb.append(bytes[i] == (byte) 0x01 ? "有货\r\n" : "无货\r\n");
            }
            return sb.toString();
        }

        public byte getCol() {
            return mRawData[2];
        }
    }

    public static final class InitStatus extends AbstractResult {

        public InitStatus(byte[] data) {
            super(data, TYPE_INIT_STATUS);
        }

        public String getStatus() {

            switch (mRawData[3]) {
                case (byte) 0x01: return "内升降和旋转定位阶段";

                case (byte) 0x02: return "内取货电机测试阶段";

                case (byte) 0x03: return "内升降测试阶段";

                case (byte) 0x04: return "条码扫描阶段";

                case (byte) 0x05: return "保温门及内推货测试阶段";

                case (byte) 0x06: return "外升降和内推货测试阶段";

                case (byte) 0x07: return "外推货和取物门测试阶段";

                case (byte) 0x10: return "命令初始化完成";
            }
            return "未知阶段";
        }
    }

    public static final class ShipmentStatus extends AbstractResult {

        public ShipmentStatus(byte[] data) {
            super(data, TYPE_SHIPMENT_STATUS);
        }

        public int getStatusCode() {
            return (int) mRawData[3];
        }

        public String getStatus() {

            byte h = mRawData[3];
            switch (h) {
                case (byte) 0x01: return "内升降和旋转定位阶段";
                case (byte) 0x02: return "从货架上取货";
                case (byte) 0x03: return "将盒饭送到外托盘";
                case (byte) 0x04: return "微波加热过程";
                case (byte) 0x05: return "送到取物口";
                case (byte) 0x06: return "取盒饭过程";
                case (byte) 0x10: return "出货过程完成";
            }
            return "未知阶段";
        }
    }

    public static final class DoorStatus extends AbstractResult {

        public DoorStatus(byte[] data) {
            super(data, TYPE_DOOR_STATUS);
        }

        public boolean isOpen() {
            return mRawData[3] == (byte) 0x00;
        }

        public String getStatus() {
            return mRawData[3] == (byte) 0x00 ? "关闭" : "打开";
        }
    }

    public static final class OtherStatus extends AbstractResult {

        public OtherStatus(byte[] data) {
            super(data, TYPE_OTHER_STATUS);
        }

        public boolean isBusy() {
            return mRawData[3] == (byte) 0x01;
        }
    }

    public static final class Irda extends AbstractResult {

        public Irda(byte[] data) {
            super(data, TYPE_IRDA);
        }

        public boolean isError() {
            return mRawData[2] == (byte) 0x01;
        }

    }

    public static final class Temperature extends AbstractResult {

        public Temperature(byte[] data) {
            super(data, TYPE_TEMPERATURE);
        }

        public int getTemperature() {
            return mRawData[7];
        }

        public int getId() { // 1 左仓,2 右仓
            return mRawData[6] & 0xFF;
        }
    }
}
