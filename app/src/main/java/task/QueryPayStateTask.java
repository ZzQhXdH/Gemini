package task;

import android.os.Handler;
import android.os.Message;

import org.json.JSONException;
import org.json.JSONObject;

import application.GeminiApplication;
import application.MainCallback;
import application.MainHandler;
import popup.HeatPopupWindow;
import util.Custom;
import util.Http;
import util.PayManager;

/**
 * Created by xdhwwdz20112163.com on 2018/3/22.
 */

public class QueryPayStateTask implements Runnable {

    private MainCallback mCallback;

    public static final int QUERY_SUCC = 20;
    public static final int QUERY_ERROR = 21;
    public static final int QUERY_TIME_OUT = 22;
    public static final int QUERY_CANCEL = 23;

    public static boolean sQueryFlag;

    public QueryPayStateTask(MainCallback callback) {
        mCallback = callback;
    }

    @Override
    public void run() {

        int queryCount = 40;
        String result;
        JSONObject object;
        String order;
        String status;
        Message message = Message.obtain();
        message.obj = mCallback;

        final String userOrder = PayManager.instance().getOrder();

        do {
            result = Http.instance().xQueryPayState();

            if (result == null) {
                break;
            }

            try {
                object = new JSONObject(result);
            } catch (JSONException e) {
                e.printStackTrace();
                break;
            }

            order = object.optString("out_trade_no");
            status = object.optString("macstate");

            if (userOrder.equals(order) && status.equals("paymentsuccess")) {
                mCallback.what = QUERY_SUCC;
                GeminiApplication.getMainHandler().sendMessage(message);
                Http.instance().vUpdateServerPayStatus();
                return;
            }

            queryCount --;

            Custom.sleep(2000);

        } while ((queryCount > 0) && sQueryFlag);


        if (queryCount <= 0) {
            mCallback.what = QUERY_TIME_OUT;
            GeminiApplication.getMainHandler().sendMessage(message);
            return;
        }

        if (sQueryFlag) {
            mCallback.what = QUERY_ERROR;
            GeminiApplication.getMainHandler().sendMessage(message);
            return;
        }

    }
}
