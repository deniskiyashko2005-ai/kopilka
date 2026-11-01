package com.example.kopilka

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.compose.ui.unit.IntOffset
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kopilka.ui.AddGoalScreen
import com.example.kopilka.ui.GoalDetailScreen
import com.example.kopilka.ui.HomeScreen
import com.example.kopilka.ui.theme.KopilkaTheme
import com.example.kopilka.viewmodel.GoalViewModel

sealed interface Screen {
    data object Home : Screen
    data object Add : Screen
    data class Detail(val goalId: String) : Screen
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val prefs = remember { getSharedPreferences("kopilka_theme", Context.MODE_PRIVATE) }
            var themeOverride by remember {
                mutableStateOf<Boolean?>(when (prefs.getString("theme", "system")) {
                    "light" -> false
                    "dark" -> true
                    else -> null
                })
            }
            val systemDark = isSystemInDarkTheme()
            val darkTheme = themeOverride ?: systemDark
            KopilkaTheme(darkTheme = darkTheme) {
                val vm: GoalViewModel = viewModel()
                var screen by remember { mutableStateOf<Screen>(Screen.Home) }
                BackHandler(enabled = screen !is Screen.Home) { screen = Screen.Home }
                AnimatedContent(targetState = screen, transitionSpec = {
                    val forward = targetState !is Screen.Home
                    val springSpec = spring<IntOffset>(dampingRatio = 0.85f,
                        stiffness = Spring.StiffnessMediumLow)
                    if (forward) {
                        (slideInHorizontally(animationSpec = springSpec) { it } + fadeIn(tween(220))) togetherWith
                            (slideOutHorizontally(animationSpec = springSpec) { -it / 3 } + fadeOut(tween(150)))
                    } else {
                        (slideInHorizontally(animationSpec = springSpec) { -it / 3 } + fadeIn(tween(220))) togetherWith
                            (slideOutHorizontally(animationSpec = springSpec) { it } + fadeOut(tween(150)))
                    }
                }, label = "screen") { currentScreen ->
                    when (currentScreen) {
                        is Screen.Home -> HomeScreen(viewModel = vm, isDark = darkTheme,
                            onToggleTheme = {
                                val next = !darkTheme
                                themeOverride = next
                                prefs.edit().putString("theme", if (next) "dark" else "light").apply()
                            }, onAddClick = { screen = Screen.Add },
                            onGoalClick = { id -> screen = Screen.Detail(id) })
                        is Screen.Add -> AddGoalScreen(onBack = { screen = Screen.Home },
                            onSave = { name, target, initial, color ->
                                vm.addGoal(name, target, initial, color)
                                screen = Screen.Home
                            })
                        is Screen.Detail -> GoalDetailScreen(goalId = currentScreen.goalId,
                            viewModel = vm, onBack = { screen = Screen.Home })
                    }
                }
            }
        }
    }
}
