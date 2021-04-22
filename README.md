# GalleryLayoutManager使RecyclerView支持类似Gallery的功能

![Image text](https://github.com/ChasingWord/GalleryLayoutManager/blob/main/screen_shot/1.jpg)

### 使用方式：<br>
 val rcvSecond: RecyclerView = findViewById(R.id.second_rcv)<br>
rcvSecond.layoutParams.height = (resources.getDimensionPixelSize(R.dimen.dp_96) * 1.16f).toInt()<br>
val gallerySecond = GalleryLayoutManager()<br>
gallerySecond.setFirstInterval(resources.getDimensionPixelSize(R.dimen.fab_margin))<br>
gallerySecond.setInterval(resources.getDimensionPixelSize(R.dimen.appbar_padding_top))<br>
gallerySecond.setScale(1.116f)<br>
rcvSecond.layoutManager = gallerySecond<br>

#### 待修复：<br>
如果需要进行Scale缩放，RecyclerView需要将高度设置为scale之后的高度<br>
如果不需要Scale缩放，RecyclerView的高度可设置为wrap_content<br>
