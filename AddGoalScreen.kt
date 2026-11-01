package com.example.kopilka.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kopilka.ui.theme.TBankYellow

private val Palette = listOf(0xFFFFDD2D, 0xFF34C759, 0xFF007AFF,
    0xFFAF52DE, 0xFFFF9500, 0xFFFF3B30)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGoalScreen(onBack: () -> Unit,
    onSave: (name: String, target: Double, initial: Double, color: Long) -> Unit) {
    var name by remember { mutableStateOf("") }
    var target by remember { mutableStateOf("") }
    var initial by remember { mutableStateOf("") }
    var color by remember { mutableStateOf(Palette[0]) }
    val canSave = name.isNotBlank() && parseAmount(target) > 0
    val btnInteraction = remember { MutableInteractionSource() }
    val btnScale = rememberPressScale(btnInteraction)
    Scaffold(containerColor = MaterialTheme.colorScheme.background, topBar = {
        TopAppBar(title = { Text("Новая копилка", fontWeight = FontWeight.SemiBold) },
            navigationIcon = { IconButton(onClick = onBack) {
                Icon(Icons.Rounded.ArrowBack, contentDescription = "Назад")
            } }, colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background,
                titleContentColor = MaterialTheme.colorScheme.onBackground,
                navigationIconContentColor = MaterialTheme.colorScheme.onBackground))
    }) { pad ->
        Column(Modifier.fillMaxSize().padding(pad).padding(horizontal = 20.dp)) {
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(value = name, onValueChange = { name = it },
                label = { Text("Название") }, singleLine = true, shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    cursorColor = MaterialTheme.colorScheme.onSurface,
                    focusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant),
                modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(12.dp))
            MoneyField(target, { target = it }, "Цель, ₽", Modifier.fillMaxWidth())
            Spacer(Modifier.height(12.dp))
            MoneyField(initial, { initial = it }, "Уже есть, ₽", Modifier.fillMaxWidth())
            Spacer(Modifier.height(24.dp))
            Text("Цвет", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Palette.forEach { c ->
                    val selected = color == c
                    Box(Modifier.size(44.dp).background(Color(c), CircleShape).then(
                        if (selected) Modifier.border(3.dp, MaterialTheme.colorScheme.onBackground, CircleShape)
                        else Modifier).clickable { color = c })
                }
            }
            Spacer(Modifier.weight(1f))
            Button(onClick = { onSave(name.trim(), parseAmount(target), parseAmount(initial), color) },
                enabled = canSave, shape = RoundedCornerShape(16.dp), interactionSource = btnInteraction,
                colors = ButtonDefaults.buttonColors(containerColor = TBankYellow,
                    contentColor = Color(0xFF0D0D0D),
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant),
                modifier = Modifier.fillMaxWidth().height(56.dp)
                    .graphicsLayer { scaleX = btnScale; scaleY = btnScale }) {
                Text("Создать", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}
