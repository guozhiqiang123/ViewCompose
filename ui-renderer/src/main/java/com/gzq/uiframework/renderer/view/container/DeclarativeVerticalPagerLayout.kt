package com.gzq.uiframework.renderer.view.container

import android.content.Context
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.gzq.uiframework.renderer.node.LazyListItem
import com.gzq.uiframework.renderer.reconcile.LazyListDiff
import com.gzq.uiframework.renderer.view.lazy.LazyHolderRegistry
import com.gzq.uiframework.renderer.view.lazy.LazyItemSessionController
import com.gzq.uiframework.renderer.view.lazy.PagerState
import com.gzq.uiframework.renderer.view.lazy.FrameworkRecyclerViewDefaults
import com.gzq.uiframework.renderer.view.tree.LayoutPassTracker

internal class DeclarativeVerticalPagerLayout(
    context: Context,
) : FrameLayout(context) {
    private val viewPager = ViewPager2(context)
    private val adapter = VerticalPagerAdapter()
    private var onPageChanged: ((Int) -> Unit)? = null
    private var pagerState: PagerState? = null
    private var suppressCallback: Boolean = false
    private val pageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            pagerState?.currentPage = position
            if (!suppressCallback) {
                onPageChanged?.invoke(position)
            }
        }

        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int,
        ) {
            pagerState?.currentPage = position
            pagerState?.pageOffset = positionOffset
        }
    }

    init {
        viewPager.layoutParams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
        )
        viewPager.orientation = ViewPager2.ORIENTATION_VERTICAL
        viewPager.adapter = adapter
        viewPager.registerOnPageChangeCallback(pageChangeCallback)
        applyRecyclerDefaults()
        addView(viewPager)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val startNs = System.nanoTime()
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        LayoutPassTracker.recordMeasure(
            viewName = javaClass.simpleName,
            durationNs = System.nanoTime() - startNs,
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val startNs = System.nanoTime()
        super.onLayout(changed, left, top, right, bottom)
        LayoutPassTracker.recordLayout(
            viewName = javaClass.simpleName,
            durationNs = System.nanoTime() - startNs,
        )
    }

    fun bind(
        pages: List<LazyListItem>,
        currentPage: Int,
        onPageChanged: ((Int) -> Unit)?,
        offscreenPageLimit: Int,
        pagerState: PagerState?,
        userScrollEnabled: Boolean,
    ) {
        this.onPageChanged = onPageChanged
        this.pagerState = pagerState
        pagerState?.viewPager = viewPager
        viewPager.offscreenPageLimit = offscreenPageLimit.coerceAtLeast(1)
        viewPager.isUserInputEnabled = userScrollEnabled
        adapter.submitPages(pages)
        val resolvedPage = if (pages.isEmpty()) {
            return
        } else {
            currentPage.coerceIn(0, pages.lastIndex)
        }
        if (viewPager.currentItem != resolvedPage) {
            suppressCallback = true
            viewPager.setCurrentItem(resolvedPage, false)
            suppressCallback = false
        }
    }

    fun dispose() {
        pagerState?.viewPager = null
        viewPager.unregisterOnPageChangeCallback(pageChangeCallback)
        adapter.disposeAll()
    }

    fun applyRecyclerDefaults(
        sharePool: Boolean = false,
        disableItemAnimator: Boolean = false,
    ) {
        resolvePagerRecyclerView()?.let { recyclerView ->
            FrameworkRecyclerViewDefaults.applyVerticalPagerDefaults(
                recyclerView = recyclerView,
                sharePool = sharePool,
                disableItemAnimator = disableItemAnimator,
            )
        }
    }

    private fun resolvePagerRecyclerView(): RecyclerView? {
        return viewPager.getChildAt(0) as? RecyclerView
    }
}

internal class VerticalPagerAdapter : RecyclerView.Adapter<VerticalPagerViewHolder>() {
    private var pages: List<LazyListItem> = emptyList()
    private val holderRegistry = LazyHolderRegistry<VerticalPagerViewHolder> { holder ->
        holder.recycle()
    }

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VerticalPagerViewHolder {
        val container = FrameLayout(parent.context).apply {
            layoutParams = RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            )
        }
        return VerticalPagerViewHolder(container)
    }

    override fun onBindViewHolder(holder: VerticalPagerViewHolder, position: Int) {
        bindHolder(
            holder = holder,
            position = position,
            payload = null,
        )
    }

    override fun onBindViewHolder(
        holder: VerticalPagerViewHolder,
        position: Int,
        payloads: MutableList<Any>,
    ) {
        bindHolder(
            holder = holder,
            position = position,
            payload = payloads.lastOrNull(),
        )
    }

    override fun onViewAttachedToWindow(holder: VerticalPagerViewHolder) {
        super.onViewAttachedToWindow(holder)
        holderRegistry.onAttached(holder)
    }

    override fun onViewDetachedFromWindow(holder: VerticalPagerViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holderRegistry.onDetached(holder)
    }

    override fun onViewRecycled(holder: VerticalPagerViewHolder) {
        holderRegistry.onRecycled(holder)
    }

    override fun getItemCount(): Int = pages.size

    override fun getItemId(position: Int): Long {
        val key = pages[position].key
        return key?.hashCode()?.toLong() ?: position.toLong()
    }

    fun submitPages(newPages: List<LazyListItem>) {
        val result = LazyListDiff.calculate(
            previous = this.pages,
            next = newPages,
        )
        this.pages = result.items
        if (result.diffResult != null) {
            result.diffResult.dispatchUpdatesTo(this)
        } else {
            notifyDataSetChanged()
        }
        if (result.updates.isEmpty()) {
            holderRegistry.forEachBound { holder ->
                val position = holder.bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION && position < this.pages.size) {
                    holder.bind(this.pages[position])
                }
            }
        }
    }

    fun disposeAll() {
        holderRegistry.disposeAll()
        pages = emptyList()
        notifyDataSetChanged()
    }

    private fun bindHolder(
        holder: VerticalPagerViewHolder,
        position: Int,
        payload: Any?,
    ) {
        holderRegistry.onBound(holder)
        holder.bind(
            item = pages[position],
            payload = payload,
        )
    }
}

internal class VerticalPagerViewHolder(
    private val container: FrameLayout,
) : RecyclerView.ViewHolder(container) {
    private val controller = LazyItemSessionController(
        createSession = { item ->
            item.sessionFactory.create(container)
        },
        clearContainer = container::removeAllViews,
    )

    fun bind(
        item: LazyListItem,
        payload: Any? = null,
    ) {
        controller.bind(
            item = item,
            payload = payload,
        )
    }

    fun recycle() {
        controller.recycle()
    }
}
