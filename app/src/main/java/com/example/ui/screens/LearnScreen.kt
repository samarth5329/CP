package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.local.FinSmartContentCatalog
import com.example.data.local.FinSmartStrings
import com.example.data.local.UserSettingsState
import com.example.data.model.LearningModuleId
import com.example.data.model.LessonItem
import com.example.data.model.QuizResultRecord
import com.example.data.model.UserProfile
import com.example.ui.theme.FinSmartCard
import com.example.ui.theme.FinSmartDimens
import com.example.ui.theme.GradientActionButton
import com.example.ui.theme.IconBadge
import com.example.ui.theme.LocalFinSmartColors
import com.example.ui.theme.SectionHeader
import com.example.ui.theme.StatusPill
import kotlin.math.roundToInt

private enum class LearnSubView {
    OVERVIEW, MODULE_LESSONS, QUIZ, CERTIFICATES
}

@Composable
fun LearnScreen(
    profile: UserProfile?,
    quizResults: List<QuizResultRecord>,
    settings: UserSettingsState,
    initialModule: LearningModuleId? = null,
    isSpeaking: Boolean,
    onReadText: (String) -> Unit,
    onStopTts: () -> Unit,
    onCompleteLesson: (String) -> Unit,
    onSubmitQuiz: (String, String, Int, Int) -> Unit,
    onToggleOfflineModule: (String) -> Unit,
    onOpenFraudSimulator: () -> Unit
) {
    val colors = LocalFinSmartColors.current
    val strings = FinSmartStrings.get(settings.language)
    val completedLessons = profile?.completedLessonIds.orEmpty().toSet()
    val totalLessons = FinSmartContentCatalog.lessons.size
    val progressRatio = if (totalLessons > 0) completedLessons.size.toFloat() / totalLessons else 0f
    val progressPercent = (progressRatio * 100).roundToInt()

    var subView by remember(initialModule) {
        mutableStateOf(if (initialModule != null) LearnSubView.MODULE_LESSONS else LearnSubView.OVERVIEW)
    }
    var selectedModule by remember(initialModule) {
        mutableStateOf(initialModule ?: LearningModuleId.BASIC_BANKING)
    }
    var selectedLesson by remember { mutableStateOf<LessonItem?>(null) }
    var selectedPathFilter by remember { mutableStateOf("All") }

    if (selectedLesson != null) {
        BackHandler {
            onStopTts()
            selectedLesson = null
        }
        LessonDetailView(
            lesson = selectedLesson!!,
            isCompleted = selectedLesson!!.id in completedLessons,
            settings = settings,
            isSpeaking = isSpeaking,
            onReadText = onReadText,
            onStopTts = onStopTts,
            onComplete = {
                onCompleteLesson(selectedLesson!!.id)
                selectedLesson = null
            },
            onBack = {
                onStopTts()
                selectedLesson = null
            }
        )
        return
    }

    if (subView != LearnSubView.OVERVIEW) {
        BackHandler { subView = LearnSubView.OVERVIEW }
    }

    when (subView) {
        LearnSubView.MODULE_LESSONS -> {
            ModuleLessonsView(
                moduleId = selectedModule,
                completedLessons = completedLessons,
                settings = settings,
                isOfflineSaved = selectedModule.name in settings.offlineSavedModuleIds,
                onToggleOffline = { onToggleOfflineModule(selectedModule.name) },
                onSelectLesson = { selectedLesson = it },
                onBack = { subView = LearnSubView.OVERVIEW }
            )
        }
        LearnSubView.QUIZ -> {
            KnowledgeQuizView(
                quizResults = quizResults,
                onSubmitQuiz = onSubmitQuiz,
                onBack = { subView = LearnSubView.OVERVIEW }
            )
        }
        LearnSubView.CERTIFICATES -> {
            CertificatesAndBadgesView(
                completedLessonCount = completedLessons.size,
                completedQuizCount = profile?.completedQuizIds?.size ?: 0,
                earnedBadgeIds = profile?.earnedBadgeIds.orEmpty().toSet(),
                displayName = profile?.displayName ?: "FinSmart Learner",
                onBack = { subView = LearnSubView.OVERVIEW }
            )
        }
        LearnSubView.OVERVIEW -> {
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
                            text = strings.learnTitle,
                            style = MaterialTheme.typography.headlineMedium,
                            color = colors.textPrimary
                        )
                        Text(
                            text = strings.learnSubtitle,
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.textSecondary
                        )
                    }

                    // Progress Card
                    item {
                        FinSmartCard(
                            containerColor = colors.surface,
                            accentBorderColor = colors.primary
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(64.dp)) {
                                    CircularProgressIndicator(
                                        progress = { progressRatio },
                                        modifier = Modifier.size(64.dp),
                                        color = colors.primary,
                                        trackColor = colors.primary.copy(alpha = 0.16f),
                                        strokeWidth = 6.dp
                                    )
                                    Text(
                                        text = "$progressPercent%",
                                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                        color = colors.textPrimary
                                    )
                                }
                                Spacer(modifier = Modifier.width(14.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Your Learning Progress",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = colors.textPrimary
                                    )
                                    Text(
                                        text = "${completedLessons.size} / $totalLessons Lessons Done",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = colors.textSecondary
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        StatusPill(
                                            text = "${profile?.streakDays ?: 1}d Streak",
                                            color = colors.warning,
                                            icon = Icons.Default.LocalFireDepartment
                                        )
                                        StatusPill(
                                            text = "${profile?.earnedBadgeIds?.size ?: 0} Badges",
                                            color = colors.accentPurple,
                                            icon = Icons.Default.EmojiEvents
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Learning Path Filter Chips
                    item {
                        SectionHeader(title = "4 Core Learning Modules")
                        Spacer(modifier = Modifier.height(8.dp))
                        val paths = listOf("All", "Banking for Beginners", "Avoiding Digital Fraud", "Money Management")
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(paths) { path ->
                                FilterChip(
                                    selected = selectedPathFilter == path,
                                    onClick = { selectedPathFilter = path },
                                    label = { Text(path) }
                                )
                            }
                        }
                    }

                    val filteredModules = LearningModuleId.entries.filter { mod ->
                        selectedPathFilter == "All" || mod.learningPath == selectedPathFilter
                    }

                    items(filteredModules) { mod ->
                        val modLessons = FinSmartContentCatalog.lessons.filter { it.moduleId == mod }
                        val doneCount = modLessons.count { it.id in completedLessons }
                        val (icon, tint, bg) = moduleVisuals(mod, colors)
                        FinSmartCard(
                            containerColor = bg,
                            accentBorderColor = tint,
                            modifier = Modifier.testTag("module_card_${mod.name.lowercase()}"),
                            onClick = {
                                selectedModule = mod
                                subView = LearnSubView.MODULE_LESSONS
                            }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconBadge(icon = icon, tint = tint, backgroundColor = tint.copy(alpha = 0.18f), size = 48.dp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = mod.title,
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = colors.textPrimary,
                                            modifier = Modifier.weight(1f)
                                        )
                                        if (mod.name in settings.offlineSavedModuleIds) {
                                            Icon(
                                                imageVector = Icons.Default.DownloadDone,
                                                contentDescription = "Available offline",
                                                tint = colors.success,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = mod.subtitle,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = colors.textSecondary
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    LinearProgressIndicator(
                                        progress = { if (modLessons.isNotEmpty()) doneCount.toFloat() / modLessons.size else 0f },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(6.dp)
                                            .clip(RoundedCornerShape(50)),
                                        color = tint,
                                        trackColor = colors.surface
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "$doneCount / ${modLessons.size} Lessons",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = colors.textSecondary
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Open ${mod.title}",
                                    tint = tint
                                )
                            }
                        }
                    }

                    // Quizzes, Simulator, Certificates
                    item {
                        SectionHeader(title = "Practice & Achievements")
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            FinSmartCard(
                                modifier = Modifier.weight(1f).testTag("open_quiz_card"),
                                containerColor = colors.lavenderTint,
                                accentBorderColor = colors.accentPurple,
                                onClick = { subView = LearnSubView.QUIZ }
                            ) {
                                IconBadge(icon = Icons.Default.Quiz, tint = colors.accentPurple, backgroundColor = colors.accentPurple.copy(alpha = 0.18f))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Knowledge Quiz", style = MaterialTheme.typography.titleSmall, color = colors.textPrimary)
                                Text("Test your banking & scam skills", style = MaterialTheme.typography.bodySmall, color = colors.textSecondary)
                            }
                            FinSmartCard(
                                modifier = Modifier.weight(1f).testTag("open_certificates_card"),
                                containerColor = colors.amberTint,
                                accentBorderColor = colors.warning,
                                onClick = { subView = LearnSubView.CERTIFICATES }
                            ) {
                                IconBadge(icon = Icons.Default.WorkspacePremium, tint = colors.warning, backgroundColor = colors.warning.copy(alpha = 0.18f))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Badges & Certs", style = MaterialTheme.typography.titleSmall, color = colors.textPrimary)
                                Text("${profile?.earnedBadgeIds?.size ?: 0} unlocked", style = MaterialTheme.typography.bodySmall, color = colors.textSecondary)
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        FinSmartCard(
                            containerColor = colors.coralTint,
                            accentBorderColor = colors.danger,
                            onClick = onOpenFraudSimulator
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconBadge(icon = Icons.Default.Shield, tint = colors.danger, backgroundColor = colors.danger.copy(alpha = 0.16f))
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Daily Scam Scenarios & Simulator", style = MaterialTheme.typography.titleMedium, color = colors.textPrimary)
                                    Text("Practice real calls, QR codes & fake KYC traps safely", style = MaterialTheme.typography.bodySmall, color = colors.textSecondary)
                                }
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = colors.danger)
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(12.dp)) }
                }
            }
        }
    }
}

@Composable
private fun ModuleLessonsView(
    moduleId: LearningModuleId,
    completedLessons: Set<String>,
    settings: UserSettingsState,
    isOfflineSaved: Boolean,
    onToggleOffline: () -> Unit,
    onSelectLesson: (LessonItem) -> Unit,
    onBack: () -> Unit
) {
    val colors = LocalFinSmartColors.current
    val lessons = remember(moduleId) { FinSmartContentCatalog.lessons.filter { it.moduleId == moduleId } }
    val (icon, tint, _) = moduleVisuals(moduleId, colors)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(FinSmartDimens.ScreenPadding)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = colors.textPrimary)
            }
            IconBadge(icon = icon, tint = tint, backgroundColor = tint.copy(alpha = 0.16f), size = 38.dp)
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(moduleId.title, style = MaterialTheme.typography.titleLarge, color = colors.textPrimary)
                Text(moduleId.subtitle, style = MaterialTheme.typography.bodySmall, color = colors.textSecondary)
            }
            FilterChip(
                selected = isOfflineSaved,
                onClick = onToggleOffline,
                label = { Text(if (isOfflineSaved) "Saved Offline" else "Save Offline") }
            )
        }
        Spacer(modifier = Modifier.height(14.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(lessons) { lesson ->
                val done = lesson.id in completedLessons
                FinSmartCard(
                    containerColor = if (done) colors.mintTint.copy(alpha = 0.55f) else colors.surface,
                    accentBorderColor = if (done) colors.success else tint,
                    onClick = { onSelectLesson(lesson) }
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconBadge(
                            icon = if (done) Icons.Default.CheckCircle else icon,
                            tint = if (done) colors.success else tint,
                            backgroundColor = (if (done) colors.success else tint).copy(alpha = 0.15f),
                            size = 40.dp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = lesson.localizedTitle(settings.language),
                                style = MaterialTheme.typography.titleMedium,
                                color = colors.textPrimary
                            )
                            Text(
                                text = "${lesson.durationMinutes} min • ${lesson.summaryEn}",
                                style = MaterialTheme.typography.bodySmall,
                                color = colors.textSecondary
                            )
                        }
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = colors.textSecondary)
                    }
                }
            }
        }
    }
}

@Composable
private fun LessonDetailView(
    lesson: LessonItem,
    isCompleted: Boolean,
    settings: UserSettingsState,
    isSpeaking: Boolean,
    onReadText: (String) -> Unit,
    onStopTts: () -> Unit,
    onComplete: () -> Unit,
    onBack: () -> Unit
) {
    val colors = LocalFinSmartColors.current
    val title = lesson.localizedTitle(settings.language)
    val body = lesson.localizedContent(settings.language)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(FinSmartDimens.ScreenPadding)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = colors.textPrimary)
            }
            Text(
                text = lesson.moduleId.title,
                style = MaterialTheme.typography.titleMedium,
                color = colors.textSecondary,
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = {
                    if (isSpeaking) onStopTts() else onReadText("$title. $body")
                },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(colors.primary)
            ) {
                Icon(
                    imageVector = if (isSpeaking) Icons.Default.Stop else Icons.Default.PlayArrow,
                    contentDescription = if (isSpeaking) "Stop audio" else "Read lesson aloud",
                    tint = if (colors.isDarkNeon) Color(0xFF041C14) else Color.White
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Text(title, style = MaterialTheme.typography.headlineMedium, color = colors.textPrimary)
                Spacer(modifier = Modifier.height(4.dp))
                StatusPill(text = "${lesson.durationMinutes} min read • Offline ready", color = colors.secondary)
            }
            item {
                FinSmartCard(containerColor = colors.surface) {
                    Text(
                        text = body,
                        style = MaterialTheme.typography.bodyLarge,
                        color = colors.textPrimary
                    )
                }
            }
            item {
                FinSmartCard(containerColor = colors.mintTint, accentBorderColor = colors.success) {
                    Text("Key Takeaway", style = MaterialTheme.typography.titleSmall, color = colors.success)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(lesson.keyTakeawayEn, style = MaterialTheme.typography.bodyMedium, color = colors.textPrimary)
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        GradientActionButton(
            text = if (isCompleted) "Completed — Review Again" else "Mark Lesson Complete",
            icon = Icons.Default.CheckCircle,
            onClick = onComplete
        )
    }
}

@Composable
private fun KnowledgeQuizView(
    quizResults: List<QuizResultRecord>,
    onSubmitQuiz: (String, String, Int, Int) -> Unit,
    onBack: () -> Unit
) {
    val colors = LocalFinSmartColors.current
    val questions = FinSmartContentCatalog.quizzes
    var currentIndex by remember { mutableIntStateOf(0) }
    var selectedOption by remember { mutableStateOf<Int?>(null) }
    var answered by remember { mutableStateOf(false) }
    var score by remember { mutableIntStateOf(0) }
    var finished by remember { mutableStateOf(false) }

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
            Text("Financial Safety Quiz", style = MaterialTheme.typography.titleLarge, color = colors.textPrimary)
        }
        Spacer(modifier = Modifier.height(12.dp))

        if (finished) {
            FinSmartCard(containerColor = colors.mintTint, accentBorderColor = colors.success) {
                Text("Quiz Completed!", style = MaterialTheme.typography.headlineSmall, color = colors.textPrimary)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Your Score: $score / ${questions.size}",
                    style = MaterialTheme.typography.titleLarge,
                    color = colors.primary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (score >= 5) "Excellent awareness! Keep applying these habits daily." else "Good effort! Review the Fraud & UPI lessons to raise your score.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textSecondary
                )
                Spacer(modifier = Modifier.height(12.dp))
                GradientActionButton(
                    text = "Retake Quiz",
                    onClick = {
                        currentIndex = 0
                        selectedOption = null
                        answered = false
                        score = 0
                        finished = false
                    }
                )
            }
            if (quizResults.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                SectionHeader(title = "Recent Quiz History")
                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(quizResults.take(5)) { res ->
                        FinSmartCard {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(res.quizTitle, style = MaterialTheme.typography.titleSmall, color = colors.textPrimary)
                                StatusPill(text = "${res.score}/${res.totalQuestions}", color = colors.primary)
                            }
                        }
                    }
                }
            }
        } else {
            val q = questions[currentIndex]
            Text(
                text = "Question ${currentIndex + 1} of ${questions.size}",
                style = MaterialTheme.typography.labelLarge,
                color = colors.secondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            FinSmartCard(containerColor = colors.surface, accentBorderColor = colors.accentPurple) {
                Text(q.question, style = MaterialTheme.typography.titleMedium, color = colors.textPrimary)
                Spacer(modifier = Modifier.height(12.dp))
                q.options.forEachIndexed { idx, opt ->
                    val isRight = idx == q.correctIndex
                    val bg = when {
                        !answered && selectedOption == idx -> colors.softBlueTint
                        answered && isRight -> colors.mintTint
                        answered && selectedOption == idx && !isRight -> colors.coralTint
                        else -> colors.secondaryBackground
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(bg)
                            .clickable(enabled = !answered) { selectedOption = idx }
                            .padding(horizontal = 10.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedOption == idx,
                            onClick = { if (!answered) selectedOption = idx }
                        )
                        Text(opt, style = MaterialTheme.typography.bodyMedium, color = colors.textPrimary)
                    }
                }
                if (answered) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = if (selectedOption == q.correctIndex) "Correct! ${q.explanation}" else "Careful: ${q.explanation}",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (selectedOption == q.correctIndex) colors.success else colors.danger
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            GradientActionButton(
                text = if (!answered) "Check Answer" else if (currentIndex == questions.lastIndex) "Finish Quiz" else "Next Question",
                enabled = selectedOption != null,
                onClick = {
                    if (!answered) {
                        answered = true
                        if (selectedOption == q.correctIndex) score++
                    } else if (currentIndex < questions.lastIndex) {
                        currentIndex++
                        selectedOption = null
                        answered = false
                    } else {
                        finished = true
                        onSubmitQuiz("finsmart_master_quiz", "FinSmart Safety Quiz", score, questions.size)
                    }
                }
            )
        }
    }
}

@Composable
private fun CertificatesAndBadgesView(
    completedLessonCount: Int,
    completedQuizCount: Int,
    earnedBadgeIds: Set<String>,
    displayName: String,
    onBack: () -> Unit
) {
    val colors = LocalFinSmartColors.current
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
            Text("Badges & Certificates", style = MaterialTheme.typography.titleLarge, color = colors.textPrimary)
        }
        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                val eligibleForCert = completedLessonCount >= 6 && completedQuizCount >= 1
                FinSmartCard(
                    containerColor = if (eligibleForCert) colors.mintTint else colors.amberTint,
                    accentBorderColor = if (eligibleForCert) colors.success else colors.warning
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconBadge(
                            icon = Icons.Default.WorkspacePremium,
                            tint = if (eligibleForCert) colors.success else colors.warning,
                            backgroundColor = colors.surface
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("FinSmart Digital Safety Certificate", style = MaterialTheme.typography.titleMedium, color = colors.textPrimary)
                            Text(
                                text = if (eligibleForCert) "Awarded to $displayName for completing $completedLessonCount lessons and $completedQuizCount quiz!"
                                else "Complete at least 6 lessons and 1 Safety Quiz to unlock your certificate ($completedLessonCount/6 lessons).",
                                style = MaterialTheme.typography.bodySmall,
                                color = colors.textSecondary
                            )
                        }
                    }
                }
            }
            items(FinSmartContentCatalog.badges) { badge ->
                val unlocked = badge.id in earnedBadgeIds || (completedLessonCount >= badge.requiredLessons && completedQuizCount >= badge.requiredQuizzes && badge.id != "scam_fighter")
                FinSmartCard(
                    containerColor = if (unlocked) colors.surface else colors.secondaryBackground,
                    accentBorderColor = if (unlocked) colors.primary else null
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconBadge(
                            icon = Icons.Default.EmojiEvents,
                            tint = if (unlocked) colors.warning else colors.textSecondary,
                            backgroundColor = if (unlocked) colors.amberTint else colors.surface
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(badge.title, style = MaterialTheme.typography.titleSmall, color = colors.textPrimary)
                            Text(badge.description, style = MaterialTheme.typography.bodySmall, color = colors.textSecondary)
                        }
                        StatusPill(
                            text = if (unlocked) "Unlocked" else "Locked",
                            color = if (unlocked) colors.success else colors.textSecondary
                        )
                    }
                }
            }
        }
    }
}

private fun moduleVisuals(
    moduleId: LearningModuleId,
    colors: com.example.ui.theme.FinSmartColors
): Triple<ImageVector, Color, Color> = when (moduleId) {
    LearningModuleId.BASIC_BANKING -> Triple(Icons.Default.AccountBalance, colors.accentBlue, colors.softBlueTint)
    LearningModuleId.SAVINGS_PLANNING -> Triple(Icons.Default.Savings, colors.accentAmber, colors.amberTint)
    LearningModuleId.DIGITAL_UPI -> Triple(Icons.Default.QrCode2, colors.secondary, colors.cyanTint)
    LearningModuleId.FRAUD_SAFETY -> Triple(Icons.Default.Shield, colors.danger, colors.coralTint)
}
