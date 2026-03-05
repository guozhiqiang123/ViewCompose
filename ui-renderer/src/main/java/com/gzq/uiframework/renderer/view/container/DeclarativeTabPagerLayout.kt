package com.gzq.uiframework.renderer.view.container

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.gzq.uiframework.renderer.node.TabPage
import com.gzq.uiframework.renderer.reconcile.TabPagerDiff
import com.gzq.uiframework.renderer.reconcile.TabPagerSelectionResolver
import com.gzq.uiframework.renderer.reconcile.TabPagerUpdate
import com.gzq.uiframework.renderer.view.lazy.LazyHolderRegistry
import com.gzq.uiframework.renderer.view.lazy.LazyItemSessionController
import com.gzq.uiframework.renderer.view.tree.LayoutPassTracker
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

@Deprecated("Use DeclarativeTabRowLayout + DeclarativeHorizontalPagerLayout instead")
internal class DeclarativeTabPagerLayout(
    context: Context,
) : LinearLayout(context) {
    private val tabLayout = TabLayout(context)
    private val viewPager = ViewPager2(context)
    private val adapter = TabPagerAdapter()
    private var mediator: TabLayoutMediator? = null
    private var pages: List<TabPage> = emptyList()
    private var onTabSelected: ((Int) -> Unit)? = null
    private var suppressSelectionCallback: Boolean = false
    private val pageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            if (!suppressSelectionCallback) {
                onTabSelected?.invoke(position)
            }
        }
    }

    init {
        orientation = VERTICAL
        tabLayout.tabMode = TabLayout.MODE_SCROLLABLE
        tabLayout.layoutParams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
        )
        viewPager.layoutParams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            0,
            1f,
        )
        viewPager.adapter = adapter
        viewPager.registerOnPageChangeCallback(pageChangeCallback)
        mediator = TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = pages.getOrNull(position)?.title.orEmpty()
        }.also { it.attach() }
        addView(tabLayout)
        addView(viewPager)
    }

    override fun onMeasure(
        widthMeasureSpec: Int,
        heightMeasureSpec: Int,
    ) {
        val startNs = System.nanoTime()
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        LayoutPassTracker.recordMeasure(
            viewName = javaClass.simpleName,
            durationNs = System.nanoTime() - startNs,
        )
    }

    override fun onLayout(
        changed: Boolean,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int,
    ) {
        val startNs = System.nanoTime()
        super.onLayout(changed, left, top, right, bottom)
        LayoutPassTracker.recordLayout(
            viewName = javaClass.simpleName,
            durationNs = System.nanoTime() - startNs,
        )
    }

    fun bind(
        pages: List<TabPage>,
        selectedTabIndex: Int,
        onTabSelected: ((Int) -> Unit)?,
        backgroundColor: Int,
        indicatorColor: Int,
        cornerRadius: Int,
        indicatorHeight: Int,
        tabPaddingHorizontal: Int,
        tabPaddingVertical: Int,
        selectedTextColor: Int,
        unselectedTextColor: Int,
        rippleColor: Int,
    ) {
        this.onTabSelected = onTabSelected
        this.pages = pages
        viewPager.offscreenPageLimit = pages.size.coerceAtLeast(1)
        adapter.submitPages(pages)
        tabLayout.background = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(backgroundColor)
            this.cornerRadius = cornerRadius.toFloat()
        }
        viewPager.setBackgroundColor(backgroundColor)
        tabLayout.setSelectedTabIndicator(
            GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                setColor(indicatorColor)
                this.cornerRadius = (indicatorHeight / 2f).coerceAtLeast(1f)
                setSize(0, indicatorHeight)
            },
        )
        tabLayout.setTabTextColors(unselectedTextColor, selectedTextColor)
        tabLayout.tabRippleColor = ColorStateList.valueOf(rippleColor)
        tabLayout.setTabIndicatorFullWidth(false)
        syncTabs()
        applyTabItemPadding(
            horizontal = tabPaddingHorizontal,
            vertical = tabPaddingVertical,
        )
        val resolvedIndex = TabPagerSelectionResolver.resolve(pages, selectedTabIndex)
        if (resolvedIndex == null) {
            return
        }
        if (viewPager.currentItem == resolvedIndex) {
            return
        }
        suppressSelectionCallback = true
        viewPager.setCurrentItem(resolvedIndex, false)
        suppressSelectionCallback = false
    }

    fun dispose() {
        mediator?.detach()
        mediator = null
        viewPager.unregisterOnPageChangeCallback(pageChangeCallback)
        adapter.disposeAll()
    }

    private fun syncTabs() {
        if (tabLayout.tabCount != pages.size) {
            return
        }
        pages.forEachIndexed { index, page ->
            val tab = tabLayout.getTabAt(index)
            if (tab != null && tab.text != page.title) {
                tab.text = page.title
            }
        }
    }

    private fun applyTabItemPadding(
        horizontal: Int,
        vertical: Int,
    ) {
        val strip = tabLayout.getChildAt(0) as? ViewGroup ?: return
        for (index in 0 until strip.childCount) {
            strip.getChildAt(index).setPadding(
                horizontal,
                vertical,
                horizontal,
                vertical,
            )
        }
    }
}

internal class TabPagerAdapter : RecyclerView.Adapter<TabPagerViewHolder>() {
    private var pages: List<TabPage> = emptyList()
    private val holderRegistry = LazyHolderRegistry<TabPagerViewHolder> { holder ->
        holder.recycle()
    }

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): TabPagerViewHolder {
        val container = FrameLayout(parent.context).apply {
            layoutParams = RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            )
        }
        return TabPagerViewHolder(container)
    }

    override fun onBindViewHolder(
        holder: TabPagerViewHolder,
        position: Int,
    ) {
        holderRegistry.onBound(holder)
        holder.bind(pages[position])
    }

    override fun onViewAttachedToWindow(holder: TabPagerViewHolder) {
        super.onViewAttachedToWindow(holder)
        holderRegistry.onAttached(holder)
    }

    override fun onViewDetachedFromWindow(holder: TabPagerViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holderRegistry.onDetached(holder)
    }

    override fun onViewRecycled(holder: TabPagerViewHolder) {
        holderRegistry.onRecycled(holder)
    }

    override fun getItemCount(): Int = pages.size

    override fun getItemId(position: Int): Long {
        val key = pages[position].item.key
        return key?.hashCode()?.toLong() ?: position.toLong()
    }

    fun submitPages(pages: List<TabPage>) {
        val result = TabPagerDiff.calculate(
            previous = this.pages,
            next = pages,
        )
        this.pages = result.pages
        result.updates.forEach { update ->
            when (update) {
                is TabPagerUpdate.Insert -> notifyItemInserted(update.index)
                is TabPagerUpdate.Remove -> notifyItemRemoved(update.index)
                is TabPagerUpdate.Move -> notifyItemMoved(update.fromIndex, update.toIndex)
                is TabPagerUpdate.Change -> notifyItemChanged(update.index)
                TabPagerUpdate.ReloadAll -> notifyDataSetChanged()
            }
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
}

internal class TabPagerViewHolder(
    private val container: FrameLayout,
) : RecyclerView.ViewHolder(container) {
    private val controller = LazyItemSessionController(
        createSession = { item ->
            item.sessionFactory.create(container)
        },
        clearContainer = container::removeAllViews,
    )

    fun bind(page: TabPage) {
        controller.bind(page.item)
    }

    fun recycle() {
        controller.recycle()
    }
}
