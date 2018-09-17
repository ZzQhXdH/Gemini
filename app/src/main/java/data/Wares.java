package data;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Random;

/**
 * Created by xdhwwdz20112163.com on 2018/3/16.
 */

public class Wares {

    private final String name; // 商品名称
    private final String id; // 商品Id
    private final String[] goodsTypes; // 商品所在的货道
    private final String price; // 商品价格
    private final int starValue; // 商品的推荐星级
    private final int minImageId;
    private final int maxImageId;
    private final String minImageUrl; // 商品Url 1
    private final String maxImageUrl; // 商品Url 2
    private final String description; // 商品描述
    private final int heatTime;
    private int counter; // 商品个数
    private int index = 0; // 当前买的商品索引

    public static Wares parse(JSONObject jsonObject) {

        /**
         * 解析货道
         */
        final JSONArray jsonArray = jsonObject.optJSONArray("cargodatas");
        final String[] goodsTypes = new String[jsonArray.length()];
        for (int i = 0; i < goodsTypes.length; i ++) {
            goodsTypes[i] = jsonArray.optString(i);
        }
        final String name = jsonObject.optString("goodsName", "");
        final int heatTime = jsonObject.optInt("heatingTime", -1);
        final int starValue = jsonObject.optInt("starValue", -1);
        final int number = jsonObject.optInt("num", -1);
        final String imageUrl1 = jsonObject.optString("waresImage1", "");
        final String imageUrl2 = jsonObject.optString("waresImage2", "");
        final double price = jsonObject.optDouble("waresPrice");
        final String id = jsonObject.optString("goodsId");

        if (name == null || number < 0) {
            return null;
        }

        return new Wares(name, id, goodsTypes, number, price, starValue, imageUrl1, imageUrl2, "", heatTime);
    }

    public Wares(String name, String id,
                 String[] goodsTypes, int counter, double price,
                 int starValue, String minImageUrl,
                 String maxImageUrl, String description, int heatTime) {
        this.name = name;
        this.id = id;
        this.goodsTypes = goodsTypes;
        this.price = String.format("%.2f", price);
        this.starValue = starValue;
        this.minImageId = 0;
        this.maxImageId = 0;
        this.description = description;
        this.counter = counter;
        this.minImageUrl = minImageUrl;
        this.maxImageUrl = maxImageUrl;
        this.heatTime = heatTime;
    }

    public Wares(String name, int minImageId, int maxImageId) {
        this(name, "87", null, 0, Math.random() * 1, new Random().nextInt(6), minImageId, maxImageId, "");
    }

    public Wares(String name, String id,
                 String[] goodsTypes, int counter, double price,
                 int starValue, int minImageId,
                 int maxImageId, String description) {
        this.name = name;
        this.id = id;
        this.goodsTypes = goodsTypes;
        this.price = String.format("%.2f", price);
        this.starValue = starValue;
        this.minImageId = minImageId;
        this.maxImageId = maxImageId;
        this.description = description;
        this.counter = counter;
        minImageUrl = "";
        maxImageUrl = "";
        heatTime = 0;
    }

    public byte getHeatTime() {
        return (byte) heatTime;
    }

    public void subCounter() {
        if (counter > 0) {
            counter --;
            index ++;
        }
    }

    public String getMinImageUrl() {
        return minImageUrl;
    }

    public String getMaxImageUrl() {
        return maxImageUrl;
    }

    public int getCounter() {
        return counter;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getGoodsType() {
        return goodsTypes[index];
    }

    public String getPrice() {
        return price;
    }

    public int getStarValue() {
        return starValue;
    }

    public int getMinImageId() {
        return minImageId;
    }

    public int getMaxImageId() {
        return maxImageId;
    }

    @Override
    public String toString() {
        return "Wares{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", goodsTypes=" + Arrays.toString(goodsTypes) +
                ", price='" + price + '\'' +
                ", starValue=" + starValue +
                ", minImageId=" + minImageId +
                ", maxImageId=" + maxImageId +
                ", minImageUrl='" + minImageUrl + '\'' +
                ", maxImageUrl='" + maxImageUrl + '\'' +
                ", description='" + description + '\'' +
                ", heatTime=" + heatTime +
                ", counter=" + counter +
                ", index=" + index +
                '}';
    }
}
