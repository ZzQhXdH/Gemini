package application;


import android.os.Handler;
import android.os.Message;

/**
 * Created by xdhwwdz20112163.com on 2018/3/28.
 */

public class MainHandler extends Handler {

    @Override
    public void handleMessage(Message msg) {

        MainCallback callback = (MainCallback) msg.obj;
        callback.onCall();
    }




}
