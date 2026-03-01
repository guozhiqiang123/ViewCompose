package com.gzq.uiframework

import com.gzq.uiframework.renderer.modifier.Modifier
import com.gzq.uiframework.renderer.modifier.fillMaxSize
import com.gzq.uiframework.renderer.modifier.padding
import com.gzq.uiframework.runtime.mutableStateOf
import com.gzq.uiframework.widget.core.Button
import com.gzq.uiframework.widget.core.Environment
import com.gzq.uiframework.widget.core.LazyColumn
import com.gzq.uiframework.widget.core.SideEffect
import com.gzq.uiframework.widget.core.Theme
import com.gzq.uiframework.widget.core.UiTreeBuilder
import com.gzq.uiframework.widget.core.remember
import com.gzq.uiframework.widget.core.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal fun UiTreeBuilder.DiagnosticsPage() {
    val selectedPageState = remember { mutableStateOf(0) }
    val pendingSnapshotRefreshState = remember { mutableStateOf(false) }
    val renderSnapshotState = remember { mutableStateOf(DemoRenderDiagnosticsStore.latestSnapshot()) }
    val patchSnapshotState = remember { mutableStateOf(DemoRenderDiagnosticsStore.latestPatchActiveSnapshot()) }
    if (pendingSnapshotRefreshState.value) {
        SideEffect {
            renderSnapshotState.value = DemoRenderDiagnosticsStore.latestSnapshot()
            patchSnapshotState.value = DemoRenderDiagnosticsStore.latestPatchActiveSnapshot()
            pendingSnapshotRefreshState.value = false
        }
    }
    val pageItems = when (selectedPageState.value) {
        0 -> listOf("page", "page_filter", "runtime", "verify")
        1 -> listOf("page", "page_filter", "renderer", "verify")
        else -> listOf("page", "page_filter", "gaps", "verify")
    }
    LazyColumn(
        items = pageItems,
        key = { it },
        modifier = Modifier.fillMaxSize(),
    ) { section ->
        when (section) {
            "page" -> ChapterPageOverviewSection(
                title = "Diagnostics",
                goal = "Turn the demo into a manual regression console for runtime locals, renderer patches, and visible framework gaps.",
                modules = listOf("debug logging", "theme locals", "renderer", "roadmap gaps"),
            )

            "page_filter" -> ChapterPageFilterSection(
                pages = listOf("Runtime", "Renderer", "Gaps"),
                selectedIndex = selectedPageState.value,
                onSelectionChange = { selectedPageState.value = it },
            )

            "runtime" -> DemoSection(
                title = "Runtime Snapshot",
                subtitle = "This chapter will become the manual inspection home for state invalidation, local propagation, and effect boundaries.",
            ) {
                DiagnosticFactGroup(
                    title = "Runtime Facts",
                    facts = listOf(
                        DiagnosticFact("Debug logging", "Enabled (UIFrameworkSample)"),
                        DiagnosticFact("Top chapters", "${DEMO_CHAPTERS.size}"),
                        DiagnosticFact("Locale", Environment.localeTags.firstOrNull() ?: "und"),
                        DiagnosticFact("Layout direction", Environment.layoutDirection.name),
                        DiagnosticFact("Density", "${"%.2f".format(Locale.US, Environment.density.density)}x"),
                        DiagnosticFact("Image loader", "Coil integration active in demo"),
                    ),
                )
                DiagnosticFactGroup(
                    title = "Theme Tokens",
                    facts = listOf(
                        DiagnosticFact("Background", Theme.colors.background.asColorHex()),
                        DiagnosticFact("Surface", Theme.colors.surface.asColorHex()),
                        DiagnosticFact("Primary", Theme.colors.primary.asColorHex()),
                        DiagnosticFact("Accent", Theme.colors.accent.asColorHex()),
                        DiagnosticFact("Pressed", Theme.interactions.pressedOverlay.asColorHex()),
                        DiagnosticFact("Card radius", "${Theme.shapes.cardCornerRadius}px"),
                    ),
                )
            }

            "renderer" -> DemoSection(
                title = "Renderer Hooks",
                subtitle = "The sample now exposes recent render snapshots manually so patch/rebind/skip behavior and tree depth can be checked against the log stream.",
            ) {
                val snapshot = renderSnapshotState.value
                val patchSnapshot = patchSnapshotState.value
                Button(
                    text = if (pendingSnapshotRefreshState.value) {
                        "Capturing renderer snapshot..."
                    } else {
                        "Refresh renderer snapshots"
                    },
                    onClick = {
                        pendingSnapshotRefreshState.value = true
                    },
                    modifier = Modifier.padding(bottom = 8.dp),
                )
                DiagnosticFactGroup(
                    title = "Latest Render Snapshot",
                    facts = listOf(
                        DiagnosticFact("Render count", snapshot.renderCount.toString()),
                        DiagnosticFact("Updated at", snapshot.updatedAtMillis.formatDiagnosticsTime()),
                        DiagnosticFact("Inserted", snapshot.stats.inserts.toString()),
                        DiagnosticFact("Reused", snapshot.stats.reuses.toString()),
                        DiagnosticFact("Removed", snapshot.stats.removals.toString()),
                        DiagnosticFact("Patched", snapshot.stats.patchedNodes.toString()),
                        DiagnosticFact("Rebound", snapshot.stats.reboundNodes.toString()),
                        DiagnosticFact("Skipped", snapshot.stats.skippedBindings.toString()),
                        DiagnosticFact("VNode count", snapshot.structure.vnodeCount.toString()),
                        DiagnosticFact("Mounted count", snapshot.structure.mountedNodeCount.toString()),
                        DiagnosticFact("VNode depth", snapshot.structure.maxVNodeDepth.toString()),
                        DiagnosticFact("Mounted depth", snapshot.structure.maxMountedDepth.toString()),
                    ),
                )
                DiagnosticFactGroup(
                    title = "Latest Patch-Active Snapshot",
                    facts = listOf(
                        DiagnosticFact("Captured", patchSnapshot?.updatedAtMillis?.formatDiagnosticsTime() ?: "Not captured yet"),
                        DiagnosticFact("Patched", patchSnapshot?.stats?.patchedNodes?.toString() ?: "0"),
                        DiagnosticFact("Rebound", patchSnapshot?.stats?.reboundNodes?.toString() ?: "0"),
                        DiagnosticFact("Skipped", patchSnapshot?.stats?.skippedBindings?.toString() ?: "0"),
                        DiagnosticFact("Mounted depth", patchSnapshot?.structure?.maxMountedDepth?.toString() ?: "0"),
                        DiagnosticFact("Warnings", patchSnapshot?.warnings?.joinToString() ?: "None"),
                    ),
                )
                DiagnosticFactGroup(
                    title = "Current Rendering Model",
                    facts = listOf(
                        DiagnosticFact("Render root", "Single root RenderSession"),
                        DiagnosticFact("Update model", "Root rerender + keyed mounted reuse"),
                        DiagnosticFact("Lazy containers", "Per-item lazy sessions"),
                        DiagnosticFact("Top navigation", "TabPager mapped to ViewPager2 + TabLayout"),
                        DiagnosticFact("Local propagation", "Captured across lazy and pager sessions"),
                        DiagnosticFact("Visual inspector", "Not implemented"),
                    ),
                )
                ChecklistGroup(
                    title = "Manual Probes",
                    items = listOf(
                        "先进入 State -> Patch Stress 做几次切换，再返回这里点击 Refresh，确认 Latest Patch-Active Snapshot 里的 patched/skipped 开始增长。",
                        "切到 Layouts 或 Collections 压力页后再回来，确认 mounted depth 和 vnode depth 会跟随复杂场景变化。",
                        "打开 Layouts / Collections 压力页，观察日志中 VNode tree 与 Reconcile 摘要是否稳定。",
                        "切换章节并返回，确认 debug 日志仍持续输出到 UIFrameworkSample。",
                        "遇到视觉 bug 时，先用这里的渲染模型判断问题更像 layout、list diff 还是 local 传播。",
                    ),
                )
            }

            "gaps" -> DemoSection(
                title = "Known Gaps",
                subtitle = "These gaps are intentionally visible so the diagnostics chapter can guide future framework work.",
            ) {
                ChecklistGroup(
                    title = "Inspection",
                    items = listOf(
                        "No visual render tree inspector",
                        "No patch timeline UI",
                        "No local value explorer",
                    ),
                )
                ChecklistGroup(
                    title = "Performance",
                    items = listOf(
                        "No frame-time overlay",
                        "No measure/layout counters page",
                        "No diff cost summary UI",
                    ),
                )
                ChecklistGroup(
                    title = "Testing Hooks",
                    items = listOf(
                        "No semantics tree abstraction",
                        "No scenario replay harness",
                        "No screenshot/regression dashboard",
                    ),
                )
            }

            else -> VerificationNotesSection(
                what = "Diagnostics should be the first place to check before assuming a visual bug belongs to a widget, layout, or runtime layer.",
                howToVerify = listOf(
                    "切换 theme mode 与章节，确认 runtime snapshot 始终反映当前 environment。",
                    "在 State -> Patch Stress 执行几次更新后，返回 Renderer 页点击 Refresh，确认 patched/skipped 不再始终为 0。",
                    "切到层级更复杂的章节后再次刷新，确认 Renderer 页能看到 vnode/mounted depth。",
                    "在出现渲染问题时，对照这里列出的 gaps 判断是已知缺口还是新回归。",
                    "结合日志观察 renderer 行为，并确认 diagnostics 页面描述与当前实现一致。",
                ),
                expected = listOf(
                    "该章节能快速告诉你当前框架还缺什么。",
                    "环境信息和主题信息不会在章节切换后失真。",
                    "Renderer 页可以拿到最近一次 render 的统计快照和最近一次 patch-active 快照。",
                    "Diagnostics 会持续作为后续 inspector 的落点。",
                ),
                relatedGaps = listOf(
                    "还没有自动刷新的 render tree、patch timeline 和性能面板可视化。",
                    "还没有 deepest path、measure/layout 次数和 frame timeline。",
                ),
            )
        }
    }
}

private fun Long.formatDiagnosticsTime(): String {
    if (this <= 0L) {
        return "Not captured yet"
    }
    return SimpleDateFormat("HH:mm:ss", Locale.US).format(Date(this))
}
