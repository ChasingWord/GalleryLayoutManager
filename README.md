# GalleryLayoutManager使RecyclerView支持类似Gallery的功能

![Image text](https://github.com/ChasingWord/GalleryLayoutManager/blob/main/screen_shot/1.jpg)

## 功能介绍
#### 类似Gallery，滑动选择之后第一个可见项为选中项，可设置进行scale放大进行标记选中<br>
val gallerySecond = GalleryLayoutManager()<br>
gallerySecond.setScale(1.116f)<br>
#### item之间的间距可进行直接设置：<br>
gallerySecond.setInterval(resources.getDimensionPixelSize(R.dimen.appbar_padding_top))<br>
#### 可以设置列表第一选中项停留之后与列表头部的间距<br>
gallerySecond.setFirstInterval(resources.getDimensionPixelSize(R.dimen.fab_margin))<br>
#### 可设置两个列表联动（注：两个列表均需使用GalleryLayoutManager）<br>
new ConnectLayoutManagerHelper().bindRecyclerView(mRcv, mRcvIndicator);<br>

### 使用方式：<br>
val rcvSecond: RecyclerView = findViewById(R.id.second_rcv)<br>
rcvSecond.layoutParams.height = (resources.getDimensionPixelSize(R.dimen.dp_96) * 1.16f).toInt()<br>
val gallerySecond = GalleryLayoutManager()<br>
gallerySecond.setFirstInterval(resources.getDimensionPixelSize(R.dimen.fab_margin))<br>
gallerySecond.setInterval(resources.getDimensionPixelSize(R.dimen.appbar_padding_top))<br>
gallerySecond.setScale(1.116f)<br>
rcvSecond.layoutManager = gallerySecond<br>
//需要设置GallerySnapHelper进行定位辅助<br>
GallerySnapHelper gallerySnapHelperIndicator = new GallerySnapHelper();<br>
gallerySnapHelperIndicator.attachToRecyclerView(mRcvIndicator);<br>

#### 待修复：<br>
如果需要进行Scale缩放，RecyclerView需要将高度设置为scale之后的高度，否则RecyclerView的高度会出现错乱<br>
如果不需要Scale缩放，RecyclerView的高度可设置为wrap_content<br>
