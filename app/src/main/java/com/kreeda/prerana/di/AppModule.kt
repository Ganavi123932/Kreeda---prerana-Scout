package com.kreeda.prerana.di

import com.kreeda.prerana.data.repository.AthleteRepository
import com.kreeda.prerana.data.repository.BadgeRepository
import com.kreeda.prerana.data.repository.BenchmarkRepository
import com.kreeda.prerana.data.repository.PerformanceRepository
import com.kreeda.prerana.domain.usecase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module providing application-level dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideManageAthleteUseCase(
        athleteRepository: AthleteRepository
    ): ManageAthleteUseCase = ManageAthleteUseCase(athleteRepository)

    @Provides
    @Singleton
    fun provideLogPerformanceUseCase(
        performanceRepository: PerformanceRepository,
        benchmarkRepository: BenchmarkRepository
    ): LogPerformanceUseCase = LogPerformanceUseCase(performanceRepository, benchmarkRepository)

    @Provides
    @Singleton
    fun provideEvaluateBadgeUseCase(
        badgeRepository: BadgeRepository,
        benchmarkRepository: BenchmarkRepository,
        performanceRepository: PerformanceRepository
    ): EvaluateBadgeUseCase = EvaluateBadgeUseCase(badgeRepository, benchmarkRepository, performanceRepository)

    @Provides
    @Singleton
    fun provideGetLeaderboardUseCase(
        performanceRepository: PerformanceRepository,
        athleteRepository: AthleteRepository,
        benchmarkRepository: BenchmarkRepository,
        badgeRepository: BadgeRepository
    ): GetLeaderboardUseCase = GetLeaderboardUseCase(performanceRepository, athleteRepository, benchmarkRepository, badgeRepository)

    @Provides
    @Singleton
    fun provideGenerateTalentCurveUseCase(
        performanceRepository: PerformanceRepository,
        benchmarkRepository: BenchmarkRepository
    ): GenerateTalentCurveUseCase = GenerateTalentCurveUseCase(performanceRepository, benchmarkRepository)
}
