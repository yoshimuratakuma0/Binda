package com.legstart.binda

import com.legstart.core.BoundTask
import kotlinx.coroutines.delay

class FruitRepositoryImpl: FruitRepository {
    override suspend fun fetchFruits(): List<String> {
        delay(3000)
        return listOf("Apple", "Banana", "Orange")
    }
}