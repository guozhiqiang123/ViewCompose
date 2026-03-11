package com.viewcompose.graphics.core

typealias UiColor = Int

data class ColorStop(
    val offset: Float,
    val color: UiColor,
)

sealed interface Brush {
    data class SolidColor(
        val color: UiColor,
    ) : Brush

    data class LinearGradient(
        val from: Offset,
        val to: Offset,
        val colorStops: List<ColorStop>,
    ) : Brush

    data class RadialGradient(
        val center: Offset,
        val radius: Float,
        val colorStops: List<ColorStop>,
    ) : Brush

    data class SweepGradient(
        val center: Offset,
        val colorStops: List<ColorStop>,
    ) : Brush
}

sealed interface DrawStyle {
    data object Fill : DrawStyle

    data class Stroke(
        val width: Float = 1f,
        val cap: StrokeCap = StrokeCap.Butt,
        val join: StrokeJoin = StrokeJoin.Miter,
        val miterLimit: Float = 4f,
    ) : DrawStyle
}

enum class StrokeCap {
    Butt,
    Round,
    Square,
}

enum class StrokeJoin {
    Miter,
    Round,
    Bevel,
}

enum class BlendMode {
    SrcOver,
    SrcIn,
    SrcOut,
    SrcAtop,
    DstOver,
    DstIn,
    DstOut,
    DstAtop,
    Multiply,
    Screen,
    Overlay,
    Darken,
    Lighten,
    Plus,
}

sealed interface ColorFilterModel {
    data class Tint(
        val color: UiColor,
        val blendMode: BlendMode = BlendMode.SrcIn,
    ) : ColorFilterModel

    data class ColorMatrix(
        val values: FloatArray,
    ) : ColorFilterModel {
        init {
            require(values.size == 20) { "ColorMatrix filter requires 20 values." }
        }
    }
}

sealed interface ImageFilterModel {
    data class Blur(
        val radiusX: Float,
        val radiusY: Float,
    ) : ImageFilterModel

    data class Chain(
        val outer: ImageFilterModel,
        val inner: ImageFilterModel,
    ) : ImageFilterModel
}

data class DrawPaint(
    val brush: Brush = Brush.SolidColor(0xFF000000.toInt()),
    val style: DrawStyle = DrawStyle.Fill,
    val alpha: Float = 1f,
    val blendMode: BlendMode = BlendMode.SrcOver,
    val colorFilter: ColorFilterModel? = null,
    val imageFilter: ImageFilterModel? = null,
    val antiAlias: Boolean = true,
)
