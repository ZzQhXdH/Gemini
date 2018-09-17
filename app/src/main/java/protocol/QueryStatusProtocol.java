package protocol;

import util.SerialPortManager;

/**
 * 查询状态的协议
 */
public class QueryStatusProtocol extends AbstractStrongProtocol {

    public QueryStatusProtocol(byte type, byte[] args) {
        super(type, args);
    }

    public static class Build
    {
        private byte d1 = 0;
        private byte d2;
        private byte d3;

        public Build setQueryCode(byte code) {
            d2 = code;
            return this;
        }

        /**
         * left 01
         * right 02
         */
        public Build setId(byte id) {
            d3 = id;
            return this;
        }

        public QueryStatusProtocol build() {
            return new QueryStatusProtocol((byte) 0x43, new byte[] {d1, d2, d3});
        }
    }

    public static void vQueryTemperature(int p) {
        
        AbstractStrongProtocol protocol = new QueryStatusProtocol.Build()
                .setId((byte) (p + 1))
                .setQueryCode((byte) 2) // 温度查询
                .build();
        byte[] bytes = protocol.getByteArray();
        SerialPortManager.instance().getSerialPort().xWrite(bytes);
    }
}
