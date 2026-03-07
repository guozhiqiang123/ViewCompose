package com.viewcompose

import com.viewcompose.runtime.MutableState

internal data class FeedbackAnchors(
    val popupAnchorId: String,
    val menuAnchorId: String,
    val tooltipAnchorId: String,
)

internal data class FeedbackPageState(
    val selectedPageState: MutableState<Int>,
    val dialogVisibleState: MutableState<Boolean>,
    val dialogCountState: MutableState<Int>,
    val popupVisibleState: MutableState<Boolean>,
    val popupCountState: MutableState<Int>,
    val snackbarVisibleState: MutableState<Boolean>,
    val snackbarCountState: MutableState<Int>,
    val toastCountState: MutableState<Int>,
    val lastEventState: MutableState<String>,
    val alertDialogVisibleState: MutableState<Boolean>,
    val alertDialogIconVisibleState: MutableState<Boolean>,
    val menuExpandedState: MutableState<Boolean>,
    val menuSelectedState: MutableState<String>,
    val tooltipVisibleState: MutableState<Boolean>,
    val bottomSheetVisibleState: MutableState<Boolean>,
)
