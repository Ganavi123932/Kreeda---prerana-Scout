package com.kreeda.prerana.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Records a single performance trial for an athlete in a specific event.
 * Date-stamped for historical tracking per FR-14.
 */
@Entity(
    tableName = "performances",
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
        Index(value = ["athleteId", "eventTypeId"])
    ]
)
data class Performance(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val athleteId: Long,
    val eventTypeId: Long,
    val value: Double,          // The measured value (time in seconds, distance in meters, etc.)
    val timestamp: Long = System.currentTimeMillis(),
    val notes: String? = null   // Optional context (e.g., "windy conditions", "after warm-up")
)
