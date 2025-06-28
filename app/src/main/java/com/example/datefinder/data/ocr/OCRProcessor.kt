package com.example.datefinder.data.ocr

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.tasks.await
import java.io.InputStream

object OCRProcessor {

    private const val TAG = "OCRProcessor"

    suspend fun extractTextFromImage(context: Context, uri: Uri): String {
        return try {
            val bitmap = getBitmapFromUri(context, uri)
            val image = InputImage.fromBitmap(bitmap, 0)

            // Use Latin script recognizer for better accuracy
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            val result = recognizer.process(image).await()

            Log.d(TAG, "Extracted text: ${result.text}")
            result.text
        } catch (e: Exception) {
            Log.e(TAG, "Error extracting text from image", e)
            ""
        }
    }

    private fun getBitmapFromUri(context: Context, uri: Uri): Bitmap {
        return try {
            // Try using MediaStore first (works for most cases)
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        } catch (e: Exception) {
            // Fallback to InputStream approach
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        }
    }
}