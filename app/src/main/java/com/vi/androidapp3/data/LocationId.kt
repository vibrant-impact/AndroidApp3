package com.vi.androidapp3.data

/**
 * Unique identifiers for all explorable locations and narrative scenes across Banff.
 * Used for navigation routing, state tracking, and background ambient audio mapping.
 */
enum class LocationId(val displayName: String) {
    MUSEUM_EXTERIOR("Museum Exterior"),
    MUSEUM_INTERIOR("Banff Park Museum"),
    CAVE_AND_BASIN("Cave and Basin"),
    BOW_FALLS("Bow Falls"),
    BANFF_SPRINGS_HOTEL("Banff Springs Hotel"),
    HOT_SPRINGS("Upper Hot Springs"),
    DOWNTOWN_BANFF("Downtown Banff"),
    LAKE_MINNEWANKA("Lake Minnewanka"),
    SULPHUR_MOUNTAIN("Sulphur Mountain Gondola"),
    OBSERVATORY("Weather Station Observatory"),
    TUNNEL_MOUNTAIN("Tunnel Mountain"),
    BIGFOOT_LAIR("The Hidden Lair")
}