package com.gzq.uiframework

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WidgetShowcaseUiTest {
    @Test
    fun clickingListItemNavigatesToWidgetDetail() {
        launchDemoActivity(WidgetShowcaseActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                activity.clickTextView("Text")
            }
            waitForUiIdle()
            scenario.onActivity { activity ->
                activity.requireTextView("← 返回控件列表")
            }
        }
    }
}

