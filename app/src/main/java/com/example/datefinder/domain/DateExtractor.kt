package com.example.datefinder.domain

import android.util.Log
import java.text.SimpleDateFormat
import java.util.* // Keep this for Date and Calendar
import java.util.regex.Pattern

object DateExtractor {

    private const val TAG = "DateExtractor"

    // Flexible patterns for various date formats
    private val datePatterns = listOf(
        // e.g., 25/01/1971, 01/25/1971
        "\\b(0?[1-9]|[12]\\d|3[01])[\\/\\-](0?[1-9]|1[0-2])[\\/\\-](\\d{2,4})\\b", // DD/MM/YY(YY)
        "\\b(0?[1-9]|1[0-2])[\\/\\-](0?[1-9]|[12]\\d|3[01])[\\/\\-](\\d{2,4})\\b", // MM/DD/YY(YY)

        // e.g., 1971-01-25
        "\\b(\\d{4})-(0?[1-9]|1[0-2])-(0?[1-9]|[12]\\d|3[01])\\b", //YYYY-MM-DD
        "\\b(\\d{2})-(0?[1-9]|1[0-2])-(0?[1-9]|[12]\\d|3[01])\\b", // YY-MM-DD (less common, but possible)

        // e.g., January 25, 1971 or Jan 25, 1971
        "\\b(Jan(?:uary)?|Feb(?:ruary)?|Mar(?:ch)?|Apr(?:il)?|May|Jun(?:e)?|Jul(?:y)?|Aug(?:ust)?|Sep(?:t(?:ember)?)?|Oct(?:ober)?|Nov(?:ember)?|Dec(?:ember)?)\\.?\\s+(0?[1-9]|[12]\\d|3[01]),?\\s+(\\d{2,4})\\b",

        // e.g., 25 January 1971 or 25 Jan 1971
        // ADDED (?:\\s+of)? to handle "25 of January 1971"
        "\\b(0?[1-9]|[12]\\d|3[01])(?:\\s+of)?\\s+(Jan(?:uary)?|Feb(?:ruary)?|Mar(?:ch)?|Apr(?:il)?|May|Jun(?:e)?|Jul(?:y)?|Aug(?:ust)?|Sep(?:t(?:ember)?)?|Oct(?:ober)?|Nov(?:ember)?|Dec(?:ember)?)\\.?\\s+(\\d{2,4})\\b",

        // e.g., 25-Jan-71 or 25 Jan 71 (common in some documents)
        "\\b(0?[1-9]|[12]\\d|3[01])[\\- ](Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)[\\- ](\\d{2,4})\\b",

        // NEW: YYYY/MM/DD or YYYY.MM.DD
        "\\b(\\d{4})[\\/\\-\\.](0?[1-9]|1[0-2])[\\/\\-\\.](0?[1-9]|[12]\\d|3[01])\\b",

        // e.g., 25/01/1971, 01/25/1971, 21.03.2020
        "\\b(0?[1-9]|[12]\\d|3[01])[\\/\\-\\.](0?[1-9]|1[0-2])[\\/\\-\\.](?:\\d{2}|\\d{4})\\b", // DD/MM/YY(YY) or DD.MM.YY(YY)
        "\\b(0?[1-9]|1[0-2])[\\/\\-\\.](0?[1-9]|[12]\\d|3[01])[\\/\\-\\.](?:\\d{2}|\\d{4})\\b", // MM/DD/YY(YY) or MM.DD.YY(YY)

        // Adding patterns with day of the week (optional, but good for robust recognition)
        // e.g., Mon, Jan 25, 1971
        "\\b(?:Mon|Tue|Wed|Thu|Fri|Sat|Sun)\\.?\\s*," +
                "\\s*(Jan(?:uary)?|Feb(?:ruary)?|Mar(?:ch)?|Apr(?:il)?|May|Jun(?:e)?|Jul(?:y)?|Aug(?:ust)?|Sep(?:t(?:ember)?)?|Oct(?:ober)?|Nov(?:ember)?|Dec(?:ember)?)\\.?\\s+" +
                "(0?[1-9]|[12]\\d|3[01]),?\\s+(\\d{2,4})\\b",
        // e.g., Monday, January 25, 1971
        "\\b(?:Monday|Tuesday|Wednesday|Thursday|Friday|Saturday|Sunday),\\s*" +
                "(Jan(?:uary)?|Feb(?:ruary)?|Mar(?:ch)?|Apr(?:il)?|May|Jun(?:e)?|Jul(?:y)?|Aug(?:ust)?|Sep(?:t(?:ember)?)?|Oct(?:ober)?|Nov(?:ember)?|Dec(?:ember)?)\\s+" +
                "(0?[1-9]|[12]\\d|3[01]),?\\s+(\\d{4})\\b" // Full year for full day names
    )

    /**
     * Extracts the most relevant date from a given block of OCR text.
     */
    fun extractMostRelevantDate(text: String): String? {
        Log.d(TAG, "Raw OCR text:\n$text")

        val cleanedText = preProcessText(text)
        Log.d(TAG, "Normalized text:\n$cleanedText")

        val foundDates = mutableListOf<Pair<String, Date>>()

        for (pattern in datePatterns) {
            val regex = pattern.toRegex(RegexOption.IGNORE_CASE)
            val matches = regex.findAll(cleanedText)
            for (match in matches) {
                val rawDate = match.value
                parseToDate(rawDate)?.let { parsedDate ->
                    foundDates.add(rawDate to parsedDate)
                }
            }
        }

        Log.d(TAG, "Found valid date matches: ${foundDates.map { it.first }}")

        // Choose the most recent date (can be changed to `firstOrNull` if needed)
        return foundDates.maxByOrNull { it.second }?.first
    }

    /**
     * Attempts to parse a string into a valid Date using known formats.
     * Includes improved century handling for two-digit years.
     */
    private fun parseToDate(dateString: String): Date? {
        // Order matters: put more specific or common formats first.
        // Formats with full month names before abbreviated ones, YYYY before YY, etc.
        val formats = listOf(
            // Full year (4-digit) formats first
            "dd MMMM yyyy", "MMMM dd, yyyy",
            "dd 'of' MMMM yyyy", // For "25 of January 1971"
            "dd MMM yyyy", "MMM dd, yyyy",
            "dd-MMM-yyyy", "MMM-dd-yyyy",

            // Numeric 4-digit year formats
            "dd/MM/yyyy", "MM/dd/yyyy", "dd-MM-yyyy", "MM-dd-yyyy",
            "yyyy-MM-dd",
            "dd.MM.yyyy", "MM.dd.yyyy", // ADDED THIS LINE
            "yyyy/MM/dd", // ADDED THIS LINE for YYYY/MM/DD
            "yyyy.MM.dd", // ADDED THIS LINE for YYYY.MM.DD

            // Two-digit year (yy) formats
            "dd MMMM yy", "MMMM dd, yy",
            "dd 'of' MMMM yy", // For "25 of January 71"
            "dd MMM yy", "MMM dd, yy",
            "dd-MMM-yy", "MMM-dd-yy",

            // Numeric 2-digit year formats
            "dd/MM/yy", "MM/dd/yy", "dd-MM-yy", "MM-dd-yy",
            "yy-MM-dd",
            "dd.MM.yy", "MM.dd.yy", // ADDED THIS LINE

            // Day of week formats (ensure full year variants come before 2-digit)
            "EEEE, MMMM dd, yyyy", // Monday, January 25, 1971
            "E, MMM dd, yyyy",      // Mon, Jan 25, 1971
            "EEEE, MMMM dd, yy",    // Monday, January 25, 71
            "E, MMM dd, yy"         // Mon, Jan 25, 71
        )

        val currentYear = Calendar.getInstance().get(Calendar.YEAR)

        for (format in formats) {
            try {
                val sdf = SimpleDateFormat(format, Locale.ENGLISH)
                sdf.isLenient = false // Strict parsing is good!
                // This is a common point of failure for SimpleDateFormat
                if (format.contains("yy") && !format.contains("yyyy")) {

                    val calendar = Calendar.getInstance()
                    calendar.time = Date() // Set to current time
                    val centuryPivot = calendar.get(Calendar.YEAR) + 10 // E.g., if current 2025, pivot 2035

                    val pivotDate = Calendar.getInstance()
                    pivotDate.set(Calendar.YEAR, centuryPivot)
                    sdf.set2DigitYearStart(pivotDate.time)
                }

                return sdf.parse(dateString)

            } catch (_: Exception) {
            }
        }
        return null
    }

    /**
     * Preprocesses OCR text to fix common issues (like 25% instead of 25th).
     */
    private fun preProcessText(input: String): String {
        return input

            .replace("0f", "of", ignoreCase = true)         // "0f" → "of"
            .replace("@", "a")                              // fix: email noise or text error
            .replace(Regex("(?<=\\d)(st|nd|rd|th)?of(?=[A-Z])", RegexOption.IGNORE_CASE), " of ") // e.g., 25thofJanuary
            .replace(Regex("[^\\p{L}\\p{N}\\s.,/\\-:]"), " ")   // Keep letters, numbers, spaces, and common date punctuation
            .replace(Regex("\\s+"), " ")                    // normalize whitespace
            .trim()
    }
}