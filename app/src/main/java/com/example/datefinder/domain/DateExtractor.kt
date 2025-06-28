package com.example.datefinder.domain

import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

object DateExtractor {

    private const val TAG = "DateExtractor"

    private val datePatterns = listOf(
        "\\b(0?[1-9]|1[0-2])/(0?[1-9]|[12]\\d|3[01])/(\\d{2}|\\d{4})\\b",
        "\\b(0?[1-9]|[12]\\d|3[01])/(0?[1-9]|1[0-2])/(\\d{2}|\\d{4})\\b",
        "\\b(0?[1-9]|1[0-2])-(0?[1-9]|[12]\\d|3[01])-(\\d{2}|\\d{4})\\b",
        "\\b(0?[1-9]|[12]\\d|3[01])-(0?[1-9]|1[0-2])-(\\d{2}|\\d{4})\\b",
        "\\b(\\d{4})-(0?[1-9]|1[0-2])-(0?[1-9]|[12]\\d|3[01])\\b",
        "\\b(January|February|March|April|May|June|July|August|September|October|November|December)\\s+(0?[1-9]|[12]\\d|3[01]),?\\s+(\\d{4})\\b",
        "\\b(0?[1-9]|[12]\\d|3[01])\\s+(January|February|March|April|May|June|July|August|September|October|November|December)\\s+(\\d{4})\\b",
        "\\b(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)\\.?\\s+(0?[1-9]|[12]\\d|3[01]),?\\s+(\\d{4})\\b",
        "\\b(0?[1-9]|[12]\\d|3[01])\\s+(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)\\.?\\s+(\\d{4})\\b",
        "\\b(\\d{1,2})(st|nd|rd|th)?\\s+of\\s+(January|February|March|April|May|June|July|August|September|October|November|December)\\s+(\\d{4})\\b",
        "\\b(\\d{1,2})\\s+(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)\\s+(\\d{4})\\b"
    )

    fun extractMostRelevantDate(text: String): String? {
        Log.d(TAG, "Extracting dates from text: $text")

        val foundDates = mutableListOf<Pair<String, Date>>()

        datePatterns.forEach { pattern ->
            val regex = pattern.toRegex(RegexOption.IGNORE_CASE)
            val matches = regex.findAll(text)
            matches.forEach {
                val raw = it.value
                val date = parseToDate(raw)
                if (date != null) {
                    foundDates.add(raw to date)
                }
            }
        }

        Log.d(TAG, "Valid dates: ${foundDates.map { it.first }}")

        return foundDates.maxByOrNull { it.second }?.first
    }

    private fun parseToDate(dateString: String): Date? {
        val cleanedDate = dateString
            .replace(Regex("\\b(\\d{1,2})(st|nd|rd|th)\\b"), "$1")
            .replace("of ", "", ignoreCase = true)
            .replace("\\s+".toRegex(), " ")
            .trim()

        val formats = listOf(
            "MM/dd/yyyy", "MM/dd/yy", "dd/MM/yyyy", "dd/MM/yy",
            "MM-dd-yyyy", "MM-dd-yy", "dd-MM-yyyy", "dd-MM-yy",
            "yyyy-MM-dd",
            "MMMM dd, yyyy", "dd MMMM yyyy",
            "MMM dd, yyyy", "dd MMM yyyy",
            "MMM. dd, yyyy", "dd MMM. yyyy",
            "dd MMM yyyy", "d MMM yyyy"
        )

        for (format in formats) {
            try {
                val sdf = SimpleDateFormat(format, Locale.ENGLISH)
                sdf.isLenient = false
                return sdf.parse(cleanedDate)
            } catch (e: Exception) {
                continue
            }
        }
        return null
    }
}
