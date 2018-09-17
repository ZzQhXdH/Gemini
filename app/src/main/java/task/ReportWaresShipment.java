package task;

import org.json.JSONObject;

import util.DeviceManager;
import util.Http;
import util.Logger;
import util.PayManager;
import util.WaresManager;

public class ReportWaresShipment implements Runnable {

    private final String goodsType;

    public ReportWaresShipment(final String goodsType) {
        this.goodsType = goodsType;
    }

    @Override
    public void run() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("cargoData", goodsType);
            jsonObject.put("macAddr", DeviceManager.instance().getMacAddress());
            jsonObject.put("out_trade_no", PayManager.instance().getOrder());
            final String result = Http.instance().xPostBody(Http.REPORT_WARES_URL, jsonObject.toString());
           // final String result =  Http.instance().xPostBody("http://192.168.1.120:8080/bg-uc/replenishment/work-off/quantity.json", jsonObject.toString());
            Logger.instance().d("汇报商品已经售出", result);
        } catch (Exception e) {
            e.printStackTrace();
            HandlerTaskManager.instance().getDelayHandler().postDelayed(this, 1000);
        }

    }
}
