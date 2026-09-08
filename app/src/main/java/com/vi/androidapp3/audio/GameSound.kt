package com.vi.androidapp3.audio

import androidx.annotation.RawRes
import com.vi.androidapp3.R

/**
 * Short, one-shot sound effects for UI interactions, environmental cues,
 * and game state events. Preloaded and played via SoundPool.
 */
enum class GameSound(@RawRes val resId: Int) {
    // UI and camera interactions
    TAP(R.raw.tap),
    CLOSE(R.raw.close),
    CLICK(R.raw.click),
    CAMERA_SHUTTER(R.raw.camera_shutter),
    CAMERA_FLASH(R.raw.camera_flash),

    // Inventory and environmental puzzle interactions
    ITEM_COLLECT(R.raw.item_collect),
    DOOR_LOCKED(R.raw.door_locked),
    DOOR_UNLOCK(R.raw.door_unlock),
    ICE_CRACK(R.raw.ice_crack),
    ICICLE_CRASH(R.raw.icicle_crash),

    // Narrative event and transition triggers
    BLACKOUT_RUMBLE(R.raw.blackout_rumble),
    LAIR_WAKEUP(R.raw.lair_wakeup),
    LOCATION_TRAVEL(R.raw.return_travel),
    LOCATION_TRAVEL_TINKLE(R.raw.location_travel_tinkle),
    CHOP_WOOD(R.raw.axe_chop),
    NUGGET_REVEAL(R.raw.nugget_reveal),
    CURATOR_SUCCESS(R.raw.curator_success),
    CURATOR_WRONG(R.raw.curator_wrong),
    WOOSH(R.raw.woosh)
}