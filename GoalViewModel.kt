package com.example.kopilka.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.kopilka.data.Goal
import com.example.kopilka.data.GoalStorage
import com.example.kopilka.data.Transaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GoalViewModel(app: Application) : AndroidViewModel(app) {
    private val _goals = MutableStateFlow<List<Goal>>(emptyList())
    val goals: StateFlow<List<Goal>> = _goals.asStateFlow()

    init {
        _goals.value = GoalStorage.load(getApplication())
    }

    fun addGoal(name: String, target: Double, initial: Double, color: Long) {
        val goal = Goal(
            name = name,
            targetAmount = target,
            currentAmount = initial,
            color = color,
            history = if (initial > 0)
                listOf(Transaction(initial, System.currentTimeMillis(), "add"))
            else emptyList()
        )
        _goals.value = _goals.value + goal
        persist()
    }

    fun deposit(goalId: String, amount: Double) {
        _goals.value = _goals.value.map {
            if (it.id == goalId) it.copy(
                currentAmount = it.currentAmount + amount,
                history = it.history + Transaction(amount, System.currentTimeMillis(), "add")
            ) else it
        }
        persist()
    }

    fun withdraw(goalId: String, amount: Double) {
        _goals.value = _goals.value.map {
            if (it.id == goalId) it.copy(
                currentAmount = (it.currentAmount - amount).coerceAtLeast(0.0),
                history = it.history + Transaction(amount, System.currentTimeMillis(), "withdraw")
            ) else it
        }
        persist()
    }

    fun deleteGoal(goalId: String) {
        _goals.value = _goals.value.filter { it.id != goalId }
        persist()
    }

    private fun persist() = GoalStorage.save(getApplication(), _goals.value)
}
