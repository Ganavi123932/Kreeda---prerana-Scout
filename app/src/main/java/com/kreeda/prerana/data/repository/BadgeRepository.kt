package com.kreeda.prerana.data.repository

import com.kreeda.prerana.data.db.dao.BadgeDao
import com.kreeda.prerana.data.db.entity.Badge
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for Badge data operations.
 * Manages badge awards and queries.
 */
@Singleton
class BadgeRepository @Inject constructor(
    private val badgeDao: BadgeDao
) {
    suspend fun awardBadge(badge: Badge): Long = badgeDao.insert(badge)

    suspend fun removeBadge(badge: Badge) = badgeDao.delete(badge)

    fun getBadgesForAthlete(athleteId: Long): Flow<List<Badge>> =
        badgeDao.getBadgesForAthlete(athleteId)

    fun getBadgesForAthleteEvent(
        athleteId: Long,
        eventTypeId: Long
    ): Flow<List<Badge>> =
        badgeDao.getBadgesForAthleteEvent(athleteId, eventTypeId)

    suspend fun hasBadge(
        athleteId: Long,
        eventTypeId: Long,
        level: String
    ): Boolean = badgeDao.hasBadge(athleteId, eventTypeId, level)

    fun getBadgeCountForAthlete(athleteId: Long): Flow<Int> =
        badgeDao.getBadgeCountForAthlete(athleteId)

    suspend fun getBadgeCountForLevel(athleteId: Long, level: String): Int =
        badgeDao.getBadgeCountForLevel(athleteId, level)

    fun getRecentBadges(limit: Int = 10): Flow<List<Badge>> =
        badgeDao.getRecentBadges(limit)
}
