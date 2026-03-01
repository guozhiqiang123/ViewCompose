package com.gzq.uiframework

import com.gzq.uiframework.renderer.modifier.Modifier
import com.gzq.uiframework.renderer.modifier.fillMaxSize
import com.gzq.uiframework.renderer.modifier.fillMaxWidth
import com.gzq.uiframework.renderer.modifier.margin
import com.gzq.uiframework.renderer.modifier.weight
import com.gzq.uiframework.runtime.mutableStateOf
import com.gzq.uiframework.widget.core.Button
import com.gzq.uiframework.widget.core.ButtonVariant
import com.gzq.uiframework.widget.core.Column
import com.gzq.uiframework.widget.core.LazyColumn
import com.gzq.uiframework.widget.core.Row
import com.gzq.uiframework.widget.core.Snackbar
import com.gzq.uiframework.widget.core.SnackbarDuration
import com.gzq.uiframework.widget.core.Text
import com.gzq.uiframework.widget.core.TextDefaults
import com.gzq.uiframework.widget.core.Toast
import com.gzq.uiframework.widget.core.UiTextStyle
import com.gzq.uiframework.widget.core.UiTreeBuilder
import com.gzq.uiframework.widget.core.dp
import com.gzq.uiframework.widget.core.remember
import com.gzq.uiframework.widget.core.sp

internal fun UiTreeBuilder.FeedbackPage() {
    val snackbarVisibleState = remember { mutableStateOf(false) }
    val snackbarCountState = remember { mutableStateOf(0) }
    val toastCountState = remember { mutableStateOf(0) }
    val lastEventState = remember { mutableStateOf("Idle") }

    Snackbar(
        visible = snackbarVisibleState.value,
        message = "Feedback snackbar ${snackbarCountState.value}",
        actionLabel = "Acknowledge",
        duration = SnackbarDuration.Long,
        requestKey = "feedback_snackbar",
        onAction = {
            lastEventState.value = "Snackbar action ${snackbarCountState.value}"
            snackbarVisibleState.value = false
        },
        onDismiss = {
            if (snackbarVisibleState.value) {
                lastEventState.value = "Snackbar dismissed ${snackbarCountState.value}"
                snackbarVisibleState.value = false
            }
        },
    )
    Toast(
        visible = toastCountState.value > 0,
        message = "Feedback toast ${toastCountState.value}",
        requestKey = "feedback_toast_${toastCountState.value}",
    )

    LazyColumn(
        items = listOf("overview", "transient", "verify"),
        key = { it },
        modifier = Modifier.fillMaxSize(),
    ) { section ->
        when (section) {
            "overview" -> ChapterPageOverviewSection(
                title = "Feedback",
                goal = "Verify that host-driven transient overlays stay declarative, refresh with state, and dismiss cleanly with the session lifecycle.",
                modules = listOf("overlay host", "render session lifecycle", "transient presenters"),
            )

            "transient" -> ScenarioSection(
                kind = ScenarioKind.Benchmark,
                title = "Transient Feedback Anchor",
                subtitle = "Use this block to trigger snackbar and toast from the same stable path.",
            ) {
                BenchmarkRouteCallout(
                    route = "Catalog -> Open Feedback -> Transient Feedback Anchor",
                    stableTargets = listOf(
                        "Show Snackbar",
                        "Hide Snackbar",
                        "Show Toast",
                        "Reset Feedback",
                    ),
                )
                Text(
                    text = "Last event: ${lastEventState.value}",
                    style = UiTextStyle(fontSizeSp = 13.sp),
                    color = TextDefaults.secondaryColor(),
                    modifier = Modifier.margin(bottom = 8.dp),
                )
                Row(
                    spacing = 8.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .margin(bottom = 8.dp),
                ) {
                    Button(
                        text = "Show Snackbar",
                        onClick = {
                            snackbarCountState.value += 1
                            snackbarVisibleState.value = true
                            lastEventState.value = "Snackbar requested ${snackbarCountState.value}"
                        },
                        modifier = Modifier.weight(1f),
                    )
                    Button(
                        text = "Hide Snackbar",
                        variant = ButtonVariant.Outlined,
                        onClick = {
                            snackbarVisibleState.value = false
                            lastEventState.value = "Snackbar hidden"
                        },
                        modifier = Modifier.weight(1f),
                    )
                }
                Row(
                    spacing = 8.dp,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Button(
                        text = "Show Toast",
                        variant = ButtonVariant.Tonal,
                        onClick = {
                            toastCountState.value += 1
                            lastEventState.value = "Toast requested ${toastCountState.value}"
                        },
                        modifier = Modifier.weight(1f),
                    )
                    Button(
                        text = "Reset Feedback",
                        variant = ButtonVariant.Outlined,
                        onClick = {
                            snackbarVisibleState.value = false
                            snackbarCountState.value = 0
                            toastCountState.value = 0
                            lastEventState.value = "Idle"
                        },
                        modifier = Modifier.weight(1f),
                    )
                }
                Column(
                    spacing = 6.dp,
                    modifier = Modifier.margin(top = 12.dp),
                ) {
                    Text(text = "Snackbar count: ${snackbarCountState.value}")
                    Text(text = "Toast count: ${toastCountState.value}")
                }
            }

            else -> VerificationNotesSection(
                what = "Feedback chapter should prove that transient overlays can be driven by framework state without leaking outside the current render session.",
                howToVerify = listOf(
                    "点击 Show Snackbar，确认底部 snackbar 出现，并带有 Acknowledge action。",
                    "点击 Hide Snackbar 或等待 dismiss，确认页面里的 Last event 会同步更新。",
                    "连续点击 Show Toast，确认短暂提示会重复触发，而不会卡死在第一次请求上。",
                    "点击 Reset Feedback，确认状态计数和当前 snackbar 都回到初始状态。",
                ),
                expected = listOf(
                    "Snackbar 和 Toast 都通过同一套 overlay host 流程触发。",
                    "Snackbar dismiss 后不会继续残留在页面底部。",
                    "demo 会话结束时，不应残留悬挂的 transient overlay。",
                ),
                relatedGaps = listOf(
                    "Dialog 和 PopupWindow 还未接入 demo。",
                    "Toast 自然消失的真实回调链仍未建模。",
                ),
            )
        }
    }
}
