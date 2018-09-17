package util;

/**
 * Created by xdhwwdz20112163.com on 2018/3/29.
 */

public class GoodsTypeTest {

    private byte mRow = 1;
    private byte mCol = 1;

    public byte[] getGoodsType() {
        byte[] bytes = new byte[2];

        if (mRow > 22) {
            mRow = 0;
            mCol ++;
        }

        if (mCol > 4) {
            mCol = 0;
            mRow = 0;
        }

        bytes[0] = mCol;
        bytes[1] = mRow;

        mRow ++;

        return bytes;
    }

    public static GoodsTypeTest instance() {
        return InlineClass.sInstance;
    }

    private static class InlineClass {
        public static final GoodsTypeTest sInstance = new GoodsTypeTest();
    }
}
