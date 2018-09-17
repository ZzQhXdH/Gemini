package task;

import android.bluetooth.BluetoothClass;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import util.DeviceManager;
import util.Http;
import util.Logger;
import util.PayManager;
import util.WaresManager;

/**
 * Created by xdhwwdz20112163.com on 2018/3/28.
 */

public class RefundTask implements Runnable {

    private static final String TAG = RefundTask.class.getSimpleName();

    private static final int MAX_REPEAT_COUNT = 10;
    private static final int ERROR_DELAY_TIME = 60 * 1000;

    private int mPayType;
    private String mPayOrder = "";

    public RefundTask() {
        mPayType = PayManager.instance().getPayType();
        mPayOrder = PayManager.instance().getOrder();
    }

    @Override
    public void run() {

        final RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("out_trade_no", mPayOrder)
                .addFormDataPart("macAddr", DeviceManager.instance().getMacAddress())
                .addFormDataPart("refund_remark", "出货失败退款")
                .addFormDataPart("cargoData",  WaresManager.instance().getSelectWares().getGoodsType())
                .build();

        final String url = (mPayType == PayManager.PAY_TYPE_ALIPAY ? Http.PAY_ALIPAY_REFUND_URL : Http.PAY_REFUND_URL);

        String result;

        for (int i = 0; i < MAX_REPEAT_COUNT; i ++) {

            try {
                result = Http.instance().xPostForm(url, body);
                Logger.instance().d("退款返回", result);
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
            Logger.instance().d("退款失败", "退款失败");
        }
        Logger.instance().d(TAG, "退款失败将于1分钟以后重新去退款");
        HandlerTaskManager.instance().getDelayHandler().postDelayed(this, ERROR_DELAY_TIME);
    }
}
