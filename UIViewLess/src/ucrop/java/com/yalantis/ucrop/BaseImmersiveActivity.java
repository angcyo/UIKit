package com.yalantis.ucrop;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.angcyo.uiview.less.R;
import com.angcyo.uiview.less.base.Debug;
import com.luck.picture.lib.config.PictureSelectionConfig;
import com.luck.picture.lib.immersive.ImmersiveManage;
import com.luck.picture.lib.tools.AttrsUtils;

/**
 * Email:angcyo@126.com
 *
 * @author angcyo
 * @date 2019/05/18
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
public class BaseImmersiveActivity extends AppCompatActivity {

    protected boolean openWhiteStatusBar;
    protected int colorPrimary, colorPrimaryDark;

    @Override
    public boolean isImmersive() {
        return true;
    }

    /**
     * 具体沉浸的样式，可以根据需要自行修改状态栏和导航栏的颜色
     */
    public void immersive() {
        ImmersiveManage.immersiveAboveAPI23(this
                , colorPrimaryDark
                , colorPrimary
                , openWhiteStatusBar);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initConfig();
        if (isImmersive()) {
            immersive();
        }
    }

    /**
     * 获取配置参数
     */
    private void initConfig() {
        PictureSelectionConfig config = PictureSelectionConfig.getInstance();
        // 是否开启白色状态栏
        openWhiteStatusBar = AttrsUtils.getTypeValueBoolean(this,
                config.themeStyleId, R.attr.picture_statusFontColor);

        // 标题栏背景色
        colorPrimary = AttrsUtils.getTypeValueColor(this,
                config.themeStyleId, R.attr.colorPrimary);

        // 状态栏背景色
        colorPrimaryDark = AttrsUtils.getTypeValueColor(this,
                config.themeStyleId, R.attr.colorPrimaryDark);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            Debug.addDebugTextView(this);
        }
    }
}
