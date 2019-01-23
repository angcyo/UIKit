package com.angcyo.uiview.less.widget

import android.content.Context
import android.support.v7.widget.AppCompatSpinner
import android.util.AttributeSet
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SpinnerAdapter
import com.angcyo.uiview.less.R
import com.angcyo.uiview.less.utils.Reflect

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/01/11
 */
open class RSpinner(context: Context, attributeSet: AttributeSet? = null) : AppCompatSpinner(context, attributeSet) {

    private var dropDownLayout = -1

    init {
        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.RSpinner)
        dropDownLayout = typedArray.getResourceId(R.styleable.RSpinner_r_spinner_drop_down_layout, dropDownLayout)

//        if (dropDownLayout > 0) {
//            attributeSet?.let {
//                for (i in 0 until it.attributeCount) {
//                    if ("entries" == it.getAttributeName(i)) {
//                        val resourceValue = it.getAttributeResourceValue(i, -1)
//                        if (resourceValue > 0) {
//                            val entries = context.resources.getStringArray(resourceValue)
//                            val adapter = ArrayAdapter<CharSequence>(context, dropDownLayout, entries)
//                            //adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
//                            setAdapter(adapter)
//                        }
//                    }
//                }
//            }
//        }

        typedArray.recycle()

        initAdapterStyle(adapter)
    }

    fun setDropDownLayout(layoutId: Int) {
        dropDownLayout = layoutId
        initAdapterStyle(adapter)
    }

    override fun setAdapter(adapter: SpinnerAdapter?) {
        super.setAdapter(adapter)
        initAdapterStyle(adapter)
    }

    fun initAdapterStyle(adapter: SpinnerAdapter?) {
        if (adapter is ArrayAdapter<*>) {
            if (dropDownLayout != -1) {
                adapter.setDropDownViewResource(dropDownLayout)
                Reflect.setFieldValue(adapter, "mResource", dropDownLayout)
            }
        }
        if (adapter is RSpinnerAdapter<*>) {
            if (dropDownLayout != -1) {
                adapter.thisResource = dropDownLayout
            }
        }
    }

    /**设置数据源*/
    fun setStrings(list: List<String>) {
        val adapter = ArrayAdapter<CharSequence>(context, dropDownLayout, list)
        setAdapter(adapter)
    }
}

abstract class OnSpinnerItemSelected : AdapterView.OnItemSelectedListener {
    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

    }
}