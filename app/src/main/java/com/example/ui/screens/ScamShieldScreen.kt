package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.ImageSearch
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.core.ocr.ScreenshotOcrHelper
import com.example.core.security.LinkAnalysisResult
import com.example.core.security.MessageAnalysisResult
import com.example.core.security.ScamVerdict
import com.example.data.model.ScanHistoryItem
import com.example.data.model.TrustedContact
import com.example.ui.theme.FinSmartCard
import com.example.ui.theme.FinSmartDimens
import com.example.ui.theme.GradientActionButton
import com.example.ui.theme.IconBadge
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.LocalFinSmartColors
import com.example.ui.theme.SectionHeader
import com.example.ui.theme.StatusPill
import kotlinx.coroutines.launch

private enum class ScamTab(val label: String) {
    LINK("Link"),
    MESSAGE("Message"),
    CALL("Call"),
    SCREENSHOT("Screenshot")
}

@Composable
fun ScamShieldScreen(
    latestLinkResult: LinkAnalysisResult?,
    latestMessageResult: MessageAnalysisResult?,
    scanHistory: List<ScanHistoryItem>,
    trustedContacts: List<TrustedContact>,
    onCheckUrl: (String) -> Unit,
    onCheckMessage: (String, String) -> Unit,
    onCheckCall: (String, String) -> Unit,
    onReportLatestScan: () -> Unit,
    onSharedWithTrustedContact: (String) -> Unit,
    onBack: () -> Unit
) {
    val colors = LocalFinSmartColors.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var activeTab by remember { mutableStateOf(ScamTab.LINK) }
    var urlInput by remember { mutableStateOf("") }
    var messageInput by remember { mutableStateOf("") }
    var callNumberInput by remember { mutableStateOf("") }
    var callClaimInput by remember { mutableStateOf("") }

    var ocrExtractedText by remember { mutableStateOf("") }
    var isOcrLoading by remember { mutableStateOf(false) }
    var ocrError by remember { mutableStateOf<String?>(null) }
    var showPrivacyWarningDialog by remember { mutableStateOf(false) }
    var shareTargetText by remember { mutableStateOf<String?>(null) }
    var copyFeedback by remember { mutableStateOf<String?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            isOcrLoading = true
            ocrError = null
            scope.launch {
                ScreenshotOcrHelper.extractTextFromUri(context, uri)
                    .onSuccess { text ->
                        ocrExtractedText = text
                        isOcrLoading = false
                    }
                    .onFailure { err ->
                        ocrError = err.localizedMessage ?: "Failed to read text from screenshot."
                        isOcrLoading = false
                    }
            }
        }
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
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = colors.textPrimary)
                    }
                    IconBadge(icon = Icons.Default.Shield, tint = colors.primary, backgroundColor = colors.mintTint)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("FinSmart Scam Shield", style = MaterialTheme.typography.titleLarge, color = colors.textPrimary)
                        Text(
                            text = "Check links, SMS, calls & screenshots safely before acting",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textSecondary
                        )
                    }
                }
            }

            // Mode Tabs
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ScamTab.entries.forEach { tab ->
                        val selected = activeTab == tab
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (selected) colors.primary else colors.surface,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { activeTab = tab }
                                .testTag("scam_tab_${tab.name.lowercase()}")
                        ) {
                            Text(
                                text = tab.label,
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (selected) {
                                    if (colors.isDarkNeon) Color(0xFF041D14) else Color.White
                                } else colors.textPrimary,
                                modifier = Modifier.padding(vertical = 10.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }

            // Tab Inputs
            item {
                when (activeTab) {
                    ScamTab.LINK -> {
                        FinSmartCard(containerColor = colors.surface, accentBorderColor = colors.primary) {
                            Text("Paste a Suspicious Website Link", style = MaterialTheme.typography.titleMedium, color = colors.textPrimary)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "FinSmart never opens or downloads the website during scanning.",
                                style = MaterialTheme.typography.bodySmall,
                                color = colors.textSecondary
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = urlInput,
                                onValueChange = { urlInput = it },
                                label = { Text("Paste URL (e.g. sbi-kyc-update.in)") },
                                leadingIcon = { Icon(Icons.Default.Link, contentDescription = null) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().testTag("scam_url_input")
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                SuggestionChip(onClick = { urlInput = "https://sbi-kyc-update.in/login" }, label = { Text("Test Fake Link") })
                                SuggestionChip(onClick = { urlInput = "https://npci.org.in" }, label = { Text("Test Official") })
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            GradientActionButton(
                                text = "Check Safety",
                                icon = Icons.Default.Search,
                                onClick = { onCheckUrl(urlInput) },
                                modifier = Modifier.testTag("scam_check_url_button")
                            )
                        }
                    }
                    ScamTab.MESSAGE -> {
                        FinSmartCard(containerColor = colors.surface, accentBorderColor = colors.secondary) {
                            Text("Paste Suspicious SMS or WhatsApp Message", style = MaterialTheme.typography.titleMedium, color = colors.textPrimary)
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = messageInput,
                                onValueChange = { messageInput = it },
                                label = { Text("Paste message text here...") },
                                leadingIcon = { Icon(Icons.Default.Message, contentDescription = null) },
                                minLines = 3,
                                maxLines = 6,
                                modifier = Modifier.fillMaxWidth().testTag("scam_message_input")
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                SuggestionChip(
                                    onClick = { messageInput = "Dear customer, your bank KYC has expired today. Click bit.ly/kyc-fast or share OTP to avoid block." },
                                    label = { Text("Sample KYC SMS") }
                                )
                                SuggestionChip(
                                    onClick = { messageInput = "Your electricity connection will be disconnected tonight at 9 PM. Call 9876543210 and install AnyDesk." },
                                    label = { Text("Sample Power SMS") }
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            GradientActionButton(
                                text = "Analyse Message",
                                icon = Icons.Default.Search,
                                onClick = { onCheckMessage(messageInput, "MESSAGE") },
                                modifier = Modifier.testTag("scam_check_message_button")
                            )
                        }
                    }
                    ScamTab.CALL -> {
                        FinSmartCard(containerColor = colors.surface, accentBorderColor = colors.warning) {
                            Text("Fake Customer-Care & Call Checker", style = MaterialTheme.typography.titleMedium, color = colors.textPrimary)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Scammers plant fake helpline numbers in online search results and ask callers to share OTPs or install screen-sharing apps.",
                                style = MaterialTheme.typography.bodySmall,
                                color = colors.textSecondary
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = callNumberInput,
                                onValueChange = { callNumberInput = it },
                                label = { Text("Caller phone number (optional)") },
                                leadingIcon = { Icon(Icons.Default.Call, contentDescription = null) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = callClaimInput,
                                onValueChange = { callClaimInput = it },
                                label = { Text("What did the caller ask you to do?") },
                                minLines = 2,
                                maxLines = 4,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            GradientActionButton(
                                text = "Check Call Risk",
                                icon = Icons.Default.Search,
                                onClick = { onCheckCall(callNumberInput, callClaimInput) }
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            FinSmartCard(containerColor = colors.amberTint, accentBorderColor = colors.warning) {
                                Text("How to verify real customer care:", style = MaterialTheme.typography.titleSmall, color = colors.textPrimary)
                                Text(
                                    text = "• Use ONLY the official helpline printed on your bank passbook, debit card back, or inside your official bank app.\n• Real support NEVER asks for UPI PIN, OTP, or AnyDesk/TeamViewer.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = colors.textPrimary
                                )
                            }
                        }
                    }
                    ScamTab.SCREENSHOT -> {
                        FinSmartCard(containerColor = colors.surface, accentBorderColor = colors.accentPurple) {
                            Text("Screenshot Scam Analysis (On-Device OCR)", style = MaterialTheme.typography.titleMedium, color = colors.textPrimary)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Text is extracted privately on your phone using ML Kit OCR — your screenshot is not uploaded to external servers.",
                                style = MaterialTheme.typography.bodySmall,
                                color = colors.textSecondary
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            GradientActionButton(
                                text = "Select Screenshot from Phone",
                                icon = Icons.Default.ImageSearch,
                                onClick = { showPrivacyWarningDialog = true },
                                startColor = colors.accentPurple,
                                endColor = colors.accentBlue
                            )
                            if (isOcrLoading) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Reading text on device...", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                            if (ocrError != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(ocrError.orEmpty(), style = MaterialTheme.typography.bodySmall, color = colors.danger)
                            }
                            if (ocrExtractedText.isNotBlank()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Review or edit extracted text before checking:",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = colors.textPrimary
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                OutlinedTextField(
                                    value = ocrExtractedText,
                                    onValueChange = { ocrExtractedText = it },
                                    minLines = 3,
                                    maxLines = 7,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                GradientActionButton(
                                    text = "Analyse Extracted Text",
                                    icon = Icons.Default.Search,
                                    onClick = { onCheckMessage(ocrExtractedText, "SCREENSHOT") }
                                )
                            }
                        }
                    }
                }
            }

            // Link Result Display
            if (activeTab == ScamTab.LINK && latestLinkResult != null) {
                item {
                    val (bg, accent, icon) = verdictVisuals(latestLinkResult.verdict, colors)
                    FinSmartCard(containerColor = bg, accentBorderColor = accent) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconBadge(icon = icon, tint = accent, backgroundColor = accent.copy(alpha = 0.16f))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                StatusPill(text = latestLinkResult.verdict.badgeText, color = accent)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(latestLinkResult.headline, style = MaterialTheme.typography.titleMedium, color = colors.textPrimary)
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Domain: ${latestLinkResult.host} (${latestLinkResult.scheme.uppercase()})",
                            style = MaterialTheme.typography.bodySmall.copy(fontFamily = JetBrainsMonoFontFamily),
                            color = colors.textSecondary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(latestLinkResult.simpleExplanation, style = MaterialTheme.typography.bodyMedium, color = colors.textPrimary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Risk Indicators:", style = MaterialTheme.typography.titleSmall, color = colors.textPrimary)
                        latestLinkResult.indicators.forEach { ind ->
                            Text("• $ind", style = MaterialTheme.typography.bodySmall, color = colors.textPrimary)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Recommended Action: ${latestLinkResult.recommendedAction}", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold), color = accent)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(latestLinkResult.limitationsNote, style = MaterialTheme.typography.labelSmall, color = colors.textSecondary)

                        Spacer(modifier = Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            OutlinedButton(
                                onClick = {
                                    val summary = "FinSmart Link Check\nDomain: ${latestLinkResult.host}\nStatus: ${latestLinkResult.verdict.label}\nExplanation: ${latestLinkResult.simpleExplanation}"
                                    copyToClipboard(context, "FinSmart Link Analysis", summary)
                                    copyFeedback = "Analysis copied to clipboard."
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Copy")
                            }
                            OutlinedButton(
                                onClick = onReportLatestScan,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Flag, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Report")
                            }
                            OutlinedButton(
                                onClick = {
                                    shareTargetText = "Please help me review this link I received: ${latestLinkResult.host}. FinSmart result: ${latestLinkResult.verdict.label} — ${latestLinkResult.simpleExplanation}"
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Ask Family")
                            }
                        }
                    }
                }
            }

            // Message / Call / Screenshot Result Display
            if (activeTab != ScamTab.LINK && latestMessageResult != null) {
                item {
                    val (bg, accent, icon) = verdictVisuals(latestMessageResult.verdict, colors)
                    FinSmartCard(containerColor = bg, accentBorderColor = accent) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconBadge(icon = icon, tint = accent, backgroundColor = accent.copy(alpha = 0.16f))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                StatusPill(text = latestMessageResult.verdict.badgeText, color = accent)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(latestMessageResult.riskLabel, style = MaterialTheme.typography.titleMedium, color = colors.textPrimary)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(latestMessageResult.simpleExplanation, style = MaterialTheme.typography.bodyMedium, color = colors.textPrimary)
                        if (latestMessageResult.detectedPhrases.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Detected Warning Clues:", style = MaterialTheme.typography.titleSmall, color = colors.textPrimary)
                            latestMessageResult.detectedPhrases.forEach { p ->
                                Text("• $p", style = MaterialTheme.typography.bodySmall, color = colors.textPrimary)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Recommended Action: ${latestMessageResult.recommendedAction}", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold), color = accent)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("What to Avoid:", style = MaterialTheme.typography.titleSmall, color = colors.textPrimary)
                        latestMessageResult.avoidActions.forEach { av ->
                            Text("✕ $av", style = MaterialTheme.typography.bodySmall, color = colors.textSecondary)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(latestMessageResult.disclaimer, style = MaterialTheme.typography.labelSmall, color = colors.textSecondary)
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            OutlinedButton(
                                onClick = {
                                    copyToClipboard(
                                        context,
                                        "FinSmart Scam Check",
                                        "${latestMessageResult.riskLabel}\n${latestMessageResult.simpleExplanation}"
                                    )
                                    copyFeedback = "Analysis copied."
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Copy")
                            }
                            OutlinedButton(
                                onClick = {
                                    shareTargetText = "Can you help me check if this message is safe? FinSmart flagged: ${latestMessageResult.riskLabel}. Preview: ${latestMessageResult.inputPreview}"
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Ask Family")
                            }
                        }
                    }
                }
            }

            if (copyFeedback != null) {
                item {
                    FinSmartCard(containerColor = colors.mintTint, onClick = { copyFeedback = null }) {
                        Text(copyFeedback.orEmpty(), style = MaterialTheme.typography.bodySmall, color = colors.primary)
                    }
                }
            }

            // Recent Checks
            item {
                SectionHeader(title = "Recent Checks", subtitle = "Saved locally & synced when online")
            }
            if (scanHistory.isEmpty()) {
                item {
                    FinSmartCard {
                        Text(
                            text = "No checks yet. Paste any link or SMS above to inspect it safely.",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textSecondary
                        )
                    }
                }
            } else {
                items(scanHistory.take(10), key = { it.id }) { item ->
                    val verdict = ScamVerdict.entries.firstOrNull { it.name == item.verdict } ?: ScamVerdict.GREY
                    val (_, accent, icon) = verdictVisuals(verdict, colors)
                    FinSmartCard(containerColor = colors.surface, accentBorderColor = accent) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconBadge(icon = icon, tint = accent, backgroundColor = accent.copy(alpha = 0.14f), size = 38.dp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.inputPreview, style = MaterialTheme.typography.titleSmall, color = colors.textPrimary, maxLines = 1)
                                Text("${item.scanType} • ${item.summary}", style = MaterialTheme.typography.bodySmall, color = accent)
                            }
                            if (item.reported) {
                                StatusPill(text = "Reported", color = colors.danger)
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(12.dp)) }
        }
    }

    if (showPrivacyWarningDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyWarningDialog = false },
            title = { Text("Screenshot Privacy Notice") },
            text = {
                Text(
                    "Before choosing a screenshot, make sure you avoid exposing sensitive bank account numbers, passwords, or PINs. FinSmart reads text privately on your phone and lets you edit the extracted text before scanning."
                )
            },
            confirmButton = {
                Button(onClick = {
                    showPrivacyWarningDialog = false
                    photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }) {
                    Text("Choose Screenshot")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPrivacyWarningDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (shareTargetText != null) {
        val activeContacts = trustedContacts.filter { it.consentGranted && !it.revoked }
        AlertDialog(
            onDismissRequest = { shareTargetText = null },
            title = { Text("Share Check with Trusted Contact") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "You are voluntarily sharing only this scam check summary (no bank details or credentials are included):",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textSecondary
                    )
                    Text(shareTargetText.orEmpty(), style = MaterialTheme.typography.bodySmall)
                    if (activeContacts.isEmpty()) {
                        Text(
                            text = "No active Trusted Contacts added yet. You can add one in Tools -> Trusted Family Safety, or share directly via another app.",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.warning
                        )
                    } else {
                        activeContacts.forEach { contact ->
                            OutlinedButton(
                                onClick = {
                                    onSharedWithTrustedContact(contact.id)
                                    val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_TEXT, "Hi ${contact.name}, ${shareTargetText.orEmpty()}")
                                    }
                                    context.startActivity(Intent.createChooser(sendIntent, "Share with ${contact.name}"))
                                    shareTargetText = null
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Share with ${contact.name} (${contact.relationship})")
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val sendIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, shareTargetText.orEmpty())
                    }
                    context.startActivity(Intent.createChooser(sendIntent, "Share scam check"))
                    shareTargetText = null
                }) { Text("Share via Apps") }
            },
            dismissButton = {
                TextButton(onClick = { shareTargetText = null }) { Text("Close") }
            }
        )
    }
}

private fun verdictVisuals(
    verdict: ScamVerdict,
    colors: com.example.ui.theme.FinSmartColors
): Triple<Color, Color, androidx.compose.ui.graphics.vector.ImageVector> = when (verdict) {
    ScamVerdict.GREEN -> Triple(colors.mintTint, colors.success, Icons.Default.CheckCircle)
    ScamVerdict.YELLOW -> Triple(colors.amberTint, colors.warning, Icons.Default.Warning)
    ScamVerdict.RED -> Triple(colors.coralTint, colors.danger, Icons.Default.Warning)
    ScamVerdict.GREY -> Triple(colors.secondaryBackground, colors.textSecondary, Icons.Default.HelpOutline)
}

fun copyToClipboard(context: Context, label: String, text: String) {
    val cb = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    cb.setPrimaryClip(ClipData.newPlainText(label, text))
}
