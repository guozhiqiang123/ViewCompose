package com.viewcompose.graphics.core

data class Offset(
    val x: Float,
    val y: Float,
) {
    companion object {
        val Zero = Offset(0f, 0f)
    }
}

data class Size(
    val width: Float,
    val height: Float,
) {
    companion object {
        val Zero = Size(0f, 0f)
    }
}

data class Rect(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float,
) {
    val width: Float
        get() = right - left

    val height: Float
        get() = bottom - top

    companion object {
        val Zero = Rect(0f, 0f, 0f, 0f)
    }
}

data class Radius(
    val x: Float,
    val y: Float,
) {
    companion object {
        val Zero = Radius(0f, 0f)
    }
}

data class RoundRect(
    val rect: Rect,
    val topLeft: Radius = Radius.Zero,
    val topRight: Radius = Radius.Zero,
    val bottomRight: Radius = Radius.Zero,
    val bottomLeft: Radius = Radius.Zero,
)

class Matrix3(
    values: FloatArray = identityValues(),
) {
    val values: FloatArray = values.copyOf()

    init {
        require(this.values.size == 9) { "Matrix3 requires 9 values." }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Matrix3) return false
        return values.contentEquals(other.values)
    }

    override fun hashCode(): Int {
        return values.contentHashCode()
    }

    companion object {
        fun identity(): Matrix3 = Matrix3(identityValues())

        private fun identityValues(): FloatArray {
            return floatArrayOf(
                1f, 0f, 0f,
                0f, 1f, 0f,
                0f, 0f, 1f,
            )
        }
    }
}
