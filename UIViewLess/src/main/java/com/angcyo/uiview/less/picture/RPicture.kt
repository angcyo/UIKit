package com.angcyo.uiview.less.picture

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import com.angcyo.lib.L
import com.angcyo.uiview.less.R
import com.angcyo.uiview.less.utils.RUtils
import com.angcyo.uiview.less.utils.Root
import com.luck.picture.lib.PictureSelectionModel
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureConfig.MULTIPLE
import com.luck.picture.lib.config.PictureConfig.SINGLE
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import java.io.File

/**
 * https://github.com/LuckSiege/PictureSelector
 * 2.2.3(未更新)
 *
 *
 * Email:angcyo@126.com
 *
 * @author angcyo
 * @date 2018/12/15
 */
object RPicture {

    fun build(activity: Activity): Builder {
        return Builder(activity)
    }

    /**
     * 开始选择媒体
     * */
    fun selector(activity: Activity, config: PictureSelectionModel.() -> Unit = {}) {
        build(activity).apply {
            ofImage()
            singleMode()
            config(config)
            doIt()
        }
    }

    fun start(activity: Activity, selectionMedia: List<LocalMedia>? /*已经选中的媒体*/) {
        build(activity).setSelectionMedia(selectionMedia).circle().doIt()
    }

    /**
     * 打开图片浏览
     */
    fun previewPicture(activity: Activity, position: Int, medias: List<LocalMedia>) {
        PictureSelector.create(activity).themeStyle(R.style.picture_default_style).openExternalPreview(position, medias)
    }

    /**
     * 视频
     */
    fun previewVideo(activity: Activity, path: String?) {
        PictureSelector.create(activity).externalPictureVideo(path)
    }

    /**
     * 音频
     */
    fun previewAudio(activity: Activity, path: String?) {
        PictureSelector.create(activity).externalPictureAudio(path)
    }

    fun baseEnterAnim(activity: Activity?) {
        activity?.overridePendingTransition(R.anim.base_tran_to_bottom_enter, 0)
    }

    fun baseExitAnim(activity: Activity?) {
        activity?.overridePendingTransition(0, R.anim.base_tran_to_bottom_exit)
    }

    /**
     * 获取返回值
     *
     *
     * 返回null, 表示被取消
     */
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): List<LocalMedia>? {
        var result: List<LocalMedia>? = null
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                PictureConfig.CHOOSE_REQUEST ->
                    // 图片选择结果回调
                    result = PictureSelector.obtainMultipleResult(data)
                // 例如 LocalMedia 里面返回三种path
                // 1.media.getPath(); 为原图path
                // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
                // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                // 如果裁剪并压缩了，已取压缩路径为准，因为是先裁剪后压缩的
                else -> {
                }
            }
        } else {
            when (requestCode) {
                PictureConfig.CHOOSE_REQUEST ->
                    //
                    L.w("图片选择, 已被取消.")
                else -> {
                }
            }
        }
        if (RUtils.isListEmpty(result)) {
            return result
        }
        val builder = StringBuilder("图片选择结果返回:")
        for (i in result!!.indices) {
            val media = result[i]
            builder.append("\n")
            builder.append(i)
            builder.append("->原始->")
            builder.append(media.path)
            builder.append(" ")
            builder.append(RUtils.formatFileSize(File(media.path).length()))
            builder.append(" ")
            builder.append(media.selectorType)
            builder.append(" ")
            builder.append(media.pictureType)

            if (media.isCut) {
                builder.append("\n   剪切->")
                builder.append(media.cutPath)
                builder.append(" ")
                builder.append(RUtils.formatFileSize(File(media.cutPath).length()))
            }

            if (media.isCompressed) {
                builder.append("\n   压缩->")
                builder.append(media.compressPath)
                builder.append(" ")
                builder.append(RUtils.formatFileSize(File(media.compressPath).length()))
            }
            builder.append("\n")
        }
        L.w(builder.toString())
        return result
    }

    class Builder(internal var activity: Activity) {

        /**
         * 已经选中的媒体
         */
        internal var selectionMedia: List<LocalMedia>? = null

        var selectionModel: PictureSelectionModel
            internal set

        init {

            // 进入相册 以下是例子：不需要的api可以不写
            selectionModel = PictureSelector.create(activity)
                // 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                .openGallery(PictureMimeType.ofImage())
                // 主题样式设置 具体参考 values/styles   用法：R.style.picture.white.style
                .theme(R.style.picture_default_style)
                // 最大图片选择数量
                .maxSelectNum(9)
                // 最小选择数量
                .minSelectNum(1)
                // 每行显示个数
                .imageSpanCount(4)
                // 多选 or 单选
                .selectionMode(MULTIPLE)
                // 是否可预览图片
                .previewImage(true)
                // 是否可预览视频
                .previewVideo(true)
                // 是否可播放音频
                .enablePreviewAudio(true)
                // 是否显示拍照按钮
                .isCamera(true)
                // 图片列表点击 缩放效果 默认true
                .isZoomAnim(true)
                //.imageFormat(PictureMimeType.PNG)// 拍照保存图片格式后缀,默认jpeg
                //.setOutputCameraPath("/CustomPath")// 自定义拍照保存路径
                // 是否裁剪
                .enableCrop(false)
                // 是否压缩
                .compress(true)
                //同步true或异步false 压缩 默认同步
                .synOrAsy(true)
                //压缩图片保存地址
                .compressSavePath(Root.getAppExternalFolder("LuBan"))
                //.sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                // glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                .glideOverride(160, 160)
                // 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                .withAspectRatio(1, 1)
                // 是否显示uCrop工具栏，默认不显示
                .hideBottomControls(true)
                // 是否显示gif图片
                .isGif(true)
                // 裁剪框是否可拖拽
                .freeStyleCropEnabled(false)
                // 是否圆形裁剪
                .circleDimmedLayer(false)
                // 是否显示裁剪矩形边框 圆形裁剪时建议设为false
                .showCropFrame(true)
                // 是否显示裁剪矩形网格 圆形裁剪时建议设为false
                .showCropGrid(true)
                // 是否开启点击声音
                .openClickSound(false)
                // 是否传入已选图片
                .selectionMedia(selectionMedia)
                // 是否可拖动裁剪框(固定)
                .isDragFrame(false)
                //.videoMaxSecond(15)
                //.videoMinSecond(10)
                //.previewEggs(false)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中)
                //.cropCompressQuality(90)// 裁剪压缩质量 默认100
                .minimumCompressSize(100)// 小于100kb的图片不压缩
                //.cropWH()// 裁剪宽高比，设置如果大于图片本身宽高则无效
                // 裁剪是否可旋转图片
                .rotateEnabled(true)
                // 裁剪是否可放大缩小图片
                .scaleEnabled(true)
            //.videoQuality()// 视频录制质量 0 or 1
            //.videoSecond()//显示多少秒以内的视频or音频也可适用
            //.recordVideoSecond()//录制视频秒数 默认60s
            //.forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
        }


        /**
         * 选中媒体
         */
        fun setSelectionMedia(selectionMedia: List<LocalMedia>?): Builder {
            this.selectionMedia = selectionMedia
            selectionModel.selectionMedia(selectionMedia)
            return this
        }

        fun ofImage(): Builder {
            selectionModel.mimeType(PictureMimeType.ofImage())
            return this
        }

        fun ofAll(): Builder {
            selectionModel.mimeType(PictureMimeType.ofAll())
            return this
        }

        fun ofVideo(): Builder {
            selectionModel.mimeType(PictureMimeType.ofVideo())
            return this
        }

        fun ofAudio(): Builder {
            selectionModel.mimeType(PictureMimeType.ofAudio())
            return this
        }

        fun circle(): Builder {
            // 是否圆形裁剪
            selectionModel.circleDimmedLayer(true)
                // 是否显示裁剪矩形边框 圆形裁剪时建议设为false
                .showCropFrame(false)
                // 是否显示裁剪矩形网格 圆形裁剪时建议设为false
                .showCropGrid(false)
            return this
        }

        fun rect(): Builder {
            // 是否圆形裁剪
            selectionModel.circleDimmedLayer(false)
                // 是否显示裁剪矩形边框 圆形裁剪时建议设为false
                .showCropFrame(true)
                // 是否显示裁剪矩形网格 圆形裁剪时建议设为false
                .showCropGrid(true)
            return this
        }

        fun maxSelectNum(num: Int): Builder {
            selectionModel.maxSelectNum(num)
            return this
        }

        fun minSelectNum(num: Int): Builder {
            selectionModel.minSelectNum(num)
            return this
        }

        /**
         * 单选or多选, 默认是多选
         */
        fun selectionMode(mode: Int): Builder {
            selectionModel.selectionMode(mode)
            return this
        }

        fun multipleMode(): Builder {
            selectionMode(MULTIPLE)
            return this
        }

        fun singleMode(): Builder {
            selectionMode(SINGLE)
            return this
        }

        fun compress(value: Boolean): Builder {
            selectionModel.compress(value)
            return this
        }

        fun enableCrop(value: Boolean): Builder {
            selectionModel.enableCrop(value)
            return this
        }

        /**
         * 原生配置方法回调
         * */
        fun config(config: PictureSelectionModel.() -> Unit = {}): Builder {
            selectionModel.config()
            return this
        }

        fun doIt() {
            selectionModel.forResult()
        }

    }
}


fun PictureSelectionModel.ofImage() {
    mimeType(PictureMimeType.ofImage())
}

fun PictureSelectionModel.ofAll() {
    mimeType(PictureMimeType.ofAll())
}

fun PictureSelectionModel.ofVideo() {
    mimeType(PictureMimeType.ofVideo())
}

fun PictureSelectionModel.ofAudio() {
    mimeType(PictureMimeType.ofAudio())
}

fun PictureSelectionModel.multipleMode() {
    selectionMode(MULTIPLE)
}

fun PictureSelectionModel.singleMode() {
    selectionMode(SINGLE)
}

