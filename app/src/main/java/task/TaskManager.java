package task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by xdhwwdz20112163.com on 2018/3/19.
 */

public class TaskManager {

    private ExecutorService mExecutors;

    public void runTask(Runnable runnable) {
        mExecutors.execute(runnable);
    }


    private TaskManager() {
        mExecutors = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    public static TaskManager instance() {
        return InlineClass.sInstance;
    }

    private static final class InlineClass {
        public static final TaskManager sInstance = new TaskManager();
    }
}
