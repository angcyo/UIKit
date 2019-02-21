package com.angcyo.uiview.less.widget.pager;

import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * Viewpager 页面切换动画，只支持3.0以上版本
 * <p/>
 * [-∞，-1]完全不可见
 * [-1,  0]从不可见到完全可见
 * [0,1]从完全可见到不可见
 * [1,∞]完全不可见
 * <p/>
 * Created by doc on 15/1/6.
 */
public class FadeInOutPageTransformer implements ViewPager.PageTransformer {

    /**
     * @see ViewPager#setCurrentItem(int, boolean)
     */
    @Override
    public void transformPage(@NonNull View page, float position) {
        if (Math.abs(position) > 1 ||
                (page.getMeasuredWidth() == 0 && page.getMeasuredHeight() == 0)) {
            //smoothScroll为false时, 这个方法也会回调
            //但是此时, position 上一页和下一页都是负数.
            // 如果调用了page.setAlpha(0);那么界面就看不见东西了
            page.setAlpha(1);
        }
        //页码完全不可见
        else if (position < -1) {
            page.setAlpha(0);
        }
        //当前页, 负数从 -0.1 -0.2 -0.3 ... -1
        else if (position < 0) {
            page.setAlpha(1 + position);
        }
        //下一页, 正数从 1 0.9 0.8 0.7 ... 0
        else if (position < 1) {
            page.setAlpha(1 - position);
        } else {
            page.setAlpha(1);
        }
    }
}