package com.gzq.uiframework.renderer.view.tree

import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.gzq.uiframework.renderer.node.NodeType
import com.gzq.uiframework.renderer.node.VNode
import com.gzq.uiframework.renderer.view.container.DeclarativeBoxLayout
import com.gzq.uiframework.renderer.view.container.DeclarativeFlowColumnLayout
import com.gzq.uiframework.renderer.view.container.DeclarativeFlowRowLayout
import com.gzq.uiframework.renderer.view.container.DeclarativeHorizontalPagerLayout
import com.gzq.uiframework.renderer.view.container.DeclarativeLazyVerticalGridLayout
import com.gzq.uiframework.renderer.view.container.DeclarativeLinearLayout
import com.gzq.uiframework.renderer.view.container.DeclarativeNavigationBarLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.gzq.uiframework.renderer.view.container.DeclarativeScrollableColumnLayout
import com.gzq.uiframework.renderer.view.container.DeclarativeScrollableRowLayout
import com.gzq.uiframework.renderer.view.container.DeclarativeSegmentedControlLayout
import com.gzq.uiframework.renderer.view.container.DeclarativeTabRowLayout
import com.gzq.uiframework.renderer.view.container.DeclarativeTextFieldLayout
import com.gzq.uiframework.renderer.view.container.DeclarativeVerticalPagerLayout
import com.gzq.uiframework.renderer.view.lazy.FrameworkRecyclerViewDefaults
import com.gzq.uiframework.renderer.view.lazy.LazyLinearLayoutManager
import com.gzq.uiframework.renderer.view.lazy.LazyColumnAdapter

internal object ViewNodeFactory {
    fun createView(
        context: Context,
        node: VNode,
        createAndroidView: ((Context) -> View)?,
    ): View {
        return when (node.type) {
            NodeType.Text -> TextView(context)
            NodeType.TextField -> DeclarativeTextFieldLayout(context)
            NodeType.Checkbox -> CheckBox(context)
            NodeType.Switch -> Switch(context)
            NodeType.RadioButton -> RadioButton(context)
            NodeType.Slider -> SeekBar(context)
            NodeType.LinearProgressIndicator -> LinearProgressIndicator(context)
            NodeType.CircularProgressIndicator -> CircularProgressIndicator(context)
            NodeType.Button -> Button(context)
            NodeType.IconButton -> ImageButton(context)
            NodeType.Row -> DeclarativeLinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
            }
            NodeType.Column -> DeclarativeLinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
            }
            NodeType.Box, NodeType.Surface -> DeclarativeBoxLayout(context)
            NodeType.Spacer, NodeType.Divider -> View(context)
            NodeType.Image -> ImageView(context)
            NodeType.AndroidView -> createAndroidView?.invoke(context) ?: View(context)
            NodeType.LazyColumn -> RecyclerView(context).apply {
                layoutManager = LazyLinearLayoutManager(context)
                adapter = LazyColumnAdapter()
                FrameworkRecyclerViewDefaults.applyLazyColumnDefaults(this)
            }
            NodeType.LazyRow -> RecyclerView(context).apply {
                layoutManager = LazyLinearLayoutManager(
                    context = context,
                    orientation = LinearLayoutManager.HORIZONTAL,
                    reverseLayout = false,
                )
                adapter = LazyColumnAdapter(LinearLayoutManager.HORIZONTAL)
                FrameworkRecyclerViewDefaults.applyLazyRowDefaults(this)
            }
            NodeType.SegmentedControl -> DeclarativeSegmentedControlLayout(context)
            NodeType.ScrollableColumn -> DeclarativeScrollableColumnLayout(context)
            NodeType.ScrollableRow -> DeclarativeScrollableRowLayout(context)
            NodeType.FlowRow -> DeclarativeFlowRowLayout(context)
            NodeType.FlowColumn -> DeclarativeFlowColumnLayout(context)
            NodeType.NavigationBar -> DeclarativeNavigationBarLayout(context)
            NodeType.HorizontalPager -> DeclarativeHorizontalPagerLayout(context)
            NodeType.VerticalPager -> DeclarativeVerticalPagerLayout(context)
            NodeType.TabRow -> DeclarativeTabRowLayout(context)
            NodeType.LazyVerticalGrid -> DeclarativeLazyVerticalGridLayout(context)
            NodeType.PullToRefresh -> SwipeRefreshLayout(context)
        }
    }
}
