package com.gzq.uiframework

import com.gzq.uiframework.renderer.modifier.Modifier
import com.gzq.uiframework.renderer.modifier.fillMaxSize
import com.gzq.uiframework.runtime.mutableStateOf
import com.gzq.uiframework.widget.core.LazyColumn
import com.gzq.uiframework.widget.core.UiTreeBuilder
import com.gzq.uiframework.widget.core.remember

internal fun UiTreeBuilder.FeedbackPage(
    initialPageIndex: Int = 0,
) {
    val anchors = FeedbackAnchors(
        popupAnchorId = "feedback_popup_anchor",
        menuAnchorId = "feedback_menu_anchor",
        tooltipAnchorId = "feedback_tooltip_anchor",
    )
    val state = FeedbackPageState(
        selectedPageState = remember { mutableStateOf(initialPageIndex.coerceIn(0, 2)) },
        dialogVisibleState = remember { mutableStateOf(false) },
        dialogCountState = remember { mutableStateOf(0) },
        popupVisibleState = remember { mutableStateOf(false) },
        popupCountState = remember { mutableStateOf(0) },
        snackbarVisibleState = remember { mutableStateOf(false) },
        snackbarCountState = remember { mutableStateOf(0) },
        toastCountState = remember { mutableStateOf(0) },
        lastEventState = remember { mutableStateOf("空闲") },
        alertDialogVisibleState = remember { mutableStateOf(false) },
        alertDialogIconVisibleState = remember { mutableStateOf(false) },
        menuExpandedState = remember { mutableStateOf(false) },
        menuSelectedState = remember { mutableStateOf("未选择") },
        tooltipVisibleState = remember { mutableStateOf(false) },
        bottomSheetVisibleState = remember { mutableStateOf(false) },
    )

    DeclareFeedbackOverlays(
        anchors = anchors,
        state = state,
    )

    LazyColumn(
        items = feedbackPageItems(state.selectedPageState.value),
        key = { it },
        modifier = Modifier.fillMaxSize(),
    ) { section ->
        RenderFeedbackSection(
            section = section,
            anchors = anchors,
            state = state,
        )
    }
}
