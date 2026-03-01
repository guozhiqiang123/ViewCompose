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
            startCatalogAndWait()
            waitForText("Capability Modules")
            waitForText("Open State")
            waitForText("Open Layouts")
        },
    ) {
        openDemoModule("State")
        waitForText("State & Effects")
        returnToCatalog()
        openDemoModule("Layouts")
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
            startCatalogAndWait()
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
    fun foundationsBenchmarkAnchor() = benchmarkRule.measureRepeated(
        packageName = TARGET_PACKAGE,
        metrics = listOf(FrameTimingMetric()),
        compilationMode = CompilationMode.Partial(),
        iterations = DEFAULT_ITERATIONS,
        startupMode = StartupMode.WARM,
        setupBlock = {
            startDemoActivityAndWait(
                moduleKey = "foundations",
                expectedText = "Foundations",
            )
            scrollUntilText("Foundations Benchmark Off")
            scrollUntilText("Reset Foundations Benchmark")
        },
    ) {
        clickText("Foundations Benchmark Off")
        waitForText("Foundations Benchmark On")
        clickText("Reset Foundations Benchmark")
        waitForText("Foundations Benchmark Off")
    }

    @Test
    fun layoutsBenchmarkAnchor() = benchmarkRule.measureRepeated(
        packageName = TARGET_PACKAGE,
        metrics = listOf(FrameTimingMetric()),
        compilationMode = CompilationMode.Partial(),
        iterations = DEFAULT_ITERATIONS,
        startupMode = StartupMode.WARM,
        setupBlock = {
            startDemoActivityAndWait(
                moduleKey = "layouts",
                expectedText = "Layouts",
            )
            scrollUntilText("Layouts Benchmark Compact")
            scrollUntilText("Reset Layouts Benchmark")
        },
    ) {
        clickText("Layouts Benchmark Compact")
        waitForText("Layouts Benchmark Expanded")
        clickText("Reset Layouts Benchmark")
        waitForText("Layouts Benchmark Compact")
    }

    @Test
    fun inputBenchmarkAnchor() = benchmarkRule.measureRepeated(
        packageName = TARGET_PACKAGE,
        metrics = listOf(FrameTimingMetric()),
        compilationMode = CompilationMode.Partial(),
        iterations = DEFAULT_ITERATIONS,
        startupMode = StartupMode.WARM,
        setupBlock = {
            startDemoActivityAndWait(
                moduleKey = "input",
                expectedText = "Input",
            )
            scrollUntilText("Input Benchmark Compact")
            scrollUntilText("Reset Input Benchmark")
        },
    ) {
        clickText("Input Benchmark Compact")
        waitForText("Input Benchmark Expanded")
        clickText("Reset Input Benchmark")
        waitForText("Input Benchmark Compact")
    }

    @Test
    fun collectionsBenchmarkAnchor() = benchmarkRule.measureRepeated(
        packageName = TARGET_PACKAGE,
        metrics = listOf(FrameTimingMetric()),
        compilationMode = CompilationMode.Partial(),
        iterations = DEFAULT_ITERATIONS,
        startupMode = StartupMode.WARM,
        setupBlock = {
            startDemoActivityAndWait(
                moduleKey = "collections",
                expectedText = "Collections",
            )
            scrollUntilText("Collections Benchmark A-B-C")
            scrollUntilText("Reset Collections Benchmark")
        },
    ) {
        clickText("Collections Benchmark A-B-C")
        waitForText("Collections Benchmark C-A-B")
        clickText("Reset Collections Benchmark")
        waitForText("Collections Benchmark A-B-C")
    }

    @Test
    fun collectionsScroll() = benchmarkRule.measureRepeated(
        packageName = TARGET_PACKAGE,
        metrics = listOf(FrameTimingMetric()),
        compilationMode = CompilationMode.Partial(),
        iterations = DEFAULT_ITERATIONS,
        startupMode = StartupMode.WARM,
        setupBlock = {
            startCatalogAndWait()
            openDemoModule("Collections")
            waitForText("Collections")
        },
    ) {
        swipePageUp()
        swipePageUp()
        swipePageUp()
    }

    @Test
    fun patchUpdates() = benchmarkRule.measureRepeated(
        packageName = TARGET_PACKAGE,
        metrics = listOf(FrameTimingMetric()),
        compilationMode = CompilationMode.Partial(),
        iterations = DEFAULT_ITERATIONS,
        startupMode = StartupMode.WARM,
        setupBlock = {
            startCatalogAndWait()
            openDemoModule("State")
            waitForText("Patch")
            clickText("Patch")
            waitForText("Patch Stress")
            waitForText("Advance patch state 0")
            waitForText("Reset patch state")
        },
    ) {
        clickText("Advance patch state 0")
        waitForText("Advance patch state 1")
        clickText("Advance patch state 1")
        waitForText("Advance patch state 2")
        clickText("Reset patch state")
        waitForText("Advance patch state 0")
    }

    @Test
    fun diagnosticsRefreshAfterPatch() = benchmarkRule.measureRepeated(
        packageName = TARGET_PACKAGE,
        metrics = listOf(FrameTimingMetric()),
        compilationMode = CompilationMode.Partial(),
        iterations = DEFAULT_ITERATIONS,
        startupMode = StartupMode.WARM,
        setupBlock = {
            startCatalogAndWait()
            openDemoModule("State")
            waitForText("State & Effects")
            waitForText("Patch")
            clickText("Patch")
            waitForText("Patch Stress")
            waitForText("Advance patch state 0")
            clickText("Advance patch state 0")
            waitForText("Advance patch state 1")
        },
    ) {
        clickText("Open diagnostics renderer")
        scrollUntilText("Latest Patch-Active Snapshot")
    }
}
