//package com.example.datefinder.domain
//
//import android.util.Log
//import java.text.SimpleDateFormat
//import java.util.*
//import java.util.regex.Pattern
//
//object DateExtractor {
//
//    private const val TAG = "DateExtractor"
//
//    // Flexible patterns for various date formats
//    private val datePatterns = listOf(
//        // e.g., 25/01/1971, 01/25/1971 (with 2 or 4 digit years)
//        "\\b(0?[1-9]|[12]\\d|3[01])[\\/\\-](0?[1-9]|1[0-2])[\\/\\-](\\d{2,4})\\b", // DD/MM/YY(YY)
//        "\\b(0?[1-9]|1[0-2])[\\/\\-](0?[1-9]|[12]\\d|3[01])[\\/\\-](\\d{2,4})\\b", // MM/DD/YY(YY)
//
//        // e.g., 1971-01-25 (with 2 or 4 digit years - less common for 2-digit first)
//        "\\b(\\d{4})-(0?[1-9]|1[0-2])-(0?[1-9]|[12]\\d|3[01])\\b", // YYYY-MM-DD
//        "\\b(\\d{2})-(0?[1-9]|1[0-2])-(0?[1-9]|[12]\\d|3[01])\\b", // YY-MM-DD (less common, but possible)
//
//        "\\b(0?[1-9]|[12]\\d|3[01])[\\/\\-](0?[1-9]|1[0-2])[\\/\\-](\\d{2,4})\\b", // DD/MM/YY(YY)
//        "\\b(0?[1-9]|1[0-2])[\\/\\-](0?[1-9]|[12]\\d|3[01])[\\/\\-](\\d{2,4})\\b", // MM/DD/YY(YY)
//
//        // e.g., 1971-01-25
//        "\\b(\\d{4})-(0?[1-9]|1[0-2])-(0?[1-9]|[12]\\d|3[01])\\b", // YYYY-MM-DD
//        "\\b(\\d{2})-(0?[1-9]|1[0-2])-(0?[1-9]|[12]\\d|3[01])\\b", // YY-MM-DD (less common, but possible)
//
//        // e.g., January 25, 1971 or Jan 25, 1971
//        "\\b(Jan(?:uary)?|Feb(?:ruary)?|Mar(?:ch)?|Apr(?:il)?|May|Jun(?:e)?|Jul(?:y)?|Aug(?:ust)?|Sep(?:t(?:ember)?)?|Oct(?:ober)?|Nov(?:ember)?|Dec(?:ember)?)\\.?\\s+(0?[1-9]|[12]\\d|3[01]),?\\s+(\\d{2,4})\\b",
//
//        // e.g., 25 January 1971 or 25 Jan 1971
//        // MODIFIED LINE BELOW: Added (?:\\s+of)?
//        "\\b(0?[1-9]|[12]\\d|3[01])(?:\\s+of)?\\s+(Jan(?:uary)?|Feb(?:ruary)?|Mar(?:ch)?|Apr(?:il)?|May|Jun(?:e)?|Jul(?:y)?|Aug(?:ust)?|Sep(?:t(?:ember)?)?|Oct(?:ober)?|Nov(?:ember)?|Dec(?:ember)?)\\.?\\s+(\\d{2,4})\\b",
//
//        // e.g., 25-Jan-71 or 25 Jan 71 (common in some documents)
//        "\\b(0?[1-9]|[12]\\d|3[01])[\\- ](Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)[\\- ](\\d{2,4})\\b",
//
//        // Adding patterns with day of the week (optional, but good for robust recognition)
//        // e.g., Mon, Jan 25, 1971
//        "\\b(?:Mon|Tue|Wed|Thu|Fri|Sat|Sun)\\.?\\s*," +
//                "\\s*(Jan(?:uary)?|Feb(?:ruary)?|Mar(?:ch)?|Apr(?:il)?|May|Jun(?:e)?|Jul(?:y)?|Aug(?:ust)?|Sep(?:t(?:ember)?)?|Oct(?:ober)?|Nov(?:ember)?|Dec(?:ember)?)\\.?\\s+" +
//                "(0?[1-9]|[12]\\d|3[01]),?\\s+(\\d{2,4})\\b",
//        // e.g., Monday, January 25, 1971
//        "\\b(?:Monday|Tuesday|Wednesday|Thursday|Friday|Saturday|Sunday),\\s*" +
//                "(Jan(?:uary)?|Feb(?:ruary)?|Mar(?:ch)?|Apr(?:il)?|May|Jun(?:e)?|Jul(?:y)?|Aug(?:ust)?|Sep(?:t(?:ember)?)?|Oct(?:ober)?|Nov(?:ember)?|Dec(?:ember)?)\\s+" +
//                "(0?[1-9]|[12]\\d|3[01]),?\\s+(\\d{4})\\b", // Full year for full day names
//
//        // e.g., January 25, 1971 or Jan 25, 1971 (with 2 or 4 digit years)
//        "\\b(Jan(?:uary)?|Feb(?:ruary)?|Mar(?:ch)?|Apr(?:il)?|May|Jun(?:e)?|Jul(?:y)?|Aug(?:ust)?|Sep(?:t(?:ember)?)?|Oct(?:ober)?|Nov(?:ember)?|Dec(?:ember)?)\\.?\\s+(0?[1-9]|[12]\\d|3[01]),?\\s+(\\d{2,4})\\b",
//
//        // e.g., 25 January 1971 or 25 Jan 1971 (with 2 or 4 digit years)
//        "\\b(0?[1-9]|[12]\\d|3[01])\\s+(Jan(?:uary)?|Feb(?:ruary)?|Mar(?:ch)?|Apr(?:il)?|May|Jun(?:e)?|Jul(?:y)?|Aug(?:ust)?|Sep(?:t(?:ember)?)?|Oct(?:ober)?|Nov(?:ember)?|Dec(?:ember)?)\\.?\\s+(\\d{2,4})\\b",
//
//        // e.g., 25-Jan-71 or 25 Jan 71 (common in some documents)
//        "\\b(0?[1-9]|[12]\\d|3[01])[\\- ](Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)[\\- ](\\d{2,4})\\b",
//
//        // Adding patterns with day of the week (optional, but good for robust recognition)
//        // e.g., Mon, Jan 25, 1971
//        "\\b(?:Mon|Tue|Wed|Thu|Fri|Sat|Sun)\\.?\\s*," +
//                "\\s*(Jan(?:uary)?|Feb(?:ruary)?|Mar(?:ch)?|Apr(?:il)?|May|Jun(?:e)?|Jul(?:y)?|Aug(?:ust)?|Sep(?:t(?:ember)?)?|Oct(?:ober)?|Nov(?:ember)?|Dec(?:ember)?)\\.?\\s+" +
//                "(0?[1-9]|[12]\\d|3[01]),?\\s+(\\d{2,4})\\b",
//        // e.g., Monday, January 25, 1971
//        "\\b(?:Monday|Tuesday|Wednesday|Thursday|Friday|Saturday|Sunday),\\s*" +
//                "(Jan(?:uary)?|Feb(?:ruary)?|Mar(?:ch)?|Apr(?:il)?|May|Jun(?:e)?|Jul(?:y)?|Aug(?:ust)?|Sep(?:t(?:ember)?)?|Oct(?:ober)?|Nov(?:ember)?|Dec(?:ember)?)\\s+" +
//                "(0?[1-9]|[12]\\d|3[01]),?\\s+(\\d{4})\\b" // Full year for full day names
//    )
//
//    /**
//     * Extracts all valid dates from a given block of OCR text.
//     */
//    fun extractAllDates(text: String): List<String> {
//        Log.d(TAG, "Raw OCR text:\n$text")
//
//        val cleanedText = preProcessText(text)
//        Log.d(TAG, "Normalized text:\n$cleanedText")
//
//        val foundDates = mutableListOf<String>()
//
//        for (pattern in datePatterns) {
//            val regex = pattern.toRegex(RegexOption.IGNORE_CASE)
//            val matches = regex.findAll(cleanedText)
//            for (match in matches) {
//                val rawDate = match.value
//                // Only add if it can be successfully parsed into a Date object
//                parseToDate(rawDate)?.let {
//                    foundDates.add(rawDate)
//                }
//            }
//        }
//        Log.d(TAG, "Found valid date matches: $foundDates")
//        return foundDates.distinct() // Remove duplicates if the same date matches multiple patterns
//    }
//
//    /**
//     * Extracts the most relevant date from a given block of OCR text.
//     */
//    fun extractMostRelevantDate(text: String): String? {
//        val allDates = extractAllDates(text)
//        return allDates.mapNotNull { rawDate ->
//            parseToDate(rawDate)?.let { parsedDate ->
//                rawDate to parsedDate
//            }
//        }.maxByOrNull { it.second }?.first // Still takes the most recent
//    }
//
//    /**
//     * Attempts to parse a string into a valid Date using known formats.
//     */
//    private fun parseToDate(dateString: String): Date? {
//        val formats = listOf(
//            // Numeric dates
//            "dd/MM/yyyy", "MM/dd/yyyy", "dd-MM-yyyy", "MM-dd-yyyy",
//            "dd/MM/yy",   "MM/dd/yy",   "dd-MM-yy",   "MM-dd/yy", // Two-digit years
//            "yyyy-MM-dd", "yy-MM-dd", // Two-digit year first (less common but possible)
//
//            // Month name dates (full month name)
//            "MMMM dd, yyyy", "dd MMMM yyyy", // Jan 25, 1971 | 25 Jan 1971
//            "MMMM dd, yy",   "dd MMMM yy",   // Jan 25, 71 | 25 Jan 71
//
//            // Abbreviated month name dates
//            "MMM dd, yyyy", "dd MMM yyyy", // Jan 25, 1971 | 25 Jan 1971 (without dot)
//            "MMM dd, yy",   "dd MMM yy",   // Jan 25, 71 | 25 Jan 71
//            "MMM. dd, yyyy", "dd MMM. yyyy", // Jan. 25, 1971 | 25 Jan. 1971 (with dot)
//            "MMM. dd, yy",   "dd MMM. yy",   // Jan. 25, 71 | 25 Jan. 71
//
//            // Dates with hyphens for abbreviated month names
//            "dd-MMM-yyyy", "dd-MMM-yy",
//
//            // Dates with day of the week (full and abbreviated)
//            "E, MMM dd, yyyy",   // Mon, Jan 25, 1971
//            "E, MMM dd, yy",     // Mon, Jan 25, 71
//            "EEEE, MMMM dd, yyyy", // Monday, January 25, 1971
//
//            // New format for "DD of Month YYYY"
//            "dd 'of' MMMM yyyy", // e.g., 25 of January 1971
//            "dd 'of' MMM yyyy",  // e.g., 25 of Jan 1971
//            "dd 'of' MMMM yy",   // e.g., 25 of January 71
//            "dd 'of' MMM yy",    // e.g., 25 of Jan 71
//        )
//
//        for (format in formats) {
//            try {
//                val sdf = SimpleDateFormat(format, Locale.ENGLISH)
//                sdf.isLenient = false
//                // Special handling for two-digit years (yy) to ensure correct century
//                // This is crucial for "71" being 1971 and not 2071 if the current year is 2025.
//                // Adjust the century to be within a reasonable range (e.g., 80 years back, 20 years forward)
//                if (format.contains("yy") && !format.contains("yyyy")) {
//                    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
//                    val twoDigitYear = dateString.substring(dateString.length - 2).toIntOrNull()
//                    if (twoDigitYear != null) {
//                        val calendar = Calendar.getInstance()
//                        calendar.time = sdf.parse(dateString)!!
//                        var parsedYear = calendar.get(Calendar.YEAR)
//
//                        // If parsed year is in the future too much (e.g., 71 interpreted as 2071 when it should be 1971)
//                        // This heuristic assumes dates are usually in the recent past or near future.
//                        if (parsedYear > currentYear + 10) { // e.g., 2071 vs 2025
//                            parsedYear -= 100
//                        } else if (parsedYear < currentYear - 80) { // e.g., 1900 vs 2025, if 00 is interpreted as 1900 instead of 2000
//                            parsedYear += 100
//                        }
//                        calendar.set(Calendar.YEAR, parsedYear)
//                        return calendar.time
//                    }
//                }
//                return sdf.parse(dateString)
//            } catch (e: Exception) {
//                // Log.d(TAG, "Failed to parse '$dateString' with format '$format': ${e.message}") // For debugging
//                // Try next format
//            }
//        }
//        return null
//    }
//
//    /**
//     * Preprocesses OCR text to fix common issues (like 25% instead of 25th).
//     */
//    private fun preProcessText(input: String): String {
//        return input
//            // Targeted replacement for "25%" to "25th" only when followed by a potential date component
//            // or as a standalone number. Be cautious. Might be better to remove non-numeric chars
//            // and let date patterns handle the pure numbers.
//            // For now, let's make it simpler if not strictly needed.
//            // .replace(Regex("(?<=\\d)%(?=\\s*\\d|\\s*[A-Za-z])"), "th") // More targeted, but still risky
//            .replace("0f", "of", ignoreCase = true)         // "0f" → "of"
//            .replace("@", "a")                              // fix: email noise or text error
//            .replace(Regex("(?<=\\d)(st|nd|rd|th)?of(?=[A-Z])", RegexOption.IGNORE_CASE), " of ") // e.g., 25thofJanuary
//            .replace(Regex("[^\\p{L}\\p{N}\\s.,/\\-:]"), " ") // Keep letters, numbers, spaces, and common date punctuation
//            .replace(Regex("\\s+"), " ")                    // normalize whitespace
//            .trim()
//    }
//}
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

                // Special handling for two-digit years (yy) to ensure correct century
                // This is a common point of failure for SimpleDateFormat
                if (format.contains("yy") && !format.contains("yyyy")) {
                    // Set the 100-year window for two-digit year parsing
                    // This determines the pivot year for 2-digit years.
                    // For example, if pivot is 2050, then 71 -> 1971, 25 -> 2025
                    // Let's set it dynamically based on the current year
                    val calendar = Calendar.getInstance()
                    calendar.time = Date() // Set to current time
                    val centuryPivot = calendar.get(Calendar.YEAR) + 10 // E.g., if current 2025, pivot 2035

                    // SimpleDateFormat's set2DigitYearStart is inclusive.
                    // If current year is 2025 and you want 71 to be 1971,
                    // and 25 to be 2025, you might set the pivot to current year + some offset (e.g., 10-20 years in future)
                    // If your dates are mostly historical, you might set it further back.
                    val pivotDate = Calendar.getInstance()
                    pivotDate.set(Calendar.YEAR, centuryPivot)
                    sdf.set2DigitYearStart(pivotDate.time)
                }

                return sdf.parse(dateString)

            } catch (_: Exception) {
                // Log.d(TAG, "Failed to parse '$dateString' with format '$format': ${e.message}") // For debugging
                // Try next format
            }
        }
        return null
    }

    /**
     * Preprocesses OCR text to fix common issues (like 25% instead of 25th).
     */
    private fun preProcessText(input: String): String {
        return input
            // Keep the '%' replacement if it's genuinely an OCR error leading to dates.
            // Be cautious, as "25%" is not "25th". If it's a common OCR artifact, keep it.
            // If the OCR gives "25" and you want to ensure the regex handles it, ensure patterns like "\\b\\d{1,2}\\b" are available.
            // For now, removing the generic % to "th" as it might corrupt non-date text.
            // .replace("%", "th") // Removed to prevent incorrect transformations.

            .replace("0f", "of", ignoreCase = true)         // "0f" → "of"
            .replace("@", "a")                              // fix: email noise or text error
            .replace(Regex("(?<=\\d)(st|nd|rd|th)?of(?=[A-Z])", RegexOption.IGNORE_CASE), " of ") // e.g., 25thofJanuary
            .replace(Regex("[^\\p{L}\\p{N}\\s.,/\\-:]"), " ")   // Keep letters, numbers, spaces, and common date punctuation
            .replace(Regex("\\s+"), " ")                    // normalize whitespace
            .trim()
    }
}