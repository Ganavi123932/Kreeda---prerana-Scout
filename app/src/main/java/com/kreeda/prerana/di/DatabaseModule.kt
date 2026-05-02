package com.kreeda.prerana.di

import android.content.Context
import androidx.room.Room
import com.kreeda.prerana.data.db.KreedaDatabase
import com.kreeda.prerana.data.db.dao.AthleteDao
import com.kreeda.prerana.data.db.dao.BadgeDao
import com.kreeda.prerana.data.db.dao.BenchmarkDao
import com.kreeda.prerana.data.db.dao.PerformanceDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module providing database and DAO dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): KreedaDatabase {
        return Room.databaseBuilder(
            context,
            KreedaDatabase::class.java,
            KreedaDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideAthleteDao(database: KreedaDatabase): AthleteDao =
        database.athleteDao()

    @Provides
    fun providePerformanceDao(database: KreedaDatabase): PerformanceDao =
        database.performanceDao()

    @Provides
    fun provideBadgeDao(database: KreedaDatabase): BadgeDao =
        database.badgeDao()

    @Provides
    fun provideBenchmarkDao(database: KreedaDatabase): BenchmarkDao =
        database.benchmarkDao()
}
