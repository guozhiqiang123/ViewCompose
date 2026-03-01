package com.gzq.uiframework.benchmark

import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DemoInteractionBenchmark {
    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun chapterSwitch() = benchmarkRule.measureRepeated(
        packageName = TARGET_PACKAGE,
        metrics = listOf(FrameTimingMetric()),
        compilationMode = CompilationMode.Partial(),
        iterations = DEFAULT_ITERATIONS,
        startupMode = StartupMode.WARM,
        setupBlock = {
            startDemoAndWait()
            waitForText("State")
            waitForText("Layouts")
        },
    ) {
        clickText("State")
        waitForText("State & Effects")
        clickText("Layouts")
        waitForText("Layouts")
    }

    @Test
    fun themeSwitch() = benchmarkRule.measureRepeated(
        packageName = TARGET_PACKAGE,
        metrics = listOf(FrameTimingMetric()),
        compilationMode = CompilationMode.Partial(),
        iterations = DEFAULT_ITERATIONS,
        startupMode = StartupMode.WARM,
        setupBlock = {
            startDemoAndWait()
            waitForText("Demo Theme")
            waitForText("Light")
            waitForText("Dark")
        },
    ) {
        clickText("Dark")
        waitForText("Dark")
        clickText("Light")
        waitForText("Light")
    }

    @Test
    fun collectionsScroll() = benchmarkRule.measureRepeated(
        packageName = TARGET_PACKAGE,
        metrics = listOf(FrameTimingMetric()),
        compilationMode = CompilationMode.Partial(),
        iterations = DEFAULT_ITERATIONS,
        startupMode = StartupMode.WARM,
        setupBlock = {
            startDemoAndWait()
            clickText("Collections")
            waitForText("Collections")
        },
    ) {
        swipePageUp()
        swipePageUp()
        swipePageUp()
    }
}
