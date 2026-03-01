package com.gzq.uiframework.benchmark

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import org.junit.Assert.assertNotNull

internal fun MacrobenchmarkScope.startDemoAndWait() {
    pressHome()
    startActivityAndWait()
    device.wait(Until.hasObject(By.text("Demo Theme")), UI_WAIT_TIMEOUT_MS)
}

internal fun MacrobenchmarkScope.waitForText(text: String) {
    device.wait(Until.hasObject(By.text(text)), UI_WAIT_TIMEOUT_MS)
}

internal fun MacrobenchmarkScope.scrollUntilText(
    text: String,
    maxSwipes: Int = 6,
) {
    repeat(maxSwipes + 1) { attempt ->
        if (device.hasObject(By.text(text))) {
            return
        }
        if (attempt < maxSwipes) {
            swipePageUp()
        }
    }
    waitForText(text)
}

internal fun MacrobenchmarkScope.clickText(text: String) {
    scrollUntilText(text)
    val node = device.findObject(By.text(text))
    assertNotNull("Expected to find text: $text", node)
    node!!.click()
    device.waitForIdle()
}

internal fun MacrobenchmarkScope.swipePageUp() {
    val width = device.displayWidth
    val height = device.displayHeight
    device.swipe(
        width / 2,
        (height * 0.78f).toInt(),
        width / 2,
        (height * 0.22f).toInt(),
        20,
    )
    device.waitForIdle()
}
