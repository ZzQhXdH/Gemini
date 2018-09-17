package application;

import android.app.Application;
import android.content.Context;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import service.SerialPortService;
import task.HandlerTaskManager;
import task.QueryDeviceStateTask;
import task.UpdateFaultTask;

/**
 * Created by xdhwwdz20112163.com on 2018/3/16.
 */

public class GeminiApplication extends Application {

    private static Context sAppContext = null;
    private static Handler sHandler = null;

    public static Handler getMainHandler() {
        return sHandler;
    }

    public static Context getAppContext() {
        return sAppContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sAppContext = this.getApplicationContext();
        sHandler = new MainHandler();
        HandlerTaskManager.instance().getDelayHandler().post(new UpdateFaultTask());
    }
}
