package com.example.data.model

import androidx.annotation.Keep
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

enum class AppLanguage(val code: String, val displayName: String, val nativeLabel: String) {
    ENGLISH("en", "English", "English"),
    KANNADA("kn", "Kannada", "ಕನ್ನಡ"),
    HINDI("hi", "Hindi", "हिन्दी")
}

enum class ExpenseCategory(val label: String, val isNeedByDefault: Boolean) {
    HOUSEHOLD("Household", true),
    FOOD("Food", true),
    TRANSPORT("Transport", true),
    EDUCATION("Education", true),
    HEALTHCARE("Healthcare", true),
    BILLS("Electricity & Bills", true),
    SHOPPING("Shopping", false),
    OTHER("Others", false)
}

@Keep
@IgnoreExtraProperties
data class UserProfile(
    @DocumentId val id: String = "",
    val userId: String = "",
    val displayName: String = "FinSmart User",
    val financialSafetyScore: Int = 55,
    val streakDays: Int = 1,
    val completedLessonIds: List<String> = emptyList(),
    val completedQuizIds: List<String> = emptyList(),
    val earnedBadgeIds: List<String> = emptyList(),
    val selectedLanguage: String = AppLanguage.ENGLISH.name,
    val preferredTheme: String = "SYSTEM",
    val simpleMode: Boolean = false,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class ExpenseRecord(
    @DocumentId val id: String = "",
    val userId: String = "",
    val title: String = "",
    val category: String = ExpenseCategory.OTHER.name,
    val amount: Double = 0.0,
    val monthKey: String = "",
    val dateMillis: Long = 0L,
    val note: String? = null,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class MonthlyBudget(
    @DocumentId val id: String = "",
    val userId: String = "",
    val monthKey: String = "",
    val monthlyIncome: Double = 0.0,
    val householdLimit: Double = 0.0,
    val foodLimit: Double = 0.0,
    val transportLimit: Double = 0.0,
    val educationLimit: Double = 0.0,
    val healthcareLimit: Double = 0.0,
    val billsLimit: Double = 0.0,
    val shoppingLimit: Double = 0.0,
    val otherLimit: Double = 0.0,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun totalPlannedExpenses(): Double =
        householdLimit + foodLimit + transportLimit + educationLimit +
            healthcareLimit + billsLimit + shoppingLimit + otherLimit
}

@Keep
@IgnoreExtraProperties
data class SavingsGoal(
    @DocumentId val id: String = "",
    val userId: String = "",
    val title: String = "",
    val category: String = "Emergency Fund",
    val targetAmount: Double = 0.0,
    val savedAmount: Double = 0.0,
    val targetDateMillis: Long = 0L,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class ScanHistoryItem(
    @DocumentId val id: String = "",
    val userId: String = "",
    val scanType: String = "LINK",
    val inputPreview: String = "",
    val verdict: String = "GREY",
    val summary: String = "",
    val reasons: List<String> = emptyList(),
    val reported: Boolean = false,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class TrustedContact(
    @DocumentId val id: String = "",
    val userId: String = "",
    val name: String = "",
    val relationship: String = "Family",
    val phoneNumber: String = "",
    val consentGranted: Boolean = false,
    val revoked: Boolean = false,
    val sharedAlertsCount: Int = 0,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class IncidentReport(
    @DocumentId val id: String = "",
    val userId: String = "",
    val incidentDate: String = "",
    val approximateTime: String = "",
    val amount: Double = 0.0,
    val referenceId: String = "",
    val paymentMethod: String = "UPI",
    val description: String = "",
    val evidenceSummary: String? = null,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class QuizResultRecord(
    @DocumentId val id: String = "",
    val userId: String = "",
    val quizId: String = "",
    val quizTitle: String = "",
    val score: Int = 0,
    val totalQuestions: Int = 0,
    @ServerTimestamp val createdAt: Timestamp? = null
)

// Domain models for local educational catalog (works 100% offline)
enum class LearningModuleId(val title: String, val subtitle: String, val learningPath: String) {
    BASIC_BANKING("Basic Banking", "Accounts, ATM, Cards & IFSC", "Banking for Beginners"),
    SAVINGS_PLANNING("Savings & Planning", "Budgeting, Needs vs Wants", "Money Management"),
    DIGITAL_UPI("Digital Payments (UPI)", "UPI, QR, Mobile Banking & Safety", "Banking for Beginners"),
    FRAUD_SAFETY("Fraud & Scam Safety", "Fake Calls, Links, KYC & OTP Scams", "Avoiding Digital Fraud")
}

data class LessonItem(
    val id: String,
    val moduleId: LearningModuleId,
    val titleEn: String,
    val titleKn: String,
    val titleHi: String,
    val summaryEn: String,
    val contentEn: String,
    val contentKn: String,
    val contentHi: String,
    val keyTakeawayEn: String,
    val durationMinutes: Int
) {
    fun localizedTitle(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> titleEn
        AppLanguage.KANNADA -> titleKn
        AppLanguage.HINDI -> titleHi
    }

    fun localizedContent(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> contentEn
        AppLanguage.KANNADA -> contentKn
        AppLanguage.HINDI -> contentHi
    }
}

data class QuizQuestion(
    val id: String,
    val moduleId: LearningModuleId,
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)

data class FraudScenario(
    val id: String,
    val title: String,
    val situation: String,
    val options: List<String>,
    val safeOptionIndex: Int,
    val explanation: String,
    val safetyRule: String
)

data class GovSchemeInfo(
    val id: String,
    val name: String,
    val shortName: String,
    val purpose: String,
    val eligibility: List<String>,
    val benefits: List<String>,
    val documents: List<String>,
    val applicationSteps: List<String>,
    val officialUrl: String,
    val lastVerified: String,
    val agentWarning: String
)

data class BeforeYouPaySituation(
    val id: String,
    val title: String,
    val subtitle: String,
    val riskReminder: String,
    val checklist: List<String>
)

data class NeedsVsWantsItem(
    val id: String,
    val title: String,
    val exampleCost: String,
    val isNeed: Boolean,
    val explanation: String
)

data class AwarenessVideoGuide(
    val id: String,
    val title: String,
    val category: String,
    val duration: String,
    val narrationScript: String,
    val keyPoints: List<String>
)

data class SafetyBadge(
    val id: String,
    val title: String,
    val description: String,
    val requiredLessons: Int = 0,
    val requiredQuizzes: Int = 0
)

data class ChatMessage(
    val id: String,
    val text: String,
    val isFromUser: Boolean,
    val timestampLabel: String,
    val isSafetyWarning: Boolean = false
)
