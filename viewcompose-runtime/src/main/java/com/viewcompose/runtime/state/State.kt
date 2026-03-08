package com.viewcompose.runtime

import com.viewcompose.runtime.state.DerivedStateImpl
import com.viewcompose.runtime.state.MutableStateImpl

interface State<T> {
    val value: T
}

interface MutableState<T> : State<T> {
    override var value: T
}

fun <T> mutableStateOf(
    value: T,
    policy: SnapshotMutationPolicy<T> = structuralEqualityPolicy(),
): MutableState<T> = MutableStateImpl(
    initialValue = value,
    policy = policy,
)

fun <T> derivedStateOf(block: () -> T): State<T> = DerivedStateImpl(block)
