package com.kreeda.prerana.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.kreeda.prerana.data.db.dao.AthleteDao
import com.kreeda.prerana.data.db.dao.BadgeDao
import com.kreeda.prerana.data.db.dao.BenchmarkDao
import com.kreeda.prerana.data.db.dao.PerformanceDao
import com.kreeda.prerana.data.db.entity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Room Database for Kreeda-Prerana Scout.
 * Version 1 — Initial schema with 5 entities.
 * Pre-seeds event types and benchmark thresholds on first creation.
 */
@Database(
    entities = [
        Athlete::class,
        EventType::class,
        Performance::class,
        Badge::class,
        Benchmark::class
    ],
    version = 1,
    exportSchema = true
)
abstract class KreedaDatabase : RoomDatabase() {

    abstract fun athleteDao(): AthleteDao
    abstract fun performanceDao(): PerformanceDao
    abstract fun badgeDao(): BadgeDao
    abstract fun benchmarkDao(): BenchmarkDao

    companion object {
        const val DATABASE_NAME = "kreeda_prerana.db"

        /**
         * Callback to pre-seed the database with event types and benchmarks
         * on first creation.
         */
        fun seedCallback(scope: CoroutineScope): Callback {
            return object : Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    scope.launch(Dispatchers.IO) {
                        // Event types will be seeded via the DatabaseModule
                    }
                }
            }
        }
    }
}
