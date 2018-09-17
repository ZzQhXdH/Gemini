package serialport;

/**
 * Created by xdhwwdz20112163.com on 2018/3/23.
 */

public interface ISerialPort {

    boolean xOpen(int baud_rate, int data_bits, int priority, int stop_bits, int flow_control);

    int xWrite(final byte[] bytes);

    int xRead(final byte[] bytes);

    boolean isPermission();
}
