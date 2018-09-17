package protocol;

public abstract class AbstractStrongProtocol {

    protected byte[] mData;

    public byte[] getByteArray() {
        return mData;
    }

    public AbstractStrongProtocol(byte type, byte[] args) {

        int len = 5;
        if (args != null) {
            len += args.length;
        }
        byte c = 0;
        mData = new byte[len];

        mData[0] = 0x02;
        c += mData[0];

        mData[1] = (byte) (len - 1);
        c += mData[1];

        mData[2] = type;
        c += mData[2];

        mData[3] = 1;
        c += mData[3];

        if (args != null)
        {
            for (int i = 0; i < args.length; i ++)
            {
                c += args[i];
                mData[4 + i] = args[i];
            }
        }
        mData[len - 1] = c;
    }
}
