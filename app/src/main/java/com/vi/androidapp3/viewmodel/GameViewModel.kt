package com.vi.androidapp3.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.vi.androidapp3.data.InventoryItem
import com.vi.androidapp3.data.LocationId
import com.vi.androidapp3.data.LocationLead
import com.vi.androidapp3.data.Photo

/**
 * Central ViewModel managing game state, quest progressions, inventory ownership,
 * journal photo collections, and location unlock logic across the entire app.
 */
class GameViewModel : ViewModel() {

    // MARK: - App State
    var hasStartedGame by mutableStateOf(false)
    var currentLocation by mutableStateOf(LocationId.MUSEUM_EXTERIOR)

    // MARK: - Museum Exterior
    var hasOpenedMailbox by mutableStateOf(false)
    var hasReadMailboxNote by mutableStateOf(false)
    var hasCollectedShovel by mutableStateOf(false)
    var isMuseumDoorUnlocked by mutableStateOf(false)
    var hasSeenReturnFromLairMessage by mutableStateOf(false)

    // MARK: - Bow Falls
    var hasCollectedGaffHook by mutableStateOf(false)
    var hasOpenedFallsCanister by mutableStateOf(false)
    var hasCollectedWoodenMatches by mutableStateOf(false)

    // MARK: - Cave and Basin
    var hasOpenedBasinChest by mutableStateOf(false)
    var hasCollectedVintageBrassToken by mutableStateOf(false)

    // MARK: - Banff Springs Hotel
    var hasPhotographedGhostBride by mutableStateOf(false)
    var hasFoundCafeLead by mutableStateOf(false)

    // MARK: - Downtown Banff
    var hasEnteredSnowyOwlCafe by mutableStateOf(false)
    var hasTradedVintageBrassToken by mutableStateOf(false)
    var hasCollectedObservatoryStoryLead by mutableStateOf(false)

    // MARK: - Upper Hot Springs
    var hasCollectedObservatoryLockerKey by mutableStateOf(false)

    // MARK: - Sulphur Mountain
    var hasMeltedWeatherStationDoorIce by mutableStateOf(false)

    // MARK: - Observatory Interior
    var hasReadObservatoryLogbook by mutableStateOf(false)
    var hasCollectedObservatoryJournalLead by mutableStateOf(false)
    var hasOpenedObservatoryLocker by mutableStateOf(false)
    var hasCollectedRustyCrowbar by mutableStateOf(false)

    // MARK: - Lake Minnewanka
    var hasOpenedMinnewankaCrate by mutableStateOf(false)
    var hasCollectedWoodcuttersAxe by mutableStateOf(false)

    // MARK: - Tunnel Mountain
    var hasBrokenCaveEntranceBoards by mutableStateOf(false)
    var hasTriggeredIcicleFall by mutableStateOf(false)

    // MARK: - Bigfoot's Lair
    var hasWokenInBigfootLair by mutableStateOf(false)
    var hasInspectedLairExit by mutableStateOf(false)
    var hasMetBigfootFamily by mutableStateOf(false)
    var hasInspectedLostLemonMine by mutableStateOf(false)
    var hasTakenBigfootEvidencePhoto by mutableStateOf(false)
    var hasEscapedBigfootLair by mutableStateOf(false)
    var hasReturnedFromBigfootLair by mutableStateOf(false)
    var hasFoundGoldNuggetInPocket by mutableStateOf(false)
    var hasSpokenToCuratorAfterLair by mutableStateOf(false)
    var shouldShowMuseumWakeUpAfterLair by mutableStateOf(false)

    // MARK: - Inventory
    var collectedInventoryItemIDs by mutableStateOf(setOf<String>())
        private set
    var usedInventoryItemIDs by mutableStateOf(setOf<String>())
        private set

    val inventory: List<InventoryItem>
        get() = InventoryItem.entries.filter { item ->
            collectedInventoryItemIDs.contains(item.id) && !usedInventoryItemIDs.contains(item.id)
        }

    val hasCompletedRequiredLairInteractions: Boolean
        get() = hasInspectedLairExit &&
                hasMetBigfootFamily &&
                hasInspectedLostLemonMine &&
                hasTakenBigfootEvidencePhoto

    val locationLeads: List<LocationLead> = LocationLead.all

    // MARK: - Journal Photos
    var photoIDs by mutableStateOf(setOf<String>())
        private set

    val curatorAnswer = "SASQUATCH"

    val discoveredCuratorLetters: String
        get() = Photo.all
            .filter { photoIDs.contains(it.id) }
            .joinToString(separator = "") { it.secretLetter }

    val totalPhotoCount: Int
        get() = Photo.all.size

    val photoCount: Int
        get() = photoIDs.size

    // MARK: - Inventory Operations
    fun collectInventoryItem(item: InventoryItem) {
        if (collectedInventoryItemIDs.contains(item.id) && !usedInventoryItemIDs.contains(item.id)) return

        if (usedInventoryItemIDs.contains(item.id)) {
            usedInventoryItemIDs = usedInventoryItemIDs - item.id
        }
        collectedInventoryItemIDs = collectedInventoryItemIDs + item.id
        updateCollectedFlag(item)
    }

    fun useInventoryItem(item: InventoryItem) {
        if (!collectedInventoryItemIDs.contains(item.id)) return
        usedInventoryItemIDs = usedInventoryItemIDs + item.id
    }

    fun hasInventoryItem(item: InventoryItem): Boolean {
        return collectedInventoryItemIDs.contains(item.id) && !usedInventoryItemIDs.contains(item.id)
    }

    fun hasCollectedInventoryItem(item: InventoryItem): Boolean {
        return collectedInventoryItemIDs.contains(item.id)
    }

    fun hasUsedInventoryItem(item: InventoryItem): Boolean {
        return usedInventoryItemIDs.contains(item.id)
    }

    fun collectShovel() {
        collectInventoryItem(InventoryItem.SMALL_SHOVEL)
    }

    private fun updateCollectedFlag(item: InventoryItem) {
        when (item) {
            InventoryItem.SMALL_SHOVEL -> hasCollectedShovel = true
            InventoryItem.GAFF_HOOK -> hasCollectedGaffHook = true
            InventoryItem.WOODEN_MATCHES -> hasCollectedWoodenMatches = true
            InventoryItem.CAFE_LEAD -> hasFoundCafeLead = true
            InventoryItem.VINTAGE_BRASS_TOKEN -> hasCollectedVintageBrassToken = true
            InventoryItem.OBSERVATORY_STORY_LEAD -> hasCollectedObservatoryStoryLead = true
            InventoryItem.OBSERVATORY_LOCKER_KEY -> hasCollectedObservatoryLockerKey = true
            InventoryItem.RUSTY_CROWBAR -> hasCollectedRustyCrowbar = true
            InventoryItem.WOODCUTTERS_AXE -> hasCollectedWoodcuttersAxe = true
            InventoryItem.OBSERVATORY_JOURNAL_LEAD -> hasCollectedObservatoryJournalLead = true
            InventoryItem.LOST_LEMON_GOLD_NUGGET -> {
                hasReturnedFromBigfootLair = true
                hasFoundGoldNuggetInPocket = true
            }
        }
    }

    // MARK: - Progression Requirements
    val hasCompletedSecondPhaseItems: Boolean
        get() = hasCollectedInventoryItem(InventoryItem.GAFF_HOOK) &&
                hasCollectedInventoryItem(InventoryItem.WOODEN_MATCHES) &&
                hasCollectedInventoryItem(InventoryItem.VINTAGE_BRASS_TOKEN)

    val hasUnlockedObservatory: Boolean
        get() = hasMeltedWeatherStationDoorIce

    val hasUnlockedLakeMinnewanka: Boolean
        get() = hasCollectedRustyCrowbar

    val hasUnlockedTunnelMountain: Boolean
        get() = hasCollectedWoodcuttersAxe

    // MARK: - Corkboard Helpers
    fun isLocationLeadAvailable(lead: LocationLead): Boolean {
        return when (lead.location) {
            LocationId.BOW_FALLS, LocationId.CAVE_AND_BASIN, LocationId.BANFF_SPRINGS_HOTEL -> true
            LocationId.HOT_SPRINGS, LocationId.DOWNTOWN_BANFF, LocationId.SULPHUR_MOUNTAIN -> hasCompletedSecondPhaseItems
            LocationId.OBSERVATORY -> hasUnlockedObservatory
            LocationId.LAKE_MINNEWANKA -> hasUnlockedLakeMinnewanka
            LocationId.TUNNEL_MOUNTAIN -> hasUnlockedTunnelMountain
            LocationId.MUSEUM_EXTERIOR, LocationId.MUSEUM_INTERIOR, LocationId.BIGFOOT_LAIR -> true
        }
    }

    fun isLocationLeadCompleted(lead: LocationLead): Boolean {
        return when (lead.location) {
            LocationId.BOW_FALLS -> hasCollectedGaffHook && hasCollectedWoodenMatches
            LocationId.CAVE_AND_BASIN -> hasCollectedVintageBrassToken
            LocationId.BANFF_SPRINGS_HOTEL -> hasPhotographedGhostBride
            LocationId.HOT_SPRINGS -> hasFoundCafeLead && hasTradedVintageBrassToken
            LocationId.DOWNTOWN_BANFF -> hasTradedVintageBrassToken
            LocationId.SULPHUR_MOUNTAIN -> hasMeltedWeatherStationDoorIce
            LocationId.OBSERVATORY -> hasCollectedRustyCrowbar && hasCollectedObservatoryJournalLead
            LocationId.LAKE_MINNEWANKA -> hasCollectedWoodcuttersAxe
            LocationId.TUNNEL_MOUNTAIN -> hasTriggeredIcicleFall
            else -> false
        }
    }

    fun selectLocationLead(lead: LocationLead) {
        if (!isLocationLeadAvailable(lead)) return
        currentLocation = lead.location
    }

    fun finishBigfootLairSequence() {
        hasEscapedBigfootLair = true
        hasReturnedFromBigfootLair = true
        hasFoundGoldNuggetInPocket = false
        shouldShowMuseumWakeUpAfterLair = true
        currentLocation = LocationId.MUSEUM_EXTERIOR
    }

    // MARK: - Journal Photos
    fun capturePhoto(photo: Photo) {
        if (photoIDs.contains(photo.id)) return
        photoIDs = photoIDs + photo.id
        if (photo.id == Photo.banffSpringsHotel.id) {
            hasPhotographedGhostBride = true
        }
    }

    fun hasPhoto(photo: Photo): Boolean = photoIDs.contains(photo.id)
    fun hasPhoto(photoId: String): Boolean = photoIDs.contains(photoId)

    val journalPhotos: List<Photo>
        get() = Photo.all.filter { photoIDs.contains(it.id) }

    val photoRewardCode: String
        get() = when (photoCount) {
            in 7..9 -> "BANFF20"
            in 5..6 -> "BANFF10"
            else -> "LOCKED"
        }

    val photoRewardMessage: String
        get() = when (photoCount) {
            in 7..9 -> "You photographed $photoCount journal photos and unlocked a 20% discount code."
            in 5..6 -> "You photographed $photoCount journal photos and unlocked a 10% discount code."
            else -> "You photographed $photoCount journal photos. Photograph at least 5 to unlock a discount code."
        }

    // MARK: - Final Puzzle
    fun isCuratorAnswerCorrect(answer: String): Boolean {
        return answer.trim().uppercase() == curatorAnswer
    }

    // MARK: - Reset
    fun resetForNewGame() {
        hasStartedGame = false
        currentLocation = LocationId.MUSEUM_EXTERIOR
        hasOpenedMailbox = false
        hasReadMailboxNote = false
        hasCollectedShovel = false
        isMuseumDoorUnlocked = false
        hasSeenReturnFromLairMessage = false
        hasCollectedGaffHook = false
        hasOpenedFallsCanister = false
        hasCollectedWoodenMatches = false
        hasOpenedBasinChest = false
        hasCollectedVintageBrassToken = false
        hasPhotographedGhostBride = false
        hasFoundCafeLead = false
        hasEnteredSnowyOwlCafe = false
        hasTradedVintageBrassToken = false
        hasCollectedObservatoryStoryLead = false
        hasCollectedObservatoryLockerKey = false
        hasMeltedWeatherStationDoorIce = false
        hasReadObservatoryLogbook = false
        hasCollectedObservatoryJournalLead = false
        hasOpenedObservatoryLocker = false
        hasCollectedRustyCrowbar = false
        hasOpenedMinnewankaCrate = false
        hasCollectedWoodcuttersAxe = false
        hasBrokenCaveEntranceBoards = false
        hasTriggeredIcicleFall = false
        hasWokenInBigfootLair = false
        hasInspectedLairExit = false
        hasMetBigfootFamily = false
        hasInspectedLostLemonMine = false
        hasTakenBigfootEvidencePhoto = false
        hasEscapedBigfootLair = false
        hasReturnedFromBigfootLair = false
        hasFoundGoldNuggetInPocket = false
        hasSpokenToCuratorAfterLair = false
        shouldShowMuseumWakeUpAfterLair = false
        collectedInventoryItemIDs = emptySet()
        usedInventoryItemIDs = emptySet()
        photoIDs = emptySet()
    }
}