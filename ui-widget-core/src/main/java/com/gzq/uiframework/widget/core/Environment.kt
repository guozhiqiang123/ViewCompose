package com.gzq.uiframework.widget.core

import android.content.Context
import kotlin.math.roundToInt

data class UiDensity(
    val density: Float,
    val scaledDensity: Float,
) {
    fun dp(value: Int): Int = (value * density).roundToInt()

    fun sp(value: Int): Int = (value * scaledDensity).roundToInt()
}

enum class UiLayoutDirection {
    Ltr,
    Rtl,
}

data class UiEnvironmentValues(
    val density: UiDensity,
    val localeTags: List<String>,
    val layoutDirection: UiLayoutDirection,
)

object UiEnvironmentDefaults {
    fun values(): UiEnvironmentValues {
        return UiEnvironmentValues(
            density = UiDensity(
                density = 1f,
                scaledDensity = 1f,
            ),
            localeTags = listOf("und"),
            layoutDirection = UiLayoutDirection.Ltr,
        )
    }
}

private val LocalEnvironment = LocalValue(UiEnvironmentDefaults::values)

object Environment {
    val density: UiDensity
        get() = LocalContext.current(LocalEnvironment).density

    val localeTags: List<String>
        get() = LocalContext.current(LocalEnvironment).localeTags

    val layoutDirection: UiLayoutDirection
        get() = LocalContext.current(LocalEnvironment).layoutDirection
}

fun UiTreeBuilder.UiEnvironment(
    values: UiEnvironmentValues? = null,
    androidContext: Context? = null,
    content: UiTreeBuilder.() -> Unit,
) {
    val resolvedValues = values
        ?: androidContext?.let(AndroidEnvironmentBridge::fromContext)
        ?: UiEnvironmentDefaults.values()
    LocalContext.provide(LocalEnvironment, resolvedValues) {
        content()
    }
}
