package com.gzq.uiframework

import com.gzq.uiframework.renderer.modifier.Modifier
import com.gzq.uiframework.renderer.modifier.backgroundColor
import com.gzq.uiframework.renderer.modifier.cornerRadius
import com.gzq.uiframework.renderer.modifier.fillMaxSize
import com.gzq.uiframework.renderer.modifier.fillMaxWidth
import com.gzq.uiframework.renderer.modifier.height
import com.gzq.uiframework.renderer.modifier.margin
import com.gzq.uiframework.renderer.modifier.padding
import com.gzq.uiframework.renderer.modifier.testTag
import com.gzq.uiframework.renderer.node.ImageSource
import com.gzq.uiframework.runtime.mutableStateOf
import com.gzq.uiframework.widget.core.AnchorTarget
import com.gzq.uiframework.widget.core.AlertDialog
import com.gzq.uiframework.widget.core.Button
import com.gzq.uiframework.widget.core.ButtonVariant
import com.gzq.uiframework.widget.core.Column
import com.gzq.uiframework.widget.core.Dialog
import com.gzq.uiframework.widget.core.DialogPosition
import com.gzq.uiframework.widget.core.Divider
import com.gzq.uiframework.widget.core.DropdownMenu
import com.gzq.uiframework.widget.core.DropdownMenuItem
import com.gzq.uiframework.widget.core.LazyColumn
import com.gzq.uiframework.widget.core.ModalBottomSheet
import com.gzq.uiframework.widget.core.PlainTooltip
import com.gzq.uiframework.widget.core.Popup
import com.gzq.uiframework.widget.core.PopupAlignment
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

internal fun UiTreeBuilder.FeedbackPage(
    initialPageIndex: Int = 0,
) {
    val popupAnchorId = "feedback_popup_anchor"
    val menuAnchorId = "feedback_menu_anchor"
    val tooltipAnchorId = "feedback_tooltip_anchor"
    val selectedPageState = remember { mutableStateOf(initialPageIndex.coerceIn(0, 2)) }
    val dialogVisibleState = remember { mutableStateOf(false) }
    val dialogCountState = remember { mutableStateOf(0) }
    val popupVisibleState = remember { mutableStateOf(false) }
    val popupCountState = remember { mutableStateOf(0) }
    val snackbarVisibleState = remember { mutableStateOf(false) }
    val snackbarCountState = remember { mutableStateOf(0) }
    val toastCountState = remember { mutableStateOf(0) }
    val lastEventState = remember { mutableStateOf("空闲") }
    val alertDialogVisibleState = remember { mutableStateOf(false) }
    val alertDialogIconVisibleState = remember { mutableStateOf(false) }
    val menuExpandedState = remember { mutableStateOf(false) }
    val menuSelectedState = remember { mutableStateOf("未选择") }
    val tooltipVisibleState = remember { mutableStateOf(false) }
    val bottomSheetVisibleState = remember { mutableStateOf(false) }

    // Overlay declarations
    Dialog(
        visible = dialogVisibleState.value,
        requestKey = "feedback_dialog",
        position = DialogPosition.Bottom,
        scrimOpacity = 0.48f,
        onDismissRequest = {
            if (dialogVisibleState.value) {
                dialogVisibleState.value = false
                lastEventState.value = "Dialog 关闭 ${dialogCountState.value}"
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
                text = "自定义 Dialog ${dialogCountState.value}",
                style = UiTextStyle(fontSizeSp = 18.sp),
            )
            Text(
                text = "Dialog 内容通过 overlay host 渲染到同一个 render session 中。",
                color = TextDefaults.secondaryColor(),
            )
            Row(
                spacing = 8.dp,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Button(
                    text = "确认",
                    onClick = {
                        dialogVisibleState.value = false
                        lastEventState.value = "Dialog 确认 ${dialogCountState.value}"
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag(DemoTestTags.FEEDBACK_DIALOG_CONFIRM),
                )
                Button(
                    text = "关闭",
                    variant = ButtonVariant.Outlined,
                    onClick = {
                        dialogVisibleState.value = false
                        lastEventState.value = "Dialog 关闭 ${dialogCountState.value}"
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag(DemoTestTags.FEEDBACK_DIALOG_CLOSE),
                )
            }
        }
    }
    Popup(
        visible = popupVisibleState.value,
        anchorId = popupAnchorId,
        requestKey = "feedback_popup",
        alignment = PopupAlignment.AboveStart,
        offsetY = 8.dp,
        onDismissRequest = {
            if (popupVisibleState.value) {
                popupVisibleState.value = false
                lastEventState.value = "Popup 关闭 ${popupCountState.value}"
            }
        },
    ) {
        Column(
            spacing = 10.dp,
            modifier = Modifier
                .backgroundColor(SurfaceDefaults.variantBackgroundColor())
                .cornerRadius(SurfaceDefaults.cardCornerRadius())
                .padding(12.dp),
        ) {
            Text(
                text = "Popup ${popupCountState.value}",
                style = UiTextStyle(fontSizeSp = 16.sp),
            )
            Text(
                text = "锚定弹窗内容，通过 anchor target 定位。",
                color = TextDefaults.secondaryColor(),
            )
            Button(
                text = "关闭 Popup",
                variant = ButtonVariant.Outlined,
                onClick = {
                    popupVisibleState.value = false
                    lastEventState.value = "Popup 手动关闭 ${popupCountState.value}"
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(DemoTestTags.FEEDBACK_POPUP_DISMISS),
            )
        }
    }
    Snackbar(
        visible = snackbarVisibleState.value,
        message = "Snackbar 通知 ${snackbarCountState.value}",
        actionLabel = "知道了",
        duration = SnackbarDuration.Long,
        requestKey = "feedback_snackbar",
        onAction = {
            lastEventState.value = "Snackbar 操作 ${snackbarCountState.value}"
            snackbarVisibleState.value = false
        },
        onDismiss = {
            if (snackbarVisibleState.value) {
                lastEventState.value = "Snackbar 消失 ${snackbarCountState.value}"
                snackbarVisibleState.value = false
            }
        },
    )
    Toast(
        visible = toastCountState.value > 0,
        message = "Toast 提示 ${toastCountState.value}",
        requestKey = "feedback_toast_${toastCountState.value}",
    )
    AlertDialog(
        visible = alertDialogVisibleState.value,
        title = "确认删除？",
        text = "此操作不可撤销，删除后数据将无法恢复。",
        confirmButtonText = "确定",
        onConfirm = {
            alertDialogVisibleState.value = false
            lastEventState.value = "AlertDialog 确认"
        },
        dismissButtonText = "取消",
        onDismiss = {
            alertDialogVisibleState.value = false
            lastEventState.value = "AlertDialog 取消"
        },
        requestKey = "feedback_alert_dialog",
    )
    AlertDialog(
        visible = alertDialogIconVisibleState.value,
        title = "更新可用",
        text = "发现新版本，是否立即更新？",
        confirmButtonText = "更新",
        onConfirm = {
            alertDialogIconVisibleState.value = false
            lastEventState.value = "AlertDialog 带图标 确认"
        },
        dismissButtonText = "稍后",
        onDismiss = {
            alertDialogIconVisibleState.value = false
            lastEventState.value = "AlertDialog 带图标 取消"
        },
        icon = ImageSource.Resource(R.drawable.demo_media_icon),
        requestKey = "feedback_alert_dialog_icon",
    )
    DropdownMenu(
        expanded = menuExpandedState.value,
        anchorId = menuAnchorId,
        onDismissRequest = { menuExpandedState.value = false },
        requestKey = "feedback_dropdown_menu",
    ) {
        DropdownMenuItem(
            text = "编辑",
            onClick = {
                menuSelectedState.value = "编辑"
                menuExpandedState.value = false
            },
            leadingIcon = ImageSource.Resource(R.drawable.demo_media_icon),
        )
        DropdownMenuItem(
            text = "复制",
            onClick = {
                menuSelectedState.value = "复制"
                menuExpandedState.value = false
            },
        )
        DropdownMenuItem(
            text = "分享",
            onClick = {
                menuSelectedState.value = "分享"
                menuExpandedState.value = false
            },
            trailingText = "Ctrl+S",
        )
        DropdownMenuItem(
            text = "删除",
            onClick = {},
            enabled = false,
        )
    }
    PlainTooltip(
        text = "这是一个提示信息",
        visible = tooltipVisibleState.value,
        anchorId = tooltipAnchorId,
        onDismissRequest = { tooltipVisibleState.value = false },
        requestKey = "feedback_tooltip",
    )
    ModalBottomSheet(
        visible = bottomSheetVisibleState.value,
        requestKey = "feedback_bottom_sheet",
        onDismissRequest = {
            bottomSheetVisibleState.value = false
            lastEventState.value = "BottomSheet 关闭"
        },
    ) {
        Column(
            spacing = 12.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Text(
                text = "底部弹窗",
                style = UiTextStyle(fontSizeSp = 18.sp),
            )
            Text(
                text = "ModalBottomSheet 通过 overlay 路径渲染，支持手势下滑关闭。",
                color = TextDefaults.secondaryColor(),
            )
            Divider()
            Button(
                text = "选项一：保存草稿",
                onClick = {
                    bottomSheetVisibleState.value = false
                    lastEventState.value = "BottomSheet 保存草稿"
                },
                modifier = Modifier.fillMaxWidth(),
            )
            Button(
                text = "选项二：丢弃更改",
                variant = ButtonVariant.Outlined,
                onClick = {
                    bottomSheetVisibleState.value = false
                    lastEventState.value = "BottomSheet 丢弃更改"
                },
                modifier = Modifier.fillMaxWidth(),
            )
            Button(
                text = "关闭",
                variant = ButtonVariant.Tonal,
                onClick = {
                    bottomSheetVisibleState.value = false
                    lastEventState.value = "BottomSheet 关闭"
                },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }

    val pageItems = when (selectedPageState.value) {
        0 -> listOf("benchmark", "page", "page_filter", "transient", "verify")
        1 -> listOf("page", "page_filter", "alert_sheet", "verify")
        else -> listOf("page", "page_filter", "menu_tooltip", "verify")
    }

    LazyColumn(
        items = pageItems,
        key = { it },
        modifier = Modifier.fillMaxSize(),
    ) { section ->
        when (section) {
            "page" -> ChapterPageOverviewSection(
                title = "反馈组件",
                goal = "验证瞬态 overlay（Snackbar、Toast、Dialog、Popup）和结构化弹窗（AlertDialog、DropdownMenu、Tooltip、ModalBottomSheet）的生命周期和交互。",
                modules = listOf("overlay host", "render session lifecycle", "transient presenters", "AlertDialog", "DropdownMenu", "PlainTooltip", "ModalBottomSheet"),
            )

            "page_filter" -> ChapterPageFilterSection(
                pages = listOf("瞬态反馈", "弹窗", "菜单"),
                selectedIndex = selectedPageState.value,
                onSelectionChange = { selectedPageState.value = it },
            )

            "benchmark" -> ScenarioSection(
                kind = ScenarioKind.Benchmark,
                title = "反馈组件 Benchmark 锚点",
                subtitle = "AlertDialog show/dismiss 和 DropdownMenu 展开/收起的稳定路径。",
            ) {
                BenchmarkRouteCallout(
                    route = "Catalog -> Feedback -> 瞬态反馈页 -> Benchmark 锚点",
                    stableTargets = listOf(
                        "Show Snackbar / Show Toast / Show Dialog / Show Popup",
                        "AlertDialog show/dismiss",
                        "DropdownMenu expand/collapse",
                    ),
                )
            }

            "transient" -> ScenarioSection(
                kind = ScenarioKind.Core,
                title = "瞬态反馈",
                subtitle = "Snackbar、Toast、Dialog、Popup 的触发、展示和关闭。",
            ) {
                Text(
                    text = "最后事件: ${lastEventState.value}",
                    style = UiTextStyle(fontSizeSp = 13.sp),
                    color = TextDefaults.secondaryColor(),
                    modifier = Modifier
                        .margin(bottom = 8.dp)
                        .testTag(DemoTestTags.FEEDBACK_LAST_EVENT),
                )
                Row(
                    spacing = 8.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .margin(bottom = 8.dp),
                ) {
                    Button(
                        text = "显示 Snackbar",
                        onClick = {
                            snackbarCountState.value += 1
                            snackbarVisibleState.value = true
                            lastEventState.value = "Snackbar 请求 ${snackbarCountState.value}"
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag(DemoTestTags.FEEDBACK_SHOW_SNACKBAR),
                    )
                    Button(
                        text = "隐藏 Snackbar",
                        variant = ButtonVariant.Outlined,
                        onClick = {
                            snackbarVisibleState.value = false
                            lastEventState.value = "Snackbar 隐藏"
                        },
                        modifier = Modifier.weight(1f),
                    )
                }
                Row(
                    spacing = 8.dp,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Button(
                        text = "显示 Toast",
                        variant = ButtonVariant.Tonal,
                        onClick = {
                            toastCountState.value += 1
                            lastEventState.value = "Toast 请求 ${toastCountState.value}"
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag(DemoTestTags.FEEDBACK_SHOW_TOAST),
                    )
                    Button(
                        text = "显示 Dialog",
                        onClick = {
                            dialogCountState.value += 1
                            dialogVisibleState.value = true
                            lastEventState.value = "Dialog 请求 ${dialogCountState.value}"
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag(DemoTestTags.FEEDBACK_SHOW_DIALOG),
                    )
                }
                Row(
                    spacing = 8.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .margin(top = 8.dp),
                ) {
                    AnchorTarget(
                        anchorId = popupAnchorId,
                        modifier = Modifier.weight(1f),
                    ) {
                        Button(
                            text = "显示 Popup",
                            variant = ButtonVariant.Tonal,
                            onClick = {
                                popupCountState.value += 1
                                popupVisibleState.value = true
                                lastEventState.value = "Popup 请求 ${popupCountState.value}"
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag(DemoTestTags.FEEDBACK_SHOW_POPUP),
                        )
                    }
                    Button(
                        text = "重置",
                        variant = ButtonVariant.Outlined,
                        onClick = {
                            dialogVisibleState.value = false
                            dialogCountState.value = 0
                            popupVisibleState.value = false
                            popupCountState.value = 0
                            snackbarVisibleState.value = false
                            snackbarCountState.value = 0
                            toastCountState.value = 0
                            lastEventState.value = "空闲"
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag(DemoTestTags.FEEDBACK_RESET),
                    )
                }
                Column(
                    spacing = 6.dp,
                    modifier = Modifier.margin(top = 12.dp),
                ) {
                    Text(
                        text = "Dialog 次数: ${dialogCountState.value}",
                        modifier = Modifier.testTag(DemoTestTags.FEEDBACK_DIALOG_COUNT),
                    )
                    Text(
                        text = "Popup 次数: ${popupCountState.value}",
                        modifier = Modifier.testTag(DemoTestTags.FEEDBACK_POPUP_COUNT),
                    )
                    Text(text = "Snackbar 次数: ${snackbarCountState.value}")
                    Text(
                        text = "Toast 次数: ${toastCountState.value}",
                        modifier = Modifier.testTag(DemoTestTags.FEEDBACK_TOAST_COUNT),
                    )
                }
            }

            "alert_sheet" -> ScenarioSection(
                kind = ScenarioKind.Core,
                title = "AlertDialog + ModalBottomSheet",
                subtitle = "AlertDialog 提供标准化确认弹窗。ModalBottomSheet 提供底部弹出面板。",
            ) {
                Text(
                    text = "最后事件: ${lastEventState.value}",
                    style = UiTextStyle(fontSizeSp = 13.sp),
                    color = TextDefaults.secondaryColor(),
                    modifier = Modifier.margin(bottom = 8.dp),
                )
                Button(
                    text = "显示 AlertDialog（标准）",
                    onClick = { alertDialogVisibleState.value = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .margin(bottom = 8.dp),
                )
                Button(
                    text = "显示 AlertDialog（带图标）",
                    variant = ButtonVariant.Tonal,
                    onClick = { alertDialogIconVisibleState.value = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .margin(bottom = 12.dp),
                )
                Divider(modifier = Modifier.margin(bottom = 12.dp))
                Button(
                    text = "显示 ModalBottomSheet",
                    onClick = { bottomSheetVisibleState.value = true },
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            "menu_tooltip" -> ScenarioSection(
                kind = ScenarioKind.Core,
                title = "DropdownMenu + PlainTooltip",
                subtitle = "DropdownMenu 提供锚定下拉菜单。PlainTooltip 提供简单的提示气泡。",
            ) {
                Text(
                    text = "菜单选中项: ${menuSelectedState.value}",
                    style = UiTextStyle(fontSizeSp = 13.sp),
                    color = TextDefaults.secondaryColor(),
                    modifier = Modifier.margin(bottom = 8.dp),
                )
                AnchorTarget(
                    anchorId = menuAnchorId,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Button(
                        text = "打开菜单",
                        onClick = { menuExpandedState.value = true },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                Text(
                    text = "菜单包含 4 个选项：编辑（带图标）、复制、分享（带快捷键）、删除（禁用）",
                    style = UiTextStyle(fontSizeSp = 13.sp),
                    color = TextDefaults.secondaryColor(),
                    modifier = Modifier.margin(top = 8.dp, bottom = 16.dp),
                )
                Divider(modifier = Modifier.margin(bottom = 12.dp))
                AnchorTarget(
                    anchorId = tooltipAnchorId,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Button(
                        text = if (tooltipVisibleState.value) "隐藏 Tooltip" else "显示 Tooltip",
                        variant = ButtonVariant.Tonal,
                        onClick = { tooltipVisibleState.value = !tooltipVisibleState.value },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                Text(
                    text = "PlainTooltip 锚定在上方按钮，显示纯文字提示。",
                    style = UiTextStyle(fontSizeSp = 13.sp),
                    color = TextDefaults.secondaryColor(),
                    modifier = Modifier.margin(top = 8.dp),
                )
            }

            else -> VerificationNotesSection(
                what = "反馈组件应验证 overlay 驱动的瞬态反馈和结构化弹窗的生命周期和交互。",
                howToVerify = listOf(
                    "点击显示 Snackbar，确认底部出现带操作按钮的通知。",
                    "连续点击显示 Toast，确认短暂提示重复触发。",
                    "点击显示 Dialog，确认弹窗出现并可通过按钮关闭。",
                    "点击显示 AlertDialog，确认标准确认弹窗出现，点击确定/取消关闭。",
                    "点击显示 AlertDialog 带图标，确认图标显示在标题上方。",
                    "点击打开菜单，确认 4 个菜单项正常显示，禁用项不可点击。",
                    "点击显示 Tooltip，确认提示气泡出现在锚点附近。",
                    "点击显示 ModalBottomSheet，确认底部面板弹出并可选择选项或关闭。",
                ),
                expected = listOf(
                    "所有 overlay 通过 host 渲染，关闭后不残留。",
                    "AlertDialog 和 ModalBottomSheet 的交互事件正确更新 lastEvent。",
                    "DropdownMenu 的禁用项视觉降低透明度，不响应点击。",
                ),
            )
        }
    }
}
