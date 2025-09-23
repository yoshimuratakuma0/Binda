package com.legstart.core_async.scopes

import com.legstart.core.Cancelable
import com.legstart.core.TaskScope
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers

class RxJava3TaskScope(
    private val scheduler: Scheduler = Schedulers.io(),
) : TaskScope {
    override fun launch(task: () -> Unit): Cancelable {
        val worker = scheduler.createWorker()
        val disposable = worker.schedule {
            task()
        }
        return object : Cancelable {
            override val isCancelled: Boolean
                get() = disposable.isDisposed

            override fun cancel() {
                disposable.dispose()
            }
        }
    }
}