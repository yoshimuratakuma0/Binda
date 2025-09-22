package com.legstart.core

interface BoundTask<T> {
    fun start(
        onSuccess: (T) -> Unit,
        onError: (Throwable) -> Unit = {},
        onCancel: () -> Unit = {}
    )
    fun cancel()
}