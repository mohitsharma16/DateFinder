package com.example.datefinder.domain

import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

object DateExtractor {

    private const val TAG = "DateExtractor"

    private val datePatterns = listOf(
        // MM/dd/yyyy or MM/dd/yy
        "\\b(0?[1-9]|1[0-2])/(0?[1-9]|[12]\\d|3[01])/(\\d{2}|\\d{4})\\b",
        // dd/MM/yyyy or dd/MM/yy
        "\\b(0?[1-9]|[12]\\d|3[01])/(0?[1-9]|1[0-2])/(\\d{2}|\\d{4})\\b",
        // MM-dd-yyyy or MM-dd-yy
        "\\b(0?[1-9]|1[0-2])-(0?[1-9]|[12]\\d|3[01])-(\\d{2}|\\d{4})\\b",
        // dd-MM-yyyy or dd-MM-yy
        "\\b(0?[1-9]|[12]\\d|3[01])-(0?[1-9]|1[0-2])-(\\d{2}|\\d{4})\\b",
        // yyyy-MM-dd (ISO format)
        "\\b(\\d{4})-(0?[1-9]|1[0-2])-(0?[1-9]|[12]\\d|3[01])\\b",
        // Month dd, yyyy (e.g., January 15, 2024)
        "\\b(January|February|March|April|May|June|July|August|September|October|November|December)\\s+(0?[1-9]|[12]\\d|3[01]),?\\s+(\\d{4})\\b",
        // dd Month yyyy (e.g., 15 January 2024)
        "\\b(0?[1-9]|[12]\\d|3[01])\\s+(January|February|March|April|May|June|July|August|September|October|November|December)\\s+(\\d{4})\\b",
        // Abbreviated months (Jan, Feb, etc.)
        "\\b(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)\\.?\\s+(0?[1-9]|[12]\\d|3[01]),?\\s+(\\d{4})\\b",
        "\\b(0?[1-9]|[12]\\d|3[01])\\s+(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)\\.?\\s+(\\d{4})\\b"
    )

    fun extractMostRelevantDate(text: String): String? {
        Log.d(TAG, "Extracting dates from text: $text")

        val foundDates = mutableListOf<String>()

        datePatterns.forEach { pattern ->
            val regex = pattern.toRegex(RegexOption.IGNORE_CASE)
            val matches = regex.findAll(text)
            foundDates.addAll(matches.map { it.value })
        }

        Log.d(TAG, "Found dates: $foundDates")

        if (foundDates.isEmpty()) {
            return null
        }

        // Return the first valid date found
        // You could implement more sophisticated logic here to pick the "most relevant" date
        return foundDates.firstOrNull { isValidDate(it) }
    }

    private fun isValidDate(dateString: String): Boolean {
        val formats = listOf(
            "MM/dd/yyyy", "MM/dd/yy", "dd/MM/yyyy", "dd/MM/yy",
            "MM-dd-yyyy", "MM-dd-yy", "dd-MM-yyyy", "dd-MM-yy",
            "yyyy-MM-dd",
            "MMMM dd, yyyy", "dd MMMM yyyy",
            "MMM dd, yyyy", "dd MMM yyyy",
            "MMM. dd, yyyy", "dd MMM. yyyy"
        )

        for (format in formats) {
            try {
                val sdf = SimpleDateFormat(format, Locale.ENGLISH)
                sdf.isLenient = false
                sdf.parse(dateString)
                return true
            } catch (e: Exception) {
                // Continue to next format
            }
        }
        return false
    }
}