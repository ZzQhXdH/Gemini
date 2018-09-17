package task;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import activity.HomeActivity;
import application.GeminiApplication;
import util.GoodsTypeManager;
import util.Http;
import util.Logger;
import util.WaresManager;

public class UpdateBarcodeTask implements Runnable {

    @Override
    public void run() {

        final String s = GoodsTypeManager.instance().toJson().toString();
        Logger.instance().d("条码信息", s);
        final String result = Http.instance().xPostBody(Http.BAR_CODE_UP_URL, s);
        Logger.instance().d("返回", result == null ? "没有返回" : result);
        if (result == null) {
            HandlerTaskManager.instance().getHandler().post(this);
            return;
        }
        WaresManager.instance().vUpdate(result);
        Intent intent = new Intent(HomeActivity.UPDATE_DATA);
        LocalBroadcastManager.getInstance(GeminiApplication.getAppContext()).sendBroadcast(intent);
    }
}
