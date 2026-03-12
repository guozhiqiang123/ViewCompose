package com.viewcompose.widget.core

/**
 * Optional callback propagated through locals so child render sessions
 * (lazy/pager/overlay session hosts) can report render diagnostics.
 */
val LocalRenderResultListener = uiLocalOf<((RenderTreeResult) -> Unit)?> { null }

