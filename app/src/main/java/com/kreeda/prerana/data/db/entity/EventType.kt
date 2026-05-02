package com.kreeda.prerana.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Defines a type of athletic event (e.g., 100m Sprint, Long Jump, Shot Put).
 * The [higherIsBetter] flag determines sorting direction for leaderboards —
 * false for timed events (lower is better), true for distance/height events.
 */
@Entity(tableName = "event_types")
data class EventType(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,          // e.g., "100m Sprint", "Long Jump"
    val category: String,      // "Track", "Field", "Sport-Specific"
    val unit: String,          // "seconds", "meters", "points"
    val higherIsBetter: Boolean // false for sprints (lower time = better)
)
