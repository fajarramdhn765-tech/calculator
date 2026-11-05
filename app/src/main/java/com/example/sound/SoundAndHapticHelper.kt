package com.example.sound

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.HapticFeedbackConstants
import android.view.View
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

enum class SoundType {
    KEY_PRESS,
    OPERATOR_PRESS,
    ACTION_CLEAR,
    EQUALS,
    ERROR
}

class SoundAndHapticHelper(private val context: Context) {
    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    private val audioScope = CoroutineScope(Dispatchers.Default)

    fun playSound(type: SoundType, enabled: Boolean) {
        if (!enabled) return
        audioScope.launch {
            try {
                when (type) {
                    SoundType.KEY_PRESS -> playTone(950, 16, 0.28)
                    SoundType.OPERATOR_PRESS -> playTone(1250, 20, 0.32)
                    SoundType.ACTION_CLEAR -> playTone(650, 24, 0.35)
                    SoundType.EQUALS -> playChime()
                    SoundType.ERROR -> playTone(320, 50, 0.40)
                }
            } catch (_: Exception) {
                // Ignore audio error gracefully
            }
        }
    }

    fun triggerHaptic(view: View?, enabled: Boolean) {
        if (!enabled) return
        try {
            if (view != null) {
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            } else if (vibrator != null && vibrator.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(18, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(18)
                }
            }
        } catch (_: Exception) {
            // Ignore vibration error gracefully
        }
    }

    private fun playTone(freqHz: Int, durationMs: Int, volume: Double) {
        val sampleRate = 22050
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val buffer = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            // Smooth attack and release envelope to prevent audible clicks
            val envelope = when {
                i < numSamples * 0.15 -> i / (numSamples * 0.15)
                i > numSamples * 0.65 -> (numSamples - i) / (numSamples * 0.35)
                else -> 1.0
            }
            val angle = 2.0 * PI * i / (sampleRate.toDouble() / freqHz)
            buffer[i] = (sin(angle) * Short.MAX_VALUE * volume * envelope).toInt().toShort()
        }

        val audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(buffer.size * 2)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()

        audioTrack.write(buffer, 0, buffer.size)
        audioTrack.play()
        Thread.sleep(durationMs.toLong() + 10)
        audioTrack.release()
    }

    private fun playChime() {
        // Dual futuristic harmonic chime for equals
        playTone(1050, 22, 0.3)
        playTone(1580, 30, 0.35)
    }
}
