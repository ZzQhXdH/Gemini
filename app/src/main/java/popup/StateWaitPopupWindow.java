package popup;

import android.view.View;
import android.widget.PopupWindow;

/**
 * Created by xdhwwdz20112163.com on 2018/3/27.
 */

public class StateWaitPopupWindow {

    private View mMainView;
    private PopupWindow mPopupWindow;

    public static StateWaitPopupWindow instance() {
        return InlineClass.sInstance;
    }

    private static final class InlineClass {
        public static final StateWaitPopupWindow sInstance = new StateWaitPopupWindow();
    }
}
