package com.vi.androidapp3.data

/**
 * Progression gates categorizing investigation milestones in the museum journal.
 */
enum class LocationLeadPhase(val rawValue: Int, val title: String, val lockedMessage: String) {
    FIRST(1, "The Foundations", ""),
    SECOND(2, "The Ascent", "Find usable items from the first three leads."),
    THIRD(3, "The Deep Wilderness", "Discover the observatory's secrets and retrieve the crowbar."),
    FINAL(4, "The Final Trail", "Recover the woodcutter's axe from the ice.")
}

/**
 * Encapsulates field notes, lore teasers, and lock states displayed on the museum map desk.
 */
data class LocationLead(
    val id: String,
    val title: String,
    val subtitle: String,
    val publicMystery: String,
    val curatorNote: String,
    val location: LocationId,
    val phase: LocationLeadPhase,
    val iconName: String
) {
    companion object {
        /** Master registry of all investigable trail leads across the park. */
        val all: List<LocationLead> = listOf(
            LocationLead(
                id = "banff_springs_hotel",
                title = "Banff Springs Hotel",
                subtitle = "Ghosts in the grand old halls.",
                publicMystery = "The hotel has no shortage of ghost stories, but one old tag points somewhere more ordinary — and more useful.",
                curatorNote = "Ghost stories are useful. They let witnesses tell the truth without being believed.",
                location = LocationId.BANFF_SPRINGS_HOTEL,
                phase = LocationLeadPhase.FIRST,
                iconName = "building_columns"
            ),
            LocationLead(
                id = "cave_and_basin",
                title = "Cave and Basin",
                subtitle = "Steam, stone, and old beginnings.",
                publicMystery = "Banff's origins are tied to mineral springs, old claims, and strange stories of warm caves in winter.",
                curatorNote = "Start where Banff began. Places of origin have a way of remembering more than people do.",
                location = LocationId.CAVE_AND_BASIN,
                phase = LocationLeadPhase.FIRST,
                iconName = "flame"
            ),
            LocationLead(
                id = "bow_falls",
                title = "Bow Falls",
                subtitle = "Winter thunder beneath the ice.",
                publicMystery = "The frozen falls hide old routes, strong wood, and something buried beneath the snow.",
                curatorNote = "Most people hear water. Listen for what answers it.",
                location = LocationId.BOW_FALLS,
                phase = LocationLeadPhase.FIRST,
                iconName = "water_waves"
            ),
            LocationLead(
                id = "hot_springs",
                title = "Upper Hot Springs",
                subtitle = "Warm refuge in a frozen world.",
                publicMystery = "The steaming pools hide more than just warmth. Signs point to a researcher interested in local tokens.",
                curatorNote = "Some say the hot springs hold the secrets of the earth. Others just want a good soak.",
                location = LocationId.HOT_SPRINGS,
                phase = LocationLeadPhase.FIRST,
                iconName = "thermometer"
            ),
            LocationLead(
                id = "downtown_banff",
                title = "Downtown Banff",
                subtitle = "A warm cafe and a local researcher.",
                publicMystery = "A lead from the hot springs points to the Snowy Owl Cafe, where a researcher may know more about the observatory.",
                curatorNote = "A story often turns on the smallest exchange. Seek out who knows the town's older secrets.",
                location = LocationId.DOWNTOWN_BANFF,
                phase = LocationLeadPhase.FIRST,
                iconName = "cup"
            ),
            LocationLead(
                id = "sulphur_mountain",
                title = "Sulphur Mountain Summit",
                subtitle = "A view from above the pattern.",
                publicMystery = "From the summit, the observatory beckons. Its door may be iced shut, but perhaps a warm clue awaits.",
                curatorNote = "When the pieces refuse to connect, climb higher.",
                location = LocationId.SULPHUR_MOUNTAIN,
                phase = LocationLeadPhase.FIRST,
                iconName = "mountain"
            ),
            LocationLead(
                id = "observatory",
                title = "Observatory",
                subtitle = "Records from the summit.",
                publicMystery = "Dusty logbooks and locked cabinets may contain clues about who, or what, watches the wilderness.",
                curatorNote = "The highest points often hold the oldest truths.",
                location = LocationId.OBSERVATORY,
                phase = LocationLeadPhase.FIRST,
                iconName = "telescope"
            ),
            LocationLead(
                id = "lake_minnewanka",
                title = "Lake Minnewanka",
                subtitle = "A drowned town beneath the ice.",
                publicMystery = "A frozen crate near the lake may contain something strong enough for the final trail.",
                curatorNote = "Some places are buried by water. Some by silence.",
                location = LocationId.LAKE_MINNEWANKA,
                phase = LocationLeadPhase.FIRST,
                iconName = "snowflake"
            ),
            LocationLead(
                id = "tunnel_mountain",
                title = "Tunnel Mountain",
                subtitle = "Tracks between town and timber.",
                publicMystery = "Large winter tracks have been reported near the trails, usually dismissed as bears, boots, or melting snow.",
                curatorNote = "A trail is a sentence written by something that passed through.",
                location = LocationId.TUNNEL_MOUNTAIN,
                phase = LocationLeadPhase.FIRST,
                iconName = "pawprint"
            )
        )
    }
}