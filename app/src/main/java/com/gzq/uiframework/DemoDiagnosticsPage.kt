package com.gzq.uiframework

import com.gzq.uiframework.renderer.modifier.Modifier
import com.gzq.uiframework.renderer.modifier.fillMaxSize
import com.gzq.uiframework.renderer.modifier.padding
import com.gzq.uiframework.runtime.mutableStateOf
import com.gzq.uiframework.renderer.view.tree.LayoutPassTracker
import com.gzq.uiframework.widget.core.Button
import com.gzq.uiframework.widget.core.Environment
import com.gzq.uiframework.widget.core.LazyColumn
import com.gzq.uiframework.widget.core.SideEffect
import com.gzq.uiframework.widget.core.Text
import com.gzq.uiframework.widget.core.TextDefaults
import com.gzq.uiframework.widget.core.Theme
import com.gzq.uiframework.widget.core.UiTextStyle
import com.gzq.uiframework.widget.core.UiTreeBuilder
import com.gzq.uiframework.widget.core.dp
import com.gzq.uiframework.widget.core.remember
import com.gzq.uiframework.widget.core.sp
import com.gzq.uiframework.runtime.MutableState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal fun UiTreeBuilder.DiagnosticsPage(
    selectedPageState: MutableState<Int>,
) {
    val pendingSnapshotRefreshState = remember { mutableStateOf(false) }
    val benchmarkRefreshCountState = remember { mutableStateOf(0) }
    val renderSnapshotState = remember { mutableStateOf(DemoRenderDiagnosticsStore.latestSnapshot()) }
    val patchSnapshotState = remember { mutableStateOf(DemoRenderDiagnosticsStore.latestPatchActiveSnapshot()) }
    val layoutSnapshotState = remember { mutableStateOf(LayoutPassTracker.snapshot()) }
    if (pendingSnapshotRefreshState.value) {
        SideEffect {
            renderSnapshotState.value = DemoRenderDiagnosticsStore.latestSnapshot()
            patchSnapshotState.value = DemoRenderDiagnosticsStore.latestPatchActiveSnapshot()
            layoutSnapshotState.value = LayoutPassTracker.snapshot()
            pendingSnapshotRefreshState.value = false
        }
    }
    val pageItems = when (selectedPageState.value) {
        0 -> listOf("benchmark", "page", "page_filter", "runtime", "verify")
        1 -> listOf("page", "page_filter", "renderer_actions", "renderer", "verify")
        else -> listOf("page", "page_filter", "gaps", "verify")
    }
    LazyColumn(
        items = pageItems,
        key = { it },
        modifier = Modifier.fillMaxSize(),
    ) { section ->
        when (section) {
            "page" -> ChapterPageOverviewSection(
                title = "诊断",
                goal = "将 demo 变为运行时 locals、渲染器 patch 和框架已知缺口的手动回归控制台。",
                modules = listOf("debug logging", "theme locals", "renderer", "roadmap gaps"),
            )

            "page_filter" -> ChapterPageFilterSection(
                pages = listOf("运行时", "渲染器", "缺口"),
                selectedIndex = selectedPageState.value,
                onSelectionChange = { selectedPageState.value = it },
            )

            "benchmark" -> ScenarioSection(
                kind = ScenarioKind.Benchmark,
                title = "Diagnostics Benchmark 锚点",
                subtitle = "此区块固定在默认的运行时页面，让 benchmark 控件始终保持在首屏可见。",
            ) {
                Text(
                    text = "诊断刷新次数 ${benchmarkRefreshCountState.value}",
                    modifier = Modifier.padding(bottom = 8.dp),
                )
                Button(
                    text = "刷新 Diagnostics Benchmark",
                    onClick = {
                        benchmarkRefreshCountState.value = benchmarkRefreshCountState.value + 1
                        pendingSnapshotRefreshState.value = true
                    },
                    modifier = Modifier.padding(bottom = 8.dp),
                )
                Button(
                    text = "重置 Diagnostics Benchmark",
                    onClick = {
                        benchmarkRefreshCountState.value = 0
                        pendingSnapshotRefreshState.value = true
                    },
                    modifier = Modifier.padding(bottom = 8.dp),
                )
                BenchmarkRouteCallout(
                    route = "Launcher -> MainActivity(extra=diagnostics) -> Diagnostics -> Diagnostics Benchmark Anchor",
                    stableTargets = listOf(
                        "Refresh Diagnostics Benchmark",
                        "Reset Diagnostics Benchmark",
                    ),
                )
                Text(
                    text = "稳定路径: launcher -> diagnostics module -> benchmark anchor",
                    style = UiTextStyle(fontSizeSp = 12.sp),
                    color = TextDefaults.secondaryColor(),
                )
            }

            "runtime" -> ScenarioSection(
                kind = ScenarioKind.Core,
                title = "运行时快照",
                subtitle = "此章节将成为状态失效、local 传播和副作用边界的手动检查入口。",
            ) {
                DiagnosticFactGroup(
                    title = "运行时数据",
                    facts = listOf(
                        DiagnosticFact("调试日志", "已启用 (UIFrameworkSample)"),
                        DiagnosticFact("可用模块", "${AVAILABLE_DEMO_MODULES.size}"),
                        DiagnosticFact("规划模块", "${PLANNED_DEMO_MODULES.size}"),
                        DiagnosticFact("区域设置", Environment.localeTags.firstOrNull() ?: "und"),
                        DiagnosticFact("布局方向", Environment.layoutDirection.name),
                        DiagnosticFact("密度", "${"%.2f".format(Locale.US, Environment.density.density)}x"),
                        DiagnosticFact("图片加载器", "Coil 集成已在 demo 中启用"),
                    ),
                )
                DiagnosticFactGroup(
                    title = "主题 Token",
                    facts = listOf(
                        DiagnosticFact("Background", Theme.colors.background.asColorHex()),
                        DiagnosticFact("Surface", Theme.colors.surface.asColorHex()),
                        DiagnosticFact("Primary", Theme.colors.primary.asColorHex()),
                        DiagnosticFact("Accent", Theme.colors.accent.asColorHex()),
                        DiagnosticFact("Pressed", (0x22000000 or (Theme.colors.textPrimary and 0x00FFFFFF)).asColorHex()),
                        DiagnosticFact("Card radius", "${Theme.shapes.cardCornerRadius}px"),
                    ),
                )
            }

            "renderer_actions" -> ScenarioSection(
                kind = ScenarioKind.Benchmark,
                title = "渲染器操作",
                subtitle = "将手动探针放在顶部附近，使诊断刷新在反复测试和 benchmark 运行中易于触达。",
            ) {
                Button(
                    text = "刷新渲染器快照",
                    onClick = {
                        pendingSnapshotRefreshState.value = true
                    },
                    modifier = Modifier.padding(bottom = 8.dp),
                )
                if (pendingSnapshotRefreshState.value) {
                    Text(
                        text = "正在捕获渲染器快照…",
                        modifier = Modifier.padding(bottom = 8.dp),
                    )
                }
                Button(
                    text = "重置布局计数器",
                    onClick = {
                        LayoutPassTracker.reset()
                        pendingSnapshotRefreshState.value = true
                    },
                    modifier = Modifier.padding(bottom = 8.dp),
                )
            }

            "renderer" -> ScenarioSection(
                kind = ScenarioKind.Core,
                title = "渲染器钩子",
                subtitle = "示例现在手动暴露最近的渲染快照，以便检查 patch/rebind/skip 行为和树深度。",
            ) {
                val snapshot = renderSnapshotState.value
                val patchSnapshot = patchSnapshotState.value
                val layoutSnapshot = layoutSnapshotState.value
                DiagnosticFactGroup(
                    title = "最近渲染快照",
                    facts = listOf(
                        DiagnosticFact("渲染次数", snapshot.renderCount.toString()),
                        DiagnosticFact("更新时间", snapshot.updatedAtMillis.formatDiagnosticsTime()),
                        DiagnosticFact("插入", snapshot.stats.inserts.toString()),
                        DiagnosticFact("复用", snapshot.stats.reuses.toString()),
                        DiagnosticFact("移除", snapshot.stats.removals.toString()),
                        DiagnosticFact("已 Patch", snapshot.stats.patchedNodes.toString()),
                        DiagnosticFact("已重绑", snapshot.stats.reboundNodes.toString()),
                        DiagnosticFact("已跳过", snapshot.stats.skippedBindings.toString()),
                        DiagnosticFact("VNode 数量", snapshot.structure.vnodeCount.toString()),
                        DiagnosticFact("已挂载数量", snapshot.structure.mountedNodeCount.toString()),
                        DiagnosticFact("VNode 深度", snapshot.structure.maxVNodeDepth.toString()),
                        DiagnosticFact("挂载深度", snapshot.structure.maxMountedDepth.toString()),
                    ),
                )
                DiagnosticFactGroup(
                    title = "最近 Patch-Active 快照",
                    facts = listOf(
                        DiagnosticFact("捕获时间", patchSnapshot?.updatedAtMillis?.formatDiagnosticsTime() ?: "尚未捕获"),
                        DiagnosticFact("已 Patch", patchSnapshot?.stats?.patchedNodes?.toString() ?: "0"),
                        DiagnosticFact("已重绑", patchSnapshot?.stats?.reboundNodes?.toString() ?: "0"),
                        DiagnosticFact("已跳过", patchSnapshot?.stats?.skippedBindings?.toString() ?: "0"),
                        DiagnosticFact("挂载深度", patchSnapshot?.structure?.maxMountedDepth?.toString() ?: "0"),
                        DiagnosticFact("警告", patchSnapshot?.warnings?.joinToString() ?: "无"),
                    ),
                )
                val bindingsByType = patchSnapshot?.stats?.bindingsByType
                if (bindingsByType != null && bindingsByType.isNotEmpty()) {
                    DiagnosticFactGroup(
                        title = "按节点类型的绑定明细",
                        facts = bindingsByType.entries
                            .sortedByDescending { it.value.patched + it.value.rebound }
                            .map { (type, stats) ->
                                val typeName = type::class.simpleName ?: "?"
                                DiagnosticFact(
                                    typeName,
                                    "patched=${stats.patched}  rebound=${stats.rebound}  skipped=${stats.skipped}",
                                )
                            },
                    )
                }
                DiagnosticFactGroup(
                    title = "布局 Pass 计数器",
                    facts = listOf(
                        DiagnosticFact("总 measure 次数", layoutSnapshot.totalMeasureCount.toString()),
                        DiagnosticFact("总 layout 次数", layoutSnapshot.totalLayoutCount.toString()),
                        DiagnosticFact("measure 耗时", layoutSnapshot.totalMeasureNs.formatNsAsMs()),
                        DiagnosticFact("layout 耗时", layoutSnapshot.totalLayoutNs.formatNsAsMs()),
                    ) + layoutSnapshot.entries
                        .take(6)
                        .map { entry ->
                            DiagnosticFact(
                                entry.viewName,
                                "measure=${entry.measureCount} (${entry.totalMeasureNs.formatNsAsMs()}), " +
                                    "layout=${entry.layoutCount} (${entry.totalLayoutNs.formatNsAsMs()})",
                            )
                        },
                )
                DiagnosticFactGroup(
                    title = "当前渲染模型",
                    facts = listOf(
                        DiagnosticFact("渲染根", "单根 RenderSession"),
                        DiagnosticFact("更新模型", "根重渲染 + 基于 key 的已挂载复用"),
                        DiagnosticFact("懒容器", "逐项懒 session"),
                        DiagnosticFact("顶部导航", "TabRow + HorizontalPager via ViewPager2 + RecyclerView"),
                        DiagnosticFact("Local 传播", "跨懒容器和 pager session 捕获"),
                        DiagnosticFact("可视检查器", "尚未实现"),
                    ),
                )
                ChecklistGroup(
                    title = "手动探针",
                    items = listOf(
                        "先进入 State -> Patch Stress 做几次切换，再返回这里点击刷新，确认最近 Patch-Active 快照里的 patched/skipped 开始增长。",
                        "点击重置布局计数器后进入 Layouts / Input / Foundations，再回来刷新，确认布局 Pass 计数器主要由自定义容器增长。",
                        "切到 Layouts 或 Collections 压力页后再回来，确认挂载深度和 VNode 深度会跟随复杂场景变化。",
                        "打开 Layouts / Collections 压力页，观察日志中 VNode tree 与 Reconcile 摘要是否稳定。",
                        "切换章节并返回，确认 debug 日志仍持续输出到 UIFrameworkSample。",
                        "遇到视觉 bug 时，先用这里的渲染模型判断问题更像 layout、list diff 还是 local 传播。",
                    ),
                )
            }

            "gaps" -> ScenarioSection(
                kind = ScenarioKind.Guide,
                title = "已知缺口",
                subtitle = "这些缺口有意保持可见，以便诊断章节引导后续框架工作。",
            ) {
                ChecklistGroup(
                    title = "检查",
                    items = listOf(
                        "尚无可视化渲染树检查器",
                        "尚无 Patch 时间线 UI",
                        "尚无 Local 值浏览器",
                    ),
                )
                ChecklistGroup(
                    title = "性能",
                    items = listOf(
                        "尚无帧耗时覆盖层",
                        "尚无 measure/layout 计数器页面",
                        "尚无 diff 开销汇总 UI",
                    ),
                )
                ChecklistGroup(
                    title = "测试钩子",
                    items = listOf(
                        "尚无语义树抽象",
                        "尚无场景回放工具",
                        "尚无截图/回归仪表盘",
                    ),
                )
            }

            else -> VerificationNotesSection(
                what = "在假设视觉 bug 属于 widget、layout 或 runtime 层之前，诊断应是首先检查的地方。",
                howToVerify = listOf(
                    "切换 theme mode 与章节，确认运行时快照始终反映当前 environment。",
                    "在 State -> Patch Stress 执行几次更新后，返回渲染器页点击刷新，确认 patched/skipped 不再始终为 0。",
                    "点击重置布局计数器，再进入一个复杂章节操作后返回，确认布局 Pass 计数器出现新的 measure/layout 增长。",
                    "对比不同章节后刷新，确认热点排序会把更贵的容器排到前面，而不是只按次数排。",
                    "切到层级更复杂的章节后再次刷新，确认渲染器页能看到 VNode/mounted 深度。",
                    "在出现渲染问题时，对照这里列出的缺口判断是已知缺口还是新回归。",
                    "结合日志观察 renderer 行为，并确认诊断页面描述与当前实现一致。",
                ),
                expected = listOf(
                    "该章节能快速告诉你当前框架还缺什么。",
                    "环境信息和主题信息不会在章节切换后失真。",
                    "渲染器页可以拿到最近一次 render 的统计快照和最近一次 patch-active 快照。",
                    "渲染器页可以看到自定义容器的 measure/layout 次数和累计耗时。",
                    "诊断会持续作为后续 inspector 的落点。",
                ),
                relatedGaps = listOf(
                    "还没有自动刷新的 render tree、patch timeline 和性能面板可视化。",
                    "还没有 deepest path、frame timeline 和每节点耗时。",
                ),
            )
        }
    }
}

private fun Long.formatDiagnosticsTime(): String {
    if (this <= 0L) {
        return "尚未捕获"
    }
    return SimpleDateFormat("HH:mm:ss", Locale.US).format(Date(this))
}

private fun Long.formatNsAsMs(): String {
    return String.format(Locale.US, "%.2f ms", this / 1_000_000f)
}
