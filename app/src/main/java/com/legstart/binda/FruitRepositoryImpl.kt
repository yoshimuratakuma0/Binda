package com.legstart.binda

import kotlinx.coroutines.delay

class FruitRepositoryImpl : FruitRepository {
    override suspend fun fetchFruits(): List<String> {
        delay(3000)
        return listOf("Apple", "Banana", "Orange")
    }
}
