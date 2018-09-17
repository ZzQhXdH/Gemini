package util;

import com.jf.geminjava.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import data.Wares;

/**
 * Created by xdhwwdz20112163.com on 2018/3/16.
 */

public class WaresManager {

    private List<Wares> mWaresList = new ArrayList<>();
    private int mSelectWaresIndex = -1;
    private Wares mSelectWares = null;

    private static final String[] COL1_ARRAY = {
            "1-1", "1-2", "1-3", "1-4", "1-5",
            "1-6", "1-7", "1-8", "1-9", "1-10",
            "1-11", "1-12", "1-13", "1-14", "1-15",
            "1-16", "1-17", "1-18", "1-19", "1-20",
            "1-21", "1-22",
    };

    private static final String[] COL2_ARRAY = {
            "2-1", "2-2", "2-3", "2-4", "2-5",
            "2-6", "2-7", "2-8", "2-9", "2-10",
            "2-11", "2-12", "2-13", "2-14", "2-15",
            "2-16", "2-17", "2-18", "2-19", "2-20",
            "2-21", "2-22",
    };

    private static final String[] COL3_ARRAY = {
            "3-1", "3-2", "3-3", "3-4", "3-5",
            "3-6", "3-7", "3-8", "3-9", "3-10",
            "3-11", "3-12", "3-13", "3-14", "3-15",
            "3-16",
    };

    private static final String[] COL4_ARRAY = {
            "4-1", "4-2", "4-3", "4-4", "4-5",
            "4-6", "4-7", "4-8", "4-9", "4-10",
            "4-11", "4-12", "4-13"
    };

    public void vUpdate(final String jsonString) {

        mWaresList.clear();
        try {
            final JSONObject jsonObject = new JSONObject(jsonString);
            final JSONArray jsonArray = jsonObject.optJSONArray("jArrayData");
            for (int i = 0; i < jsonArray.length(); i ++) {
                Wares wares = Wares.parse(jsonArray.getJSONObject(i));
                if (wares == null) {
                    Logger.instance().d("解析错误", "解析出错");
                    continue;
                }
                Logger.instance().d("解析成功", wares.toString());
                mWaresList.add(wares);
            }
        }  catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Wares getSelectWares() {
        return mSelectWares;
    }

    public void setSelectWaresIndex(int index) {
        mSelectWaresIndex = index;
        mSelectWares = mWaresList.get(index);
    }

    public int getSelectWaresIndex() {
        return mSelectWaresIndex;
    }

    public int getWaresCount() {
        return mWaresList.size();
    }

    public Wares get(int index) {
        return mWaresList.get(index);
    }

    private WaresManager() {
/*
        Wares wares = new Wares("番茄烧牛腩", "id1", COL1_ARRAY, 22, 0.01, 4, R.drawable.fqsnn, R.drawable.fqsnn2, "");
        mWaresList.add(wares);

        wares = new Wares("红烧肥肠", "id2", COL2_ARRAY, 22, 0.01, 4, R.drawable.hsfc, R.drawable.hsfc2, "");
        mWaresList.add(wares);

        wares = new Wares("农家小炒肉", "id3", COL3_ARRAY, 16, 0.01, 5, R.drawable.njxcr, R.drawable.njxcr2,"");
        mWaresList.add(wares);

        wares = new Wares("梅菜扣肉", "id4", COL4_ARRAY, 13, 0.01, 5, R.drawable.mckr, R.drawable.mckr2, "");
        mWaresList.add(wares);
*/
/*
        wares = new Wares("苦瓜", R.drawable._5, R.drawable._55);
        mWaresList.add(wares);

        wares = new Wares("苦瓜2", R.drawable._6, R.drawable._66);
        mWaresList.add(wares);

        wares = new Wares("肉丝", R.drawable._7, R.drawable._77);
        mWaresList.add(wares);

        wares = new Wares("肥肠", R.drawable._8, R.drawable._88);
        mWaresList.add(wares);
  */  }

    public static WaresManager instance() {
        return InlineClass.sInstance;
    }

    private static class InlineClass {
        public static final WaresManager sInstance = new WaresManager();
    }
}
