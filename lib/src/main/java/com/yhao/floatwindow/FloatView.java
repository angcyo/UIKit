package com.yhao.floatwindow;

import android.view.Gravity;
import android.view.View;

/**
 * Created by yhao on 17-11-14.
 * https://github.com/yhaolpz
 */

public abstract class FloatView {

    abstract void setSize(int width, int height);

    abstract void setView(View view);

    abstract void setGravity(int gravity, int xOffset, int yOffset);

    abstract void init();

    abstract void dismiss();

    int getGravity() {
        return Gravity.TOP | Gravity.LEFT;
    }

    void updateXY(int x, int y) {
    }

    void updateX(int x) {
    }

    void updateY(int y) {
    }

    public void updateFlags(int flags) {
    }

    int getX() {
        return 0;
    }

    int getY() {
        return 0;
    }
}
