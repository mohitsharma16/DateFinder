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
    private var lastSpokenText: String? = null
    private val pendingTexts = mutableListOf<String>()

    fun speakText(context: Context, text: String, forceSpeak: Boolean = false) {
        Log.d(TAG, "Request to speak: $text | forceSpeak=$forceSpeak")
        lastSpokenText = text

        if (tts == null || !isInitialized) {
            initializeTTS(context) {
                speakText(context, text, forceSpeak)
            }
            return
        }

        if (isSpeaking()) {
            if (forceSpeak) {
                stop()
                Log.d(TAG, "Stopped current speech to force speak new text")
            } else {
                Log.d(TAG, "Already speaking. Skipping new speech.")
                return
            }
        }
        performSpeech(context, text)
    }

    fun repeatLastSpoken(context: Context) {
        lastSpokenText?.let {
            speakText(context, it, forceSpeak = true)
        } ?: Log.w(TAG, "No text available to repeat")
    }

    private fun initializeTTS(context: Context, onReady: (() -> Unit)? = null) {
        tts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                Log.d(TAG, "TTS initialized successfully")
                isInitialized = true

                val langResult = tts?.setLanguage(Locale.getDefault())
                if (langResult == TextToSpeech.LANG_MISSING_DATA ||
                    langResult == TextToSpeech.LANG_NOT_SUPPORTED
                ) {
                    Log.w(TAG, "Language not supported, using English")
                    tts?.language = Locale.ENGLISH
                }

                tts?.setSpeechRate(1.0f)
                tts?.setPitch(1.0f)

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

                onReady?.invoke()

            } else {
                Log.e(TAG, "TTS initialization failed")
                isInitialized = false
            }
        }
    }

    private fun performSpeech(context: Context, text: String) {
        if (!isInitialized || tts == null) {
            Log.w(TAG, "TTS not initialized during performSpeech. Reinitializing...")
            initializeTTS(context) {
                speakText(context, text)
            }
            return
        }

        val utteranceId = "tts_${System.currentTimeMillis()}"
        val result = tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)

        if (result == TextToSpeech.ERROR) {
            Log.e(TAG, "Error in speaking text. Reinitializing...")
            isInitialized = false
            initializeTTS(context) {
                speakText(context, text)
            }
        }
    }

    fun isSpeaking(): Boolean {
        val speaking = tts?.isSpeaking ?: false
        Log.d(TAG, "isSpeaking() = $speaking")
        return speaking
    }

    fun stop() {
        Log.d(TAG, "Stopping TTS")
        tts?.stop()
    }

    fun shutdown() {
        Log.d(TAG, "Shutting down TTS")
        tts?.stop()
        tts?.shutdown()
        tts = null
        isInitialized = false
        pendingTexts.clear()
    }
}