package com.legstart.core

interface BoundTask<T> {
    val isCancelled: Boolean
    fun start(
        onSuccess: (T) -> Unit,
        onError: (Throwable) -> Unit = {},
        onCancel: () -> Unit = {}
    )

    fun cancel()
}
