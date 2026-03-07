package com.viewcompose.widget.core

import android.view.View
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Locale

class AndroidEnvironmentBridgeTest {
    @Test
    fun `layout direction maps rtl and ltr`() {
        assertEquals(UiLayoutDirection.Rtl, EnvironmentValueMapper.layoutDirection(View.LAYOUT_DIRECTION_RTL))
        assertEquals(UiLayoutDirection.Ltr, EnvironmentValueMapper.layoutDirection(View.LAYOUT_DIRECTION_LTR))
    }

    @Test
    fun `locale mapper falls back to default locale when list is absent`() {
        val locale = Locale.getDefault().toLanguageTag()

        assertEquals(listOf(locale), EnvironmentValueMapper.localeTags(null))
    }
}
