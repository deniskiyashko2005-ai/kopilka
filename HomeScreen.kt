package com.example.kopilka.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kopilka.data.Goal
import com.example.kopilka.ui.theme.TBankYellow
import com.example.kopilka.viewmodel.GoalViewModel
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    viewModel: GoalViewModel,
    isDark: Boolean,
    onToggleTheme: () -> Unit,
    onAddClick: () -> Unit,
    onGoalClick: (String) -> Unit
) {
    val goals by viewModel.goals.collectAsState()
    val totalSaved = goals.sumOf { it.currentAmount }
    val totalTarget = goals.sumOf { it.targetAmount }
    val fabInteraction = remember { MutableInteractionSource() }
    val fabScale = rememberPressScale(fabInteraction)

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = TBankYellow,
                contentColor = Color(0xFF0D0D0D),
                shape = CircleShape,
                interactionSource = fabInteraction,
                modifier = Modifier.graphicsLayer { scaleX = fabScale; scaleY = fabScale }
            ) {
                Icon(Icons.Rounded.Add, contentDescription = "Добавить")
            }
        }
    ) { pad ->
        Column(
            modifier = Modifier.fillMaxSize().padding(pad)
                .verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(24.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Копилки", fontSize = 34.sp, fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground, letterSpacing = (-1).sp)
                Spacer(Modifier.weight(1f))
                IconButton(onClick = onToggleTheme) {
                    Icon(if (isDark) Icons.Rounded.LightMode else Icons.Rounded.DarkMode,
                        contentDescription = "Сменить тему", tint = MaterialTheme.colorScheme.onBackground)
                }
            }
            Spacer(Modifier.height(16.dp))
            TotalCard(totalSaved, totalTarget, goals.size, isDark)
            Spacer(Modifier.height(24.dp))
            if (goals.isEmpty()) {
                EmptyState()
            } else {
                goals.forEachIndexed { index, goal ->
                    var visible by remember(goal.id) { mutableStateOf(false) }
                    LaunchedEffect(goal.id) {
                        delay((index * 55L).coerceAtMost(400L))
                        visible = true
                    }
                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn(animationSpec = tween(260)) +
                            slideInVertically(animationSpec = spring(
                                dampingRatio = 0.8f, stiffness = Spring.StiffnessMediumLow
                            )) { it / 4 }
                    ) {
                        GoalCard(goal) { onGoalClick(goal.id) }
                    }
                    Spacer(Modifier.height(12.dp))
                }
            }
            Spacer(Modifier.height(96.dp))
        }
    }
}

@Composable
private fun TotalCard(saved: Double, target: Double, count: Int, isDark: Boolean) {
    val cardBg = if (isDark) TBankYellow else Color(0xFF0D0D0D)
    val titleColor = if (isDark) Color(0xFF0D0D0D).copy(alpha = 0.65f) else Color(0xFF9E9E9E)
    val valueColor = if (isDark) Color(0xFF0D0D0D) else Color.White
    val accentColor = if (isDark) Color(0xFF0D0D0D) else TBankYellow
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg)) {
        Column(Modifier.padding(24.dp)) {
            Text("Всего накоплено", color = titleColor, fontSize = 14.sp)
            Spacer(Modifier.height(8.dp))
            AnimatedMoney(saved, 36.sp, FontWeight.Bold, valueColor)
            Spacer(Modifier.height(14.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Цель: ${formatMoney(target)}", color = accentColor,
                    fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Spacer(Modifier.weight(1f))
                Text("$count ${pluralGoals(count)}", color = titleColor, fontSize = 13.sp)
            }
        }
    }
}

@Composable
private fun GoalCard(goal: Goal, onClick: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    val scale = rememberPressScale(interaction)
    Card(
        modifier = Modifier.fillMaxWidth().graphicsLayer { scaleX = scale; scaleY = scale }
            .clickable(interactionSource = interaction, indication = null, onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(48.dp).background(Color(goal.color), CircleShape),
                    contentAlignment = Alignment.Center) {
                    Text(goal.name.take(1).uppercase(), fontWeight = FontWeight.Bold,
                        fontSize = 20.sp, color = Color(0xFF0D0D0D))
                }
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)) {
                    Text(goal.name, fontSize = 17.sp, fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface)
                    Text("${(goal.progress * 100).toInt()}% от цели", fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Spacer(Modifier.height(16.dp))
            LinearProgressIndicator(progress = { goal.progress },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                color = Color(goal.color), trackColor = MaterialTheme.colorScheme.surfaceVariant,
                strokeCap = androidx.compose.ui.graphics.StrokeCap.Round)
            Spacer(Modifier.height(12.dp))
            Row {
                Text(formatMoney(goal.currentAmount), fontWeight = FontWeight.Bold,
                    fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
                Spacer(Modifier.weight(1f))
                Text("из ${formatMoney(goal.targetAmount)}", fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun EmptyState() {
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(Modifier.fillMaxWidth().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Пока нет копилок", fontWeight = FontWeight.SemiBold, fontSize = 17.sp,
                color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(6.dp))
            Text("Нажмите «+», чтобы создать первую", fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
