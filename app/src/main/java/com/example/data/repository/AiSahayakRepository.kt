package com.example.data.repository

import android.util.Log
import com.example.data.model.AppLanguage
import com.example.data.model.ChatMessage
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.content
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeout

private const val TAG = "AiSahayakRepo"

sealed interface AiStreamChunk {
    data class Delta(val accumulatedText: String) : AiStreamChunk
    data class Completed(val finalText: String) : AiStreamChunk
    data class Error(val message: String) : AiStreamChunk
}

class AiSahayakRepository {

    private val model by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            systemInstruction = content {
                text(
                    """
                    You are FinSmart AI Sahayak, a warm, trustworthy financial literacy and digital-safety assistant designed for Indian households, rural users, senior citizens, students, and first-time UPI/banking users.
                    
                    STRICT SAFETY & BEHAVIOR RULES:
                    1. NEVER ask the user for their OTP, UPI PIN, ATM PIN, CVV, bank password, Aadhaar number, or full bank account number. If they paste sensitive credentials, warn them immediately not to share them anywhere.
                    2. NEVER claim to be a bank representative, RBI official, or police officer.
                    3. NEVER claim that a link, phone number, or payment request is verified safe unless you explicitly state you are only explaining common patterns.
                    4. Use simple, everyday language. Whenever you mention a banking term (like IFSC, KYC, EMI, PMJDY, UPI Collect), explain it simply.
                    5. Give clear bullet points or numbered step-by-step instructions.
                    6. Flag potentially dangerous actions clearly (e.g., scanning QR to receive money, installing AnyDesk/TeamViewer, tapping urgent KYC SMS links).
                    7. Match the user's requested language strictly (English, Kannada in Kannada script, or Hindi in Devanagari script).
                    8. Keep responses concise and easy to read by default unless the user asks for a detailed explanation.
                    """.trimIndent()
                )
            }
        )
    }

    fun streamReply(
        userQuestion: String,
        language: AppLanguage,
        detailedMode: Boolean,
        recentHistory: List<ChatMessage>
    ): Flow<AiStreamChunk> = flow {
        val langInstruction = when (language) {
            AppLanguage.ENGLISH -> "Respond in simple, clear English."
            AppLanguage.KANNADA -> "Respond in simple, natural Kannada (ಕನ್ನಡ script) that ordinary families can easily understand."
            AppLanguage.HINDI -> "Respond in simple, natural Hindi (हिन्दी Devanagari script) that ordinary families can easily understand."
        }
        val lengthInstruction = if (detailedMode) {
            "Provide a thorough step-by-step explanation with practical Indian examples."
        } else {
            "Keep the answer short (3 to 5 concise bullet points or 3 short sentences) unless safety steps require more."
        }

        val chatHistory = recentHistory.takeLast(6).map { msg ->
            content(role = if (msg.isFromUser) "user" else "model") {
                text(msg.text)
            }
        }

        val promptWithGuardrails = """
            [$langInstruction $lengthInstruction]
            User question: ${userQuestion.trim()}
        """.trimIndent()

        val accumulated = StringBuilder()
        try {
            withTimeout(60_000L) {
                val chat = model.startChat(history = chatHistory)
                chat.sendMessageStream(promptWithGuardrails).collect { chunk ->
                    val piece = chunk.text.orEmpty()
                    if (piece.isNotEmpty()) {
                        accumulated.append(piece)
                        emit(AiStreamChunk.Delta(accumulated.toString()))
                    }
                }
            }
            val finalStr = accumulated.toString().trim()
            if (finalStr.isNotEmpty()) {
                emit(AiStreamChunk.Completed(finalStr))
            } else {
                emit(AiStreamChunk.Error("AI Sahayak returned an empty response. Please try again."))
            }
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "AI Sahayak timed out", e)
            emit(AiStreamChunk.Error("Request timed out after 60 seconds. Please check your connection and retry."))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "AI Sahayak generation failed", e)
            emit(AiStreamChunk.Error(e.localizedMessage ?: "Unable to reach FinSmart AI Sahayak right now."))
        }
    }
}
