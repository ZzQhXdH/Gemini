package fragment;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.jf.geminjava.R;

import popup.WaitPopupWindow;

/**
 * Created by xdhwwdz20112163.com on 2018/3/27.
 */

public class BarcodeFragment extends Fragment {

    private static final String TAG = BarcodeFragment.class.getSimpleName();

    private ListViewAdapter mListViewAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_bar_code, null);
        mListViewAdapter = new ListViewAdapter(view);
        return view;
    }


    private static class ListViewAdapter extends BaseAdapter
            implements ListView.OnItemClickListener,
            View.OnClickListener {

        private ListView mListView;
        private FloatingActionButton mActionButton;
        private ObjectAnimator mObjectAnimator;

        public ListViewAdapter(View view) {

            mListView = view.findViewById(R.id.id_fragment_bar_code_list_view);
            mActionButton = view.findViewById(R.id.id_fragment_bar_code_action_button);
            mListView.setAdapter(this);
            mListView.setOnItemClickListener(this);
            mActionButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            WaitPopupWindow.instance().show(mActionButton);
            mObjectAnimator = ObjectAnimator.ofFloat(mActionButton, "rotation", 0, 360);
            mObjectAnimator.setRepeatMode(ValueAnimator.RESTART);
            mObjectAnimator.setRepeatCount(ValueAnimator.INFINITE);
            mObjectAnimator.setDuration(1000);
            mObjectAnimator.setInterpolator(new LinearInterpolator());
            mObjectAnimator.start();
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        }

        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return null;
        }
    }

}
