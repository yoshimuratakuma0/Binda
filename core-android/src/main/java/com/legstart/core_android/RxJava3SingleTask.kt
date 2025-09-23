package com.legstart.core_android

import com.legstart.core.BoundTask
import com.legstart.core.TaskScope
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers

class RxJava3SingleTask<T : Any>(
    private val single: Single<T>,
    private val scheduler: Scheduler = Schedulers.io(),
) : SingleTask<T> {
    override fun bindTo(taskScope: TaskScope): BoundTask<T> {
        return object : BoundTask<T> {
            private lateinit var disposable: Disposable
            override val isCancelled: Boolean
                get() = disposable.isDisposed

            override fun start(
                onSuccess: (T) -> Unit,
                onError: (Throwable) -> Unit,
                onCancel: () -> Unit
            ) {
                disposable = single
                    .subscribeOn(scheduler)
                    .subscribe(
                        { result: T -> onSuccess(result) },
                        { error: Throwable -> onError(error) }
                    )
            }

            override fun cancel() {
                disposable.dispose()
            }
        }
    }
}

