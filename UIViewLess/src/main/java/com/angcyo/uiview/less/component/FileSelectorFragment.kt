package com.angcyo.uiview.less.component

import android.graphics.Color
import android.os.Bundle
import android.os.SystemClock
import android.support.v4.app.FragmentManager
import android.text.TextUtils
import android.text.format.Formatter
import android.view.Gravity
import android.view.View
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import com.angcyo.http.HttpSubscriber
import com.angcyo.http.Ok
import com.angcyo.http.RFunc
import com.angcyo.http.Rx
import com.angcyo.uiview.less.R
import com.angcyo.uiview.less.base.BaseFragment
import com.angcyo.uiview.less.base.helper.FragmentHelper
import com.angcyo.uiview.less.kotlin.dialog.menuDialog
import com.angcyo.uiview.less.kotlin.minValue
import com.angcyo.uiview.less.recycler.RBaseViewHolder
import com.angcyo.uiview.less.recycler.adapter.RBaseAdapter
import com.angcyo.uiview.less.recycler.widget.IShowState
import com.angcyo.uiview.less.skin.SkinHelper
import com.angcyo.uiview.less.utils.RSpan
import com.angcyo.uiview.less.utils.RUtils
import com.angcyo.uiview.less.utils.Root
import com.angcyo.uiview.less.utils.TopToast
import com.angcyo.uiview.less.utils.utilcode.utils.FileUtils
import com.angcyo.uiview.less.utils.utilcode.utils.MD5
import com.angcyo.uiview.less.widget.group.RLinearLayout
import com.angcyo.uiview.less.widget.group.TouchBackLayout
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/04/29
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
open class FileSelectorFragment : BaseFragment() {

    companion object {
        fun show(fragmentManager: FragmentManager?, config: FileSelectorConfig.() -> Unit) {
            FragmentHelper.build(fragmentManager)
                .showFragment(FileSelectorFragment().fileSelectorConfig(config))
                .defaultEnterAnim()
                .doIt()
        }
    }

    private var config = FileSelectorConfig()

    override fun getLayoutId(): Int {
        return R.layout.base_fragment_file_selector
    }

    /**获取上一层路径*/
    private fun getPrePath(): String = config.targetPath.substring(0, config.targetPath.lastIndexOf("/"))

    private var scrollView: HorizontalScrollView? = null

    private var selectorItemView: RLinearLayout? = null

    /**
     * 调用此方法, 配置参数
     * */
    fun fileSelectorConfig(config: FileSelectorConfig.() -> Unit): FileSelectorFragment {
        this.config.config()
        return this
    }

    override fun initBaseView(viewHolder: RBaseViewHolder, arguments: Bundle?, savedInstanceState: Bundle?) {
        super.initBaseView(viewHolder, arguments, savedInstanceState)

        //半屏效果
        viewHolder.v<TouchBackLayout>(R.id.base_touch_back_layout).apply {
            enableTouchBack = true
            offsetScrollTop = (resources.displayMetrics.heightPixels) / 2

            onTouchBackListener = object : TouchBackLayout.OnTouchBackListener {
                override fun onTouchBackListener(
                    layout: TouchBackLayout,
                    oldScrollY: Int,
                    scrollY: Int,
                    maxScrollY: Int
                ) {
                    if (scrollY >= maxScrollY) {
                        backFragment(false, false)
                    }
                }
            }
        }

        viewHolder.tv(R.id.current_file_path_view).text = config.targetPath
        viewHolder.view(R.id.base_selector_button).isEnabled = false

        scrollView = viewHolder.v(R.id.current_file_path_layout)

        /*上一个路径*/
        viewHolder.click(R.id.current_file_path_layout) {
            resetPath(getPrePath())
        }
        //选择按钮
        viewHolder.click(R.id.base_selector_button) {
            //T_.show(selectorFilePath)

            backFragment(false)

            config.onFileSelector?.invoke(File(config.selectorFilePath))
        }

        viewHolder.reV(R.id.base_recycler_view).apply {
            adapter = object : RBaseAdapter<FileItem>(mAttachContext) {
                override fun getItemLayoutId(viewType: Int): Int = R.layout.base_fragment_file_selector_item

                override fun onBindView(holder: RBaseViewHolder, position: Int, item: FileItem?) {
                    if (item == null) {
                        return
                    }
                    val bean = item.file
                    holder.tv(R.id.base_name_view).text = bean.name
                    holder.tv(R.id.base_time_view).text = formatTime(bean.lastModified())

                    //权限信息
                    holder.tv(R.id.base_auth_view).text = RSpan.get(if (bean.isDirectory) "d" else "-")
                        .append(if (bean.canExecute()) "e" else "-")
                        .append(if (bean.canRead()) "r" else "-")
                        .append(if (bean.canWrite()) "w" else "-")
                        .create()

                    holder.tv(R.id.base_md5_view).visibility = View.GONE

                    //文件/文件夹 提示信息
                    when {
                        bean.isDirectory -> {
                            holder.glideImgV(R.id.base_image_view).apply {
                                reset()
                                setImageResource(R.mipmap.base_floder_32)
                            }
                            if (bean.canRead()) {
                                holder.tv(R.id.base_tip_view).text = "${bean.listFiles().size} 项"
                            }
                        }
                        bean.isFile -> {
                            holder.glideImgV(R.id.base_image_view).apply {
                                reset()
                                if (item.imageType == Ok.ImageType.UNKNOWN) {
                                    setImageResource(R.mipmap.base_file_32)
                                } else {
                                    url = bean.absolutePath
                                }
                            }
                            if (bean.canRead()) {
                                holder.tv(R.id.base_tip_view).text = formatFileSize(bean.length())
                            }

                            //MD5值
                            if (config.showFileMd5) {
                                holder.tv(R.id.base_md5_view).visibility = View.VISIBLE
                                holder.tv(R.id.base_md5_view).text = item.fileMd5
                            }
                        }
                        else -> {
                            holder.glideImgV(R.id.base_image_view).apply {
                                reset()
                            }
                            if (bean.canRead()) {
                                holder.tv(R.id.base_tip_view).text = "unknown"
                            }
                        }
                    }

                    fun selectorItemView(itemView: RLinearLayout, selector: Boolean) {
                        if (selector) {
                            itemView.setRBackgroundDrawable(SkinHelper.getSkin().getThemeTranColor(0x30))
                        } else {
                            itemView.setRBackgroundDrawable(Color.TRANSPARENT)
                        }
                    }

                    val itemView: RLinearLayout = holder.itemView as RLinearLayout
                    selectorItemView(itemView, TextUtils.equals(config.selectorFilePath, bean.absolutePath))

                    if (bean.canRead()) {
                        //item 点击事件
                        holder.clickItem {
                            if (bean.isDirectory) {
                                resetPath(bean.absolutePath)
                            } else if (bean.isFile) {
                                setSelectorFilePath(bean.absolutePath)

                                selectorItemView?.let {
                                    selectorItemView(it, false)
                                }

                                selectorItemView = itemView
                                selectorItemView(itemView, true)
                            }
                        }
                    } else {
                        //没权限
                        holder.itemView.setOnClickListener(null)

                        holder.tv(R.id.base_tip_view).text = "无权限操作"
                    }

                    if (config.showFileMenu) {
                        if (bean.isFile) {
                            itemView.setOnLongClickListener {
                                val file = File(bean.absolutePath)
                                menuDialog {
                                    itemTextGravity = Gravity.CENTER_VERTICAL
                                    showItemDividers = LinearLayout.SHOW_DIVIDER_NONE
                                    items = mutableListOf("打开", "删除", "分享")
                                    itemIcons = mutableListOf(
                                        R.drawable.ic_file_open,
                                        R.drawable.ic_file_delete,
                                        R.drawable.ic_file_share
                                    )
                                    onItemClick = { _, index, _ ->
                                        when (index) {
                                            0 -> RUtils.openFile(mContext, file)
                                            1 -> {
                                                if (FileUtils.deleteFile(file)) {
                                                    resetPath(file.path, true)
                                                    setSelectorFilePath("")
                                                } else {
                                                    TopToast.show("删除失败!")
                                                }
                                            }
                                            2 -> RUtils.shareFile(mAttachContext, bean.absolutePath)
                                        }
                                        false
                                    }
                                }
                                false
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onFragmentFirstShow(bundle: Bundle?) {
        super.onFragmentFirstShow(bundle)
        loadPath(config.targetPath, 360)
    }

    private fun formatTime(time: Long): String = config.simpleFormat.format(time)

    private fun formatFileSize(size: Long): String = Formatter.formatFileSize(mAttachContext, size)

    private fun setSelectorFilePath(path: String) {
        config.selectorFilePath = path
        baseViewHolder.view(R.id.base_selector_button).isEnabled = File(config.selectorFilePath).exists()
    }

    private fun resetPath(path: String, force: Boolean = false) {
        //L.e("call: resetPath -> $path")
        config.targetPath = path
        if (!force && baseViewHolder.tv(R.id.current_file_path_view).text.toString() == config.targetPath) {
            return
        }
        loadPath(path)
    }

    private fun loadPath(path: String, delay: Long = 0L) {
        config.targetPath = path
        //baseViewHolder.view(R.id.base_selector_button).isEnabled = false
        baseViewHolder.tv(R.id.current_file_path_view).text = config.targetPath

        scrollView?.let {
            it.post {
                it.scrollTo((it.getChildAt(0).measuredWidth - it.measuredWidth).minValue(0), 0)
            }
        }

        baseViewHolder.reV(R.id.base_recycler_view).adapterRaw.setShowState(IShowState.NORMAL)
        getFileList(config.targetPath, delay) {
            baseViewHolder.reV(R.id.base_recycler_view).adapterRaw.resetData(it)
            if (it.isEmpty()) {
                baseViewHolder.reV(R.id.base_recycler_view).adapterRaw.setShowState(IShowState.EMPTY)
            }
        }
    }

    private fun getFileList(path: String, delay: Long = 0L, onFileList: (List<FileItem>) -> Unit) {
        Rx.base(object : RFunc<List<FileItem>>() {
            override fun onFuncCall(): List<FileItem> {
                val file = File(path)
                val result: List<FileItem> = if (file.exists() && file.isDirectory && file.canRead()) {
                    val list = file.listFiles().asList()
                    Collections.sort(list) { o1, o2 ->
                        when {
                            (o1.isDirectory && o2.isDirectory) || (o1.isFile && o2.isFile) -> o1.name.toLowerCase().compareTo(
                                o2.name.toLowerCase()
                            )
                            o2.isDirectory -> 1
                            o1.isDirectory -> -1
                            else -> o1.name.toLowerCase().compareTo(o2.name.toLowerCase())
                        }
                    }

                    val items = mutableListOf<FileItem>()
                    val fileList: List<File>

                    fileList = if (config.showHideFile) {
                        list
                    } else {
                        list.filter {
                            !it.isHidden
                        }
                    }
                    fileList.mapTo(items) {
                        FileItem(
                            it,
                            Ok.ImageType.of(Ok.ImageTypeUtil.getImageType(it)),
                            if (config.showFileMd5) MD5.getStreamMD5(it.absolutePath) else ""
                        )
                    }
                    items
                } else {
                    emptyList()
                }

                if (delay > 0) {
                    SystemClock.sleep(delay)
                }

                return result
            }

        }, object : HttpSubscriber<List<FileItem>>() {
            override fun onSucceed(bean: List<FileItem>) {
                super.onSucceed(bean)
                onFileList.invoke(bean)
            }
        })
    }
}

data class FileItem(val file: File, val imageType: Ok.ImageType, val fileMd5: String = "")

open class FileSelectorConfig {

    /**是否显示隐藏文件*/
    var showHideFile = false

    /**是否显示文件MD5值*/
    var showFileMd5 = false

    /**是否长按显示文件菜单*/
    var showFileMenu = false

    /**最根的目录*/
    var storageDirectory = Root.externalStorageDirectory()
        set(value) {
            if (File(value).exists()) {
                field = value
                targetPath = value
            }
        }

    /**目标路径*/
    var targetPath: String = storageDirectory
        set(value) {

            if (value.isNotEmpty() && value.startsWith(storageDirectory)) {
                val file = File(value)
                if (file.isDirectory) {
                    field = value
                } else if (file.isFile) {
                    field = file.parent
                }
            } else {
                field = storageDirectory
            }
        }

    val simpleFormat by lazy {
        SimpleDateFormat("yyyy/MM/dd", Locale.CHINA)
    }

    /*选中的文件*/
    var selectorFilePath: String = ""
    var onFileSelector: ((File) -> Unit)? = null

}