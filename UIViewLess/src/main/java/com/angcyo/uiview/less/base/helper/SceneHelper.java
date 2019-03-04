package com.angcyo.uiview.less.base.helper;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.transition.ChangeBounds;
import android.support.transition.Scene;
import android.support.transition.Transition;
import android.support.transition.TransitionManager;
import android.view.ViewGroup;
import com.angcyo.lib.L;

/**
 * Email:angcyo@126.com
 *
 * @author angcyo
 * @date 2019/03/04
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
public class SceneHelper {

    public static Builder build(@NonNull ViewGroup viewGroup) {
        return new Builder(viewGroup);
    }

    public static class Builder {
        protected ViewGroup viewGroup;

        protected Scene scene;
        protected Transition transition;

        protected Builder(ViewGroup viewGroup) {
            this.viewGroup = viewGroup;

            transition = new ChangeBounds();
        }

        public Builder setScene(Scene scene) {
            this.scene = scene;
            return this;
        }

        public Builder setSceneLayout(@LayoutRes int layoutId) {
            if (viewGroup != null) {
                setScene(Scene.getSceneForLayout(viewGroup, layoutId, viewGroup.getContext()));
            }
            return this;
        }

        public Builder setTransition(Transition transition) {
            this.transition = transition;
            return this;
        }

        public void doIt() {
            if (scene == null ||
                    viewGroup == null) {
                L.e("必要的参数不合法,请检查参数:"
                        + "\n1->scene:" + scene + (scene == null ? " ×" : " √")
                        + "\n2->viewGroup:" + viewGroup + (viewGroup == null ? " ×" : " √")
                );
                return;
            }
            TransitionManager.go(scene, transition);
        }
    }
}
