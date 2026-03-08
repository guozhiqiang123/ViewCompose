package com.viewcompose.widget.core

import androidx.annotation.MainThread
import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.MutableCreationExtras
import com.viewcompose.viewmodel.LocalViewModelStoreOwner
import kotlin.reflect.KClass

@MainThread
inline fun <reified VM : ViewModel> viewModel(
    key: String? = null,
    owner: ViewModelStoreOwner? = null,
    factory: ViewModelProvider.Factory? = null,
    extras: CreationExtras? = null,
): VM {
    return viewModel(
        modelClass = VM::class,
        key = key,
        owner = owner,
        factory = factory,
        extras = extras,
    )
}

@MainThread
fun <VM : ViewModel> viewModel(
    modelClass: KClass<VM>,
    key: String? = null,
    owner: ViewModelStoreOwner? = null,
    factory: ViewModelProvider.Factory? = null,
    extras: CreationExtras? = null,
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
        extras,
        modelClass,
    ) {
        val provider = ViewModelProvider(
            resolvedOwner.viewModelStore,
            resolveFactory(
                owner = resolvedOwner,
                override = factory,
            ),
            resolveCreationExtras(
                owner = resolvedOwner,
                override = extras,
            ),
        )
        if (key.isNullOrBlank()) {
            provider[modelClass.java]
        } else {
            provider[key, modelClass.java]
        }
    }
}

private fun resolveFactory(
    owner: ViewModelStoreOwner,
    override: ViewModelProvider.Factory?,
): ViewModelProvider.Factory {
    if (override != null) {
        return override
    }
    return (owner as? HasDefaultViewModelProviderFactory)?.defaultViewModelProviderFactory
        ?: ViewModelProvider.NewInstanceFactory()
}

private fun resolveCreationExtras(
    owner: ViewModelStoreOwner,
    override: CreationExtras?,
): CreationExtras {
    if (override != null) {
        return override
    }
    val defaults = (owner as? HasDefaultViewModelProviderFactory)?.defaultViewModelCreationExtras
        ?: CreationExtras.Empty
    return MutableCreationExtras(defaults)
}
