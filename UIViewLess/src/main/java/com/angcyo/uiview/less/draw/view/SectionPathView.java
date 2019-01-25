package com.angcyo.uiview.less.draw.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import com.angcyo.uiview.less.draw.RSectionPathDraw;
import com.angcyo.uiview.less.resources.AnimUtil;

/**
 * Email:angcyo@126.com
 *
 * @author angcyo
 * @date 2019/01/10
 */
public class SectionPathView extends BaseDrawView<RSectionPathDraw> {

    RSectionPathDraw sectionPathDraw;

    ValueAnimator valueAnimator;

    public SectionPathView(Context context) {
        this(context, null);
    }

    public SectionPathView(Context context, AttributeSet attrs) {
        super(context, attrs);
        sectionPathDraw = baseDraw;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        valueAnimator = AnimUtil.valueAnimator(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //animation.getAnimatedValue();
                sectionPathDraw.setProgress((int) (animation.getAnimatedFraction() * 100));
            }
        });
        //valueAnimator.setRepeatMode(ValueAnimator.REVERSE);
//        valueAnimator.setRepeatCount(0);
        valueAnimator.setDuration(2000);
        valueAnimator.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        valueAnimator.cancel();
    }
}