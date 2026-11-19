package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.local.FinSmartContentCatalog
import com.example.data.local.FinSmartStrings
import com.example.data.local.UserSettingsState
import com.example.data.model.LearningModuleId
import com.example.data.model.UserProfile
import com.example.ui.theme.FinSmartCard
import com.example.ui.theme.FinSmartDimens
import com.example.ui.theme.IconBadge
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.LocalFinSmartColors
import com.example.ui.theme.OfflineStatusChip
import com.example.ui.theme.SectionHeader
import com.example.ui.theme.StatusPill
import com.example.ui.viewmodel.MonthlyMoneySummary
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun HomeScreen(
    userName: String,
    profile: UserProfile?,
    settings: UserSettingsState,
    summary: MonthlyMoneySummary,
    isOnline: Boolean,
    isSpeaking: Boolean,
    onReadTip: (String) -> Unit,
    onStopTts: () -> Unit,
    onOpenScamShield: () -> Unit,
    onOpenBeforeYouPay: () -> Unit,
    onOpenAiSahayak: () -> Unit,
    onOpenMoneyManager: () -> Unit,
    onOpenSavingsGoals: () -> Unit,
    onOpenSos: () -> Unit,
    onOpenLearnModule: (LearningModuleId) -> Unit,
    onOpenSettings: () -> Unit
) {
    val colors = LocalFinSmartColors.current
    val strings = FinSmartStrings.get(settings.language)
    val inrFormat = remember { NumberFormat.getIntegerInstance(Locale("en", "IN")) }

    val completedCount = profile?.completedLessonIds?.size ?: 0
    val totalLessons = FinSmartContentCatalog.lessons.size
    val safetyScore = profile?.financialSafetyScore ?: 55
    val tips = FinSmartContentCatalog.dailySafetyTips[settings.language]
        ?: FinSmartContentCatalog.dailySafetyTips.values.first()
    val tipIndex = ((profile?.streakDays ?: 1) - 1).coerceIn(0, tips.lastIndex)
    val todayTip = tips[tipIndex]

    var showNotificationsDialog by remember { mutableStateOf(false) }

    val scoreLabel = when {
        safetyScore >= 85 -> "Safety Guardian"
        safetyScore >= 65 -> "Safety Aware"
        else -> "Getting Started"
    }

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
            // Top Brand Bar
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_finsmart_emblem),
                            contentDescription = "FinSmart logo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "FinSmart",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = colors.textPrimary
                            )
                            Text(
                                text = "Smart Money. Safe Future.",
                                style = MaterialTheme.typography.labelSmall,
                                color = colors.textSecondary
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OfflineStatusChip(isOnline = isOnline)
                        Spacer(modifier = Modifier.width(4.dp))
                        IconButton(
                            onClick = { showNotificationsDialog = true },
                            modifier = Modifier.testTag("home_notifications_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Safety notifications",
                                tint = colors.textPrimary
                            )
                        }
                        IconButton(
                            onClick = onOpenSettings,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(colors.mintTint)
                                .testTag("home_profile_settings_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Profile and settings",
                                tint = colors.primary
                            )
                        }
                    }
                }
            }

            // Greeting + Scenic Hero Banner
            item {
                Column {
                    Text(
                        text = "${strings.greetingPrefix}, ${userName.substringBefore(" ")}!",
                        style = MaterialTheme.typography.headlineMedium,
                        color = colors.textPrimary
                    )
                    Text(
                        text = strings.companionTagline,
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(148.dp)
                            .clip(RoundedCornerShape(22.dp))
                    ) {
                        Image(
                            painter = painterResource(
                                id = if (colors.isDarkNeon) R.drawable.img_home_banner_dark
                                else R.drawable.img_home_banner_light
                            ),
                            contentDescription = "FinSmart family financial protection illustration",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, colors.background.copy(alpha = 0.72f))
                                    )
                                )
                        )
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            StatusPill(
                                text = "Lessons: $completedCount / $totalLessons",
                                color = colors.primary,
                                icon = Icons.Default.VerifiedUser
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            if (settings.simpleMode) {
                                StatusPill(
                                    text = "Simple Mode On",
                                    color = colors.accentPurple
                                )
                            }
                        }
                    }
                }
            }

            // Financial Safety Score Card
            item {
                FinSmartCard(
                    containerColor = if (colors.isDarkNeon) colors.surface else colors.mintTint.copy(alpha = 0.55f),
                    accentBorderColor = colors.primary
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            IconBadge(
                                icon = Icons.Default.Shield,
                                tint = colors.primary,
                                backgroundColor = colors.primary.copy(alpha = 0.16f),
                                size = 48.dp,
                                iconSize = 26.dp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = strings.safetyScoreTitle,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = colors.textSecondary
                                )
                                Text(
                                    text = "$safetyScore / 100",
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontFamily = JetBrainsMonoFontFamily,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = colors.textPrimary
                                )
                            }
                        }
                        StatusPill(text = scoreLabel, color = colors.success)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    LinearProgressIndicator(
                        progress = { safetyScore / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(9.dp)
                            .clip(RoundedCornerShape(50)),
                        color = colors.primary,
                        trackColor = colors.surface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Complete lessons, quizzes, and Scam Shield checks to raise your score.",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textSecondary
                    )
                }
            }

            // Money Summary 3-Card Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MoneyStatMiniCard(
                        label = strings.incomeLabel,
                        amountText = "₹${inrFormat.format(summary.income.roundToInt())}",
                        bgColor = colors.mintTint,
                        accentColor = colors.primary,
                        modifier = Modifier.weight(1f),
                        onClick = onOpenMoneyManager
                    )
                    MoneyStatMiniCard(
                        label = strings.expensesLabel,
                        amountText = "₹${inrFormat.format(summary.totalExpenses.roundToInt())}",
                        bgColor = colors.coralTint,
                        accentColor = colors.danger,
                        modifier = Modifier.weight(1f),
                        onClick = onOpenMoneyManager
                    )
                    MoneyStatMiniCard(
                        label = strings.savingsLabel,
                        amountText = "₹${inrFormat.format(summary.remaining.roundToInt())}",
                        bgColor = colors.cyanTint,
                        accentColor = colors.secondary,
                        modifier = Modifier.weight(1f),
                        onClick = onOpenMoneyManager
                    )
                }
            }

            // Quick Actions (adapts cleanly for Simple Mode!)
            item {
                SectionHeader(
                    title = strings.quickActionsTitle,
                    subtitle = if (settings.simpleMode) "Large buttons for easy everyday use" else "Instant safety & financial tools"
                )
                Spacer(modifier = Modifier.height(10.dp))

                if (settings.simpleMode) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        SimpleModeActionCard(
                            title = "Check Link or SMS",
                            subtitle = "Check if a message or link is fake",
                            icon = Icons.Default.Link,
                            tint = colors.primary,
                            bg = colors.mintTint,
                            onClick = onOpenScamShield
                        )
                        SimpleModeActionCard(
                            title = "Before You Pay Checklist",
                            subtitle = "Verify before sending money or scanning QR",
                            icon = Icons.Default.VerifiedUser,
                            tint = colors.accentPink,
                            bg = colors.coralTint,
                            onClick = onOpenBeforeYouPay
                        )
                        SimpleModeActionCard(
                            title = "Ask AI Sahayak",
                            subtitle = "Ask questions in English, ಕನ್ನಡ or हिन्दी",
                            icon = Icons.Default.SmartToy,
                            tint = colors.accentPurple,
                            bg = colors.lavenderTint,
                            onClick = onOpenAiSahayak
                        )
                        SimpleModeActionCard(
                            title = "Emergency Fraud Help (1930)",
                            subtitle = "Call 1930 immediately if money was stolen",
                            icon = Icons.Default.ReportProblem,
                            tint = colors.danger,
                            bg = colors.coralTint,
                            onClick = onOpenSos
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            QuickActionTile(
                                title = "Check Link\n& Message",
                                icon = Icons.Default.Link,
                                tint = colors.primary,
                                bg = colors.mintTint,
                                modifier = Modifier.weight(1f).testTag("qa_scam_shield"),
                                onClick = onOpenScamShield
                            )
                            QuickActionTile(
                                title = "Before\nYou Pay",
                                icon = Icons.Default.VerifiedUser,
                                tint = colors.accentPink,
                                bg = colors.coralTint,
                                modifier = Modifier.weight(1f).testTag("qa_before_you_pay"),
                                onClick = onOpenBeforeYouPay
                            )
                            QuickActionTile(
                                title = "Ask AI\nSahayak",
                                icon = Icons.Default.SmartToy,
                                tint = colors.accentPurple,
                                bg = colors.lavenderTint,
                                modifier = Modifier.weight(1f).testTag("qa_ai_sahayak"),
                                onClick = onOpenAiSahayak
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            QuickActionTile(
                                title = "Add\nExpense",
                                icon = Icons.Default.AddCircleOutline,
                                tint = colors.accentBlue,
                                bg = colors.softBlueTint,
                                modifier = Modifier.weight(1f).testTag("qa_add_expense"),
                                onClick = onOpenMoneyManager
                            )
                            QuickActionTile(
                                title = "Savings\nGoal",
                                icon = Icons.Default.Savings,
                                tint = colors.accentAmber,
                                bg = colors.amberTint,
                                modifier = Modifier.weight(1f).testTag("qa_savings_goal"),
                                onClick = onOpenSavingsGoals
                            )
                            QuickActionTile(
                                title = "Report\nFraud",
                                icon = Icons.Default.ReportProblem,
                                tint = colors.danger,
                                bg = colors.coralTint,
                                modifier = Modifier.weight(1f).testTag("qa_report_fraud"),
                                onClick = onOpenSos
                            )
                        }
                    }
                }
            }

            // Today's Safety Tip with TTS Read-Aloud
            item {
                FinSmartCard(
                    containerColor = colors.amberTint,
                    accentBorderColor = colors.warning
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconBadge(
                            icon = Icons.Default.Lightbulb,
                            tint = colors.warning,
                            backgroundColor = colors.warning.copy(alpha = 0.2f),
                            size = 42.dp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = strings.todaysTipTitle,
                                style = MaterialTheme.typography.titleSmall,
                                color = colors.textPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = todayTip,
                                style = MaterialTheme.typography.bodySmall,
                                color = colors.textPrimary.copy(alpha = 0.85f)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = { if (isSpeaking) onStopTts() else onReadTip(todayTip) },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(colors.primary)
                                .testTag("home_read_tip_button")
                        ) {
                            Icon(
                                imageVector = if (isSpeaking) Icons.Default.Stop else Icons.Default.PlayArrow,
                                contentDescription = if (isSpeaking) "Stop reading tip" else "Read safety tip aloud",
                                tint = if (colors.isDarkNeon) Color(0xFF042014) else Color.White
                            )
                        }
                    }
                }
            }

            // Recommended Learning Module
            item {
                SectionHeader(
                    title = "Recommended Next Step",
                    subtitle = "Continue your financial safety journey"
                )
                Spacer(modifier = Modifier.height(8.dp))
                val nextModule = remember(profile) {
                    val done = profile?.completedLessonIds.orEmpty().toSet()
                    LearningModuleId.entries.firstOrNull { mod ->
                        FinSmartContentCatalog.lessons.any { it.moduleId == mod && it.id !in done }
                    } ?: LearningModuleId.FRAUD_SAFETY
                }
                FinSmartCard(
                    containerColor = colors.softBlueTint,
                    accentBorderColor = colors.accentBlue,
                    onClick = { onOpenLearnModule(nextModule) }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconBadge(
                            icon = Icons.Default.AccountBalance,
                            tint = colors.accentBlue,
                            backgroundColor = colors.accentBlue.copy(alpha = 0.16f)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = nextModule.title,
                                style = MaterialTheme.typography.titleMedium,
                                color = colors.textPrimary
                            )
                            Text(
                                text = nextModule.subtitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = colors.textSecondary
                            )
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Open module",
                            tint = colors.accentBlue
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(12.dp)) }
        }
    }

    if (showNotificationsDialog) {
        AlertDialog(
            onDismissRequest = { showNotificationsDialog = false },
            title = { Text("Safety Alerts & Reminders") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "• Reminder: Never enter your UPI PIN to receive money or cashback.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "• Offline Ready: Core banking & fraud safety lessons are available even without internet.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "• Tip: Set up a Trusted Family Contact in Tools for extra protection.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showNotificationsDialog = false }) {
                    Text("Got it")
                }
            }
        )
    }
}

@Composable
private fun MoneyStatMiniCard(
    label: String,
    amountText: String,
    bgColor: Color,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val colors = LocalFinSmartColors.current
    FinSmartCard(
        modifier = modifier,
        containerColor = bgColor,
        accentBorderColor = accentColor,
        contentPadding = PaddingValues(12.dp),
        onClick = onClick
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = colors.textSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = amountText,
            style = MaterialTheme.typography.titleMedium.copy(
                fontFamily = JetBrainsMonoFontFamily,
                fontWeight = FontWeight.Bold
            ),
            color = colors.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun QuickActionTile(
    title: String,
    icon: ImageVector,
    tint: Color,
    bg: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val colors = LocalFinSmartColors.current
    FinSmartCard(
        modifier = modifier.height(104.dp),
        containerColor = bg,
        accentBorderColor = tint,
        contentPadding = PaddingValues(10.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconBadge(
                icon = icon,
                tint = tint,
                backgroundColor = tint.copy(alpha = 0.16f),
                size = 38.dp,
                iconSize = 20.dp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = colors.textPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
private fun SimpleModeActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    tint: Color,
    bg: Color,
    onClick: () -> Unit
) {
    val colors = LocalFinSmartColors.current
    FinSmartCard(
        containerColor = bg,
        accentBorderColor = tint,
        contentPadding = PaddingValues(16.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconBadge(icon = icon, tint = tint, backgroundColor = tint.copy(alpha = 0.18f), size = 52.dp, iconSize = 28.dp)
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = colors.textPrimary)
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = colors.textSecondary)
            }
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = tint)
        }
    }
}
