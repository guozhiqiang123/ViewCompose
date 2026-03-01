package com.gzq.uiframework

import com.gzq.uiframework.renderer.modifier.Modifier
import com.gzq.uiframework.renderer.modifier.backgroundColor
import com.gzq.uiframework.renderer.modifier.cornerRadius
import com.gzq.uiframework.renderer.modifier.fillMaxSize
import com.gzq.uiframework.renderer.modifier.fillMaxWidth
import com.gzq.uiframework.renderer.modifier.margin
import com.gzq.uiframework.renderer.modifier.padding
import com.gzq.uiframework.renderer.modifier.weight
import com.gzq.uiframework.runtime.mutableStateOf
import com.gzq.uiframework.widget.core.Button
import com.gzq.uiframework.widget.core.ButtonVariant
import com.gzq.uiframework.widget.core.Column
import com.gzq.uiframework.widget.core.Dialog
import com.gzq.uiframework.widget.core.LazyColumn
import com.gzq.uiframework.widget.core.Row
import com.gzq.uiframework.widget.core.Snackbar
import com.gzq.uiframework.widget.core.SnackbarDuration
import com.gzq.uiframework.widget.core.SurfaceDefaults
import com.gzq.uiframework.widget.core.Text
import com.gzq.uiframework.widget.core.TextDefaults
import com.gzq.uiframework.widget.core.Toast
import com.gzq.uiframework.widget.core.UiTextStyle
import com.gzq.uiframework.widget.core.UiTreeBuilder
import com.gzq.uiframework.widget.core.dp
import com.gzq.uiframework.widget.core.remember
import com.gzq.uiframework.widget.core.sp

internal fun UiTreeBuilder.FeedbackPage() {
    val dialogVisibleState = remember { mutableStateOf(false) }
    val dialogCountState = remember { mutableStateOf(0) }
    val snackbarVisibleState = remember { mutableStateOf(false) }
    val snackbarCountState = remember { mutableStateOf(0) }
    val toastCountState = remember { mutableStateOf(0) }
    val lastEventState = remember { mutableStateOf("Idle") }

    Dialog(
        visible = dialogVisibleState.value,
        requestKey = "feedback_dialog",
        onDismissRequest = {
            if (dialogVisibleState.value) {
                dialogVisibleState.value = false
                lastEventState.value = "Dialog dismissed ${dialogCountState.value}"
            }
        },
    ) {
        Column(
            spacing = 12.dp,
            modifier = Modifier
                .fillMaxWidth()
                .backgroundColor(SurfaceDefaults.backgroundColor())
                .cornerRadius(SurfaceDefaults.cardCornerRadius())
                .padding(16.dp),
        ) {
            Text(
                text = "Feedback Dialog ${dialogCountState.value}",
                style = UiTextStyle(fontSizeSp = 18.sp),
            )
            Text(
                text = "Dialog overlay content now renders through the same render session pipeline as the page tree.",
                color = TextDefaults.secondaryColor(),
            )
            Row(
                spacing = 8.dp,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Button(
                    text = "Confirm Dialog",
                    onClick = {
                        dialogVisibleState.value = false
                        lastEventState.value = "Dialog confirmed ${dialogCountState.value}"
                    },
                    modifier = Modifier.weight(1f),
                )
                Button(
                    text = "Close Dialog",
                    variant = ButtonVariant.Outlined,
                    onClick = {
                        dialogVisibleState.value = false
                        lastEventState.value = "Dialog closed ${dialogCountState.value}"
                    },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
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
                        "Show Dialog",
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
                        text = "Show Dialog",
                        onClick = {
                            dialogCountState.value += 1
                            dialogVisibleState.value = true
                            lastEventState.value = "Dialog requested ${dialogCountState.value}"
                        },
                        modifier = Modifier.weight(1f),
                    )
                }
                Row(
                    spacing = 8.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .margin(top = 8.dp),
                ) {
                    Button(
                        text = "Reset Feedback",
                        variant = ButtonVariant.Outlined,
                        onClick = {
                            dialogVisibleState.value = false
                            dialogCountState.value = 0
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
                    Text(text = "Dialog count: ${dialogCountState.value}")
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
                    "点击 Show Dialog，确认弹窗内容可见，且点击 Confirm Dialog / Close Dialog 后页面状态会同步更新。",
                    "点击 Reset Feedback，确认状态计数和当前 snackbar 都回到初始状态。",
                ),
                expected = listOf(
                    "Dialog 通过 overlay host 渲染，而不是作为页面内联节点出现。",
                    "Snackbar 和 Toast 都通过同一套 overlay host 流程触发。",
                    "Snackbar dismiss 后不会继续残留在页面底部。",
                    "demo 会话结束时，不应残留悬挂的 transient overlay。",
                ),
                relatedGaps = listOf(
                    "PopupWindow 还未接入 demo。",
                    "Toast 自然消失的真实回调链仍未建模。",
                ),
            )
        }
    }
}
