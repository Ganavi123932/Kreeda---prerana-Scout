package com.kreeda.prerana.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Defines benchmark thresholds for badge evaluation.
 * Pre-seeded with district, state, and national level targets
 * for each event, gender, and age group combination.
 */
@Entity(
    tableName = "benchmarks",
    foreignKeys = [
        ForeignKey(
            entity = EventType::class,
            parentColumns = ["id"],
            childColumns = ["eventTypeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["eventTypeId"]),
        Index(value = ["eventTypeId", "level", "gender", "ageGroup"], unique = true)
    ]
)
data class Benchmark(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val eventTypeId: Long,
    val level: String,          // "District", "State", "National"
    val gender: String,         // "Male", "Female"
    val ageGroup: String,       // "U-14", "U-17", "U-19"
    val threshold: Double       // The performance value to meet/exceed
)
