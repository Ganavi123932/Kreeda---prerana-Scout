package com.kreeda.prerana.data.db.dao

import androidx.room.*
import com.kreeda.prerana.data.db.entity.Badge
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Badge entity.
 * Manages milestone badges awarded to athletes.
 */
@Dao
interface BadgeDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(badge: Badge): Long

    @Delete
    suspend fun delete(badge: Badge)

    @Query("SELECT * FROM badges WHERE athleteId = :athleteId ORDER BY awardedAt DESC")
    fun getBadgesForAthlete(athleteId: Long): Flow<List<Badge>>

    @Query("""
        SELECT * FROM badges 
        WHERE athleteId = :athleteId AND eventTypeId = :eventTypeId
        ORDER BY awardedAt DESC
    """)
    fun getBadgesForAthleteEvent(
        athleteId: Long,
        eventTypeId: Long
    ): Flow<List<Badge>>

    @Query("""
        SELECT EXISTS(
            SELECT 1 FROM badges 
            WHERE athleteId = :athleteId 
            AND eventTypeId = :eventTypeId 
            AND level = :level
        )
    """)
    suspend fun hasBadge(athleteId: Long, eventTypeId: Long, level: String): Boolean

    @Query("SELECT COUNT(*) FROM badges WHERE athleteId = :athleteId")
    fun getBadgeCountForAthlete(athleteId: Long): Flow<Int>

    @Query("""
        SELECT COUNT(*) FROM badges 
        WHERE athleteId = :athleteId AND level = :level
    """)
    suspend fun getBadgeCountForLevel(athleteId: Long, level: String): Int

    @Query("SELECT * FROM badges ORDER BY awardedAt DESC LIMIT :limit")
    fun getRecentBadges(limit: Int = 10): Flow<List<Badge>>
}
