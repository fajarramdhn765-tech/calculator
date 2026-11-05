package com.example.data.repository

import com.example.data.local.CalculationEntity
import com.example.data.local.HistoryDao
import kotlinx.coroutines.flow.Flow

class HistoryRepository(private val dao: HistoryDao) {
    val allHistory: Flow<List<CalculationEntity>> = dao.getAllHistory()

    suspend fun addCalculation(expression: String, result: String): Long {
        return dao.insert(
            CalculationEntity(
                expression = expression,
                result = result,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun delete(id: Long) {
        dao.deleteById(id)
    }

    suspend fun clearAll() {
        dao.clearAll()
    }
}
