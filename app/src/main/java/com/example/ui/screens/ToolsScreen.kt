package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.FinSmartCard
import com.example.ui.theme.FinSmartDimens
import com.example.ui.theme.IconBadge
import com.example.ui.theme.LocalFinSmartColors
import com.example.ui.theme.SectionHeader

@Composable
fun ToolsScreen(
    onOpenScamShield: () -> Unit,
    onOpenBeforeYouPay: () -> Unit,
    onOpenMoneyManager: () -> Unit,
    onOpenSavingsGoals: () -> Unit,
    onOpenCalculators: () -> Unit,
    onOpenFraudSimulator: () -> Unit,
    onOpenGovSchemes: () -> Unit,
    onOpenAwarenessVideos: () -> Unit,
    onOpenTrustedContacts: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val colors = LocalFinSmartColors.current

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
                Text(
                    text = "Safety & Money Tools",
                    style = MaterialTheme.typography.headlineMedium,
                    color = colors.textPrimary
                )
                Text(
                    text = "Practical tools for daily digital payments, budgeting & fraud prevention",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textSecondary
                )
            }

            item {
                SectionHeader(title = "Fraud Protection")
                Spacer(Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    ToolActionCard(
                        title = "FinSmart Scam Shield",
                        subtitle = "Check suspicious links, SMS messages, calls & screenshots",
                        icon = Icons.Default.Shield,
                        tint = colors.primary,
                        bg = colors.mintTint,
                        tag = "tool_scam_shield",
                        onClick = onOpenScamShield
                    )
                    ToolActionCard(
                        title = "Before You Pay Checklist",
                        subtitle = "Verify UPI, QR scan, collect requests & online sellers",
                        icon = Icons.Default.VerifiedUser,
                        tint = colors.accentPurple,
                        bg = colors.lavenderTint,
                        tag = "tool_before_you_pay",
                        onClick = onOpenBeforeYouPay
                    )
                    ToolActionCard(
                        title = "Fraud Simulator & Daily Practice",
                        subtitle = "Test your judgment against real-life Indian scam traps",
                        icon = Icons.Default.Psychology,
                        tint = colors.danger,
                        bg = colors.coralTint,
                        tag = "tool_fraud_simulator",
                        onClick = onOpenFraudSimulator
                    )
                }
            }

            item {
                SectionHeader(title = "Household Money & Calculators")
                Spacer(Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    ToolActionCard(
                        title = "Money Manager & Budget",
                        subtitle = "Track monthly income, expenses, insights & charts",
                        icon = Icons.Default.AccountBalanceWallet,
                        tint = colors.secondary,
                        bg = colors.cyanTint,
                        tag = "tool_money_manager",
                        onClick = onOpenMoneyManager
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        FinSmartCard(
                            modifier = Modifier
                                .weight(1f)
                                .testTag("tool_savings_goals"),
                            containerColor = colors.amberTint,
                            accentBorderColor = colors.warning,
                            onClick = onOpenSavingsGoals
                        ) {
                            IconBadge(icon = Icons.Default.Savings, tint = colors.warning, backgroundColor = colors.warning.copy(alpha = 0.16f))
                            Spacer(Modifier.height(8.dp))
                            Text("Savings Goals", style = MaterialTheme.typography.titleSmall, color = colors.textPrimary)
                            Text("Monthly targets", style = MaterialTheme.typography.bodySmall, color = colors.textSecondary)
                        }
                        FinSmartCard(
                            modifier = Modifier
                                .weight(1f)
                                .testTag("tool_calculators"),
                            containerColor = colors.softBlueTint,
                            accentBorderColor = colors.accentBlue,
                            onClick = onOpenCalculators
                        ) {
                            IconBadge(icon = Icons.Default.Calculate, tint = colors.accentBlue, backgroundColor = colors.accentBlue.copy(alpha = 0.16f))
                            Spacer(Modifier.height(8.dp))
                            Text("Calculators", style = MaterialTheme.typography.titleSmall, color = colors.textPrimary)
                            Text("EMI, Savings, Interest", style = MaterialTheme.typography.bodySmall, color = colors.textSecondary)
                        }
                    }
                }
            }

            item {
                SectionHeader(title = "Schemes, Guides & Family Safety")
                Spacer(Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    ToolActionCard(
                        title = "Government Financial Schemes",
                        subtitle = "PMJDY, APY, PMSBY, PMJJBY — official details & agent warnings",
                        icon = Icons.Default.AccountBalance,
                        tint = colors.primary,
                        bg = colors.surface,
                        tag = "tool_gov_schemes",
                        onClick = onOpenGovSchemes
                    )
                    ToolActionCard(
                        title = "Awareness Audio-Visual Guides",
                        subtitle = "Offline step-by-step narrated safety guides",
                        icon = Icons.Default.VideoLibrary,
                        tint = colors.accentBlue,
                        bg = colors.surface,
                        tag = "tool_awareness_videos",
                        onClick = onOpenAwarenessVideos
                    )
                    ToolActionCard(
                        title = "Trusted Family Safety",
                        subtitle = "Consent-based family contact for second opinions",
                        icon = Icons.Default.FamilyRestroom,
                        tint = colors.accentPurple,
                        bg = colors.surface,
                        tag = "tool_trusted_contacts",
                        onClick = onOpenTrustedContacts
                    )
                    ToolActionCard(
                        title = "Settings & Accessibility",
                        subtitle = "Language, Light / Dark Neon theme, Simple Mode & Voice Speed",
                        icon = Icons.Default.Settings,
                        tint = colors.textSecondary,
                        bg = colors.surface,
                        tag = "tool_settings",
                        onClick = onOpenSettings
                    )
                }
            }

            item { Spacer(Modifier.height(12.dp)) }
        }
    }
}

@Composable
private fun ToolActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    tint: Color,
    bg: Color,
    tag: String,
    onClick: () -> Unit
) {
    val colors = LocalFinSmartColors.current
    FinSmartCard(
        modifier = Modifier.testTag(tag),
        containerColor = bg,
        accentBorderColor = tint,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconBadge(icon = icon, tint = tint, backgroundColor = tint.copy(alpha = 0.16f), size = 46.dp)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = colors.textPrimary
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Open $title",
                tint = tint
            )
        }
    }
}
