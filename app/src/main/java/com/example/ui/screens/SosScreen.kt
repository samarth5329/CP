package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.local.FinSmartStrings
import com.example.data.local.UserSettingsState
import com.example.data.model.IncidentReport
import com.example.ui.theme.FinSmartCard
import com.example.ui.theme.FinSmartDimens
import com.example.ui.theme.GradientActionButton
import com.example.ui.theme.IconBadge
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.LocalFinSmartColors
import com.example.ui.theme.SectionHeader
import com.example.ui.theme.StatusPill
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun SosScreen(
    settings: UserSettingsState,
    incidentReports: List<IncidentReport>,
    onSaveIncidentReport: (
        incidentDate: String,
        approximateTime: String,
        amount: Double,
        referenceId: String,
        paymentMethod: String,
        description: String,
        evidenceSummary: String?
    ) -> Unit
) {
    val colors = LocalFinSmartColors.current
    val strings = FinSmartStrings.get(settings.language)
    val context = LocalContext.current
    val inr = remember { NumberFormat.getIntegerInstance(Locale("en", "IN")) }

    var showReportDialog by remember { mutableStateOf(false) }
    var reportFeedback by remember { mutableStateOf<String?>(null) }

    val immediateSteps = remember {
        listOf(
            "Stop interacting with the suspected caller or fraudster immediately.",
            "Do NOT share any OTP, UPI PIN, ATM PIN, CVV, or screen-sharing codes.",
            "Call 1930 (National Cyber Crime Helpline) immediately — early reporting improves chances of freezing funds, though recovery is not guaranteed.",
            "Report online at https://cybercrime.gov.in/ and save your acknowledgement number.",
            "Contact your bank immediately using ONLY the official helpline printed on your passbook, debit card back, or inside your official bank app.",
            "Temporarily block compromised UPI IDs, debit/credit cards, and internet banking.",
            "Preserve evidence: take screenshots of messages, caller numbers, UTR / UPI transaction reference IDs, and bank SMS alerts.",
            "File a complaint at your nearest police station / Cyber Crime Cell with your prepared incident summary."
        )
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
                    IconBadge(
                        icon = Icons.Default.NotificationsActive,
                        tint = colors.danger,
                        backgroundColor = colors.coralTint,
                        size = 48.dp,
                        iconSize = 26.dp
                    )
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(strings.sosTitle, style = MaterialTheme.typography.headlineSmall, color = colors.textPrimary)
                        Text(strings.sosSubtitle, style = MaterialTheme.typography.bodySmall, color = colors.textSecondary)
                    }
                }
            }

            // Call 1930 Primary Emergency Button
            item {
                Button(
                    onClick = {
                        val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:1930"))
                        context.startActivity(dialIntent)
                    },
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.danger,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("sos_call_1930_button")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(28.dp))
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                text = strings.call1930Title,
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Text(
                                text = strings.call1930Subtitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }
                }
            }

            // Cybercrime Portal & Official Bank Contact Card
            item {
                OutlinedButton(
                    onClick = {
                        val portalIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://cybercrime.gov.in/"))
                        context.startActivity(portalIntent)
                    },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("sos_portal_button")
                ) {
                    Icon(Icons.Default.AccountBalance, contentDescription = null, tint = colors.danger)
                    Spacer(Modifier.width(8.dp))
                    Text("Report at cybercrime.gov.in", style = MaterialTheme.typography.titleSmall, color = colors.textPrimary)
                    Spacer(Modifier.width(8.dp))
                    Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(18.dp), tint = colors.textSecondary)
                }
                Spacer(Modifier.height(10.dp))
                FinSmartCard(containerColor = colors.amberTint, accentBorderColor = colors.warning) {
                    Text("Verify Official Bank Contact Numbers", style = MaterialTheme.typography.titleSmall, color = colors.textPrimary)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Never search random customer care numbers online. Check the back of your ATM/debit card, your official bank passbook, or the Help section inside your official bank mobile app to freeze your account.",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textPrimary
                    )
                }
            }

            // 8 Immediate Action Steps
            item {
                FinSmartCard(containerColor = colors.surface, accentBorderColor = colors.danger) {
                    Text("Immediate Action Steps", style = MaterialTheme.typography.titleMedium, color = colors.textPrimary)
                    Spacer(Modifier.height(10.dp))
                    immediateSteps.forEachIndexed { idx, step ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(colors.danger),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${idx + 1}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                            }
                            Spacer(Modifier.width(10.dp))
                            Text(
                                text = step,
                                style = MaterialTheme.typography.bodySmall,
                                color = colors.textPrimary,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Prepare Incident Details Button
            item {
                GradientActionButton(
                    text = "Prepare Incident Details",
                    icon = Icons.Default.Description,
                    startColor = colors.accentPurple,
                    endColor = colors.accentBlue,
                    contentColor = Color.White,
                    onClick = { showReportDialog = true },
                    modifier = Modifier.testTag("sos_prepare_incident_button")
                )
            }

            if (reportFeedback != null) {
                item {
                    FinSmartCard(containerColor = colors.mintTint, onClick = { reportFeedback = null }) {
                        Text(reportFeedback.orEmpty(), style = MaterialTheme.typography.bodySmall, color = colors.primary)
                    }
                }
            }

            if (incidentReports.isNotEmpty()) {
                item {
                    SectionHeader(title = "Prepared Incident Reports", subtitle = "Copy or share when filing on 1930 / cybercrime.gov.in")
                }
                items(incidentReports, key = { it.id }) { report ->
                    val formattedSummary = buildString {
                        appendLine("FinSmart Fraud Incident Summary")
                        appendLine("Date & Time: ${report.incidentDate} ${report.approximateTime}")
                        appendLine("Amount Lost: ₹${inr.format(report.amount.roundToInt())}")
                        appendLine("Payment Method: ${report.paymentMethod}")
                        appendLine("Transaction / UTR Ref: ${report.referenceId}")
                        appendLine("Details: ${report.description}")
                        if (!report.evidenceSummary.isNullOrBlank()) {
                            appendLine("Evidence Notes: ${report.evidenceSummary}")
                        }
                    }
                    FinSmartCard(containerColor = colors.surface, accentBorderColor = colors.accentPurple) {
                        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                            Text(
                                text = "₹${inr.format(report.amount.roundToInt())} • ${report.paymentMethod}",
                                style = MaterialTheme.typography.titleMedium.copy(fontFamily = JetBrainsMonoFontFamily, fontWeight = FontWeight.Bold),
                                color = colors.danger
                            )
                            StatusPill(text = report.incidentDate, color = colors.accentPurple)
                        }
                        Spacer(Modifier.height(4.dp))
                        Text("Ref/UTR: ${report.referenceId.ifBlank { "Not provided" }}", style = MaterialTheme.typography.labelMedium, color = colors.textSecondary)
                        Text(report.description, style = MaterialTheme.typography.bodySmall, color = colors.textPrimary)
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            OutlinedButton(
                                onClick = {
                                    copyToClipboard(context, "Fraud Incident Summary", formattedSummary)
                                    reportFeedback = "Incident report copied to clipboard."
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Copy")
                            }
                            OutlinedButton(
                                onClick = {
                                    val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_TEXT, formattedSummary)
                                    }
                                    context.startActivity(Intent.createChooser(sendIntent, "Share incident summary"))
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Share")
                            }
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(12.dp)) }
        }
    }

    if (showReportDialog) {
        IncidentReportDialog(
            onDismiss = { showReportDialog = false },
            onSave = { date, time, amt, refId, method, desc, evidence ->
                onSaveIncidentReport(date, time, amt, refId, method, desc, evidence)
                showReportDialog = false
            }
        )
    }
}

@Composable
private fun IncidentReportDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, Double, String, String, String, String?) -> Unit
) {
    val today = remember { SimpleDateFormat("dd/MM/yyyy", Locale.US).format(Date()) }
    val nowTime = remember { SimpleDateFormat("hh:mm a", Locale.US).format(Date()) }

    var date by remember { mutableStateOf(today) }
    var time by remember { mutableStateOf(nowTime) }
    var amountStr by remember { mutableStateOf("") }
    var referenceId by remember { mutableStateOf("") }
    var method by remember { mutableStateOf("UPI") }
    var description by remember { mutableStateOf("") }
    var evidence by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    val methods = listOf("UPI", "Bank Card", "Net Banking", "QR / Link", "Other")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Prepare Incident Report") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("Date") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = time, onValueChange = { time = it }, label = { Text("Time") }, modifier = Modifier.weight(1f))
                }
                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it },
                    label = { Text("Amount Lost (₹)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = referenceId,
                    onValueChange = { referenceId = it },
                    label = { Text("UTR / Transaction Reference ID") },
                    modifier = Modifier.fillMaxWidth()
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(methods) { m ->
                        FilterChip(selected = method == m, onClick = { method = m }, label = { Text(m) })
                    }
                }
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("What happened? (caller/app/link details)") },
                    minLines = 2,
                    maxLines = 4,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = evidence,
                    onValueChange = { evidence = it },
                    label = { Text("Evidence notes (SMS/call screenshots)") },
                    modifier = Modifier.fillMaxWidth()
                )
                if (error != null) Text(error.orEmpty(), color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }
        },
        confirmButton = {
            Button(onClick = {
                val amt = amountStr.toDoubleOrNull()
                if (amt == null || amt < 0.0 || description.isBlank()) {
                    error = "Enter the amount and a brief description of what happened."
                    return@Button
                }
                onSave(date, time, amt, referenceId, method, description, evidence.ifBlank { null })
            }) {
                Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Save Summary")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
