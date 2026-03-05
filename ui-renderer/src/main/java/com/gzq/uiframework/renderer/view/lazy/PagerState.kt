package com.gzq.uiframework.renderer.view.lazy

import androidx.viewpager2.widget.ViewPager2

class PagerState {
    var currentPage: Int = 0
        internal set
    var pageOffset: Float = 0f
        internal set
    internal var viewPager: ViewPager2? = null

    fun scrollToPage(page: Int) {
        viewPager?.setCurrentItem(page, true)
    }
}
