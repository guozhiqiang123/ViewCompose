package com.viewcompose.widget.core

import android.content.Context
import android.os.LocaleList
import android.view.View
import java.util.Locale

object AndroidEnvironmentBridge {
    fun fromContext(context: Context): UiEnvironmentValues {
        val configuration = context.resources.configuration
        val displayMetrics = context.resources.displayMetrics
        return UiEnvironmentValues(
            density = UiDensity(
                density = displayMetrics.density,
                scaledDensity = displayMetrics.density * configuration.fontScale,
            ),
            localeTags = EnvironmentValueMapper.localeTags(configuration.locales),
            layoutDirection = EnvironmentValueMapper.layoutDirection(configuration.layoutDirection),
        )
    }
}

internal object EnvironmentValueMapper {
    fun localeTags(localeList: LocaleList?): List<String> {
        if (localeList == null || localeList.isEmpty) {
            return listOf(Locale.getDefault().toLanguageTag())
        }
        return buildList {
            for (index in 0 until localeList.size()) {
                add(localeList[index].toLanguageTag())
            }
        }
    }

    fun layoutDirection(direction: Int): UiLayoutDirection {
        return when (direction) {
            View.LAYOUT_DIRECTION_RTL -> UiLayoutDirection.Rtl
            else -> UiLayoutDirection.Ltr
        }
    }
}
