package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.local.FinSmartContentCatalog
import com.example.data.model.ExpenseCategory
import com.example.data.model.ExpenseRecord
import com.example.data.model.MonthlyBudget
import com.example.data.model.SavingsGoal
import com.example.ui.theme.FinSmartCard
import com.example.ui.theme.FinSmartDimens
import com.example.ui.theme.GradientActionButton
import com.example.ui.theme.IconBadge
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.LocalFinSmartColors
import com.example.ui.theme.SectionHeader
import com.example.ui.theme.StatusPill
import com.example.ui.viewmodel.MonthlyMoneySummary
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.pow
import kotlin.math.roundToInt

enum class MoneyTab(val label: String) {
    OVERVIEW("Overview"),
    EXPENSES("Expenses"),
    GOALS("Goals"),
    NEEDS_WANTS("Needs/Wants"),
    CALCULATORS("Calculators")
}

@Composable
fun MoneyManagerScreen(
    initialTab: MoneyTab = MoneyTab.OVERVIEW,
    summary: MonthlyMoneySummary,
    expenses: List<ExpenseRecord>,
    budgets: List<MonthlyBudget>,
    goals: List<SavingsGoal>,
    onShiftMonth: (Int) -> Unit,
    onSaveBudget: (MonthlyBudget) -> Unit,
    onSaveExpense: (String?, String, ExpenseCategory, Double, Long, String?) -> Unit,
    onDeleteExpense: (String) -> Unit,
    onSaveGoal: (String?, String, String, Double, Double, Long) -> Unit,
    onDeleteGoal: (String) -> Unit,
    onBack: () -> Unit
) {
    val colors = LocalFinSmartColors.current
    val inr = remember { NumberFormat.getIntegerInstance(Locale("en", "IN")) }
    var activeTab by remember(initialTab) { mutableStateOf(initialTab) }
    var showBudgetDialog by remember { mutableStateOf(false) }
    var editingExpense by remember { mutableStateOf<ExpenseRecord?>(null) }
    var showAddExpenseDialog by remember { mutableStateOf(false) }
    var editingGoal by remember { mutableStateOf<SavingsGoal?>(null) }
    var showAddGoalDialog by remember { mutableStateOf(false) }

    val categoryColors = listOf(
        colors.primary,
        colors.secondary,
        colors.accentPurple,
        colors.accentBlue,
        colors.accentPink,
        colors.warning,
        colors.danger,
        colors.accentAmber
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .widthIn(max = 720.dp)
                .fillMaxSize(),
            contentPadding = PaddingValues(FinSmartDimens.ScreenPadding),
            verticalArrangement = Arrangement.spacedBy(FinSmartDimens.SectionGap)
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = colors.textPrimary)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Money Manager", style = MaterialTheme.typography.titleLarge, color = colors.textPrimary)
                        Text("Track expenses, plan savings & reach goals", style = MaterialTheme.typography.bodySmall, color = colors.textSecondary)
                    }
                }
            }

            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(MoneyTab.entries) { tab ->
                        FilterChip(
                            selected = activeTab == tab,
                            onClick = { activeTab = tab },
                            label = { Text(tab.label) },
                            modifier = Modifier.testTag("money_tab_${tab.name.lowercase()}")
                        )
                    }
                }
            }

            // Month Switcher
            item {
                FinSmartCard(containerColor = colors.surface) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { onShiftMonth(-1) }) {
                            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Previous month", tint = colors.textPrimary)
                        }
                        Text(summary.monthLabel, style = MaterialTheme.typography.titleMedium, color = colors.textPrimary)
                        IconButton(onClick = { onShiftMonth(1) }) {
                            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Next month", tint = colors.textPrimary)
                        }
                    }
                }
            }

            when (activeTab) {
                MoneyTab.OVERVIEW -> {
                    item {
                        FinSmartCard(containerColor = colors.surface, accentBorderColor = colors.primary) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(120.dp)) {
                                    DonutChart(
                                        slices = if (summary.income > 0) {
                                            listOf(
                                                summary.totalExpenses.toFloat() to colors.danger,
                                                summary.remaining.coerceAtLeast(0.0).toFloat() to colors.primary
                                            )
                                        } else {
                                            listOf(1f to colors.secondaryBackground)
                                        },
                                        modifier = Modifier.size(110.dp)
                                    )
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = "₹${inr.format(summary.income.roundToInt())}",
                                            style = MaterialTheme.typography.labelLarge.copy(fontFamily = JetBrainsMonoFontFamily, fontWeight = FontWeight.Bold),
                                            color = colors.textPrimary
                                        )
                                        Text("Income", style = MaterialTheme.typography.labelSmall, color = colors.textSecondary)
                                    }
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                                        Text("Expenses", style = MaterialTheme.typography.bodySmall, color = colors.danger)
                                        Text("₹${inr.format(summary.totalExpenses.roundToInt())}", style = MaterialTheme.typography.titleSmall.copy(fontFamily = JetBrainsMonoFontFamily), color = colors.textPrimary)
                                    }
                                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                                        Text("Remaining", style = MaterialTheme.typography.bodySmall, color = colors.primary)
                                        Text("₹${inr.format(summary.remaining.roundToInt())}", style = MaterialTheme.typography.titleSmall.copy(fontFamily = JetBrainsMonoFontFamily), color = colors.textPrimary)
                                    }
                                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                                        Text("Savings %", style = MaterialTheme.typography.bodySmall, color = colors.secondary)
                                        Text("${summary.savingsPercent.roundToInt()}%", style = MaterialTheme.typography.titleSmall.copy(fontFamily = JetBrainsMonoFontFamily), color = colors.secondary)
                                    }
                                    TextButton(onClick = { showBudgetDialog = true }, contentPadding = PaddingValues(0.dp)) {
                                        Text("Set Monthly Income & Budget")
                                    }
                                }
                            }
                        }
                    }

                    // Expense Breakdown Chart
                    item {
                        val nonZero = ExpenseCategory.entries.mapIndexedNotNull { idx, cat ->
                            val amt = summary.categoryTotals[cat] ?: 0.0
                            if (amt > 0.0) Triple(cat, amt, categoryColors[idx % categoryColors.size]) else null
                        }
                        FinSmartCard(containerColor = colors.surface) {
                            Text("Expense Breakdown", style = MaterialTheme.typography.titleMedium, color = colors.textPrimary)
                            Spacer(modifier = Modifier.height(10.dp))
                            if (nonZero.isEmpty()) {
                                Text(
                                    text = "No expenses recorded for ${summary.monthLabel}. Tap Expenses to add your real spending.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = colors.textSecondary
                                )
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    DonutChart(
                                        slices = nonZero.map { it.second.toFloat() to it.third },
                                        modifier = Modifier.size(100.dp)
                                    )
                                    Spacer(modifier = Modifier.width(14.dp))
                                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                                        nonZero.forEach { (cat, amt, c) ->
                                            val pct = if (summary.totalExpenses > 0) ((amt / summary.totalExpenses) * 100).roundToInt() else 0
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Box(Modifier.size(10.dp).clip(CircleShape).background(c))
                                                    Spacer(Modifier.width(6.dp))
                                                    Text(cat.label, style = MaterialTheme.typography.bodySmall, color = colors.textPrimary)
                                                }
                                                Text("₹${inr.format(amt.roundToInt())} ($pct%)", style = MaterialTheme.typography.labelSmall, color = colors.textSecondary)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Yearly Totals Bar Summary
                    item {
                        val currentYear = summary.monthKey.substringBefore("-")
                        val yearTotal = expenses.filter { it.monthKey.startsWith(currentYear) }.sumOf { it.amount }
                        FinSmartCard(containerColor = colors.softBlueTint, accentBorderColor = colors.accentBlue) {
                            Text("$currentYear Yearly Expense Total: ₹${inr.format(yearTotal.roundToInt())}", style = MaterialTheme.typography.titleSmall, color = colors.textPrimary)
                            Text("Educational money-management tracker — FinSmart does not hold funds or access bank accounts.", style = MaterialTheme.typography.bodySmall, color = colors.textSecondary)
                        }
                    }

                    // Real Financial Insights
                    item {
                        FinSmartCard(containerColor = colors.mintTint, accentBorderColor = colors.primary) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconBadge(icon = Icons.Default.Lightbulb, tint = colors.primary, backgroundColor = colors.primary.copy(alpha = 0.16f), size = 38.dp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("Money Insights", style = MaterialTheme.typography.titleMedium, color = colors.textPrimary)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            summary.insights.forEach { insight ->
                                Text("• $insight", style = MaterialTheme.typography.bodySmall, color = colors.textPrimary)
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                        }
                    }
                }

                MoneyTab.EXPENSES -> {
                    item {
                        GradientActionButton(
                            text = "Add New Expense",
                            icon = Icons.Default.Add,
                            onClick = { showAddExpenseDialog = true },
                            modifier = Modifier.testTag("add_expense_button")
                        )
                    }
                    val monthExpenses = expenses.filter { it.monthKey == summary.monthKey }
                    if (monthExpenses.isEmpty()) {
                        item {
                            FinSmartCard {
                                Text("No expenses logged for ${summary.monthLabel}. Add an expense above to start tracking.", style = MaterialTheme.typography.bodySmall, color = colors.textSecondary)
                            }
                        }
                    } else {
                        items(monthExpenses, key = { it.id }) { exp ->
                            val cat = ExpenseCategory.entries.firstOrNull { it.name == exp.category } ?: ExpenseCategory.OTHER
                            val dateStr = SimpleDateFormat("dd MMM", Locale.US).format(Date(exp.dateMillis))
                            FinSmartCard(containerColor = colors.surface) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(exp.title, style = MaterialTheme.typography.titleSmall, color = colors.textPrimary)
                                        Text("${cat.label} • $dateStr", style = MaterialTheme.typography.bodySmall, color = colors.textSecondary)
                                        if (!exp.note.isNullOrBlank()) {
                                            Text(exp.note, style = MaterialTheme.typography.labelSmall, color = colors.textSecondary)
                                        }
                                    }
                                    Text(
                                        text = "₹${inr.format(exp.amount.roundToInt())}",
                                        style = MaterialTheme.typography.titleMedium.copy(fontFamily = JetBrainsMonoFontFamily, fontWeight = FontWeight.Bold),
                                        color = colors.danger
                                    )
                                    IconButton(onClick = { editingExpense = exp }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit expense", tint = colors.secondary)
                                    }
                                    IconButton(onClick = { onDeleteExpense(exp.id) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete expense", tint = colors.danger)
                                    }
                                }
                            }
                        }
                    }
                }

                MoneyTab.GOALS -> {
                    item {
                        GradientActionButton(
                            text = "Create Savings Goal",
                            icon = Icons.Default.Savings,
                            onClick = { showAddGoalDialog = true }
                        )
                    }
                    if (goals.isEmpty()) {
                        item {
                            FinSmartCard {
                                Text("Create goals like Emergency Fund, Education, or Medical Savings. FinSmart suggests monthly savings targets without moving any money.", style = MaterialTheme.typography.bodySmall, color = colors.textSecondary)
                            }
                        }
                    } else {
                        items(goals, key = { it.id }) { goal ->
                            val pct = if (goal.targetAmount > 0) ((goal.savedAmount / goal.targetAmount) * 100).coerceIn(0.0, 100.0).roundToInt() else 0
                            val remaining = (goal.targetAmount - goal.savedAmount).coerceAtLeast(0.0)
                            val monthsLeft = ((goal.targetDateMillis - System.currentTimeMillis()) / (30.0 * 86_400_000.0)).coerceAtLeast(1.0)
                            val suggestedMonthly = (remaining / monthsLeft).roundToInt()
                            FinSmartCard(containerColor = colors.surface, accentBorderColor = colors.primary) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(goal.title, style = MaterialTheme.typography.titleMedium, color = colors.textPrimary)
                                        Text(goal.category, style = MaterialTheme.typography.bodySmall, color = colors.textSecondary)
                                    }
                                    StatusPill(text = "$pct%", color = colors.primary)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                LinearProgressIndicator(
                                    progress = { pct / 100f },
                                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(50)),
                                    color = colors.primary,
                                    trackColor = colors.secondaryBackground
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Saved ₹${inr.format(goal.savedAmount.roundToInt())} of ₹${inr.format(goal.targetAmount.roundToInt())} • Remaining ₹${inr.format(remaining.roundToInt())}", style = MaterialTheme.typography.bodySmall, color = colors.textPrimary)
                                Text("Suggested monthly saving: ₹${inr.format(suggestedMonthly)}/month", style = MaterialTheme.typography.labelMedium, color = colors.secondary)
                                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                                    TextButton(onClick = { editingGoal = goal }) { Text("Update") }
                                    TextButton(onClick = { onDeleteGoal(goal.id) }) { Text("Delete", color = colors.danger) }
                                }
                            }
                        }
                    }
                }

                MoneyTab.NEEDS_WANTS -> {
                    item { NeedsVsWantsInteractiveCard() }
                }

                MoneyTab.CALCULATORS -> {
                    item { FinancialCalculatorsSection() }
                }
            }

            item { Spacer(modifier = Modifier.height(12.dp)) }
        }
    }

    if (showBudgetDialog) {
        val currentBudget = budgets.firstOrNull { it.monthKey == summary.monthKey } ?: MonthlyBudget(monthKey = summary.monthKey)
        BudgetEditorDialog(
            budget = currentBudget,
            onDismiss = { showBudgetDialog = false },
            onSave = { updated ->
                onSaveBudget(updated)
                showBudgetDialog = false
            }
        )
    }

    if (showAddExpenseDialog || editingExpense != null) {
        ExpenseEditorDialog(
            existing = editingExpense,
            onDismiss = {
                showAddExpenseDialog = false
                editingExpense = null
            },
            onSave = { title, cat, amount, note ->
                onSaveExpense(editingExpense?.id, title, cat, amount, editingExpense?.dateMillis ?: System.currentTimeMillis(), note)
                showAddExpenseDialog = false
                editingExpense = null
            }
        )
    }

    if (showAddGoalDialog || editingGoal != null) {
        GoalEditorDialog(
            existing = editingGoal,
            onDismiss = {
                showAddGoalDialog = false
                editingGoal = null
            },
            onSave = { title, cat, target, saved, months ->
                val targetDate = System.currentTimeMillis() + (months.coerceIn(1, 240) * 30L * 86_400_000L)
                onSaveGoal(editingGoal?.id, title, cat, target, saved, targetDate)
                showAddGoalDialog = false
                editingGoal = null
            }
        )
    }
}

@Composable
private fun DonutChart(
    slices: List<Pair<Float, Color>>,
    modifier: Modifier = Modifier
) {
    val total = slices.sumOf { it.first.toDouble() }.toFloat().coerceAtLeast(1f)
    Canvas(modifier = modifier) {
        val strokeWidth = size.minDimension * 0.22f
        val diameter = size.minDimension - strokeWidth
        val topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f)
        val arcSize = Size(diameter, diameter)
        var startAngle = -90f
        slices.forEach { (value, color) ->
            val sweep = (value / total) * 360f
            drawArc(
                color = color,
                startAngle = startAngle,
                sweepAngle = (sweep - 2f).coerceAtLeast(1f),
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
            )
            startAngle += sweep
        }
    }
}

@Composable
private fun NeedsVsWantsInteractiveCard() {
    val colors = LocalFinSmartColors.current
    val choices = remember { mutableStateMapOf<String, Boolean>() }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        FinSmartCard(containerColor = colors.mintTint, accentBorderColor = colors.primary) {
            Text("Needs vs Wants Learning Tool", style = MaterialTheme.typography.titleMedium, color = colors.textPrimary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "NEEDS: Food, housing, school fees, medicines & bills.\nWANTS: Entertainment, impulse shopping & extra subscriptions.",
                style = MaterialTheme.typography.bodySmall,
                color = colors.textPrimary
            )
        }
        FinSmartContentCatalog.needsVsWantsItems.forEach { item ->
            val chosen = choices[item.id]
            FinSmartCard(containerColor = colors.surface) {
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(item.title, style = MaterialTheme.typography.titleSmall, color = colors.textPrimary)
                        Text("Example: ${item.exampleCost}", style = MaterialTheme.typography.bodySmall, color = colors.textSecondary)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        FilterChip(selected = chosen == true, onClick = { choices[item.id] = true }, label = { Text("Need") })
                        FilterChip(selected = chosen == false, onClick = { choices[item.id] = false }, label = { Text("Want") })
                    }
                }
                if (chosen != null) {
                    Spacer(Modifier.height(6.dp))
                    val right = chosen == item.isNeed
                    Text(
                        text = if (right) "Great thinking! ${item.explanation}" else "Friendly tip: ${item.explanation}",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (right) colors.success else colors.warning
                    )
                }
            }
        }
    }
}

@Composable
private fun FinancialCalculatorsSection() {
    val colors = LocalFinSmartColors.current
    val inr = remember { NumberFormat.getIntegerInstance(Locale("en", "IN")) }
    var calcIndex by remember { mutableIntStateOf(0) }
    var principalStr by remember { mutableStateOf("50000") }
    var rateStr by remember { mutableStateOf("10") }
    var monthsStr by remember { mutableStateOf("12") }

    val p = principalStr.toDoubleOrNull()?.coerceAtLeast(0.0) ?: 0.0
    val r = rateStr.toDoubleOrNull()?.coerceAtLeast(0.0) ?: 0.0
    val m = monthsStr.toIntOrNull()?.coerceAtLeast(1) ?: 1

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = calcIndex == 0, onClick = { calcIndex = 0 }, label = { Text("EMI Loan") })
            FilterChip(selected = calcIndex == 1, onClick = { calcIndex = 1 }, label = { Text("Savings Plan") })
            FilterChip(selected = calcIndex == 2, onClick = { calcIndex = 2 }, label = { Text("Interest") })
        }

        FinSmartCard(containerColor = colors.surface, accentBorderColor = colors.accentPurple) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconBadge(icon = Icons.Default.Calculate, tint = colors.accentPurple, backgroundColor = colors.lavenderTint)
                Spacer(Modifier.width(10.dp))
                Text(
                    text = when (calcIndex) {
                        0 -> "Loan EMI Calculator"
                        1 -> "Monthly Savings Target"
                        else -> "Simple & Compound Interest"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.textPrimary
                )
            }
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = principalStr,
                onValueChange = { principalStr = it },
                label = { Text(if (calcIndex == 1) "Target Goal Amount (₹)" else "Principal Amount (₹)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            if (calcIndex != 1) {
                OutlinedTextField(
                    value = rateStr,
                    onValueChange = { rateStr = it },
                    label = { Text("Annual Interest Rate (%)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
            }
            OutlinedTextField(
                value = monthsStr,
                onValueChange = { monthsStr = it },
                label = { Text("Duration (Months)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            FinSmartCard(containerColor = colors.mintTint, accentBorderColor = colors.primary) {
                when (calcIndex) {
                    0 -> {
                        val monthlyRate = (r / 12.0) / 100.0
                        val emi = if (monthlyRate == 0.0) p / m else {
                            val factor = (1.0 + monthlyRate).pow(m)
                            (p * monthlyRate * factor) / (factor - 1.0)
                        }
                        val totalPayable = emi * m
                        Text("Estimated Monthly EMI: ₹${inr.format(emi.roundToInt())}", style = MaterialTheme.typography.titleMedium, color = colors.primary)
                        Text("Total Repayment: ₹${inr.format(totalPayable.roundToInt())} (Interest: ₹${inr.format((totalPayable - p).coerceAtLeast(0.0).roundToInt())})", style = MaterialTheme.typography.bodySmall, color = colors.textPrimary)
                    }
                    1 -> {
                        val perMonth = p / m
                        Text("Save ₹${inr.format(perMonth.roundToInt())} per month", style = MaterialTheme.typography.titleMedium, color = colors.primary)
                        Text("You will reach ₹${inr.format(p.roundToInt())} in $m months.", style = MaterialTheme.typography.bodySmall, color = colors.textPrimary)
                    }
                    else -> {
                        val years = m / 12.0
                        val simple = p * (r / 100.0) * years
                        val compound = p * ((1.0 + r / 100.0).pow(years) - 1.0)
                        Text("Simple Interest: ₹${inr.format(simple.roundToInt())} (Total: ₹${inr.format((p + simple).roundToInt())})", style = MaterialTheme.typography.titleSmall, color = colors.primary)
                        Text("Annual Compound Interest: ₹${inr.format(compound.roundToInt())} (Total: ₹${inr.format((p + compound).roundToInt())})", style = MaterialTheme.typography.bodySmall, color = colors.textPrimary)
                    }
                }
            }
        }
    }
}

@Composable
private fun BudgetEditorDialog(
    budget: MonthlyBudget,
    onDismiss: () -> Unit,
    onSave: (MonthlyBudget) -> Unit
) {
    var income by remember { mutableStateOf(if (budget.monthlyIncome > 0) budget.monthlyIncome.roundToInt().toString() else "") }
    var food by remember { mutableStateOf(if (budget.foodLimit > 0) budget.foodLimit.roundToInt().toString() else "") }
    var household by remember { mutableStateOf(if (budget.householdLimit > 0) budget.householdLimit.roundToInt().toString() else "") }
    var bills by remember { mutableStateOf(if (budget.billsLimit > 0) budget.billsLimit.roundToInt().toString() else "") }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Set Monthly Budget (${budget.monthKey})") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = income,
                    onValueChange = { income = it },
                    label = { Text("Monthly Income (₹)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = household,
                    onValueChange = { household = it },
                    label = { Text("Household Budget (₹)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = food,
                    onValueChange = { food = it },
                    label = { Text("Food Budget (₹)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = bills,
                    onValueChange = { bills = it },
                    label = { Text("Bills & Electricity (₹)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                if (error != null) Text(error.orEmpty(), color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }
        },
        confirmButton = {
            Button(onClick = {
                val inc = income.toDoubleOrNull()
                if (inc == null || inc < 0.0) {
                    error = "Enter a valid monthly income (0 or more)."
                    return@Button
                }
                onSave(
                    budget.copy(
                        monthlyIncome = inc,
                        householdLimit = household.toDoubleOrNull()?.coerceAtLeast(0.0) ?: 0.0,
                        foodLimit = food.toDoubleOrNull()?.coerceAtLeast(0.0) ?: 0.0,
                        billsLimit = bills.toDoubleOrNull()?.coerceAtLeast(0.0) ?: 0.0
                    )
                )
            }) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
private fun ExpenseEditorDialog(
    existing: ExpenseRecord?,
    onDismiss: () -> Unit,
    onSave: (String, ExpenseCategory, Double, String?) -> Unit
) {
    var title by remember { mutableStateOf(existing?.title.orEmpty()) }
    var amount by remember { mutableStateOf(if (existing != null && existing.amount > 0) existing.amount.roundToInt().toString() else "") }
    var category by remember {
        mutableStateOf(ExpenseCategory.entries.firstOrNull { it.name == existing?.category } ?: ExpenseCategory.FOOD)
    }
    var note by remember { mutableStateOf(existing?.note.orEmpty()) }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (existing == null) "Add Expense" else "Edit Expense") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Expense description") })
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount (₹)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(ExpenseCategory.entries) { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat.label) }
                        )
                    }
                }
                OutlinedTextField(value = note, onValueChange = { note = it }, label = { Text("Note (optional)") })
                if (error != null) Text(error.orEmpty(), color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }
        },
        confirmButton = {
            Button(onClick = {
                val amt = amount.toDoubleOrNull()
                if (title.isBlank() || amt == null || amt <= 0.0) {
                    error = "Enter a description and amount greater than 0."
                    return@Button
                }
                onSave(title, category, amt, note.ifBlank { null })
            }) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
private fun GoalEditorDialog(
    existing: SavingsGoal?,
    onDismiss: () -> Unit,
    onSave: (String, String, Double, Double, Int) -> Unit
) {
    var title by remember { mutableStateOf(existing?.title.orEmpty()) }
    var category by remember { mutableStateOf(existing?.category ?: "Emergency Fund") }
    var target by remember { mutableStateOf(if (existing != null) existing.targetAmount.roundToInt().toString() else "") }
    var saved by remember { mutableStateOf(if (existing != null) existing.savedAmount.roundToInt().toString() else "0") }
    var months by remember { mutableStateOf("12") }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (existing == null) "Create Savings Goal" else "Update Savings Goal") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Goal name (e.g. Emergency Fund)") })
                OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category (Education / Medical / Home)") })
                OutlinedTextField(value = target, onValueChange = { target = it }, label = { Text("Target Amount (₹)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                OutlinedTextField(value = saved, onValueChange = { saved = it }, label = { Text("Amount Already Saved (₹)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                OutlinedTextField(value = months, onValueChange = { months = it }, label = { Text("Target Months Ahead") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                if (error != null) Text(error.orEmpty(), color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }
        },
        confirmButton = {
            Button(onClick = {
                val t = target.toDoubleOrNull()
                val s = saved.toDoubleOrNull() ?: 0.0
                val m = months.toIntOrNull() ?: 12
                if (title.isBlank() || t == null || t <= 0.0 || s < 0.0) {
                    error = "Enter a valid title and positive target amount."
                    return@Button
                }
                onSave(title, category, t, s, m)
            }) { Text("Save Goal") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
