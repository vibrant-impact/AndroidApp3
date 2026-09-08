package com.vi.androidapp3.audio

import androidx.annotation.RawRes
import com.vi.androidapp3.R

/**
 * Continuous looping ambient soundscapes mapped to raw audio resources.
 * Managed primarily through MediaPlayer in SoundManager.
 */
enum class AmbientSound(@RawRes val resId: Int) {
    SNOWY_EXTERIOR(R.raw.ambience_snowy_exterior),
    TOWN_STREET(R.raw.ambience_town_street),
    CAVE_DRIP(R.raw.ambience_cave_drip),
    FIREPLACE(R.raw.ambience_fireplace),
    WIND(R.raw.wind)
}