package com.legstart.binda

import com.legstart.core.SingleTask
import com.legstart.core_async.RxJava3SingleTask
import io.reactivex.rxjava3.core.Single

class RxJava3FruitRepository : FruitRepository {
    override fun fetchFruits(): SingleTask<List<String>> {
        return RxJava3SingleTask(
            single = Single
                .timer(3, java.util.concurrent.TimeUnit.SECONDS)
                .map { listOf("Lemon", "Grape", "Kiwi") },
        )
    }
}
