package anim;

import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by xdhwwdz20112163.com on 2018/3/22.
 */

public class ShakeAnimation extends Animation {

    private float mDistance;
    private float mSpeed;

    public ShakeAnimation(float distance, float speed) {
        super();
        mDistance = distance;
        mSpeed = speed;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {

        float d = (float) Math.sin(interpolatedTime * mSpeed) * mDistance;
        t.getMatrix().setTranslate(d, 0);
    }
}
