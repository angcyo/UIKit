package com.luck.picture.lib;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.angcyo.uiview.less.R;
import com.angcyo.uiview.less.utils.RUtils;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureSelectionConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.tools.ToastManage;

import java.util.List;

/**
 * Email:angcyo@126.com
 *
 * @author angcyo
 * @date 2019/07/03
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
public class ItemCheck {
    public static void check(@NonNull Context context, @NonNull List<LocalMedia> selectImages, @NonNull PictureSelectionConfig config) {
        String pictureType = selectImages.size() > 0 ? selectImages.get(0).getPictureType() : "";
        int maxSelectNum = config.maxSelectNum;
        String str;
        if (config.mimeType == PictureConfig.TYPE_ALL) {
            str = context.getString(R.string.picture_message_image_video_max_num, maxSelectNum);
        } else {
            boolean eqImg = pictureType.startsWith(PictureConfig.IMAGE);
            str = eqImg ? context.getString(R.string.picture_message_max_num, maxSelectNum)
                    : context.getString(R.string.picture_message_video_max_num, maxSelectNum);
        }
        ToastManage.s(context, str);
    }

    public static boolean checkFileSize(@NonNull Context context, @NonNull LocalMedia image, @NonNull PictureSelectionConfig config) {
        if (image.getFileSize() > config.maxFileSize) {
            String str = "不能选择超过 " + RUtils.formatFileSize(config.maxFileSize) + " 的图片或者视频";
            ToastManage.s(context, str);
            return false;
        }
        return true;
    }

    public static void checkShowFileSize(@NonNull TextView fileSizeView, @NonNull PictureSelectionConfig config, @NonNull LocalMedia image) {
        if (config.showFileSize) {
            //文件大小
            fileSizeView.setVisibility(View.VISIBLE);
            fileSizeView.setText(RUtils.formatFileSize(image.getFileSize()));
        } else {
            fileSizeView.setVisibility(View.GONE);
        }
    }

    public static void checkShowAllFileSize(@NonNull TextView allFileSizeView, @NonNull PictureSelectionConfig config, @NonNull List<LocalMedia> selectImages) {
        if (config.showFileSize) {
            //文件大小
            allFileSizeView.setVisibility(View.VISIBLE);
            long fileSize = 0;
            for (LocalMedia media : selectImages) {
                fileSize += media.getFileSize();
            }
            if (fileSize > 0) {
                allFileSizeView.setText(RUtils.formatFileSize(fileSize));
            } else {
                allFileSizeView.setText("");
            }
        } else {
            allFileSizeView.setVisibility(View.GONE);
        }
    }
}
