package protocol;


import util.SerialPortManager;

/**
 * Created by xdhwwdz20112163.com on 2018/3/20.
 */

public abstract class AbstractProtocol {

    // 外升降直流电机控制命令 1B 06 61 XX 0D 0A
    // XX 1:外升降直流电机上升, 2:外升降直流电机下降
    public static final int CMD0 = 0;
    private static byte[] mCommand0 = new byte[] {
            (byte) 0x1B, 0x06, 0x61, 00, 0x0D, 0x0A
    };

    // 保温门控制命令 1B 06 62 XX 0D 0A
    // XX 1:关闭, 2:打开
    public static final int CMD1 = 1;
    private static byte[] mCommand1 = new byte[] {
            (byte) 0x1B, 0x06, 0x62, 00, 0x0D, 0x0A
    };

    // 推货电机控制命令 1B 06 63 XX 0D 0A
    // XX 1:推进, 2:退回
    public static final int CDM2 = 2;
    private static byte[] mCommand2 = new byte[] {
            (byte) 0x1B, 0x06, 0x63, 00, 0x0D, 0x0A
    };

    // 取物门控制命令 1B 06 64 XX 0D 0A
    // XX 1:关闭, 2:退回
    public static final int CMD3 = 3;
    private static byte[] mCommand3 = new byte[] {
            (byte) 0x1B, 0x06, 0x64, 00, 0x0D, 0x0A
    };

    // 外托盘推货步进电机控制命令 1B 06 65 XX 0D 0A
    // XX 1:推出饭盒, 2:收回
    public static final int CMD4 = 4;
    private static byte[] mCommand4 = new byte[] {
            (byte) 0x1B, 0x06, 0x65, 00, 0x0D, 0x0A
    };

    // 外托盘红外检测命令 1B 05 66 0D 0A
    public static final int CMD5 = 5;
    public static final byte[] mCommand5 = new byte[] {
            (byte) 0x1B, 0x05, (byte) 0x66, 0x0D, 0x0A
    };

    // 微波测试命令 1B 06 67 XX 0D 0A
    // XX 微波加热时间 最小10s
    public static final int CMD6 = 6;
    private static byte[] mCommand6 = new byte[] {
            (byte) 0x1B, 0x06, 0x67, 0x00, 0x0D, 0x0A
    };

    // 内升降步进电机控制命令 1B 08 71 XX YY ZZ 0D 0A
    // XX,YY:测试步数=XX*256+YY
    // ZZ 1:向上运行, 2:向下运行
    public static final int CMD7 = 7;
    private static byte[] mCommand7 = new byte[] {
            (byte) 0x1B, 0x08, (byte) 0x71, 00, 00, 00, 0x0D, 0x0A
    };

    // 条码测试命令 1B 05 80 0D 0A
    public static final int CMD8 = 8;
    public static final byte[] mCommand8 = new byte[] {
        0x1B, 0x05, (byte) 0x80, 0x0D, 0x0A
    };

    // 货道状态查询命令 1B 06 81 XX 0D 0A
    // XX: 列号
    public static final int CMD9 = 9;
    private static byte[] mCommand9 = new byte[] {
            (byte) 0x1B, 0x06, (byte) 0x81, 00, 0x0D, 0x0A
    };

    // 条码查询命令 1B 07 82 XX YY 0D 0A
    // XX: 列号, YY: 行号
    public static final int CMD10 = 10;
    private static byte[] mCommand10 = new byte[] {
            (byte) 0x1B, 0x07, (byte) 0x82, 00, 00, 0x0D, 0x0A
    };

    // 状态查询命令 1B 05 83 0D 0A
    public static final int CMD11 = 11;
    public static final byte[] mCommand11 = new byte[] {
            (byte) 0x1B, 0x05, (byte) 0x83, 0x0D, 0x0A
    };

    // 货仓门状态查询命令 1B 05 84 0D 0A
    public static final int CMD12 = 12;
    public static final byte[] mCommand12 = new byte[] {
            (byte) 0x1B, 0x05, (byte) 0x84, 0x0D, 0x0A
    };

    // 初始化命令 1B 05 90 0D 0A
    public static final int CMD13 = 13;
    public static final byte[] mCommand13 = new byte[] {
            (byte) 0x1B, 0x05, (byte) 0x90, 0x0D, 0x0A
    };

    // 出货命令 1B 08 91 XX YY ZZ 0D 0A
    // XX: 列号, YY: 行号, ZZ: 加热时间
    public static final int CMD14 = 14;
    private static byte[] mCommand14 = new byte[] {
            (byte) 0x1B, 0x08, (byte) 0x91, 00, 00, 00, 0x0D, 0x0A
    };

    // 列行参数设定命令 1B 09 92 W0 X0 Y0 Z0 0D 0A
    // W0:1列层高 3C, 50, 64
    // X0:2列层高 3C, 50, 64
    // Y0:3列层高 3C, 50, 64
    // Z0:4列层高 3C, 50, 64
    public static final int CMD15 = 15;
    private static byte[] mCommand15 = new byte[] {
            (byte) 0x1B, 0x09, (byte) 0x92, 00, 00, 00, 00, 0x0D, 0x0A
    };

    protected int mType;

    protected abstract int getType();

    public static void vWriteCommand1(byte flag) {
        mCommand0[3] = flag;
        SerialPortManager.instance().getSerialPort().xWrite(mCommand0);
    }

    public static void vWriteCommand2(byte flag) {
        mCommand1[3] = flag;
        SerialPortManager.instance().getSerialPort().xWrite(mCommand1);
    }

    public static void vWriteCommand3(byte flag) {
        mCommand2[3] = flag;
        SerialPortManager.instance().getSerialPort().xWrite(mCommand2);
    }

    public static void vWriteCommand4(byte flag) {
        mCommand3[3] = flag;
        SerialPortManager.instance().getSerialPort().xWrite(mCommand3);
    }

    public static void vWriteCommand5(byte flag) {
        mCommand4[3] = flag;
        SerialPortManager.instance().getSerialPort().xWrite(mCommand4);
    }

    public static void vWriteCommand6() {
        SerialPortManager.instance().getSerialPort().xWrite(mCommand5);
    }

    public static void vWriteCommand7(byte time) {
        mCommand6[3] = time;
        SerialPortManager.instance().getSerialPort().xWrite(mCommand6);
    }

    public static void vWriteCommand8(short steps, byte flag) {
        mCommand7[3] = (byte) (steps >> 8);
        mCommand7[4] = (byte) (steps & 0xFF);
        mCommand7[5] = flag;
        SerialPortManager.instance().getSerialPort().xWrite(mCommand7);
    }

    public static void vWriteCommand9() {
        SerialPortManager.instance().getSerialPort().xWrite(mCommand8);
    }

    public static void vWriteCommand10(byte col) {
        mCommand9[3] = col;
        SerialPortManager.instance().getSerialPort().xWrite(mCommand9);
    }

    public static void vWriteCommand11(byte col, byte row) {
        mCommand10[3] = col;
        mCommand10[4] = row;
        SerialPortManager.instance().getSerialPort().xWrite(mCommand10);
    }

    public static void vWriteCommand12() {
        SerialPortManager.instance().getSerialPort().xWrite(mCommand11);
    }

    public static void vWriteCommand13() {
        SerialPortManager.instance().getSerialPort().xWrite(mCommand12);
    }

    public static void vWriteCommand14() {
        SerialPortManager.instance().getSerialPort().xWrite(mCommand13);
    }

    public static void vWriteCommand15(byte col, byte row, byte time) {
        mCommand14[3] = col;
        mCommand14[4] = row;
        mCommand14[5] = time;
        SerialPortManager.instance().getSerialPort().xWrite(mCommand14);
    }

    public static void vWriteCommand16(byte col1, byte col2, byte col3, byte col4) {
        mCommand15[3] = col1;
        mCommand15[4] = col2;
        mCommand15[5] = col3;
        mCommand15[6] = col4;
        SerialPortManager.instance().getSerialPort().xWrite(mCommand15);
    }

}
