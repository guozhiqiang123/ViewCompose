package com.viewcompose.overlay.android.presenter

import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import com.viewcompose.widget.core.ModalBottomSheetOverlayContent
import com.viewcompose.widget.core.ModalBottomSheetOverlayHandle
import com.viewcompose.widget.core.ModalBottomSheetOverlayPresenter
import com.viewcompose.widget.core.ModalBottomSheetOverlaySpec
import com.viewcompose.widget.core.OverlayEntryId
import com.viewcompose.widget.core.OverlaySurfaceSession
import com.viewcompose.widget.core.createOverlaySurfaceSession

class AndroidModalBottomSheetPresenter(
    private val rootView: View,
) : ModalBottomSheetOverlayPresenter {
    override fun show(
        entryId: OverlayEntryId,
        spec: ModalBottomSheetOverlaySpec,
        content: ModalBottomSheetOverlayContent,
    ): ModalBottomSheetOverlayHandle {
        return AndroidModalBottomSheetHandle(
            rootView = rootView,
            spec = spec,
            content = content,
        )
    }
}

private class AndroidModalBottomSheetHandle(
    rootView: View,
    spec: ModalBottomSheetOverlaySpec,
    content: ModalBottomSheetOverlayContent,
) : ModalBottomSheetOverlayHandle {
    private val dialogContainer = FrameLayout(rootView.context).apply {
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
        )
    }
    private val dialog = BottomSheetDialog(rootView.context).apply {
        setContentView(dialogContainer)
    }
    private val defaultNavigationBarColor: Int? = dialog.window?.readNavigationBarColorCompat()
    private var defaultSheetBackground: Drawable? = null
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
        spec: ModalBottomSheetOverlaySpec,
        content: ModalBottomSheetOverlayContent,
    ) {
        currentSpec = spec
        dialog.setCancelable(spec.dismissOnBackPress)
        dialog.setCanceledOnTouchOutside(spec.dismissOnClickOutside)
        dialog.window?.apply {
            setDimAmount(spec.scrimOpacity.coerceIn(0f, 1f))
            val color = spec.navigationBarColor ?: defaultNavigationBarColor
            if (color != null) {
                applyNavigationBarColorCompat(
                    color = color,
                    enforceContrast = spec.navigationBarColor == null,
                )
            }
        }
        if (spec.skipPartiallyExpanded) {
            dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
            dialog.behavior.skipCollapsed = true
        }
        surfaceSession.update(content.surface)
        if (!dialog.isShowing) {
            dialog.show()
        }
        applySheetCornerRadius(spec)
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

    private fun applySheetCornerRadius(spec: ModalBottomSheetOverlaySpec) {
        val sheet = dialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet) ?: return
        if (defaultSheetBackground == null) {
            defaultSheetBackground = sheet.background?.constantState?.newDrawable()?.mutate()
        }
        val radius = spec.topCornerRadius
        if (radius == null) {
            sheet.background = defaultSheetBackground
            return
        }
        val fillColorInt = when (val background = sheet.background) {
            is MaterialShapeDrawable -> background.fillColor?.defaultColor
            is ColorDrawable -> background.color
            else -> null
        } ?: ColorDrawable(0).color
        val model = ShapeAppearanceModel.builder()
            .setTopLeftCorner(CornerFamily.ROUNDED, radius.toFloat())
            .setTopRightCorner(CornerFamily.ROUNDED, radius.toFloat())
            .setBottomLeftCorner(CornerFamily.ROUNDED, 0f)
            .setBottomRightCorner(CornerFamily.ROUNDED, 0f)
            .build()
        sheet.background = MaterialShapeDrawable(model).apply {
            initializeElevationOverlay(sheet.context)
            fillColor = ColorStateList.valueOf(fillColorInt)
        }
    }
}

@Suppress("DEPRECATION")
private fun Window.readNavigationBarColorCompat(): Int = navigationBarColor

@Suppress("DEPRECATION")
private fun Window.applyNavigationBarColorCompat(
    color: Int,
    enforceContrast: Boolean,
) {
    navigationBarColor = color
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        isNavigationBarContrastEnforced = enforceContrast
    }
}
