package activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import android.view.animation.LinearInterpolator;

import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import com.jf.geminjava.R;
import com.youth.banner.Banner;
import com.youth.banner.BannerScroller;
import com.youth.banner.Transformer;
import com.youth.banner.loader.ImageLoader;
import com.youth.banner.view.BannerViewPager;


import java.io.File;
import java.util.ArrayList;
import java.util.List;


import android_serialport_api.SerialPortFinder;
import application.GeminiApplication;
import data.Wares;

import popup.BootInitPopupWindow;
import popup.DebugMainPopupWindow;
import popup.InitializePopupWindow;
import popup.MaintainDebugPopupWindow;
import popup.MaintainPopupWindow;
import popup.OrderPopupWindow;
import popup.PayPopupWindow;
import popup.ShipmentPopupWindow;
import popup.TakeFoodPopupWindow;


import popup.WaitPopupWindow;
import receiver.InitializeReceiver;
import service.SerialPortService;

import task.HandlerTaskManager;
import task.QueryDeviceStateTask;
import task.RefundTask;
import task.TaskManager;
import task.UpdateBarcodeTask;
import util.Custom;
import util.DeviceManager;
import util.GoodsTypeManager;
import util.Http;
import util.Logger;
import util.PayManager;
import util.WaresManager;
import view.ScheduleView;
import view.WaitView;


/**
 * Created by xdhwwdz20112163.com on 2018/3/16.
 */

public class HomeActivity extends AppCompatActivity {

    public static final String QUIT = "home.activity.quit";
    public static final String UPDATE_DATA = "receiver.update.data";

    private static final int REQ_CODE = 2;
    private static final String TAG = HomeActivity.class.getSimpleName();

    private boolean mFirstFlag = true;

    private VideoManager mVideoManager;
    private Button mButtonOrder; // 订单取餐
    private RecyclerViewManager mRecyclerViewManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //  hideButtonKey();
        requestPermission();

        mButtonOrder = findViewById(R.id.id_home_button_order);

        mRecyclerViewManager = new RecyclerViewManager(getDelegate()); // ReceiverView管理器 负责处理RecyclerView的业务逻辑

        mButtonOrder.setOnClickListener(v -> TakeFoodPopupWindow.instance().show(mButtonOrder, null));

        mButtonOrder.setOnLongClickListener(v -> onLongClick());

        SerialPortService.start(GeminiApplication.getAppContext());

        //InitializeReceiver.registerReceiver();
        registerBroadcast();

        String[] paths = new SerialPortFinder().getAllDevicesPath();
        for (String path : paths)
        {
            Logger.instance().d("调试", path);
        }
    }
// 00:18:05:0b:d5:cd

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus & mFirstFlag) {
            mFirstFlag = false;
          //  InitializePopupWindow.instance().show(mButtonOrder);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mVideoManager.next();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mVideoManager.stop();
    }

    @Override
    protected void onDestroy() {
        unregisterBroadcast();
        SerialPortService.stop();
        HandlerTaskManager.instance().quit();
        super.onDestroy();
        System.exit(0);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        boolean flag = intent.getBooleanExtra(QUIT, false);
        if (flag) {
            finish();
        }
    }

    private boolean onLongClick() {

        MaintainPopupWindow.instance().show(mButtonOrder, mOnMaintainPassListener);
        return true;
    }

    private void hideButtonKey() {

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        window.setAttributes(params);
    }

    private void onUpdate() {

        HandlerTaskManager.instance().getHandler().post(new UpdateBarcodeTask());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            finish();
            return;
        }
        mVideoManager = new VideoManager(getDelegate());
    }

    /**
     * RecyclerView 数据更新广播
     */
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            final String action = intent.getAction();
            switch (action) {
                case UPDATE_DATA:
                    mRecyclerViewManager.notifyDataSetChanged();
                    QueryDeviceStateTask.start();
                    break;
            }
        }
    };

    private void registerBroadcast() {

        IntentFilter filter = new IntentFilter();
        filter.addAction(UPDATE_DATA);
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, filter);
    }

    private void unregisterBroadcast() {

        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
    }

    private void requestPermission() {
        int res = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (res != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQ_CODE);
            return;
        }
        mVideoManager = new VideoManager(getDelegate());
    }

    // 密码输入界面事件回调
    private MaintainPopupWindow.OnMaintainPassListener mOnMaintainPassListener = new MaintainPopupWindow.OnMaintainPassListener() {
        @Override
        public void onPassSuccess() {
            MaintainDebugPopupWindow.instance().show(mButtonOrder, mOnDebugListener);
        }

        @Override
        public void onPassError() {
         //   MaintainDebugPopupWindow.instance().show(mButtonOrder, mOnDebugListener);
        }
    };

    // Debug界面按钮事件
    private MaintainDebugPopupWindow.OnDebugListener mOnDebugListener = new MaintainDebugPopupWindow.OnDebugListener() {

        @Override
        public void onClickReplenish() {
            Logger.instance().d(TAG, "补货事件");
            DebugMainPopupWindow.instance().show(mButtonOrder, v -> onUpdate());
        }

        @Override
        public void onClickDevice() {
            Logger.instance().d(TAG, "设备事件");
            InitializePopupWindow.instance().show(mButtonOrder);
        }

        @Override
        public void onClickFinish() {
            Intent intent = new Intent(HomeActivity.this, MaintainDebugActivity.class);
            startActivity(intent);
            Logger.instance().d(TAG, "完成事件");
        }

        @Override
        public void onClickDebug() {
            Logger.instance().d(TAG, "调试事件");
            Intent intent = new Intent(HomeActivity.this, DebugActivity.class);
            startActivity(intent);
        }
    };

    private static class VideoManager {

        private final VideoView mVideoView;
        private File[] mFiles;
        private int mNextIndex;

        public VideoManager(AppCompatDelegate delegate) {
            mVideoView = delegate.findViewById(R.id.id_home_video_view);
            mNextIndex = 0;
            mVideoView.setOnCompletionListener(mp -> next());
            TaskManager.instance().runTask(DiscoverTask);
        }

        public boolean isStart() {
            return mFiles != null && mFiles.length != 0;
        }

        public void next() {

            if (!isStart()) {
                return;
            }

            if (mNextIndex >= mFiles.length) {
                mNextIndex = 0;
            }
            mVideoView.setVideoPath(mFiles[mNextIndex].getPath());
            mVideoView.start();
            mNextIndex++;
        }

        private Handler mHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                next();
            }
        };

        private Runnable DiscoverTask = new Runnable() {

            @Override
            public void run() {
                mFiles = Custom.scanFiles("video", "mp4");
                if (mFiles != null && mFiles.length != 0) {
                    mHandler.sendEmptyMessage(0);
                    return;
                }
            }
        };

        public void stop() {
            if (!isStart()) {
                return;
            }
            mVideoView.pause();
        }

    }

    private interface OnItemClickListener {
        void onClick(int position);
    }

    private static class RecyclerViewManager extends RecyclerView.Adapter<RecyclerViewManager.ViewHolder>
            implements OnItemClickListener,
            OrderPopupWindow.OnPayListener,
            ValueAnimator.AnimatorUpdateListener,
            PayPopupWindow.PayStateListener,
            ShipmentPopupWindow.OnShipmentListener {

        private final RecyclerView mRecyclerView;
        private final Banner mBanner;

        public RecyclerViewManager(AppCompatDelegate delegate) {

            mBanner = delegate.findViewById(R.id.id_home_banner);
            mRecyclerView = delegate.findViewById(R.id.id_home_recycler_view);
          //  mMainImageView = delegate.findViewById(R.id.id_home_image_view);

            LinearLayoutManager manager = new LinearLayoutManager(mRecyclerView.getContext(),
                    LinearLayoutManager.HORIZONTAL, false);
            mRecyclerView.setLayoutManager(manager);
            mRecyclerView.setAdapter(this);

            // 设置RecyclerView的属性动画
            ValueAnimator animator = ValueAnimator.ofInt(1, 2);
            animator.setInterpolator(new LinearInterpolator());
            animator.setRepeatMode(ValueAnimator.RESTART);
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.addUpdateListener(this);
            animator.start();

          //  mMainImageView.setImageResource(R.drawable._back);

            List<Object> images = new ArrayList<>();
            images.add(Custom.resourceIdToUri(mBanner.getContext(), R.drawable._back));
            images.add(Custom.resourceIdToUri(mBanner.getContext(), R.drawable._back1));
            images.add(Custom.resourceIdToUri(mBanner.getContext(), R.drawable._back2));
            images.add(Custom.resourceIdToUri(mBanner.getContext(), R.drawable._back3));
            mBanner.setImages(images);
            mBanner.setDelayTime(5000);
            mBanner.setImageLoader(new ImageLoader() {
                @Override
                public void displayImage(Context context, Object path, ImageView imageView) {
                    imageView.setImageURI((Uri) path);
                }
            });
            mBanner.setBannerAnimation(Transformer.ScaleInOut);
            mBanner.isAutoPlay(true);
            mBanner.start();
        }

        @Override
        public void onShipmentError() {

            Logger.instance().d(TAG, "出货失败");
            TaskManager.instance().runTask(new RefundTask());
         //   mMainImageView.setImageResource(R.drawable._back);
        }

        @Override
        public void onShipmentSuccess() {
            Logger.instance().d(TAG, "出货成功");
            notifyDataSetChanged();
        //    mMainImageView.setImageResource(R.drawable._back);
        }

        @Override
        public void onShipmentTimeOut() {
            Logger.instance().d(TAG, "出货超时");
            TaskManager.instance().runTask(new RefundTask());
       //     mMainImageView.setImageResource(R.drawable._back);
        }

        @Override // 支付成功
        public void onPaySuccess() {
            Logger.instance().d(TAG, "支付成功");
            ShipmentPopupWindow.instance().show(mRecyclerView, this);
        }

        @Override // 取消支付
        public void onPayCancel() {
            Logger.instance().d(TAG, "支付取消");
        //    mMainImageView.setImageResource(R.drawable._back);
        }

        @Override // 支付超时
        public void onPayTimeOut() {
            Logger.instance().d(TAG, "支付超时");
       //     mMainImageView.setImageResource(R.drawable._back);
        }

        @Override // 获取二维码失败
        public void onQRCodeError() {
            Logger.instance().d(TAG, "获取二维码失败");
        }

        @Override // 支付网络错误
        public void onPayError() {
            Logger.instance().d(TAG, "支付网络错误");
        }

        @Override // 用户取消付款
        public void onPayCanael() {
            Logger.instance().d(TAG, "用户取消付款");
       //     mMainImageView.setImageResource(R.drawable._back);
        }

        @Override // 使用支付宝付款
        public void onAlipay() {
            PayPopupWindow.instance().show(mRecyclerView, this);
        }

        @Override // 使用微信付款
        public void onWechat() {
            PayPopupWindow.instance().show(mRecyclerView, this);
        }

        @Override // 动画更新事件
        public void onAnimationUpdate(ValueAnimator animation) {
            mRecyclerView.scrollBy(1, 0);
        }

        @Override // RecyclerView Item的Click事件
        public void onClick(int position) {

       //     mMainImageView.setImageResource(R.drawable.__back);
            WaresManager.instance().setSelectWaresIndex(position);
            OrderPopupWindow.instance().show(mRecyclerView, this);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            int count = WaresManager.instance().getWaresCount();
            Wares wares = WaresManager.instance().get(position % count);
            if (wares.getCounter() <= 0) {
                holder.setImageDisable(wares.getMinImageUrl());
                holder.setOnItemClickListener(null, position % count);
            } else {
                holder.setImage(wares.getMinImageUrl());
                holder.setOnItemClickListener(this, position % count);
            }

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_recycler_view, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public int getItemCount() {

            if (WaresManager.instance().getWaresCount() <= 0) {
                Logger.instance().d(TAG, "没有任何商品信息");
                return 0;
            }
            return Integer.MAX_VALUE;
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {

            private FrameLayout mFrameLayout = null;
            private ProgressBar mProgressBar = null;
            private TextView mTextView = null;

            public ViewHolder(View itemView) {
                super(itemView);
                mFrameLayout = itemView.findViewById(R.id.id_item_home_frame_layout);
                mProgressBar = itemView.findViewById(R.id.id_item_home_progress_bar);
                mTextView = itemView.findViewById(R.id.id_item_home_empty_text_view);
                mProgressBar.setVisibility(View.GONE);
            }

            public void setOnItemClickListener(final OnItemClickListener listener, final int position) {
                if (listener == null) {
                    mFrameLayout.setOnClickListener(null);
                    return;
                }
                mFrameLayout.setOnClickListener(v -> listener.onClick(position));
            }

            public void setImage(final String url) {

                mProgressBar.setVisibility(View.VISIBLE);
                mTextView.setVisibility(View.GONE);
                mFrameLayout.setBackgroundResource(R.drawable.black_background);
                Glide.with(mFrameLayout.getContext())
                        .load(url)
                        .asBitmap()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .skipMemoryCache(false)
                        .into(new SimpleTarget<Bitmap>(216, 172) {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                BitmapDrawable bitmapDrawable = new BitmapDrawable(resource);
                                LayerDrawable layerDrawable = Custom.getLayerDrawable(resource, 0x44000000);
                                Drawable drawable = Custom.getSelector(layerDrawable, bitmapDrawable);
                                mProgressBar.setVisibility(View.GONE);
                                mFrameLayout.setBackgroundDrawable(drawable);
                            }
                        });
            }

            public void setImageDisable(final String url) {

                mProgressBar.setVisibility(View.VISIBLE);
                mTextView.setVisibility(View.VISIBLE);
                mFrameLayout.setBackgroundResource(R.drawable.black_background);
                Glide.with(mFrameLayout.getContext())
                        .load(url)
                        .asBitmap()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .skipMemoryCache(false)
                        .into(new SimpleTarget<Bitmap>(216, 172) {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {

                                mProgressBar.setVisibility(View.GONE);
                                LayerDrawable layerDrawable = Custom.getLayerDrawable(resource, 0x44000000);
                                mFrameLayout.setBackgroundDrawable(layerDrawable);
                            }
                        });
            }

            public void setImage(int resId) {

                Bitmap bm = BitmapFactory.decodeResource(mFrameLayout.getResources(), resId);
                BitmapDrawable bitmapDrawable = new BitmapDrawable(bm);
                LayerDrawable layerDrawable = Custom.getLayerDrawable(bm, 0x44000000);
                Drawable drawable = Custom.getSelector(layerDrawable, bitmapDrawable);
                mFrameLayout.setBackgroundDrawable(drawable);
            }

            public void setImageDisable(int resId) {

                Bitmap bm = BitmapFactory.decodeResource(mFrameLayout.getResources(), resId);
                LayerDrawable layerDrawable = Custom.getLayerDrawable(bm, 0x44000000);
                mFrameLayout.setBackgroundDrawable(layerDrawable);
            }

        }

    }
}
