package com.gzq.uiframework

import android.app.Activity
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.test.core.app.ActivityScenario
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import java.io.File

internal fun <A : Activity> launchDemoActivity(
    activityClass: Class<A>,
    themeMode: DemoThemeMode = DemoThemeMode.Light,
): ActivityScenario<A> {
    DemoThemeSession.mode = themeMode
    return ActivityScenario.launch(activityClass)
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

internal fun Activity.requireTextView(text: String): TextView {
    val root = findViewById<ViewGroup>(android.R.id.content)
    val view = findTextViewByText(root, text)
    assertNotNull("Expected to find TextView with text: $text", view)
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
