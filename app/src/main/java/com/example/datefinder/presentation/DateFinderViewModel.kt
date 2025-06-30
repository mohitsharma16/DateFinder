package com.example.datefinder.presentation

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.datefinder.data.ocr.OCRProcessor
import com.example.datefinder.domain.DateExtractor
import com.example.datefinder.utils.TTSHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DateFinderUiState(
    val imageUri: Uri? = null,
    val extractedDate: String? = null,
    val isProcessing: Boolean = false,
    val errorMessage: String? = null
)

class DateFinderViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(DateFinderUiState())
    val uiState: StateFlow<DateFinderUiState> = _uiState.asStateFlow()

    companion object {
        private const val TAG = "DateFinderViewModel"
    }

    fun onImageSelected(context: Context, uri: Uri) {
        Log.d(TAG, "Image selected: $uri")

        _uiState.value = _uiState.value.copy(
            imageUri = uri,
            isProcessing = true,
            errorMessage = null,
            extractedDate = null
        )

        viewModelScope.launch {
            try {
                Log.d(TAG, "Starting OCR processing")
                val text = OCRProcessor.extractTextFromImage(context, uri)
                Log.d(TAG, "OCR completed, extracted text: $text")

                val date = DateExtractor.extractMostRelevantDate(text)
                Log.d(TAG, "Date extraction completed: $date")

                _uiState.value = _uiState.value.copy(
                    extractedDate = date,
                    isProcessing = false
                )

                // Announce the result via TTS
                if (date != null) {
                    TTSHelper.speakText(context, "Date found: $date")
                } else {
                    TTSHelper.speakText(context, "No date detected in the image")
                }

            } catch (e: Exception) {
                Log.e(TAG, "Error processing image", e)
                _uiState.value = _uiState.value.copy(
                    isProcessing = false,
                    errorMessage = "Error processing image: ${e.message}"
                )
                TTSHelper.speakText(context, "Error processing image")
            }
        }
    }
    fun repeatLastSpoken(context: Context) {
        TTSHelper.repeatLastSpoken(context)
    }

    override fun onCleared() {
        super.onCleared()
        TTSHelper.shutdown()
    }
}