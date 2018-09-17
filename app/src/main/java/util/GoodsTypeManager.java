package util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import data.GoodsType;

/**
 * Created by xdhwwdz20112163.com on 2018/3/19.
 */

public class GoodsTypeManager {

    private List<GoodsType> mGoodsTypes;

    public JSONObject toJson() {
        JSONObject object = new JSONObject();
        try {
            object.put("macAddr", DeviceManager.instance().getMacAddress());
            JSONArray array = new JSONArray();
            for (int i = 0; i < mGoodsTypes.size(); i ++) {
                array.put(mGoodsTypes.get(i).toJson());
            }
            object.put("array", array);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return object;

    }

    public void addBarcode(GoodsType goodsType) {
        mGoodsTypes.add(goodsType);
    }

    public void print() {
        for (GoodsType type : mGoodsTypes) {
            Logger.instance().d("条码信息", type.toString());
        }
    }

    public void clear() {
        mGoodsTypes.clear();
    }

    public void addGoodsType(GoodsType type) {
        mGoodsTypes.add(type);
    }

    private GoodsTypeManager() {

        mGoodsTypes = new ArrayList<>();
/*
        mGoodsTypes.add(new GoodsType("1-1", "11223344180323"));
        mGoodsTypes.add(new GoodsType("1-2", "13223344180323"));
        mGoodsTypes.add(new GoodsType("1-3", "14223344180323"));
        mGoodsTypes.add(new GoodsType("1-4", "15223344180323"));
        mGoodsTypes.add(new GoodsType("1-5", "11623344180323"));
        mGoodsTypes.add(new GoodsType("1-6", "11423344180323"));
        mGoodsTypes.add(new GoodsType("1-7", "11323344180323"));
        mGoodsTypes.add(new GoodsType("1-8", "11229344180323"));
    //    mGoodsTypes.add(new GoodsType("1-9", "11353344180323"));
        mGoodsTypes.add(new GoodsType("1-10", "11623346180323"));*/
    }

    public static GoodsTypeManager instance() {
        return InlineClass.sInstance;
    }

    private static class InlineClass {
        public static final GoodsTypeManager sInstance = new GoodsTypeManager();
    }

}
