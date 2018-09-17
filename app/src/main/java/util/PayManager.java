package util;

/**
 * Created by xdhwwdz20112163.com on 2018/3/19.
 */

public class PayManager {

    public static final int PAY_TYPE_WECHAT = 0;
    public static final int PAY_TYPE_ALIPAY = 1;
    public static final int PAY_UNDEFINED = 100;

    private volatile int mPayType;
    private volatile String mOrder;

    public int getPayType() {
        return mPayType;
    }

    public void setPayType(int type) {
        mPayType = type;
    }

    public String getOrder() {
        return mOrder == null ? "" : mOrder;
    }

    public void setOrder(String order) {
        mOrder = order;
    }

    public static PayManager instance() {
        return InlineClass.sInstance;
    }

    private static class InlineClass {
        public static final PayManager sInstance = new PayManager();
    }
}
