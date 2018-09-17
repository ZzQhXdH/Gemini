package activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.PluralsRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jf.geminjava.R;

import fragment.BarcodeFragment;

/**
 * Created by xdhwwdz20112163.com on 2018/3/26.
 */

public class MaintainDebugActivity extends AppCompatActivity {

    private static final String TAG = MaintainDebugActivity.class.getSimpleName();

    private ViewPagerAdapter mViewPagerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintain_debug);
        vInitUi();
    }

    private void vInitUi() {
        new ViewPagerAdapter(getSupportFragmentManager(), getDelegate());
    }

    private static final class ViewPagerAdapter extends FragmentPagerAdapter
            implements ViewPager.OnPageChangeListener, View.OnClickListener {

        private ViewPager mViewPager;
        private LinearLayout[] mLinearLayoutTab = new LinearLayout[3];

        public ViewPagerAdapter(FragmentManager fm, AppCompatDelegate delegate) {
            super(fm);
            mViewPager = delegate.findViewById(R.id.id_maintain_view_pager);
            mViewPager.setAdapter(this);
            mLinearLayoutTab[0] = delegate.findViewById(R.id.id_maintain_debug_tab1);
            mLinearLayoutTab[1] = delegate.findViewById(R.id.id_maintain_debug_tab2);
            mLinearLayoutTab[2] = delegate.findViewById(R.id.id_maintain_debug_tab3);
            mViewPager.setOffscreenPageLimit(3);
            for (int i = 0; i < 3; i ++) {
                setTab(i);
            }

        }

        private void setTab(int index) {

            TextView textView = mLinearLayoutTab[index].findViewById(R.id.id_include_text_view);
            ImageView imageView = mLinearLayoutTab[index].findViewById(R.id.id_include_image_view);
        }

        @Override
        public void onClick(View v) {

        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }

        @Override
        public Fragment getItem(int position) {
            return new BarcodeFragment();
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

}
