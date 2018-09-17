package util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.net.Uri;
import android.os.Environment;

import java.io.File;

/**
 * Created by xdhwwdz20112163.com on 2018/3/19.
 */

public class Custom {

    public static String fromByteArray(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < bytes.length; i ++) {
            builder.append(String.format("%02x", bytes[i]));
        }
        return builder.toString();
    }

    public static String fromByteArrayExt(final byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < bytes.length; i ++) {
            builder.append(String.format("%02x ", bytes[i]));
        }
        builder.append("\r\n");
        return builder.toString();
    }

    public static String fromByteArrayExtNo(final byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < bytes.length; i ++) {
            builder.append(String.format("%02x ", bytes[i]));
        }
        return builder.toString();
    }

    public static File[] scanFiles(final String path, final String type) {

        File dir = new File(Environment.getExternalStorageDirectory(), path);
        return dir.listFiles(((dir1, name) -> name.endsWith(type)));
    }

    public static Uri resourceIdToUri(Context context, int id) {
        return Uri.parse("android.resource://" + context.getPackageName()
                    + "/" + id);
    }

    public static StateListDrawable getSelector(Drawable pressDraw, Drawable normalDraw) {

        StateListDrawable drawable = new StateListDrawable();
        drawable.addState(new int[] {android.R.attr.state_pressed}, pressDraw);
        drawable.addState(new int[] {}, normalDraw);
        return drawable;
    }

    public static LayerDrawable getLayerDrawable(Bitmap bm, int argb) {

        Drawable[] drawables = new Drawable[2];
        drawables[0] = new BitmapDrawable(bm);
        drawables[1] = new ColorDrawable(argb);
        LayerDrawable layerDrawable = new LayerDrawable(drawables);
        return layerDrawable;
    }

    public static byte[] getGoodsType(final String goodsType) {
        byte[] bytes = new byte[2];
        String[] strings = goodsType.split("-");
        bytes[0] = Byte.parseByte(strings[0]);
        bytes[1] = Byte.parseByte(strings[1]);
        return bytes;
    }

    public static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
