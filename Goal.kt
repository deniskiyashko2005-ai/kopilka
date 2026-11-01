package com.example.kopilka.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.UUID

data class Transaction(
    val amount: Double,
    val date: Long,
    val type: String // "add" | "withdraw"
)

data class Goal(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val targetAmount: Double,
    val currentAmount: Double = 0.0,
    val color: Long = 0xFFFFDD2D,
    val history: List<Transaction> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
) {
    val progress: Float
        get() = if (targetAmount <= 0) 0f
        else (currentAmount / targetAmount).toFloat().coerceIn(0f, 1f)
}

object GoalStorage {
    private const val PREFS = "kopilka_prefs"
    private const val KEY = "goals"
    private val gson = Gson()

    fun load(context: Context): List<Goal> {
        val json = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY, null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<Goal>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun save(context: Context, goals: List<Goal>) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putString(KEY, gson.toJson(goals)).apply()
    }
}
