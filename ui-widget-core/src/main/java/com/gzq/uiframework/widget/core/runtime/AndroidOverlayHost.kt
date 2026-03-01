package com.gzq.uiframework.widget.core

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.View.MeasureSpec
import android.view.Window
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.PopupWindow
import androidx.core.view.doOnLayout
import com.gzq.uiframework.renderer.view.tree.MountedNode
import com.gzq.uiframework.renderer.view.tree.ViewTreeRenderer
import com.gzq.uiframework.renderer.R

class AndroidOverlayHost(
    rootView: View,
) : OverlayHost {
    private val delegate = CompositeOverlayHost(
        DialogOverlayHost(AndroidDialogOverlayPresenter(rootView)),
        PopupOverlayHost(AndroidPopupOverlayPresenter(rootView)),
        AndroidTransientFeedbackOverlayHost(rootView),
    )

    override fun commit(
        sessionId: OverlaySessionId,
        requests: List<OverlayRequest>,
    ) {
        delegate.commit(sessionId, requests)
    }

    override fun clear(sessionId: OverlaySessionId) {
        delegate.clear(sessionId)
    }
}

internal class CompositeOverlayHost(
    private vararg val delegates: OverlayHost,
) : OverlayHost {
    override fun commit(
        sessionId: OverlaySessionId,
        requests: List<OverlayRequest>,
    ) {
        delegates.forEach { host ->
            host.commit(sessionId, requests)
        }
    }

    override fun clear(sessionId: OverlaySessionId) {
        delegates.forEach { host ->
            host.clear(sessionId)
        }
    }
}

internal class AndroidDialogOverlayPresenter(
    private val rootView: View,
) : DialogOverlayPresenter {
    override fun show(
        entryId: OverlayEntryId,
        spec: DialogOverlaySpec,
        content: DialogOverlayContent,
    ): DialogOverlayHandle {
        return AndroidDialogOverlayHandle(
            rootView = rootView,
            spec = spec,
            content = content,
        )
    }
}

internal class AndroidPopupOverlayPresenter(
    private val rootView: View,
) : PopupOverlayPresenter {
    override fun show(
        entryId: OverlayEntryId,
        spec: PopupOverlaySpec,
        content: PopupOverlayContent,
    ): PopupOverlayHandle {
        return AndroidPopupOverlayHandle(
            rootView = rootView,
            spec = spec,
            content = content,
        )
    }
}

private class AndroidDialogOverlayHandle(
    rootView: View,
    spec: DialogOverlaySpec,
    content: DialogOverlayContent,
) : DialogOverlayHandle {
    private val dialogContainer = FrameLayout(rootView.context).apply {
        val inset = 24.dp(rootView)
        setPadding(inset, inset, inset, inset)
        background = ColorDrawable(Color.TRANSPARENT)
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
        )
    }
    private val dialog = Dialog(rootView.context).apply {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(dialogContainer)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
    private var mountedNodes: List<MountedNode> = emptyList()
    private var currentSpec = spec
    private var programmaticDismiss = false

    init {
        dialog.setOnDismissListener {
            if (!programmaticDismiss) {
                currentSpec.onDismissRequest?.invoke()
            }
        }
        update(
            spec = spec,
            content = content,
        )
        dialog.show()
    }

    override fun update(
        spec: DialogOverlaySpec,
        content: DialogOverlayContent,
    ) {
        currentSpec = spec
        dialog.setCancelable(spec.dismissOnBackPress)
        dialog.setCanceledOnTouchOutside(spec.dismissOnClickOutside)
        dialog.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            )
            setGravity(spec.position.toGravity())
            val clampedScrim = spec.scrimOpacity.coerceIn(0f, 1f)
            if (clampedScrim > 0f) {
                addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                setDimAmount(clampedScrim)
            } else {
                clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            }
        }
        val renderResult = ViewTreeRenderer.renderInto(
            container = dialogContainer,
            previous = mountedNodes,
            nodes = content.nodes,
        )
        mountedNodes = renderResult.mountedNodes
        if (!dialog.isShowing) {
            dialog.show()
        }
    }

    override fun dismiss() {
        programmaticDismiss = true
        dialog.setOnDismissListener(null)
        ViewTreeRenderer.disposeMounted(
            container = dialogContainer,
            mountedNodes = mountedNodes,
        )
        mountedNodes = emptyList()
        if (dialog.isShowing) {
            dialog.dismiss()
        }
        programmaticDismiss = false
    }
}

private class AndroidPopupOverlayHandle(
    private val rootView: View,
    spec: PopupOverlaySpec,
    content: PopupOverlayContent,
) : PopupOverlayHandle {
    private val popupContainer = FrameLayout(rootView.context).apply {
        background = ColorDrawable(Color.TRANSPARENT)
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
        )
    }
    private val popupWindow = PopupWindow(
        popupContainer,
        ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT,
        spec.focusable,
    ).apply {
        setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        elevation = 12.dp(rootView).toFloat()
    }
    private var mountedNodes: List<MountedNode> = emptyList()
    private var currentSpec = spec
    private var programmaticDismiss = false

    init {
        popupWindow.setOnDismissListener {
            if (!programmaticDismiss) {
                currentSpec.onDismissRequest?.invoke()
            }
        }
        update(
            spec = spec,
            content = content,
        )
    }

    override fun update(
        spec: PopupOverlaySpec,
        content: PopupOverlayContent,
    ) {
        currentSpec = spec
        popupWindow.isFocusable = spec.focusable
        popupWindow.isOutsideTouchable = spec.dismissOnClickOutside
        val renderResult = ViewTreeRenderer.renderInto(
            container = popupContainer,
            previous = mountedNodes,
            nodes = content.nodes,
        )
        mountedNodes = renderResult.mountedNodes
        val anchor = rootView.findAnchorTarget(spec.anchorId)
        if (anchor == null) {
            if (popupWindow.isShowing) {
                dismiss()
            }
            return
        }
        popupContainer.measure(
            rootView.width.atMostMeasureSpec(),
            rootView.height.atMostMeasureSpec(),
        )
        val popupWidth = popupContainer.measuredWidth
        val popupHeight = popupContainer.measuredHeight
        val xOffset = spec.alignment.resolveXOffset(
            anchorWidth = anchor.width,
            popupWidth = popupWidth,
            isRtl = anchor.layoutDirection == View.LAYOUT_DIRECTION_RTL,
            baseOffset = spec.offsetX,
        )
        val yOffset = spec.alignment.resolveYOffset(
            anchorHeight = anchor.height,
            popupHeight = popupHeight,
            baseOffset = spec.offsetY,
        )
        anchor.doOnLayout {
            if (!popupWindow.isShowing) {
                popupWindow.showAsDropDown(anchor, xOffset, yOffset)
            } else {
                popupWindow.update(anchor, xOffset, yOffset, -1, -1)
            }
        }
    }

    override fun dismiss() {
        programmaticDismiss = true
        popupWindow.setOnDismissListener(null)
        ViewTreeRenderer.disposeMounted(
            container = popupContainer,
            mountedNodes = mountedNodes,
        )
        mountedNodes = emptyList()
        if (popupWindow.isShowing) {
            popupWindow.dismiss()
        }
        programmaticDismiss = false
    }
}

private fun Int.dp(view: View): Int {
    return (this * view.resources.displayMetrics.density).toInt()
}

private fun View.findAnchorTarget(anchorId: String): View? {
    if (getTag(R.id.ui_framework_anchor_id) == anchorId) {
        return this
    }
    val group = this as? ViewGroup ?: return null
    for (index in 0 until group.childCount) {
        val match = group.getChildAt(index).findAnchorTarget(anchorId)
        if (match != null) {
            return match
        }
    }
    return null
}

private fun DialogPosition.toGravity(): Int {
    return when (this) {
        DialogPosition.Top -> Gravity.TOP or Gravity.CENTER_HORIZONTAL
        DialogPosition.Center -> Gravity.CENTER
        DialogPosition.Bottom -> Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
    }
}

private fun PopupAlignment.resolveXOffset(
    anchorWidth: Int,
    popupWidth: Int,
    isRtl: Boolean,
    baseOffset: Int,
): Int {
    val startOffset = if (isRtl) {
        anchorWidth - popupWidth
    } else {
        0
    }
    val endOffset = if (isRtl) {
        0
    } else {
        anchorWidth - popupWidth
    }
    val alignedOffset = when (this) {
        PopupAlignment.BelowStart,
        PopupAlignment.AboveStart,
        -> startOffset

        PopupAlignment.BelowCenter,
        PopupAlignment.AboveCenter,
        -> (anchorWidth - popupWidth) / 2

        PopupAlignment.BelowEnd,
        PopupAlignment.AboveEnd,
        -> endOffset
    }
    return alignedOffset + baseOffset
}

private fun PopupAlignment.resolveYOffset(
    anchorHeight: Int,
    popupHeight: Int,
    baseOffset: Int,
): Int {
    return when (this) {
        PopupAlignment.BelowStart,
        PopupAlignment.BelowCenter,
        PopupAlignment.BelowEnd,
        -> baseOffset

        PopupAlignment.AboveStart,
        PopupAlignment.AboveCenter,
        PopupAlignment.AboveEnd,
        -> -anchorHeight - popupHeight + baseOffset
    }
}

private fun Int.atMostMeasureSpec(): Int {
    return if (this > 0) {
        MeasureSpec.makeMeasureSpec(this, MeasureSpec.AT_MOST)
    } else {
        MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
    }
}
