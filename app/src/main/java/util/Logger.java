package util;


import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by xdhwwdz20112163.com on 2018/3/16.
 */

public class Logger {

    private static final String DEBUG_FILE_NAME = "双子座日子文件.txt";

    private FileWriter mFileWriter = null;

    public static Logger instance() {
        return InlineClass.sInstance;
    }

    public void d(final String tag, final String message) {
        Log.d(tag, message);
    }

    public void w(final String tag, final String message) {
        Log.w(tag, message);
    }

    public void e(final String tag, final String message) {
        Log.e(tag, message);
    }

    public void file(final String message) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = format.format(Calendar.getInstance().getTime());
        StringBuilder builder = new StringBuilder();
        builder.append(time);
        builder.append("\r\n");
        builder.append(message);
        builder.append("\r\n");
        try {
            mFileWriter.append(builder.toString());
            mFileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void vOpenLoggerFile() {
        try {
            File file = new File(Environment.getExternalStorageDirectory(), DEBUG_FILE_NAME);
            if (!file.exists()) {
                file.createNewFile();
            }
            mFileWriter = new FileWriter(file, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Logger() {
        vOpenLoggerFile();
    }

    private static class InlineClass {
        public static final Logger sInstance = new Logger();
    }
}
