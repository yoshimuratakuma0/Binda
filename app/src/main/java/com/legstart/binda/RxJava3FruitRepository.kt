package com.legstart.binda

import com.legstart.core_android.RxJava3SingleTask
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

class RxJava3FruitRepository : FruitRepository {
    override fun fetchFruits(): RxJava3SingleTask<List<String>> {
        return RxJava3SingleTask(
            single = Single
                .timer(3, java.util.concurrent.TimeUnit.SECONDS)
                .map { listOf("Apple", "Banana", "Orange") },
            scheduler = Schedulers.io(),
        )
    }
}