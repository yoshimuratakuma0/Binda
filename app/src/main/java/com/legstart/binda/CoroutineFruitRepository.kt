package com.legstart.binda

import com.legstart.core.SingleTask
import com.legstart.core_async.CoroutineSingleTask
import kotlinx.coroutines.delay

class CoroutineFruitRepository : FruitRepository {
    override fun fetchFruits(): SingleTask<List<String>> {
        return CoroutineSingleTask {
            delay(2000)
            listOf("Apple", "Banana", "Orange")
        }
    }
}
