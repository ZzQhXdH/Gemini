package receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import activity.HomeActivity;
import util.Custom;

/**
 * Created by xdhwwdz20112163.com on 2018/3/22.
 */

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Custom.sleep(10000);
        intent = new Intent(context, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
