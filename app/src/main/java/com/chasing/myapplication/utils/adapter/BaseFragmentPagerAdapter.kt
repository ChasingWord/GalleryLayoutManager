package com.chasing.base.adapter

import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

/**
 * Created by chasing on 2020/12/30.
 */
open class BaseFragmentPagerAdapter(
    private var fm: FragmentManager,
    private var datas: List<Fragment>?
) : FragmentPageAdapter(fm) {
    private var mChildCount = 0
    private var mViewPagerId = 0

    fun setViewPagerId(viewPagerId: Int) {
        mViewPagerId = viewPagerId
    }

    fun setDatas(datas: List<Fragment>?) {
        mChildCount = count
        if (this.datas != null && this.datas!!.isNotEmpty() && mViewPagerId > 0) { //只有移除原来的Fragment，新添加的Fragment才能展示出来
            for (i in this.datas!!.indices) {
                val frgTag: String = makeFragmentName(mViewPagerId, i.toLong())
                val fragment: Fragment = fm.findFragmentByTag(frgTag) ?: continue
                val ft: FragmentTransaction = fm.beginTransaction()
                ft.remove(fragment)
                ft.commit()
                fm.executePendingTransactions()
            }
        }
        this.datas = datas
        notifyDataSetChanged()
    }

    override fun getItemPosition(@NonNull `object`: Any): Int {
        // 最简单解决 notifyDataSetChanged() 页面不刷新问题的方法
        if (mChildCount > 0) {
            mChildCount--
            return POSITION_NONE
        }
        return super.getItemPosition(`object`)
    }

    override fun getItem(position: Int): Fragment? {
        return datas!![position]
    }

    override fun getCount(): Int {
        return datas!!.size
    }
}