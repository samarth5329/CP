package com.example.core.ocr

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await

object ScreenshotOcrHelper {
    private const val TAG = "ScreenshotOcr"

    suspend fun extractTextFromUri(context: Context, imageUri: Uri): Result<String> {
        return try {
            val image = InputImage.fromFilePath(context, imageUri)
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            val result = recognizer.process(image).await()
            recognizer.close()
            val text = result.text.trim()
            if (text.isBlank()) {
                Result.failure(IllegalStateException("No readable text was detected in this screenshot. You can still type or paste the message manually."))
            } else {
                Result.success(text)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "On-device OCR failed", e)
            Result.failure(e)
        }
    }
}
