package com.kreeda.prerana.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Represents a milestone badge awarded to an athlete.
 * Levels: "District", "State", "National"
 * Badges are automatically evaluated after each performance entry (FR-05).
 */
@Entity(
    tableName = "badges",
    foreignKeys = [
        ForeignKey(
            entity = Athlete::class,
            parentColumns = ["id"],
            childColumns = ["athleteId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = EventType::class,
            parentColumns = ["id"],
            childColumns = ["eventTypeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["athleteId"]),
        Index(value = ["eventTypeId"]),
        Index(value = ["athleteId", "eventTypeId", "level"], unique = true)
    ]
)
data class Badge(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val athleteId: Long,
    val eventTypeId: Long,
    val level: String,          // "District", "State", "National"
    val awardedAt: Long = System.currentTimeMillis()
)
