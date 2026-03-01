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
    fun inputBenchmarkTextField_keepsLabelValueAndSupportingTextFullyVisible() {
        launchDemoActivity(InputActivity::class.java).use { scenario ->
            waitForUiIdle()
            scenario.onActivity { activity ->
                val label = activity.requireTextView("Benchmark field")
                val value = activity.requireTextView("Compact payload")
                val supporting = activity.requireTextView("Compact supporting copy.")
                assertViewCompletelyVisible(label)
                assertViewCompletelyVisible(value)
                assertViewCompletelyVisible(supporting)
                assertTextFitsVertically(value)
            }
        }
    }

    @Test
    fun feedbackPage_triggersSnackbarAndToastFlows() {
        launchDemoActivity(FeedbackActivity::class.java).use { scenario ->
            waitForUiIdle()
            scrollDeviceTextIntoView("Show Snackbar")
            scenario.onActivity { activity ->
                val showSnackbar = activity.requireTextView("Show Snackbar")
                val showToast = activity.requireTextView("Show Toast")
                assertViewFullyVisible(showSnackbar)
                assertViewFullyVisible(showToast)
                activity.clickTextView("Show Snackbar")
            }
            waitForUiIdle()
            scenario.onActivity { activity ->
                val message = activity.requireTextView("Feedback snackbar 1")
                val action = activity.requireTextView("Acknowledge")
                assertViewFullyVisible(message)
                assertViewFullyVisible(action)
                activity.clickTextView("Acknowledge")
            }
            waitForUiIdle()
            scenario.onActivity { activity ->
                val lastEvent = activity.requireTextView("Last event: Snackbar action 1")
                assertViewFullyVisible(lastEvent)
                activity.clickTextView("Show Toast")
            }
            waitForUiIdle()
            scenario.onActivity { activity ->
                val toastEvent = activity.requireTextView("Last event: Toast requested 1")
                assertViewFullyVisible(toastEvent)
                assertTextNotEllipsized(toastEvent)
            }
            scrollDeviceTextIntoView("Toast count: 1")
            captureDeviceScreenshot("feedback-transient-light")
            scenario.onActivity { activity ->
                val toastCount = activity.requireTextView("Toast count: 1")
                assertViewFullyVisible(toastCount)
                assertTextNotEllipsized(toastCount)
            }
        }
    }

    @Test
    fun inputStress_controlsRemainVisibleAndReadable() {
        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            InputActivity::class.java,
        ).putExtra(EXTRA_INPUT_PAGE_INDEX, 2)
        launchDemoActivity<InputActivity>(intent, themeMode = DemoThemeMode.Light).use { scenario ->
            waitForUiIdle()
            scrollDeviceTextIntoView("Expanded Copy")
            captureDeviceScreenshot("input-stress-light")
            scenario.onActivity { activity ->
                val expanded = activity.requireTextView("Expanded Copy")
                val readonly = activity.requireTextView("Editable Notes")
                val error = activity.requireTextView("Clear Error")
                assertViewFullyVisible(expanded)
                assertViewFullyVisible(readonly)
                assertViewFullyVisible(error)
                assertTextNotEllipsized(expanded)
                assertTextNotEllipsized(readonly)
                assertTextNotEllipsized(error)
            }
            scrollDeviceTextIntoView("Protected field")
            scenario.onActivity { activity ->
                val protectedField = activity.requireTextView("Protected field")
                assertViewFullyVisible(protectedField)
                assertTextNotEllipsized(protectedField)
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
    fun componentStyleOverride_updatesPrimaryTokenButtonBackground() {
        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            FoundationsActivity::class.java,
        ).putExtra(EXTRA_FOUNDATIONS_PAGE_INDEX, 1)
        launchDemoActivity<FoundationsActivity>(intent, themeMode = DemoThemeMode.Light).use { scenario ->
            waitForUiIdle()
            scrollDeviceTextIntoView("Primary Token")
            captureDeviceScreenshot("foundations-component-override-light")
            scenario.onActivity { activity ->
                val tokenButton = activity.requireTextView("Primary Token")
                assertViewFullyVisible(tokenButton)
                assertTextNotEllipsized(tokenButton)
                assertViewBackgroundColor(
                    view = tokenButton,
                    expectedColor = DemoThemeTokens.light.colors.textPrimary,
                )
            }
        }
    }

    @Test
    fun foundationsMedia_viewsRemainVisibleAfterScroll() {
        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            FoundationsActivity::class.java,
        ).putExtra(EXTRA_FOUNDATIONS_PAGE_INDEX, 2)
        launchDemoActivity<FoundationsActivity>(intent, themeMode = DemoThemeMode.Light).use { scenario ->
            waitForUiIdle()
            scrollDeviceTextIntoView("Image + Icon")
            scrollDeviceDescriptionIntoView("Remote image")
            scenario.onActivity { activity ->
                val remoteImage = activity.requireViewWithContentDescription("Remote image")
                assertViewFullyVisible(remoteImage)
            }
            scrollDeviceDescriptionIntoView("Fallback image")
            scenario.onActivity { activity ->
                val fallbackImage = activity.requireViewWithContentDescription("Fallback image")
                assertViewFullyVisible(fallbackImage)
            }
            scrollDeviceDescriptionIntoView("Primary icon button")
            captureDeviceScreenshot("foundations-media-light")
            scenario.onActivity { activity ->
                val iconButton = activity.requireViewWithContentDescription("Primary icon button")
                assertViewFullyVisible(iconButton)
            }
        }
    }

    @Test
    fun layoutsEdge_viewsRemainVisibleAfterPageJump() {
        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            LayoutsActivity::class.java,
        ).putExtra(EXTRA_LAYOUTS_PAGE_INDEX, 2)
        launchDemoActivity<LayoutsActivity>(intent, themeMode = DemoThemeMode.Light).use { scenario ->
            waitForUiIdle()
            scrollDeviceTextIntoView("Use Long Labels")
            captureDeviceScreenshot("layouts-edge-light")
            scenario.onActivity { activity ->
                val toggle = activity.requireTextView("Use Long Labels")
                val weighted = activity.requireTextView("Weighted")
                val action = activity.requireTextView("Action")
                val icon = activity.requireViewWithContentDescription("Layout probe icon")
                assertViewFullyVisible(toggle)
                assertViewFullyVisible(weighted)
                assertViewFullyVisible(action)
                assertViewFullyVisible(icon)
                assertTextNotEllipsized(toggle)
                assertTextNotEllipsized(weighted)
                assertTextNotEllipsized(action)
            }
        }
    }

    @Test
    fun collectionsStress_toggleUpdatesVisibleControls() {
        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            CollectionsActivity::class.java,
        ).putExtra(EXTRA_COLLECTIONS_PAGE_INDEX, 2)
        launchDemoActivity<CollectionsActivity>(intent, themeMode = DemoThemeMode.Light).use { scenario ->
            waitForUiIdle()
            scrollDeviceTextIntoView("Insert X")
            scenario.onActivity { activity ->
                activity.clickTextView("Insert X")
            }
            waitForUiIdle()
            captureDeviceScreenshot("collections-stress-light")
            scenario.onActivity { activity ->
                val remove = activity.requireTextView("Remove X")
                val rotate = activity.requireTextView("Rotate Order")
                val activeIds = activity.requireTextView("Active ids: X -> A -> B -> C -> D")
                assertViewFullyVisible(remove)
                assertViewFullyVisible(rotate)
                assertViewFullyVisible(activeIds)
                assertTextNotEllipsized(remove)
                assertTextNotEllipsized(rotate)
                assertTextNotEllipsized(activeIds)
            }
        }
    }

    @Test
    fun statePatchStress_refreshesStableTabPagerPageContent() {
        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            StateActivity::class.java,
        ).putExtra(EXTRA_STATE_PAGE_INDEX, 2)
        launchDemoActivity<StateActivity>(intent, themeMode = DemoThemeMode.Light).use { scenario ->
            waitForUiIdle()
            scrollDeviceTextIntoView("Advance patch state 0")
            scenario.onActivity { activity ->
                activity.clickTextView("Advance patch state 0")
            }
            waitForUiIdle()
            scrollDeviceTextIntoView("Stable summary 1")
            captureDeviceScreenshot("state-patch-stable-tab-light")
            scenario.onActivity { activity ->
                val summary = activity.requireTextView("Stable summary 1")
                assertViewFullyVisible(summary)
                assertTextNotEllipsized(summary)
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
