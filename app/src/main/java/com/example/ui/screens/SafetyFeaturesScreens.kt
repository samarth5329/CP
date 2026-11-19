package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.local.FinSmartContentCatalog
import com.example.data.model.TrustedContact
import com.example.ui.theme.FinSmartCard
import com.example.ui.theme.FinSmartDimens
import com.example.ui.theme.GradientActionButton
import com.example.ui.theme.IconBadge
import com.example.ui.theme.LocalFinSmartColors
import com.example.ui.theme.StatusPill

@Composable
fun BeforeYouPayScreen(
    trustedContacts: List<TrustedContact>,
    onAskAi: () -> Unit,
    onBack: () -> Unit
) {
    val colors = LocalFinSmartColors.current
    val context = LocalContext.current
    val situations = FinSmartContentCatalog.beforeYouPaySituations
    var selectedSituation by remember { mutableStateOf(situations.first()) }
    val checkedItems = remember { mutableStateMapOf<String, Boolean>() }

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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = colors.textPrimary)
                    }
                    IconBadge(icon = Icons.Default.VerifiedUser, tint = colors.accentPurple, backgroundColor = colors.lavenderTint)
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text("Before You Pay", style = MaterialTheme.typography.titleLarge, color = colors.textPrimary)
                        Text("Quick interactive safety check before digital payments", style = MaterialTheme.typography.bodySmall, color = colors.textSecondary)
                    }
                }
            }

            item {
                Text("What are you about to do?", style = MaterialTheme.typography.titleMedium, color = colors.textPrimary)
            }

            items(situations) { sit ->
                val isSelected = selectedSituation.id == sit.id
                val (icon, tint, bg) = when (sit.id) {
                    "upi_transfer" -> Triple(Icons.Default.Send, colors.primary, colors.mintTint)
                    "qr_scan" -> Triple(Icons.Default.QrCodeScanner, colors.accentPurple, colors.lavenderTint)
                    "collect_request" -> Triple(Icons.Default.ReceiptLong, colors.accentBlue, colors.softBlueTint)
                    else -> Triple(Icons.Default.ShoppingBag, colors.warning, colors.amberTint)
                }
                FinSmartCard(
                    containerColor = if (isSelected) bg else colors.surface,
                    accentBorderColor = if (isSelected) tint else null,
                    onClick = { selectedSituation = sit }
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconBadge(icon = icon, tint = tint, backgroundColor = tint.copy(alpha = 0.16f), size = 42.dp)
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(sit.title, style = MaterialTheme.typography.titleSmall, color = colors.textPrimary)
                            Text(sit.subtitle, style = MaterialTheme.typography.bodySmall, color = colors.textSecondary)
                        }
                        if (isSelected) StatusPill(text = "Active", color = tint)
                    }
                }
            }

            item {
                FinSmartCard(containerColor = colors.mintTint, accentBorderColor = colors.success) {
                    Text("${selectedSituation.title} Checklist", style = MaterialTheme.typography.titleMedium, color = colors.textPrimary)
                    Spacer(Modifier.height(4.dp))
                    Text(selectedSituation.riskReminder, style = MaterialTheme.typography.bodySmall, color = colors.warning)
                    Spacer(Modifier.height(10.dp))
                    selectedSituation.checklist.forEachIndexed { idx, step ->
                        val key = "${selectedSituation.id}_$idx"
                        val checked = checkedItems[key] == true
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { checkedItems[key] = !checked }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(checked = checked, onCheckedChange = { checkedItems[key] = it })
                            Text(step, style = MaterialTheme.typography.bodyMedium, color = colors.textPrimary)
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    val allDone = selectedSituation.checklist.indices.all { checkedItems["${selectedSituation.id}_$it"] == true }
                    if (allDone) {
                        StatusPill(text = "All safety checks verified!", color = colors.success, icon = Icons.Default.CheckCircle)
                    }
                }
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(onClick = onAskAi, modifier = Modifier.weight(1f)) {
                        Text("Ask AI Sahayak")
                    }
                    val activeContact = trustedContacts.firstOrNull { it.consentGranted && !it.revoked }
                    if (activeContact != null) {
                        OutlinedButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, "Hi ${activeContact.name}, I am about to ${selectedSituation.title.lowercase()}. Can you quickly confirm if this looks safe?")
                                }
                                context.startActivity(Intent.createChooser(intent, "Ask ${activeContact.name}"))
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Ask ${activeContact.name}")
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(12.dp)) }
        }
    }
}

@Composable
fun FraudSimulatorScreen(
    onBack: () -> Unit
) {
    val colors = LocalFinSmartColors.current
    val scenarios = FinSmartContentCatalog.fraudScenarios
    var currentIdx by remember { mutableIntStateOf(0) }
    var selectedOpt by remember { mutableStateOf<Int?>(null) }
    var checked by remember { mutableStateOf(false) }

    val scenario = scenarios[currentIdx]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(FinSmartDimens.ScreenPadding)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = colors.textPrimary)
            }
            Text("Fraud Simulator", style = MaterialTheme.typography.titleLarge, color = colors.textPrimary)
        }
        Spacer(Modifier.height(10.dp))
        Text("Scenario ${currentIdx + 1} of ${scenarios.size}", style = MaterialTheme.typography.labelLarge, color = colors.secondary)
        Spacer(Modifier.height(8.dp))

        FinSmartCard(containerColor = colors.surface, accentBorderColor = colors.danger) {
            Text(scenario.title, style = MaterialTheme.typography.titleMedium, color = colors.textPrimary)
            Spacer(Modifier.height(8.dp))
            Text(scenario.situation, style = MaterialTheme.typography.bodyMedium, color = colors.textPrimary)
            Spacer(Modifier.height(12.dp))
            scenario.options.forEachIndexed { idx, opt ->
                val isSafe = idx == scenario.safeOptionIndex
                val bg = when {
                    !checked && selectedOpt == idx -> colors.softBlueTint
                    checked && isSafe -> colors.mintTint
                    checked && selectedOpt == idx && !isSafe -> colors.coralTint
                    else -> colors.secondaryBackground
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(bg)
                        .clickable(enabled = !checked) { selectedOpt = idx }
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(selected = selectedOpt == idx, onClick = { if (!checked) selectedOpt = idx })
                    Text(opt, style = MaterialTheme.typography.bodyMedium, color = colors.textPrimary)
                }
            }
            if (checked) {
                Spacer(Modifier.height(10.dp))
                val wasRight = selectedOpt == scenario.safeOptionIndex
                Text(
                    text = if (wasRight) "Safe Choice! ${scenario.explanation}" else "Danger! ${scenario.explanation}",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (wasRight) colors.success else colors.danger
                )
                Spacer(Modifier.height(4.dp))
                StatusPill(text = scenario.safetyRule, color = colors.primary)
            }
        }
        Spacer(Modifier.height(14.dp))
        GradientActionButton(
            text = if (!checked) "Check My Decision" else "Next Scenario",
            enabled = selectedOpt != null,
            onClick = {
                if (!checked) {
                    checked = true
                } else {
                    currentIdx = (currentIdx + 1) % scenarios.size
                    selectedOpt = null
                    checked = false
                }
            }
        )
    }
}

@Composable
fun GovSchemesScreen(onBack: () -> Unit) {
    val colors = LocalFinSmartColors.current
    val context = LocalContext.current

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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = colors.textPrimary)
                    }
                    IconBadge(icon = Icons.Default.AccountBalance, tint = colors.primary, backgroundColor = colors.mintTint)
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text("Government Schemes", style = MaterialTheme.typography.titleLarge, color = colors.textPrimary)
                        Text("Verified official banking & insurance schemes", style = MaterialTheme.typography.bodySmall, color = colors.textSecondary)
                    }
                }
            }

            item {
                FinSmartCard(containerColor = colors.amberTint, accentBorderColor = colors.warning) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = colors.warning)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Scheme terms may update over time. Always confirm final eligibility at your bank branch or official government portal. Never pay unauthorized agents.",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textPrimary
                        )
                    }
                }
            }

            items(FinSmartContentCatalog.govSchemes) { scheme ->
                FinSmartCard(containerColor = colors.surface, accentBorderColor = colors.primary) {
                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                        Text(scheme.shortName, style = MaterialTheme.typography.titleLarge, color = colors.primary)
                        StatusPill(text = "Verified: ${scheme.lastVerified}", color = colors.secondary)
                    }
                    Text(scheme.name, style = MaterialTheme.typography.titleSmall, color = colors.textPrimary)
                    Spacer(Modifier.height(6.dp))
                    Text(scheme.purpose, style = MaterialTheme.typography.bodyMedium, color = colors.textPrimary)
                    Spacer(Modifier.height(8.dp))
                    Text("Eligibility:", style = MaterialTheme.typography.titleSmall, color = colors.textPrimary)
                    scheme.eligibility.forEach { Text("• $it", style = MaterialTheme.typography.bodySmall, color = colors.textSecondary) }
                    Spacer(Modifier.height(6.dp))
                    Text("Key Benefits:", style = MaterialTheme.typography.titleSmall, color = colors.textPrimary)
                    scheme.benefits.forEach { Text("• $it", style = MaterialTheme.typography.bodySmall, color = colors.textSecondary) }
                    Spacer(Modifier.height(6.dp))
                    Text("Required Documents: ${scheme.documents.joinToString(", ")}", style = MaterialTheme.typography.bodySmall, color = colors.textPrimary)
                    Spacer(Modifier.height(6.dp))
                    Text("How to Apply:", style = MaterialTheme.typography.titleSmall, color = colors.textPrimary)
                    scheme.applicationSteps.forEachIndexed { idx, s -> Text("${idx + 1}. $s", style = MaterialTheme.typography.bodySmall, color = colors.textSecondary) }
                    Spacer(Modifier.height(8.dp))
                    FinSmartCard(containerColor = colors.coralTint, contentPadding = PaddingValues(10.dp)) {
                        Text("Agent Warning: ${scheme.agentWarning}", style = MaterialTheme.typography.bodySmall, color = colors.danger)
                    }
                    Spacer(Modifier.height(10.dp))
                    OutlinedButton(
                        onClick = {
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(scheme.officialUrl)))
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Visit Official Portal (${scheme.shortName})")
                    }
                }
            }
            item { Spacer(Modifier.height(12.dp)) }
        }
    }
}

@Composable
fun AwarenessVideosScreen(
    isSpeaking: Boolean,
    onPlayAudioGuide: (String) -> Unit,
    onStopAudio: () -> Unit,
    onBack: () -> Unit
) {
    val colors = LocalFinSmartColors.current
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background),
        contentPadding = PaddingValues(FinSmartDimens.ScreenPadding),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = colors.textPrimary)
                }
                IconBadge(icon = Icons.Default.VideoLibrary, tint = colors.accentBlue, backgroundColor = colors.softBlueTint)
                Spacer(Modifier.width(10.dp))
                Column {
                    Text("Awareness Audio-Visual Guides", style = MaterialTheme.typography.titleLarge, color = colors.textPrimary)
                    Text("Works offline with built-in voice narration", style = MaterialTheme.typography.bodySmall, color = colors.textSecondary)
                }
            }
        }
        items(FinSmartContentCatalog.awarenessVideos) { guide ->
            FinSmartCard(containerColor = colors.surface, accentBorderColor = colors.accentBlue) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        StatusPill(text = "${guide.category} • ${guide.duration}", color = colors.accentBlue)
                        Spacer(Modifier.height(6.dp))
                        Text(guide.title, style = MaterialTheme.typography.titleMedium, color = colors.textPrimary)
                    }
                    IconButton(onClick = { if (isSpeaking) onStopAudio() else onPlayAudioGuide(guide.narrationScript) }) {
                        Icon(
                            imageVector = if (isSpeaking) Icons.Default.Stop else Icons.Default.PlayArrow,
                            contentDescription = "Play guide",
                            tint = colors.primary
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                Text(guide.narrationScript, style = MaterialTheme.typography.bodySmall, color = colors.textSecondary)
                Spacer(Modifier.height(8.dp))
                guide.keyPoints.forEach { kp ->
                    Text("✓ $kp", style = MaterialTheme.typography.labelMedium, color = colors.primary)
                }
            }
        }
    }
}

@Composable
fun TrustedContactsScreen(
    contacts: List<TrustedContact>,
    onSaveContact: (String?, String, String, String, Boolean) -> Unit,
    onToggleRevoke: (String, Boolean) -> Unit,
    onDeleteContact: (String) -> Unit,
    onBack: () -> Unit
) {
    val colors = LocalFinSmartColors.current
    var showAddDialog by remember { mutableStateOf(false) }

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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = colors.textPrimary)
                    }
                    IconBadge(icon = Icons.Default.FamilyRestroom, tint = colors.primary, backgroundColor = colors.mintTint)
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text("Trusted Family Safety", style = MaterialTheme.typography.titleLarge, color = colors.textPrimary)
                        Text("Optional second opinion for suspicious messages", style = MaterialTheme.typography.bodySmall, color = colors.textSecondary)
                    }
                }
            }

            item {
                FinSmartCard(containerColor = colors.softBlueTint, accentBorderColor = colors.accentBlue) {
                    Text("Privacy & Control Guarantee:", style = MaterialTheme.typography.titleSmall, color = colors.textPrimary)
                    Text(
                        text = "• FinSmart NEVER shares financial data or messages automatically.\n• You decide each time whether to share a scam check.\n• Trusted contacts can NEVER view or control your bank account.\n• You can pause or revoke access with one tap.",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textPrimary
                    )
                }
            }

            item {
                GradientActionButton(
                    text = "Add Trusted Family Contact",
                    icon = Icons.Default.Add,
                    onClick = { showAddDialog = true },
                    modifier = Modifier.testTag("add_trusted_contact_button")
                )
            }

            if (contacts.isEmpty()) {
                item {
                    FinSmartCard {
                        Text("No trusted contact added yet. Ideal for elderly family members or first-time UPI users who want a second opinion before paying strangers.", style = MaterialTheme.typography.bodySmall, color = colors.textSecondary)
                    }
                }
            } else {
                items(contacts, key = { it.id }) { contact ->
                    FinSmartCard(
                        containerColor = colors.surface,
                        accentBorderColor = if (contact.revoked) colors.warning else colors.primary
                    ) {
                        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(contact.name, style = MaterialTheme.typography.titleMedium, color = colors.textPrimary)
                                Text("${contact.relationship} • ${contact.phoneNumber}", style = MaterialTheme.typography.bodySmall, color = colors.textSecondary)
                                Text("Alerts shared manually: ${contact.sharedAlertsCount}", style = MaterialTheme.typography.labelSmall, color = colors.textSecondary)
                            }
                            StatusPill(
                                text = if (contact.revoked) "Access Paused" else "Active Consent",
                                color = if (contact.revoked) colors.warning else colors.success
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = { onToggleRevoke(contact.id, contact.revoked) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(if (contact.revoked) "Restore Access" else "Revoke Access")
                            }
                            IconButton(onClick = { onDeleteContact(contact.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete contact", tint = colors.danger)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        var name by remember { mutableStateOf("") }
        var relation by remember { mutableStateOf("Son / Daughter") }
        var phone by remember { mutableStateOf("") }
        var consent by remember { mutableStateOf(false) }
        var err by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add Trusted Contact") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Full Name") })
                    OutlinedTextField(value = relation, onValueChange = { relation = it }, label = { Text("Relationship") })
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone Number") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { consent = !consent }) {
                        Checkbox(checked = consent, onCheckedChange = { consent = it })
                        Text(
                            text = "I give explicit consent to save this contact so I can manually ask them for help on suspicious messages. No data is shared automatically.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    if (err != null) Text(err.orEmpty(), color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (name.isBlank() || phone.filter { it.isDigit() }.length < 6) {
                        err = "Enter a valid name and phone number."
                        return@Button
                    }
                    if (!consent) {
                        err = "Please check the explicit consent box."
                        return@Button
                    }
                    onSaveContact(null, name, relation, phone, true)
                    showAddDialog = false
                }) { Text("Save") }
            },
            dismissButton = { TextButton(onClick = { showAddDialog = false }) { Text("Cancel") } }
        )
    }
}
