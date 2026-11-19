package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.core.security.OperationType
import com.example.core.security.handleFirestoreError
import com.example.data.model.ExpenseCategory
import com.example.data.model.ExpenseRecord
import com.example.data.model.IncidentReport
import com.example.data.model.MonthlyBudget
import com.example.data.model.QuizResultRecord
import com.example.data.model.SavingsGoal
import com.example.data.model.ScanHistoryItem
import com.example.data.model.TrustedContact
import com.example.data.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class FinSmartFirestoreRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        ),
        FirebaseAuth.getInstance()
    )

    private fun requireMatchedUserId(expectedUserId: String): String {
        val uid = auth.currentUser?.uid
            ?: throw IllegalStateException("Please sign in with Google to sync your FinSmart data.")
        check(uid == expectedUserId) { "Active user changed; refusing cross-user operation." }
        return uid
    }

    private fun userDoc(uid: String) = db.collection("users").document(uid)

    fun observeUserProfile(userId: String): Flow<UserProfile?> =
        userDoc(userId).snapshots()
            .map { snapshot ->
                if (!snapshot.exists()) null
                else snapshot.toObject(UserProfile::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
            }
            .catch { error ->
                logAndRethrowIfNeeded(error, OperationType.GET, "users/$userId", userId)
            }

    fun observeExpenses(userId: String): Flow<List<ExpenseRecord>> =
        userDoc(userId).collection("expenses").snapshots()
            .map { snapshot ->
                snapshot.toObjects(ExpenseRecord::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                    .sortedByDescending { it.dateMillis }
            }
            .catch { error ->
                logAndRethrowIfNeeded(error, OperationType.LIST, "users/$userId/expenses", userId)
            }

    fun observeAllBudgets(userId: String): Flow<List<MonthlyBudget>> =
        userDoc(userId).collection("budgets").snapshots()
            .map { snapshot ->
                snapshot.toObjects(MonthlyBudget::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
            }
            .catch { error ->
                logAndRethrowIfNeeded(error, OperationType.LIST, "users/$userId/budgets", userId)
            }

    fun observeGoals(userId: String): Flow<List<SavingsGoal>> =
        userDoc(userId).collection("goals").snapshots()
            .map { snapshot ->
                snapshot.toObjects(SavingsGoal::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                    .sortedBy { it.targetDateMillis }
            }
            .catch { error ->
                logAndRethrowIfNeeded(error, OperationType.LIST, "users/$userId/goals", userId)
            }

    fun observeScanHistory(userId: String): Flow<List<ScanHistoryItem>> =
        userDoc(userId).collection("scanHistory").snapshots()
            .map { snapshot ->
                snapshot.toObjects(ScanHistoryItem::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                    .sortedByDescending { it.createdAt?.seconds ?: 0L }
            }
            .catch { error ->
                logAndRethrowIfNeeded(error, OperationType.LIST, "users/$userId/scanHistory", userId)
            }

    fun observeTrustedContacts(userId: String): Flow<List<TrustedContact>> =
        userDoc(userId).collection("trustedContacts").snapshots()
            .map { snapshot ->
                snapshot.toObjects(TrustedContact::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
            }
            .catch { error ->
                logAndRethrowIfNeeded(error, OperationType.LIST, "users/$userId/trustedContacts", userId)
            }

    fun observeIncidentReports(userId: String): Flow<List<IncidentReport>> =
        userDoc(userId).collection("incidentReports").snapshots()
            .map { snapshot ->
                snapshot.toObjects(IncidentReport::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                    .sortedByDescending { it.createdAt?.seconds ?: 0L }
            }
            .catch { error ->
                logAndRethrowIfNeeded(error, OperationType.LIST, "users/$userId/incidentReports", userId)
            }

    fun observeQuizResults(userId: String): Flow<List<QuizResultRecord>> =
        userDoc(userId).collection("quizResults").snapshots()
            .map { snapshot ->
                snapshot.toObjects(QuizResultRecord::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                    .sortedByDescending { it.createdAt?.seconds ?: 0L }
            }
            .catch { error ->
                logAndRethrowIfNeeded(error, OperationType.LIST, "users/$userId/quizResults", userId)
            }

    private fun logAndRethrowIfNeeded(
        error: Throwable,
        operationType: OperationType,
        path: String,
        expectedUid: String
    ): Nothing {
        val firestoreError = generateSequence(error) { it.cause }
            .filterIsInstance<FirebaseFirestoreException>()
            .firstOrNull()
        if (firestoreError == null && error is CancellationException) throw error
        val exceptionToLog = firestoreError ?: (error as? Exception)
        if (exceptionToLog != null && auth.currentUser?.uid == expectedUid) {
            handleFirestoreError(exceptionToLog, operationType, path)
        }
        throw error
    }

    suspend fun ensureProfileExists(
        userId: String,
        displayName: String,
        language: String,
        theme: String,
        simpleMode: Boolean
    ): Result<Unit> = runSafeWrite(OperationType.CREATE, "users/$userId") {
        val uid = requireMatchedUserId(userId)
        val docRef = userDoc(uid)
        val existing = docRef.get().await()
        if (!existing.exists()) {
            val safeName = displayName.trim().ifBlank { "FinSmart User" }.take(60)
            val payload = mapOf(
                "userId" to uid,
                "displayName" to safeName,
                "financialSafetyScore" to 55,
                "streakDays" to 1,
                "completedLessonIds" to emptyList<String>(),
                "completedQuizIds" to emptyList<String>(),
                "earnedBadgeIds" to emptyList<String>(),
                "selectedLanguage" to language,
                "preferredTheme" to theme,
                "simpleMode" to simpleMode,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.set(payload).await()
        }
    }

    suspend fun updateProfilePreferences(
        userId: String,
        language: String,
        theme: String,
        simpleMode: Boolean
    ): Result<Unit> = runSafeWrite(OperationType.UPDATE, "users/$userId") {
        val uid = requireMatchedUserId(userId)
        userDoc(uid).update(
            mapOf(
                "selectedLanguage" to language,
                "preferredTheme" to theme,
                "simpleMode" to simpleMode,
                "updatedAt" to FieldValue.serverTimestamp()
            )
        ).await()
    }

    suspend fun markLessonCompleted(
        userId: String,
        lessonId: String,
        newCompletedLessons: List<String>,
        newBadges: List<String>,
        newScore: Int,
        streakDays: Int
    ): Result<Unit> = runSafeWrite(OperationType.UPDATE, "users/$userId") {
        val uid = requireMatchedUserId(userId)
        userDoc(uid).update(
            mapOf(
                "completedLessonIds" to newCompletedLessons.distinct().take(50),
                "earnedBadgeIds" to newBadges.distinct().take(50),
                "financialSafetyScore" to newScore.coerceIn(0, 100),
                "streakDays" to streakDays.coerceIn(0, 3650),
                "updatedAt" to FieldValue.serverTimestamp()
            )
        ).await()
    }

    suspend fun saveQuizResult(
        userId: String,
        quizId: String,
        quizTitle: String,
        score: Int,
        totalQuestions: Int,
        updatedCompletedQuizzes: List<String>,
        updatedBadges: List<String>,
        updatedSafetyScore: Int
    ): Result<Unit> = runSafeWrite(OperationType.CREATE, "users/$userId/quizResults") {
        val uid = requireMatchedUserId(userId)
        val resRef = userDoc(uid).collection("quizResults").document()
        resRef.set(
            mapOf(
                "userId" to uid,
                "quizId" to quizId.take(60),
                "quizTitle" to quizTitle.take(100),
                "score" to score.coerceIn(0, 100),
                "totalQuestions" to totalQuestions.coerceIn(1, 100),
                "createdAt" to FieldValue.serverTimestamp()
            )
        ).await()
        userDoc(uid).update(
            mapOf(
                "completedQuizIds" to updatedCompletedQuizzes.distinct().take(50),
                "earnedBadgeIds" to updatedBadges.distinct().take(50),
                "financialSafetyScore" to updatedSafetyScore.coerceIn(0, 100),
                "updatedAt" to FieldValue.serverTimestamp()
            )
        ).await()
    }

    suspend fun upsertExpense(
        userId: String,
        expenseId: String?,
        title: String,
        category: ExpenseCategory,
        amount: Double,
        monthKey: String,
        dateMillis: Long,
        note: String?
    ): Result<Unit> {
        val path = "users/$userId/expenses/${expenseId ?: "new"}"
        return runSafeWrite(if (expenseId == null) OperationType.CREATE else OperationType.UPDATE, path) {
            val uid = requireMatchedUserId(userId)
            val cleanTitle = title.trim().take(80)
            require(cleanTitle.isNotEmpty()) { "Expense title is required." }
            require(amount > 0.0 && amount <= 100_000_000.0) { "Amount must be greater than zero." }
            require(monthKey.matches(Regex("^[0-9]{4}-[0-9]{2}$"))) { "Invalid month format." }
            val cleanNote = note?.trim()?.take(200)?.takeIf { it.isNotEmpty() }

            if (expenseId.isNullOrBlank()) {
                val docRef = userDoc(uid).collection("expenses").document()
                val payload = mutableMapOf<String, Any>(
                    "userId" to uid,
                    "title" to cleanTitle,
                    "category" to category.name,
                    "amount" to amount,
                    "monthKey" to monthKey,
                    "dateMillis" to dateMillis,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                if (cleanNote != null) payload["note"] = cleanNote
                docRef.set(payload).await()
            } else {
                val docRef = userDoc(uid).collection("expenses").document(expenseId)
                val updates = mutableMapOf<String, Any>(
                    "title" to cleanTitle,
                    "category" to category.name,
                    "amount" to amount,
                    "monthKey" to monthKey,
                    "dateMillis" to dateMillis,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                if (cleanNote != null) updates["note"] = cleanNote
                docRef.update(updates).await()
            }
        }
    }

    suspend fun deleteExpense(userId: String, expenseId: String): Result<Unit> =
        runSafeWrite(OperationType.DELETE, "users/$userId/expenses/$expenseId") {
            val uid = requireMatchedUserId(userId)
            require(expenseId.isNotBlank())
            userDoc(uid).collection("expenses").document(expenseId).delete().await()
        }

    suspend fun saveMonthlyBudget(
        userId: String,
        budget: MonthlyBudget
    ): Result<Unit> = runSafeWrite(OperationType.WRITE, "users/$userId/budgets/${budget.monthKey}") {
        val uid = requireMatchedUserId(userId)
        require(budget.monthKey.matches(Regex("^[0-9]{4}-[0-9]{2}$"))) { "Invalid month key." }
        val docRef = userDoc(uid).collection("budgets").document(budget.monthKey)
        val existing = docRef.get().await()
        if (existing.exists()) {
            docRef.update(
                mapOf(
                    "monthlyIncome" to budget.monthlyIncome.coerceIn(0.0, 100_000_000.0),
                    "householdLimit" to budget.householdLimit.coerceIn(0.0, 100_000_000.0),
                    "foodLimit" to budget.foodLimit.coerceIn(0.0, 100_000_000.0),
                    "transportLimit" to budget.transportLimit.coerceIn(0.0, 100_000_000.0),
                    "educationLimit" to budget.educationLimit.coerceIn(0.0, 100_000_000.0),
                    "healthcareLimit" to budget.healthcareLimit.coerceIn(0.0, 100_000_000.0),
                    "billsLimit" to budget.billsLimit.coerceIn(0.0, 100_000_000.0),
                    "shoppingLimit" to budget.shoppingLimit.coerceIn(0.0, 100_000_000.0),
                    "otherLimit" to budget.otherLimit.coerceIn(0.0, 100_000_000.0),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        } else {
            docRef.set(
                mapOf(
                    "userId" to uid,
                    "monthKey" to budget.monthKey,
                    "monthlyIncome" to budget.monthlyIncome.coerceIn(0.0, 100_000_000.0),
                    "householdLimit" to budget.householdLimit.coerceIn(0.0, 100_000_000.0),
                    "foodLimit" to budget.foodLimit.coerceIn(0.0, 100_000_000.0),
                    "transportLimit" to budget.transportLimit.coerceIn(0.0, 100_000_000.0),
                    "educationLimit" to budget.educationLimit.coerceIn(0.0, 100_000_000.0),
                    "healthcareLimit" to budget.healthcareLimit.coerceIn(0.0, 100_000_000.0),
                    "billsLimit" to budget.billsLimit.coerceIn(0.0, 100_000_000.0),
                    "shoppingLimit" to budget.shoppingLimit.coerceIn(0.0, 100_000_000.0),
                    "otherLimit" to budget.otherLimit.coerceIn(0.0, 100_000_000.0),
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        }
    }

    suspend fun upsertSavingsGoal(
        userId: String,
        goalId: String?,
        title: String,
        category: String,
        targetAmount: Double,
        savedAmount: Double,
        targetDateMillis: Long
    ): Result<Unit> = runSafeWrite(OperationType.WRITE, "users/$userId/goals") {
        val uid = requireMatchedUserId(userId)
        val cleanTitle = title.trim().take(80)
        val cleanCat = category.trim().ifBlank { "Savings" }.take(40)
        require(cleanTitle.isNotEmpty()) { "Goal title is required." }
        require(targetAmount > 0.0 && targetAmount <= 100_000_000.0) { "Target must be > 0." }
        require(savedAmount >= 0.0 && savedAmount <= 100_000_000.0) { "Saved amount cannot be negative." }

        if (goalId.isNullOrBlank()) {
            userDoc(uid).collection("goals").document().set(
                mapOf(
                    "userId" to uid,
                    "title" to cleanTitle,
                    "category" to cleanCat,
                    "targetAmount" to targetAmount,
                    "savedAmount" to savedAmount,
                    "targetDateMillis" to targetDateMillis,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        } else {
            userDoc(uid).collection("goals").document(goalId).update(
                mapOf(
                    "title" to cleanTitle,
                    "category" to cleanCat,
                    "targetAmount" to targetAmount,
                    "savedAmount" to savedAmount,
                    "targetDateMillis" to targetDateMillis,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        }
    }

    suspend fun deleteGoal(userId: String, goalId: String): Result<Unit> =
        runSafeWrite(OperationType.DELETE, "users/$userId/goals/$goalId") {
            val uid = requireMatchedUserId(userId)
            userDoc(uid).collection("goals").document(goalId).delete().await()
        }

    suspend fun saveScanRecord(
        userId: String,
        scanType: String,
        inputPreview: String,
        verdict: String,
        summary: String,
        reasons: List<String>
    ): Result<String> = runSafeWriteWithValue(OperationType.CREATE, "users/$userId/scanHistory") {
        val uid = requireMatchedUserId(userId)
        val docRef = userDoc(uid).collection("scanHistory").document()
        docRef.set(
            mapOf(
                "userId" to uid,
                "scanType" to scanType,
                "inputPreview" to inputPreview.trim().ifBlank { "Scan" }.take(160),
                "verdict" to verdict,
                "summary" to summary.trim().ifBlank { "Analysis complete." }.take(300),
                "reasons" to reasons.take(10).map { it.take(120) },
                "reported" to false,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
        ).await()
        docRef.id
    }

    suspend fun markScanReported(userId: String, scanId: String): Result<Unit> =
        runSafeWrite(OperationType.UPDATE, "users/$userId/scanHistory/$scanId") {
            val uid = requireMatchedUserId(userId)
            userDoc(uid).collection("scanHistory").document(scanId).update(
                mapOf(
                    "reported" to true,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        }

    suspend fun upsertTrustedContact(
        userId: String,
        contactId: String?,
        name: String,
        relationship: String,
        phoneNumber: String,
        consentGranted: Boolean,
        revoked: Boolean = false
    ): Result<Unit> = runSafeWrite(OperationType.WRITE, "users/$userId/trustedContacts") {
        val uid = requireMatchedUserId(userId)
        val cleanName = name.trim().take(60)
        val cleanRel = relationship.trim().ifBlank { "Family" }.take(40)
        val cleanPhone = phoneNumber.trim().take(20)
        require(cleanName.isNotEmpty()) { "Name is required." }
        require(cleanPhone.length >= 5) { "Valid phone number is required." }
        require(consentGranted) { "Explicit consent is required to add a trusted contact." }

        if (contactId.isNullOrBlank()) {
            userDoc(uid).collection("trustedContacts").document().set(
                mapOf(
                    "userId" to uid,
                    "name" to cleanName,
                    "relationship" to cleanRel,
                    "phoneNumber" to cleanPhone,
                    "consentGranted" to true,
                    "revoked" to revoked,
                    "sharedAlertsCount" to 0,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        } else {
            userDoc(uid).collection("trustedContacts").document(contactId).update(
                mapOf(
                    "name" to cleanName,
                    "relationship" to cleanRel,
                    "phoneNumber" to cleanPhone,
                    "consentGranted" to consentGranted,
                    "revoked" to revoked,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        }
    }

    suspend fun toggleTrustedContactRevoke(userId: String, contactId: String, revoked: Boolean): Result<Unit> =
        runSafeWrite(OperationType.UPDATE, "users/$userId/trustedContacts/$contactId") {
            val uid = requireMatchedUserId(userId)
            userDoc(uid).collection("trustedContacts").document(contactId).update(
                mapOf(
                    "revoked" to revoked,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        }

    suspend fun incrementSharedAlertCount(userId: String, contactId: String): Result<Unit> =
        runSafeWrite(OperationType.UPDATE, "users/$userId/trustedContacts/$contactId") {
            val uid = requireMatchedUserId(userId)
            userDoc(uid).collection("trustedContacts").document(contactId).update(
                mapOf(
                    "sharedAlertsCount" to FieldValue.increment(1),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        }

    suspend fun deleteTrustedContact(userId: String, contactId: String): Result<Unit> =
        runSafeWrite(OperationType.DELETE, "users/$userId/trustedContacts/$contactId") {
            val uid = requireMatchedUserId(userId)
            userDoc(uid).collection("trustedContacts").document(contactId).delete().await()
        }

    suspend fun saveIncidentReport(
        userId: String,
        incidentDate: String,
        approximateTime: String,
        amount: Double,
        referenceId: String,
        paymentMethod: String,
        description: String,
        evidenceSummary: String?
    ): Result<Unit> = runSafeWrite(OperationType.CREATE, "users/$userId/incidentReports") {
        val uid = requireMatchedUserId(userId)
        require(incidentDate.isNotBlank()) { "Incident date is required." }
        require(description.isNotBlank()) { "Description is required." }
        val cleanEvidence = evidenceSummary?.trim()?.take(500)?.takeIf { it.isNotEmpty() }
        val payload = mutableMapOf<String, Any>(
            "userId" to uid,
            "incidentDate" to incidentDate.trim().take(30),
            "approximateTime" to approximateTime.trim().ifBlank { "Unknown" }.take(30),
            "amount" to amount.coerceIn(0.0, 100_000_000.0),
            "referenceId" to referenceId.trim().take(80),
            "paymentMethod" to paymentMethod.trim().ifBlank { "UPI" }.take(40),
            "description" to description.trim().take(1000),
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        if (cleanEvidence != null) payload["evidenceSummary"] = cleanEvidence
        userDoc(uid).collection("incidentReports").document().set(payload).await()
    }

    private suspend fun runSafeWrite(
        operationType: OperationType,
        path: String,
        block: suspend () -> Unit
    ): Result<Unit> = try {
        block()
        Result.success(Unit)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        handleFirestoreError(e, operationType, path)
        Result.failure(e)
    }

    private suspend fun <T> runSafeWriteWithValue(
        operationType: OperationType,
        path: String,
        block: suspend () -> T
    ): Result<T> = try {
        Result.success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        handleFirestoreError(e, operationType, path)
        Result.failure(e)
    }
}
