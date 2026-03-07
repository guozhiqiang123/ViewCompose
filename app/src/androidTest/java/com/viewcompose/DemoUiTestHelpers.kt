package com.viewcompose

import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.InsetDrawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.RippleDrawable
import android.view.View
import android.view.ViewParent
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiScrollable
import androidx.test.uiautomator.UiSelector
import androidx.test.uiautomator.Until
import androidx.lifecycle.Lifecycle
import com.viewcompose.renderer.R as RendererR
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import java.io.File

internal data class RecyclerViewportAnchor(
    val position: Int,
    val offset: Int,
)

internal fun <A : Activity> launchDemoActivity(
    activityClass: Class<A>,
    themeMode: DemoThemeMode = DemoThemeMode.Light,
): ActivityScenario<A> {
    DemoThemeSession.mode = themeMode
    return ActivityScenario.launch(activityClass).also { scenario ->
        scenario.moveToState(Lifecycle.State.RESUMED)
    }
}

internal fun <A : Activity> launchDemoActivity(
    intent: Intent,
    themeMode: DemoThemeMode = DemoThemeMode.Light,
): ActivityScenario<A> {
    DemoThemeSession.mode = themeMode
    return ActivityScenario.launch<A>(intent).also { scenario ->
        scenario.moveToState(Lifecycle.State.RESUMED)
    }
}

internal fun waitForUiIdle() {
    InstrumentationRegistry.getInstrumentation().waitForIdleSync()
}

internal fun captureDeviceScreenshot(name: String) {
    val context = InstrumentationRegistry.getInstrumentation().targetContext
    val directory = File(context.getExternalFilesDir(null), "ui-test-screenshots")
    if (!directory.exists()) {
        directory.mkdirs()
    }
    UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        .takeScreenshot(File(directory, "$name.png"))
}

internal fun clickDeviceText(text: String) {
    val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    val node = device.wait(Until.hasObject(By.text(text)), 5_000)
    assertTrue("Expected device text target: $text", node)
    val target = device.findObject(By.text(text))
    assertNotNull("Expected device object for text: $text", target)
    target!!.click()
    waitForUiIdle()
}

internal fun assertDeviceTextVisible(text: String) {
    val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    val node = device.wait(Until.hasObject(By.text(text)), 5_000)
    assertTrue("Expected device text target: $text", node)
    val target = device.findObject(By.text(text))
    assertNotNull("Expected visible device object for text: $text", target)
    val bounds = target!!.visibleBounds
    assertTrue("Expected visible width > 0 for device text: $text", bounds.width() > 0)
    assertTrue("Expected visible height > 0 for device text: $text", bounds.height() > 0)
}

internal fun scrollDeviceTextIntoView(text: String) {
    UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    val scrollable = UiScrollable(UiSelector().scrollable(true))
    scrollable.setAsVerticalList()
    val found = scrollable.scrollTextIntoView(text) || scrollable.scrollIntoView(UiSelector().text(text))
    assertTrue("Expected to scroll text target into view: $text", found)
    waitForUiIdle()
}

internal fun scrollDeviceDescriptionIntoView(description: String) {
    UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    val scrollable = UiScrollable(UiSelector().scrollable(true))
    scrollable.setAsVerticalList()
    val found = scrollable.scrollDescriptionIntoView(description) ||
        scrollable.scrollIntoView(UiSelector().description(description))
    assertTrue("Expected to scroll description target into view: $description", found)
    waitForUiIdle()
}

internal fun Activity.requireTextView(text: String): TextView {
    val root = findViewById<ViewGroup>(android.R.id.content)
    val view = findTextViewByText(root, text)
    assertNotNull("Expected to find TextView with text: $text", view)
    return view!!
}

internal fun Activity.requireViewByTestTag(tag: String): View {
    return requireViewByTestTagVisible(tag)
}

internal fun Activity.requireTextViewByTestTag(tag: String): TextView {
    return requireTextViewByTestTagVisible(tag)
}

internal fun Activity.requireViewByTestTagVisible(
    tag: String,
    maxScrollAttempts: Int = 24,
): View {
    val root = findViewById<ViewGroup>(android.R.id.content)
    repeat(maxScrollAttempts) {
        val view = findViewByTestTag(root, tag)
        if (view != null && isViewVisible(view)) {
            return view
        }
        val recyclerView = findFirstRecyclerView(root) ?: return@repeat
        val delta = (recyclerView.height * 0.7f).toInt().coerceAtLeast(1)
        recyclerView.scrollBy(0, delta)
    }
    val view = findViewByTestTag(root, tag)
    assertNotNull("Expected to find view with testTag: $tag", view)
    assertViewFullyVisible(view!!)
    return view
}

internal fun Activity.requireTextViewByTestTagVisible(
    tag: String,
    maxScrollAttempts: Int = 24,
): TextView {
    val view = requireViewByTestTagVisible(tag, maxScrollAttempts)
    assertTrue("Expected testTag=$tag to map to TextView, but was ${view.javaClass.simpleName}", view is TextView)
    return view as TextView
}

internal fun Activity.clickByTestTag(tag: String) {
    var current: View? = requireViewByTestTagVisible(tag)
    while (current != null && !current.isClickable) {
        current = current.parent as? View
    }
    assertNotNull("Expected clickable host for testTag: $tag", current)
    current!!.performClick()
}

internal fun Activity.focusInputByTestTag(tag: String) {
    val host = requireViewByTestTagVisible(tag)
    val input = findFirstEditText(host)
    assertNotNull("Expected EditText descendant for testTag: $tag", input)
    input!!.requestFocus()
}

internal fun Activity.readFirstRecyclerAnchor(): RecyclerViewportAnchor? {
    val root = findViewById<ViewGroup>(android.R.id.content)
    val recyclerView = findFirstRecyclerView(root) ?: return null
    val layoutManager = recyclerView.layoutManager as? LinearLayoutManager ?: return null
    val position = layoutManager.findFirstVisibleItemPosition()
    if (position == RecyclerView.NO_POSITION) {
        return null
    }
    val anchorView = layoutManager.findViewByPosition(position)
    val offset = if (layoutManager.orientation == RecyclerView.HORIZONTAL) {
        (anchorView?.left ?: recyclerView.paddingLeft) - recyclerView.paddingLeft
    } else {
        (anchorView?.top ?: recyclerView.paddingTop) - recyclerView.paddingTop
    }
    return RecyclerViewportAnchor(position = position, offset = offset)
}

internal fun Activity.clickTextView(text: String) {
    var current: View? = requireTextView(text)
    while (current != null && !current.isClickable) {
        current = (current.parent as? View)
    }
    assertNotNull("Expected clickable host for text: $text", current)
    current!!.performClick()
}

internal fun Activity.requireViewWithContentDescription(description: String): View {
    val root = findViewById<ViewGroup>(android.R.id.content)
    val view = findViewByContentDescription(root, description)
    assertNotNull("Expected to find view with contentDescription: $description", view)
    return view!!
}

internal fun assertViewFullyVisible(view: View) {
    assertTrue("Expected view to be shown", view.isShown)
    val rect = Rect()
    val visible = view.getGlobalVisibleRect(rect)
    assertTrue("Expected view to have visible rect", visible)
    assertTrue("Expected visible width > 0", rect.width() > 0)
    assertTrue("Expected visible height > 0", rect.height() > 0)
    assertTrue("Expected measured width > 0", view.width > 0)
    assertTrue("Expected measured height > 0", view.height > 0)
}

internal fun assertViewCompletelyVisible(view: View) {
    assertViewFullyVisible(view)
    val rect = Rect()
    val visible = view.getGlobalVisibleRect(rect)
    assertTrue("Expected view to have global rect", visible)
    assertEquals("Expected full width to be visible", view.width, rect.width())
    assertEquals("Expected full height to be visible", view.height, rect.height())
}

internal fun assertTextFitsVertically(textView: TextView) {
    val layout = textView.layout
    assertNotNull("Expected layout for text: ${textView.text}", layout)
    layout ?: return
    val contentBottom = layout.getLineBottom(layout.lineCount - 1)
    val availableHeight = textView.height - textView.compoundPaddingTop - textView.compoundPaddingBottom
    assertTrue(
        "Expected text to fit vertically for text: ${textView.text}",
        contentBottom <= availableHeight,
    )
}

internal fun assertTextNotEllipsized(textView: TextView) {
    val layout = textView.layout
    assertNotNull("Expected layout for text: ${textView.text}", layout)
    layout ?: return
    for (line in 0 until layout.lineCount) {
        assertEquals(
            "Expected no ellipsis for text: ${textView.text}",
            0,
            layout.getEllipsisCount(line),
        )
    }
    assertFalse(
        "Expected text to stay on-screen for text: ${textView.text}",
        textView.text.isNotEmpty() && textView.width <= textView.compoundPaddingLeft + textView.compoundPaddingRight,
    )
}

internal fun findTextViewByText(root: View, text: String): TextView? {
    if (root is TextView && root.text?.toString() == text) {
        return root
    }
    if (root is ViewGroup) {
        for (index in 0 until root.childCount) {
            val match = findTextViewByText(root.getChildAt(index), text)
            if (match != null) {
                return match
            }
        }
    }
    return null
}

internal fun findViewByContentDescription(root: View, description: String): View? {
    if (root.contentDescription?.toString() == description) {
        return root
    }
    if (root is ViewGroup) {
        for (index in 0 until root.childCount) {
            val match = findViewByContentDescription(root.getChildAt(index), description)
            if (match != null) {
                return match
            }
        }
    }
    return null
}

internal fun findViewByTestTag(root: View, tag: String): View? {
    if (root.getTag(RendererR.id.ui_framework_test_tag) == tag) {
        return root
    }
    if (root is ViewGroup) {
        for (index in 0 until root.childCount) {
            val match = findViewByTestTag(root.getChildAt(index), tag)
            if (match != null) {
                return match
            }
        }
    }
    return null
}

private fun findFirstRecyclerView(root: View): RecyclerView? {
    if (root is RecyclerView) {
        return root
    }
    if (root is ViewGroup) {
        for (index in 0 until root.childCount) {
            val match = findFirstRecyclerView(root.getChildAt(index))
            if (match != null) {
                return match
            }
        }
    }
    return null
}

private fun findFirstEditText(root: View): EditText? {
    if (root is EditText) {
        return root
    }
    if (root is ViewGroup) {
        for (index in 0 until root.childCount) {
            val match = findFirstEditText(root.getChildAt(index))
            if (match != null) {
                return match
            }
        }
    }
    return null
}

private fun isViewVisible(view: View): Boolean {
    if (!view.isShown) return false
    val rect = Rect()
    return view.getGlobalVisibleRect(rect) && rect.width() > 0 && rect.height() > 0
}

internal fun assertViewBackgroundColor(view: View, expectedColor: Int) {
    val actual = resolveDrawableColor(view.background)
    assertNotNull("Expected background drawable color for ${view.javaClass.simpleName}", actual)
    assertEquals(
        "Expected background color to match theme token",
        expectedColor,
        actual,
    )
}

private fun resolveDrawableColor(drawable: Drawable?): Int? {
    return when (drawable) {
        null -> null
        is RippleDrawable -> {
            resolveDrawableColor(drawable.getDrawable(0))
                ?: resolveDrawableColor(drawable.findDrawableByLayerId(android.R.id.mask))
        }
        is InsetDrawable -> resolveDrawableColor(drawable.drawable)
        is LayerDrawable -> {
            for (index in 0 until drawable.numberOfLayers) {
                val color = resolveDrawableColor(drawable.getDrawable(index))
                if (color != null) {
                    return color
                }
            }
            null
        }
        is GradientDrawable -> drawable.color?.defaultColor
        else -> null
    }
}
