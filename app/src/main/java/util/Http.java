package util;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.Gravity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.jf.geminjava.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.CharArrayReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import application.GeminiApplication;

import data.Wares;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import protocol.AbstractResult;

/**
 * Created by xdhwwdz20112163.com on 2018/3/16.
 */

public class Http {

    /**
     * 服务器基URL
     */
    //public static final String BASE_URL = "http://10.1.2.39:89";
 //   public static final String BASE_URL = "http://101.132.150.93:8080";
    public static final String BASE_URL = "http://test.hontech-rdcenter.com:8080";
  //  public static final String BASE_URL = "http://wechat.hontech-rdcenter.com";
   // public static final String BASE_URL = "http://192.168.1.108:8080";
    /**
     * 支付相关接口的基础URL
     * 云服务器: http://101.132.150.93:8080
     */
  //  public static final String PAY_BASE_URL = "http://10.1.2.35:78";

    public static final String COULD_BASE_URL = BASE_URL;

    /**
     * 获取商品数据 1h获取一次
     * POST Json body方式传递参数
     *
     * @parameter macAddr : mac地址 example "11:22:33:44:55:66"
     * @parameter isFirstStart : 是否是首次索要数据? example true or false
     */
    public static final String WARES_URL = BASE_URL + "/bg-uc/goodssearch/goods-info/list.json";

    /**
     * 获取货道数据
     * 在URL后面拼接参数的方式传递参数
     *
     * @parameter example: ?macAddr="mac地址"
     */
    public static final String GOODS_URL = BASE_URL + "/bg-uc/cargoconfig/list.json";

    /**
     * 获取加热数据
     * 在URL后面拼接参数的方式传递参数
     *
     * @parameter example: ?macAddr="mac地址"
     */
    public static final String TEMPERATURE_SETTING_URL = BASE_URL + "/bg-uc/sbzt/sets.json";

    /**
     * 上传异常信息
     * 使用POST body方式传递参数
     *
     * @parameter macAddr: mac地址
     * @parameter rotate: 旋转步进电机的状态
     * @parameter getGoodsDoor1: 取物门的电机1状态
     * @parameter getGoodsDoor2: 取物门的电机2状态
     * @parameter getGoodsDoor3: 取物门的电机3状态
     * @parameter getGoodsDoor4: 取物门的电机4状态
     * @parameter getGoodsDoor5: 取物门的电机5状态
     * @parameter getGoodsDoor6: 取物门的电机6状态
     * @parameter getGoodsDoor7: 取物门的电机7状态
     * @parameter getGoodsDoor8: 取物门的电机8状态
     * @parameter getGoodsDoor9: 取物门的电机9状态
     * @parameter getGoodsDoor10: 取物门的电10机状态
     * @parameter trough: 槽型开关状态
     * @parameter temperatureSensor: DS18B20状态
     * @parameter save: 保留
     * @parameter doorStatus: 门状态
     * @parameter houseTemperature: 货仓温度
     * @parameter trouble: 是否有故障
     */
    public static final String FAULT_URL = BASE_URL + "/bg-uc/sbzt/receive.json";

    /**
     * 检查用户输入的管理账号和密码是否正确
     * 使用POST Json body方式传递参数
     *
     * @parameter emplCode: 账号
     * @patameter password: 密码
     * @return success: true or false
     */
    public static final String CHECK_PASSWORD_URL = BASE_URL + "/bg-uc/checkMain/main-info/check.json";

    /**
     * 获取补货清单
     * 使用POST Json body 方式传递参数
     *
     * @parameter macAddr: mac地址
     * @return 补货清单
     */
    public static final String GET_SHIPMENT_LIST_URL = BASE_URL + "/bg-uc/replenishment/detail-inter/data.json";

    /**
     * 查看补货清单
     * 使用POST Json body 方式传递参数
     *
     * @parameter macAddr: mac地址
     * @return 补货清单
     */
    public static final String GET_RAW_SHIPMENT_LIST_URL = BASE_URL + "/bg-uc/replenishment/detail-inter-original/data.json";

    /**
     * 报告某件商品已经售出
     * 使用POST Json body方式传递参数
     *
     * @parameter macAddr: mac地址
     * @parameter cargoData: 商品所在的货道
     */
    public static final String REPORT_WARES_URL = BASE_URL + "/bg-uc/replenishment/work-off/quantity.json";

    /**
     * 补货完成通知服务器
     * 使用POST Json body方式传递参数
     *
     * @parameter macAddr: mac地址
     * @return 最新的商品数据
     */
    public static final String SHIPMENT_FINISH_URL = BASE_URL + "/bg-uc/replenishment/client/replen-data/finish.json";

    /**
     * 获取支付宝二维码
     * 使用POST Form方式传递参数
     *
     * @parameter goods {
     * macAddress: mac地址
     * tradename: 商品名称
     * price: 价格
     * ID: id
     * }
     * @return 二维码Url链接 Json {"alipay":"二维码Url"}
     */
    public static final String ALIPAY_QRCODE_URL = COULD_BASE_URL + "/bg-uc/jf/com/pm/getAlipay.json";

    /**
     * 获取微信的二维码
     * 使用POST Form方式传递参数
     *
     * @parameter goods {
     * macAddress: mac地址
     * tradename: 商品名称
     * price: 价格
     * ID: id
     * }
     * @return 二维码Url链接 Json {"wechat":"二维码Url"}
     */
    public static final String WECHAT_QRCODE_URL = COULD_BASE_URL + "/bg-uc/jf/com/pm/getWechat.json";

    /**
     * 查询支付结果
     * 使用POST Form方式传递参数
     *
     * @parameter macaddress: mac地址
     * @parameter out_trade_no: 订单
     * @return macstate: 支付结果(paymentsuccess 表示支付成功)
     */
    public static final String QUERY_PAY_RESULT_URL = COULD_BASE_URL + "/bg-uc/jf/com/pm/searchMacState.json";

    /**
     * 汇报出货结果
     * 使用POST Form方式传递参数
     *
     * @parameter macaddress: mac地址
     * @parameter shipmentstate: 出货状态 shipmentsuccess or shipmentfail
     */
    public static final String REPORT_SHIPMENT_RESULT = COULD_BASE_URL + "/bg-uc/jf/com/pm/updateMacState.json";

    /**
     * 更新服务器状态
     * 使用POST Form方式传递参数
     *
     * @parameter out_trade_no: 订单号
     * @parameter state: 0
     */
    public static final String UPDATE_SERVER_STATUS_URL = COULD_BASE_URL + "/bg-uc/jf/com/pm/updateState.json";

    /**
     * 微信退款接口
     *
     * @parameter out_trade_no: 订单号
     */
    public static final String PAY_REFUND_URL = COULD_BASE_URL + "/bg-uc/jf/com/pm/returnrefundWechat.json";

    /**
     * 支付宝退款接口
     *
     * @parameter out_trade_no: 订单号
     */
    public static final String PAY_ALIPAY_REFUND_URL = COULD_BASE_URL + "/bg-uc/jf/com/pm/returnrefundAlipay.json";


    /***********************************双子座特有的URL************************************************/
    public static final String GEMIN_BASE = "http://192.168.1.120:8080";
    public static final String GEMIN_BASE2 = "http://192.168.1.103:8080";

    //public static final String BAR_CODE_UP_URL = GEMIN_BASE + "/bg-uc/replenishment/gemini-data/save.json";
    public static final String BAR_CODE_UP_URL = COULD_BASE_URL + "/bg-uc/replenishment/gemini-info/list.json";
    public static final String GEMIN_FAULT_URL = COULD_BASE_URL + "/bg-uc/sbzt/receivesz.json";

    private OkHttpClient mOkHttpClient = null;
    private QRCodeWriter mQRCodeWriter = null;
    private Map<EncodeHintType, Object> mHintTypeObjectMap = null;


    // 向后台上传异常数据
    public void vUpdateFaultData(AbstractResult.Fault fault) throws Exception {

        String content = fault.toJsonString();
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), content);
        Request request = new Request.Builder()
                .url(GEMIN_FAULT_URL)
                .post(body)
                .build();
        Call call = mOkHttpClient.newCall(request);
        Logger.instance().d("vUpdateFaultData", content);
        Logger.instance().d("vUpdateFaultData", call.execute().body().string());
    }

    @Deprecated
    public void vRefund() {

        final String order = PayManager.instance().getOrder();
        int type = PayManager.instance().getPayType();
        String url;
        if (type == PayManager.PAY_TYPE_WECHAT) {
            url = PAY_REFUND_URL;
        } else if (type == PayManager.PAY_TYPE_ALIPAY) {
            url = PAY_ALIPAY_REFUND_URL;
        } else {
            return;
        }

        RequestBody body = new MultipartBody.Builder()
                .addFormDataPart("out_trade_no", order)
                .setType(MultipartBody.FORM)
                .build();

        Request request = new Request.Builder()
                .post(body)
                .url(url)
                .build();

        Call call = mOkHttpClient.newCall(request);

        Response response;

        try {
            response = call.execute();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        try {
            Logger.instance().d("退款结果", response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String xPostForm(final String url, final RequestBody body) throws IOException {

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Call call = mOkHttpClient.newCall(request);
        Response response = call.execute();
        return response.body().string();
    }

    public void vUpdateServerPayStatus() {

        final String order = PayManager.instance().getOrder();
        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("out_trade_no", order)
                .addFormDataPart("state", "0")
                .build();

        Request request = new Request.Builder()
                .url(UPDATE_SERVER_STATUS_URL)
                .post(body)
                .build();

        Call call = mOkHttpClient.newCall(request);

        Response response;

        try {
            response = call.execute();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        try {
            Logger.instance().d("更新服务器状态", response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String xQueryPayState() {

        final String order = PayManager.instance().getOrder();
        final String mac = DeviceManager.instance().getMacAddress();

        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("out_trade_no", order)
                .addFormDataPart("macaddress", mac)
                .build();

        Request request = new Request.Builder()
                .post(body)
                .url(QUERY_PAY_RESULT_URL)
                .build();

        Call call = mOkHttpClient.newCall(request);

        Response response;

        try {
            response = call.execute();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        try {
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String vGet(final String url) {

        Request request = new Request.Builder().url(url).get().build();
        Call call = mOkHttpClient.newCall(request);
        Response response;
        try {
            response = call.execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String xPostBody(final String url, final String content) {

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), content);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        Call call = mOkHttpClient.newCall(request);
        Response response;
        try {
            response = call.execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void vSetPayStatus() {

        final String mac = DeviceManager.instance().getMacAddress();
        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("macaddress", mac)
                .addFormDataPart("shipmentstate", "shipmentfail")
                .build();

        Request request = new Request.Builder()
                .url(REPORT_SHIPMENT_RESULT)
                .post(body)
                .build();

        Call call = mOkHttpClient.newCall(request);

        Response response;

        try {
            response = call.execute();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        try {
            Logger.instance().d("设置支付状态", response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Bitmap getQRCodeBitmap() {

        vSetPayStatus();

        String url = WECHAT_QRCODE_URL;
        String jsonKey = "wechat";

        if (PayManager.instance().getPayType() == PayManager.PAY_TYPE_ALIPAY) {
            url = ALIPAY_QRCODE_URL;
            jsonKey = "alipay";
        }

        Wares wares = WaresManager.instance().getSelectWares();
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("macAddress", DeviceManager.instance().getMacAddress());
            jsonObject.put("tradename", wares.getName());
            jsonObject.put("price", wares.getPrice());
            jsonObject.put("ID", wares.getId());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("goods", jsonObject.toString())
                .build();

        Request request = new Request.Builder().url(url).post(body).build();

        Call call = mOkHttpClient.newCall(request);

        Response response;
        String resultContent;

        try {
            response = call.execute();
            resultContent = response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        String qrcode;

        try {
            jsonObject = new JSONObject(resultContent);
            String order = jsonObject.optString("order");
            PayManager.instance().setOrder(order);
            qrcode = jsonObject.optString(jsonKey);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return createQRCodeBitmap(qrcode);
    }

    public Bitmap createQRCodeBitmap(final String content) {

        Context context = GeminiApplication.getAppContext();
        int width = (int) context.getResources().getDimension(R.dimen.p1600);
        int height = (int) context.getResources().getDimension(R.dimen.p1600);
        BitMatrix matrix;
        try {
            matrix = mQRCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height, mHintTypeObjectMap);
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
        matrix = deleteWhite(matrix);
        width = matrix.getWidth();
        height = matrix.getHeight();
        int[] pixels = new int[width * height];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (matrix.get(j, i)) {
                    pixels[i * width + j] = 0x00;
                } else {
                    pixels[i * width + j] = 0xFFFFFFFF;
                }
            }
        }
        return Bitmap.createBitmap(pixels, width, height, Bitmap.Config.RGB_565);
    }

    private BitMatrix deleteWhite(BitMatrix matrix) {
        int[] rec = matrix.getEnclosingRectangle();
        int resWidth = rec[2] + 1;
        int resHeight = rec[3] + 1;

        BitMatrix resMatrix = new BitMatrix(resWidth, resHeight);
        resMatrix.clear();
        for (int i = 0; i < resWidth; i++) {
            for (int j = 0; j < resHeight; j++) {
                if (matrix.get(i + rec[0], j + rec[1]))
                    resMatrix.set(i, j);
            }
        }
        return resMatrix;
    }

    private Http() {

        mOkHttpClient = new OkHttpClient.Builder()
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS)
                .build();

        mQRCodeWriter = new QRCodeWriter();
        mHintTypeObjectMap = new HashMap<>();
        mHintTypeObjectMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        mHintTypeObjectMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        mHintTypeObjectMap.put(EncodeHintType.MARGIN, 4);
    }

    public static Http instance() {
        return InlineClass.sInstance;
    }

    private static class InlineClass {
        public static final Http sInstance = new Http();
    }
}
