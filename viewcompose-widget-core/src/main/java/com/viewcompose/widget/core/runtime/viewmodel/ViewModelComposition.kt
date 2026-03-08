package com.viewcompose.widget.core

import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import kotlin.reflect.KClass

@MainThread
inline fun <reified VM : ViewModel> viewModel(
    key: String? = null,
    owner: ViewModelStoreOwner? = null,
    factory: ViewModelProvider.Factory? = null,
): VM {
    return viewModel(
        modelClass = VM::class,
        key = key,
        owner = owner,
        factory = factory,
    )
}

@MainThread
fun <VM : ViewModel> viewModel(
    modelClass: KClass<VM>,
    key: String? = null,
    owner: ViewModelStoreOwner? = null,
    factory: ViewModelProvider.Factory? = null,
): VM {
    val resolvedOwner = owner ?: LocalViewModelStoreOwner.current
    requireNotNull(resolvedOwner) {
        "No ViewModelStoreOwner found. Use ComponentActivity/Fragment.setUiContent " +
            "or wrap with ProvideViewModelStoreOwner."
    }
    return remember(
        resolvedOwner,
        key,
        factory,
        modelClass,
    ) {
        val provider = if (factory != null) {
            ViewModelProvider(resolvedOwner, factory)
        } else {
            ViewModelProvider(resolvedOwner)
        }
        if (key.isNullOrBlank()) {
            provider[modelClass.java]
        } else {
            provider[key, modelClass.java]
        }
    }
}
