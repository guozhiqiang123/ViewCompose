package com.gzq.uiframework.widget.core

import com.gzq.uiframework.renderer.layout.BoxAlignment
import com.gzq.uiframework.renderer.layout.HorizontalAlignment
import com.gzq.uiframework.renderer.layout.MainAxisArrangement
import com.gzq.uiframework.renderer.modifier.Modifier
import com.gzq.uiframework.renderer.modifier.backgroundColor
import com.gzq.uiframework.renderer.modifier.clip
import com.gzq.uiframework.renderer.modifier.cornerRadius
import com.gzq.uiframework.renderer.modifier.minWidth
import com.gzq.uiframework.renderer.modifier.padding
import com.gzq.uiframework.renderer.node.ImageSource

fun UiTreeBuilder.AlertDialog(
    visible: Boolean,
    title: String,
    text: String,
    confirmButtonText: String,
    onConfirm: () -> Unit,
    dismissButtonText: String? = null,
    onDismiss: (() -> Unit)? = null,
    icon: ImageSource? = null,
    requestKey: String = "alert_dialog",
    dismissOnBackPress: Boolean = true,
    dismissOnClickOutside: Boolean = true,
    onDismissRequest: (() -> Unit)? = null,
) {
    Dialog(
        visible = visible,
        requestKey = requestKey,
        dismissOnBackPress = dismissOnBackPress,
        dismissOnClickOutside = dismissOnClickOutside,
        onDismissRequest = onDismissRequest,
    ) {
        val radius = AlertDialogDefaults.cornerRadius()
        Box(
            modifier = Modifier
                .minWidth(AlertDialogDefaults.minWidth())
                .backgroundColor(AlertDialogDefaults.containerColor())
                .cornerRadius(radius)
                .clip()
                .padding(AlertDialogDefaults.contentPadding()),
        ) {
            Column(
                horizontalAlignment = HorizontalAlignment.Center,
            ) {
                if (icon != null) {
                    Icon(
                        source = icon,
                        tint = AlertDialogDefaults.iconTint(),
                        size = AlertDialogDefaults.iconSize(),
                    )
                    Spacer(modifier = Modifier.padding(bottom = AlertDialogDefaults.iconBottomSpacing()))
                }
                Text(
                    text = title,
                    style = AlertDialogDefaults.titleStyle(),
                    color = AlertDialogDefaults.titleColor(),
                )
                Spacer(modifier = Modifier.padding(bottom = AlertDialogDefaults.titleToTextSpacing()))
                Text(
                    text = text,
                    style = AlertDialogDefaults.textStyle(),
                    color = AlertDialogDefaults.textColor(),
                )
                Spacer(modifier = Modifier.padding(bottom = AlertDialogDefaults.textToButtonsSpacing()))
                Row(
                    spacing = AlertDialogDefaults.buttonSpacing(),
                    arrangement = MainAxisArrangement.End,
                    modifier = Modifier.align(HorizontalAlignment.End),
                ) {
                    if (dismissButtonText != null && onDismiss != null) {
                        TextButton(
                            text = dismissButtonText,
                            onClick = onDismiss,
                        )
                    }
                    TextButton(
                        text = confirmButtonText,
                        onClick = onConfirm,
                    )
                }
            }
        }
    }
}

fun UiTreeBuilder.PlainTooltip(
    text: String,
    visible: Boolean,
    anchorId: String,
    onDismissRequest: (() -> Unit)? = null,
    requestKey: String = "tooltip",
) {
    Popup(
        visible = visible,
        anchorId = anchorId,
        requestKey = requestKey,
        alignment = PopupAlignment.BelowStart,
        focusable = false,
        onDismissRequest = onDismissRequest,
    ) {
        Box(
            contentAlignment = BoxAlignment.Center,
            modifier = Modifier
                .backgroundColor(TooltipDefaults.containerColor())
                .cornerRadius(TooltipDefaults.cornerRadius())
                .clip()
                .padding(
                    horizontal = TooltipDefaults.horizontalPadding(),
                    vertical = TooltipDefaults.verticalPadding(),
                ),
        ) {
            Text(
                text = text,
                style = TooltipDefaults.textStyle(),
                color = TooltipDefaults.contentColor(),
            )
        }
    }
}
