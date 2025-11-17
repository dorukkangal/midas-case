package com.midas.core.network.di

import com.midas.core.network.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import javax.inject.Singleton

/**
 * Network module factory for providing network dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    @OptIn(ExperimentalSerializationApi::class)
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = false
        explicitNulls = false
    }

    @Provides
    @Singleton
    fun provideHttpClient(
        json: Json
    ): HttpClient = HttpClient(Android) {
        install(ContentNegotiation) {
            json(json)
        }

        install(Logging) {
            logger = Logger.ANDROID
            level = if (BuildConfig.DEBUG) LogLevel.ALL else LogLevel.NONE
        }

        defaultRequest {
            url(BuildConfig.BASE_URL)
            headers.append("x-cg-demo-api-key", BuildConfig.API_KEY)
            headers.append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            headers.append(HttpHeaders.Accept, ContentType.Application.Json.toString())
        }

        engine {
            connectTimeout = 30_000
            socketTimeout = 30_000
        }
    }
}
