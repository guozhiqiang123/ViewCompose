package com.viewcompose.runtime

import com.viewcompose.runtime.state.DerivedStateImpl
import com.viewcompose.runtime.state.MutableStateImpl

interface State<T> {
    val value: T
}

interface MutableState<T> : State<T> {
    override var value: T
}

fun <T> mutableStateOf(value: T): MutableState<T> = MutableStateImpl(value)

fun <T> derivedStateOf(block: () -> T): State<T> = DerivedStateImpl(block)
