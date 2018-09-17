package receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import application.GeminiApplication;
import protocol.AbstractResult;
import task.QueryDeviceStateTask;

/**
 * Created by xdhwwdz20112163.com on 2018/3/29.
 */

public class AckReceiver extends BroadcastReceiver {

    private static final String TAG = AckReceiver.class.getSimpleName();

    public static void register() {

        Context context = GeminiApplication.getAppContext();
        IntentFilter filter = new IntentFilter();
        filter.addAction(AbstractResult.TYPE_ACK);
        LocalBroadcastManager.getInstance(context).registerReceiver(InlineClass.sInstance, filter);
        QueryDeviceStateTask.start();
    }

    public static void unregister() {
        QueryDeviceStateTask.stop();
        Context context = GeminiApplication.getAppContext();
        LocalBroadcastManager.getInstance(context).unregisterReceiver(InlineClass.sInstance);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        unregister();
    }

    private static final class InlineClass {
        public static final AckReceiver sInstance = new AckReceiver();
    }
}
