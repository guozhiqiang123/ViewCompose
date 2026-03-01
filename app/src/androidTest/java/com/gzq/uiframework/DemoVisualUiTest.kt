package com.gzq.uiframework

import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DemoVisualUiTest {
    @Test
    fun layoutsBenchmarkButtons_areVisibleAndNotEllipsized() {
        launchDemoActivity(LayoutsActivity::class.java).use { scenario ->
            waitForUiIdle()
            captureDeviceScreenshot("layouts-benchmark-light")
            scenario.onActivity { activity ->
                val toggle = activity.requireTextView("Layouts Benchmark Compact")
                val reset = activity.requireTextView("Reset Layouts Benchmark")
                assertViewFullyVisible(toggle)
                assertViewFullyVisible(reset)
                assertTextNotEllipsized(toggle)
                assertTextNotEllipsized(reset)
            }
        }
    }

    @Test
    fun collectionsBenchmarkControls_areVisibleAndNotEllipsized() {
        launchDemoActivity(CollectionsActivity::class.java).use { scenario ->
            waitForUiIdle()
            captureDeviceScreenshot("collections-benchmark-light")
            scenario.onActivity { activity ->
                val toggle = activity.requireTextView("Collections Benchmark A-B-C")
                val reset = activity.requireTextView("Reset Collections Benchmark")
                assertViewFullyVisible(toggle)
                assertViewFullyVisible(reset)
                assertTextNotEllipsized(toggle)
                assertTextNotEllipsized(reset)
            }
        }
    }

    @Test
    fun interopBenchmarkControls_andNativeMirror_areVisible() {
        launchDemoActivity(InteropActivity::class.java).use { scenario ->
            waitForUiIdle()
            captureDeviceScreenshot("interop-benchmark-light")
            scenario.onActivity { activity ->
                val toggle = activity.requireTextView("Interop Benchmark Primary")
                val reset = activity.requireTextView("Reset Interop Benchmark")
                val nativeMirror = activity.requireTextView("Native benchmark TextView: primary")
                assertViewFullyVisible(toggle)
                assertViewFullyVisible(reset)
                assertViewFullyVisible(nativeMirror)
                assertTextNotEllipsized(toggle)
                assertTextNotEllipsized(reset)
            }
        }
    }

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
    fun scopedThemeOverride_updatesPrimaryButtonBackground() {
        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            FoundationsActivity::class.java,
        ).putExtra(EXTRA_FOUNDATIONS_PAGE_INDEX, 1)
        launchDemoActivity<FoundationsActivity>(intent, themeMode = DemoThemeMode.Light).use { scenario ->
            waitForUiIdle()
            captureDeviceScreenshot("foundations-theme-override-light")
            scenario.onActivity { activity ->
                val accentButton = activity.requireTextView("Accent As Primary")
                assertViewFullyVisible(accentButton)
                assertTextNotEllipsized(accentButton)
                assertViewBackgroundColor(
                    view = accentButton,
                    expectedColor = DemoThemeTokens.light.colors.accent,
                )
            }
        }
    }

    @Test
    fun darkTheme_appliesExpectedTitleColorOnFoundationsPage() {
        launchDemoActivity(FoundationsActivity::class.java, themeMode = DemoThemeMode.Light).use { scenario ->
            waitForUiIdle()
            clickDeviceText("Dark")
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
