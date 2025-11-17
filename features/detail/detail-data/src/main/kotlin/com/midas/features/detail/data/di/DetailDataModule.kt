package com.midas.features.detail.data.di

import com.midas.features.detail.data.remote.api.CoinDetailApiService
import com.midas.features.detail.data.repository.CoinDetailRepositoryImpl
import com.midas.features.detail.domain.repository.CoinDetailRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DetailDataModule {

    @Binds
    @Singleton
    abstract fun bindCoinDetailRepository(
        coinDetailRepositoryImpl: CoinDetailRepositoryImpl
    ): CoinDetailRepository

    companion object {
        @Provides
        @Singleton
        fun provideCoinDetailApiService(
            httpClient: HttpClient
        ): CoinDetailApiService = CoinDetailApiService(httpClient)
    }
}
