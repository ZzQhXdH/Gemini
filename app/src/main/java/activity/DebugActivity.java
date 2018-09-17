package activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.support.v4.content.LocalBroadcastManager;


import android.support.v4.widget.CircularProgressDrawable;
import android.support.v7.app.AppCompatActivity;

import android.widget.ArrayAdapter;
import android.widget.Button;

import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;


import com.jf.geminjava.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android_serialport_api.SerialPort;
import application.GeminiApplication;
import protocol.AbstractProtocol;
import protocol.AbstractResult;
import protocol.AbstractStrongProtocol;
import protocol.QueryStatusProtocol;
import service.SerialPortService;
import task.HandlerTaskManager;
import task.QueryDeviceStateTask;
import util.Custom;
import util.Logger;
import util.SerialPortManager;

/**
 * Created by xdhwwdz20112163.com on 2018/3/20.
 */

public class DebugActivity extends AppCompatActivity {

    private static final String TAG = DebugActivity.class.getSimpleName();

    private LocalBroadcastManager mBroadcastManager;
    private ScrollView mScrollView;
    private TextView mTextViewMessage;
    private Toast mToast;

    private Map<String, OnResultListener> mResultListenerMap;

    private boolean shipmentFlag = false; // 出货完成标志位
    private boolean shipmentingFlag = false; // 正在出货标志位
    private boolean isOneColShipmentFlag = false; // 是否处于单列出货模式
    private boolean isAllShipmentFlag = false; // 是否处于全部出货模式

    private int curShipmentCol = 1; // 待出货的列
    private int curShipmentRow = 1; // 待出货的行

    private Button mButtonQuit;
    private Button mButtonQuitAll;

    private ToggleButton mToggleButtonQuWuMen; // 取物门电机
    private Button mButtonInit; // 初始化
    private ToggleButton mToggleButtonWaiShengJiang; // 外升降电机
    private ToggleButton mToggleButtonBaoWenMen; // 保温门
    private ToggleButton mToggleButtonTuiHuo; // 推货电机
    private ToggleButton mToggleButtonWaiTuoPan; // 外托盘推货
    private Button mButtonWaiTuoPanHongWai; // 外托盘红外检测
    private Button mButtonTiaoMaJianCe; // 条码检测
    private Button mButtonStatusCheck; // 状态检测
    private Button mButtonHuoCanMenStatusCheck; // 货仓门状态检测

    private Spinner mSpinnerShipmentCol; // 单个出货的列
    private Spinner mSpinnerShipmentRow; // 单个出货的行
    private EditText mEditTextHeatTime; // 出货的时候加热时间设置
    private Button mButtonShipment; // 出货按钮

    private Spinner mSpinnerCol; // 整列出货的列
    private Button mButtonColAll; // 整列出货按钮
    private Button mButtonColAllPause; // 整列出货暂停
    private Button mButtonColAllResume; // 整列出货继续

    private Button mButtonWeiBoTest; // 微波测试
    private EditText mEditTextWeiboTime; // 微波测试


    private Spinner mSpinnerCol1; // 第一列
    private Spinner mSpinnerCol2;
    private Spinner mSpinnerCol3;
    private Spinner mSpinnerCol4;
    private Button mButtonColSet; // 行列参数设定

    private Spinner mSpinnerStack; // 货仓号
    private Button mButtonQueryTemperature;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);
        vInitUi();
        vInitEvent();

        mBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(AbstractResult.TYPE_BAR_CODE);
        filter.addAction(AbstractResult.TYPE_ACK);
        filter.addAction(AbstractResult.TYPE_FAULT);
        filter.addAction(AbstractResult.TYPE_GOODS_TYPE);
        filter.addAction(AbstractResult.TYPE_IRDA);
        filter.addAction(AbstractResult.TYPE_NCK);
        filter.addAction(AbstractResult.TYPE_INIT_STATUS);
        filter.addAction(AbstractResult.TYPE_SHIPMENT_STATUS);
        filter.addAction(AbstractResult.TYPE_OTHER_STATUS);
        filter.addAction(AbstractResult.TYPE_DOOR_STATUS);
        filter.addAction(AbstractResult.TYPE_TEMPERATURE);
        mBroadcastManager.registerReceiver(mBroadcastReceiver, filter);

        mResultListenerMap = new HashMap<>();
        mResultListenerMap.put(AbstractResult.TYPE_ACK, onAckResult);
        mResultListenerMap.put(AbstractResult.TYPE_NCK, onNckResult);
        mResultListenerMap.put(AbstractResult.TYPE_BAR_CODE, onBarcodeResult);
        mResultListenerMap.put(AbstractResult.TYPE_FAULT, onFaultResult);
        mResultListenerMap.put(AbstractResult.TYPE_INIT_STATUS, onInitStatusResult);
        mResultListenerMap.put(AbstractResult.TYPE_SHIPMENT_STATUS, onShipmentStatusResult);
        mResultListenerMap.put(AbstractResult.TYPE_DOOR_STATUS, onDoorStatusResult);
        mResultListenerMap.put(AbstractResult.TYPE_OTHER_STATUS, onOtherResult);
        mResultListenerMap.put(AbstractResult.TYPE_IRDA, onIrdaResult);
        mResultListenerMap.put(AbstractResult.TYPE_GOODS_TYPE, onGoodsTypeResult);
        mResultListenerMap.put(AbstractResult.TYPE_TEMPERATURE, onTemperatureResult);
    }

    @Override
    protected void onDestroy() {
        mBroadcastManager.unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();
    }

    private void gotoHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra(HomeActivity.QUIT, true);
        startActivity(intent);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            final String action = intent.getAction();
            final byte[] bytes = intent.getByteArrayExtra(SerialPortService.RECV);
            mResultListenerMap.get(action).onResult(bytes);
        }
    };

    private void vInitEvent() {

        /**
         * 退出调试界面
         */
        mButtonQuit.setOnClickListener(view -> {
            finish();
        });

        /**
         * 退出整个系统
         */
        mButtonQuitAll.setOnClickListener(view -> {
            gotoHomeActivity();
        });

        /**
         * 长按清空
         */
        mTextViewMessage.setOnLongClickListener(view -> {
            mTextViewMessage.setText("");
            return true;
        });

        /**
         * 取物门控制
         */
        mToggleButtonQuWuMen.setOnCheckedChangeListener((btn, check) -> {
            if (check) {
                HandlerTaskManager.instance().getHandler().post(() -> AbstractProtocol.vWriteCommand4((byte) 2));
            } else {
                HandlerTaskManager.instance().getHandler().post(() -> AbstractProtocol.vWriteCommand4((byte) 1));
            }
        });

        /**
         * 初始化控制
         */
        mButtonInit.setOnClickListener(view -> {
            HandlerTaskManager.instance().getHandler().post(() -> AbstractProtocol.vWriteCommand14());
        });

        /**
         * 外升降直流电机控制
         */
        mToggleButtonWaiShengJiang.setOnCheckedChangeListener((view, check) -> {
            if (check) { // 上升
                HandlerTaskManager.instance().getHandler().post(() -> AbstractProtocol.vWriteCommand1((byte) 1));
            } else { // 下降
                HandlerTaskManager.instance().getHandler().post(() -> AbstractProtocol.vWriteCommand1((byte) 2));
            }
        });


        /**
         * 保温门控制
         */
        mToggleButtonBaoWenMen.setOnCheckedChangeListener((view, check) -> {
            if (check) { // 打开
                HandlerTaskManager.instance().getHandler().post(() -> AbstractProtocol.vWriteCommand2((byte) 2));
            } else { // 关闭
                HandlerTaskManager.instance().getHandler().post(() -> AbstractProtocol.vWriteCommand2((byte) 1));
            }
        });

        /**
         * 推货电机控制
         */
        mToggleButtonTuiHuo.setOnCheckedChangeListener((view, check) -> {
            if (check) { // 推进
                HandlerTaskManager.instance().getHandler().post(() -> AbstractProtocol.vWriteCommand3((byte) 1));
            } else { // 后退
                HandlerTaskManager.instance().getHandler().post(() -> AbstractProtocol.vWriteCommand3((byte) 2));
            }
        });

        /**
         * 外托盘
         */
        mToggleButtonWaiTuoPan.setOnCheckedChangeListener((view, check) -> {
            if (check) { // 外托盘推货推进
                HandlerTaskManager.instance().getHandler().post(() -> AbstractProtocol.vWriteCommand5((byte) 1));
            } else { // 回收
                HandlerTaskManager.instance().getHandler().post(() -> AbstractProtocol.vWriteCommand5((byte) 2));
            }
        });

        /**
         * 红外检测
         */
        mButtonWaiTuoPanHongWai.setOnClickListener(view -> {
            HandlerTaskManager.instance().getHandler().post(() -> AbstractProtocol.vWriteCommand6());
        });

        /**
         * 条码测试
         */
        mButtonTiaoMaJianCe.setOnClickListener(view -> {
            HandlerTaskManager.instance().getHandler().post(() -> AbstractProtocol.vWriteCommand9());
        });

        /**
         * 状态查询
         */
        mButtonStatusCheck.setOnClickListener(view -> {
            HandlerTaskManager.instance().getHandler().post(() -> AbstractProtocol.vWriteCommand12());
        });

        /**
         * 货仓门状态查询
         */
        mButtonHuoCanMenStatusCheck.setOnClickListener(view -> {
            HandlerTaskManager.instance().getHandler().post(() -> AbstractProtocol.vWriteCommand13());
        });

        /**
         * 单个出货
         */
        mButtonShipment.setOnClickListener(view -> {
            int col = mSpinnerShipmentCol.getSelectedItemPosition() + 1;
            int row = mSpinnerShipmentRow.getSelectedItemPosition() + 1;
            int heat = Integer.parseInt(mEditTextHeatTime.getText().toString());
            curShipmentCol = col;
            curShipmentRow = row;
            HandlerTaskManager.instance().getHandler().post(() -> {
                AbstractProtocol.vWriteCommand15((byte) col, (byte) row, (byte) heat);
            });
            shipmentFlag = false;
            shipmentingFlag = true;
            isOneColShipmentFlag = false;
            GeminiApplication.getMainHandler().postDelayed(() -> QueryDeviceStateTask.start(), 500);
        });

        /**
         * 整列出货
         */
        mButtonColAll.setOnClickListener(view -> {
            curShipmentCol = mSpinnerCol.getSelectedItemPosition() + 1;
            curShipmentRow = 1;
            HandlerTaskManager.instance().getHandler().post(() -> {
                AbstractProtocol.vWriteCommand15((byte) curShipmentCol, (byte) curShipmentRow, (byte) 0);
            });
            shipmentFlag = false;
            shipmentingFlag = true;
            isOneColShipmentFlag = true;
            GeminiApplication.getMainHandler().postDelayed(() -> QueryDeviceStateTask.start(), 500);
        });

        /**
         * 整列出货暂停
         */
        mButtonColAllPause.setOnClickListener(view -> {
            isOneColShipmentFlag = false;
            GeminiApplication.getMainHandler().postDelayed(() -> QueryDeviceStateTask.stop(), 500);
            mTextViewMessage.append("当前货道出货完毕以后就会暂停\r\n");
            mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
        });

        /**
         * 整列出货继续
         */
        mButtonColAllResume.setOnClickListener(view -> {

            isOneColShipmentFlag = true;
            if (isOneColShipmentFlag && (curShipmentRow <= 22)) { // 如果需要单列出货

                curShipmentRow ++;
                if (curShipmentRow > 22) { // 全部出货完成
                    isOneColShipmentFlag = false;
                    showToast("单列出货完成");
                    return;
                }
                HandlerTaskManager.instance().getHandler().post(() -> {
                    AbstractProtocol.vWriteCommand15((byte) curShipmentCol, (byte) curShipmentRow, (byte) 0);
                });
                GeminiApplication.getMainHandler().postDelayed(() -> QueryDeviceStateTask.start(), 500);
            } else {
                isOneColShipmentFlag = false;
                showToast("本列出货已经完成");
            }
        });

        mButtonWeiBoTest.setOnClickListener(view -> {
            String s = mEditTextWeiboTime.getText().toString();
            if (s.isEmpty()) {
                showToast("请输入微波加热时间");
                return;
            }
            byte time = Byte.parseByte(s);
            HandlerTaskManager.instance().getHandler().post(() -> {
               AbstractProtocol.vWriteCommand7(time);
            });
        });

        mButtonColSet.setOnClickListener(view -> {
            int index = mSpinnerCol1.getSelectedItemPosition();
            byte h1 = 0;
            switch (index) {
                case 0: h1 = 0x3C; break;
                case 1: h1 = 0x50; break;
                case 2: h1 = 0x64; break;
            }
            index = mSpinnerCol2.getSelectedItemPosition();
            byte h2 = 0;
            switch (index) {
                case 0: h2 = 0x3C; break;
                case 1: h2 = 0x50; break;
                case 2: h2 = 0x64; break;
            }
            index = mSpinnerCol3.getSelectedItemPosition();
            byte h3 = 0;
            switch (index) {
                case 0: h3 = 0x3C; break;
                case 1: h3 = 0x50; break;
                case 2: h3 = 0x64; break;
            }
            index = mSpinnerCol4.getSelectedItemPosition();
            byte h4 = 0;
            switch (index) {
                case 0: h4 = 0x3C; break;
                case 1: h4 = 0x50; break;
                case 2: h4 = 0x64; break;
            }
            final byte col1 = h1;
            final byte col2 = h2;
            final byte col3 = h3;
            final byte col4 = h4;
            HandlerTaskManager.instance().getHandler().post(() -> {
                AbstractProtocol.vWriteCommand16(col1, col2, col3, col4);
            });
        });


        mButtonQueryTemperature.setOnClickListener((view) -> {

            int p = mSpinnerStack.getSelectedItemPosition();
            AbstractStrongProtocol protocol = new QueryStatusProtocol.Build()
                    .setId((byte) (p + 1))
                    .setQueryCode((byte) 2) // 温度查询
                    .build();
            HandlerTaskManager.instance().getHandler().post(() -> {
                byte[] bytes = protocol.getByteArray();
                SerialPortManager.instance().getSerialPort().xWrite(bytes);
            });
        });

    }

    private void vInitUi() {

        mButtonQuit = findViewById(R.id.id_debug_button_quit);
        mButtonQuitAll = findViewById(R.id.id_debug_button_quit_all);

        mScrollView = findViewById(R.id.id_debug_scroll_view);
        mTextViewMessage = findViewById(R.id.id_debug_text_view_message);

        mToggleButtonQuWuMen = findViewById(R.id.id_debug_toggle_button_quwumen);
        mButtonInit = findViewById(R.id.id_debug_toggle_button_initialize);
        mToggleButtonWaiShengJiang = findViewById(R.id.id_debug_toggle_button_wai_sheng_jiang);
        mToggleButtonBaoWenMen = findViewById(R.id.id_debug_toggle_button_bao_wen_men);
        mToggleButtonTuiHuo = findViewById(R.id.id_debug_toggle_button_tui_huo);

        mToggleButtonWaiTuoPan = findViewById(R.id.id_debug_toggle_button_wai_tuo_pan);
        mButtonWaiTuoPanHongWai = findViewById(R.id.id_debug_button_wai_tuo_pan_hong_wai);
        mButtonTiaoMaJianCe = findViewById(R.id.id_debug_button_tiao_ma_check);
        mButtonStatusCheck = findViewById(R.id.id_debug_button_status_check);
        mButtonHuoCanMenStatusCheck = findViewById(R.id.id_debug_button_huo_can_men_status);

        mSpinnerShipmentCol = findViewById(R.id.id_debug_shipment_spinner_col);
        mSpinnerShipmentRow = findViewById(R.id.id_debug_shipment_spinner_row);
        mEditTextHeatTime = findViewById(R.id.id_debug_shipment_edit_text_heat_time);
        mButtonShipment = findViewById(R.id.id_debug_shipment_button);

        mSpinnerCol = findViewById(R.id.id_debug_spinner_col);
        mButtonColAll = findViewById(R.id.id_debug_shipment_button_all_col);
        mButtonColAllPause = findViewById(R.id.id_debug_button_one_shipment_pause);
        mButtonColAllResume = findViewById(R.id.id_debug_button_one_shipment_resume);

        mButtonWeiBoTest = findViewById(R.id.id_debug_button_weibo_test);
        mEditTextWeiboTime = findViewById(R.id.id_debug_edit_text_weibo_time);

        mButtonQueryTemperature = findViewById(R.id.id_debug_button_query_temperature);
        mSpinnerStack = findViewById(R.id.id_debug_spinner_stack);

        initOneShipment();  // 初始化单个出货的Spinner

        initColShipment(); // 初始化整列出货的Spinner

        initRowColArg(); // 行列出货参数设定

        initStackSpinner();
    }

    private void initStackSpinner() {

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.array_spinner_stack, R.layout.item_spinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        mSpinnerStack.setAdapter(adapter);
    }

    private void initRowColArg() { // 行列出货参数设定

        mSpinnerCol1 = findViewById(R.id.id_debug_spinner_col_1);
        mSpinnerCol2 = findViewById(R.id.id_debug_spinner_col_2);
        mSpinnerCol3 = findViewById(R.id.id_debug_spinner_col_3);
        mSpinnerCol4 = findViewById(R.id.id_debug_spinner_col_4);
        mButtonColSet = findViewById(R.id.id_debug_button_row_col_arg_set);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.array_spinner_row_col_arg, R.layout.item_spinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        mSpinnerCol1.setAdapter(adapter);

        adapter = ArrayAdapter.createFromResource(this, R.array.array_spinner_row_col_arg, R.layout.item_spinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        mSpinnerCol2.setAdapter(adapter);

        adapter = ArrayAdapter.createFromResource(this, R.array.array_spinner_row_col_arg, R.layout.item_spinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        mSpinnerCol3.setAdapter(adapter);

        adapter = ArrayAdapter.createFromResource(this, R.array.array_spinner_row_col_arg, R.layout.item_spinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        mSpinnerCol4.setAdapter(adapter);
    }

    private void initOneShipment() {

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.array_shipment_col_array, R.layout.item_spinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        mSpinnerShipmentCol.setAdapter(adapter);

        adapter = ArrayAdapter.createFromResource(this, R.array.array_shipment_row_array, R.layout.item_spinner);
        mSpinnerShipmentRow.setAdapter(adapter);
    }

    private void initColShipment() {

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.array_shipment_col_array, R.layout.item_spinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        mSpinnerCol.setAdapter(adapter);
    }

    private void showToast(final String msg) {

        if (mToast == null) {
            mToast = Toast.makeText(this, msg, Toast.LENGTH_LONG);
        } else {
            mToast.setDuration(Toast.LENGTH_LONG);
            mToast.setText(msg);
        }
        mToast.show();
    }

    private void appendReceiverMessage(final byte[] bytes, final String extMsg) {

        final String msg = Custom.fromByteArrayExtNo(bytes);
        StringBuilder builder = new StringBuilder("串口接收:");
        builder.append(msg);
        builder.append("(");
        builder.append(extMsg);
        builder.append(")\r\n");
        mTextViewMessage.append(builder.toString());
        mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
    }

    private OnResultListener onAckResult = (byte[] bytes) -> {

        appendReceiverMessage(bytes, "ACK");
        if (shipmentFlag) {

            showToast(String.format("%d-%d:出货成功", curShipmentCol, curShipmentRow));
            shipmentFlag = false;
            GeminiApplication.getMainHandler().postDelayed(() -> QueryDeviceStateTask.stop(), 500);
            if (isAllShipmentFlag && (curShipmentRow <= 22) && (curShipmentCol <= 4)) { // 全部出货模式

                curShipmentRow++;
                if (curShipmentRow > 22) {
                    curShipmentCol ++;
                    curShipmentRow = 1;
                }

                if (curShipmentCol > 4) {
                    curShipmentRow = 1;
                    curShipmentCol = 1;
                    showToast("全部出货完成");
                    isAllShipmentFlag = false;
                    return;
                }

                HandlerTaskManager.instance().getHandler().post(() -> {
                    AbstractProtocol.vWriteCommand15((byte) curShipmentCol, (byte) curShipmentRow, (byte) 0);
                });
                GeminiApplication.getMainHandler().postDelayed(() -> QueryDeviceStateTask.start(), 500);
            }

            if (isOneColShipmentFlag && (curShipmentRow <= 22)) { // 如果需要单列出货

                curShipmentRow ++;
                if (curShipmentRow > 22) { // 全部出货完成
                    isOneColShipmentFlag = false;
                    showToast("单列出货完成");
                    return;
                }
                HandlerTaskManager.instance().getHandler().post(() -> {
                    AbstractProtocol.vWriteCommand15((byte) curShipmentCol, (byte) curShipmentRow, (byte) 0);
                });
                GeminiApplication.getMainHandler().postDelayed(() -> QueryDeviceStateTask.start(), 500);
            }
        }
    };

    private OnResultListener onNckResult = (byte[] bytes) -> {
        appendReceiverMessage(bytes, "NCK");
        if (shipmentingFlag) {
            showToast(String.format("%d-%d:出货失败", curShipmentCol, curShipmentRow));
            shipmentingFlag = false;
            isOneColShipmentFlag = false;
            isAllShipmentFlag = false;
            shipmentFlag = false;
            GeminiApplication.getMainHandler().postDelayed(() -> QueryDeviceStateTask.stop(), 500);
        }
    };

    private OnResultListener onBarcodeResult = (byte[] bytes) -> {
        AbstractResult.BarCode result = new AbstractResult.BarCode(bytes);
        StringBuilder builder = new StringBuilder(result.getGoodsType());
        builder.append(":");
        builder.append(result.getBarcode());
        builder.append("\r\n");
        mTextViewMessage.append(builder.toString());
        mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
    };

    private OnResultListener onInitStatusResult = (byte[] bytes) -> {
        AbstractResult.InitStatus result = new AbstractResult.InitStatus(bytes);
        StringBuilder sb = new StringBuilder(result.getStatus());
        sb.append("\r\n");
        mTextViewMessage.append(sb.toString());
        mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
    };

    private OnResultListener onShipmentStatusResult = (byte[] bytes) -> {
        AbstractResult.ShipmentStatus result = new AbstractResult.ShipmentStatus(bytes);
        final String msg = String.format("%d-%d:%s\r\n", curShipmentCol, curShipmentRow, result.getStatus());
        mTextViewMessage.append(msg);
        mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
        if (result.getStatusCode() == (byte) 0x10) {
            shipmentFlag = true;
        }
    };

    private OnResultListener onDoorStatusResult = (byte[] bytes) -> {
        AbstractResult.DoorStatus result = new AbstractResult.DoorStatus(bytes);
        mTextViewMessage.append(result.getStatus() + "\r\n");
        mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
    };

    private OnResultListener onOtherResult = (byte[] bytes) -> {

        AbstractResult.OtherStatus result = new AbstractResult.OtherStatus(bytes);

        if (result.isBusy()) {
            mTextViewMessage.append("设备正忙\r\n");
        } else {
            mTextViewMessage.append(String.format("%d-%d:无货\r\n", curShipmentCol, curShipmentRow));
            showToast(String.format("%d-%d:出货失败", curShipmentCol, curShipmentRow));

            if ((!isAllShipmentFlag) || (!isOneColShipmentFlag)) {
                GeminiApplication.getMainHandler().postDelayed(() -> QueryDeviceStateTask.stop(), 500);
            }

            if (isAllShipmentFlag && (curShipmentRow <= 22) && (curShipmentCol <= 4)) { // 全部出货模式

                curShipmentRow++;
                if (curShipmentRow > 22) {
                    curShipmentCol ++;
                    curShipmentRow = 1;
                }

                if (curShipmentCol > 4) {
                    curShipmentRow = 1;
                    curShipmentCol = 1;
                    showToast("全部出货完成");
                    isAllShipmentFlag = false;
                    GeminiApplication.getMainHandler().postDelayed(() -> QueryDeviceStateTask.stop(), 500);
                    return;
                }

                HandlerTaskManager.instance().getHandler().post(() -> {
                    AbstractProtocol.vWriteCommand15((byte) curShipmentCol, (byte) curShipmentRow, (byte) 0);
                });
               // GeminiApplication.getMainHandler().postDelayed(() -> QueryDeviceStateTask.start(), 500);
            }

            if (isOneColShipmentFlag && (curShipmentRow <= 22)) { // 如果需要单列出货

                curShipmentRow ++;
                if (curShipmentRow > 22) { // 全部出货完成
                    isOneColShipmentFlag = false;
                    showToast("单列出货完成");
                    GeminiApplication.getMainHandler().postDelayed(() -> QueryDeviceStateTask.stop(), 500);
                    return;
                }
                HandlerTaskManager.instance().getHandler().post(() -> {
                    AbstractProtocol.vWriteCommand15((byte) curShipmentCol, (byte) curShipmentRow, (byte) 0);
                });
               // GeminiApplication.getMainHandler().postDelayed(() -> QueryDeviceStateTask.start(), 500);
            }
        }
        mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
    };

    private OnResultListener onFaultResult = (byte[] bytes) -> {
        AbstractResult.Fault result = new AbstractResult.Fault(bytes);
        try {
            mTextViewMessage.append(result.toJsonString());
        } catch (Exception e) {
            e.printStackTrace();
            mTextViewMessage.append("异常数据解析错误");
        }
        mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
        shipmentingFlag = false;
        shipmentFlag = false;
        isOneColShipmentFlag = false;
        GeminiApplication.getMainHandler().postDelayed(() -> QueryDeviceStateTask.stop(), 500);
    };

    private OnResultListener onGoodsTypeResult = (byte[] bytes) -> {
        AbstractResult.GoodsType result = new AbstractResult.GoodsType(bytes);
        mTextViewMessage.append(result.getInfo());
        mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
    };

    private OnResultListener onIrdaResult = (byte[] bytes) -> {
        AbstractResult.Irda result = new AbstractResult.Irda(bytes);
        if (result.isError()) {
            mTextViewMessage.append("红外检测异常");
        } else {
            mTextViewMessage.append("红外检测正常");
        }
        mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
    };

    private OnResultListener onTemperatureResult = (byte[] bytes) -> {

        AbstractResult.Temperature temp = new AbstractResult.Temperature(bytes);
        String name;
        if (temp.getId() == 0x01) {
            name = "左仓";
        } else {
            name = "右仓";
        }
        mTextViewMessage.append(name + "温度=" + temp.getTemperature());
        mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
    };

    private interface OnResultListener {

        void onResult(byte[] bytes);
    }

}
