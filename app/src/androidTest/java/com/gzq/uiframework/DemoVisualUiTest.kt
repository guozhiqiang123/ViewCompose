package com.gzq.uiframework

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DemoVisualUiTest {
    @Test
    fun foundationsBenchmarkButtons_areVisibleAndNotEllipsized() {
        launchDemoActivity(FoundationsActivity::class.java).use { scenario ->
            waitForUiIdle()
            captureDeviceScreenshot("foundations-benchmark-light")
            scenario.onActivity { activity ->
                val primary = activity.requireTextView("Foundations Benchmark Off")
                val reset = activity.requireTextView("Reset Foundations Benchmark")
                assertViewFullyVisible(primary)
                assertViewFullyVisible(reset)
                assertTextNotEllipsized(primary)
                assertTextNotEllipsized(reset)
            }
        }
    }

    @Test
    fun inputPage_controlsStayVisibleAndResetFormIsNotEllipsized() {
        launchDemoActivity(InputActivity::class.java).use { scenario ->
            waitForUiIdle()
            captureDeviceScreenshot("input-fields-light")
            scenario.onActivity { activity ->
                val benchmark = activity.requireTextView("Input Benchmark Compact")
                val resetBenchmark = activity.requireTextView("Reset Input Benchmark")
                val benchmarkLabel = activity.requireTextView("Benchmark field")
                assertViewFullyVisible(benchmark)
                assertViewFullyVisible(resetBenchmark)
                assertViewFullyVisible(benchmarkLabel)
                assertTextNotEllipsized(benchmark)
                assertTextNotEllipsized(resetBenchmark)
                assertTextNotEllipsized(benchmarkLabel)
            }
        }
    }

    @Test
    fun darkTheme_appliesExpectedTitleColorOnFoundationsPage() {
        launchDemoActivity(FoundationsActivity::class.java, themeMode = DemoThemeMode.Light).use { scenario ->
            waitForUiIdle()
            onView(withText("Dark")).perform(click())
            waitForUiIdle()
            captureDeviceScreenshot("foundations-dark-theme")
            scenario.onActivity { activity ->
                val title = activity.requireTextView("Foundations")
                assertViewFullyVisible(title)
                assertTextNotEllipsized(title)
                org.junit.Assert.assertEquals(
                    "Expected title color to match dark theme primary text token",
                    DemoThemeTokens.dark.colors.textPrimary,
                    title.currentTextColor,
                )
            }
        }
    }
}
