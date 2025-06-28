package com.example.datefinder.utils

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import java.util.*

object TTSHelper {

    private const val TAG = "TTSHelper"
    private var tts: TextToSpeech? = null
    private var isInitialized = false
    private val pendingTexts = mutableListOf<String>()

    fun speakText(context: Context, text: String) {
        Log.d(TAG, "Speaking text: $text")

        if (tts == null) {
            initializeTTS(context)
        }

        if (isInitialized) {
            performSpeech(text)
        } else {
            // Queue the text to be spoken once TTS is initialized
            pendingTexts.add(text)
        }
    }

    private fun initializeTTS(context: Context) {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                Log.d(TAG, "TTS initialized successfully")
                isInitialized = true

                // Set language
                val langResult = tts?.setLanguage(Locale.getDefault())
                if (langResult == TextToSpeech.LANG_MISSING_DATA ||
                    langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.w(TAG, "Language not supported, using English")
                    tts?.setLanguage(Locale.ENGLISH)
                }

                // Set speech rate and pitch
                tts?.setSpeechRate(1.0f)
                tts?.setPitch(1.0f)

                // Set utterance progress listener
                tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        Log.d(TAG, "Started speaking: $utteranceId")
                    }

                    override fun onDone(utteranceId: String?) {
                        Log.d(TAG, "Finished speaking: $utteranceId")
                    }

                    override fun onError(utteranceId: String?) {
                        Log.e(TAG, "Error speaking: $utteranceId")
                    }
                })

                // Speak any pending texts
                pendingTexts.forEach { performSpeech(it) }
                pendingTexts.clear()

            } else {
                Log.e(TAG, "TTS initialization failed")
                isInitialized = false
            }
        }
    }

    private fun performSpeech(text: String) {
        val utteranceId = "tts_${System.currentTimeMillis()}"
        val result = tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)

        if (result == TextToSpeech.ERROR) {
            Log.e(TAG, "Error in speaking text")
        }
    }

    fun shutdown() {
        Log.d(TAG, "Shutting down TTS")
        tts?.stop()
        tts?.shutdown()
        tts = null
        isInitialized = false
        pendingTexts.clear()
    }

    fun isSpeaking(): Boolean {
        return tts?.isSpeaking ?: false
    }

    fun stop() {
        tts?.stop()
    }
}