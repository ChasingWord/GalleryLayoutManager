package com.chasing.base.adapter

/**
 * Created by chasing on 2020/12/29.
 */
abstract class BaseRecyclerMultitemTypeSupport<T> {
    //BaseRecylerViewHolder getViewHolderHelper(int layoutId);
    abstract fun getItemViewType(t: T?, position: Int): Int

    abstract fun getLayoutId(type: Int): Int

    /**
     * 是否需要处理itemview
     * @param t
     * @param position
     * @return
     */
    abstract fun isConvert(t: T?, position: Int): Boolean
}