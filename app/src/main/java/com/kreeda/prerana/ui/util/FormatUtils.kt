package com.kreeda.prerana.ui.util

import java.util.Locale

/**
 * Utility functions for formatting performance values and display text.
 */
object FormatUtils {

    /**
     * Format a performance value with appropriate precision based on unit.
     */
    fun formatPerformanceValue(value: Double, unit: String): String {
        return when (unit.lowercase(Locale.ROOT)) {
            "seconds" -> String.format(Locale.getDefault(), "%.2f s", value)
            "meters" -> String.format(Locale.getDefault(), "%.2f m", value)
            "points" -> String.format(Locale.getDefault(), "%.0f pts", value)
            else -> String.format(Locale.getDefault(), "%.2f %s", value, unit)
        }
    }

    /**
     * Format chronometer time from milliseconds to MM:SS.CC
     */
    fun formatChronoTime(millis: Long): String {
        val minutes = (millis / 60000).toInt()
        val seconds = ((millis % 60000) / 1000).toInt()
        val centis = ((millis % 1000) / 10).toInt()
        return String.format(Locale.getDefault(), "%02d:%02d.%02d", minutes, seconds, centis)
    }

    /**
     * Format chronometer time to seconds with 2 decimal places.
     */
    fun chronoMillisToSeconds(millis: Long): Double {
        return millis / 1000.0
    }

    /**
     * Get the age group based on the athlete's age.
     */
    fun getAgeGroup(age: Int): String {
        return when {
            age < 14 -> "U-14"
            age < 17 -> "U-17"
            age < 19 -> "U-19"
            else -> "U-19"
        }
    }

    /**
     * Get badge emoji based on level.
     */
    fun getBadgeEmoji(level: String): String {
        return when (level) {
            "District" -> "🥉"
            "State" -> "🥈"
            "National" -> "🥇"
            else -> "🏅"
        }
    }

    /**
     * Get badge label text.
     */
    fun getBadgeLabel(level: String): String {
        return when (level) {
            "District" -> "District Ready"
            "State" -> "State Ready"
            "National" -> "National Ready"
            else -> level
        }
    }

    /**
     * Get sport icon text.
     */
    fun getSportIcon(sport: String): String {
        return when (sport.lowercase(Locale.ROOT)) {
            "athletics" -> "🏃"
            "kabaddi" -> "🤼"
            "kho-kho", "kho kho" -> "🏃‍♂️"
            "cricket" -> "🏏"
            "football" -> "⚽"
            "hockey" -> "🏑"
            "badminton" -> "🏸"
            "volleyball" -> "🏐"
            "basketball" -> "🏀"
            else -> "🏅"
        }
    }
}
