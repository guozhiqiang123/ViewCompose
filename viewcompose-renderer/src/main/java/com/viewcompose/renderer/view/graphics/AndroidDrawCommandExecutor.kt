package com.viewcompose.renderer.view.graphics

import android.graphics.Bitmap
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.PorterDuffXfermode
import android.graphics.RadialGradient
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.SweepGradient
import android.graphics.drawable.Drawable
import android.os.Build
import com.viewcompose.graphics.core.BlendMode
import com.viewcompose.graphics.core.Brush
import com.viewcompose.graphics.core.ColorFilterModel
import com.viewcompose.graphics.core.DrawCommand
import com.viewcompose.graphics.core.DrawPaint
import com.viewcompose.graphics.core.DrawStyle
import com.viewcompose.graphics.core.ImageFilterModel
import com.viewcompose.graphics.core.PathCommand
import com.viewcompose.graphics.core.PathFillType
import com.viewcompose.graphics.core.PathModel
import com.viewcompose.graphics.core.StrokeCap
import com.viewcompose.graphics.core.StrokeJoin
import com.viewcompose.graphics.core.TextStyle
import kotlin.math.max

internal object AndroidDrawCommandExecutor {
    fun execute(
        canvas: Canvas,
        commands: List<DrawCommand>,
    ) {
        for (command in commands) {
            when (command) {
                DrawCommand.Save -> canvas.save()
                DrawCommand.Restore -> canvas.restore()
                is DrawCommand.SaveLayer -> {
                    val bounds = command.bounds?.let {
                        RectF(it.left, it.top, it.right, it.bottom)
                    }
                    canvas.saveLayer(
                        bounds,
                        command.paint.toAndroidPaint(),
                    )
                }
                is DrawCommand.Translate -> canvas.translate(command.dx, command.dy)
                is DrawCommand.Scale -> canvas.scale(
                    command.sx,
                    command.sy,
                    command.pivot.x,
                    command.pivot.y,
                )
                is DrawCommand.Rotate -> canvas.rotate(
                    command.degrees,
                    command.pivot.x,
                    command.pivot.y,
                )
                is DrawCommand.Skew -> canvas.skew(command.kx, command.ky)
                is DrawCommand.Concat -> {
                    canvas.concat(
                        Matrix().apply {
                            setValues(command.matrix.values)
                        },
                    )
                }
                is DrawCommand.ClipRect -> canvas.clipRect(
                    command.rect.left,
                    command.rect.top,
                    command.rect.right,
                    command.rect.bottom,
                )
                is DrawCommand.ClipPath -> canvas.clipPath(command.path.toAndroidPath())
                is DrawCommand.DrawLine -> canvas.drawLine(
                    command.from.x,
                    command.from.y,
                    command.to.x,
                    command.to.y,
                    command.paint.toAndroidPaint(),
                )
                is DrawCommand.DrawRect -> canvas.drawRect(
                    command.rect.left,
                    command.rect.top,
                    command.rect.right,
                    command.rect.bottom,
                    command.paint.toAndroidPaint(),
                )
                is DrawCommand.DrawRoundRect -> canvas.drawRoundRect(
                    RectF(
                        command.roundRect.rect.left,
                        command.roundRect.rect.top,
                        command.roundRect.rect.right,
                        command.roundRect.rect.bottom,
                    ),
                    max(command.roundRect.topLeft.x, command.roundRect.topLeft.y),
                    max(command.roundRect.topLeft.x, command.roundRect.topLeft.y),
                    command.paint.toAndroidPaint(),
                )
                is DrawCommand.DrawCircle -> canvas.drawCircle(
                    command.center.x,
                    command.center.y,
                    command.radius,
                    command.paint.toAndroidPaint(),
                )
                is DrawCommand.DrawOval -> canvas.drawOval(
                    RectF(
                        command.rect.left,
                        command.rect.top,
                        command.rect.right,
                        command.rect.bottom,
                    ),
                    command.paint.toAndroidPaint(),
                )
                is DrawCommand.DrawArc -> canvas.drawArc(
                    RectF(
                        command.oval.left,
                        command.oval.top,
                        command.oval.right,
                        command.oval.bottom,
                    ),
                    command.startAngleDegrees,
                    command.sweepAngleDegrees,
                    command.useCenter,
                    command.paint.toAndroidPaint(),
                )
                is DrawCommand.DrawPath -> canvas.drawPath(
                    command.path.toAndroidPath(),
                    command.paint.toAndroidPaint(),
                )
                is DrawCommand.DrawImage -> drawImage(
                    canvas = canvas,
                    command = command,
                )
                is DrawCommand.DrawText -> drawText(
                    canvas = canvas,
                    command = command,
                )
            }
        }
    }

    private fun drawImage(
        canvas: Canvas,
        command: DrawCommand.DrawImage,
    ) {
        val paint = command.paint.toAndroidPaint()
        val srcRect = command.src?.let {
            android.graphics.Rect(
                it.left.toInt(),
                it.top.toInt(),
                it.right.toInt(),
                it.bottom.toInt(),
            )
        }
        val dstRect = command.dst?.let {
            android.graphics.Rect(
                it.left.toInt(),
                it.top.toInt(),
                it.right.toInt(),
                it.bottom.toInt(),
            )
        }
        when (val source = command.image.stableId) {
            is Bitmap -> {
                if (srcRect != null || dstRect != null) {
                    canvas.drawBitmap(
                        source,
                        srcRect,
                        dstRect ?: android.graphics.Rect(
                            0,
                            0,
                            source.width,
                            source.height,
                        ),
                        paint,
                    )
                } else {
                    canvas.drawBitmap(source, 0f, 0f, paint)
                }
            }
            is Drawable -> {
                val prevBounds = source.bounds
                if (dstRect != null) {
                    source.bounds = dstRect
                } else {
                    source.setBounds(0, 0, command.image.width, command.image.height)
                }
                source.draw(canvas)
                source.bounds = prevBounds
            }
        }
    }

    private fun drawText(
        canvas: Canvas,
        command: DrawCommand.DrawText,
    ) {
        val paint = command.paint.toAndroidPaint().applyTextStyle(command.style)
        canvas.drawText(
            command.text,
            command.origin.x,
            command.origin.y,
            paint,
        )
    }

    private fun DrawPaint.toAndroidPaint(): Paint {
        return Paint().apply {
            isAntiAlias = antiAlias
            alpha = (this@toAndroidPaint.alpha.coerceIn(0f, 1f) * 255).toInt()
            shader = brush.toShader()
            color = (brush as? Brush.SolidColor)?.color ?: Color.TRANSPARENT
            style = when (val drawStyle = this@toAndroidPaint.style) {
                DrawStyle.Fill -> Paint.Style.FILL
                is DrawStyle.Stroke -> {
                    strokeWidth = drawStyle.width
                    strokeCap = when (drawStyle.cap) {
                        StrokeCap.Butt -> Paint.Cap.BUTT
                        StrokeCap.Round -> Paint.Cap.ROUND
                        StrokeCap.Square -> Paint.Cap.SQUARE
                    }
                    strokeJoin = when (drawStyle.join) {
                        StrokeJoin.Miter -> Paint.Join.MITER
                        StrokeJoin.Round -> Paint.Join.ROUND
                        StrokeJoin.Bevel -> Paint.Join.BEVEL
                    }
                    strokeMiter = drawStyle.miterLimit
                    Paint.Style.STROKE
                }
            }
            setBlendModeCompat(this@toAndroidPaint.blendMode)
            colorFilter = this@toAndroidPaint.colorFilter.toAndroidColorFilter()
            applyImageFilter(this@toAndroidPaint.imageFilter)
        }
    }

    private fun Paint.applyImageFilter(filter: ImageFilterModel?) {
        maskFilter = when (filter) {
            is ImageFilterModel.Blur ->
                BlurMaskFilter(max(filter.radiusX, filter.radiusY), BlurMaskFilter.Blur.NORMAL)
            is ImageFilterModel.Chain -> null
            null -> null
        }
    }

    private fun Paint.setBlendModeCompat(mode: BlendMode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            blendMode = when (mode) {
                BlendMode.SrcOver -> android.graphics.BlendMode.SRC_OVER
                BlendMode.SrcIn -> android.graphics.BlendMode.SRC_IN
                BlendMode.SrcOut -> android.graphics.BlendMode.SRC_OUT
                BlendMode.SrcAtop -> android.graphics.BlendMode.SRC_ATOP
                BlendMode.DstOver -> android.graphics.BlendMode.DST_OVER
                BlendMode.DstIn -> android.graphics.BlendMode.DST_IN
                BlendMode.DstOut -> android.graphics.BlendMode.DST_OUT
                BlendMode.DstAtop -> android.graphics.BlendMode.DST_ATOP
                BlendMode.Multiply -> android.graphics.BlendMode.MULTIPLY
                BlendMode.Screen -> android.graphics.BlendMode.SCREEN
                BlendMode.Overlay -> android.graphics.BlendMode.OVERLAY
                BlendMode.Darken -> android.graphics.BlendMode.DARKEN
                BlendMode.Lighten -> android.graphics.BlendMode.LIGHTEN
                BlendMode.Plus -> android.graphics.BlendMode.PLUS
            }
        } else {
            @Suppress("DEPRECATION")
            xfermode = PorterDuffXfermode(
                when (mode) {
                    BlendMode.SrcOver -> PorterDuff.Mode.SRC_OVER
                    BlendMode.SrcIn -> PorterDuff.Mode.SRC_IN
                    BlendMode.SrcOut -> PorterDuff.Mode.SRC_OUT
                    BlendMode.SrcAtop -> PorterDuff.Mode.SRC_ATOP
                    BlendMode.DstOver -> PorterDuff.Mode.DST_OVER
                    BlendMode.DstIn -> PorterDuff.Mode.DST_IN
                    BlendMode.DstOut -> PorterDuff.Mode.DST_OUT
                    BlendMode.DstAtop -> PorterDuff.Mode.DST_ATOP
                    BlendMode.Multiply -> PorterDuff.Mode.MULTIPLY
                    BlendMode.Screen -> PorterDuff.Mode.SCREEN
                    BlendMode.Overlay -> PorterDuff.Mode.OVERLAY
                    BlendMode.Darken -> PorterDuff.Mode.DARKEN
                    BlendMode.Lighten -> PorterDuff.Mode.LIGHTEN
                    BlendMode.Plus -> PorterDuff.Mode.ADD
                },
            )
        }
    }

    private fun ColorFilterModel?.toAndroidColorFilter(): android.graphics.ColorFilter? {
        return when (this) {
            is ColorFilterModel.Tint -> PorterDuffColorFilter(
                color,
                when (blendMode) {
                    BlendMode.SrcOver -> PorterDuff.Mode.SRC_OVER
                    BlendMode.SrcIn -> PorterDuff.Mode.SRC_IN
                    BlendMode.SrcOut -> PorterDuff.Mode.SRC_OUT
                    BlendMode.SrcAtop -> PorterDuff.Mode.SRC_ATOP
                    BlendMode.DstOver -> PorterDuff.Mode.DST_OVER
                    BlendMode.DstIn -> PorterDuff.Mode.DST_IN
                    BlendMode.DstOut -> PorterDuff.Mode.DST_OUT
                    BlendMode.DstAtop -> PorterDuff.Mode.DST_ATOP
                    BlendMode.Multiply -> PorterDuff.Mode.MULTIPLY
                    BlendMode.Screen -> PorterDuff.Mode.SCREEN
                    BlendMode.Overlay -> PorterDuff.Mode.OVERLAY
                    BlendMode.Darken -> PorterDuff.Mode.DARKEN
                    BlendMode.Lighten -> PorterDuff.Mode.LIGHTEN
                    BlendMode.Plus -> PorterDuff.Mode.ADD
                },
            )
            is ColorFilterModel.ColorMatrix -> ColorMatrixColorFilter(ColorMatrix(values))
            null -> null
        }
    }

    private fun Brush.toShader(): Shader? {
        return when (this) {
            is Brush.SolidColor -> null
            is Brush.LinearGradient -> LinearGradient(
                from.x,
                from.y,
                to.x,
                to.y,
                colorStops.map { it.color }.toIntArray(),
                colorStops.map { it.offset }.toFloatArray(),
                Shader.TileMode.CLAMP,
            )
            is Brush.RadialGradient -> RadialGradient(
                center.x,
                center.y,
                radius,
                colorStops.map { it.color }.toIntArray(),
                colorStops.map { it.offset }.toFloatArray(),
                Shader.TileMode.CLAMP,
            )
            is Brush.SweepGradient -> SweepGradient(
                center.x,
                center.y,
                colorStops.map { it.color }.toIntArray(),
                colorStops.map { it.offset }.toFloatArray(),
            )
        }
    }

    private fun PathModel.toAndroidPath(): Path {
        return Path().apply {
            fillType = when (this@toAndroidPath.fillType) {
                PathFillType.NonZero -> Path.FillType.WINDING
                PathFillType.EvenOdd -> Path.FillType.EVEN_ODD
            }
            for (command in this@toAndroidPath.commands) {
                when (command) {
                    is PathCommand.MoveTo -> moveTo(command.x, command.y)
                    is PathCommand.LineTo -> lineTo(command.x, command.y)
                    is PathCommand.QuadTo -> quadTo(command.x1, command.y1, command.x2, command.y2)
                    is PathCommand.CubicTo -> cubicTo(
                        command.x1,
                        command.y1,
                        command.x2,
                        command.y2,
                        command.x3,
                        command.y3,
                    )
                    is PathCommand.ArcTo -> arcTo(
                        RectF(
                            command.oval.left,
                            command.oval.top,
                            command.oval.right,
                            command.oval.bottom,
                        ),
                        command.startAngleDegrees,
                        command.sweepAngleDegrees,
                        command.forceMoveTo,
                    )
                    PathCommand.Close -> close()
                }
            }
        }
    }

    private fun Paint.applyTextStyle(style: TextStyle): Paint {
        textSize = style.textSizePx
        isFakeBoldText = style.isBold
        textSkewX = if (style.isItalic) -0.25f else 0f
        return this
    }
}
