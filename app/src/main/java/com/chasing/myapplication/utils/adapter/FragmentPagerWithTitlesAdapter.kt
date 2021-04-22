package com.chasing.base.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

/**
 * Created by chasing on 2020/12/30.
 */
class FragmentPagerWithTitlesAdapter(
    fm: FragmentManager,
    datas: List<Fragment>,
    private var mTitles: Array<String?>
) :
    BaseFragmentPagerAdapter(fm, datas) {

    override fun getPageTitle(position: Int): CharSequence? {
        return mTitles[position]
    }
}