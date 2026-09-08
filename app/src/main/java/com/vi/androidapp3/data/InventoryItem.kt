package com.vi.androidapp3.data

/**
 * Represents collectible tools, keys, and story artifacts.
 * Each item holds metadata for UI display, asset resolution, and journal tracking.
 */
enum class InventoryItem(
    val id: String,
    val displayName: String,
    val imageName: String,
    val description: String
) {
    SMALL_SHOVEL(
        "smallShovel",
        "Small Shovel",
        "item_small_shovel",
        "A little metal shovel, perfect for digging through packed snow."
    ),
    GAFF_HOOK(
        "gaffHook",
        "Gaff Hook",
        "item_gaff_hook",
        "A sturdy hooked pole that can snag objects just out of reach."
    ),
    WOODEN_MATCHES(
        "woodenMatches",
        "Wooden Matches",
        "item_wooden_matches",
        "Dry wooden matches sealed in a canister. Useful for melting thick ice."
    ),
    CAFE_LEAD(
        "cafeLead",
        "Snowy Owl Cafe Lead",
        "item_cafe_lead",
        "Scribblings on a napkin point toward the Snowy Owl Cafe in downtown Banff."
    ),
    VINTAGE_BRASS_TOKEN(
        "vintageBrassToken",
        "Vintage Brass Token",
        "item_vintage_brass_token",
        "An old brass token stamped with a worn Banff emblem."
    ),
    OBSERVATORY_STORY_LEAD(
        "observatoryStoryLead",
        "Observatory Story Lead",
        "item_observatory_story_lead",
        "A local research lead pointing toward Sulphur Mountain's old observatory."
    ),
    OBSERVATORY_LOCKER_KEY(
        "observatoryLockerKey",
        "Observatory Locker Key",
        "item_observatory_locker_key",
        "A green key for an old metal locker inside the observatory."
    ),
    RUSTY_CROWBAR(
        "rustyCrowbar",
        "Rusty Crowbar",
        "item_rusty_crowbar",
        "A heavy crowbar, rusted but strong enough to pry open frozen crates."
    ),
    WOODCUTTERS_AXE(
        "woodcuttersAxe",
        "Woodcutter's Axe",
        "item_woodcutters_axe",
        "A sharp woodcutter's axe, heavy enough to break through old boards."
    ),
    OBSERVATORY_JOURNAL_LEAD(
        "observatoryJournalLead",
        "Observatory Journal Entry",
        "item_journal_page",
        "A journal entry pointing toward Tunnel Mountain. Is it referencing Bigfoot?"
    ),
    LOST_LEMON_GOLD_NUGGET(
        "lostLemonGoldNugget",
        "Lost Lemon Gold Nugget",
        "item_lost_lemon_gold_nugget",
        "A massive gold nugget from the legendary Lost Lemon Mine."
    );

    companion object {
        /** Resolves an InventoryItem by its unique string identifier. */
        fun fromId(id: String): InventoryItem? = entries.find { it.id == id }
    }
}