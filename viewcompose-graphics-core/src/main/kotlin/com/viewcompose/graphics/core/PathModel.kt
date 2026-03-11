package com.viewcompose.graphics.core

enum class PathFillType {
    NonZero,
    EvenOdd,
}

sealed interface PathCommand {
    data class MoveTo(
        val x: Float,
        val y: Float,
    ) : PathCommand

    data class LineTo(
        val x: Float,
        val y: Float,
    ) : PathCommand

    data class QuadTo(
        val x1: Float,
        val y1: Float,
        val x2: Float,
        val y2: Float,
    ) : PathCommand

    data class CubicTo(
        val x1: Float,
        val y1: Float,
        val x2: Float,
        val y2: Float,
        val x3: Float,
        val y3: Float,
    ) : PathCommand

    data class ArcTo(
        val oval: Rect,
        val startAngleDegrees: Float,
        val sweepAngleDegrees: Float,
        val forceMoveTo: Boolean,
    ) : PathCommand

    data object Close : PathCommand
}

data class PathModel(
    val fillType: PathFillType = PathFillType.NonZero,
    val commands: List<PathCommand> = emptyList(),
)

class PathBuilder {
    private val commands = mutableListOf<PathCommand>()
    private var fillType: PathFillType = PathFillType.NonZero

    fun fillType(fillType: PathFillType): PathBuilder {
        this.fillType = fillType
        return this
    }

    fun moveTo(
        x: Float,
        y: Float,
    ): PathBuilder {
        commands += PathCommand.MoveTo(x, y)
        return this
    }

    fun lineTo(
        x: Float,
        y: Float,
    ): PathBuilder {
        commands += PathCommand.LineTo(x, y)
        return this
    }

    fun quadTo(
        x1: Float,
        y1: Float,
        x2: Float,
        y2: Float,
    ): PathBuilder {
        commands += PathCommand.QuadTo(x1, y1, x2, y2)
        return this
    }

    fun cubicTo(
        x1: Float,
        y1: Float,
        x2: Float,
        y2: Float,
        x3: Float,
        y3: Float,
    ): PathBuilder {
        commands += PathCommand.CubicTo(x1, y1, x2, y2, x3, y3)
        return this
    }

    fun arcTo(
        oval: Rect,
        startAngleDegrees: Float,
        sweepAngleDegrees: Float,
        forceMoveTo: Boolean = false,
    ): PathBuilder {
        commands += PathCommand.ArcTo(
            oval = oval,
            startAngleDegrees = startAngleDegrees,
            sweepAngleDegrees = sweepAngleDegrees,
            forceMoveTo = forceMoveTo,
        )
        return this
    }

    fun close(): PathBuilder {
        commands += PathCommand.Close
        return this
    }

    fun build(): PathModel {
        return PathModel(
            fillType = fillType,
            commands = commands.toList(),
        )
    }
}

fun path(builder: PathBuilder.() -> Unit): PathModel {
    return PathBuilder().apply(builder).build()
}
