package com.viewcompose.overlay.android.presenter

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.View
import android.view.View.MeasureSpec
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.PopupWindow
import androidx.core.view.doOnLayout
import com.viewcompose.ui.modifier.OVERLAY_ANCHOR_TAG_KEY
import com.viewcompose.widget.core.AndroidEnvironmentBridge
import com.viewcompose.widget.core.DialogOverlayContent
import com.viewcompose.widget.core.DialogOverlayHandle
import com.viewcompose.widget.core.DialogOverlayPresenter
import com.viewcompose.widget.core.DialogOverlaySpec
import com.viewcompose.widget.core.DialogPosition
import com.viewcompose.widget.core.OverlayEntryId
import com.viewcompose.widget.core.PopupAlignment
import com.viewcompose.widget.core.PopupOverlayContent
import com.viewcompose.widget.core.PopupOverlayHandle
import com.viewcompose.widget.core.PopupOverlayPresenter
import com.viewcompose.widget.core.PopupOverlaySpec
import com.viewcompose.widget.core.OverlaySurfaceSession
import com.viewcompose.widget.core.createOverlaySurfaceSession

class AndroidDialogOverlayPresenter(
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

class AndroidPopupOverlayPresenter(
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
    private val density = AndroidEnvironmentBridge.fromContext(rootView.context).density
    private val dialogContainer = FrameLayout(rootView.context).apply {
        val inset = density.dp(24)
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
    private val surfaceSession: OverlaySurfaceSession = createOverlaySurfaceSession(
        container = dialogContainer,
        content = content.surface,
    )
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
        surfaceSession.update(content.surface)
        if (!dialog.isShowing) {
            dialog.show()
        }
    }

    override fun dismiss() {
        programmaticDismiss = true
        dialog.setOnDismissListener(null)
        surfaceSession.dispose()
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
    private val density = AndroidEnvironmentBridge.fromContext(rootView.context).density
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
        elevation = density.dp(12).toFloat()
    }
    private val surfaceSession: OverlaySurfaceSession = createOverlaySurfaceSession(
        container = popupContainer,
        content = content.surface,
    )
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
        surfaceSession.update(content.surface)
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
        surfaceSession.dispose()
        if (popupWindow.isShowing) {
            popupWindow.dismiss()
        }
        programmaticDismiss = false
    }
}

private fun View.findAnchorTarget(anchorId: String): View? {
    if (getTag(OVERLAY_ANCHOR_TAG_KEY) == anchorId) {
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
