package com.legstart.binda

class RxJavaFruitRepository : FruitRepository {
    override fun fetchFruits(): RxJavaSingleTask<List<String>> {
        return RxJavaSingleTask { emitter ->
            // Simulate network delay
            Thread.sleep(2000)
            val fruits = listOf("Apple", "Banana", "Cherry", "Date", "Elderberry")
            emitter.onNext(fruits)
            emitter.onComplete()
        }
    }
}