package com.chasing.myapplication

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.chasing.base.adapter.BaseRecyclerAdapter
import com.chasing.myapplication.ui.main.FirstAdapter
import com.chasing.myapplication.ui.main.SecondAdapter
import com.chasing.myapplication.utils.layout_manager.ConnectLayoutManagerHelper
import com.chasing.myapplication.utils.layout_manager.GalleryLayoutManager
import com.chasing.myapplication.utils.layout_manager.GallerySnapHelper

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val list = mutableListOf<String>()
        list.add("1")
        list.add("1")
        list.add("1")
        list.add("1")
        list.add("1")
        list.add("1")
        list.add("1")
        list.add("1")
        list.add("1")
        list.add("1")
        list.add("1")

        val firstAdapter = FirstAdapter(this)
        val rcvFirst: RecyclerView = findViewById(R.id.first_rcv)
        val galleryFirst = GalleryLayoutManager()
        rcvFirst.layoutManager = galleryFirst
        rcvFirst.adapter = firstAdapter
        val gallerySnapHelperFirst = GallerySnapHelper()
        gallerySnapHelperFirst.attachToRecyclerView(rcvFirst)

        val secondAdapter = SecondAdapter(this)
        val rcvSecond: RecyclerView = findViewById(R.id.second_rcv)
        rcvSecond.layoutParams.height =
            (resources.getDimensionPixelSize(R.dimen.dp_96) * 1.16f).toInt()
        val gallerySecond = GalleryLayoutManager()
        gallerySecond.setFirstInterval(resources.getDimensionPixelSize(R.dimen.fab_margin))
        gallerySecond.setInterval(resources.getDimensionPixelSize(R.dimen.appbar_padding_top))
        gallerySecond.setScale(1.116f)
        rcvSecond.layoutManager = gallerySecond
        rcvSecond.adapter = secondAdapter
        val gallerySnapHelperSecond = GallerySnapHelper()
        gallerySnapHelperSecond.attachToRecyclerView(rcvSecond)

        secondAdapter.setItemClickListener(object : BaseRecyclerAdapter.ItemClickListener{
            override fun onItemClick(itemView: View, position: Int) {
                gallerySecond.smoothScrollToPosition(rcvSecond, null, position)
            }
        })
        galleryFirst.addOnPageChangeListener(object : GalleryLayoutManager.OnPageChangeListener {
            override fun onPageSelected(position: Int) {
                Toast.makeText(baseContext, "onPageSelected $position", Toast.LENGTH_SHORT).show()
            }

            override fun onPageSelectedWhenScroll(position: Int) {

            }
        })

        ConnectLayoutManagerHelper().bindRecyclerView(rcvFirst, rcvSecond)

        firstAdapter.insertAll(list)
        secondAdapter.insertAll(list)
    }
}