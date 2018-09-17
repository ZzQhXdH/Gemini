package data;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by xdhwwdz20112163.com on 2018/3/19.
 */

public class GoodsType {

    /**
     * 条码格式: 10 11 09 22 18 03 19
     */

    private String mBarCode;
    private String mGoodType;

    public GoodsType(String goodType, String barCode) {
        mBarCode = barCode;
        mGoodType = goodType;
    }

    public JSONObject toJson() {

        JSONObject object = new JSONObject();
        try {
            object.put("barCode", mBarCode);
            object.put("goodsType", mGoodType);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return object;
    }

    @Override
    public String toString() {

        JSONObject object = new JSONObject();
        try {
            object.put("barCode", mBarCode);
            object.put("goodsType", mGoodType);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return object.toString();
    }
}
