package task;

import android.os.Handler;
import android.os.HandlerThread;

/**
 * Created by xdhwwdz20112163.com on 2018/3/23.
 */

public class HandlerTaskManager {

    private static final String TAG = HandlerTaskManager.class.getSimpleName();
    private HandlerThread mHandlerThread;
    private HandlerThread mHandlerThreadDelay; // 用于处理延迟运行的失败任务
    private Handler mHandler;
    private Handler mHandlerDelay;

    public Handler getHandler() {
        return mHandler;
    }

    public Handler  getDelayHandler() {
        return mHandlerDelay;
    }

    public void quit() {

        mHandler.removeMessages(0);
        mHandlerThread.getLooper().quit();

        mHandlerDelay.removeMessages(0);
        mHandlerThreadDelay.getLooper().quit();
    }

    private HandlerTaskManager() {

        mHandlerThread = new HandlerThread(TAG);
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());

        mHandlerThreadDelay = new HandlerThread(TAG + "Delay");
        mHandlerThreadDelay.start();
        mHandlerDelay = new Handler(mHandlerThreadDelay.getLooper());
    }

    public static HandlerTaskManager instance() {
        return InlineClass.sInstance;
    }

    private static class InlineClass {
        public static final HandlerTaskManager sInstance = new HandlerTaskManager();
    }
}
