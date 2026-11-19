package com.example.ui.screens

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.FinSmartStrings
import com.example.data.local.UserSettingsState
import com.example.data.model.AppLanguage
import com.example.data.model.ChatMessage
import com.example.ui.theme.FinSmartCard
import com.example.ui.theme.FinSmartDimens
import com.example.ui.theme.IconBadge
import com.example.ui.theme.LocalFinSmartColors

@Composable
fun AiSahayakScreen(
    messages: List<ChatMessage>,
    streamingText: String?,
    isLoading: Boolean,
    errorMessage: String?,
    settings: UserSettingsState,
    isSpeaking: Boolean,
    onSelectLanguage: (AppLanguage) -> Unit,
    onSendMessage: (String, Boolean) -> Unit,
    onSpeakText: (String) -> Unit,
    onStopSpeaking: () -> Unit,
    onDismissError: () -> Unit
) {
    val colors = LocalFinSmartColors.current
    val strings = FinSmartStrings.get(settings.language)
    val context = LocalContext.current
    val listState = rememberLazyListState()

    var inputText by remember { mutableStateOf("") }
    var detailedMode by remember { mutableStateOf(false) }
    var speechFallbackNotice by remember { mutableStateOf<String?>(null) }
    val messageFeedback = remember { mutableStateMapOf<String, Boolean>() }

    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val matches = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val spoken = matches?.firstOrNull().orEmpty()
            if (spoken.isNotBlank()) {
                inputText = spoken
            }
        }
    }

    LaunchedEffect(messages.size, streamingText) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem((messages.size - 1).coerceAtLeast(0))
        }
    }

    val suggestedQuestions = remember(settings.language) {
        when (settings.language) {
            AppLanguage.ENGLISH -> listOf(
                "What is UPI?",
                "How to avoid fraud?",
                "What is an IFSC code?",
                "What is PMJDY?",
                "What if I shared my OTP?"
            )
            AppLanguage.KANNADA -> listOf(
                "UPI ಎಂದರೇನು?",
                "ವಂಚನೆಯಿಂದ ತಪ್ಪಿಸಿಕೊಳ್ಳುವುದು ಹೇಗೆ?",
                "IFSC ಕೋಡ್ ಎಂದರೇನು?",
                "PMJDY ಯೋಜನೆ ಎಂದರೇನು?"
            )
            AppLanguage.HINDI -> listOf(
                "UPI क्या है?",
                "धोखाधड़ी से कैसे बचें?",
                "IFSC कोड क्या होता है?",
                "PMJDY क्या है?"
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 720.dp)
                .fillMaxSize()
        ) {
            // Header + Language Pills
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.surface)
                    .padding(horizontal = FinSmartDimens.ScreenPadding, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconBadge(
                        icon = Icons.Default.SmartToy,
                        tint = colors.accentPurple,
                        backgroundColor = colors.lavenderTint,
                        size = 44.dp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = strings.aiTitle,
                            style = MaterialTheme.typography.titleLarge,
                            color = colors.textPrimary
                        )
                        Text(
                            text = strings.aiSubtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textSecondary
                        )
                    }
                    FilterChip(
                        selected = detailedMode,
                        onClick = { detailedMode = !detailedMode },
                        label = { Text(if (detailedMode) "Detailed" else "Short") }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AppLanguage.entries.forEach { lang ->
                        val selected = settings.language == lang
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = if (selected) colors.primary else colors.secondaryBackground,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(50))
                                .clickable { onSelectLanguage(lang) }
                                .testTag("ai_lang_${lang.code}")
                        ) {
                            Text(
                                text = lang.nativeLabel,
                                style = MaterialTheme.typography.labelLarge,
                                color = if (selected) {
                                    if (colors.isDarkNeon) Color(0xFF041D14) else Color.White
                                } else colors.textPrimary,
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }

            // Safety Pledge Strip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.mintTint.copy(alpha = 0.65f))
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Security, contentDescription = null, tint = colors.primary, modifier = Modifier.size(15.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Safety Pledge: FinSmart AI never asks for OTP, UPI PIN, CVV, or passwords.",
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textPrimary
                )
            }

            // Chat Messages
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(messages, key = { it.id }) { msg ->
                    ChatBubble(
                        message = msg,
                        feedback = messageFeedback[msg.id],
                        isSpeaking = isSpeaking,
                        onSpeak = { onSpeakText(msg.text) },
                        onStopSpeak = onStopSpeaking,
                        onFeedback = { helpful -> messageFeedback[msg.id] = helpful }
                    )
                }

                if (isLoading || !streamingText.isNullOrEmpty()) {
                    item {
                        FinSmartCard(
                            containerColor = colors.surface,
                            accentBorderColor = colors.accentPurple
                        ) {
                            if (streamingText.isNullOrBlank()) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = colors.accentPurple)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text("AI Sahayak is thinking...", style = MaterialTheme.typography.bodyMedium, color = colors.textSecondary)
                                }
                            } else {
                                Text(streamingText, style = MaterialTheme.typography.bodyMedium, color = colors.textPrimary)
                            }
                        }
                    }
                }

                if (errorMessage != null) {
                    item {
                        FinSmartCard(
                            containerColor = colors.coralTint,
                            accentBorderColor = colors.danger,
                            onClick = onDismissError
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = colors.danger)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "$errorMessage (Tap to dismiss)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = colors.danger
                                )
                            }
                        }
                    }
                }

                if (speechFallbackNotice != null) {
                    item {
                        FinSmartCard(containerColor = colors.amberTint, onClick = { speechFallbackNotice = null }) {
                            Text(speechFallbackNotice.orEmpty(), style = MaterialTheme.typography.bodySmall, color = colors.textPrimary)
                        }
                    }
                }
            }

            // Suggested Question Chips
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(suggestedQuestions) { q ->
                    SuggestionChip(
                        onClick = { onSendMessage(q, detailedMode) },
                        label = { Text(q, style = MaterialTheme.typography.labelMedium) }
                    )
                }
            }

            // Input Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.surface)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text(strings.aiPlaceholder) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("ai_input_field"),
                    shape = RoundedCornerShape(24.dp),
                    maxLines = 3
                )
                Spacer(modifier = Modifier.width(6.dp))
                IconButton(
                    onClick = {
                        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "${settings.language.code}-IN")
                            putExtra(RecognizerIntent.EXTRA_PROMPT, strings.aiPlaceholder)
                        }
                        if (intent.resolveActivity(context.packageManager) != null) {
                            speechLauncher.launch(intent)
                        } else {
                            speechFallbackNotice = "Speech-to-text service is unavailable on this device. You can type your question directly."
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(colors.secondaryBackground)
                ) {
                    Icon(Icons.Default.Mic, contentDescription = "Voice input", tint = colors.textPrimary)
                }
                Spacer(modifier = Modifier.width(6.dp))
                IconButton(
                    onClick = {
                        val text = inputText
                        inputText = ""
                        onSendMessage(text, detailedMode)
                    },
                    enabled = inputText.isNotBlank() && !isLoading,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(listOf(colors.primary, colors.secondary))
                        )
                        .testTag("ai_send_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send question",
                        tint = if (colors.isDarkNeon) Color(0xFF041D14) else Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun ChatBubble(
    message: ChatMessage,
    feedback: Boolean?,
    isSpeaking: Boolean,
    onSpeak: () -> Unit,
    onStopSpeak: () -> Unit,
    onFeedback: (Boolean) -> Unit
) {
    val colors = LocalFinSmartColors.current
    if (message.isFromUser) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Box(
                modifier = Modifier
                    .widthIn(max = 310.dp)
                    .clip(RoundedCornerShape(18.dp, 18.dp, 4.dp, 18.dp))
                    .background(
                        Brush.horizontalGradient(listOf(colors.accentPurple, colors.accentBlue))
                    )
                    .padding(14.dp)
            ) {
                Column {
                    Text(message.text, style = MaterialTheme.typography.bodyMedium, color = Color.White)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = message.timestampLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.78f),
                        modifier = Modifier.align(Alignment.End)
                    )
                }
            }
        }
    } else {
        FinSmartCard(
            modifier = Modifier.widthIn(max = 340.dp),
            containerColor = if (message.isSafetyWarning) colors.coralTint else colors.surface,
            accentBorderColor = if (message.isSafetyWarning) colors.danger else colors.accentPurple
        ) {
            Text(
                text = message.text,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = message.timestampLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textSecondary
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { onFeedback(true) }, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = Icons.Default.ThumbUp,
                            contentDescription = "Helpful",
                            tint = if (feedback == true) colors.success else colors.textSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    IconButton(onClick = { onFeedback(false) }, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = Icons.Default.ThumbDown,
                            contentDescription = "Not helpful",
                            tint = if (feedback == false) colors.danger else colors.textSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    IconButton(
                        onClick = { if (isSpeaking) onStopSpeak() else onSpeak() },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (isSpeaking) Icons.Default.Stop else Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Read answer aloud",
                            tint = colors.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
