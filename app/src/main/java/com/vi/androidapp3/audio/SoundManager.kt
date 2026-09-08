package com.vi.androidapp3.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.util.Log

/**
 * Singleton coordinating all game audio:
 * - Low-latency one-shot effects via SoundPool.
 * - Looping environmental tracks via managed MediaPlayer instances.
 */
class SoundManager private constructor(private val context: Context) {

    private val tag = "SoundManager"
    private val soundPool: SoundPool
    private val soundIdMap = mutableMapOf<Int, Int>() // Maps raw resource ID -> SoundPool sample ID
    private val loadedSoundIds = mutableSetOf<Int>()   // Tracks successfully decoded samples
    private val ambiencePlayers = mutableMapOf<Int, MediaPlayer>() // Active looping ambient players

    init {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(10)
            .setAudioAttributes(audioAttributes)
            .build()

        // Track when samples finish asynchronous decoding into memory
        soundPool.setOnLoadCompleteListener { _, sampleId, status ->
            if (status == 0) {
                loadedSoundIds.add(sampleId)
                Log.d(tag, "Loaded sample ID: $sampleId successfully")
            } else {
                Log.e(tag, "Error loading sample ID: $sampleId, status: $status")
            }
        }

        preloadSounds()
    }

    /** Preloads all game sound effects into memory for instant playback. */
    private fun preloadSounds() {
        GameSound.entries.forEach { sound ->
            try {
                val soundId = soundPool.load(context, sound.resId, 1)
                soundIdMap[sound.resId] = soundId
            } catch (e: Exception) {
                Log.e(tag, "Failed preloading sound: ${sound.name}", e)
            }
        }
    }

    /** Plays a short sound effect via SoundPool, falling back to MediaPlayer if still loading. */
    fun play(sound: GameSound, volume: Float = 1.0f) {
        val soundId = soundIdMap[sound.resId]

        if (soundId != null && loadedSoundIds.contains(soundId)) {
            val streamId = soundPool.play(soundId, volume, volume, 1, 0, 1.0f)
            if (streamId != 0) return
        }

        // Fallback: if SoundPool hasn't finished decoding or fails, play directly via MediaPlayer
        try {
            MediaPlayer.create(context, sound.resId)?.apply {
                setVolume(volume, volume)
                setOnCompletionListener { mp -> mp.release() }
                start()
            }
        } catch (e: Exception) {
            Log.e(tag, "Failed fallback play for sound: ${sound.name}", e)
        }
    }

    /** Starts or resumes continuous looping ambient sound for a location. */
    fun playAmbience(ambience: AmbientSound, volume: Float = 0.35f) {
        val key = ambience.resId
        val existing = ambiencePlayers[key]

        if (existing != null) {
            if (!existing.isPlaying) {
                existing.setVolume(volume, volume)
                existing.start()
            }
            return
        }

        try {
            val player = MediaPlayer.create(context, ambience.resId)
            if (player != null) {
                player.isLooping = true
                player.setVolume(volume, volume)
                player.start()
                ambiencePlayers[key] = player
                Log.d(tag, "Started ambience: ${ambience.name}")
            } else {
                Log.e(tag, "MediaPlayer.create returned null for ambience: ${ambience.name}")
            }
        } catch (e: Exception) {
            Log.e(tag, "Failed playing ambience: ${ambience.name}", e)
        }
    }

    /** Stops and releases a specific ambient track when exiting a location. */
    fun stopAmbience(ambience: AmbientSound) {
        ambiencePlayers.remove(ambience.resId)?.let { player ->
            try {
                if (player.isPlaying) {
                    player.stop()
                }
                player.release()
            } catch (e: Exception) {
                Log.e(tag, "Error stopping ambience: ${ambience.name}", e)
            }
        }
    }

    /** Stops and releases all active ambient sound players. */
    fun stopAllAmbience() {
        ambiencePlayers.values.forEach { player ->
            try {
                if (player.isPlaying) {
                    player.stop()
                }
                player.release()
            } catch (e: Exception) {
                Log.e(tag, "Error releasing ambience player", e)
            }
        }
        ambiencePlayers.clear()
    }

    companion object {
        @Volatile
        private var instance: SoundManager? = null

        /** Thread-safe singleton initialization using application context. */
        fun initialize(context: Context) {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        instance = SoundManager(context.applicationContext)
                    }
                }
            }
        }

        /** Global singleton accessor. */
        val shared: SoundManager
            get() = instance ?: error("SoundManager must be initialized before access")
    }
}