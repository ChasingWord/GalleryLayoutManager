package com.chasing.base.adapter

import android.annotation.SuppressLint
import android.os.Parcelable
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Lifecycle
import androidx.viewpager.widget.PagerAdapter

/**
 * Created by chasing on 2020/12/30.
 */
abstract class FragmentPageAdapter(private var mFragmentManager: FragmentManager) : PagerAdapter() {
    private var mCurTransaction: FragmentTransaction? = null
    private var mCurrentPrimaryItem: Fragment? = null

    /**
     * Return the Fragment associated with a specified position.
     */
    abstract fun getItem(position: Int): Fragment?

    override fun startUpdate(container: ViewGroup) {
        if (container.id == View.NO_ID) {
            throw IllegalStateException(
                "ViewPager with adapter " + this
                        + " requires a view id"
            )
        }
    }

    @SuppressLint("CommitTransaction")
    @NonNull
    override fun instantiateItem(@NonNull container: ViewGroup, position: Int): Any {
        if (mCurTransaction == null) {
            mCurTransaction = mFragmentManager.beginTransaction()
        }
        val itemId = getItemId(position)

        // Do we already have this fragment?
        val name = makeFragmentName(container.id, itemId)
        var fragment: Fragment? = mFragmentManager.findFragmentByTag(name)
        if (fragment != null) {
            mCurTransaction!!.attach(fragment)
        } else {
            fragment = getItem(position)
            if (!fragment?.isAdded!!) mCurTransaction!!.add(
                container.id, fragment,
                makeFragmentName(container.id, itemId)
            ) else mCurTransaction!!.show(fragment)
        }
        if (fragment !== mCurrentPrimaryItem) {
            fragment.setMenuVisibility(false)
            mCurTransaction?.setMaxLifecycle(fragment, Lifecycle.State.STARTED)
        }
        return fragment
    }

    @SuppressLint("CommitTransaction")
    override fun destroyItem(@NonNull container: ViewGroup, position: Int, @NonNull `object`: Any) {
        if (mCurTransaction == null) {
            mCurTransaction = mFragmentManager.beginTransaction()
        }
        mCurTransaction!!.detach(`object` as Fragment)
    }

    override fun setPrimaryItem(
        @NonNull container: ViewGroup,
        position: Int,
        @NonNull `object`: Any
    ) {
        val fragment: Fragment? = `object` as Fragment?
        if (fragment !== mCurrentPrimaryItem) {
            if (mCurrentPrimaryItem != null) {
                mCurrentPrimaryItem!!.setMenuVisibility(false)
                mCurTransaction?.setMaxLifecycle(mCurrentPrimaryItem!!, Lifecycle.State.STARTED)
            }
            if (fragment != null) {
                fragment.setMenuVisibility(true)
                mCurTransaction?.setMaxLifecycle(fragment, Lifecycle.State.RESUMED)
            }
            mCurrentPrimaryItem = fragment
        }
    }

    override fun finishUpdate(@NonNull container: ViewGroup) {
        if (mCurTransaction != null) {
            mCurTransaction!!.commitAllowingStateLoss()
            mCurTransaction = null
        }
    }

    override fun isViewFromObject(@NonNull view: View, @NonNull `object`: Any): Boolean {
        return (`object` as Fragment).view === view
    }

    override fun saveState(): Parcelable? {
        return null
    }

    override fun restoreState(state: Parcelable?, loader: ClassLoader?) {}

    /**
     * Return a unique identifier for the item at the given position.
     *
     *
     *
     * The default implementation returns the given position.
     * Subclasses should override this method if the positions of items can change.
     *
     * @param position Position within this adapter
     * @return Unique identifier for the item at position
     */
    private fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun makeFragmentName(viewId: Int, id: Long): String {
        return "android:switcher:$viewId:$id"
    }
}