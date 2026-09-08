package com.vi.androidapp3.data

/**
 * Represents an in-game collectible photograph, including its historical context
 * and the secret anagram letter revealed upon capture.
 */
data class Photo(
    val id: String,
    val location: LocationId,
    val photoName: String,
    val historicalNote: String,
    val secretLetter: String,
    val cameraImageName: String
) {
    companion object {
        val museumExterior = Photo(
            id = "photo_museum_bigfoot_footprint",
            location = LocationId.MUSEUM_EXTERIOR,
            photoName = "Mysterious Massive Footprint",
            historicalNote = "Legends surrounding Bigfoot in the Canadian Rockies trace back centuries, with Indigenous communities sharing rich oral traditions about powerful wilderness guardians — beings to be respected rather than hunted.",
            secretLetter = "A",
            cameraImageName = "camera_museum_exterior_footprint"
        )

        val bowFalls = Photo(
            id = "photo_bow_falls_douglas_fir",
            location = LocationId.BOW_FALLS,
            photoName = "Douglas Fir Trees",
            historicalNote = "The Stoney Nakoda name for the river is Mînî Thnî Wapta, meaning Cold Water River. The area surrounding Bow Falls was highly prized for gathering Douglas fir wood, valued for its flexibility and strength in crafting hunting bows.",
            secretLetter = "S",
            cameraImageName = "camera_bow_falls_douglas_fir"
        )

        val caveAndBasin = Photo(
            id = "photo_cave_and_basin_vent",
            location = LocationId.CAVE_AND_BASIN,
            photoName = "Cave and Basin Vent Hole",
            historicalNote = "Before this area became part of a national park, local Indigenous groups considered these thermal waters a sacred place of peace and healing. In 1883, three CPR rail workers descended through this ceiling vent, sparking a gold-rush-style battle for ownership.",
            secretLetter = "T",
            cameraImageName = "camera_cave_and_basin_vent"
        )

        val banffSpringsHotel = Photo(
            id = "photo_banff_springs_hotel_ghost_bride",
            location = LocationId.BANFF_SPRINGS_HOTEL,
            photoName = "Ghost Bride in the Window",
            historicalNote = "In the early 1930s, a young bride reportedly tripped on the hotel's grand staircase after her gown caught fire from nearby candlelight. Hotel staff and visitors have since reported seeing her spectral figure dancing alone in the mountain-view ballroom.",
            secretLetter = "C",
            cameraImageName = "camera_banff_springs_ghost_bride"
        )

        val downtownBanff = Photo(
            id = "photo_downtown_bigfoot_ice_sculpture",
            location = LocationId.DOWNTOWN_BANFF,
            photoName = "Bigfoot Ice Sculpture",
            historicalNote = "Banff's ice sculpture history spans over a century, tracing back to the first official Banff Winter Carnival in 1917, which featured ice sculptures, masquerade balls, and skijoring.",
            secretLetter = "Q",
            cameraImageName = "camera_downtown_bigfoot_ice_sculpture"
        )

        val hotSprings = Photo(
            id = "photo_hot_springs_marilyn_monroe",
            location = LocationId.HOT_SPRINGS,
            photoName = "Marilyn Monroe at the Hot Springs",
            historicalNote = "When the Upper Hot Springs officially opened in 1886, guests arrived by horse-drawn carriage. The mineral waters, rich in sulphate, calcium, and magnesium, were promoted as a medical cure for ailments ranging from rheumatism to winter fatigue.",
            secretLetter = "H",
            cameraImageName = "camera_hot_springs_marilyn"
        )

        val sulphurMountain = Photo(
            id = "photo_sulphur_mountain_banff_town",
            location = LocationId.SULPHUR_MOUNTAIN,
            photoName = "Town of Banff from Up High",
            historicalNote = "From Sulphur Mountain, Banff looks small beneath the vast sweep of the Bow Valley. Norman Sanson climbed this mountain more than 1,000 times to record weather observations, watching the town, trails, and weather patterns from above — exactly the kind of view needed to see what others missed.",
            secretLetter = "U",
            cameraImageName = "camera_sulphur_mountain_banff_view"
        )

        val lakeMinnewanka = Photo(
            id = "photo_lake_minnewanka_underwater_town",
            location = LocationId.LAKE_MINNEWANKA,
            photoName = "Minnewanka Landing",
            historicalNote = "Deep beneath the frozen surface lies Minnewanka Landing, once a booming 1880s summer resort town complete with hotels and wharves. It was swallowed by the lake in 1941 when a hydroelectric dam raised the water level by 64 feet.",
            secretLetter = "S",
            cameraImageName = "camera_lake_minnewanka_underwater_town"
        )

        val tunnelMountain = Photo(
            id = "photo_tunnel_mountain_snowy_owl",
            location = LocationId.TUNNEL_MOUNTAIN,
            photoName = "Snowy Owl",
            historicalNote = "Snowy owls are powerful Arctic hunters, famous for their pale feathers and silent flight. They sometimes travel far south during winter irruptions, appearing like rare messengers from the North. Seeing one watch the trail near Tunnel Mountain feels less like chance and more like fate.",
            secretLetter = "A",
            cameraImageName = "camera_tunnel_mountain_snowy_owl"
        )

        /** Complete collection of discoverable photos in the game. */
        val all: List<Photo> = listOf(
            museumExterior,
            bowFalls,
            caveAndBasin,
            banffSpringsHotel,
            downtownBanff,
            hotSprings,
            sulphurMountain,
            lakeMinnewanka,
            tunnelMountain
        )

        /** Retrieves the photo objective associated with a specific location. */
        fun photo(forLocation: LocationId): Photo? = all.find { it.location == forLocation }
    }
}