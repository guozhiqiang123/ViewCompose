package com.viewcompose.widget.core

import androidx.annotation.MainThread
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner

/**
 * Returns a [SavedStateHandle] scoped to the current [ViewModelStoreOwner].
 * The handle is backed by an internal ViewModel entry and survives configuration changes.
 */
@MainThread
fun savedStateHandle(
    key: String = "__viewcompose_saved_state_handle__",
    owner: ViewModelStoreOwner? = null,
): SavedStateHandle {
    val holder: SavedStateHandleHolderViewModel = viewModel(
        modelClass = SavedStateHandleHolderViewModel::class,
        key = key,
        owner = owner,
    )
    return holder.handle
}

class SavedStateHandleHolderViewModel(
    val handle: SavedStateHandle,
) : ViewModel()
