package com.gzq.uiframework

import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DemoVisualUiTest {
    @Test
    fun layoutsBenchmarkControls_areVisibleAndNotEllipsized() {
        launchDemoActivity(LayoutsActivity::class.java).use { scenario ->
            waitForUiIdle()
            captureDeviceScreenshot("layouts-benchmark-light")
            scenario.onActivity { activity ->
                val toggle = activity.requireTextViewByTestTag(DemoTestTags.LAYOUTS_BENCHMARK_TOGGLE)
                val reset = activity.requireTextViewByTestTag(DemoTestTags.LAYOUTS_BENCHMARK_RESET)
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
                val toggle = activity.requireTextViewByTestTag(DemoTestTags.COLLECTIONS_BENCHMARK_TOGGLE)
                val reset = activity.requireTextViewByTestTag(DemoTestTags.COLLECTIONS_BENCHMARK_RESET)
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
                val toggle = activity.requireTextViewByTestTag(DemoTestTags.INTEROP_BENCHMARK_TOGGLE)
                val reset = activity.requireTextViewByTestTag(DemoTestTags.INTEROP_BENCHMARK_RESET)
                val nativeMirror = activity.requireTextViewByTestTag(DemoTestTags.INTEROP_BENCHMARK_NATIVE_TEXT)
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
                val toggle = activity.requireTextViewByTestTag(DemoTestTags.FOUNDATIONS_BENCHMARK_TOGGLE)
                val reset = activity.requireTextViewByTestTag(DemoTestTags.FOUNDATIONS_BENCHMARK_RESET)
                assertViewFullyVisible(toggle)
                assertViewFullyVisible(reset)
                assertTextNotEllipsized(toggle)
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
                val benchmark = activity.requireTextViewByTestTag(DemoTestTags.INPUT_BENCHMARK_TOGGLE)
                val resetBenchmark = activity.requireTextViewByTestTag(DemoTestTags.INPUT_BENCHMARK_RESET)
                val benchmarkField = activity.requireViewByTestTag(DemoTestTags.INPUT_BENCHMARK_FIELD)
                assertViewFullyVisible(benchmark)
                assertViewFullyVisible(resetBenchmark)
                assertViewFullyVisible(benchmarkField)
                assertTextNotEllipsized(benchmark)
                assertTextNotEllipsized(resetBenchmark)
            }
        }
    }

    @Test
    fun feedbackPage_triggersTransientFlows() {
        launchDemoActivity(FeedbackActivity::class.java).use { scenario ->
            waitForUiIdle()
            scenario.onActivity { activity ->
                activity.clickByTestTag(DemoTestTags.FEEDBACK_SHOW_SNACKBAR)
                activity.clickByTestTag(DemoTestTags.FEEDBACK_SHOW_TOAST)
                activity.clickByTestTag(DemoTestTags.FEEDBACK_SHOW_DIALOG)
                activity.clickByTestTag(DemoTestTags.FEEDBACK_SHOW_POPUP)
            }
            waitForUiIdle()
            captureDeviceScreenshot("feedback-transient-light")
            scenario.onActivity { activity ->
                val dialogCount = activity.requireTextViewByTestTag(DemoTestTags.FEEDBACK_DIALOG_COUNT)
                val popupCount = activity.requireTextViewByTestTag(DemoTestTags.FEEDBACK_POPUP_COUNT)
                val toastCount = activity.requireTextViewByTestTag(DemoTestTags.FEEDBACK_TOAST_COUNT)
                assertViewFullyVisible(dialogCount)
                assertViewFullyVisible(popupCount)
                assertViewFullyVisible(toastCount)
                assertEquals(1, extractCount(dialogCount.text.toString()))
                assertEquals(1, extractCount(popupCount.text.toString()))
                assertEquals(1, extractCount(toastCount.text.toString()))
                activity.clickByTestTag(DemoTestTags.FEEDBACK_RESET)
            }
            waitForUiIdle()
            scenario.onActivity { activity ->
                val dialogCount = activity.requireTextViewByTestTag(DemoTestTags.FEEDBACK_DIALOG_COUNT)
                val popupCount = activity.requireTextViewByTestTag(DemoTestTags.FEEDBACK_POPUP_COUNT)
                val toastCount = activity.requireTextViewByTestTag(DemoTestTags.FEEDBACK_TOAST_COUNT)
                val lastEvent = activity.requireTextViewByTestTag(DemoTestTags.FEEDBACK_LAST_EVENT)
                assertEquals(0, extractCount(dialogCount.text.toString()))
                assertEquals(0, extractCount(popupCount.text.toString()))
                assertEquals(0, extractCount(toastCount.text.toString()))
                assertTrue(lastEvent.text.toString().contains("空闲"))
            }
        }
    }

    @Test
    fun feedbackPage_modalBottomSheet_showAndDismissFlow() {
        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            FeedbackActivity::class.java,
        ).putExtra(EXTRA_FEEDBACK_PAGE_INDEX, 1)
        launchDemoActivity<FeedbackActivity>(intent, themeMode = DemoThemeMode.Light).use { scenario ->
            waitForUiIdle()
            scenario.onActivity { activity ->
                activity.clickByTestTag(DemoTestTags.FEEDBACK_SHOW_BOTTOM_SHEET)
            }
            waitForUiIdle()
            assertDeviceTextVisible("底部弹窗")
            captureDeviceScreenshot("feedback-bottom-sheet-light")
            clickDeviceText("关闭")
            waitForUiIdle()
            scenario.onActivity { activity ->
                val lastEvent = activity.requireTextViewByTestTag(DemoTestTags.FEEDBACK_LAST_EVENT)
                assertTrue(lastEvent.text.toString().contains("BottomSheet 关闭"))
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
            captureDeviceScreenshot("input-stress-light")
            scenario.onActivity { activity ->
                val expanded = activity.requireTextViewByTestTag(DemoTestTags.INPUT_STRESS_EXPAND)
                val readonly = activity.requireTextViewByTestTag(DemoTestTags.INPUT_STRESS_READONLY)
                val error = activity.requireTextViewByTestTag(DemoTestTags.INPUT_STRESS_ERROR)
                val protectedField = activity.requireViewByTestTag(DemoTestTags.INPUT_STRESS_PROTECTED_FIELD)
                assertViewFullyVisible(expanded)
                assertViewFullyVisible(readonly)
                assertViewFullyVisible(error)
                assertViewFullyVisible(protectedField)
                assertTextNotEllipsized(expanded)
                assertTextNotEllipsized(readonly)
                assertTextNotEllipsized(error)
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
                val accentButton = activity.requireTextViewByTestTag(DemoTestTags.FOUNDATIONS_ACCENT_PRIMARY)
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
            captureDeviceScreenshot("foundations-component-override-light")
            scenario.onActivity { activity ->
                val tokenButton = activity.requireTextViewByTestTag(DemoTestTags.FOUNDATIONS_PRIMARY_TOKEN)
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
            captureDeviceScreenshot("foundations-media-light")
            scenario.onActivity { activity ->
                val remoteImage = activity.requireViewByTestTag(DemoTestTags.FOUNDATIONS_REMOTE_IMAGE)
                val fallbackImage = activity.requireViewByTestTag(DemoTestTags.FOUNDATIONS_FALLBACK_IMAGE)
                val iconButton = activity.requireViewByTestTag(DemoTestTags.FOUNDATIONS_PRIMARY_ICON_BUTTON)
                assertViewFullyVisible(remoteImage)
                assertViewFullyVisible(fallbackImage)
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
            captureDeviceScreenshot("layouts-edge-light")
            scenario.onActivity { activity ->
                val toggle = activity.requireTextViewByTestTag(DemoTestTags.LAYOUTS_EDGE_TOGGLE)
                val weighted = activity.requireTextViewByTestTag(DemoTestTags.LAYOUTS_EDGE_WEIGHTED)
                val action = activity.requireTextViewByTestTag(DemoTestTags.LAYOUTS_EDGE_ACTION)
                val icon = activity.requireViewByTestTag(DemoTestTags.LAYOUTS_EDGE_PROBE_ICON)
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
            scenario.onActivity { activity ->
                val rotate = activity.requireTextViewByTestTag(DemoTestTags.COLLECTIONS_STRESS_ROTATE)
                val edge = activity.requireTextViewByTestTag(DemoTestTags.COLLECTIONS_STRESS_EDGE)
                assertViewFullyVisible(rotate)
                assertViewFullyVisible(edge)
                activity.clickByTestTag(DemoTestTags.COLLECTIONS_STRESS_EDGE)
            }
            waitForUiIdle()
            captureDeviceScreenshot("collections-stress-light")
            scenario.onActivity { activity ->
                val activeIds = activity.requireTextViewByTestTag(DemoTestTags.COLLECTIONS_STRESS_ACTIVE_IDS)
                assertViewFullyVisible(activeIds)
                assertTextNotEllipsized(activeIds)
                assertTrue(activeIds.text.toString().contains("X"))
            }
        }
    }

    @Test
    fun collectionsStress_rotateOrder_refreshesVisibleIdsAcrossToggles() {
        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            CollectionsActivity::class.java,
        ).putExtra(EXTRA_COLLECTIONS_PAGE_INDEX, 2)
        launchDemoActivity<CollectionsActivity>(intent, themeMode = DemoThemeMode.Light).use { scenario ->
            waitForUiIdle()
            scenario.onActivity { activity ->
                val ids = activity.requireTextViewByTestTag(DemoTestTags.COLLECTIONS_STRESS_ACTIVE_IDS)
                assertViewFullyVisible(ids)
                assertTrue(ids.text.toString().contains("A -> B -> C -> D"))
                activity.clickByTestTag(DemoTestTags.COLLECTIONS_STRESS_ROTATE)
            }
            waitForUiIdle()
            scenario.onActivity { activity ->
                val ids = activity.requireTextViewByTestTag(DemoTestTags.COLLECTIONS_STRESS_ACTIVE_IDS)
                assertViewFullyVisible(ids)
                assertTrue(ids.text.toString().contains("C -> D -> A -> B"))
                activity.clickByTestTag(DemoTestTags.COLLECTIONS_STRESS_ROTATE)
            }
            waitForUiIdle()
            captureDeviceScreenshot("collections-stress-rotate-light")
            scenario.onActivity { activity ->
                val ids = activity.requireTextViewByTestTag(DemoTestTags.COLLECTIONS_STRESS_ACTIVE_IDS)
                assertViewFullyVisible(ids)
                assertTrue(ids.text.toString().contains("A -> B -> C -> D"))
            }
        }
    }

    @Test
    fun collectionsGrid_spanToggle_refreshesVisibleItemContent() {
        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            CollectionsActivity::class.java,
        ).putExtra(EXTRA_COLLECTIONS_PAGE_INDEX, 5)
        launchDemoActivity<CollectionsActivity>(intent, themeMode = DemoThemeMode.Light).use { scenario ->
            waitForUiIdle()
            scenario.onActivity { activity ->
                val firstItem = activity.requireTextViewByTestTag(DemoTestTags.COLLECTIONS_GRID_FIRST_ITEM)
                assertViewFullyVisible(firstItem)
                assertTrue(firstItem.text.toString().contains("2列"))
                activity.clickByTestTag(DemoTestTags.COLLECTIONS_GRID_THREE_COLS)
            }
            waitForUiIdle()
            captureDeviceScreenshot("collections-grid-refresh-light")
            scenario.onActivity { activity ->
                val firstItem = activity.requireTextViewByTestTag(DemoTestTags.COLLECTIONS_GRID_FIRST_ITEM)
                assertViewFullyVisible(firstItem)
                assertTrue(firstItem.text.toString().contains("3列"))
            }
        }
    }

    @Test
    fun statePatchStress_refreshesStableTabContent() {
        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            StateActivity::class.java,
        ).putExtra(EXTRA_STATE_PAGE_INDEX, 2)
        launchDemoActivity<StateActivity>(intent, themeMode = DemoThemeMode.Light).use { scenario ->
            waitForUiIdle()
            scenario.onActivity { activity ->
                activity.clickByTestTag(DemoTestTags.STATE_PATCH_ADVANCE)
            }
            waitForUiIdle()
            captureDeviceScreenshot("state-patch-stable-tab-light")
            scenario.onActivity { activity ->
                val summary = activity.requireTextViewByTestTag(DemoTestTags.STATE_STABLE_SUMMARY)
                assertViewFullyVisible(summary)
                assertTextNotEllipsized(summary)
                assertTrue(summary.text.toString().contains("1"))
            }
        }
    }

    @Test
    fun statePatchStress_horizontalPagerContentUpdatesAcrossAdvances() {
        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            StateActivity::class.java,
        ).putExtra(EXTRA_STATE_PAGE_INDEX, 2)
        launchDemoActivity<StateActivity>(intent, themeMode = DemoThemeMode.Light).use { scenario ->
            waitForUiIdle()
            scenario.onActivity { activity ->
                activity.clickByTestTag(DemoTestTags.STATE_PATCH_ADVANCE)
                activity.clickByTestTag(DemoTestTags.STATE_PATCH_ADVANCE)
            }
            waitForUiIdle()
            captureDeviceScreenshot("state-patch-stable-tab-step2-light")
            scenario.onActivity { activity ->
                val summary = activity.requireTextViewByTestTag(DemoTestTags.STATE_STABLE_SUMMARY)
                assertViewFullyVisible(summary)
                assertTextNotEllipsized(summary)
                assertTrue(summary.text.toString().contains("2"))
            }
        }
    }

    @Test
    fun statePatchStress_verticalPagerContentUpdatesAcrossAdvances() {
        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            StateActivity::class.java,
        ).putExtra(EXTRA_STATE_PAGE_INDEX, 2)
        launchDemoActivity<StateActivity>(intent, themeMode = DemoThemeMode.Light).use { scenario ->
            waitForUiIdle()
            scenario.onActivity { activity ->
                activity.clickByTestTag(DemoTestTags.STATE_PATCH_ADVANCE)
                activity.clickByTestTag(DemoTestTags.STATE_PATCH_ADVANCE)
            }
            waitForUiIdle()
            captureDeviceScreenshot("state-patch-vertical-pager-step2-light")
            scenario.onActivity { activity ->
                val summary = activity.requireTextViewByTestTag(DemoTestTags.STATE_VERTICAL_PAGER_SUMMARY)
                assertViewFullyVisible(summary)
                assertTextNotEllipsized(summary)
                assertTrue(summary.text.toString().contains("2"))
            }
        }
    }

    private fun extractCount(text: String): Int {
        return "(\\d+)".toRegex().find(text)?.value?.toIntOrNull() ?: 0
    }
}
