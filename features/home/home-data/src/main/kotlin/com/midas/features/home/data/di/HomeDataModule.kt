package com.midas.features.home.data.di

import com.midas.features.home.data.remote.api.CoinApiService
import com.midas.features.home.data.repository.CoinRepositoryImpl
import com.midas.features.home.domain.repository.CoinRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class HomeDataModule {

    @Binds
    @Singleton
    abstract fun bindCoinRepository(
        coinRepositoryImpl: CoinRepositoryImpl
    ): CoinRepository

    companion object {
        @Provides
        @Singleton
        fun provideCoinApiService(
            httpClient: HttpClient
        ): CoinApiService = CoinApiService(httpClient)
    }
}
