package com.example.ui.viewmodel

import android.util.Log
import com.example.core.security.LinkAnalysisResult
import com.example.core.security.MessageAnalysisResult
import com.example.core.security.ScamAnalyzer
import com.example.data.local.FinSmartContentCatalog
import com.example.data.local.PreferencesManager
import com.example.data.local.UserSettingsState
import com.example.data.model.AppLanguage
import com.example.data.model.ChatMessage
import com.example.data.model.ExpenseCategory
import com.example.data.model.ExpenseRecord
import com.example.data.model.IncidentReport
import com.example.data.model.MonthlyBudget
import com.example.data.model.QuizResultRecord
import com.example.data.model.SavingsGoal
import com.example.data.model.ScanHistoryItem
import com.example.data.model.TrustedContact
import com.example.data.model.UserProfile
import com.example.data.repository.AiSahayakRepository
import com.example.data.repository.AiStreamChunk
import com.example.data.repository.FinSmartFirestoreRepository
import com.example.ui.theme.ThemeMode
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

data class MonthlyMoneySummary(
    val monthKey: String,
    val monthLabel: String,
    val income: Double,
    val totalExpenses: Double,
    val remaining: Double,
    val savingsPercent: Double,
    val categoryTotals: Map<ExpenseCategory, Double>,
    val insights: List<String>
)

class FinSmartMainViewModel(
    private val firestoreRepo: FinSmartFirestoreRepository,
    private val aiRepo: AiSahayakRepository,
    private val preferencesManager: PreferencesManager,
    val currentUserId: String,
    private val initialDisplayName: String
) : ViewModel() {

    private companion object {
        const val TAG = "FinSmartMainVM"
        const val STOP_TIMEOUT_MS = 5000L
    }

    val settings: StateFlow<UserSettingsState> = preferencesManager.settingsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), UserSettingsState())

    val profileState: StateFlow<UiState<UserProfile?>> = firestoreRepo.observeUserProfile(currentUserId)
        .toSafeUiState("Failed to load profile")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), UiState.Loading)

    val expenses: StateFlow<List<ExpenseRecord>> = firestoreRepo.observeExpenses(currentUserId)
        .safeListFlow("expenses")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), emptyList())

    val budgets: StateFlow<List<MonthlyBudget>> = firestoreRepo.observeAllBudgets(currentUserId)
        .safeListFlow("budgets")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), emptyList())

    val goals: StateFlow<List<SavingsGoal>> = firestoreRepo.observeGoals(currentUserId)
        .safeListFlow("goals")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), emptyList())

    val scanHistory: StateFlow<List<ScanHistoryItem>> = firestoreRepo.observeScanHistory(currentUserId)
        .safeListFlow("scanHistory")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), emptyList())

    val trustedContacts: StateFlow<List<TrustedContact>> = firestoreRepo.observeTrustedContacts(currentUserId)
        .safeListFlow("trustedContacts")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), emptyList())

    val incidentReports: StateFlow<List<IncidentReport>> = firestoreRepo.observeIncidentReports(currentUserId)
        .safeListFlow("incidentReports")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), emptyList())

    val quizResults: StateFlow<List<QuizResultRecord>> = firestoreRepo.observeQuizResults(currentUserId)
        .safeListFlow("quizResults")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), emptyList())

    private val _selectedMonthKey = MutableStateFlow(currentMonthKey())
    val selectedMonthKey: StateFlow<String> = _selectedMonthKey.asStateFlow()

    val monthlySummary: StateFlow<MonthlyMoneySummary> = combine(
        _selectedMonthKey,
        expenses,
        budgets,
        goals
    ) { monthKey, allExpenses, allBudgets, allGoals ->
        computeMonthlySummary(monthKey, allExpenses, allBudgets, allGoals)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
        computeMonthlySummary(currentMonthKey(), emptyList(), emptyList(), emptyList())
    )

    // Scam Shield live results
    private val _latestLinkResult = MutableStateFlow<LinkAnalysisResult?>(null)
    val latestLinkResult: StateFlow<LinkAnalysisResult?> = _latestLinkResult.asStateFlow()

    private val _latestMessageResult = MutableStateFlow<MessageAnalysisResult?>(null)
    val latestMessageResult: StateFlow<MessageAnalysisResult?> = _latestMessageResult.asStateFlow()

    private val _latestScanDocId = MutableStateFlow<String?>(null)
    val latestScanDocId: StateFlow<String?> = _latestScanDocId.asStateFlow()

    // AI Sahayak chat state
    private val _aiMessages = MutableStateFlow(
        listOf(
            ChatMessage(
                id = "welcome_ai",
                text = "Namaste! I am FinSmart AI Sahayak. Ask me anything about bank accounts, UPI safety, savings, government schemes, or checking suspicious messages.\n\nSafety pledge: I will never ask for your OTP, UPI PIN, CVV, or bank password.",
                isFromUser = false,
                timestampLabel = currentTimeLabel()
            )
        )
    )
    val aiMessages: StateFlow<List<ChatMessage>> = _aiMessages.asStateFlow()

    private val _streamingAiText = MutableStateFlow<String?>(null)
    val streamingAiText: StateFlow<String?> = _streamingAiText.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    private val _aiError = MutableStateFlow<String?>(null)
    val aiError: StateFlow<String?> = _aiError.asStateFlow()

    private val _statusBanner = MutableStateFlow<String?>(null)
    val statusBanner: StateFlow<String?> = _statusBanner.asStateFlow()

    init {
        viewModelScope.launch {
            val currentSettings = settings.value
            firestoreRepo.ensureProfileExists(
                userId = currentUserId,
                displayName = initialDisplayName,
                language = currentSettings.language.name,
                theme = currentSettings.themeMode.name,
                simpleMode = currentSettings.simpleMode
            )
        }
    }

    fun clearStatusBanner() {
        _statusBanner.value = null
    }

    fun clearAiError() {
        _aiError.value = null
    }

    fun shiftSelectedMonth(deltaMonths: Int) {
        val parts = _selectedMonthKey.value.split("-")
        val year = parts.getOrNull(0)?.toIntOrNull() ?: 2026
        val month = (parts.getOrNull(1)?.toIntOrNull() ?: 11) - 1
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, 1)
            add(Calendar.MONTH, deltaMonths)
        }
        _selectedMonthKey.value = String.format(
            Locale.US,
            "%04d-%02d",
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH) + 1
        )
    }

    fun completeLesson(lessonId: String) {
        val current = (profileState.value as? UiState.Success)?.data
        val existingLessons = current?.completedLessonIds.orEmpty()
        val updatedLessons = (existingLessons + lessonId).distinct()
        val existingQuizzes = current?.completedQuizIds.orEmpty()
        val newBadges = computeEarnedBadges(updatedLessons.size, existingQuizzes.size, scanHistory.value.isNotEmpty())
        val newScore = computeSafetyScore(
            lessonCount = updatedLessons.size,
            quizCount = existingQuizzes.size,
            hasScans = scanHistory.value.isNotEmpty(),
            hasTrustedContact = trustedContacts.value.any { it.consentGranted && !it.revoked }
        )
        val streak = (current?.streakDays ?: 1).coerceAtLeast(1)

        viewModelScope.launch {
            firestoreRepo.markLessonCompleted(
                userId = currentUserId,
                lessonId = lessonId,
                newCompletedLessons = updatedLessons,
                newBadges = newBadges,
                newScore = newScore,
                streakDays = streak
            ).onSuccess {
                _statusBanner.value = "Lesson marked complete! Progress synced."
            }.onFailure {
                _statusBanner.value = "Saved locally; sync failed: ${it.localizedMessage}"
            }
        }
    }

    fun submitQuizScore(quizId: String, quizTitle: String, score: Int, total: Int) {
        val current = (profileState.value as? UiState.Success)?.data
        val existingLessons = current?.completedLessonIds.orEmpty()
        val updatedQuizzes = (current?.completedQuizIds.orEmpty() + quizId).distinct()
        val newBadges = computeEarnedBadges(existingLessons.size, updatedQuizzes.size, scanHistory.value.isNotEmpty())
        val newScore = computeSafetyScore(
            lessonCount = existingLessons.size,
            quizCount = updatedQuizzes.size,
            hasScans = scanHistory.value.isNotEmpty(),
            hasTrustedContact = trustedContacts.value.any { it.consentGranted && !it.revoked }
        )
        viewModelScope.launch {
            firestoreRepo.saveQuizResult(
                userId = currentUserId,
                quizId = quizId,
                quizTitle = quizTitle,
                score = score,
                totalQuestions = total,
                updatedCompletedQuizzes = updatedQuizzes,
                updatedBadges = newBadges,
                updatedSafetyScore = newScore
            ).onFailure {
                _statusBanner.value = "Could not save quiz score: ${it.localizedMessage}"
            }
        }
    }

    fun checkUrlSafety(rawUrl: String) {
        val result = ScamAnalyzer.analyzeUrl(rawUrl)
        _latestLinkResult.value = result
        if (result.normalizedInput.isNotBlank()) {
            viewModelScope.launch {
                firestoreRepo.saveScanRecord(
                    userId = currentUserId,
                    scanType = "LINK",
                    inputPreview = result.host.ifBlank { result.normalizedInput },
                    verdict = result.verdict.name,
                    summary = result.headline,
                    reasons = result.indicators
                ).onSuccess { docId ->
                    _latestScanDocId.value = docId
                }
            }
        }
    }

    fun checkMessageSafety(rawMessage: String, scanType: String = "MESSAGE") {
        val result = ScamAnalyzer.analyzeMessage(rawMessage)
        _latestMessageResult.value = result
        if (result.inputPreview.isNotBlank()) {
            viewModelScope.launch {
                firestoreRepo.saveScanRecord(
                    userId = currentUserId,
                    scanType = scanType,
                    inputPreview = result.inputPreview,
                    verdict = result.verdict.name,
                    summary = result.riskLabel,
                    reasons = result.detectedPhrases.ifEmpty { listOf(result.simpleExplanation) }
                ).onSuccess { docId ->
                    _latestScanDocId.value = docId
                }
            }
        }
    }

    fun checkCallSafety(phoneNumber: String, callerClaim: String) {
        val result = ScamAnalyzer.analyzeCallClaim(phoneNumber, callerClaim)
        _latestMessageResult.value = result
        if (phoneNumber.isNotBlank() || callerClaim.isNotBlank()) {
            viewModelScope.launch {
                firestoreRepo.saveScanRecord(
                    userId = currentUserId,
                    scanType = "CALL",
                    inputPreview = "${phoneNumber.trim()} — ${callerClaim.trim()}".take(140),
                    verdict = result.verdict.name,
                    summary = result.riskLabel,
                    reasons = result.detectedPhrases.ifEmpty { listOf(result.simpleExplanation) }
                ).onSuccess { docId ->
                    _latestScanDocId.value = docId
                }
            }
        }
    }

    fun markLatestScanReported() {
        val id = _latestScanDocId.value ?: return
        viewModelScope.launch {
            firestoreRepo.markScanReported(currentUserId, id)
                .onSuccess { _statusBanner.value = "Flagged in your scan log. If money was lost, call 1930 immediately." }
        }
    }

    fun saveExpense(
        expenseId: String?,
        title: String,
        category: ExpenseCategory,
        amount: Double,
        dateMillis: Long,
        note: String?
    ) {
        val monthKey = SimpleDateFormat("yyyy-MM", Locale.US).format(Date(dateMillis))
        viewModelScope.launch {
            firestoreRepo.upsertExpense(
                userId = currentUserId,
                expenseId = expenseId,
                title = title,
                category = category,
                amount = amount,
                monthKey = monthKey,
                dateMillis = dateMillis,
                note = note
            ).onSuccess {
                _selectedMonthKey.value = monthKey
                _statusBanner.value = if (expenseId == null) "Expense added." else "Expense updated."
            }.onFailure {
                _statusBanner.value = it.localizedMessage ?: "Failed to save expense."
            }
        }
    }

    fun deleteExpense(expenseId: String) {
        viewModelScope.launch {
            firestoreRepo.deleteExpense(currentUserId, expenseId)
                .onSuccess { _statusBanner.value = "Expense removed." }
                .onFailure { _statusBanner.value = it.localizedMessage ?: "Delete failed." }
        }
    }

    fun saveBudget(budget: MonthlyBudget) {
        viewModelScope.launch {
            firestoreRepo.saveMonthlyBudget(currentUserId, budget)
                .onSuccess { _statusBanner.value = "Monthly budget saved." }
                .onFailure { _statusBanner.value = it.localizedMessage ?: "Could not save budget." }
        }
    }

    fun saveGoal(
        goalId: String?,
        title: String,
        category: String,
        targetAmount: Double,
        savedAmount: Double,
        targetDateMillis: Long
    ) {
        viewModelScope.launch {
            firestoreRepo.upsertSavingsGoal(
                userId = currentUserId,
                goalId = goalId,
                title = title,
                category = category,
                targetAmount = targetAmount,
                savedAmount = savedAmount,
                targetDateMillis = targetDateMillis
            ).onSuccess {
                _statusBanner.value = "Savings goal saved."
            }.onFailure {
                _statusBanner.value = it.localizedMessage ?: "Could not save goal."
            }
        }
    }

    fun deleteGoal(goalId: String) {
        viewModelScope.launch {
            firestoreRepo.deleteGoal(currentUserId, goalId)
                .onSuccess { _statusBanner.value = "Goal deleted." }
        }
    }

    fun saveTrustedContact(
        contactId: String?,
        name: String,
        relationship: String,
        phone: String,
        consentGranted: Boolean
    ) {
        viewModelScope.launch {
            firestoreRepo.upsertTrustedContact(
                userId = currentUserId,
                contactId = contactId,
                name = name,
                relationship = relationship,
                phoneNumber = phone,
                consentGranted = consentGranted
            ).onSuccess {
                _statusBanner.value = "Trusted contact saved with your consent."
            }.onFailure {
                _statusBanner.value = it.localizedMessage ?: "Could not save trusted contact."
            }
        }
    }

    fun toggleTrustedContactRevoke(contactId: String, currentlyRevoked: Boolean) {
        viewModelScope.launch {
            firestoreRepo.toggleTrustedContactRevoke(currentUserId, contactId, !currentlyRevoked)
                .onSuccess {
                    _statusBanner.value = if (!currentlyRevoked) "Sharing access paused/revoked." else "Sharing access re-enabled."
                }
        }
    }

    fun deleteTrustedContact(contactId: String) {
        viewModelScope.launch {
            firestoreRepo.deleteTrustedContact(currentUserId, contactId)
                .onSuccess { _statusBanner.value = "Trusted contact removed." }
        }
    }

    fun recordSharedAlertToTrustedContact(contactId: String) {
        viewModelScope.launch {
            firestoreRepo.incrementSharedAlertCount(currentUserId, contactId)
        }
    }

    fun saveIncidentReport(
        incidentDate: String,
        approximateTime: String,
        amount: Double,
        referenceId: String,
        paymentMethod: String,
        description: String,
        evidenceSummary: String?
    ) {
        viewModelScope.launch {
            firestoreRepo.saveIncidentReport(
                userId = currentUserId,
                incidentDate = incidentDate,
                approximateTime = approximateTime,
                amount = amount,
                referenceId = referenceId,
                paymentMethod = paymentMethod,
                description = description,
                evidenceSummary = evidenceSummary
            ).onSuccess {
                _statusBanner.value = "Incident details saved securely. You can copy and submit at cybercrime.gov.in or when calling 1930."
            }.onFailure {
                _statusBanner.value = it.localizedMessage ?: "Failed to save report."
            }
        }
    }

    fun sendAiMessage(question: String, detailedMode: Boolean) {
        val trimmed = question.trim()
        if (trimmed.isEmpty() || _isAiLoading.value) return

        // Safety intercept: warn immediately if user accidentally typed an OTP/PIN/CVV-like sensitive pattern
        val containsSensitiveAsk = Regex("""(?i)\b(my otp is|my pin is|cvv is|atm pin)\b""").containsMatchIn(trimmed)
        val userMsg = ChatMessage(
            id = "u_${System.currentTimeMillis()}",
            text = trimmed,
            isFromUser = true,
            timestampLabel = currentTimeLabel()
        )
        val priorHistory = _aiMessages.value
        _aiMessages.value = priorHistory + userMsg
        _aiError.value = null

        if (containsSensitiveAsk) {
            _aiMessages.value = _aiMessages.value + ChatMessage(
                id = "warn_${System.currentTimeMillis()}",
                text = "Safety Alert: Never type or share your OTP, UPI PIN, ATM PIN, or CVV with any app, person, or assistant. Please delete that number and never share credentials with anyone.",
                isFromUser = false,
                timestampLabel = currentTimeLabel(),
                isSafetyWarning = true
            )
            return
        }

        _isAiLoading.value = true
        _streamingAiText.value = ""

        val activeLang = settings.value.language
        viewModelScope.launch {
            aiRepo.streamReply(
                userQuestion = trimmed,
                language = activeLang,
                detailedMode = detailedMode,
                recentHistory = priorHistory
            ).collect { chunk ->
                when (chunk) {
                    is AiStreamChunk.Delta -> {
                        _streamingAiText.value = chunk.accumulatedText
                    }
                    is AiStreamChunk.Completed -> {
                        _streamingAiText.value = null
                        _isAiLoading.value = false
                        _aiMessages.value = _aiMessages.value + ChatMessage(
                            id = "a_${System.currentTimeMillis()}",
                            text = chunk.finalText,
                            isFromUser = false,
                            timestampLabel = currentTimeLabel()
                        )
                    }
                    is AiStreamChunk.Error -> {
                        _streamingAiText.value = null
                        _isAiLoading.value = false
                        _aiError.value = chunk.message
                    }
                }
            }
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            preferencesManager.setThemeMode(mode)
            syncPrefsToProfile(theme = mode.name)
        }
    }

    fun setLanguage(language: AppLanguage) {
        viewModelScope.launch {
            preferencesManager.setLanguage(language)
            syncPrefsToProfile(language = language.name)
        }
    }

    fun setSimpleMode(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.setSimpleMode(enabled)
            syncPrefsToProfile(simpleMode = enabled)
        }
    }

    fun setTextScale(scale: Float) {
        viewModelScope.launch { preferencesManager.setTextScale(scale) }
    }

    fun setHighContrast(enabled: Boolean) {
        viewModelScope.launch { preferencesManager.setHighContrast(enabled) }
    }

    fun setVoiceSpeed(speed: Float) {
        viewModelScope.launch { preferencesManager.setVoiceSpeed(speed) }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch { preferencesManager.setNotificationsEnabled(enabled) }
    }

    fun toggleOfflineModule(moduleId: String) {
        viewModelScope.launch { preferencesManager.toggleOfflineModule(moduleId) }
    }

    private suspend fun syncPrefsToProfile(
        language: String = settings.value.language.name,
        theme: String = settings.value.themeMode.name,
        simpleMode: Boolean = settings.value.simpleMode
    ) {
        firestoreRepo.updateProfilePreferences(
            userId = currentUserId,
            language = language,
            theme = theme,
            simpleMode = simpleMode
        )
    }

    private fun computeEarnedBadges(lessonCount: Int, quizCount: Int, hasScans: Boolean): List<String> {
        return FinSmartContentCatalog.badges.filter { badge ->
            when (badge.id) {
                "scam_fighter" -> lessonCount >= badge.requiredLessons && hasScans
                else -> lessonCount >= badge.requiredLessons && quizCount >= badge.requiredQuizzes
            }
        }.map { it.id }
    }

    private fun computeSafetyScore(
        lessonCount: Int,
        quizCount: Int,
        hasScans: Boolean,
        hasTrustedContact: Boolean
    ): Int {
        val lessonPoints = ((lessonCount / 18.0) * 35.0).roundToInt()
        val quizPoints = (quizCount * 7).coerceAtMost(20)
        val scanPoints = if (hasScans) 10 else 0
        val contactPoints = if (hasTrustedContact) 10 else 0
        return (25 + lessonPoints + quizPoints + scanPoints + contactPoints).coerceIn(25, 100)
    }

    private fun computeMonthlySummary(
        monthKey: String,
        allExpenses: List<ExpenseRecord>,
        allBudgets: List<MonthlyBudget>,
        allGoals: List<SavingsGoal>
    ): MonthlyMoneySummary {
        val currentExpenses = allExpenses.filter { it.monthKey == monthKey }
        val budget = allBudgets.firstOrNull { it.monthKey == monthKey }
        val income = budget?.monthlyIncome ?: 0.0
        val totalSpent = currentExpenses.sumOf { it.amount }
        val remaining = income - totalSpent
        val savingsPct = if (income > 0.0) ((remaining / income) * 100.0) else 0.0

        val catTotals = ExpenseCategory.entries.associateWith { cat ->
            currentExpenses.filter { it.category == cat.name }.sumOf { it.amount }
        }

        val prevKey = shiftMonthKey(monthKey, -1)
        val prevExpenses = allExpenses.filter { it.monthKey == prevKey }

        val insights = mutableListOf<String>()
        if (income <= 0.0 && currentExpenses.isEmpty()) {
            insights += "Enter your monthly income and expenses to see real household savings insights."
        } else {
            if (income > 0.0) {
                insights += if (remaining >= 0) {
                    "Your current remaining balance represents ${savingsPct.roundToInt()}% of your entered monthly income."
                } else {
                    "Your recorded expenses exceed your monthly income by ₹${(-remaining).roundToInt()}."
                }
            }
            val prevTotal = prevExpenses.sumOf { it.amount }
            if (prevTotal > 0.0) {
                val topIncreasedCat = ExpenseCategory.entries.mapNotNull { cat ->
                    val currAmt = catTotals[cat] ?: 0.0
                    val prevAmt = prevExpenses.filter { it.category == cat.name }.sumOf { it.amount }
                    val diff = currAmt - prevAmt
                    if (diff > 100.0) cat to diff else null
                }.maxByOrNull { it.second }
                if (topIncreasedCat != null) {
                    insights += "You spent ₹${topIncreasedCat.second.roundToInt()} more on ${topIncreasedCat.first.label} this month compared with last month."
                }
            }
            val firstGoal = allGoals.firstOrNull { it.targetAmount > it.savedAmount }
            if (firstGoal != null) {
                val remainingGoal = (firstGoal.targetAmount - firstGoal.savedAmount).coerceAtLeast(0.0)
                val monthsLeft = ((firstGoal.targetDateMillis - System.currentTimeMillis()) / (30.0 * 86_400_000.0))
                    .coerceAtLeast(1.0)
                val perMonth = (remainingGoal / monthsLeft).roundToInt()
                insights += "Saving roughly ₹$perMonth per month can help you reach your '${firstGoal.title}' goal on time."
            }
        }

        return MonthlyMoneySummary(
            monthKey = monthKey,
            monthLabel = formatMonthLabel(monthKey),
            income = income,
            totalExpenses = totalSpent,
            remaining = remaining,
            savingsPercent = savingsPct,
            categoryTotals = catTotals,
            insights = insights
        )
    }

    private fun <T> Flow<T>.toSafeUiState(fallbackMsg: String): Flow<UiState<T>> =
        this.map<T, UiState<T>> { UiState.Success(it) }
            .catch { error ->
                val fsErr = generateSequence(error) { it.cause }
                    .filterIsInstance<FirebaseFirestoreException>()
                    .firstOrNull()
                if (fsErr == null && error is CancellationException) throw error
                val root = fsErr ?: error
                Log.w(TAG, "Stream error: $fallbackMsg", root)
                emit(UiState.Error(root.message ?: fallbackMsg))
            }

    private fun <T> Flow<List<T>>.safeListFlow(name: String): Flow<List<T>> =
        this.catch { error ->
            val fsErr = generateSequence(error) { it.cause }
                .filterIsInstance<FirebaseFirestoreException>()
                .firstOrNull()
            if (fsErr == null && error is CancellationException) throw error
            Log.w(TAG, "Error observing $name", fsErr ?: error)
            emit(emptyList())
        }

    private fun currentMonthKey(): String =
        SimpleDateFormat("yyyy-MM", Locale.US).format(Date())

    private fun shiftMonthKey(key: String, delta: Int): String {
        val parts = key.split("-")
        val y = parts.getOrNull(0)?.toIntOrNull() ?: 2026
        val m = (parts.getOrNull(1)?.toIntOrNull() ?: 11) - 1
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, y)
            set(Calendar.MONTH, m)
            set(Calendar.DAY_OF_MONTH, 1)
            add(Calendar.MONTH, delta)
        }
        return String.format(Locale.US, "%04d-%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1)
    }

    private fun formatMonthLabel(key: String): String {
        val parts = key.split("-")
        val y = parts.getOrNull(0)?.toIntOrNull() ?: 2026
        val m = (parts.getOrNull(1)?.toIntOrNull() ?: 11) - 1
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, y)
            set(Calendar.MONTH, m)
            set(Calendar.DAY_OF_MONTH, 1)
        }
        return SimpleDateFormat("MMM yyyy", Locale.US).format(cal.time)
    }

    private fun currentTimeLabel(): String =
        SimpleDateFormat("hh:mm a", Locale.US).format(Date())
}
