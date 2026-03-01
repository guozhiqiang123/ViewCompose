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

internal fun MacrobenchmarkScope.startCatalogAndWait() {
    startDemoAndWait()
    if (!device.wait(Until.hasObject(By.text("Capability Modules")), 1_000)) {
        val backNode = device.findObject(By.text("Back to catalog"))
        if (backNode != null) {
            backNode.click()
            device.waitForIdle()
        } else {
            device.pressBack()
            device.waitForIdle()
        }
    }
    waitForText("Capability Modules")
    scrollToPageTop()
    waitForText("Capability Modules")
}

internal fun MacrobenchmarkScope.startDemoActivityAndWait(
    moduleKey: String,
    expectedText: String,
) {
    pressHome()
    startActivityAndWait { intent ->
        intent.putExtra("demo_module_key", moduleKey)
    }
    waitForText(expectedText)
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

internal fun MacrobenchmarkScope.openDemoModule(title: String) {
    clickText("Open $title")
}

internal fun MacrobenchmarkScope.returnToCatalog() {
    clickText("Back to catalog")
    waitForText("Capability Modules")
}

internal fun MacrobenchmarkScope.clickChapterTab(text: String) {
    scrollTabStripUntilText(text)
    val node = device.findObject(By.text(text))
    assertNotNull("Expected to find chapter tab: $text", node)
    node!!.click()
    device.waitForIdle()
}

internal fun MacrobenchmarkScope.scrollTabStripUntilText(
    text: String,
    maxSwipes: Int = 6,
) {
    repeat(maxSwipes + 1) { attempt ->
        if (device.hasObject(By.text(text))) {
            return
        }
        if (attempt < maxSwipes) {
            swipeTabStripLeft()
        }
    }
    waitForText(text)
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

internal fun MacrobenchmarkScope.swipePageDown() {
    val width = device.displayWidth
    val height = device.displayHeight
    device.swipe(
        width / 2,
        (height * 0.22f).toInt(),
        width / 2,
        (height * 0.78f).toInt(),
        20,
    )
    device.waitForIdle()
}

internal fun MacrobenchmarkScope.scrollToPageTop(
    attempts: Int = 4,
) {
    repeat(attempts) {
        swipePageDown()
    }
}

internal fun MacrobenchmarkScope.swipeTabStripLeft() {
    val width = device.displayWidth
    val height = device.displayHeight
    val y = (height * 0.32f).toInt()
    device.swipe(
        (width * 0.82f).toInt(),
        y,
        (width * 0.18f).toInt(),
        y,
        16,
    )
    device.waitForIdle()
}
