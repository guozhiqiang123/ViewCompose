package com.viewcompose.host.android.graphics

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.graphics.Shader
import android.os.Build
import android.view.View
import com.viewcompose.host.android.nativeView
import com.viewcompose.ui.modifier.Modifier

/**
 * Android-specific graphics interop for business code that explicitly opts into native View behavior.
 *
 * This API is intentionally hosted in `host-android` to keep graphics mainline APIs platform-neutral.
 */
object AndroidGraphicsInterop {
    fun applyRenderEffect(
        target: View,
        effect: RenderEffect?,
    ): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            return false
        }
        target.setRenderEffect(effect)
        return true
    }

    fun clearRenderEffect(target: View): Boolean {
        return applyRenderEffect(
            target = target,
            effect = null,
        )
    }

    fun createBlurEffect(
        radiusX: Float,
        radiusY: Float,
        tileMode: Shader.TileMode = Shader.TileMode.CLAMP,
    ): RenderEffect? {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            return null
        }
        return RenderEffect.createBlurEffect(radiusX, radiusY, tileMode)
    }

    fun chainRenderEffects(
        outer: RenderEffect,
        inner: RenderEffect,
    ): RenderEffect? {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            return null
        }
        return RenderEffect.createChainEffect(outer, inner)
    }

    fun createColorFilterEffect(
        colorFilter: ColorFilter,
        input: RenderEffect? = null,
    ): RenderEffect? {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            return null
        }
        return if (input == null) {
            RenderEffect.createColorFilterEffect(colorFilter)
        } else {
            RenderEffect.createColorFilterEffect(colorFilter, input)
        }
    }

    fun createRuntimeShader(shaderSource: String): RuntimeShader? {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return null
        }
        return RuntimeShader(shaderSource)
    }

    fun createRuntimeShaderEffect(
        shader: RuntimeShader,
        inputShaderName: String,
    ): RenderEffect? {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return null
        }
        return RenderEffect.createRuntimeShaderEffect(shader, inputShaderName)
    }

    fun renderToBitmap(
        width: Int,
        height: Int,
        config: Bitmap.Config = Bitmap.Config.ARGB_8888,
        draw: Canvas.() -> Unit,
    ): Bitmap {
        require(width > 0) { "width must be > 0" }
        require(height > 0) { "height must be > 0" }
        return Bitmap.createBitmap(width, height, config).apply {
            Canvas(this).draw()
        }
    }

    fun drawDrawableToBitmap(
        drawable: android.graphics.drawable.Drawable,
        width: Int,
        height: Int,
        config: Bitmap.Config = Bitmap.Config.ARGB_8888,
    ): Bitmap {
        return renderToBitmap(
            width = width,
            height = height,
            config = config,
        ) {
            drawable.setBounds(0, 0, width, height)
            drawable.draw(this)
        }
    }

    fun setLayerPaint(
        target: View,
        configure: Paint.() -> Unit,
    ) {
        target.setLayerType(
            View.LAYER_TYPE_HARDWARE,
            Paint().apply(configure),
        )
    }
}

fun Modifier.androidGraphics(
    key: Any = Unit,
    configure: (View) -> Unit,
): Modifier {
    return nativeView(
        key = key,
        configure = configure,
    )
}
