package com.example.kopilka.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kopilka.ui.theme.GreenAccent
import com.example.kopilka.ui.theme.RedAccent
import com.example.kopilka.ui.theme.TBankYellow
import com.example.kopilka.viewmodel.GoalViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalDetailScreen(goalId: String, viewModel: GoalViewModel, onBack: () -> Unit) {
    val goals by viewModel.goals.collectAsState()
    val goal = goals.firstOrNull { it.id == goalId } ?: run {
        LaunchedEffect(Unit) { onBack() }
        return
    }
    var showDeposit by remember { mutableStateOf(false) }
    var showWithdraw by remember { mutableStateOf(false) }
    var showDelete by remember { mutableStateOf(false) }
    Scaffold(containerColor = MaterialTheme.colorScheme.background, topBar = {
        TopAppBar(title = { Text(goal.name, fontWeight = FontWeight.SemiBold) },
            navigationIcon = { IconButton(onClick = onBack) {
                Icon(Icons.Rounded.ArrowBack, contentDescription = "Назад")
            } }, actions = { IconButton(onClick = { showDelete = true }) {
                Icon(Icons.Rounded.Delete, contentDescription = "Удалить",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant)
            } }, colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background,
                titleContentColor = MaterialTheme.colorScheme.onBackground,
                navigationIconContentColor = MaterialTheme.colorScheme.onBackground))
    }) { pad ->
        Column(Modifier.fillMaxSize().padding(pad).verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(Modifier.height(16.dp))
            Box(Modifier.size(240.dp), contentAlignment = Alignment.Center) {
                CircularProgress(goal.progress, Color(goal.color), Modifier.fillMaxSize())
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("${(goal.progress * 100).toInt()}%", fontSize = 40.sp,
                        fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                    Text("накоплено", fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Spacer(Modifier.height(24.dp))
            AnimatedMoney(goal.currentAmount, 32.sp, FontWeight.Bold, MaterialTheme.colorScheme.onBackground)
            Text("из ${formatMoney(goal.targetAmount)}", fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(8.dp))
            Text("Осталось ${formatMoney((goal.targetAmount - goal.currentAmount).coerceAtLeast(0.0))}",
                fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(28.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                Button(onClick = { showDeposit = true }, shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TBankYellow,
                        contentColor = Color(0xFF0D0D0D)),
                    modifier = Modifier.weight(1f).height(52.dp)) {
                    Text("Пополнить", fontWeight = FontWeight.SemiBold)
                }
                OutlinedButton(onClick = { showWithdraw = true }, shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.weight(1f).height(52.dp)) {
                    Text("Снять", color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.SemiBold)
                }
            }
            Spacer(Modifier.height(32.dp))
            Text("История", modifier = Modifier.fillMaxWidth(), fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onBackground)
            Spacer(Modifier.height(12.dp))
            if (goal.history.isEmpty()) {
                Text("Пока нет операций", color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp, modifier = Modifier.fillMaxWidth())
            } else {
                goal.history.reversed().forEach { tx ->
                    Row(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface,
                        RoundedCornerShape(14.dp)).padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(if (tx.type == "add") "Пополнение" else "Снятие",
                                fontWeight = FontWeight.Medium, fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface)
                            Text(SimpleDateFormat("d MMM, HH:mm", Locale("ru")).format(Date(tx.date)),
                                fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text((if (tx.type == "add") "+" else "−") + formatMoney(tx.amount),
                            fontWeight = FontWeight.SemiBold,
                            color = if (tx.type == "add") GreenAccent else RedAccent)
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }
            Spacer(Modifier.height(40.dp))
        }
    }
    if (showDeposit) AmountSheet("Пополнить", { showDeposit = false }) { amount ->
        viewModel.deposit(goal.id, amount); showDeposit = false
    }
    if (showWithdraw) AmountSheet("Снять", { showWithdraw = false }) { amount ->
        viewModel.withdraw(goal.id, amount); showWithdraw = false
    }
    if (showDelete) AlertDialog(onDismissRequest = { showDelete = false },
        title = { Text("Удалить копилку?") }, text = { Text("Действие нельзя отменить.") },
        confirmButton = { TextButton(onClick = {
            viewModel.deleteGoal(goal.id); showDelete = false; onBack()
        }) { Text("Удалить", color = RedAccent) } },
        dismissButton = { TextButton(onClick = { showDelete = false }) {
            Text("Отмена", color = MaterialTheme.colorScheme.onSurfaceVariant)
        } })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AmountSheet(title: String, onDismiss: () -> Unit, onConfirm: (Double) -> Unit) {
    val sheetState = rememberModalBottomSheetState()
    var value by remember { mutableStateOf("") }
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = { Box(Modifier.padding(top = 10.dp, bottom = 6.dp).width(36.dp).height(5.dp)
            .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(3.dp))) }) {
        Column(Modifier.padding(horizontal = 24.dp).padding(bottom = 32.dp)) {
            Text(title, fontSize = 20.sp, fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(16.dp))
            MoneyField(value, { value = it }, "Сумма, ₽", Modifier.fillMaxWidth())
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(100.0, 500.0, 1000.0, 5000.0).forEach { amount ->
                    AssistChip(onClick = { value = amount.toInt().toString() },
                        label = { Text(formatMoney(amount), fontSize = 13.sp) })
                }
            }
            Spacer(Modifier.height(20.dp))
            Button(onClick = { onConfirm(parseAmount(value)) }, enabled = parseAmount(value) > 0,
                shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.buttonColors(
                    containerColor = TBankYellow, contentColor = Color(0xFF0D0D0D),
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant),
                modifier = Modifier.fillMaxWidth().height(52.dp)) {
                Text("Готово", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
