package com.example.ui.screens

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DownloadForOffline
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.FinSmartStrings
import com.example.data.local.UserSettingsState
import com.example.data.model.AppLanguage
import com.example.data.model.LearningModuleId
import com.example.ui.theme.FinSmartCard
import com.example.ui.theme.FinSmartDimens
import com.example.ui.theme.IconBadge
import com.example.ui.theme.LocalFinSmartColors
import com.example.ui.theme.ThemeMode

@Composable
fun SettingsScreen(
    userEmail: String,
    settings: UserSettingsState,
    onSetSimpleMode: (Boolean) -> Unit,
    onSetLanguage: (AppLanguage) -> Unit,
    onSetThemeMode: (ThemeMode) -> Unit,
    onSetTextScale: (Float) -> Unit,
    onSetHighContrast: (Boolean) -> Unit,
    onSetVoiceSpeed: (Float) -> Unit,
    onTestVoice: () -> Unit,
    onSetNotifications: (Boolean) -> Unit,
    onToggleOfflineModule: (String) -> Unit,
    onOpenTrustedContacts: () -> Unit,
    onSignOut: () -> Unit,
    onBack: () -> Unit
) {
    val colors = LocalFinSmartColors.current
    val strings = FinSmartStrings.get(settings.language)

    var showLanguageDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showAccessibilityDialog by remember { mutableStateOf(false) }
    var showOfflineDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = colors.textPrimary)
                    }
                    Text(strings.settingsTitle, style = MaterialTheme.typography.headlineMedium, color = colors.textPrimary)
                }
                if (userEmail.isNotBlank()) {
                    Text(
                        text = "Signed in as $userEmail",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textSecondary,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                }
            }

            // Simple Mode Toggle Card
            item {
                FinSmartCard(
                    containerColor = colors.mintTint,
                    accentBorderColor = colors.primary
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconBadge(icon = Icons.Default.SmartToy, tint = colors.primary, backgroundColor = colors.primary.copy(alpha = 0.16f))
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(strings.simpleModeTitle, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = colors.textPrimary)
                            Text(strings.simpleModeSubtitle, style = MaterialTheme.typography.bodySmall, color = colors.textSecondary)
                        }
                        Switch(
                            checked = settings.simpleMode,
                            onCheckedChange = onSetSimpleMode,
                            modifier = Modifier.testTag("settings_simple_mode_switch")
                        )
                    }
                }
            }

            // Language
            item {
                SettingsRowCard(
                    title = "Language",
                    subtitle = "${settings.language.displayName} (${settings.language.nativeLabel})",
                    icon = Icons.Default.Language,
                    tint = colors.primary,
                    bg = colors.mintTint,
                    tag = "settings_language_card",
                    onClick = { showLanguageDialog = true }
                )
            }

            // Appearance (Light / Dark Neon / System Default)
            item {
                SettingsRowCard(
                    title = "Appearance",
                    subtitle = settings.themeMode.label,
                    icon = Icons.Default.DarkMode,
                    tint = colors.accentAmber,
                    bg = colors.amberTint,
                    tag = "settings_appearance_card",
                    onClick = { showThemeDialog = true }
                )
            }

            // Accessibility
            item {
                SettingsRowCard(
                    title = "Accessibility",
                    subtitle = "Text size ${(settings.textScale * 100).toInt()}% • Contrast: ${if (settings.highContrast) "High" else "Standard"} • Voice ${settings.voiceSpeed}x",
                    icon = Icons.Default.AccessibilityNew,
                    tint = colors.accentBlue,
                    bg = colors.softBlueTint,
                    tag = "settings_accessibility_card",
                    onClick = { showAccessibilityDialog = true }
                )
            }

            // Notifications
            item {
                FinSmartCard(containerColor = colors.surface) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        IconBadge(icon = Icons.Default.Notifications, tint = colors.danger, backgroundColor = colors.coralTint)
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text("Notifications", style = MaterialTheme.typography.titleMedium, color = colors.textPrimary)
                            Text("Learning reminders, savings goals & safety tips", style = MaterialTheme.typography.bodySmall, color = colors.textSecondary)
                        }
                        Switch(
                            checked = settings.notificationsEnabled,
                            onCheckedChange = onSetNotifications,
                            modifier = Modifier.testTag("settings_notifications_switch")
                        )
                    }
                }
            }

            // Offline Learning Downloads
            item {
                SettingsRowCard(
                    title = "Offline Downloads",
                    subtitle = "${settings.offlineSavedModuleIds.size} of ${LearningModuleId.entries.size} modules pinned offline",
                    icon = Icons.Default.DownloadForOffline,
                    tint = colors.secondary,
                    bg = colors.cyanTint,
                    tag = "settings_offline_card",
                    onClick = { showOfflineDialog = true }
                )
            }

            // Trusted Family Contacts
            item {
                SettingsRowCard(
                    title = "Trusted Family Safety",
                    subtitle = "Manage consent-based family contacts",
                    icon = Icons.Default.FamilyRestroom,
                    tint = colors.accentPurple,
                    bg = colors.lavenderTint,
                    tag = "settings_trusted_contacts_card",
                    onClick = onOpenTrustedContacts
                )
            }

            // Privacy & Security
            item {
                SettingsRowCard(
                    title = "Privacy & Security",
                    subtitle = "Your data, permissions & safety guarantees",
                    icon = Icons.Default.Security,
                    tint = colors.warning,
                    bg = colors.amberTint,
                    tag = "settings_privacy_card",
                    onClick = { showPrivacyDialog = true }
                )
            }

            // About FinSmart
            item {
                SettingsRowCard(
                    title = "About FinSmart",
                    subtitle = "Version 2.0.0",
                    icon = Icons.Default.Info,
                    tint = colors.accentBlue,
                    bg = colors.softBlueTint,
                    tag = "settings_about_card",
                    onClick = { showAboutDialog = true }
                )
            }

            // Sign out button
            item {
                Spacer(Modifier.height(4.dp))
                Button(
                    onClick = onSignOut,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.coralTint,
                        contentColor = colors.danger
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("settings_logout_button")
                ) {
                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Sign Out", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }

    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text("Select Language") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    AppLanguage.entries.forEach { lang ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    onSetLanguage(lang)
                                    showLanguageDialog = false
                                }
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = settings.language == lang, onClick = {
                                onSetLanguage(lang)
                                showLanguageDialog = false
                            })
                            Text("${lang.displayName} (${lang.nativeLabel})", style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showLanguageDialog = false }) { Text("Close") } }
        )
    }

    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = { Text("Choose Appearance") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    ThemeMode.entries.forEach { mode ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    onSetThemeMode(mode)
                                    showThemeDialog = false
                                }
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = settings.themeMode == mode, onClick = {
                                onSetThemeMode(mode)
                                showThemeDialog = false
                            })
                            Text(mode.label, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showThemeDialog = false }) { Text("Done") } }
        )
    }

    if (showAccessibilityDialog) {
        AlertDialog(
            onDismissRequest = { showAccessibilityDialog = false },
            title = { Text("Accessibility Controls") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Text Size", style = MaterialTheme.typography.titleSmall)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(1.0f to "Normal", 1.15f to "Large", 1.3f to "Extra Large").forEach { (scale, lbl) ->
                            FilterChip(
                                selected = kotlin.math.abs(settings.textScale - scale) < 0.05f,
                                onClick = { onSetTextScale(scale) },
                                label = { Text(lbl) }
                            )
                        }
                    }
                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                        Text("High Contrast Mode", style = MaterialTheme.typography.titleSmall)
                        Switch(checked = settings.highContrast, onCheckedChange = onSetHighContrast)
                    }
                    Text("Voice Speed (${String.format(java.util.Locale.US, "%.2fx", settings.voiceSpeed)})", style = MaterialTheme.typography.titleSmall)
                    Slider(
                        value = settings.voiceSpeed,
                        onValueChange = onSetVoiceSpeed,
                        valueRange = 0.75f..1.4f
                    )
                    OutlinedButton(onClick = onTestVoice, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Default.RecordVoiceOver, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Test Voice Read-Aloud")
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showAccessibilityDialog = false }) { Text("Done") } }
        )
    }

    if (showOfflineDialog) {
        AlertDialog(
            onDismissRequest = { showOfflineDialog = false },
            title = { Text("Offline Learning Modules") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("All 18 core lessons work offline automatically. Pin your priority modules below:", style = MaterialTheme.typography.bodySmall)
                    LearningModuleId.entries.forEach { mod ->
                        val checked = mod.name in settings.offlineSavedModuleIds
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onToggleOfflineModule(mod.name) },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(checked = checked, onCheckedChange = { onToggleOfflineModule(mod.name) })
                            Text(mod.title, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showOfflineDialog = false }) { Text("Done") } }
        )
    }

    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = { Text("Privacy & Security Pledge") },
            text = {
                Text(
                    "• FinSmart NEVER asks for bank passwords, OTPs, CVVs, ATM PINs, or UPI PINs.\n" +
                        "• FinSmart NEVER reads SMS inbox or call logs automatically.\n" +
                        "• Screenshot OCR uses Google's Photo Picker (zero broad storage permissions) and extracts text on-device.\n" +
                        "• Trusted family contacts are optional and manual — nothing is shared automatically.\n" +
                        "• Your learning progress, budget, goals, and scan history are stored securely under your private account."
                )
            },
            confirmButton = { TextButton(onClick = { showPrivacyDialog = false }) { Text("Understood") } }
        )
    }

    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text("About FinSmart v2.0.0") },
            text = {
                Text(
                    "FinSmart is an educational financial literacy and digital fraud-safety companion built to empower Indian households, elderly citizens, rural communities, and first-time UPI users.\n\n" +
                        "FinSmart is NOT a bank, lender, or payment app and does not hold funds or guarantee fraud recovery."
                )
            },
            confirmButton = { TextButton(onClick = { showAboutDialog = false }) { Text("Close") } }
        )
    }
}

@Composable
private fun SettingsRowCard(
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
        containerColor = colors.surface,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconBadge(icon = icon, tint = tint, backgroundColor = bg)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium, color = colors.textPrimary)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = colors.textSecondary)
            }
        }
    }
}
