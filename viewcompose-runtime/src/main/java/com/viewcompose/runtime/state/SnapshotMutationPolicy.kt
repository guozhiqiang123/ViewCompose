package com.viewcompose.runtime

interface SnapshotMutationPolicy<T> {
    fun equivalent(
        a: T,
        b: T,
    ): Boolean

    fun merge(
        previous: T,
        current: T,
        applied: T,
    ): T?
}

@Suppress("UNCHECKED_CAST")
fun <T> structuralEqualityPolicy(): SnapshotMutationPolicy<T> = StructuralEqualityPolicy as SnapshotMutationPolicy<T>

@Suppress("UNCHECKED_CAST")
fun <T> referentialEqualityPolicy(): SnapshotMutationPolicy<T> = ReferentialEqualityPolicy as SnapshotMutationPolicy<T>

@Suppress("UNCHECKED_CAST")
fun <T> neverEqualPolicy(): SnapshotMutationPolicy<T> = NeverEqualPolicy as SnapshotMutationPolicy<T>

private object StructuralEqualityPolicy : SnapshotMutationPolicy<Any?> {
    override fun equivalent(
        a: Any?,
        b: Any?,
    ): Boolean = a == b

    override fun merge(
        previous: Any?,
        current: Any?,
        applied: Any?,
    ): Any? = null
}

private object ReferentialEqualityPolicy : SnapshotMutationPolicy<Any?> {
    override fun equivalent(
        a: Any?,
        b: Any?,
    ): Boolean = a === b

    override fun merge(
        previous: Any?,
        current: Any?,
        applied: Any?,
    ): Any? = null
}

private object NeverEqualPolicy : SnapshotMutationPolicy<Any?> {
    override fun equivalent(
        a: Any?,
        b: Any?,
    ): Boolean = false

    override fun merge(
        previous: Any?,
        current: Any?,
        applied: Any?,
    ): Any? = null
}
