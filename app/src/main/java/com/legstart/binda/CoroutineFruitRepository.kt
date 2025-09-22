package com.legstart.binda

import com.legstart.core_android.CoroutineSingleTask
import com.legstart.core_android.SingleTask
import kotlinx.coroutines.delay

class CoroutineFruitRepository : FruitRepository {
    override fun fetchFruits(): SingleTask<List<String>> {
        return CoroutineSingleTask {
            delay(3000)
            listOf("Apple", "Banana", "Orange")
        }
    }
}
