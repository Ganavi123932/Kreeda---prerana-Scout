package com.kreeda.prerana.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a student athlete in the Kreeda-Prerana system.
 * Contains personal details, school information, and sport designation.
 */
@Entity(tableName = "athletes")
data class Athlete(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val age: Int,
    val gender: String,        // "Male", "Female", "Other"
    val school: String,
    val studentClass: String,  // e.g., "8th", "9th", "10th"
    val primarySport: String,  // e.g., "Athletics", "Kabaddi", "Kho-Kho"
    val photoUri: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
