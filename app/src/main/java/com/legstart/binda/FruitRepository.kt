package com.legstart.binda

import com.legstart.core_android.SingleTask

interface FruitRepository {
    fun fetchFruits(): SingleTask<List<String>>
}