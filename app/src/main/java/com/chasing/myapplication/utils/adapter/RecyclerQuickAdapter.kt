package com.chasing.base.adapter

import android.content.Context
import android.view.View

/**
 * Created by chasing on 2020/12/30.
 */
abstract class RecyclerQuickAdapter<T> : BaseRecyclerAdapter<T, RecyclerViewHelper> {

    constructor(context: Context, layoutId: Int) : super(context, layoutId, null) {
    }

    constructor(
        context: Context,
        multitemTypeSupport: BaseRecyclerMultitemTypeSupport<T>
    ) : super(context, null, multitemTypeSupport) {
    }

    override fun getAdapterHelper(position: Int, itemView: View): RecyclerViewHelper {
        return RecyclerViewHelper(context, itemView, position)
    }
}