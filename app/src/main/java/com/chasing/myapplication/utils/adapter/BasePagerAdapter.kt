package com.chasing.base.adapter

import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.viewpager.widget.PagerAdapter

/**
 * Created by chasing on 2020/12/30.
 */
class BasePagerAdapter(var data: List<View>) : PagerAdapter() {
    override fun getCount(): Int {
        return data.size
    }

    override fun getItemPosition(@NonNull `object`: Any): Int {
        return POSITION_NONE
    }

    override fun isViewFromObject(@NonNull view: View, @NonNull `object`: Any): Boolean {
        return view === `object`
    }

    @NonNull
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        container.addView(data[position], 0) //添加页卡
        return data[position]
    }

    override fun destroyItem(@NonNull container: ViewGroup, position: Int, @NonNull `object`: Any) {
        if (position < data.size) container.removeView(data[position]) //删除页卡
    }
}